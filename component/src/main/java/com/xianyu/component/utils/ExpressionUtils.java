package com.xianyu.component.utils;

import com.xianyu.component.helper.SpringHelper;
import java.lang.reflect.Method;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class ExpressionUtils {

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    public static boolean isEl(String param) {
        return Objects.nonNull(param) && param.startsWith("#");
    }

    public static @Nullable Object parseExpression(String expressionString, Method method, Object[] args) {
        // 1. 先解析占位符
        String placeholderResolved = SpringHelper.getEnvironment().resolvePlaceholders(expressionString);

        // 2. 准备 SpEL evaluation context
        StandardEvaluationContext context = new StandardEvaluationContext();
        String[] paramNames = new DefaultParameterNameDiscoverer().getParameterNames(method);
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }

        // 3. 解析表达式
        Expression expression = PARSER.parseExpression(placeholderResolved);
        Object valueObj = expression.getValue(context);
        return valueObj;
    }
}