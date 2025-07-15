# 配置文件和命令使用指南

## 配置文件功能

### 1. 配置文件位置

配置文件位于 `config/ai-config.json`

### 2. 配置文件结构
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

## 新增的配置命令

### 1. API 设置命令（管理员权限）

#### 设置 API 地址
```
/aichat config endpoint https://api.deepseek.com/chat/completions
```

#### 设置 AI 模型
```
/aichat config model deepseek-chat
```

#### 设置默认 AI 人设
```
/aichat config defaultpersonality 你是一个友善的 Minecraft 助手
```

### 2. 预设人设管理（管理员权限）

#### 添加预设人设
```
/aichat config presets add pvp 你是一个 PvP 专家，擅长战斗策略和装备搭配
```

#### 删除预设人设
```
/aichat config presets remove pvp
```

#### 列出所有预设人设
```
/aichat config presets list
```

### 3. 玩家使用预设人设

#### 使用预设人设
```
/aichat config presets use builder
```

### 4. 配置管理命令（管理员权限）

#### 查看当前配置
```
/aichat config show
```

#### 保存配置
```
/aichat config save
```

#### 重新加载配置
```
/aichat config reload
```

## 使用场景示例

### 场景1：服务器管理员设置自定义 API
```
# 设置自定义 API 地址
/aichat config endpoint https://your-custom-api.com/chat/completions

# 设置自定义模型
/aichat config model your-custom-model

# 保存配置
/aichat config save
```

### 场景2：创建专业化 AI 助手
```
# 添加建筑师人设
/aichat config presets add architect 你是一个专业的建筑师，擅长中世纪、现代和幻想风格的建筑设计

# 添加红石工程师人设
/aichat config presets add engineer 你是一个红石工程专家，精通各种自动化装置和逻辑电路

# 玩家使用建筑师人设
/aichat config presets use architect
```

### 场景3：多语言支持
```
# 设置中文默认人设
/aichat config defaultpersonality 你是一个友善的 Minecraft 助手，用中文回答问题

# 添加英文人设
/aichat config presets add english You are a helpful Minecraft assistant. Answer in English.
```

## 权限说明

- **管理员权限**（OP 等级 2）：
  - 设置 API Key
  - 配置 API 端点和模型
  - 管理预设人设
  - 查看和保存配置

- **普通玩家权限**：
  - 发送消息给 AI
  - 设置个人人设
  - 使用预设人设
  - 清空个人对话历史

## 注意事项

1. 配置文件在服务器启动时自动加载
2. 使用 `/aichat config save` 保存配置到文件
3. 使用 `/aichat config reload` 重新加载配置文件
4. 每个玩家的对话历史和人设是独立的
5. 预设人设可以被所有玩家使用
6. API Key 和端点配置是全局的，影响所有玩家
