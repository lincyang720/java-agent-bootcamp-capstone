package com.qiutian.capstone.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会话记忆：按 sessionId 维护多轮对话历史，支持取最近 N 轮作为上下文。
 * 对应课程第 6 章「Agent 记忆机制」与第 15 章「上下文工程的 Select 策略」。
 */
public class ConversationMemory {

    private final Map<String, List<String>> store = new HashMap<>();

    public void add(String sessionId, String who, String message) {
        store.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(who + "：" + message);
    }

    public List<String> history(String sessionId) {
        return store.getOrDefault(sessionId, List.of());
    }

    /** 取最近 last 条对话，拼成上下文文本 */
    public String context(String sessionId, int last) {
        List<String> h = history(sessionId);
        int from = Math.max(0, h.size() - last);
        return String.join("\n", h.subList(from, h.size()));
    }
}
