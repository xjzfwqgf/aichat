package com.example.util;

import com.example.config.AIConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final String CONFIG_FILE = "ai_chat_config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    public static AIConfig loadConfig() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE);
        
        if (Files.exists(configPath)) {
            try (Reader reader = Files.newBufferedReader(configPath)) {
                return GSON.fromJson(reader, AIConfig.class);
            } catch (IOException e) {
                System.err.println("Failed to load config: " + e.getMessage());
            }
        }
        
        return new AIConfig();
    }
    
    public static void saveConfig(AIConfig config) {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE);
        
        try (Writer writer = Files.newBufferedWriter(configPath)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }
}
