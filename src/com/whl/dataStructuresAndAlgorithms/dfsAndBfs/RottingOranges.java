package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author whl
 * @version V1.0
 * @Title: 994. 腐烂的橘子
 * @Description:
 */
public class RottingOranges {
    /**
     * 1. BFS
     * 思路：
     * 1. 准备工作：先遍历二维矩阵, 将所有腐烂的橙子放入队列, 并计算新鲜橙子的个数
     * 2. 腐烂过程：每当新鲜橙子个数不为0时, 执行一次腐烂过程, count++, 并从队列中取出所有腐烂橙子的下标, 让其4个方向的橙子腐烂
     * 3. 结束判断：当新鲜橙子个数为0时, 则表示全部腐烂, 返回count
     * 时间复杂度：O(m*n)
     * 空间复杂度：O(m*n)
     * 执行用时：4ms
     * @param grid
     * @return
     */
    public int orangesRotting(int[][] grid) {
        if (grid == null || grid.length == 0) return -1;
        int rows = grid.length;
        int cols = grid[0].length;
        Queue<int[]> queue = new LinkedList<>();
        int count_fresh = 0;
        //1. 将所有腐烂橙子index放入队列, 同时计算新鲜橙子数量
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 2) {
                    queue.offer(new int[]{i, j});
                } else if (grid[i][j] == 1) {
                    count_fresh++;
                }
            }
        }
        if (count_fresh == 0) return 0;
        int count = 0;
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        //bfs执行腐烂过程
        while (!queue.isEmpty()) {
            //每当新鲜橙子个数不为0时, 执行一次腐烂过程, count++,
            if (count_fresh > 0) {
                count++;
                int size = queue.size();
                for (int i = 0; i < size; i++) {
                    //从队列中取出所有腐烂橙子的下标, 让其4个方向的橙子腐烂
                    int[] point = queue.poll();
                    for (int[] dir : dirs) {
                        int x = point[0] + dir[0];
                        int y = point[1] + dir[1];
                        //若x,y越界, 或者已经被腐烂, 或者没有橙子, 那么continue
                        if (x < 0 || x >= rows || y < 0 || y >= cols || grid[x][y] == 2 || grid[x][y] == 0) {
                            continue;
                        }
                        grid[x][y] = 2;
                        //将新腐烂的橙子放入队列
                        queue.offer(new int[]{x, y});
                        count_fresh--;
                    }
                }
            } else {
                break;
            }
        }
        return count_fresh == 0 ? count : -1;
    }

    public static void main(String[] args) {
        RottingOranges ro = new RottingOranges();
        ro.orangesRotting(new int[][]{
                {2,1,1},
                {1,1,1},
                {0,1,1}
        });
    }
}
