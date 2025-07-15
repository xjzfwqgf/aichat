package com.example.config;

import java.util.HashMap;
import java.util.Map;

public class AIConfig {
    private String apiKey;
    private String model = "deepseek-chat";
    private String apiEndpoint = "https://api.deepseek.com/chat/completions";
    private Map<String, String> defaultPersonalities = new HashMap<>();
    private String defaultPersonality = "You are a helpful assistant in Minecraft. Keep responses concise and friendly.";
    
    public AIConfig() {
        // 初始化默认人设列表
        defaultPersonalities.put("builder", "你是一个专业的建筑师，擅长设计各种风格的建筑，会提供详细的建筑建议和指导。");
        defaultPersonalities.put("redstone", "你是一个红石工程专家，擅长设计和解释各种红石机器和电路。");
        defaultPersonalities.put("helper", "你是一个耐心的新手指导员，会详细解释游戏机制和基础知识。");
        defaultPersonalities.put("explorer", "你是一个经验丰富的冒险家，擅长提供探索、战斗和生存建议。");
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getApiEndpoint() {
        return apiEndpoint;
    }
    
    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }
    
    public String getDefaultPersonality() {
        return defaultPersonality;
    }
    
    public void setDefaultPersonality(String defaultPersonality) {
        this.defaultPersonality = defaultPersonality;
    }
    
    public Map<String, String> getDefaultPersonalities() {
        return defaultPersonalities;
    }
    
    public void setDefaultPersonalities(Map<String, String> defaultPersonalities) {
        this.defaultPersonalities = defaultPersonalities;
    }
    
    public void addDefaultPersonality(String name, String personality) {
        defaultPersonalities.put(name, personality);
    }
    
    public String getPersonality(String name) {
        return defaultPersonalities.getOrDefault(name, defaultPersonality);
    }
    
    public void removeDefaultPersonality(String name) {
        defaultPersonalities.remove(name);
    }
}
