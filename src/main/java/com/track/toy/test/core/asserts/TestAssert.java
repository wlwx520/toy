package com.track.toy.test.core.asserts;

import com.track.toy.test.core.common.Constant;
import com.track.toy.test.core.node.TestNode;
import lombok.Data;

@Data
public class TestAssert {
    protected String expressionStr;
    protected boolean isSuccess;

    //断言，通过表达式统一判断
    public boolean asserts(TestNode testNode) {
        return this.isSuccess = Boolean.valueOf(Constant.EXPRESSION_HELPER.expressionFilter(expressionStr, testNode));
    }
}
