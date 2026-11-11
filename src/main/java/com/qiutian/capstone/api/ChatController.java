package com.qiutian.capstone.api;

import com.qiutian.capstone.service.CustomerServiceAgent;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 对外 REST 接口：把实战 Agent 暴露成 HTTP 服务，
 * 对应课程第 10 章「Agent 工程化与质量保障」中「Agent 作为微服务 AI 门面」。
 */
@RestController
@RequestMapping("/api")
public class ChatController {

    private final CustomerServiceAgent agent;

    public ChatController(CustomerServiceAgent agent) {
        this.agent = agent;
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> body) {
        String sessionId = body.getOrDefault("sessionId", "default");
        String message = body.getOrDefault("message", "");
        String answer = agent.ask(sessionId, message);
        Map<String, String> resp = new LinkedHashMap<>();
        resp.put("answer", answer);
        resp.put("mode", agent.isOnline() ? "online" : "offline");
        return resp;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> resp = new LinkedHashMap<>();
        resp.put("status", "UP");
        resp.put("mode", agent.isOnline() ? "online" : "offline");
        return resp;
    }
}
