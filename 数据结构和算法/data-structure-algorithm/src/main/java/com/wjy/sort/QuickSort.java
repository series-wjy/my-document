package com.wjy.sort;

import java.util.Arrays;

public class QuickSort {

	public static void main(String[] args) {
		QuickSort obj = new QuickSort();
		
		int[] arr = {8,2,1,2,5,13,7,9,0,33,24};
		//int[] arr = {8,2,1};
		
		obj.quickSort(arr, 0, arr.length - 1);
		System.out.println(Arrays.toString(arr));
	}

	// ��������
	public void quickSort(int[] arr, int s, int e) {
		// �ݹ���ֹ����
		if(s >= e) {
			return;
		}
		
		int mid = partition(arr, s, e);
		quickSort(arr, s, mid - 1);
		quickSort(arr, mid + 1, e);
	}
	
	// ����������ѡ��һ��Ԫ�أ��������Ϊ���Σ�������Ԫ���������е�λ��
	private int partition(int[] arr, int s, int e) {
		// ����pivotԪ�صĵ�һ��Ԫ��ָ��
		int i = s;
		// ����ָ��
		int j = s;
		int pivot = arr[e];
		for(; j < e; j++) {
			// ����Ԫ�ز��Ƚϣ�С��pivot�ͺ�iλ�õ�Ԫ�ؽ���������i�����ƶ�һ��λ��
			if(arr[j] < pivot) {
				int tmp = arr[i];
				arr[i++] = arr[j];
				arr[j] = tmp;
			}
		}
		int tmp = arr[i];
		arr[i] = pivot;
		arr[e] = tmp;
		return i;
	}
}
