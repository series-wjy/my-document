package com.wjy.fibonacci;

public class Demo3 {

	public static void main(String[] args) {
		for (int i = 1; i <= 20; i++) {
			System.out.println(Demo3.f(i));
		}
	}

	static int f(int n) {
		if (n == 1)
			return 1;
		if (n == 2)
			return 2;

		int ret = 0;
		int pre = 2;
		int prepre = 1;
		for (int i = 3; i <= n; ++i) {
			ret = pre + prepre;
			prepre = pre;
			pre = ret;
		}
		return ret;
	}

}
