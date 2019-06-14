package com.track.toy.test.core;

import com.track.toy.helper.ExpressionHelper;
import com.track.toy.test.core.expression.ActionInfoExpression;

public class Constant {
    //表达式管理器
    public static final ExpressionHelper EXPRESSION_HELPER = new ExpressionHelper();

    //初始化所有表达式
    public static void initExpression() {
        ActionInfoExpression.init();
    }
}
