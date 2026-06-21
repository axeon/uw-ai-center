# uw-ai-center

AI 能力中心微服务。基于 [LangChain4j](https://docs.langchain4j.dev/) 统一对接 OpenAI / Ollama / DashScope（阿里云百炼）等多家模型供应商，对内通过 RPC、对外通过 User/Open 接口提供 AI 对话、流式聊天、图片生成、实时语音识别、RAG 知识库检索与翻译能力。

- **groupId / artifactId**：`com.umtone:uw-ai-center`
- **版本**：1.2.0
- **父工程**：`com.umtone:uw-base`
- **LangChain4j**：1.15.0
- **Elasticsearch**：8.18.1（RAG 向量库）

## 核心能力

| 能力 | 说明 | 关键入口 |
|------|------|---------|
| 同步对话 | 单轮同步生成，支持工具调用 / RAG / 附件 | `AiChatService.generate` |
| 流式对话 | SSE 流式生成 | `AiChatService.chatGenerate` |
| 多轮聊天 | 基于 sessionId 加载历史、流式返回 | `AiChatService.chat` |
| 图片生成 | 文本生成图片（通义万相多图） | `AiImageService.generate` |
| 实时语音识别 | DashScope Fun-ASR，WebSocket 实时流 + 文件转录 | `AiAudioService` / `/ws/audio/transcribe` |
| RAG 检索 | 向量 + BM25 双路召回、Min-Max 归一化加权融合 | `AiRagService.query` / `AiRagSearcher` |
| 翻译 | 数组翻译 / Map 翻译，结构化输出 | `AiTranslateService` |

## 架构分层

```
controller/         接口层，按角色划分
├── rpc/            供其他微服务 RPC 调用（Chat/Image/Audio/Translate/Tool/Config）
├── user/           C 端用户接口（身份由 AuthServiceHelper 注入）
├── open/           对外开放接口 / 枚举查询 / 调试
├── saas/           租户管理（模型配置、API配置、RAG库、会话）
├── admin/          平台管理
└── ops/            运维管理

service/            业务服务（AiChatService / AiImageService / AiAudioService / AiRagService / AiTranslateService / AiRagSearcher）
vendor/             供应商抽象与实现
├── AiVendor             供应商接口
├── AiVendorHelper       供应商注册、配置聚合缓存（FusionCache）、客户端实例缓存（Caffeine）
├── AiVendorClientWrapper 客户端封装（ChatModel/StreamingChatModel/EmbeddingModel/ImageModel/ASR/TTS），AutoCloseable
├── openai/              OpenAI（LangChain4j）
├── ollama/              Ollama（LangChain4j）
└── dashscope/           阿里云 DashScope 原生 API（图片生成、Fun-ASR 实时语音识别、TTS）
tool/               AI 工具（AiToolHelper 通过 RPC 转发工具调用到外部微服务；AiToolCallback 生成 ToolSpecification）
advisor/            AiMysqlChatMemory（MySQL 会话记忆加载/清除）
conf/               配置（ES 客户端、WebSocket、Swagger、Vendor 自动注册、配置属性）
entity/             数据实体（ai_session_info / ai_session_msg / ai_model_config / ai_model_api / ai_rag_lib / ai_rag_doc / ai_tool_info）
vo/ / dto/          值对象与查询参数
constant/           ModelType / ModelTag / SessionType 枚举
util/               AiDocumentSplitter（RAG 文档分割）/ SecurityUtils（SSRF 防护）
```

## 模型类型

`ModelType` 枚举决定 Vendor 客户端的构建方式：

| 类型 | 说明 | OpenAI | Ollama | DashScope |
|------|------|:------:|:------:|:---------:|
| CHAT | 同步 + 流式对话 | ✅ | ✅ | — |
| EMBEDDING | 文本转向量 | ✅ | ✅ | — |
| IMAGE_GENERATION | 图片生成 | — | — | ✅ |
| AUDIO_TRANSCRIPTION | 实时语音识别 | — | — | ✅ |

## 缓存机制

- **FusionCache**（`AiVendorHelper`）：
  - `AiModelConfigData`（configId → 聚合配置）、`AiModelApi`（apiId → API 配置）；
  - `configCode → configId`、`apiCode → apiId` 高频映射缓存，命中唯一索引。
  - 配置变更时由 controller 调用 `invalidateConfig` / `invalidateApiConfig` 级联失效，并通过 `CacheChangeNotifyListener` 关闭并失效底层 `AiVendorClientWrapper`（释放 HTTP 连接池）。
- **Caffeine**：`AiVendorClientWrapper` 按 configId 缓存（最大 1000）；RAG 客户端按 libId 缓存。
- 缓存均带 null 保护，避免穿透。

## 关键设计

- **工具调用循环**：`AiChatService` 中工具调用迭代上限 `MAX_TOOL_ITERATIONS=10`，防止模型反复请求工具导致死循环；工具上下文（`toolContext`）会合并进每次工具入参。
- **会话历史**：`AiMysqlChatMemory.load` 按时间序加载最近 50 条历史消息拼入上下文。
- **RAG 双路召回**：向量（ES KNN）+ BM25（ES match）各路独立 Min-Max 归一化后按权重（默认向量 0.7 / BM25 0.3）加权融合，取 TopK；BM25 失败自动降级为纯向量检索。
- **流式响应**：聊天以 `Flux<String>` + SSE 下发，每段包装为 `AiChatSentEvent`。
- **语音识别**：`DashScopeRealtimeTranscriptionModel` 实例按 configId 复用，单次会话状态封装在 `Session` 内，每次 `start()` 创建新会话上下文。

## 外部依赖

- **MySQL**：会话、消息、模型配置、API 配置、RAG 文档库、工具信息
- **Elasticsearch**：RAG 向量存储与 BM25 全文检索（索引前缀 `uw.ai.rag.`）
- **Redis**：缓存
- **Nacos**：服务注册与配置
- **DashScope**：图片生成（通义万相）、实时语音识别（Fun-ASR）

## 构建

```bash
mvn clean package -DskipTests
```

> 需能访问内网 Maven 仓库（`com.umtone:uw-base` 等内部构件）。本地编译失败通常因父 POM `${revision}` 未解析或内网仓库不可达。

## 运行

```bash
java -jar target/uw-ai-center-1.2.0.jar
```

启动后通过 Nacos 注册，对外提供 RPC / User / Open / Saas / Admin / Ops 多组接口，Swagger 文档在 `debug` / `dev` profile 下开启。

## 客户端 SDK

其他微服务调用本中心的能力，使用客户端模块 `com.umtone:uw-ai`（`AiClientHelper` / `AiChatGenerateParam` 等），通过 RPC 转发到本服务。客户端用法详见 uw-ai 模块文档。
