# AI Chat Mod 使用指南

## 快速开始

1. **启动 Minecraft** 并确保 mod 已正确加载
2. **设置 API Key**（只需要做一次）：
   ```
   /aichat setkey sk-your-deepseek-api-key
   ```
3. **开始聊天**：
   ```
   /aichat 你好，我是新手玩家
   ```

## 命令列表

### `/aichat setkey <api-key>`
**管理员专用** - 设置 DeepSeek API 密钥。密钥会自动保存到配置文件中。

**示例**：
```
/aichat setkey sk-1234567890abcdef
```

### `/aichat <消息>`
向 AI 发送消息并获得回复。支持多轮对话。每个玩家有独立的对话历史。

**示例**：
```
/aichat 帮我设计一个城堡
/aichat 我想要一个地下室
/aichat 地下室应该放什么装饰？
```

### `/aichat clear`
清空您的个人对话历史，开始新的对话。

### `/aichat personality <人设描述>`
设置 AI 的个性化人设。每个玩家可以有不同的 AI 人设。

**示例**：
```
/aichat personality 你是一个友善的建筑师，专门帮助玩家设计建筑物
/aichat personality 你是一个幽默的冒险向导，擅长提供游戏攻略
/aichat personality 你是一个严肃的红石工程师，专注于技术解决方案
```

### `/aichat reset`
重置 AI 人设为默认设置。

## 配置文件

API Key 会保存在 `config/ai_chat_config.json` 文件中，格式如下：

```json
{
  "apiKey": "sk-your-api-key",
  "model": "deepseek-chat",
  "apiEndpoint": "https://api.deepseek.com/chat/completions"
}
```

## 注意事项

1. **API Key 安全**：请妥善保管您的 API Key，不要在公共场合分享
2. **网络连接**：需要稳定的网络连接才能与 AI 通信
3. **响应时间**：AI 回复可能需要几秒钟时间
4. **多轮对话**：每个玩家有独立的对话历史，AI 会记住您之前的对话内容
5. **个性化人设**：每个玩家可以设置不同的 AI 人设，互不影响
6. **管理员权限**：只有管理员（OP）可以设置 API Key
7. **服务器支持**：完全支持多人服务器和局域网游戏，每个玩家的对话都是私密的

## 故障排除

### 问题：显示 "请先设置API Key"
**解决方法**：使用 `/aichat setkey <your-key>` 命令设置有效的 API Key

### 问题：API 请求失败
**解决方法**：
1. 检查网络连接
2. 确认 API Key 是否正确
3. 检查 DeepSeek 服务状态

### 问题：无法保存配置
**解决方法**：检查 Minecraft 的 config 目录是否有写入权限
