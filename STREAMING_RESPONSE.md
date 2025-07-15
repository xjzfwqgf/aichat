# 流式响应功能说明

## 新增特性

### 流式 AI 回复

AI 回复现在支持流式显示，让玩家可以实时看到 AI 的回复过程，而不是等待完整回复后才显示。这大大提升了交互体验，特别是对于长回复内容。

### 工作原理

1. **流式 API 调用**
   - 向 DeepSeek API 发送请求时启用 `stream: true` 参数
   - 使用长连接接收服务器发送的事件流（SSE）
   - 支持实时数据块的解析和显示

2. **实时处理**
   - 服务器端持续接收并解析数据流
   - 使用 BufferedReader 逐行处理响应内容
   - 支持 JSON 格式的增量内容解析

3. **智能显示**
   - 根据内容长度和句子结构动态更新
   - 使用缓冲区合并短小的数据块
   - 在合适的断句位置刷新显示

4. **完整保存**
   - 流式显示完成后保存完整回复
   - 自动添加到对话历史记录
   - 支持后续的对话上下文管理

### 显示策略

#### 更新条件
- 当累积内容超过 20 个字符时更新显示
- 当遇到句子结束标点（。！？换行符）时立即更新显示
- 确保最终显示完整的回复内容

#### 用户体验
- 开始时显示"AI正在思考..."
- 随着 AI 回复的生成，实时更新显示内容
- 流式显示格式：`AI: [实时内容]`

### 技术实现

#### ChatService 改进
```java
// 新增回调机制
private Consumer<String> messageCallback;

// 支持流式响应处理
requestBody.addProperty("stream", true);

// 实时处理每个数据块
BufferedReader reader = new BufferedReader(new StringReader(responseBody));
String line;
while ((line = reader.readLine()) != null) {
    // 解析 SSE 格式数据
    // 调用回调函数实时更新显示
}
```

#### ChatCommand 优化
```java
// 设置流式回调
StringBuilder fullResponse = new StringBuilder();
final String[] lastSentMessage = {""};

chatService.setMessageCallback(content -> {
    fullResponse.append(content);
    // 智能更新显示逻辑
    if (shouldUpdateDisplay(content, fullResponse)) {
        updatePlayerDisplay(player, fullResponse.toString());
    }
});
```

### 使用示例

#### 基本使用
```
/aichat 请帮我设计一个城堡
```

#### 流式显示过程
1. 玩家发送命令
2. 显示："AI正在思考..."
3. 开始流式显示："AI: 我来帮你设计一个中世纪风格的城堡"
4. 继续更新："AI: 我来帮你设计一个中世纪风格的城堡。首先，我们需要考虑城堡的整体布局..."
5. 最终显示完整回复

### 性能优化

#### 显示频率控制
- 避免过于频繁的消息更新
- 使用智能缓冲策略
- 基于内容结构决定更新时机

#### 服务器性能
- 异步处理流式数据
- 避免阻塞主线程
- 合理的缓冲区管理

### 配置说明

流式响应功能默认启用，无需额外配置。如果需要禁用流式显示，可以在 `ChatService` 中将 `stream` 设置为 `false`：

```java
requestBody.addProperty("stream", false);
```

### 兼容性

- 完全向后兼容原有功能
- 支持所有现有的配置选项
- 与多人游戏功能完全兼容
- 每个玩家的流式显示相互独立

### 错误处理

- 网络中断时优雅降级
- 流式数据解析错误时的容错处理
- 超时处理和重试机制

### 注意事项

1. 流式响应依赖于 API 提供商的支持
2. 网络延迟可能影响流式显示的流畅度
3. 长文本回复时的显示优化
4. 确保服务器性能足够处理并发流式请求

## 总结

流式响应功能大幅提升了用户体验，让 AI 对话更加自然和流畅。用户可以实时看到 AI 的思考过程，而不是等待完整回复。这个功能特别适合长文本回复和复杂问题的解答。
