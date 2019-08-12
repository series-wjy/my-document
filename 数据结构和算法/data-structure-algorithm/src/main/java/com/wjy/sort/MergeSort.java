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
		// 递归终止条件
		if(s >= e) {
			return;
		}
		
		int mid = s + (e - s) / 2;
		mergeSort(arr, s, mid);
		mergeSort(arr, mid + 1, e);
		
		// 递归到不能划分的最小下标，根据下标将分组数据合并
		merge(arr, s, mid, e);
	}
	
	// 合并分组数据
	public void merge(int[] arr, int s, int mid, int e) {
		int i = s;
		int j = mid + 1;
		int k = 0;
		int[] tmp = new int[e - s + 1];
		// 合并两段数组{1,2}，{1,3,4}
		while(i <= mid && j <= e) {
			if(arr[i] < arr[j]) {
				tmp[k++] = arr[i++];
			} else {
				tmp[k++] = arr[j++];
			}
		}
		
		// 处理剩下的数组元素{3,4}
		int start = i, end = mid;
		if(j <= e) {
			start = j;
			end = e;
		}
		while(start <= end) {
			tmp[k++] = arr[start++];
		}
		// 将排好序的数组元素替换到原数组对应位置
		for(i = 0; i < k; i ++) {
			arr[s + i] = tmp[i];
		}
		
		System.out.println(Arrays.toString(arr));
	}
}
