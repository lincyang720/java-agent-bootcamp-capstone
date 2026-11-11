package com.qiutian.capstone.agent;

import com.qiutian.capstone.tools.CalculatorTool;

/**
 * 计算子 Agent：处理含数字与运算符的算术类问题，演示「Agent 调用确定性工具」。
 * 这是课程第 2/3 章「工具调用」在实战中的落点——把可精确计算的任务交给工具，
 * 而非让 LLM 硬算（避免幻觉）。
 */
public class CalculatorAgent implements SubAgent {

    private final CalculatorTool calcTool = new CalculatorTool();

    @Override
    public String name() {
        return "计算 Agent";
    }

    @Override
    public boolean canHandle(String q) {
        return q.matches(".*[0-9].*(乘|加|减|除|×|÷|[+\\-*/]).*")
                || q.matches(".*(乘|加|减|除|×|÷|[+\\-*/]).*[0-9].*");
    }

    @Override
    public String handle(String q, AgentContext ctx) {
        // 先把中文运算符统一成英文符号，便于计算器工具解析
        String normalized = q.replace("乘以", "*").replace("乘", "*")
                .replace("除以", "/").replace("除", "/")
                .replace("加上", "+").replace("加", "+")
                .replace("减去", "-").replace("减", "-");
        // 抽取表达式：保留数字与运算符（+ - * / × ÷ 及括号空格）
        StringBuilder expr = new StringBuilder();
        for (char c : normalized.toCharArray()) {
            if ((c >= '0' && c <= '9') || "+-*/().×÷ ".indexOf(c) >= 0) {
                expr.append(c == '×' ? '*' : (c == '÷' ? '/' : c));
            }
        }
        String result = calcTool.calc(expr.toString().trim());
        if (ctx.chatClient.isPresent()) {
            return ctx.chatClient.get().prompt()
                    .system("你是计算客服，先用工具算出结果，再用一句话向用户解释。")
                    .user("用户问题：" + q + "\n工具结果：" + result)
                    .call()
                    .content();
        }
        return "【工具计算结果】" + result + "\n（离线模式：以上为计算器工具返回。）";
    }
}
