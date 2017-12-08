  package com.track.toy.bean;

public class ConditionQueue<E> {
	private int capacity;
	private int current;

	private Node<E> head;
	private Node<E> tail;

	private Condition<E> enQueueCondition;
	private Condition<E> deQueueCondition;

	private Object lock;

	public ConditionQueue() throws Exception {
		this(Integer.MAX_VALUE, null, null);
	}

	public ConditionQueue(int capacity) throws Exception {
		this(capacity, null, null);
	}

	public ConditionQueue(int capacity, Condition<E> enQueueCondition, Condition<E> deQueueCondition) {
		if (capacity <= 0) {
			capacity = Integer.MAX_VALUE;
		}
		this.capacity = capacity;
		this.current = 0;
		this.lock = new Object();
		this.enQueueCondition = enQueueCondition == null ? defaultCondition : enQueueCondition;
		this.deQueueCondition = deQueueCondition == null ? defaultCondition : deQueueCondition;

	}

	public void change() {
		synchronized (lock) {
			lock.notifyAll();
		}
	}

	public void put(E element) throws InterruptedException {
		synchronized (lock) {
			while (isFull() || !enQueueCondition.judge(element)) {
				lock.wait();
			}
			enqueue(element);
			lock.notifyAll();
		}
	}

	public E peek() throws InterruptedException {
		synchronized (lock) {
			while (isEmpty() || !deQueueCondition.judge(head.item)) {
				lock.wait();
			}
			E dequeue = dequeue();
			lock.notifyAll();
			return dequeue;
		}
	}

	private void enqueue(E element) {
		Node<E> node = new Node<>(element);
		if (head == null) {
			tail = head = node;
		} else {
			tail = tail.next = node;
		}
		current++;
	}

	private E dequeue() {
		Node<E> tmp = head;
		head = head.next;
		current--;
		return tmp.item;
	}

	private boolean isFull() {
		return capacity == current;
	}

	private boolean isEmpty() {
		return current == 0;
	}

	private static class Node<E> {
		E item;
		Node<E> next;

		Node(E element) {
			item = element;
		}
	}

	@FunctionalInterface
	public static interface Condition<E> {
		public boolean judge(E element);
	}

	private Condition<E> defaultCondition = (element) -> {
		return true;
	};

}
