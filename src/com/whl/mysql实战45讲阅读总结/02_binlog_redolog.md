### 背景
在mysql中，如果每一次更新操作都要将结果写进磁盘，磁盘也要找到对应的记录进行更新，整个过程IO成本，查询成本都很高。为了解决这个问题，mysql采用了WAL（write-ahead logging）技术，先写日志，再写磁盘。

### redo-log

采用WAL技术，当有一条语句需要更新的时候，InnoDB引擎会先把记录写入到redo-log里，并更新内存。同时，InnoDB引擎会在适当的时候（比如空闲时期）将结果更新到磁盘里。

redo-log是固定大小的，比如可以配置为四个日志文件，每个文件的大小为1GB，总共就可以记录4GB的操作。同时redo-log存在两个指针分别是write pos、check point，write pos用于指向当前记录的位置，而check point用于指向当前需要更新到磁盘的位置。

<img src="https://static001.geekbang.org/resource/image/16/a7/16a7950217b3f0f4ed02db5db59562a7.png" alt="img" style="zoom:100%;" />

例如上图，黄色是redo-log已经被写满的部分，绿色是redo-log还可以写部分，check point会以顺时针的方向将黄色部分写入磁盘，而write pos会以顺时针的方向在空闲的位置记录日志。当整个redo-log被写满，此时就不能执行新的更新操作了，必须停下来将redo-log的记录写入到磁盘中。

有了redo-log，InnoDB就可以保证数据库在异常重启的时候，之前提交的记录都不会丢失，这个能力称为crash-safe。

### bin-log

redo-log是InnoDB引擎特有的日志，而Server层也有自己的日志——bin-log。最开始，mysql自带的引擎是MyISAM，并没有InnoDB引擎，MyISAM并没有crash-safe的能力，bin-log日志只能用于归档，靠bin-log无法实现crash-safe的能力，因此才必须两个日志结合使用。

两种日志的不同：

1. redo-log是InnoDB特有的；bin-log是server层实现的，所有引擎都可以使用。
2. redo-log是物理日志，记录的是“在某个数据页上做了什么修改”；bin-log是逻辑日志，记录的是语句的原始逻辑，例如“更新 id=2 这一行的 born 字段为 1998”。
3. redo-log是循环写的，空间固定会用尽；bin-log是可以追加写的，也就是bin-log文件写到一定大小后，会切换到下一个文件继续写，并不会覆盖以前的日志。

由此，一条简单的更新语句 “update T set c=c+1 where ID=2” 在 执行器 → InnoDB 执行的流程如下：

1. 执行器先找InnoDB引擎获取id=2这一行，ID是主键，InnoDB直接通过主键索引树找到对应的行并交给执行器，如果id=2这一行所在的数据页本来就在内存中，就直接返回给执行器，否则需要从磁盘读入内存再返回。
2. 执行器获取到行数据，执行c+1，得到新的一行数据，再调用引擎接口写入这行新数据
3. InnoDB将这行数据更新到内存中，同时也将更新操作记录到redo-log，此时redo-log为prepare状态，告知执行器执行完成，随时可以提交事务。
4. 执行器生成这个更新操作的bin-log，并将bin-log写入磁盘
5. 执行器调用InnoDB的提交事务接口，InnoDB把刚才写入的prepare状态的redo-log改成commit状态，更新完成

##### 两阶段提交

我们可以发现，一套完整的更新操作伴随着两个日志的写入。首先是记录redo-log并置为prepare状态，其次是记录bin-log并告知redo-log可以提交事务，最后redo-log提交事务并置为commit状态。其中redo-log的提交步骤是有prepare、commit两个阶段的。

在之前有提到，bin-log是server层在更新时将记录写入日志用于归档的；redo-log是引擎层更新时先将记录写到日志，从而提供crash-safe以及提高更新效率的。那么要结合两者使用，就必须保证两者的状态逻辑性是一致的。因此这个两阶段提交的目的，也就是保证bin-log、redo-log的一致性。

我们可以设想：当server层写完bin-log后，突然发生crash，由于redo-log还没有commit，但是prepare状态的redo-log是保存了更新的记录的，并且bin-log也是完整的，只需要重启后自动commit即可；当server层还没开始写bin-log，突然发生crash，在重启后，发现执行器并没有调用InnoDB的提交事务接口，那么redo-log会认为事务是失败的，之前写的redo-log记录就回滚。

### 问题汇总

##### 1. 既然redo-log和bin-log都能够记录更新的操作日志，那么可不可以去掉bin-log呢？

不能，因为redo-log只存在于InnoDB，其次redo-log是循环写的，并不作持久化保存，使用redo-log做归档操作是不可能的，只能使用bin-log进行归档。