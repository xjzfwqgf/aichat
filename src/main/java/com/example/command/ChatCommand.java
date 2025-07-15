package com.example.command;

import com.example.manager.PlayerChatManager;
import com.example.service.ChatService;
import com.example.config.AIConfig;
import com.example.util.ConfigManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardCriterion;

import java.util.UUID;

public class ChatCommand {
    private static AIConfig aiConfig;
    
    public static void register() {
        // 从配置文件加载 API Key
        aiConfig = ConfigManager.loadConfig();
        PlayerChatManager.setGlobalConfig(aiConfig);
        
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("aichat")
                .then(CommandManager.literal("setkey")
                    .requires(source -> source.hasPermissionLevel(2)) // 需要管理员权限
                    .then(CommandManager.argument("key", StringArgumentType.greedyString())
                        .executes(ChatCommand::setApiKey)))
                .then(CommandManager.literal("clear")
                    .executes(ChatCommand::clearHistory))
                .then(CommandManager.literal("personality")
                    .then(CommandManager.argument("personality", StringArgumentType.greedyString())
                        .executes(ChatCommand::setPersonality)))
                .then(CommandManager.literal("reset")
                    .executes(ChatCommand::resetPersonality))
                .then(CommandManager.literal("config")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(CommandManager.literal("endpoint")
                        .then(CommandManager.argument("url", StringArgumentType.string())
                            .executes(ChatCommand::setApiEndpoint)))
                    .then(CommandManager.literal("model")
                        .then(CommandManager.argument("model", StringArgumentType.string())
                            .executes(ChatCommand::setModel)))
                    .then(CommandManager.literal("defaultpersonality")
                        .then(CommandManager.argument("personality", StringArgumentType.greedyString())
                            .executes(ChatCommand::setDefaultPersonality)))
                    .then(CommandManager.literal("presets")
                        .then(CommandManager.literal("add")
                            .then(CommandManager.argument("name", StringArgumentType.word())
                                .then(CommandManager.argument("personality", StringArgumentType.greedyString())
                                    .executes(ChatCommand::addPersonalityPreset))))
                        .then(CommandManager.literal("remove")
                            .then(CommandManager.argument("name", StringArgumentType.word())
                                .executes(ChatCommand::removePersonalityPreset)))
                        .then(CommandManager.literal("list")
                            .executes(ChatCommand::listPersonalityPresets))
                        .then(CommandManager.literal("use")
                            .then(CommandManager.argument("name", StringArgumentType.word())
                                .executes(ChatCommand::usePersonalityPreset))))
                    .then(CommandManager.literal("show")
                        .executes(ChatCommand::showConfig))
                    .then(CommandManager.literal("save")
                        .executes(ChatCommand::saveConfig))
                    .then(CommandManager.literal("reload")
                        .executes(ChatCommand::reloadConfig)))
                .then(CommandManager.argument("message", StringArgumentType.greedyString())
                    .executes(ChatCommand::sendMessage)));
        });
    }
    
    private static int setApiKey(CommandContext<ServerCommandSource> context) {
        String apiKey = StringArgumentType.getString(context, "key");
        aiConfig.setApiKey(apiKey);
        PlayerChatManager.setGlobalConfig(aiConfig);
        
        // 保存配置到文件
        ConfigManager.saveConfig(aiConfig);
        
        context.getSource().sendMessage(Text.literal("API Key 已设置！"));
        return 1;
    }
    
    private static int clearHistory(CommandContext<ServerCommandSource> context) {
        if (!PlayerChatManager.hasGlobalConfig()) {
            context.getSource().sendMessage(Text.literal("请先设置API Key: /aichat setkey <your-key>"));
            return 0;
        }
        
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            PlayerChatManager.clearPlayerHistory(player.getUuid());
            context.getSource().sendMessage(Text.literal("您的对话历史已清空！"));
        }
        return 1;
    }
    
    private static int setPersonality(CommandContext<ServerCommandSource> context) {
        String personality = StringArgumentType.getString(context, "personality");
        ServerPlayerEntity player = context.getSource().getPlayer();
        
        if (player != null) {
            PlayerChatManager.setPlayerPersonality(player.getUuid(), personality);
            context.getSource().sendMessage(Text.literal("AI人设已更新！"));
        }
        return 1;
    }
    
    private static int resetPersonality(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        
        if (player != null) {
            PlayerChatManager.setPlayerPersonality(player.getUuid(), 
                "You are a helpful assistant in Minecraft. Keep responses concise and friendly.");
            context.getSource().sendMessage(Text.literal("AI人设已重置为默认设置！"));
        }
        return 1;
    }
    
    private static int sendMessage(CommandContext<ServerCommandSource> context) {
        if (!PlayerChatManager.hasGlobalConfig()) {
            context.getSource().sendMessage(Text.literal("请先设置API Key: /aichat setkey <your-key>"));
            return 0;
        }
        
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendMessage(Text.literal("此命令只能由玩家使用！"));
            return 0;
        }
        
        String message = StringArgumentType.getString(context, "message");
        Text thinkingMessage = Text.literal("AI正在思考...").formatted(Formatting.GRAY);
        player.sendMessage(thinkingMessage, false);
        
        ChatService chatService = PlayerChatManager.getChatService(player.getUuid());

        // 创建流式响应处理系统
        final StringBuilder responseBuilder = new StringBuilder();
        final UUID currentRequestId = UUID.randomUUID();

        chatService.setMessageCallback(content -> {
            responseBuilder.append(content);
            String currentResponse = responseBuilder.toString().trim();

            // 自动转换 markdown 为纯文本
            String plainText = currentResponse
                .replaceAll("`{3,}.*?`{3,}", "[代码块]") // 代码块标记
                .replaceAll("`([^`]*)`", "$1") // 行内代码
                .replaceAll("[*_~`]+", "") // 去除粗体/斜体/删除线/代码符号
                .replaceAll("^#+\\s*", "") // 标题
                .replaceAll("^- ", "• ") // 列表
                .replaceAll("\\r", "") // 去除回车
                ;

            player.getServer().execute(() -> {
                // Scoreboard API
                Scoreboard scoreboard = player.getScoreboard();
                ScoreboardObjective objective = scoreboard.getObjective("ai_reply");
                if (objective == null) {
                    objective = scoreboard.addObjective("ai_reply", ScoreboardCriterion.DUMMY, Text.literal("AI回复"), ScoreboardCriterion.RenderType.INTEGER);
                    scoreboard.setObjectiveSlot(1, objective); // 1 = SIDEBAR
                }
                // 分行显示内容，自动截断每行长度
                int maxLineLength = 30;
                java.util.List<String> displayLines = new java.util.ArrayList<>();
                for (String line : plainText.split("\\n")) {
                    while (line.length() > maxLineLength) {
                        displayLines.add(line.substring(0, maxLineLength));
                        line = line.substring(maxLineLength);
                    }
                    displayLines.add(line);
                }
                // 只显示最后 maxDisplayLines 行
                int maxDisplayLines = 15;
                int startIdx = Math.max(0, displayLines.size() - maxDisplayLines);
                for (int i = startIdx; i < displayLines.size(); i++) {
                    String line = displayLines.get(i);
                    scoreboard.getPlayerScore(line, objective).setScore(displayLines.size() - i); // 倒序显示
                }
            });
        });

        chatService.sendMessage(message).thenAccept(response -> {
            player.getServer().execute(() -> {
                // 自动转换 markdown 为纯文本
                String plainText = response
                    .replaceAll("`{3,}.*?`{3,}", "[代码块]")
                    .replaceAll("`([^`]*)`", "$1")
                    .replaceAll("[*_~`]+", "")
                    .replaceAll("^#+\\s*", "")
                    .replaceAll("^- ", "• ")
                    .replaceAll("\\r", "");

                // 显示最终完整回复，去掉打字光标，使用普通聊天消息
                Text finalMessage = Text.literal("")
                    .append(Text.literal("AI: ").formatted(Formatting.GREEN))
                    .append(Text.literal(plainText).formatted(Formatting.WHITE));
                player.sendMessage(finalMessage, false);
                // 清空侧边栏
                Scoreboard scoreboard = player.getScoreboard();
                ScoreboardObjective objective = scoreboard.getObjective("ai_reply");
                if (objective != null) {
                    scoreboard.removeObjective(objective);
                }
            });
        }).exceptionally(throwable -> {
            player.getServer().execute(() -> {
                Text errorMessage = Text.literal("发送失败: " + throwable.getMessage())
                    .formatted(Formatting.RED);
                player.sendMessage(errorMessage, false);
            });
            return null;
        });

        return 1;
    }
    
    private static int setApiEndpoint(CommandContext<ServerCommandSource> context) {
        String endpoint = StringArgumentType.getString(context, "url");
        aiConfig.setApiEndpoint(endpoint);
        ConfigManager.saveConfig(aiConfig);
        context.getSource().sendMessage(Text.literal("API 地址已设置为: " + endpoint));
        return 1;
    }
    
    private static int setModel(CommandContext<ServerCommandSource> context) {
        String model = StringArgumentType.getString(context, "model");
        aiConfig.setModel(model);
        ConfigManager.saveConfig(aiConfig);
        context.getSource().sendMessage(Text.literal("AI 模型已设置为: " + model));
        return 1;
    }
    
    private static int setDefaultPersonality(CommandContext<ServerCommandSource> context) {
        String personality = StringArgumentType.getString(context, "personality");
        aiConfig.setDefaultPersonality(personality);
        ConfigManager.saveConfig(aiConfig);
        context.getSource().sendMessage(Text.literal("默认AI人设已更新！"));
        return 1;
    }
    
    private static int addPersonalityPreset(CommandContext<ServerCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        String personality = StringArgumentType.getString(context, "personality");
        aiConfig.addDefaultPersonality(name, personality);
        ConfigManager.saveConfig(aiConfig);
        context.getSource().sendMessage(Text.literal("已添加预设人设: " + name));
        return 1;
    }
    
    private static int removePersonalityPreset(CommandContext<ServerCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        if (aiConfig.getDefaultPersonalities().containsKey(name)) {
            aiConfig.removeDefaultPersonality(name);
            ConfigManager.saveConfig(aiConfig);
            context.getSource().sendMessage(Text.literal("已删除预设人设: " + name));
        } else {
            context.getSource().sendMessage(Text.literal("找不到预设人设: " + name));
        }
        return 1;
    }
    
    private static int listPersonalityPresets(CommandContext<ServerCommandSource> context) {
        context.getSource().sendMessage(Text.literal("可用的预设人设:"));
        aiConfig.getDefaultPersonalities().forEach((name, personality) -> {
            context.getSource().sendMessage(Text.literal("- " + name + ": " + personality));
        });
        return 1;
    }
    
    private static int usePersonalityPreset(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        String name = StringArgumentType.getString(context, "name");
        String personality = aiConfig.getPersonality(name);
        if (personality != null) {
            PlayerChatManager.setPlayerPersonality(player.getUuid(), personality);
            context.getSource().sendMessage(Text.literal("已应用预设人设: " + name));
        } else {
            context.getSource().sendMessage(Text.literal("找不到预设人设: " + name));
        }
        return 1;
    }
    
    private static int showConfig(CommandContext<ServerCommandSource> context) {
        context.getSource().sendMessage(Text.literal("当前配置:"));
        context.getSource().sendMessage(Text.literal("API 地址: " + aiConfig.getApiEndpoint()));
        context.getSource().sendMessage(Text.literal("AI 模型: " + aiConfig.getModel()));
        context.getSource().sendMessage(Text.literal("API Key: " + (aiConfig.getApiKey() != null ? "已设置" : "未设置")));
        context.getSource().sendMessage(Text.literal("默认AI人设: " + aiConfig.getDefaultPersonality()));
        context.getSource().sendMessage(Text.literal("预设人设数量: " + aiConfig.getDefaultPersonalities().size()));
        return 1;
    }
    
    private static int saveConfig(CommandContext<ServerCommandSource> context) {
        ConfigManager.saveConfig(aiConfig);
        context.getSource().sendMessage(Text.literal("配置已保存"));
        return 1;
    }
    
    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        aiConfig = ConfigManager.loadConfig();
        PlayerChatManager.setGlobalConfig(aiConfig);
        context.getSource().sendMessage(Text.literal("配置已重新加载"));
        return 1;
    }
}
