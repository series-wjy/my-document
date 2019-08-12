package com.wjy.queue;

public class CircularQueue {
	private String[] tab;
	private int capacity;
	private int head;
	private int tail;
	
	public CircularQueue(int capacity) {
		this.tab = new String[capacity];
		this.capacity = capacity;
		this.head = 0;
		this.tail = 0;
	}
	
	//入队
	public boolean enqueue(String item) {
		//队列满的情况
		if((tail + 1) % capacity == head) {
			return false;
		}
		
		tab[tail] = item;
		tail = (tail + 1) % capacity;
		return true;
	}
	
	//出队
	public String dequeue() {
		if(head == tail ) {
			return null;
		}
		String item = tab[head];
		head = (head + 1) % capacity;
		return item;
	}
	
	public static void main(String[] args) {
		CircularQueue stack = new CircularQueue(4);
		stack.enqueue("a");
		stack.enqueue("b");
		stack.enqueue("c");
		
		System.out.println(stack.dequeue());
		System.out.println(stack.dequeue());
		System.out.println(stack.dequeue());
		
		stack.enqueue("g");
		stack.enqueue("h");
		
		System.out.println(stack.dequeue());
		System.out.println(stack.dequeue());
		System.out.println(stack.dequeue());
	}
}
