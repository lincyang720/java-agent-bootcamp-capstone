package com.qiutian.capstone.rag;

import com.qiutian.capstone.config.KeyGuard;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.ArrayList;
import java.util.List;

/**
 * 知识库（RAG 的检索侧）。
 *
 * 在线模式（有 OPENAI_API_KEY）：用 OpenAiEmbeddingModel 把文档向量化，
 * 存入 InMemoryEmbeddingStore，检索时做语义相似度召回（Top-K）。
 *
 * 离线模式（无 Key）：退化为「关键词重叠打分」召回，保证链路可跑通，
 * 让你在没有模型的情况下也能看清楚 RAG 的「检索 -> 拼上下文 -> 生成」结构。
 */
public class KnowledgeBase {

    private final List<String> docs = new ArrayList<>();
    private final InMemoryEmbeddingStore<TextSegment> store;
    private final EmbeddingModel embeddingModel;
    private final boolean online;

    public KnowledgeBase() {
        // 业务知识：电商客服场景的售前/售后/技术文档
        seed("退换货政策：商品签收后 7 天内支持无理由退换，需保持外包装与配件完整。");
        seed("配送说明：下单后 48 小时内发货，偏远地区时效顺延 1-2 天。");
        seed("保修条款：X 系列电子产品提供 12 个月质保，人为损坏不在保修范围内。");
        seed("X1 智能音箱参数：支持蓝牙 5.0、内置语音助手、可控制主流智能家居设备。");
        seed("X1 智能音箱连接：长按顶部电源键 3 秒进入配对模式，在手机蓝牙列表中选择 X1 即可。");
        seed("会员权益：黄金会员享全年免运费与专属客服通道，年费 99 元。");

        this.online = KeyGuard.hasKey("OPENAI_API_KEY");
        if (online) {
            this.embeddingModel = OpenAiEmbeddingModel.builder()
                    .apiKey(KeyGuard.get("OPENAI_API_KEY"))
                    .build();
            this.store = new InMemoryEmbeddingStore<>();
            for (String d : docs) {
                Embedding e = embeddingModel.embed(d).content();
                store.add(e, TextSegment.from(d));
            }
        } else {
            this.embeddingModel = null;
            this.store = null;
        }
    }

    private void seed(String text) {
        docs.add(text);
    }

    /** 检索与 query 最相关的 Top-K 文档片段 */
    public List<String> retrieve(String query, int k) {
        if (online) {
            Embedding q = embeddingModel.embed(query).content();
            EmbeddingSearchRequest req = EmbeddingSearchRequest.builder()
                    .queryEmbedding(q)
                    .maxResults(k)
                    .build();
            EmbeddingSearchResult<TextSegment> result = store.search(req);
            List<String> hits = new ArrayList<>();
            for (EmbeddingMatch<TextSegment> m : result.matches()) {
                TextSegment seg = m.embedded();
                if (seg != null) {
                    hits.add(seg.text());
                }
            }
            return hits;
        }
        return offlineRetrieve(query, k);
    }

    /** 离线召回：按关键词重叠数量排序取 Top-K */
    private List<String> offlineRetrieve(String query, int k) {
        String[] words = query.toLowerCase().split("\\s+");
        List<String> sorted = new ArrayList<>(docs);
        sorted.sort((a, b) -> score(b, words) - score(a, words));
        return sorted.stream().limit(k).toList();
    }

    private int score(String doc, String[] words) {
        String low = doc.toLowerCase();
        int s = 0;
        for (String w : words) {
            if (!w.isBlank() && low.contains(w)) {
                s++;
            }
        }
        return s;
    }

    public boolean isOnline() {
        return online;
    }
}
