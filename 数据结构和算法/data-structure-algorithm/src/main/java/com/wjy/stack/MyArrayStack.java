package com.wjy.stack;

public class MyArrayStack {
	private String[] tab;
	private int n;
	private int count;
	
	public MyArrayStack(int capacity) {
		this.tab = new String[capacity];
		this.count = capacity;
		this.n = 0;
	}
	
	//Ñ¹Õ»
	public boolean push(String o) {
		if(n == count) {
			return false;
		}
		tab[n++] = o;
		return true;
	}
	
	//³öÕ»
	public String pop() {
		if(n == 0) {
			return null;
		}
		String o = tab[--n];
		return o;
	}
	
	public static void main(String[] args) {
		MyArrayStack stack = new MyArrayStack(5);
		stack.push("a");
		stack.push("b");
		stack.push("c");
		
		System.out.println(stack.pop());
		System.out.println(stack.pop());
		System.out.println(stack.pop());
		System.out.println(stack.pop());
		
		stack.push("e");
		stack.push("f");
		
		System.out.println(stack.pop());
		System.out.println(stack.pop());
		System.out.println(stack.pop());
	}
}
