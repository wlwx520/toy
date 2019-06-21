package com.track.toy.bean;

import java.util.LinkedList;
import java.util.List;

public class MultiProcessor<T> {
    private final int dealSize;
    private final int capacity;
    private final List<T> queue;
    private final IMultiProcessor<T> processor;
    private volatile boolean isEnd = false;

    private  final CounterLock SELF_LOCK = new CounterLock();
    private  final Object QUEUE_LOCK = new Object();

    public MultiProcessor(int capacity, int dealSize, IMultiProcessor<T> processor) {
        this.capacity = capacity;
        this.dealSize = dealSize;
        this.processor = processor;
        this.queue = new LinkedList<T>();

        process();
    }

    public void add(T t) {
        if (isEnd) {
            throw new RuntimeException("multi processor is end");
        }

        synchronized (QUEUE_LOCK) {
            if (queue.size() < capacity) {
                queue.add(t);
            }
            QUEUE_LOCK.notifyAll();
        }
    }

    private void process() {
        for (int i = 0; i < dealSize; i++) {
            SELF_LOCK.startThread();
            new Thread(() -> {
                while (!isEnd) {
                    T poll;
                    synchronized (QUEUE_LOCK) {
                        a:
                        while (true) {
                            if (isEnd && queue.isEmpty()) {
                                SELF_LOCK.endThread();
                                QUEUE_LOCK.notifyAll();
                                return;
                            }

                            if (!isEnd && queue.isEmpty()) {
                                try {
                                    QUEUE_LOCK.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            poll = get();
                            QUEUE_LOCK.notifyAll();
                            break a;
                        }
                    }

                    if (poll != null) {
                        processor.process(poll);
                    }

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
        if (queue == null || queue.isEmpty()) {
            return null;
        }
        return queue.remove(0);
    }

    @FunctionalInterface
    public interface IMultiProcessor<T> {
        void process(T t);
    }
}
