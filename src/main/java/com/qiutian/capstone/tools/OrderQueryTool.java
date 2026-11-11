package com.qiutian.capstone.tools;

import java.util.Map;

/**
 * 订单查询工具（桩实现）。
 * 真实环境应调用订单中心 RPC / Feign 接口；这里用内存 Map 模拟，演示
 * 「Agent 调用外部工具」这一核心能力——工具返回确定性数据，不依赖 LLM。
 */
public class OrderQueryTool {

    private static final Map<String, String> ORDERS = Map.of(
            "12345", "订单 12345：已发货，预计明日 18:00 前送达。",
            "67890", "订单 67890：已签收，状态=完成。"
    );

    public String query(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            return "未提供订单号。";
        }
        return ORDERS.getOrDefault(orderId.trim(), "未找到订单 " + orderId + " 的信息，请核对订单号。");
    }
}
