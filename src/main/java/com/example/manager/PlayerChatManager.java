package com.example.manager;

import com.example.service.ChatService;
import com.example.config.AIConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerChatManager {
    private static final Map<UUID, ChatService> playerChatServices = new HashMap<>();
    private static final Map<UUID, String> playerPersonalities = new HashMap<>();
    private static AIConfig globalConfig;
    
    public static void setGlobalConfig(AIConfig config) {
        globalConfig = config;
    }
    
    public static ChatService getChatService(UUID playerId) {
        return playerChatServices.computeIfAbsent(playerId, id -> {
            String personality = playerPersonalities.getOrDefault(id, 
                globalConfig != null ? globalConfig.getDefaultPersonality() : 
                "You are a helpful assistant in Minecraft. Keep responses concise and friendly.");
            return new ChatService(globalConfig, personality);
        });
    }
    
    public static void setPlayerPersonality(UUID playerId, String personality) {
        playerPersonalities.put(playerId, personality);
        // 如果玩家已有聊天服务，更新人设
        if (playerChatServices.containsKey(playerId)) {
            playerChatServices.put(playerId, new ChatService(globalConfig, personality));
        }
    }
    
    public static String getPlayerPersonality(UUID playerId) {
        return playerPersonalities.getOrDefault(playerId, 
            globalConfig != null ? globalConfig.getDefaultPersonality() : 
            "You are a helpful assistant in Minecraft. Keep responses concise and friendly.");
    }
    
    public static void clearPlayerHistory(UUID playerId) {
        ChatService service = playerChatServices.get(playerId);
        if (service != null) {
            service.clearHistory();
        }
    }
    
    public static void removePlayer(UUID playerId) {
        playerChatServices.remove(playerId);
        playerPersonalities.remove(playerId);
    }
    
    public static boolean hasGlobalConfig() {
        return globalConfig != null && globalConfig.getApiKey() != null && !globalConfig.getApiKey().isEmpty();
    }
    
    public static void resetPlayerPersonality(UUID playerId) {
        String defaultPersonality = globalConfig != null ? globalConfig.getDefaultPersonality() : 
            "You are a helpful assistant in Minecraft. Keep responses concise and friendly.";
        setPlayerPersonality(playerId, defaultPersonality);
    }
}
