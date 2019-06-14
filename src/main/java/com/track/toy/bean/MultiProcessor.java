package com.track.toy.bean;

import java.util.LinkedList;
import java.util.List;

public class MultiProcessor<T> {
    private final int dealSize;
    private final int capacity;
    private final List<T> QUEUE;
    private final IMultiProcessor<T> processor;
    private volatile boolean isEnd = false;

    private static final CounterLock SELF_LOCK = new CounterLock();
    private static final Object QUEUE_LOCK = new Object();

    public MultiProcessor(int capacity, int dealSize, IMultiProcessor<T> processor) {
        this.capacity = capacity;
        this.dealSize = dealSize;
        this.processor = processor;
        this.QUEUE = new LinkedList<T>();

        process();
    }

    public void add(T t) {
        if (QUEUE.size() < capacity) {
            QUEUE.add(t);
        }
        QUEUE_LOCK.notifyAll();
    }

    private void process() {
        for (int i = 0; i < dealSize; i++) {
            SELF_LOCK.startThread();
            new Thread(() -> {
                while (!isEnd) {
                    T poll;
                    synchronized (QUEUE_LOCK) {
                        while (isEnd || QUEUE.isEmpty()) {
                            if (isEnd) {
                                SELF_LOCK.endThread();
                                return;
                            }

                            try {
                                QUEUE_LOCK.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        poll = get();
                    }

                    QUEUE_LOCK.notifyAll();

                    if (poll == null) {
                        continue;
                    }
                    processor.process(poll);
                }

                SELF_LOCK.endThread();
            }).start();
        }
    }

    public void stop() {
        isEnd = true;
        SELF_LOCK.await();
    }

    private T get() {
        if (QUEUE == null || QUEUE.isEmpty()) {
            return null;
        }
        return QUEUE.remove(0);
    }

    @FunctionalInterface
    public interface IMultiProcessor<T> {
        void process(T t);
    }
}
