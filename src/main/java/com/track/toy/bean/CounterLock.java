package com.track.toy.bean;

public class CounterLock {
	private Object lock = new Object();
	private int count;

	public void startThread() {
		synchronized (lock) {
			count++;
		}
	}

	public void endThread() {
		synchronized (lock) {
			count--;
			lock.notifyAll();
		}
	}

	public void await() {
		synchronized (lock) {
			while (count > 0) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
