package com.track.toy.test.core.node;

import com.track.toy.test.core.asserts.GroupTestAssert;
import com.track.toy.test.core.common.TestGraph;
import com.track.toy.test.core.prepare.PrepareType;


//用于记录测试完成的节点
public class TailNode extends TestNode {
    public TailNode(TestGraph testGraph) {
        super(TestGraph.TAIL_NODE, testGraph, PrepareType.ALL, null, null, GroupTestAssert.DEFAULT_TRUE_ASSERT);
    }

    @Override
    public void testSelf() {
    }
}
