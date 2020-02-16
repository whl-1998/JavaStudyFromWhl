### 如何理解单一职责原则（SRP）

单一职责原则，Single Responsibility Principe，要求一个类或者模块只负责完成一个职责或功能。换个角度来说，如果一个类如果包含了两个或以上的业务不相干功能，那么就可以认定它的职责不够单一，应该将其拆分为多个功能单一、粒度更细的类。

例如，一个类中既包含了订单的一些操作，又包含了用户的一些操作。而订单和用户是两个独立的业务领域模型，我们将两个不相干的功能放到一起就违反了单一职责原则。为了满足单一职责，可以将类拆分为订单类和用户类。

从上述例子来看，单一职责似乎非常简单，但是在真实的开发中，对一个类的职责是否单一的判断是很难拿捏的，例如下列代码：

```java
public class UserInfo {
    private long userId;
    private String username;
    private String email;
    private String telephone;
    private long createTime;
    private long lastLoginTime;
    private String avatarUrl;
    private String provinceOfAddress; // 省
    private String cityOfAddress; // 市
    private String regionOfAddress; // 区 
    private String detailedAddress; // 详细地址
    // ...省略其他属性和方法...
}
```

对于上述代码有两种说法，其一是，UserInfo类包含的都是跟用户相关的信息，满足单一职责；另一种说法是，地址保存在UserInfo中所占的比重比较高，可以继续拆分成独立的UserAddress。

其实两种说法都不能算错，只是要针对不同的场景进行不同的选择。例如社交类系统中，用户的地址跟其他信息一样都是用来展示的，那么上述例子就是满足单一职责的；但如果是电商类系统，地址模块会涉及到物流这方面的操作，那么最好将地址信息从UserInfo中拆分出来。

那么如此偏主观、含糊不清的原则到底要如何拿捏才好呢，其实可以通过如下一些判断原则来考虑是否需要拆分：

1. 类中的代码行数、函数或者属性过多，影响到代码的可读性和可维护性时，就可以考虑是否能对类进行拆分。
2. 类依赖的其他类过多，不符合高内聚，低耦合的设计，此时可以考虑是否能拆分类。
3. 比较难给类起一个合适的名字，此时就可能是类的职责定义不够明确。
4. 类中大量的方法都是集中操作类的某几个属性，例如上述UserInfo的例子中，存在一半以上的方法操作Address属性，就可能不满足单一职责原则。



### 类的职责是否越单一越好

既然一个类最好满足单一职责原则，那么是否把类拆得越细越好呢？答案当然是否定的，物极必反，下面代码中Serialization类实现了一个简单协议的序列化与反序列化功能，具体如下：

```java
public class Serialization {
    private static final String IDENTIFIER_STRING = "UEUEUE;";
    private Gson gson;

    public Serialization() {
        this.gson = new Gson();
    }

    //序列化
    public String serialize(Map<String, String> object) {
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append(IDENTIFIER_STRING);
        textBuilder.append(gson.toJson(object));
        return textBuilder.toString();
    }

    //反序列化
    public Map<String, String> deserialize(String text) {
        if (!text.startsWith(IDENTIFIER_STRING)) {
            return Collections.emptyMap();
        }
        String gsonStr = text.substring(IDENTIFIER_STRING.length());
        return gson.fromJson(gsonStr, Map.class);
    }
}
```

如果我们要让该类的职责更加单一，就可以对Serialization进一步拆分为：负责序列化工作的Serializer和负责反序列化的Deserializer，如下所示：

```java
public class Serializer {
    private static final String IDENTIFIER_STRING = "UEUEUE;";
    private Gson gson;

    public Serializer() {
        this.gson = new Gson();
    }

    public String serialize(Map<String, String> object) {
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append(IDENTIFIER_STRING);
        textBuilder.append(gson.toJson(object));
        return textBuilder.toString();
    }
}

public class Deserializer {
    private static final String IDENTIFIER_STRING = "UEUEUE;";
    private Gson gson;

    public Deserializer() {
        this.gson = new Gson();
    }

    public Map<String, String> deserialize(String text) {
        if (!text.startsWith(IDENTIFIER_STRING)) {
            return Collections.emptyMap();
        }
        String gsonStr = text.substring(IDENTIFIER_STRING.length());
        return gson.fromJson(gsonStr, Map.class);
    }
}
```

拆分过后，首先一个非常明显的问题，代码复用性很差。其次，如果数据标识修改了，我们需要同时修改两个类的信息，代码可维护性非常差。因此，在遵循单一职责原则的同时，也不要过度拆分。