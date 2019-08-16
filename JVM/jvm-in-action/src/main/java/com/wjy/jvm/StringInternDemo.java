package com.wjy.jvm;

public class StringInternDemo {
	public static void main(String[] args) {
	    String s = new String("1");
	    s.intern();
	    String s2 = "1";
	    System.out.println(s == s2);

	    String s1 = new String("11");
	    s1.intern();
	    String s11 = "11";
		System.out.println(s1 == s11);

	    String s3 = new String("1") + new String("1");
	    s3.intern();
	    String s4 = "11";
	    System.out.println(s3 == s4);
		System.out.println(s3 == s11);

		System.out.println(s4.getBytes());
		System.out.println(s11.getBytes());
	}
}