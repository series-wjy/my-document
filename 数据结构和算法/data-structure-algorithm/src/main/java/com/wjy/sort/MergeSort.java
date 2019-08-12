package com.wjy.sort;

import java.util.Arrays;

public class MergeSort {

	public static void main(String[] args) {
		MergeSort obj = new MergeSort();
		int[] arr = {8,2,1,2,5,13,7,9,0,33,24};
		//int[] arr = {8,2,1};
		
		obj.mergeSort(arr, 0, arr.length - 1);
		System.out.println(Arrays.toString(arr));
	}
	
	public void mergeSort(int[] arr, int s, int e) {
		// �ݹ���ֹ����
		if(s >= e) {
			return;
		}
		
		int mid = s + (e - s) / 2;
		mergeSort(arr, s, mid);
		mergeSort(arr, mid + 1, e);
		
		// �ݹ鵽���ܻ��ֵ���С�±꣬�����±꽫�������ݺϲ�
		merge(arr, s, mid, e);
	}
	
	// �ϲ���������
	public void merge(int[] arr, int s, int mid, int e) {
		int i = s;
		int j = mid + 1;
		int k = 0;
		int[] tmp = new int[e - s + 1];
		// �ϲ���������{1,2}��{1,3,4}
		while(i <= mid && j <= e) {
			if(arr[i] < arr[j]) {
				tmp[k++] = arr[i++];
			} else {
				tmp[k++] = arr[j++];
			}
		}
		
		// ����ʣ�µ�����Ԫ��{3,4}
		int start = i, end = mid;
		if(j <= e) {
			start = j;
			end = e;
		}
		while(start <= end) {
			tmp[k++] = arr[start++];
		}
		// ���ź��������Ԫ���滻��ԭ�����Ӧλ��
		for(i = 0; i < k; i ++) {
			arr[s + i] = tmp[i];
		}
		
		System.out.println(Arrays.toString(arr));
	}
}
