package com.wjy.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolDeadLockTest {

	public static void main(String[] args) throws InterruptedException {
		// L1��L2 �׶ι��õ��̳߳�
		ExecutorService es = Executors.newFixedThreadPool(2);
		// L1 �׶εı���
		CountDownLatch l1 = new CountDownLatch(2);
		for (int i = 0; i < 2; i++) {
			System.out.println("L1");
			// ִ�� L1 �׶�����
			es.execute(() -> {
				// L2 �׶εı���
				CountDownLatch l2 = new CountDownLatch(2);
				// ִ�� L2 �׶�������
				for (int j = 0; j < 2; j++) {
					es.execute(() -> {
						System.out.println("L2");
						l2.countDown();
					});
				}
				// �ȴ� L2 �׶�����ִ����
				try {
					l2.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				l1.countDown();
			});
		}
		// ���� L1 �׶�����ִ����
		l1.await();
		System.out.println("end");
		es.shutdown();

		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.submit(() -> {
			try {
				String qq = pool.submit(() -> "QQ").get();
				System.out.println(qq);
			} catch (Exception e) {
			}
		});
	}
}
