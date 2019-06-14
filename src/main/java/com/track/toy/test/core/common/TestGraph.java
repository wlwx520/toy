package com.track.toy.test.core.common;

import com.track.toy.bean.CounterLock;
import com.track.toy.graph.Graph;
import com.track.toy.test.core.node.TestNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

@Data
@Slf4j
public class TestGraph {
    private static final String HEAD_NODE = "headNode";
    private String path;
    private String dataFolder;

    public TestGraph(String path, String dataFolder) {
        this.path = path;
        this.dataFolder = dataFolder;
    }

    //是否正在测试
    public volatile boolean isTesting;
    private final ExecutorService EXECUTOR_SERVICE = java.util.concurrent.Executors.newFixedThreadPool(100);
    private final CounterLock LOCK = new CounterLock();

    public void doTest(Graph<TestNode, Double, String, String> tempGraphData) {
        //开启测试
        startTest();

        //开启图的头节点测试，将自动进行节点探索并向下进行测试
        execute(() -> {
            tempGraphData.getNodeHandler().getNode(HEAD_NODE).doTest();
        });

        //主线程等待所有测试线程返回
        await();

        //停止测试
        stopTest();
    }

    public void execute(Runnable runnable) {
        LOCK.startThread();
        EXECUTOR_SERVICE.execute(() -> {
            runnable.run();
            LOCK.endThread();
        });
    }

    //开始测试
    public boolean startTest() {
        if (isTesting) {
            log.info("is already testing");
            return false;
        }
        isTesting = true;
        return true;
    }

    //关闭测试，停止所有线程池的加入
    public void stopTest() {
        isTesting = false;
    }

    public void await() {
        LOCK.await();
    }
}
