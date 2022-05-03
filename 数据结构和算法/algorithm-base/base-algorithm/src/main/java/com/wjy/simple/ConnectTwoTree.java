package com.wjy.simple;

public class ConnectTwoTree {
    public static void main(String[] args) {
        Node node = new Node(1);
        node.left = new Node(2,
                new Node(4, new Node(8), new Node(9), null),
                new Node(5, new Node(10), new Node(11), null),
                null);
        node.right = new Node(3,
                new Node(6, new Node(12), new Node(13), null),
                new Node(7, new Node(14), new Node(15), null),
                null);

        ConnectTwoTree s = new ConnectTwoTree();
        s.connect(node);
    }

    public Node connect(Node root) {
        if(root == null) {
            return root;
        }
        connect(root.left, root.right);
        return root;
    }
    private void connect(Node left, Node right) {
        if(left == null) {
            return;
        }
        left.next = right;
        System.out.println("left:" + left.val);
        connect(left.left, left.right);
        System.out.println("right:" + right.val);
        connect(right.left, right.right);
//        System.out.println("**left:" + left);
//        System.out.println("**right:" + right);
        System.out.println("left.right:" + left.right);
        System.out.println("right.left:" + right.left);
        connect(left.right, right.left);
    }

    static class Node {
        public int val;
        public Node left;
        public Node right;
        public Node next;

        public Node() {}

        public Node(int _val) {
            val = _val;
        }

        public Node(int _val, Node _left, Node _right, Node _next) {
            val = _val;
            left = _left;
            right = _right;
            next = _next;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Node{");
            sb.append("val=").append(val);
            sb.append(", left=").append(left);
            sb.append(", right=").append(right);
            sb.append(", next=").append(next);
            sb.append('}');
            return sb.toString();
        }
    };
}