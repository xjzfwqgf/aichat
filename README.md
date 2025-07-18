## Minecraft AI Chat Mod

- 这是一个 Minecraft Fabric mod，允许您在游戏中与 DeepSeek AI 进行聊天对话。

## 功能特性

- 与 DeepSeek AI 进行多轮对话
- 每个玩家独立的对话历史和 AI 人设
- 可配置的 API Key（管理员权限）
- 支持聊天历史记录管理
- 个性化 AI 人设设置
- 完全支持多人服务器和局域网游戏
- 简单的游戏内命令界面

## 使用方法

### 管理员设置 API Key

```shell
/aichat setkey <your-deepseek-api-key>
```

### 玩家发送消息给 AI

```shell
/aichat <你的消息>
```

### 设置个性化 AI 人设

```shell
/aichat personality 你是一个友善的建筑师，专门帮助玩家设计建筑物
```

### 重置 AI 人设

```shell
/aichat reset
```

### 清空个人对话历史

```shell
/aichat clear
```

### 管理员配置命令

#### 设置 API 地址

```shell
/aichat config endpoint <api-url>
```

#### 设置 AI 模型

```shell
/aichat config model <model-name>
```

#### 设置默认 AI 人设

```shell
/aichat config defaultpersonality <personality>
```

#### 管理预设人设

```shell
# 添加预设人设
/aichat config presets add <name> <personality>

# 删除预设人设
/aichat config presets remove <name>

# 列出所有预设人设
/aichat config presets list

# 使用预设人设
/aichat config presets use <name>
```

#### 查看当前配置

```shell
/aichat config show
```

#### 保存配置

```shell
/aichat config save
```

#### 重新加载配置

```shell
/aichat config reload
```

## 配置文件

配置文件位于 `config/ai-config.json`，包含以下配置项：

```json
{
  "apiKey": null,
  "model": "deepseek-chat",
  "apiEndpoint": "https://api.deepseek.com/chat/completions",
  "defaultPersonality": "You are a helpful assistant in Minecraft. Keep responses concise and friendly.",
  "defaultPersonalities": {
    "builder": "你是一个专业的建筑师，擅长设计各种风格的建筑，会提供详细的建筑建议和指导。",
    "redstone": "你是一个红石工程专家，擅长设计和解释各种红石机器和电路。",
    "helper": "你是一个耐心的新手指导员，会详细解释游戏机制和基础知识。",
    "explorer": "你是一个经验丰富的冒险家，擅长提供探索、战斗和生存建议。"
  }
}
```

### 配置说明
- `apiKey`: DeepSeek API 密钥
- `model`: AI 模型名称
- `apiEndpoint`: API 端点地址
- `defaultPersonality`: 默认 AI 人设
- `defaultPersonalities`: 预设人设库

## 使用示例

### 服务器管理员设置

1. 启动服务器并确保 mod 已加载
2. 使用管理员账户登录
3. 输入 `/aichat setkey sk-your-deepseek-api-key` 设置 API Key

### 玩家使用

1. 连接到服务器
2. 输入 `/aichat personality 你是一个友善的建筑师` 设置个性化 AI
3. 输入 `/aichat 帮我设计一个中世纪城堡` 开始对话
4. 继续对话：`/aichat 城堡需要多高的城墙？`
5. 如需重新开始：`/aichat clear`

## 许可证

本项目使用 MIT 许可证 - 详情请查看 [LICENSE](LICENSE) 文件。
