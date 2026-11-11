package com.qiutian.capstone.agent;

import org.springframework.ai.chat.client.ChatClient;

/** 技术支持的子 Agent：连接、设置、使用教程、故障排查类问题。 */
public class TechAgent implements SubAgent {

    @Override
    public String name() {
        return "技术支持 Agent";
    }

    @Override
    public boolean canHandle(String q) {
        return Intent.any(q, "连接", "设置", "怎么", "如何", "教程", "故障", "用不了", "配对", "蓝牙", "安装");
    }

    @Override
    public String handle(String q, AgentContext ctx) {
        String kb = ctx.searchTool.search(q);
        if (ctx.chatClient.isPresent()) {
            ChatClient client = ctx.chatClient.get();
            return client.prompt()
                    .system("你是产品技术支持，给出清晰的分步操作指引，引用知识库中的参数与步骤。")
                    .user("用户问题：" + q + "\n\n知识库片段：\n" + kb)
                    .call()
                    .content();
        }
        return "【知识库依据】\n" + kb + "\n\n（离线模式：请按上述步骤引导用户操作。）";
    }
}
