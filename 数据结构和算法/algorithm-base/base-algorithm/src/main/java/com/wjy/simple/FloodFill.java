package com.wjy.simple;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

class FloodFill {
    static int[] x = {1, 0, 0, -1};
    static int[] y = {0, 1, -1, 0};

    public static void main(String[] args) {
        int[][] ints = floodFillDeep(new int[][]{{1, 1, 1}, {1, 1, 0}, {1, 0, 1}}, 1, 1, 2);
        System.out.println(Arrays.toString(ints));
        int[][] ints1 = floodFillDeep(new int[][]{{0, 0, 0}, {0, 1, 0}}, 1, 1, 2);
    }

    public static int[][] floodFillDeep(int[][] image, int sr, int sc, int newColor) {
        int currColor = image[sr][sc];
        if(currColor == newColor) {
            return image;
        }
        image[sr][sc] = newColor;
        setColor(image, sr, sc, currColor, newColor);
        return image;
    }

    private static void setColor(int[][] image, int sr, int sc, int oldColor, int newColor) {
        int rowLength = image.length;
        int colLength = image[0].length;
        for(int i = 0; i < 4; i ++) {
            int nr = sr + x[i];
            int nc = sc + y[i];
            if(nc >= 0 && nr >= 0 && nr < rowLength && nc < colLength && image[nr][nc] == oldColor) {
                image[nr][nc] = newColor;
                setColor(image, nr, nc, oldColor, newColor);
            }
        }
    }

    public int[][] floodFillWidth(int[][] image, int sr, int sc, int newColor) {
        int currColor = image[sr][sc];
        if(currColor == newColor) {
            return image;
        }
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{sr, sc});
        int rowLength = image.length;
        int colLength = image[0].length;
        image[sr][sc] = newColor;
        while(!queue.isEmpty()) {
            int[] node = queue.poll();
            int r = node[0], c = node[1];
            for(int i = 0; i < 4; i ++) {
                int nr = r + x[i];
                int nc = c + y[i];
                if(nc >= 0 && nr >= 0 && nr < rowLength && nc < colLength && image[nr][nc] == currColor) {
                    queue.offer(new int[]{nr, nc});
                    image[nr][nc] = newColor;
                }
            }
        }
        return image;
    }
}