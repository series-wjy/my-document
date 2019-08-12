package com.wjy.queue;

import com.wjy.entity.Node;

public class MyListQueue {
	private Node head;
	private Node tail;
	private int capacity;
	private int count;
	
	public MyListQueue(int capacity) {
		this.capacity = capacity;
		this.count = 0;
		this.head = null;
		this.tail = null;
	}
	
	//入队
	public boolean enqueue(Node item) {
		if(count == capacity) {
			return false;
		}
		if(count == 0) {
			head = item;
			tail = item;
			count++;
			return true;
		}
		Node prev = tail;
		tail = item;
		prev.setNext(tail);
		count++;
		return true;
	}
	
	//出队
	public Node dequeue() {
		if(count == 0) {
			return null;
		}
		Node ret = head;
		Node next = head.getNext();
		head = next;
		count--;
		return ret;
	}
	
	public static void main(String[] args) {
		MyListQueue stack = new MyListQueue(3);
		stack.enqueue(new Node("a"));
		stack.enqueue(new Node("b"));
		stack.enqueue(new Node("c"));
		
		System.out.println(stack.dequeue().getVal());
		System.out.println(stack.dequeue().getVal());
		System.out.println(stack.dequeue().getVal());
		
		stack.enqueue(new Node("g"));
		stack.enqueue(new Node("h"));
		
		System.out.println(stack.dequeue().getVal());
		System.out.println(stack.dequeue().getVal());
		System.out.println(stack.dequeue().getVal());
	}
}
