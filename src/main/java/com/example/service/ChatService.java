package com.example.service;

import com.example.config.AIConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.minecraft.text.Text;

import java.io.BufferedReader;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;


public class ChatService {
    private final AIConfig config;
    private final HttpClient httpClient;
    private final Gson gson;
    private final List<Map<String, String>> conversationHistory;
    private Consumer<String> messageCallback;
    
    private String currentPersonality;
    
    public ChatService(AIConfig config, String personality) {
        this.config = config;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.conversationHistory = new ArrayList<>();
        this.currentPersonality = personality;
        this.messageCallback = null;
        
        // 添加系统消息
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", personality);
        conversationHistory.add(systemMessage);
    // ...existing code...
}

    public void setMessageCallback(Consumer<String> callback) {
        this.messageCallback = callback;
    }

    public void clearHistory() {
        conversationHistory.clear();
        // 重新添加系统消息
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", currentPersonality);
        conversationHistory.add(systemMessage);
    }
    
    public ChatService(AIConfig config) {
        this(config, "You are a helpful assistant in Minecraft. Keep responses concise and friendly.");
    }
    
    public CompletableFuture<String> sendMessage(String message) {
        // 添加用户消息到历史记录
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", message);
        conversationHistory.add(userMessage);

        // 创建请求体
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", config.getModel());
        requestBody.addProperty("stream", messageCallback != null);

        JsonArray messages = new JsonArray();
        for (Map<String, String> msg : conversationHistory) {
            JsonObject msgObj = new JsonObject();
            msgObj.addProperty("role", msg.get("role"));
            msgObj.addProperty("content", msg.get("content"));
            messages.add(msgObj);
        }
        requestBody.add("messages", messages);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(config.getApiEndpoint()))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + config.getApiKey())
            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
            .build();

        if (messageCallback != null) {
            // 流式处理
            StringBuilder fullResponse = new StringBuilder();
            CompletableFuture<String> future = new CompletableFuture<>();
            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        response.body().forEach(line -> {
                            if (line.startsWith("data: ")) {
                                String data = line.substring(6);
                                if ("[DONE]".equals(data)) {
                                    future.complete(fullResponse.toString());
                                    return;
                                }
                                try {
                                    JsonObject jsonData = JsonParser.parseString(data).getAsJsonObject();
                                    if (jsonData.has("choices")) {
                                        JsonArray choices = jsonData.getAsJsonArray("choices");
                                        if (choices.size() > 0) {
                                            JsonObject choice = choices.get(0).getAsJsonObject();
                                            if (choice.has("delta")) {
                                                JsonObject delta = choice.getAsJsonObject("delta");
                                                if (delta.has("content")) {
                                                    String content = delta.get("content").getAsString();
                                                    fullResponse.append(content);
                                                    messageCallback.accept(content);
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    // 忽略解析错误
                                }
                            }
                        });
                        // 添加AI回复到历史记录
                        Map<String, String> assistantMessage = new HashMap<>();
                        assistantMessage.put("role", "assistant");
                        assistantMessage.put("content", fullResponse.toString());
                        conversationHistory.add(assistantMessage);
                    } else {
                        future.complete("API请求失败: " + response.statusCode());
                    }
                })
                .exceptionally(e -> {
                    future.complete("发送消息时出错: " + e.getMessage());
                    return null;
                });
            return future;
        } else {
            // 非流式处理
            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    String aiResponse;
                    if (response.statusCode() == 200) {
                        JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();
                        aiResponse = responseJson.getAsJsonArray("choices")
                            .get(0).getAsJsonObject()
                            .getAsJsonObject("message")
                            .get("content").getAsString();
                        // 添加AI回复到历史记录
                        Map<String, String> assistantMessage = new HashMap<>();
                        assistantMessage.put("role", "assistant");
                        assistantMessage.put("content", aiResponse);
                        conversationHistory.add(assistantMessage);
                    } else {
                        aiResponse = "API请求失败: " + response.statusCode();
                    }
                    return aiResponse;
                })
                .exceptionally(e -> "发送消息时出错: " + e.getMessage());
        }
    }
}
