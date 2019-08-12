package com.wjy.sort;

import java.util.Arrays;

public class SelectionSort {

	public static void main(String[] args) {
		int[] arr = {3,5,4,1,2,6};
		int length = arr.length;
		for(int i = 0; i < length; i++) {
			int tmp = arr[i];
			int j = i;
			int idx = i;
			for(; j < length; j++) {
				if(arr[j] < tmp) {
					tmp = arr[j];
					idx = j;
				}
			}
			arr[idx] = arr[i];
			arr[i] = tmp;
		}
		
		System.out.println(Arrays.toString(arr));
	}
}
