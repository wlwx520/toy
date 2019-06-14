package com.track.toy.test.core.common;

import com.track.toy.graph.IGraph;
import com.track.toy.test.core.node.TestNode;

public class TestGraphBuilder implements IGraph<TestNode, Double, String, String> {
    @Override
    public String edgeKey(TestNode source, TestNode target) {
        return source.getName() + "-" + target.getName();
    }

    @Override
    public Double edgeRight(TestNode source, TestNode target) {
        return 1d;
    }

    @Override
    public String nodeKey(TestNode data) {
        return data.getName();
    }
}
