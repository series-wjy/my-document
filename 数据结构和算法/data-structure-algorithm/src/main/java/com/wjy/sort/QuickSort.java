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

	// 快速排序
	public void quickSort(int[] arr, int s, int e) {
		// 递归终止条件
		if(s >= e) {
			return;
		}
		
		int mid = partition(arr, s, e);
		quickSort(arr, s, mid - 1);
		quickSort(arr, mid + 1, e);
	}
	
	// 分区方法，选定一个元素，将数组分为两段，并返回元素在数组中的位置
	private int partition(int[] arr, int s, int e) {
		// 大于pivot元素的第一个元素指针
		int i = s;
		// 遍历指针
		int j = s;
		int pivot = arr[e];
		for(; j < e; j++) {
			// 遍历元素并比较，小于pivot就和i位置的元素交换，并让i往后移动一个位置
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
