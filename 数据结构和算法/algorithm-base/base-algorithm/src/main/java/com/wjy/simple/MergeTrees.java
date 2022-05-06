package com.wjy.simple;

import javax.swing.tree.TreeNode;
import java.util.LinkedList;
import java.util.Queue;

class MergeTrees {
    public static void main(String[] args) {
        TreeNode tree1 = new TreeNode(1, null, null);
        tree1.left = new TreeNode(2, new TreeNode(3, null, null), null);
        tree1.right = null;

        TreeNode tree2 = new TreeNode(1, null, null);
        tree2.left = null;
        tree2.right = new TreeNode(2, null, new TreeNode(3, null, null));

        TreeNode treeNode = mergeTrees(tree1, tree2);
        System.out.println(treeNode);
    }

    public static TreeNode mergeTrees(TreeNode root1, TreeNode root2) {
        if(root1 == null) {
            return root2;
        }
        if(root2 == null) {
            return root1;
        }
        Queue<TreeNode[]> que = new LinkedList<>();
        TreeNode root = new TreeNode(root1.val + root2.val);
        que.offer(new TreeNode[]{root1, root2, root});
        while(!que.isEmpty()) {
            TreeNode[] node = que.poll();
            mergeNode(node[0], node[1], node[2], que);
        }
        return root;
    }

    private static void mergeNode(TreeNode node1, TreeNode node2, TreeNode parent, Queue que) {
        if (node1 == null && node2 == null) {
            return;
        }
        if (node1 != null && node2 == null) {
            parent.left = node1.left;
            parent.right = node1.right;
            if (parent.left != null) {
                que.offer(new TreeNode[]{node1.left, null, parent.left});
            }
            if (parent.right != null) {
                que.offer(new TreeNode[]{node1.right, null, parent.right});
            }
        } else if (node1 == null && node2 != null) {
            parent.left = node2.left;
            parent.right = node2.right;
            if (parent.left != null) {
                que.offer(new TreeNode[]{node2.left, null, parent.left});
                if (parent.right != null) {
                    que.offer(new TreeNode[]{node2.right, null, parent.right});
                }
            } else {
                parent.left = mergeChild(node1.left, node2.left);
                parent.right = mergeChild(node1.right, node2.right);
                if (parent.left != null) {
                    que.offer(new TreeNode[]{node1.left, node2.left, parent.left});
                }
                if (parent.right != null) {
                    que.offer(new TreeNode[]{node1.right, node2.right, parent.right});
                }
            }
        }
    }

    private static TreeNode mergeChild(TreeNode node1, TreeNode node2) {
        if(node1 != null && node2 != null) {
            return new TreeNode(node1.val + node2.val);
        }
        if(node1 == null && node2 != null) {
            return node2;
        }
        if(node1 != null && node2 == null) {
            return node1;
        }
        return null;
    }
    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }
}

