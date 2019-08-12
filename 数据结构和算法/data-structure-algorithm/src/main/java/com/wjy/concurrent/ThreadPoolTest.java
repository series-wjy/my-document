package com.wjy.concurrent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class ThreadPoolTest {

	public static void main(String[] args) {
		ExecutorService es = new ThreadPoolExecutor(50, 500, 60L, TimeUnit.SECONDS,
				// ע��Ҫ�����н����
				new LinkedBlockingQueue<Runnable>(2000),
				// �������ҵ������ʵ�� ThreadFactory
				new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						return new Thread(r, "echo-" + r.hashCode());
					}
				},
				// �������ҵ������ʵ�� RejectedExecutionHandler
				new ThreadPoolExecutor.CallerRunsPolicy());

		List languages = Arrays.asList("Java", "Scala", "C++", "Haskell", "Lisp");

		System.out.println("Languages which starts with J :");
		filter(languages, (str) -> ((String) str).startsWith("J"));

		System.out.println("Languages which ends with a ");
		filter(languages, (str) -> ((String) str).endsWith("a"));

		System.out.println("Print all languages :");
		filter(languages, (str) -> true);

		System.out.println("Print no language : ");
		filter(languages, (str) -> false);

		System.out.println("Print language whose length greater than 4:");
		filter(languages, (str) -> ((String) str).length() > 4);
	}

	public static void filter(List names, Predicate condition) {
		 names.stream().filter((name) -> (condition.test(name)))
	        .forEach((name) -> {System.out.println(name + " ");
	    });
	}

}
