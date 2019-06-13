package com.track.toy.test.core.node;

import com.alibaba.fastjson.JSONObject;
import com.track.toy.graph.Graph;
import com.track.toy.graph.HierarchyNode;
import com.track.toy.test.core.TestGraphBuilder;
import com.track.toy.test.core.asserts.GroupTestAssert;
import com.track.toy.test.core.common.Executors;
import com.track.toy.test.core.prepare.PrepareType;
import lombok.Data;

import java.util.Set;

@Data
public abstract class TestNode {
    protected String name;
    protected Graph<TestNode, Double, String, String> graph = new Graph(new TestGraphBuilder());

    protected PrepareType prepareType = PrepareType.ALL;
    protected String prepareValue;
    protected JSONObject input;
    protected JSONObject output;
    protected GroupTestAssert groupTestAssert;
    protected boolean isSuccess;

    protected Object lock = new Object();
    protected boolean isTesting = false;

    public abstract void testSelf();

    public void doTest() {
        if (isTesting) {
            return;
        }

        isTesting = true;

        while (!prepareType.isPrepared(prepareValue, this)) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        testSelf();

        this.isSuccess = this.groupTestAssert.asserts(this);

        HierarchyNode<TestNode> hierarchy = graph.getPlusHandler().getHierarchy(name, 0, 1);
        Set<HierarchyNode<TestNode>> targets = hierarchy.getTargets();
        targets.forEach(targetTestNode -> {
            Executors.execute(() -> {
                targetTestNode.getData().doTest();
                targetTestNode.getData().lock.notifyAll();
            });
        });
    }
}
