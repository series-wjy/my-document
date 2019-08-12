package com.wjy.entity;
public class Node {
		private String val;
		private Node next;
		public Node(String val) {
			this.val = val;
		}
		
		public String getVal() {
			return this.val;
		}
		
		public Node getNext() {
			return this.next;
		}
		
		public boolean setNext(Node next) {
			this.next = next;
			return true;
		}
	}