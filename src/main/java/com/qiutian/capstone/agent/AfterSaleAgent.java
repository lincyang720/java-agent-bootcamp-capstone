package com.qiutian.capstone.agent;

import org.springframework.ai.chat.client.ChatClient;

/** 售后子 Agent：订单、物流、退换货、保修、维修类问题，可调用订单查询工具。 */
public class AfterSaleAgent implements SubAgent {

    @Override
    public String name() {
        return "售后服务 Agent";
    }

    @Override
    public boolean canHandle(String q) {
        return Intent.any(q, "订单", "物流", "发货", "到哪", "退", "换", "保修", "维修", "坏了", "质保");
    }

    @Override
    public String handle(String q, AgentContext ctx) {
        StringBuilder toolResult = new StringBuilder();
        // 简单抽取 5 位数字订单号，演示工具调用
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\d{5,}").matcher(q);
        if (m.find()) {
            String orderId = m.group();
            toolResult.append("【订单工具返回】").append(ctx.orderTool.query(orderId)).append("\n");
        }
        String kb = ctx.searchTool.search(q);
        if (ctx.chatClient.isPresent()) {
            ChatClient client = ctx.chatClient.get();
            return client.prompt()
                    .system("你是电商售后客服，先调用必要工具核实信息，再结合知识库给出处理方案。")
                    .user("用户问题：" + q + "\n\n工具结果：\n" + toolResult + "\n知识库片段：\n" + kb)
                    .call()
                    .content();
        }
        return toolResult + "【知识库依据】\n" + kb + "\n\n（离线模式：以上为工具/知识库检索结果，请人工跟进具体处理。）";
    }
}
