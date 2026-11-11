package com.qiutian.capstone.tools;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * 计算器工具：演示 Agent 调用确定性外部能力完成数值计算。
 * 使用自带的栈式算术求值器（Shunting-yard），不依赖任何脚本引擎，
 * 仅支持数字与 + - * / ( ) 运算符，避免表达式注入。
 */
public class CalculatorTool {

    public String calc(String expr) {
        if (expr == null || expr.isBlank()) {
            return "空表达式。";
        }
        if (!expr.matches("[0-9+\\-*/().\\s]+")) {
            return "表达式含非法字符，仅支持数字与 + - * / ( )。";
        }
        try {
            double result = evaluate(expr);
            // 整数结果去掉小数尾巴
            String resultStr = (result == Math.rint(result))
                    ? String.valueOf((long) result)
                    : String.valueOf(result);
            return expr.trim() + " = " + resultStr;
        } catch (Exception e) {
            return "计算错误：" + e.getMessage();
        }
    }

    /** 中缀表达式求值：词法 -> 调度场算法转 RPN -> 栈求值 */
    private double evaluate(String expr) {
        List<String> tokens = tokenize(expr);
        List<String> rpn = toRpn(tokens);
        return evalRpn(rpn);
    }

    private List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        int i = 0;
        while (i < expr.length()) {
            char c = expr.charAt(i);
            if (c == ' ' || c == '\t') {
                i++;
            } else if ("+-*/()".indexOf(c) >= 0) {
                tokens.add(String.valueOf(c));
                i++;
            } else if (Character.isDigit(c) || c == '.') {
                StringBuilder num = new StringBuilder();
                while (i < expr.length()
                        && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    num.append(expr.charAt(i++));
                }
                tokens.add(num.toString());
            } else {
                i++;
            }
        }
        return tokens;
    }

    private List<String> toRpn(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Deque<String> ops = new ArrayDeque<>();
        for (String t : tokens) {
            if (isNumber(t)) {
                output.add(t);
            } else if (t.equals("(")) {
                ops.push(t);
            } else if (t.equals(")")) {
                while (!ops.isEmpty() && !ops.peek().equals("(")) {
                    output.add(ops.pop());
                }
                if (!ops.isEmpty()) {
                    ops.pop(); // 丢弃 '('
                }
            } else { // 运算符
                while (!ops.isEmpty() && precedence(ops.peek()) >= precedence(t)) {
                    output.add(ops.pop());
                }
                ops.push(t);
            }
        }
        while (!ops.isEmpty()) {
            output.add(ops.pop());
        }
        return output;
    }

    private double evalRpn(List<String> rpn) {
        Deque<Double> stack = new ArrayDeque<>();
        for (String t : rpn) {
            if (isNumber(t)) {
                stack.push(Double.parseDouble(t));
            } else {
                double b = stack.pop();
                double a = stack.pop();
                stack.push(apply(a, b, t));
            }
        }
        return stack.pop();
    }

    private boolean isNumber(String t) {
        try {
            Double.parseDouble(t);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int precedence(String op) {
        if ("+-".indexOf(op) >= 0) {
            return 1;
        }
        if ("*/".indexOf(op) >= 0) {
            return 2;
        }
        return 0;
    }

    private double apply(double a, double b, String op) {
        return switch (op) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> a / b;
            default -> throw new IllegalArgumentException("未知运算符 " + op);
        };
    }
}
