package com.qiutian.capstone.agent;

/**
 * 子 Agent 统一接口：每个垂直领域（售前/售后/技术）实现一个 SubAgent，
 * 由 SupervisorAgent 负责路由分发。
 */
public interface SubAgent {

    /** 子 Agent 名称，用于回复时标注来源 */
    String name();

    /** 是否能处理该问题（意图识别） */
    boolean canHandle(String question);

    /** 处理问题，返回面向用户的回答 */
    String handle(String question, AgentContext ctx);
}
