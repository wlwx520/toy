package com.track.toy.test.core.node;

import com.alibaba.fastjson.JSONObject;
import com.track.toy.graph.HierarchyNode;
import com.track.toy.test.core.asserts.GroupTestAssert;
import com.track.toy.test.core.common.FileLogger;
import com.track.toy.test.core.common.TestGraph;
import com.track.toy.test.core.prepare.PrepareType;
import lombok.Data;

import java.util.Set;

//抽象测试节点
@Data
public abstract class TestNode {
    //节点名
    protected String name;
    //数据名
    protected String testDateName;
    //整个测试图，用于探索测试的先后，与并发测试
    protected TestGraph testGraph;
    //日志
    protected FileLogger fileLogger;

    //默认该节点要开启测试必须要所有父节点执行完成
    protected PrepareType prepareType;
    //如选用PrepareType.ANY,则输入number，表示至少有几个父节点完成则开始执行，如选用PrepareType.SIGN，则输入String按'，'分割，表示需要哪些特定的父节点完成
    protected String prepareValue;
    //节点入参，使用表达式快速组合入参或引用其他节点的数据
    protected JSONObject input;
    //节点返回值，用于断言判断节点测试是否成功
    protected JSONObject output;
    //节点断言，用于断言判断节点测试是否成功
    protected GroupTestAssert groupTestAssert;
    //是否成功
    protected volatile boolean isSuccess;

    //锁，用于节点在测试图中的异步任务
    protected Object lock = new Object();
    //是否正在测试，避免异步测试任务的重复开启测试
    protected volatile boolean isTesting = false;

    //节点的具体测试方法，从input获取output
    protected abstract void testSelf();

    //节点复制
    public abstract <T extends TestNode> T copy(TestGraph testGraph, String testDateName);

    public TestNode(String name, String testDateName, TestGraph testGraph, PrepareType prepareType, String prepareValue, JSONObject input, GroupTestAssert groupTestAssert) {
        this.name = name;
        this.testDateName = testDateName;
        this.testGraph = testGraph;
        this.prepareType = prepareType;
        this.prepareValue = prepareValue;
        this.input = input;
        this.groupTestAssert = groupTestAssert;

        this.fileLogger = new FileLogger(testDateName + "/" + name);
    }

    //开启节点测试
    public void doTest() {
        //如果有别的异步流程已经开启测试则直接return
        if (isTesting) {
            return;
        }

        //标识开启测试
        isTesting = true;
        fileLogger.debug("to start");

        //如果不满足开启测试的条件，则该节点测试线程休眠
        //如果整个测试任务停止，则唤醒所有锁后直接return
        synchronized (lock) {
            while (!testGraph.isTesting() || !prepareType.isPrepared(prepareValue, this)) {
                if (!testGraph.isTesting()) {
                    fileLogger.debug("test is end , to return");
                    return;
                }

                try {
                    fileLogger.debug("is not prepared to wait");
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //开始节点的具体测试
            try {
                fileLogger.info("input = {}", input == null ? "null" : input.toJSONString());
                testSelf();
                fileLogger.info("output = {}", output == null ? "null" : output.toJSONString());
                //判断断言，测试是否成功
                isSuccess = groupTestAssert.asserts(this);
            } catch (Exception e) {
                fileLogger.info("to exception e = {}", e.getCause().toString());
                isSuccess = false;
            }
            fileLogger.info("success = {}", isSuccess);
        }

        //如果节点测试失败，则停止所有异步任务，并唤醒所有锁后直接return
        if (!isSuccess) {
            testGraph.stopTest();
            testGraph.getTempGraphData().getNodeHandler().getAllNode().forEach(node -> {
                synchronized (node.lock) {
                    node.lock.notifyAll();
                }
            });
            fileLogger.debug("to fail , stop testing and notify all");
            return;
        }

        //测试成功，寻找测试图中的下一节点集合并开启测试和唤醒所有一下节点集合的锁
        HierarchyNode<TestNode> hierarchy = testGraph.getTempGraphData().getPlusHandler().getHierarchy(name, 0, 1);
        Set<HierarchyNode<TestNode>> targets = hierarchy.getTargets();
        targets.forEach(targetTestNode -> {
            if (!testGraph.isTesting()) {
                fileLogger.debug("to end , not to start or notify the target");
                return;
            }
            testGraph.execute(() -> {
                synchronized (targetTestNode.getData().lock) {
                    targetTestNode.getData().fileLogger.debug("to start or notify from = {}", name);
                    try {
                        targetTestNode.getData().doTest();
                    } catch (Exception e) {
                        targetTestNode.getData().fileLogger.info("to doTest exception name = {}", name);
                    } finally {
                        targetTestNode.getData().lock.notifyAll();
                    }
                }
            });
        });
    }

}
