package com.track.toy.test.core.asserts;

import com.track.toy.test.core.Constant;
import com.track.toy.test.core.node.TestNode;
import lombok.Data;

@Data
public class TestAssert {
    protected String source;
    protected String target;
    protected TestAssertType type;
    protected boolean isSuccess;

    //断言，通过表达式统一判断
    public boolean asserts(TestNode testNode) {
        Object expressedSource = Constant.express(source, testNode.getTestGraph().getTempGraphData());
        Object expressedTarget = Constant.express(target, testNode.getTestGraph().getTempGraphData());
        this.isSuccess = type.judge(expressedSource, expressedTarget);
        return this.isSuccess;
    }
}
