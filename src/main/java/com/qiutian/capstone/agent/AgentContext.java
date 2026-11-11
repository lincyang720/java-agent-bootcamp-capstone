package com.qiutian.capstone.agent;

import com.qiutian.capstone.memory.ConversationMemory;
import com.qiutian.capstone.rag.KnowledgeBase;
import com.qiutian.capstone.tools.KnowledgeSearchTool;
import com.qiutian.capstone.tools.OrderQueryTool;
import org.springframework.ai.chat.client.ChatClient;

import java.util.Optional;

/**
 * Agent 运行时上下文：把子 Agent 需要的全部依赖（知识库、工具、记忆、可选 LLM）打包传递。
 * 这是「上下文工程」思想在代码里的落地——每个子 Agent 只拿到它该拿的上下文，互不污染。
 */
public class AgentContext {

    public final KnowledgeBase kb;
    public final KnowledgeSearchTool searchTool;
    public final OrderQueryTool orderTool;
    public final ConversationMemory memory;
    public final Optional<ChatClient> chatClient;
    public final String sessionId;

    public AgentContext(KnowledgeBase kb,
                       ConversationMemory memory,
                       Optional<ChatClient> chatClient,
                       String sessionId) {
        this.kb = kb;
        this.searchTool = new KnowledgeSearchTool(kb);
        this.orderTool = new OrderQueryTool();
        this.memory = memory;
        this.chatClient = chatClient;
        this.sessionId = sessionId;
    }
}
