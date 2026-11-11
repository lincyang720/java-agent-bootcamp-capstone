package com.qiutian.capstone.agent;

import java.util.List;

/**
 * Supervisor Agent：主控路由。
 * 按优先级把问题分发给最合适的子 Agent：售后 > 技术 > 售前（默认）。
 * 优先级设计避免「我想买但担心退货」这类跨意图问题被错误归类。
 */
public class SupervisorAgent {

    private final List<SubAgent> agents;

    public SupervisorAgent() {
        this.agents = List.of(new AfterSaleAgent(), new CalculatorAgent(), new TechAgent(), new PreSaleAgent());
    }

    /** 路由：返回第一个能处理该问题的子 Agent；都没有则回退到售前。 */
    public SubAgent route(String question) {
        for (SubAgent a : agents) {
            if (a.canHandle(question)) {
                return a;
            }
        }
        return agents.get(agents.size() - 1);
    }

    public List<SubAgent> getAgents() {
        return agents;
    }
}
