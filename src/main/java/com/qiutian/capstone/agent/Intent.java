package com.qiutian.capstone.agent;

/** 意图识别小工具：判断用户问题是否包含给定关键词之一。 */
public final class Intent {

    private Intent() {
    }

    public static boolean any(String question, String... keywords) {
        if (question == null) {
            return false;
        }
        for (String k : keywords) {
            if (question.contains(k)) {
                return true;
            }
        }
        return false;
    }
}
