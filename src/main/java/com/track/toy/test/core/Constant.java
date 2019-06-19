package com.track.toy.test.core;

import com.track.toy.helper.ExpressionHelper;
import com.track.toy.test.core.expression.ActionInfoExpression;
import com.track.toy.test.core.expression.DataExpression;
import com.track.toy.test.core.expression.FileExpression;

public class Constant {
    //表达式管理器
    private static final ExpressionHelper EXPRESSION_HELPER = new ExpressionHelper();

    public static String express(String origin, Object... objects) {
        return EXPRESSION_HELPER.expressionFilter(origin, objects);
    }

    public static void addFilter(String key, ExpressionHelper.ExpressionFilter filter) {
        EXPRESSION_HELPER.addFilter(key, filter);
    }

    //初始化所有表达式
    public static void initExpression() {
        ActionInfoExpression.init();
        FileExpression.init();
        DataExpression.init();
    }
}
