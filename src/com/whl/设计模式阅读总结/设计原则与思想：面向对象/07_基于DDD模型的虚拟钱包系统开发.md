如果我们需要开发一个具备充值、提现、支付、查询余额、查询交易流水这五个核心的功能的虚拟钱包系统，那么基于MVC模型开发与基于DDD模型开发的系统会有什么不同呢。

首先我们对五种核心功能的业务逻辑进行分析：

**1. 充值**

用户通过第三方（银行卡、支付宝、微信等）将账户内的钱充值到虚拟钱包中。整个流程可以分为三个主要步骤：

1. 从用户的第三方账户中将金额转账到虚拟钱包应用的公共银行卡
2. 将用户的充值金额加到该用户的虚拟钱包余额上
3. 记录交易流水

<img src="https://static001.geekbang.org/resource/image/39/14/3915a6544403854d35678c81fe65f014.jpg" alt="img" style="zoom:33%;" />

**2. 支付**

用户将虚拟钱包内的余额用于购买应用内的某个商品。整个流程如下所示：

1. 用户虚拟钱包余额扣除相应的金额，商家虚拟钱包增加相应的余额
2. 记录交易流水

<img src="https://static001.geekbang.org/resource/image/7e/5e/7eb44e2f8661d1c3debde85f79fb2c5e.jpg" alt="img" style="zoom: 25%;" />

**3. 提现**

用户将虚拟钱包中的余额提现到自己的第三方账户中。整个流程如下所示：

1. 用户虚拟钱包扣除相应的金额
2. 虚拟钱包应用的公共银行卡转账相应的金额到用户的第三方账户中
3. 记录交易流水

<img src="https://static001.geekbang.org/resource/image/66/43/66ede1de93d29b86a9194ea0f80d1e43.jpg" alt="img" style="zoom: 33%;" />

**4. 查询余额**

直接返回用户虚拟钱包的余额即可。



**5. 查询交易流水**

将之前记录的交易流水按照时间、类型等条件过滤之后，显示出来即可。



### 虚拟钱包系统设计思路

根据上述的业务逻辑分析，我们可以将虚拟钱包系统的业务划分为两个部分：

1. 跟应用内的虚拟钱包账户交互
2. 跟第三方账户交互

为此，我们将整个钱包系统拆分为两个子系统：虚拟钱包系统和第三方支付系统。

<img src="https://static001.geekbang.org/resource/image/60/62/60d3cfec73986b52e3a6ef4fe147e562.jpg" alt="img" style="zoom: 25%;" />

如果要支持虚拟钱包的五个功能，那么虚拟钱包系统都需要执行下面几个操作：

<img src="https://static001.geekbang.org/resource/image/d1/30/d1a9aeb6642404f80a62293ab2e45630.jpg" alt="img" style="zoom: 33%;" />

从图中可以得知，虚拟钱包系统要支持的操作抛开查询交易流水不谈，也就是简单的余额加减操作。而对于比较特殊的 “查询交易流水” 操作，返回的交易流水信息需要包含如下的数据：

<img src="https://static001.geekbang.org/resource/image/38/68/38b56bd1981d8b40ececa4d638e4a968.jpg" alt="img" style="zoom: 33%;" />

从图中可以发现，交易流水的数据格式包含两个钱包账号（入账、出账），这主要是为了兼容支付操作（支付操作涉及了两个账户的交易）。不过，对于充值、提现这两种只需要记录一个账户信息的操作，这种交易流水数据格式的设计就稍微有点浪费内存空间。

为此，我们可以使交易流水的数据结构只包含一个账户信息。当执行支付操作的出账时，钱包账号存储出账账户信息；执行支付操作的入账时，钱包账户存储入账账户信息。下图是两种交易流水不同数据结构实现的对比：

<img src="https://static001.geekbang.org/resource/image/a7/af/a788777e25305614d94d71e5960e06af.jpg" alt="img" style="zoom:100%;" />

**上述两种交易流水数据结构设计思路哪种更好，为什么？**

