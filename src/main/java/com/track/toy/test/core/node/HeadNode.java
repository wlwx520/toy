package com.track.toy.test.core.node;

import com.track.toy.test.core.asserts.GroupTestAssert;
import com.track.toy.test.core.common.TestGraph;
import com.track.toy.test.core.prepare.PrepareType;

//用于记录测试开始的节点
public class HeadNode extends TestNode {

    public HeadNode(TestGraph testGraph, String testDateName) {
        super(TestGraph.HEAD_NODE, testDateName, testGraph, PrepareType.ALL, null, null, GroupTestAssert.DEFAULT_TRUE_ASSERT);
    }

    @Override
    public void testSelf() {
    }

    @Override
    public HeadNode copy(TestGraph testGraph, String testDateName) {
        return new HeadNode(testGraph, testDateName);
    }

}
