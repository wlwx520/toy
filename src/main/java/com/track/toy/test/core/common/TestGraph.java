package com.track.toy.test.core.common;

import com.track.toy.bean.CounterLock;
import com.track.toy.graph.Graph;
import com.track.toy.test.core.factory.LoggerFactory;
import com.track.toy.test.core.node.TestNode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestGraph {
    public static final String HEAD_NODE = "headNode";
    public static final String TAIL_NODE = "tailNode";

    @Getter
    private String path;
    @Getter
    private String dataFolder;

    public TestGraph(String path, String dataFolder) {
        this.path = path;
        this.dataFolder = dataFolder;
    }

    //是否正在测试
    private boolean isTesting = false;
    //测试锁
    private Object testingLock = new Object();

    //测试图数据
    @Getter
    public Graph<TestNode, Double, String, String> tempGraphData;
    //测试线程池
    private final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(100);
    //计数锁
    private final CounterLock LOCK = new CounterLock();

    //加载数据
    public void loadData(Graph<TestNode, Double, String, String> tempGraphData) {
        this.tempGraphData = tempGraphData;
    }

    public TestGraph copy() {
        return new TestGraph(path, dataFolder);
    }

    //执行测试
    public void doTest() {
        if (tempGraphData == null) {
            LoggerFactory.systemLog("测试数据未加载");
            return;
        }

        //开启测试
        startTest();

        //开启图的头节点测试，将自动进行节点探索并向下进行测试
        execute(() -> {
            tempGraphData.getNodeHandler().getNode(HEAD_NODE).doTest();
        });

        //主线程等待所有测试线程返回，等待计数锁归0
        await();

        //停止测试
        stopTest();
    }

    public void execute(Runnable runnable) {
        //先计数锁加1
        LOCK.startThread();
        EXECUTOR_SERVICE.execute(() -> {
            //执行任务
            runnable.run();
            //任务完成后计数锁减1
            LOCK.endThread();
        });
    }

    //开始测试
    public void startTest() {
        synchronized (testingLock) {
            isTesting = true;
        }
    }

    //关闭测试，停止所有线程池的加入
    public void stopTest() {
        synchronized (testingLock) {
            isTesting = false;
        }
    }

    public void await() {
        LOCK.await();
    }

    public boolean isTesting() {
        synchronized (testingLock) {
            return isTesting;
        }
    }
}