答：第一种更好，因为交易流水不仅要供用户查询流水信息，还要用于保证事务的一致性。这里事务指整个支付操作的数据一致性。我们要求：一次完整的支付操作，要么出账操作与入账操作同时成功，要么就都失败。如果出账成功、入账失败就会导致数据的不一致。

保证数据一致性的方式有很多，例如依赖数据库事务的原子性，将两个操作放到同一个事务中执行。但这种方式不够灵活，若支付操作涉及到的两个账户存储在不同的库中（分库分表的情况下），那么就无法利用数据库的事务机制。虽然我们也可以使用一些分布式事务的开源框架，但是它们的逻辑都比较复杂、性能也不高，会影响到业务的执行时间。因此，更加权衡的做法是不保证数据的强一致，只要实现数据最终一致即可。

那么 “数据最终一致” 具体应该怎么做，这就需要依赖交易流水的第一种数据结构。对于支付这种涉及到两个账户之间交互的操作，我们先将交易流水标记为 “TO_BE_EXECUTED” 并持久化到数据库，当入账、出账两个操作都执行完毕后，才将数据库中的交易流水更新为 “EXECUTED”。若中间执行入账、出账时出问题进入到异常块，那么将数据库中的交易流水更新为 “FAILED”。针对 “FAILED” 或者长时间处于 “TO_BE_EXECUTED” 的交易流水，我们可以对其进行特殊处理（人工接入或重新执行）。这种思路的具体代码实现可以如下所示：

```java
public void transfer(Long fromWalletId, Long toWalletId, BigDecimal amount) {
    //创建交易流水实例
    VirtualWalletTransactionEntity transactionEntity = new VirtualWalletTransactionEntity();
    transactionEntity.setAmount(amount);
    transactionEntity.setCreateTime(System.currentTimeMillis());
    transactionEntity.setFromWalletId(fromWalletId);
    transactionEntity.setToWalletId(toWalletId);
    //初始化流水实例的Status字段为"等待被执行"
    transactionEntity.setStatus(Status.TO_BE_EXECUTED);
    //持久化流水实例到数据库
    Long transactionId = transactionRepo.saveTransaction(transactionEntity);
    try {
        //执行出账操作
        debit(fromWalletId, amount);
        //执行入账操作
        credit(toWalletId, amount);
    } catch (InsufficientBalanceException e) {//若抛出异常, 则将Status字段更新为"失败"
        transactionRepo.updateStatus(transactionId, Status.CLOSED);
        ...rethrow exception e...
    } catch (Exception e) {
        transactionRepo.updateStatus(transactionId, Status.FAILED);
        ...rethrow exception e...
    }
    //当转账操作完整执行后, 更新交易流水实例的Status字段为"成功执行"
    transactionRepo.updateStatus(transactionId, Status.EXECUTED);
}
```

若选择的是第二种交易流水数据结构，那么需要使用两条流水来记录入账、出账的操作。那么 “记录两条流水” 这个操作本身又存在数据一致性问题，有可能入账交易流水成功、出账的交易流水失败。因此还是选择第一种数据结构更加合适。



**我们是否需要在虚拟钱包系统的交易流水中记录交易的类型（充值、体现、支付）？**

答：不需要。虚拟钱包只需要支持余额的增、减操作，不需要涉及复杂业务概念，以保持职责单一、功能通用。如果虚拟钱包系统耦合了过多业务概念，那么势必会影响到系统的通用性，还会导致系统的逻辑越来越复杂。

但是，若我们不在虚拟钱包的交易流水中记录交易类型，那么用户在查询流水时又如何得知每条流水的交易类型呢？

我们可以通过记录两条流水的方式解决。之前的分析中，我们提到过：整个钱包系统可以分为两个子系统 —— 虚拟钱包系统 + 第三方支付系统。对于上层钱包系统来说，它是可以感知充值、支付、提现的业务概念的，因此我们可以在钱包系统这一层额外记录一条包含交易类型的流水信息，而在底层虚拟钱包系统中记录不包含交易类型的流水信息即可。

