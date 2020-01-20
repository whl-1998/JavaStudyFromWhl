# 反射机制
通过反射机制可以在程序运行状态中动态加载一个类，在加载完成后堆内存中就生成了一个包含加载类完整类结构信息的Class对象。通过该Class对象，我们可以获取并调用所有属于加载类的方法或属性。

### 获取Class对象的三种方式
```java
Class clz1 = Class.forName();
Class clz2 = SomeClass.getClass();
Class clz3 = SomeClass.class; 
```

### Class对象的常用API
```java
//1. 获取类名称
clz.getName();//获取包名+类名
clz.getSimpleName();//获取类名

//2. 获取类属性
Field[] fields = clz.getFields();//获取类中所有public的属性
Field[] fields1 = clz.getDeclaredFields();//获取类中所有的属性

//3. 方法
Method[] methods = clz.getDeclaredMethods();//获取类中所有的方法
Method m1 = clz.getDeclaredMethod("getName", null);//获取类中指定名称 + 参数的方法
Method m2 = clz.getDeclaredMethod("setName", String.class);


//4. 构造器
Constructor[] con = clz.getDeclaredConstructors();//获取类中所有构造方法
Constructor c1 = clz.getDeclaredConstructor(String.class, String.class);//获取类中指定参数的构造方法
Constructor c2 = clz.getDeclaredConstructor(null);//获取类中的无参构造方法

//5. 实例化
User u = (User)clz.newInstance();//调用类的无参构造器创建对象
Constructor<T> constructor = clz.getDeclaredConstructor(String.class);//获取有参构造方法
User u2 = constructor.newInstance("whl");//传入参数实例化对象

//6. 通过反射调用方法
Method method = clz.getDeclaredMethod("setName", String.class)//获取指定名称 + 参数的方法
method.invoke(u3, "whl3");//通过调用invoke, 传入Class的实例对象和指定参数去执行该方法

//7. 通过反射修改属性
Field field = clz.getDeclaredField("name");//获取指定属性
field.setAccessible(true);//关闭访问安全检查开关
field.set(u4, "whl1998");//修改Class的属性默认值
```
### 总结
可以看到，通过反射我们能够做很多事情，而在各类通用框架开发中，为了保证框架通用性，需要根据配置文件加载不同的类，比如Spring中的IOC配置文件实现：
```xml
<bean class="com.spring01.a_ioc.UserServiceImpl"></bean>
```
上述代码就是通过反射获取了UserServiceImpl的Class对象，并通过这个Class对象完成了一系列操作生成了容器中的bean。  

由于反射调用可以忽略权限检查，因此可能会因为破坏封装性而导致安全问题，除此以外，反射会额外消耗一定的系统资源，因此如果不需要动态创建对象，就不要使用反射。

# 代理模式
通过代理模式能够将很多与业务逻辑无关的操作都模块化，比如事务、日志、安全等，让开发者只需要专注业务逻辑本身，最常见的应用就是Spring中的AOP了。代理模式又分为静态代理和动态代理，静态代理可以理解为 在程序运行前就已经存在的编译好的代理类，而动态代理可以理解为由程序自动生成代理类。这个生成代理类的操作就是基于反射机制实现的，也可以有其他的动态代理实现方式，例如通过cglib、javassist等字节码操作机制。区别在于JDK的动态代理必须实现通用接口，而cglib的动态代理则没有接口的限制，Spring在实现AOP会基于不同的情况选择不同的代理模式实现。

### 静态代理
生活中比较常见的代理模式也就是歌手和经纪人了，试想一下，如果没有经济人的话歌手协商、演唱、善后全都需要一个人包干，如果有经济人的话，歌手只需要负责唱歌就好，下面是该场景的代码实现：
```java
/**
	公共接口
*/
public interface Star {
    void sing();
}

/**
 * 被代理对象
 */
public class Singer implements Star{
    @Override
    public void sing() {
        System.out.println("开演唱会");
    }
}


/**
 * 代理对象
 */
public class SingerProxy implements Star{
    private Singer singer = new Singer();

    @Override
    public void sing() {
        consult();
        singer.sing();
        celebrate();
    }

    private void consult() {
        System.out.println("协商价钱");
    }

    private void celebrate() {
        System.out.println("庆祝");
    }
}
```
同样的在开发中，一个对象如果要新增某些模块化的方法，比如添加日志，如果不采用代理模式那么只能手动添加，一个类还好，如果成百上千个类都需要添加这么一个重复的日志操作，那么必然是极其耗时并且是无意义的。这也是代理模式存在的价值————将通用的操作模块化。

### 动态代理
如果我们需要很多的代理对象，每一个都这么去创建实属浪费时间，而且会有大量的重复代码，此时我们就可以采用动态代理，动态代理可以在程序运行期间根据需要动态地创建代理类及其实例来完成具体的功能。动态代理中，代理类必须实现InvocationHandler接口，并重写invoke()方法，以静态同样的示例进行动态代理实现，代码如下：
```java
public class SingerProxy implements InvocationHandler {
    private Singer singer = new Singer();//被代理对象实例

    private void consult() {
        System.out.println("协商价钱");
    }

    private void celebrate() {
        System.out.println("庆祝");
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        consult();
        method.invoke(singer, null);
        celebrate();
        return null;
    }

    public static void main(String[] args) {
	    // 获取代理对象, 参数分别为：类加载器，接口类型，代理类实例
        Star st = (Star) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Star.class}, new SingerProxy());
        st.sing();// 执行增强后的方法
    }
}
```
