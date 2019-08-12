package com.wjy.queue;

public class MyArrayQueue {
	private String[] tab;
	private int capacity;
	private int head;
	private int tail;
	
	public MyArrayQueue(int capacity) {
		this.tab = new String[capacity];
		this.capacity = capacity;
		this.head = 0;
		this.tail = 0;
	}
	
	//入队
	public boolean enqueue(String item) {
		if(tail == capacity) {
			if(head == 0) {
				return false;
			}
			if(head != 0) {
				for(int i = head; i < tail; i ++) {
					tab[i - head] = tab[i];
				}
			}
			tail = tail - head;
			head = 0;
		}
		
		tab[tail] = item;
		tail++;
		return true;
	}
	
	//出队
	public String dequeue() {
		if(head == tail ) {
			return null;
		}
		String item = tab[head];
		head++;
		return item;
	}
	
	public static void main(String[] args) {
		MyArrayQueue stack = new MyArrayQueue(3);
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