这种思路可以如下图所示：

![image-20200302221540790](C:\Users\WHL\AppData\Roaming\Typora\typora-user-images\image-20200302221540790.png)

我们可以通过查询上层钱包系统的流水信息去满足用户获取交易流水的功能需求，而针对虚拟钱包系统的交易流水则主要用于解决数据一致性问题或对账问题等。



### 基于MVC的传统开发模式

针对上面的业务逻辑分析后，我们先通过传统的MVC模型进行虚拟钱包的开发。下面是虚拟钱包系统的Controller层实现，省略了具体的代码逻辑：

```java
public class VirtualWalletController {
    private VirtualWalletService virtualWalletService;//依赖注入

    public BigDecimal getBalance(Long walletId) { ... } //查询余额

    public void debit(Long walletId, BigDecimal amount) { ... } //出账
    
    public void credit(Long walletId, BigDecimal amount) { ... } //入账
    
    public void transfer(Long fromWalletId, Long toWalletId, BigDecimal amount) { ...} //转账
}
```

Service层代码实现也进行了相应的省略：

```java
public class VirtualWalletBo {
    //省略getter/setter/constructor方法
    //...
    
    private Long id;
    private Long createTime;
    private BigDecimal balance;
}

public class VirtualWalletService {
    //依赖注入
    private VirtualWalletRepository walletRepo;
    private VirtualWalletTransactionRepository transactionRepo;

    public VirtualWalletBo getVirtualWallet(Long walletId) {
        //根据传入id获取到walletEntity实例
        VirtualWalletEntity walletEntity = walletRepo.getWalletEntity(walletId);
        //对walletEntity实例填充逻辑, 转换为walletBo
        VirtualWalletBo walletBo = convert(walletEntity);
        return walletBo;
    }
	
    public BigDecimal getBalance(Long walletId) {
        //根据传入id获取到对应虚拟钱包的余额值
        return walletRepo.getBalance(walletId);
    }

    public void debit(Long walletId, BigDecimal amount) {
        //根据传入id获取到walletEntity实例
        VirtualWalletEntity walletEntity = walletRepo.getWalletEntity(walletId);
        //获取walletEntity实例的余额值
        BigDecimal balance = walletEntity.getBalance();
        //出账参数合法校验
        if (balance.compareTo(amount) < 0) {
            throw new NoSufficientBalanceException(...);
        }
        //更新walletEntity实例的余额值
        walletRepo.updateBalance(walletId, balance.subtract(amount));
    }

    public void credit(Long walletId, BigDecimal amount) {
        //根据传入id获取到walletEntity实例
        VirtualWalletEntity walletEntity = walletRepo.getWalletEntity(walletId);
        //获取到walletEntity实例的余额
        BigDecimal balance = walletEntity.getBalance();
        //更新walletEntity实例的余额值
        walletRepo.updateBalance(walletId, balance.add(amount));
    }

    public void transfer(Long fromWalletId, Long toWalletId, BigDecimal amount) {
        //创建交易流水实例
        VirtualWalletTransactionEntity transactionEntity = new VirtualWalletTransactionEntity();
        transactionEntity.setAmount(amount);
        transactionEntity.setCreateTime(System.currentTimeMillis());
        transactionEntity.setFromWalletId(fromWalletId);
        transactionEntity.setToWalletId(toWalletId);
        //初始化流水实例的Status字段为"等待被执行"
        transactionEntity.setStatus(Status.TO_BE_EXECUTED);
        //持久化流水实例到数据库
        Long transactionId = transactionRepo.saveTransaction(transactionEntity);
        try {
            //执行出账操作
            debit(fromWalletId, amount);
            //执行入账操作
            credit(toWalletId, amount);
        } catch (InsufficientBalanceException e) {
            transactionRepo.updateStatus(transactionId, Status.CLOSED);
            ...rethrow exception e...
        } catch (Exception e) {
            transactionRepo.updateStatus(transactionId, Status.FAILED);
            ...rethrow exception e...
        }
        //当转账操作完整执行后, 更新交易流水实例的Status字段为"成功执行"
        transactionRepo.updateStatus(transactionId, Status.EXECUTED);
    }
}
```

