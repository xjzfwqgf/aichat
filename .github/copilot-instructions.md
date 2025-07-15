<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

# Minecraft Fabric Mod Development Instructions

这是一个 Minecraft Fabric mod 开发项目。在开发过程中，请遵循以下指导原则：

## 代码风格
- 使用 Java 17 语法特性
- 遵循 Minecraft 和 Fabric 的命名约定
- 使用适当的注释和文档

## Fabric 特定指导
- 使用 Fabric API 提供的功能而不是直接使用 Minecraft 内部 API
- 正确处理客户端和服务器端的代码分离
- 使用 Mixin 时要小心，确保兼容性

## 项目结构
- 主要的 mod 逻辑放在 `src/main/java/com/example/` 目录中
- 客户端特定代码应该在适当的地方进行条件编译
- 资源文件放在 `src/main/resources/assets/` 目录中

## 最佳实践
- 总是测试 mod 在单人和多人环境中的行为
- 使用适当的日志记录来调试问题
- 确保 mod 与其他 Fabric mod 兼容
