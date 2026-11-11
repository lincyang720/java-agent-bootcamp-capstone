package com.qiutian.capstone.agent;

import org.springframework.ai.chat.client.ChatClient;

/** 售前咨询子 Agent：价格、参数、功能、会员、推荐类问题。 */
public class PreSaleAgent implements SubAgent {

    @Override
    public String name() {
        return "售前咨询 Agent";
    }

    @Override
    public boolean canHandle(String q) {
        return Intent.any(q, "多少钱", "价格", "优惠", "买", "推荐", "参数", "功能", "支持", "会员", "权益");
    }

    @Override
    public String handle(String q, AgentContext ctx) {
        String kb = ctx.searchTool.search(q);
        if (ctx.chatClient.isPresent()) {
            ChatClient client = ctx.chatClient.get();
            return client.prompt()
                    .system("你是电商售前客服，语言简洁专业，只能基于给定知识库回答，不要编造。")
                    .user("用户问题：" + q + "\n\n知识库片段：\n" + kb)
                    .call()
                    .content();
        }
        return "【知识库依据】\n" + kb + "\n\n（离线模式：建议引导用户联系在线销售获取实时报价与优惠。）";
    }
}
