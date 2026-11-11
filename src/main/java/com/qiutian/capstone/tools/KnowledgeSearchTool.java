package com.qiutian.capstone.tools;

import com.qiutian.capstone.rag.KnowledgeBase;

import java.util.List;

/**
 * 知识库检索工具：把 RAG 检索能力包装成 Agent 可调用的工具，
 * 对应课程第 7 章 RAG 与第 11 章工具/MCP 的核心思想。
 */
public class KnowledgeSearchTool {

    private final KnowledgeBase kb;

    public KnowledgeSearchTool(KnowledgeBase kb) {
        this.kb = kb;
    }

    public String search(String query) {
        List<String> hits = kb.retrieve(query, 3);
        if (hits.isEmpty()) {
            return "（知识库未检索到相关内容）";
        }
        return String.join("\n", hits);
    }
}
