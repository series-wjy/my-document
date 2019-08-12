package com.wjy.sort;

import java.util.Arrays;

public class InsertionSort {

	public static void main(String[] args) {
		int[] arr = {1,2,3,4,5,6};
		int length = arr.length;
		for(int i = 1; i < length; i++) {
			int tmp = arr[i];
			int j = i - 1;
			
			for(; j >= 0; --j) {
				if(arr[j] > tmp) {
					arr[j + 1] = arr[j];
				} else {
					break;
				}
			}
			
			arr[j + 1] = tmp;
		}
		
		System.out.println(Arrays.toString(arr));

	}

}
