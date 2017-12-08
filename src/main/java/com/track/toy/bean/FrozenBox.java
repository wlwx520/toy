package com.track.toy.bean;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class FrozenBox<T> {
	private long frozenTime;
	private Set<T> datas;

	public FrozenBox(long frozenTime) {
		this.frozenTime = frozenTime;
		this.datas = Collections.synchronizedSet(new HashSet<>());
	}

	public void add(T data) {
		this.datas.add(data);
		new Timer().schedule(new MyTimerTask<>(this.datas, data), this.frozenTime);
	}

	public long getCount() {
		return this.datas.size();
	}

	public long getCount(Alive<T> alive) {
		return this.datas.stream().filter(item -> {
			return alive.alive(item);
		}).count();
	}

	private static class MyTimerTask<T> extends TimerTask {
		private Set<T> datas;
		private T data;

		private MyTimerTask(Set<T> datas, T data) {
			this.datas = datas;
			this.data = data;
		}

		@Override
		public void run() {
			this.datas.remove(data);
		} 
	}

	@FunctionalInterface
	public interface Alive<T> {
		public boolean alive(T t);
	}

}
