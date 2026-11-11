package com.qiutian.capstone.service;

import com.qiutian.capstone.agent.AgentContext;
import com.qiutian.capstone.agent.SubAgent;
import com.qiutian.capstone.agent.SupervisorAgent;
import com.qiutian.capstone.memory.ConversationMemory;
import com.qiutian.capstone.rag.KnowledgeBase;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 客服总 Agent：串联「记忆 -> 路由 -> 子 Agent 处理 -> 落库记忆」的完整闭环。
 * 这是实战项目的对外门面（Facade），REST 层只与它交互。
 */
@Service
public class CustomerServiceAgent {

    private final KnowledgeBase kb = new KnowledgeBase();
    private final ConversationMemory memory = new ConversationMemory();
    private final SupervisorAgent supervisor = new SupervisorAgent();
    private final Optional<ChatClient> chatClient;

    public CustomerServiceAgent(Optional<ChatClient> chatClient) {
        this.chatClient = chatClient;
    }

    public String ask(String sessionId, String question) {
        AgentContext ctx = new AgentContext(kb, memory, chatClient, sessionId);
        SubAgent agent = supervisor.route(question);

        memory.add(sessionId, "用户", question);
        String answer = agent.handle(question, ctx);
        memory.add(sessionId, "客服", answer);

        return "【" + agent.name() + "】\n" + answer;
    }

    public boolean isOnline() {
        return kb.isOnline();
    }
}
