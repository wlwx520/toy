package com.track.toy.test.core.node;

import com.track.toy.test.core.asserts.GroupTestAssert;
import com.track.toy.test.core.common.TestGraph;
import com.track.toy.test.core.prepare.PrepareType;


//用于记录测试完成的节点
public class TailNode extends TestNode {
    public TailNode(TestGraph testGraph, String testDateName, PrepareType type, String prepareValue) {
        super(TestGraph.TAIL_NODE, testDateName, testGraph, type, prepareValue, null, GroupTestAssert.DEFAULT_TRUE_ASSERT);
    }

    @Override
    public void testSelf() {
    }

    @Override
    public TailNode copy(TestGraph testGraph, String testDateName) {
        return new TailNode(testGraph, testDateName, prepareType, prepareValue);
    }
}
