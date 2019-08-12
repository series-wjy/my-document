package com.wjy.stack;

import com.wjy.entity.Node;

public class MyListStack {
	private Node head;
	private int n;
	private int count;
	
	public MyListStack(int capacity) {
		this.head = null;
		this.n = 0;
		this.count = capacity;
	}
	
	//Ñ¹Õ»
	public boolean push(Node item) {
		if(n == count) {
			return false;
		}
		Node tmp = this.head;
		item.setNext(tmp);
		this.head = item;
		n++;
		return true;
	}
	
	//³öÕ»
	public Node pop() {
		if(n == 0) {
			return new Node("");
		}
		Node next = head.getNext();
		Node item = head;
		head = next;
		n--;
		return item;
	}
	
	public static void main(String[] args) {
		MyListStack stack = new MyListStack(5);
		stack.push(new Node("a"));
		stack.push(new Node("b"));
		stack.push(new Node("c"));
		
		System.out.println(stack.pop().getVal());
		System.out.println(stack.pop().getVal());
		System.out.println(stack.pop().getVal());
		System.out.println(stack.pop().getVal());
		
		stack.push(new Node("e"));
		stack.push(new Node("f"));
		
		System.out.println(stack.pop().getVal());
		System.out.println(stack.pop().getVal());
		System.out.println(stack.pop().getVal());
	}
}
