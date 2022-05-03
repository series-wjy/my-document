package com.wjy.simple;

class BinarySearch {

    public static void main(String[] args) {
        System.out.println(search(new int[]{-1,0,3,5,9,12}, 9));
    }
    public static int search(int[] nums, int target) {
        return binarySearch(nums, target, 0, nums.length - 1);
    }

    private static int binarySearch(int[] nums, int target, int start, int end) {
        if(end == start) {
            if(nums[end] == target) {
                return start;
            } else {
                return -1;
            }
        } else {
            int mid = start + (end - start)/2;
            if(nums[mid] >= target) {
                return binarySearch(nums, target, start, mid);
            } else {
                return binarySearch(nums, target, mid + 1, end);
            }
        }
    }
}