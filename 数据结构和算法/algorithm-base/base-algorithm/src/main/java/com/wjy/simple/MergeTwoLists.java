package com.wjy.simple;

/**
 * Definition for singly-linked list.
 */
class MergeTwoLists {

    public static void main(String[] args) {
        ListNode list1 = new ListNode(1);
        ListNode list12 = new ListNode(2);
        ListNode list13 = new ListNode(5);
        ListNode list14 = new ListNode(7);
        list1.next = list12;
        list12.next = list13;
        list13.next = list14;

        ListNode list2 = new ListNode(3);
        ListNode list22 = new ListNode(5);
        ListNode list23 = new ListNode(6);
        ListNode list24 = new ListNode(9);
        list2.next = list22;
        list22.next = list23;
        list23.next = list24;
        System.out.println(mergeTwoLists(list1, list2));
    }

    public static ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode head = new ListNode(-1);
        ListNode next = head;
        while(list1 != null && list2 != null) {
            if(list1.val >= list2.val) {
                next.next = list2;
                list2 = list2.next;
            } else {
                next.next = list1;
                list1 = list1.next;
            }
            next = next.next;
        }
        next.next = list1 == null ? list2 : list1;

        return head.next;
    }


    public static class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("ListNode{");
            sb.append("val=").append(val);
            sb.append(", next=").append(next);
            sb.append('}');
            return sb.toString();
        }
    }
}