# Java Agent 训练营 · 实战项目：企业智能客服 Agent

这是《Java 版 Agent 架构师训练营》的**整合实战工程**，把课程中分散的多个章节能力串成一个可运行的企业级客服 Agent：

- **第 7 章 RAG**：`rag/KnowledgeBase` 负责文档向量化与语义检索（在线用 LangChain4j `InMemoryEmbeddingStore`，离线退化为关键词召回）。
- **第 4/9 章 多 Agent 编排**：`SupervisorAgent` 按意图把问题路由给 `PreSaleAgent / AfterSaleAgent / TechAgent`，是「中心化编排」模式的落地。
- **第 2/3 章 工具调用**：`OrderQueryTool / CalculatorTool / KnowledgeSearchTool` 演示 Agent 调用确定性外部能力。
- **第 6/15 章 记忆与上下文工程**：`ConversationMemory` 按 `sessionId` 维护多轮上下文。
- **第 10 章 工程化**：`api/ChatController` 把 Agent 暴露成 REST 微服务门面。

## 技术栈

- Java 17 / Spring Boot 3.4
- Spring AI 1.0.0（`ChatClient` / `OpenAiChatModel`）
- LangChain4j 1.0.1（`InMemoryEmbeddingStore` / `OpenAiEmbeddingModel`）

## 运行方式

### 1）离线演示（无需任何 API Key）

```bash
cd capstone
mvn -q spring-boot:run
```

启动后会在控制台打印 4 个演示问题（售前/售后/技术/计算），完整展示
「记忆 → 路由 → 子 Agent → 工具/RAG → 落库」的数据流，并启动 REST 服务。

### 2）在线模式（接入真实大模型）

```bash
export OPENAI_API_KEY=sk-xxxx
mvn -q spring-boot:run
```

设置 `OPENAI_API_KEY` 后，子 Agent 会用 `ChatClient` 基于知识库生成自然语言回答，
RAG 检索切换为真实向量语义召回。

### 3）调用 REST 接口

```bash
curl -X POST http://localhost:8080/api/chat \
  -H 'Content-Type: application/json' \
  -d '{"sessionId":"s1","message":"X1 怎么连接蓝牙"}'
```

健康检查：`GET http://localhost:8080/api/health`

## 目录结构

```
capstone/
├── pom.xml
└── src/main/java/com/qiutian/capstone/
    ├── CapstoneApplication.java      # 启动类 + 演示
    ├── config/                       # KeyGuard / AgentConfig
    ├── rag/KnowledgeBase.java        # RAG 检索侧
    ├── agent/                        # Supervisor + 3 个子 Agent + 路由/上下文
    ├── tools/                        # 订单 / 计算器 / 知识库检索工具
    ├── memory/ConversationMemory.java# 会话记忆
    ├── service/CustomerServiceAgent.java # 总门面
    └── api/ChatController.java       # REST 接口
```

> 设计取舍：为「可编译、可离线跑、可教学」，工程用 Spring AI 做 LLM 编排、
> LangChain4j 做 RAG 向量库，演示「跨框架协作」。真实生产可统一为一个框架，
> 并接入 pgvector / Milvus、MCP 工具网关、Temporal 长任务编排（见课程第 11/17 章）。
