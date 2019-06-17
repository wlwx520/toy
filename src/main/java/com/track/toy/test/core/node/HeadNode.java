package com.track.toy.test.core.node;

import com.track.toy.helper.FileHelper;
import com.track.toy.test.core.asserts.GroupTestAssert;
import com.track.toy.test.core.common.TestGraph;
import com.track.toy.test.core.factory.LoggerFactory;
import com.track.toy.test.core.prepare.PrepareType;

//用于记录测试开始的节点
public class HeadNode extends TestNode {
    private String logRoot;

    public HeadNode(TestGraph testGraph, String logRoot) {
        super(TestGraph.HEAD_NODE, testGraph, PrepareType.ALL, null, null, GroupTestAssert.DEFAULT_TRUE_ASSERT);
        this.logRoot = logRoot;
    }

    @Override
    public void testSelf() {
        LoggerFactory.startLog(FileHelper.getAppRoot() + "/log/" + logRoot);
    }

    @Override
    public HeadNode copy(TestGraph testGraph) {
        return new HeadNode(testGraph,logRoot);
    }

}
