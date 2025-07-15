package com.example;

import com.example.command.ChatCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod implements ModInitializer {
    public static final String MOD_ID = "fabric-mod-example";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    // 创建一个示例物品
    public static final Item EXAMPLE_ITEM = Registry.register(
        Registries.ITEM,
        new Identifier(MOD_ID, "example_item"),
        new Item(new FabricItemSettings())
    );
    
    // 创建一个示例物品组
    public static final ItemGroup EXAMPLE_GROUP = FabricItemGroup.builder()
        .displayName(Text.translatable("itemgroup.fabric-mod-example"))
        .icon(() -> new ItemStack(EXAMPLE_ITEM))
        .entries((displayContext, entries) -> {
            entries.add(EXAMPLE_ITEM);
        })
        .build();
    
    @Override
    public void onInitialize() {
        // 注册物品组
        Registry.register(Registries.ITEM_GROUP, new Identifier(MOD_ID, "example_group"), EXAMPLE_GROUP);
        
        // 注册聊天命令
        ChatCommand.register();
        
        LOGGER.info("AI Chat Mod initialized!");
    }
}
