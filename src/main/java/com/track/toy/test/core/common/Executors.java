package com.track.toy.test.core.common;

import java.util.concurrent.ExecutorService;

public class Executors {
    private static final ExecutorService EXECUTOR_SERVICE = java.util.concurrent.Executors.newFixedThreadPool(100);

    public static void execute(Runnable runnable) {
        EXECUTOR_SERVICE.execute(runnable);
    }
}
