### 如何理解KISS原则

KISS，Keep it Simple and Straightforward，尽量保持简单。我们知道代码的可读性以及可维护性是衡量代码质量的重要标准，而KISS原则就是保持代码可读性和可维护性的重要手段。那么什么样的代码才是简单直接的呢？我们可以通过下面这个例子了解：

下面三段代码可以实现同样的功能，检查输入的ipAddress是否合法：

```java
// 第一种实现方式: 使用正则表达式
public boolean isValidIpAddressV1(String ipAddress) {
    if (StringUtils.isBlank(ipAddress)) return false;
    String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
        + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
        + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
        + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
    return ipAddress.matches(regex);
}

// 第二种实现方式: 使用现成的工具类
public boolean isValidIpAddressV2(String ipAddress) {
    if (StringUtils.isBlank(ipAddress)) return false;
    String[] ipUnits = StringUtils.split(ipAddress, '.');
    if (ipUnits.length != 4) {
        return false;
    }
    for (int i = 0; i < 4; ++i) {
        int ipUnitIntValue;
        try {
            ipUnitIntValue = Integer.parseInt(ipUnits[i]);
        } catch (NumberFormatException e) {
            return false;
        }
        if (ipUnitIntValue < 0 || ipUnitIntValue > 255) {
            return false;
        }
        if (i == 0 && ipUnitIntValue == 0) {
            return false;
        }
    }
    return true;
}

// 第三种实现方式: 不使用任何工具类
public boolean isValidIpAddressV3(String ipAddress) {
    char[] ipChars = ipAddress.toCharArray();
    int length = ipChars.length;
    int ipUnitIntValue = -1;
    boolean isFirstUnit = true;
    int unitsCount = 0;
    for (int i = 0; i < length; ++i) {
        char c = ipChars[i];
        if (c == '.') {
            if (ipUnitIntValue < 0 || ipUnitIntValue > 255) return false;
            if (isFirstUnit && ipUnitIntValue == 0) return false;
            if (isFirstUnit) isFirstUnit = false;
            ipUnitIntValue = -1;
            unitsCount++;
            continue;
        }
        if (c < '0' || c > '9') {
            return false;
        }
        if (ipUnitIntValue == -1) ipUnitIntValue = 0;
        ipUnitIntValue = ipUnitIntValue * 10 + (c - '0');
    }
    if (ipUnitIntValue < 0 || ipUnitIntValue > 255) return false;
    if (unitsCount != 3) return false;
    return true;
}
```

能够发现，第一种实现方式虽然代码行数最少，但是使用了正则表达式，如果不了解业务背景根本就不明白这段代码是干嘛的，因此并不符合KISS原则。第二种实现采用了StringUtils、Integer类提供的现成的工具函数处理IpAddress，代码相比起第三种更加简单易读。虽然因为工具类需要考虑和处理更多细节，第三种性能比起第二种稍微高一些，但是除非isValidIpAddress()是影响性能瓶颈的代码，否则还是以可读性和简单为主。



### 代码逻辑复杂就违背了KISS原则吗？

```java
// KMP algorithm: a, b分别是主串和模式串；n, m分别是主串和模式串的长度。
public static int kmp(char[] a, int n, char[] b, int m) {
    int[] next = getNexts(b, m);
    int j = 0;
    for (int i = 0; i < n; ++i) {
        while (j > 0 && a[i] != b[j]) { // 一直找到a[i]和b[j]
            j = next[j - 1] + 1;
        }
        if (a[i] == b[j]) {
            ++j;
        }
        if (j == m) { // 找到匹配模式串的了
            return i - m + 1;
        }
    }
    return -1;
}

// b表示模式串，m表示模式串的长度
private static int[] getNexts(char[] b, int m) {
    int[] next = new int[m];
    next[0] = -1;
    int k = -1;
    for (int i = 1; i < m; ++i) {
        while (k != -1 && b[k + 1] != b[i]) {
            k = next[k];
        }
        if (b[k + 1] == b[i]) {
            ++k;
        }
        next[i] = k;
    }
    return next;
}
```

上述代码是KMP字符串匹配算法的实现，虽然它逻辑复杂、实现难度大、可读性差，但是并不违反KISS原则。因为KMP算法以快速高效著称，当我们需要处理长文本字符串匹配问题时，或者字符串匹配算法是系统性能瓶颈时，我们就应该选择尽可能高效的KMP算法。而KMP算法本身具备逻辑复杂、实现难度大的特性，但复杂问题用复杂方法解决并不会违反KISS原则。

但是，平时项目中涉及到的字符串匹配问题，大部分都是针对比较小的文本。这种情况下，直接调用编程语言提供的线程字符串匹配函数就足够了。如果小题大作的话，那就真的违背KISS原则了。



### 如何写出满足KISS原则的代码

1. 不要使用过于高级的技术来实现代码。
2. 不要重复造轮子，要善于使用现成的类库。
3. 不要过度使用奇技淫巧，例如位运算算法，复杂的条件语句替换if-else等，以微小的性能提升牺牲代码可读性。



### YAGNI与KISS说的是一回事么

YAGNI，You ain't gonna need it，核心思想就是：不要去编写那些用不到的代码，说白了就是别过度设计。

例如，系统暂时只用Redis存储配置信息，但以后可能会用到ZooKeeper。根据YAGNI原则，当前还未用到ZooKeeper时，就没必要提前编写这段代码。

又比如，不要在项目中提前引入不需要依赖的开发包。对于Java程序员来说，常常使用Maven来管理依赖的类库，而有些人会提前引入大量常用的Jar包，这种的做法就是违背YAGI原则的。

说白了，KISS原则解决的是“如何做”的问题，而YAGNI原则解决的是“做不做”的问题。