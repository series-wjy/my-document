package com.wjy.simple;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

class MaxAreaOfIslandWide {
    static int[] x = {1, 0, 0, -1};
    static int[] y = {0, 1, -1, 0};

    public static void main(String[] args) {
        int[][] grid = new int[][]{{1,1,0,0,0},{1,1,0,0,0},{0,0,0,1,1},{0,0,0,1,1}};

        System.out.println(maxAreaOfIslandWide(grid));
    }

    public static int maxAreaOfIslandWide(int[][] grid) {
        int maxArea = 0;
        Queue<int[]> que = new LinkedList<>();
        for(int i = 0; i < grid.length; i ++) {
            for(int j= 0; j < grid[0].length; j ++) {
                int tempArea = 0;
                int[] idx = new int[]{i, j};
                if(grid[i][j] == 1) {
                    que.offer(idx);
                    while(!que.isEmpty()) {
                        idx = que.poll();
                        tempArea += 1;
                        int r = idx[0];
                        int c = idx[1];
                        grid[r][c] = 2;
                        for(int k = 0; k < 4; k ++) {
                            int nr = r + x[k];
                            int nc = c + y[k];
                            if(nr >= 0 && nr < grid.length && nc >= 0 && nc < grid[0].length && grid[nr][nc] == 1) {
                                que.offer(new int[]{nr, nc});
                            }
                        }
                    }
                }
                maxArea = Math.max(maxArea, tempArea);
            }
        }
        return maxArea;
    }
}