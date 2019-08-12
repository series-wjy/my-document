package com.wjy.fibonacci;

public class Demo4 {
	public static void main(String[] args) {
		for (int i = 1; i <= 20; i++) {
			System.out.println(Demo4.f(i));
		}
	}

	static int f(int n) {
		if (n == 1)
			return 1;
		if (n == 2)
			return 2;
		return f(n - 1) + f(n - 2);
	}
}
