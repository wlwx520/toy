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
        String expressedSource = Constant.express(source, testNode.getTestGraph().getTempGraphData());
        String expressedTarget = Constant.express(target, testNode.getTestGraph().getTempGraphData());
        return this.isSuccess = type.judge(expressedSource, expressedTarget);
    }
}
