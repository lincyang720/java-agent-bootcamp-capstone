package com.qiutian.capstone.config;

/**
 * API Key 守卫：所有真实模型调用前必须经过本类检查。
 * 没有 Key 时工程进入「离线演示模式」，用确定性桩逻辑跑通完整链路，
 * 方便你在本机零配置就能看清楚 Agent 的架构与数据流转。
 */
public final class KeyGuard {

    private KeyGuard() {
    }

    public static boolean hasKey(String name) {
        String v = System.getenv(name);
        return v != null && !v.isBlank();
    }

    public static String get(String name) {
        return System.getenv(name);
    }
}