省略了Dao层的逻辑....

以上就是基于MVC贫血模型实现的虚拟钱包系统，整体的业务逻辑大致就如上所示。



### 基于充血模型的DDD开发模式

MVC与DDD模型的主要区别在于Service层，因此我们重点了解Service层按照DDD开发模式该如何实现。

我们将虚拟钱包VirtualWallet类设计为一个充血的Domain领域模型，并且将原来在Service中的业务逻辑搬迁到VirtualWallet中，让Service类的实现依赖VirtualWallet：

```java
public class VirtualWallet { // Domain领域模型(充血模型)
    private Long id;
    private Long createTime = System.currentTimeMillis();;
    private BigDecimal balance = BigDecimal.ZERO;

    public VirtualWallet(Long preAllocatedId) {
        this.id = preAllocatedId;
    }

    //获取余额
    public BigDecimal balance() {
        return this.balance;
    }

    //出账
    public void debit(BigDecimal amount) {
        //参数合法校验
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException(...);
        }
        //当前实例余额减去相应的值
        this.balance.subtract(amount);
    }

    //入账
    public void credit(BigDecimal amount) {
        //参数合法校验
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException(...);
        }
        //当前实例余额增加相应的值
        this.balance.add(amount);
    }
}

public class VirtualWalletService {
    //依赖
    private VirtualWalletRepository walletRepo;
    private VirtualWalletTransactionRepository transactionRepo;

    public VirtualWallet getVirtualWallet(Long walletId) {
        //获取到walletEntity实例
        VirtualWalletEntity walletEntity = walletRepo.getWalletEntity(walletId);
        //将walletEntity实例填充为VirtualWallet实例
        VirtualWallet wallet = convert(walletEntity);
        return wallet;
    }

    public BigDecimal getBalance(Long walletId) {
        return walletRepo.getBalance(walletId);
    }

    public void debit(Long walletId, BigDecimal amount) {
        //获取到walletEntity实例
        VirtualWalletEntity walletEntity = walletRepo.getWalletEntity(walletId);
        //将walletEntity实例填充为VirtualWallet实例
        VirtualWallet wallet = convert(walletEntity);
         //调用VirtualWallet实例的debit()方法执行入账逻辑
        wallet.debit(amount);
        //更新数据库中对应wallet的余额
        walletRepo.updateBalance(walletId, wallet.balance());
    }

    public void credit(Long walletId, BigDecimal amount) {
        //获取到walletEntity实例
        VirtualWalletEntity walletEntity = walletRepo.getWalletEntity(walletId);
        //注意这里与MVC模型的区别
        //将walletEntity实例填充为VirtualWallet实例
        VirtualWallet wallet = convert(walletEntity);
        //调用VirtualWallet实例的credit()方法执行转账逻辑
        wallet.credit(amount);
        //更新数据库中对应wallet的余额
        walletRepo.updateBalance(walletId, wallet.balance());
    }

    public void transfer(Long fromWalletId, Long toWalletId, BigDecimal amount) {
        //...跟基于贫血模型的传统开发模式的代码一样...
    }
}
```

通过上述代码，我们能够发现，Service层中主要的业务逻辑基本上都在领域模型VirtualWallet中实现，但是VirtualWallet依然很单薄，包含的业务逻辑都非常简单，相比起之前的MVC模型实现，似乎并没有什么优势。

这也印证了我们之前说的 —— “对于简单业务的系统，采用MVC模型更合适”，这也是为什么当前大部分业务系统都使用基于贫血模型开发的原因。

