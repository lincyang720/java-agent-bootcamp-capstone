package com.qiutian.capstone;

import com.qiutian.capstone.service.CustomerServiceAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 实战项目启动类。
 *
 * 启动后会自动跑 4 个演示问题（离线模式，无需任何 API Key），
 * 让你直接看到「多 Agent 路由 + RAG 检索 + 工具调用 + 记忆」的完整数据流。
 * 同时对外提供 REST 接口：POST http://localhost:8080/api/chat
 */
@SpringBootApplication
public class CapstoneApplication implements CommandLineRunner {

    @Autowired
    private CustomerServiceAgent agent;

    public static void main(String[] args) {
        SpringApplication.run(CapstoneApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("==================================================");
        System.out.println("  Java Agent 训练营 · 实战项目演示");
        System.out.println("  模式：" + (agent.isOnline() ? "在线（已读取 OPENAI_API_KEY）" : "离线（关键词召回 + 桩逻辑）"));
        System.out.println("==================================================");

        String[] questions = {
                "X1 智能音箱多少钱？有什么功能？",
                "我的订单 12345 到哪了？",
                "怎么把 X1 连接蓝牙？",
                "帮我算一下 199 乘以 3 等于多少"
        };

        for (String q : questions) {
            System.out.println("\n>> 用户：" + q);
            System.out.println(agent.ask("demo-session", q));
            System.out.println("--------------------------------------------------");
        }

        System.out.println("\n演示结束。REST 服务已启动：POST http://localhost:8080/api/chat");
        System.out.println("请求体示例：{\"sessionId\":\"s1\",\"message\":\"X1 怎么连蓝牙\"}");
    }
}
