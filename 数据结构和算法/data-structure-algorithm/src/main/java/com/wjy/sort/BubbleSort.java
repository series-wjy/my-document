package com.wjy.sort;

import java.util.Arrays;

public class BubbleSort {

	public static void main(String[] args) {
		int[] arr = {3,5,4,1,2,6};
		int length = arr.length;
		boolean exchange = false;
		for(int i = 0; i < length; i++) {
			for(int j = 0; j < length - i - 1; j++) {
				if(arr[j] > arr[j + 1]) {
					int tmp = arr[j + 1];
					arr[j + 1] = arr[j];
					arr[j] = tmp;
					exchange = true;
				}
			}
			if(!exchange) {
				break;
			}
		}
		
		System.out.println(Arrays.toString(arr));
	}
}
