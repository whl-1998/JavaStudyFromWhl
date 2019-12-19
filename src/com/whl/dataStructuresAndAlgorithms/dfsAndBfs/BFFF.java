package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

import java.util.*;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public class BFFF {
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        if (beginWord.equals(endWord)) return 1;
        Queue<String> queue = new LinkedList<>();
        queue.offer(beginWord);
        Set<String> visited = new HashSet<>();
        visited.add(beginWord);
        Set<String> wordListSet = new HashSet<>(wordList);
        int level = 1;
        while (!queue.isEmpty()) {
            int size = queue.size();
            while (size-- > 0) {
                String temp = queue.poll();
                if (temp.equals(endWord)) return level;
                char[] arr = temp.toCharArray();
                for (int i = 0; i < arr.length; i++) {
                    char old = arr[i];
                    for (char j = 'a'; j <= 'z'; j++) {
                        arr[i] = j;
                        String next = String.valueOf(arr);
                        if (wordListSet.contains(next) && visited.add(next)) {
                            queue.offer(next);
                        }
                    }
                    arr[i] = old;
                }
            }
            level++;
        }
        return 0;
    }

    public static void main(String[] args) {
        BFFF bf = new BFFF();
        List<String> list = Arrays.asList("hot","dot","dog","lot","log","cog");
        bf.ladderLength("hit", "cog", list);
    }
}
