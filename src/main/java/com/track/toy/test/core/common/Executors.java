package com.track.toy.test.core.common;

import java.util.concurrent.ExecutorService;

//测试执行者
public class Executors {
    //是否正在测试
    public static volatile boolean isTesting;
    private static final ExecutorService EXECUTOR_SERVICE = java.util.concurrent.Executors.newFixedThreadPool(100);

    public static void execute(Runnable runnable) {
        EXECUTOR_SERVICE.execute(runnable);
    }

    //开始测试
    public static void startTest() {
        isTesting = true;
    }

    //关闭测试，停止所有线程池的加入
    public static void stopTest() {
        isTesting = false;
        EXECUTOR_SERVICE.shutdown();
    }
}
