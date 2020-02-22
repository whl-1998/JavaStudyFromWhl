package com.whl.dataStructuresAndAlgorithms.specialDataStructures;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public class Trie {
    private class TrieNode {
        private TrieNode[] next = new TrieNode[26];
        private boolean isEnd;//标识根结点到某个结点是否能构成一个单词

        public TrieNode() {
        }
    }

    private TrieNode root;

    public Trie() {
        this.root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode node = this.root;
        for (char c : word.toCharArray()) {
            if (node.next[c - 'a'] == null) node.next[c - 'a'] = new TrieNode();
            node = node.next[c - 'a'];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            if (node.next[c - 'a'] == null) return false;
            node = node.next[c - 'a'];
        }
        return node.isEnd;
    }

    public boolean startsWith(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            if (node.next[c - 'a'] == null) return false;
            node = node.next[c - 'a'];
        }
        return true;
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.insert("hello");
        trie.search("hello");
    }
}
