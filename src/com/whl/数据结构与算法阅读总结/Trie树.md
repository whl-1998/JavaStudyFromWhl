###一、 Trie树概念

Trie树，也称为 “字典树” ，专门用于处理字符串匹配的数据结构。例如在搜索引擎中，只输入前缀词就可以根据热度从上至下显示出候选词，这个功能就是采用了字典树 + 优先队列的数据结构实现的。

假设我们有6个字符串，分别是：how、hi、her、hello、so、see，我们希望在这个字符串集合中多次查找某个字符串是否存在。最简单的一个思路就是将这6个字符串放置在数组中，然后遍历数组中的字符串，并调用字符串匹配算法判断数组中是否存在我们查找的字符串。那么有没有更高效的算法呢？此时，就可以使用Trie树解决问题。

我们可以对这6个字符串组织成Trie树的结构，之后每次查找都在Trie树中进行匹配查找。Trie树的结构如下所示：

![img](https://static001.geekbang.org/resource/image/28/32/280fbc0bfdef8380fcb632af39e84b32.jpg)

Trie树的每个结点都表示一个字符串中的字符，从根结点到红色结点的路径则表示一个字符串单词。当我们在Trie树中检索某个字符串时，例如检索字符串“her”，只需要从根结点开始，依次遍历“h，e，r” 三个结点，如果能成功遍历，则说明字符串“her”存在。


### 二、Trie树实现

Trie树是一个多叉树，采用一个数组作为结点，具体有多少个分叉取决于字符串的字符范围。假设我们的字符串只有从 a - z 这26个小写字母，那么结点数组下标为0的位置就是存储指向子结点a的指针，以此类推，下标为25的位置就是存储指向子节点z的指针。如果某个字符的子结点不存在，就在对应字符的下标位置存储null。

针对Trie树的实现，LeetCode中也有相应的例题：https://leetcode-cn.com/problems/implement-trie-prefix-tree/

对应代码实现如下：

```java
//解法1：多叉树法
class Trie {
    class TrieNode {
        public TrieNode[] next = new TrieNode[26];
        public boolean isEnd;//标识根结点到某个结点是否能构成一个单词
        
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
}
```

从Trie树的实现上看，构建Trie树的过程需要扫描需要存入Trie树中的所有的字符串，时间复杂度是O(n * k)，其中n = 字符串的个数，k = 单个字符串的平均长度。而检索字符串的操作时间复杂度则只需要O(k)，非常的高效。

虽然Trie树检索字符串的性能非常高，但是缺点也十分明显，Trie树的内存消耗是非常大的。字符串中仅仅包含了 a-z 这26个字符，每个结点就需要存储长度为26的数组了，并且每个结点还需要存储一个8字节的指针。而且，即便一个结点只有很少的子结点，我们也要维护一个长度为26的数组。（因为数组必须提前声明大小，获取连续的内存空间）

而正常情况下，字符串中是不仅仅只包含小写字母的，那么需要存储的空间就非常多了。也就是说，在某些情况下，Trie树不一定会节省内存空间，并且在重复前缀并不多的情况下，Trie树不但不能节省内存，还会浪费更多的内存空间。



### 三、Trie树与散列表、红黑树的比较

字符串匹配问题本质上就是数据的查找问题，而在一组字符串集合中查找某个字符串的操作，通过散列表和红黑树也能够实现。

并且Trie树的缺点也十分明显：

1. 字符串中包含的字符集范围不能太大，不然存储空间就会浪费很多。
2. 要求字符串的前缀重合比较多，不然空间消耗会大很多。
3. 如果要使用Trie树，就必须手工实现它，在工程中一般不建议自己手写。

综合下来，针对一组字符串中查找某个特定字符串的问题，在工程中更多采用的是散列表或红黑树。而Trie树更多的则是应用在匹配前缀字符串上，也就是搜索引擎中输入某个单词前缀，会有自动补全单词的功能。毕竟搜索引擎中的字符，必然字符前缀的重合是非常多的，除此以外，相比起内存的消耗也更要求检索前缀词这个操作的高效性。