不过，当虚拟钱包的系统支持更加复杂的业务逻辑，那么充血模型的优势就得以体现了。比如，我们需要支持透支一部分余额和冻结部分余额的功能，这时我们再在基于DDD模型的代码中增加相应的逻辑：

```java
//Domain
public class VirtualWallet {
    private Long id;
    private Long createTime = System.currentTimeMillis();;
    private BigDecimal balance = BigDecimal.ZERO;
    private boolean isAllowedOverdraft = true;//默认允许透支
    private BigDecimal overdraftAmount = BigDecimal.ZERO;//透支金额默认为0
    private BigDecimal frozenAmount = BigDecimal.ZERO;//冻结金额默认为0

    public VirtualWallet(Long preAllocatedId) {
        this.id = preAllocatedId;
    }
	
    public void freeze(BigDecimal amount) { ... } //冻结指定金额
    public void unfreeze(BigDecimal amount) { ...}//解冻指定金额
    public void increaseOverdraftAmount(BigDecimal amount) { ... }//增加指定透支金额
    public void decreaseOverdraftAmount(BigDecimal amount) { ... }//减少指定透支金额
    public void closeOverdraft() { ... }//关闭透支
    public void openOverdraft() { ... }//打开透支

    public BigDecimal balance() {
        return this.balance;
    }

    //获取最大可用金额 (余额 + 透支金额)
    public BigDecimal getAvaliableBalance() {
        //减去冻结的金额
        BigDecimal totalAvaliableBalance = this.balance.subtract(this.frozenAmount);
        //若允许透支, 那么加上透支的金额
        if (isAllowedOverdraft) {
            totalAvaliableBalance += this.overdraftAmount;
        }
        return totalAvaliableBalance;
    }

    //出账
    public void debit(BigDecimal amount) {
        //获取到最大可用金额
        BigDecimal totalAvaliableBalance = getAvaliableBalance();
        //参数合法校验
        if (totoalAvaliableBalance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException(...);
        }
        //余额减去相应值
        this.balance.subtract(amount);
    }
	
    //入账
    public void credit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException(...);
        }
        this.balance.add(amount);
    }
}
```

在业务逻辑变得相对复杂之后，领域模型VirtualWallet的代码就丰富了许多。如果功能继续演进，还可以增加更加细化的冻结策略、透支策略、VirtualWallet Id 的自动生成策略（通过分布式Id生成算法自动生成Id），VirtualWallet就会变得更加复杂，也更加值得被设计为充血模型。



**既然DDD模型中，Domain的Service类变得很薄，那么为什么不直接去掉Service类呢？**

答：Service区别与Domain还具备如下几个职责：

1. Service类需要负责与Repository交流：

   例如VirtualWalletService需要负责与Repository层交互，调用Repository类的方法以获取数据库中的数据（Entity），然后再将Entity填充为领域模型来完成业务逻辑。

   之所以是让VirtualWalletService类与Repository交互而不是领域模型，那是因为我们希望保持领域模型的独立性，不与任何其他层的代码或框架耦合。将流程性代码（从DB中取/存数据）与领域模型解耦能够让领域模型更加可复用。

2. Service类负责跨领域模型的业务聚合功能：

   例如VirtualWalletService中的 transfer() 转账方法会涉及到入账debit()、出账credit() 这两个操作，因此这部分逻辑无法放到领域模型中，所以只好暂时将转账业务放到Service中。可能未来随着功能的演进，在转账业务逻辑复杂之后，我们也可以将其抽取成为一个独立的领域模型。

3. Service类负责一些非功能性以及第三方系统交互的工作：

   例如事务、日志、邮件等，都可以放到Service中处理。



**在DDD模型中，为什么仅仅只有Service层被改造成了充血模型？**

答：Controller层主要负责对外暴露接口，Repository层主要负责与数据库交互。这两层包含的业务逻辑并不多，针对业务逻辑简单的场景，就完全没必要采用充血模型。

况且从功能上来看，Controller中的VO也只需要作为数据传输的载体，因此不包含业务逻辑、只包含数据，这样也是比较合理的。