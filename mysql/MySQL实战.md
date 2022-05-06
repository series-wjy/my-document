# MySQL实战

## SQL语句执行

### 查询语句

![1550835754343](E:\kaikeba\wjy\mysql\assets\1550835754343.png)

​	大体说，MySQL可以分为Sever层和存储引擎层两部分。

​	Server 层包括连接器、查询缓存、分析器、优化器、执行器等，涵盖MySQL的大多数核心功能，包括全部 内置函数。所有跨引擎层的功能都在这一层实现，比如存储过程、触发器、视图等。

​	存储引擎层主要负责数据的存储和提取。其架构模式是插件式的，支持InnoDB、MyISAM、Memory等。

+ **连接器**

  + 负责跟客户端建立连接，获取权限，维持和管理连接。

  + 连接建立，获取用户权限。

  + show processlist查看连接状态。

  + 如果连接太长时间没动，会自动断开，这个时间由参数wait_timeout控制，默认8小时。

  + 连接分为长连接和短连接，建立连接的过程比较复杂，尽量使用长连接。

  + 使用长连接注意事项：

    + 全部使用长连接以后，可能MySQL内存涨得快，这是因为执行过程中临时使用的内存是管理在连接对象里面的，要断开连接才会释放，累计下来导致内存占用过大，被系统强行杀掉（OOM），从现象看就是异常重启。

    + 怎么解决：

      1、定期断开长连接。

      2、如果是5.7以上版本，可以在执行一个比较大的操作以后，执行mysql_reset_connection来重新初始化连接资源。恢复连接到刚刚建立时的状态。

+ **查询缓存**

  + 查询缓存往往会失效。

  + 查询缓存可以按需开启，设置query_cache_type=DEMAND，查询中指定SQL_CACHE指定查询缓存。

    ```sql
    mysql> select SQL_CACHE * from T where ID=10；
    ```

  + MySQL8.0以后没有查询缓存功能。

+ **分析器**

  + 词法分析
  + 语法分析

+ **优化器**

  + 优化器根据执行效率决定使用哪种查询方案。

    ```sql
    mysql> select * from t1 join t2 using(ID)  where t1.c=10 and t2.d=20;
    ```

    + 既可以先从表 t1 里面取出 c=10 的记录的 ID 值，再根据 ID 值关联到表 t2，再判断 t2 里面 d 的值是否等于 20。
    + 也可以先从表 t2 里面取出 d=20 的记录的 ID 值，再根据 ID 值关联到 t1，再判断 t1 里面 c 的值是否等于 10。

+ **执行器**

  + 首先判断用户有没有查询对应表的权限（如果命中缓存，在返回结果前验证权限。查询也会在优化器之前调用precheck验证权限）。
  + 权限验证通过，打开表，调用引擎提供的接口。
  + 循环调用引擎提供的接口，一条条判断是否满足条件，满足就存入结果集，不满足就跳过。
  + 慢查询日志中的rows_examined字段是执行器每次调用引擎获取数据时累加的。某些场景下，执行器调用一次，在引擎内部扫描多行，因此，**引擎扫描行数和rows_examined并不完全相同**。
  
  对于有索引的表，执行的逻辑也差不多。第一次调用的是“取满足条件的第一行”这个接口，之后循环取“满足条件的下一行”这个接口，这些接口都是引擎中已经定义好的。
  
  在数据库的慢查询日志中有一个 **rows_examined** 的字段，表示这个语句执行过程中扫描了多少行。这个值是在执行器每次调用引擎获取数据行的时候累加的。

### 更新语句

​	更新语句会导致跟这个表有关的查询缓存失效。优化器决定使用哪一个索引，执行器负责找到这一行数据，然后更新。

+ **redo log**

  + 如果每次更新都写磁盘，整个IO过程成本很高。
  + MySQL中经常说到的技术WAL（Write-Ahead Logging），**先写日志，再写磁盘**。InnoDB先将记录写到redo log，更新内存，这时更新就算完成了。InnoDB会在适当的时候，将redo log的内容更新到磁盘。
  + redo log文件是固定大小的，可以配置为一组4个文件，文件是循环写的。![1550912910765](E:\kaikeba\wjy\mysql\assets\1550912910765.png)
  + write pos是当前记录的位置，checkpoint是当前要擦除的位置。擦除前会把记录更新到磁盘。write pos和checkpoint之间的就是可以擦除的位置。
  + write pos追上checkpoint时，说明redo log已经写满，这时更新操作需要停下来，擦掉记录，讲checkpoint往前推进。
  + 有了redo log可以保证异常重启以后，数据不丢失，这个能力叫**crash-safe**。

+ **binlog**

  ​	Binlog有三种模式，**statement** 格式的话是记sql语句， **row**格式会记录行的内容，还有一种是前两种格式的混合格式**mixed**，记录两条，更新前和更新后都有。

  redo log和binlog主要有以下三点不同：
  
  + redo log是InnoDB特有的日志文件，binlog是server层的日志文件。
  
  + redo log是物理日志，记录“在某个数据页上做了什么修改”；binlog是逻辑日志，记录这个语句的原始逻辑，比如“给ID=2这一行数据的C字段加1”。
  + redo log的循环写的，空间会用完；binlog是追加写入，当前文件写到一定大小，切换到下一个文件，不会覆盖以前的文件。
  
  一个事务的binlog完整性是有固定格式的：
  
  + statement：最后会有COMMIT。
  + row：最后会有一个XID event。
  + Mysql5.6.2版本后，还引入binlog-checksum参数，验证binlog内容的正确性。验证由于磁盘原因导致的日志中间出错的情况。

**崩溃恢复时的判断规则**：

1. 如果redo log的事务完整，也就是包含commit标识，则直接提交；
2. 如果redo log的事务只有prepare，则判断对应事务的binlog是否存在并完整：
   1. 如果是，则提交事务；
   
   + 否则，回滚事务。

redo log和binlog有一个共同的字段XID，崩溃恢复时，会扫描redo log：

+ 如果redo log是完整的，包含prepare、commit，直接提交事务；
+ 如果redo log只有prepare，没有commit，则拿着XID去binlog找对应的事务。

更新语句的执行流程：

![1550913563951](E:\kaikeba\wjy\mysql\assets\1550913563951.png)

+ **两阶段提交**
  
    redo log和binlog都可以用来表示事务提交状态，两阶段提交就是让这两个状态逻辑上保持一致。
  
    + 1、prepare阶段 2、写binlog 3、commit
    + 当在2之前崩溃，重启恢复，发现没有commit，回滚；备份恢复：没有binlog；一致。
    + 当在3之前崩溃，重启恢复，虽然没有commit，但redo log和binlog日志完整，重启后自动commit；备份有binlog，一致。

## 事务

事务就是要保证一组数据库操作，要么全部成功，要么全部失败。事务支持是在存储引擎层实现的。更新数据都是先读后写的，而这个读称为“当前读”（current read）。

### 隔离性和隔离级别

+ MySQL默认隔离级别“可重复读”，Oracle默认隔离级别“提交读”。
+ show variables like 'transaction-isolation';查看事务隔离级别。

### 事务隔离的实现（可重复读隔离级别）

+ MySQL每一个更新操作的同时，会记录一个回滚操作。回滚段示意如下（执行顺序1->2->3->4）：

  ![1551011937733](E:\kaikeba\wjy\mysql\assets\1551011937733.png)

+ 不同时刻启动的事务，会有不同的read-view；同一条记录在系统中存在多个版本，就是数据库的多版本并发控制（MVCC）。

+ 回滚段在系统判定不需要的时候，会删除回滚段（当系统中没有比这个回滚段更新的read-view的时候）。

+ 如果存在长事务，意味着系统里存在很多老的事务视图，在这个事务提前之前，它用到的回滚段记录都必须保留，会占用大量存储空间

+ 在MySQL5.5之前，回滚日志跟数据字典一起存放在ibdata文件里面，即使事务提交，回滚段被清理，文件也不会变小。

+ 长事务还会占用锁资源，可能拖垮整个库。

### 事务的启动方式

+ 事务的开启方式有两种：

  + 显示启动事务，begin或start transaction，提交语句是commit，回滚语句是rollback。

  + set autocommit=0，关闭自动提交；意味着你只执行一个select，事务就开始了，直到执行commit或rollback，或者断开连接。

    **注意：** **begin/start transaction** 命令并不是一个事务的起点，在执行到它们之后的第一个操作 InnoDB 表的语句，事务才真正启动。如果想要马上启动一个事务，可以使用 **start transaction with consistent snapshot** 命令。

    > 第一种启动方式，一致性视图是在执行第一个快照读语句时创建的；
    >
    > 第二种启动方式，一致性视图是在执行 start transaction with consistent snapshot 时创建的。

  

+ 在autocommit=1的情况下，执行begin显示开始一个事务，执行commit提交事务。如果执行commit work and chain，则提交事务并启动下一个事务。

+ 可以在infomation_schema库的innodb_trx表中查询长事务：

  ```sql
  select * from information_schema.innodb_trx where TIME_TO_SEC(timediff(now(),trx_started))>60
  ```

### 如何避免长事务

+ 应用开发端
  + 确认是否使用了 set autocommit=0。这个确认工作可以在测试环境中开展，把 MySQL 的 general_log 开起来，然后随便跑一个业务逻辑，通过 general_log 的日志来确认。一般框架如果会设置这个值，也就会提供参数来控制行为，你的目标就是把它改成 1。
  + 确认是否有不必要的只读事务。有些框架不管什么语句先用 begin/commit 框起来。只读事务可以去掉。
  + 业务连接数据库的时候，根据业务本身的预估，通过 SET MAX_EXECUTION_TIME 命令，来控制每个语句执行的最长时间，避免单个语句意外执行太长时间。
+ 数据库端
  + 监控 information_schema.Innodb_trx 表，设置长事务阈值，超过就报警 / 或者 kill。
  + Percona 的 pt-kill 这个工具不错，推荐使用。
  + 在业务功能测试阶段要求输出所有的 general_log，分析日志行为提前发现问题。
  + 如果使用的是 MySQL  5.6 或者更新版本，把 innodb_undo_tablespaces 设置成 2（或更大的值）。如果真的出现大事务导致回滚段过大，这样设置后清理起来更方便。

## 索引

### 索引概述

+ **索引常见模型**

  + Hash表模型，适合于等值查询场景（区间查询需要扫描所有key），比如Memcached等NoSQL数据库
  + 有序数组，在等值查询和范围查询场景中的性能都很优秀，插入时需要移动数据，成本较高；适用于静态数据存储。
  + 二叉树，查询时间复杂度O(log(N))，为了维持这个时间复杂度，需要保持这个棵树的平衡。
    + 二叉树的搜索效率最高，但是大多数数据库存储不用二叉树，因为索引不止存在内存中，还存在磁盘上。
    + 数据量很大时，二叉树的树高比较大，访问层级越深，访问的数据块越多，访问越慢。
    + 为了减少访问数据块，采用“N叉树”，N取决于数据块的大小。
    + InnoDB整数字段索引，N的值差不多是1200。树高为4的时候，就可以存放1200的3次方条数据（17亿）。树根总是在内存中，查找一条数据，最多访问3次磁盘，其实第二层也很可能在内存中，这样访问磁盘的次数就更少。
  + 不同的存储引擎，索引的实现和工作方式不同。

+ **InnoDB索引模型**

  ![1551024816855](E:\kaikeba\wjy\mysql\assets\1551024816855.png)

  + 表的数据是根据主键顺序以索引的方式存放的，这种存储方式的表被称为**索引组织表**。
  + InnoDB使用B+树索引模型，所以数据都是存放到B+树中。
  + 每个索引对应一棵B+树。
  + 索引分为**主键索引**（聚簇索引cluster index，叶子节点存放的是整行数据）和**非主键索引**（**二级索引**，叶子节点存放主键的值）。
  + 基于普通索引查询，需要先搜索普通索引树，得到ID值，再到主键索引树搜索一次，这个过程叫**回表**。需要多查一棵树，所以应该尽量使用主键查询。

+ **索引维护**

  + 数据的插入，如果在中间插入，需要挪动后面的数据；如果当前数据页写满，要进行页分裂，会影响空间利用率。
  + 顺序插入保证数据页的使用效率，降低数据插入成本
  + 删除数据，当相邻的两个数据页空间利用率很低的时候，就要进行页合并。
  + 主键长度越小，普通索引的叶子节点就越小，普通索引占用空间也越小。

+ **索引重建**

  + 索引可能因为删除或者页分裂等原因，导致数据页有空洞。重建索引的目的是把数据按顺序插入，这样数据页的利用率更高，索引更紧凑，更节省空间。
  + 重建主键索引会导致整个表重建，可以执行：alter table T engine=InnoDB。

+ **覆盖索引**

  + 二级索引查询，如果要查询的字段已经存在二级索引上，就不需要回表了，二级索引覆盖了查询需求，称为覆盖索引。
  + 覆盖索引能有效的减少索引树的搜索次数，显著提升查询性能，是常用优化手段。
  + 对于高频查询请求，可以建立联合索引，使用覆盖索引，不用回表请求整行数据。

+ **最左前缀原则**

  + B+树的索引结构，可以利用“最左前缀”来定位记录。
  + 最左前缀可以是联合索引的最左N个字段，也可以是字符串索引的最左M个字符。
  + 如果可以通过调整顺序来少维护一个索引，那么这个顺序往往就是优先考虑采用的。
  + 联合索引（a,b），条件里只有b无法用到这个索引。

+ **索引下推**

  + MySQL5.6引入索引下推优化（index condition pushdown），可以在索引遍历的过程中，对索引中包含的字段优先判断，过滤掉不符合条件的记录，减少回表次数。

### 索引选择

#### **尽量选择普通索引**

+ 查询过程

  + 查询过程中，唯一索引和非唯一索引查询效率差距微小，除非非唯一索引查询符合条件的记录，刚好是数据页的最后一条记录，因为要加载下一个数据页。

+ 更新过程

  + 第一种情况：更新的记录所在目标页在内存中
    + 唯一索引：找到目标位置，判断冲突，更新数据
    + 非唯一索引：找到目标位置，更新数据
  + 第二种情况：更新的记录所在目标页不在内存中
    + 唯一索引：加载数据页，找到目标位置，判断冲突，更新数据
    + 非唯一索引：将更新记录在change buffer，结束

  第二种情况，非唯一索引减少了磁盘随机I/O操作，对性能的提升明显。


#### **change buffer的适用场景（只适用普通索引）：**

+ 对于写多读少的场景，数据更新完以后马上被访问的几率很小，**使用change buffer的效果最好**。常见的是账单类、日志类系统。
+ 对于读多写少的场景，数据更新完可能马上会被访问，这时会触发**merge**操作，随机I/O次数没有减少，增加了维护change buffer的代价，**不适合使用change buffer**。

#### 字符串创建索引

主要有四种方式：

+  直接创建完整索引，这样可能比较占用空间； 
+  创建前缀索引，节省空间，但会增加查询扫描次数，并且不能使用覆盖索引； 
+  倒序存储，再创建前缀索引，用于绕过字符串本身前缀的区分度不够的问题； 
+  创建 hash 字段索引，查询性能稳定，有额外的存储和计算消耗，跟第三种方式一样，都不支持范围扫描。 

给字符串字段创建索引时，可以使用字符串的前N个字符创建前缀索引。创建前缀时注意区分度，区分度越高，重复值越少，性能越好。

使用前缀索引可能导致的问题：

+ 查询语句读取数据的次数变多。
+ 覆盖索引失效。

字符串创建索引的常用方式：

+ 对于字符串前N个字符重复较多的情况，比如身份证号码，可以采用将**身份证倒序**取身份证的后M位作为索引，增加区分度（**问题：不支持排序**）。
+ 增加一个hash字段，用于存储crc32()函数生成的4字节整数，查询时通过转换后的值进行匹配。

## 锁

### 全局锁

+ 加全局读锁：Flush tables with read lock

  可以使用 set global readonly=true，让全库进入只读状态，但还是建议 FTWRL 方式，主要有两个原因：

  1. 在有些系统中，readonly 的值会被用来做其他逻辑，比如用来判断一个库是主库还是备库。因此，修改 global 变量的方式影响面更大，不建议使用。
  2. 在异常处理机制上有差异。如果执行 FTWRL 命令之后由于客户端发生异常断开，那么 MySQL 会自动释放这个全局锁，整个库回到可以正常更新的状态。而将整个库设置为 readonly 之后，如果客户端发生异常，则数据库就会一直保持 readonly 状态，这样会导致整个库长时间处于不可写状态，风险较高。

+ 应用场景：做全库逻辑备份
  + 主库执行：主库处于只读状态，业务停摆。
  + 从库执行：从库不能同步主库binlog，存在主从延迟。
  
+ 在RR隔离级别下，通过一致性读来备份数据（Innodb）：mysqldump使用-single-transaction参数启动事务，确保生成一致性视图。只能针对支持事务引擎的库。

### 表级锁

#### 表锁

+ 语法：lock tables xxx read/write，unlock tables 主动释放锁或者客户端断开的时候自动释放。
+ 表锁对当前线程的下的操作也有限制。

#### 元数据锁

​		在 MySQL 5.5 版本中引入了 MDL，当对一个表做增删改查操作的时候，加 MDL 读锁；当要对表做结构变更操作的时候，加 MDL 写锁。

+ 访问表时自动加MDL读锁，当修改表结构时加MDL写锁。
+ 读锁之间不互斥，读与写之间、写与写之间互斥。
+ MDL写锁会阻塞之后对该表的所有MDL加锁操作。
  + DDL操作前，先确保没有长事务或者kill掉长事务。
  + DDL操作设置等待时间ALTER TABLE tbl_name NOWAIT add column xxx或者ALTER TABLE tbl_name WAIT N add column xxx。

### 行锁

InnoDB支持行锁，减少锁冲突，提升并发处理能力。

#### GAP锁

为了解决幻读，InnoDB引入GAP锁。行锁和GAP锁合称next-key lock。**间隙锁之间不冲突**。

### 两阶段锁

加锁阶段和解锁阶段，加锁阶段只加锁，解锁阶段只解锁。只有在事务结束的时候，才解锁。因此，**要把最容易引起锁冲突，影响并发度的锁，尽量放到事务的最后执行**。

### 死锁和死锁检测

+ 死锁出现的解决策略（一般采用策略2）：
  + 策略1：直接进入等待，直到超时。这个超时时间可以通过参数 **innodb_lock_wait_timeout** 来设置，默认50秒。。
  + 策略2：发起死锁检测，发现死锁后，主动回滚死锁链条中的某一个事务，让其他事务继续执行。将参数 **innodb_deadlock_detect** 设置为 on，表示开启这个逻辑。
+ 采用策略2会有**热点数据更新导致的性能问题**
  + 热点数据更新，每一条线程都要发起死锁检测，导致大量消耗CPU资源。
  + 解决方案：
    + 关闭死锁检测，存在风险
    + 控制并发度，让多余的更新操作排队
    + 将热点数据划分为多条数据，减少并发冲突
+ 死锁信息查询
  + show processlist；
  + 启动Mysql时设置performance_schema=on，相比设置为off有10%的性能损耗；
  + 查询select blocking_pid from sys.schema_table_lock_waits查询造成阻塞的process id。

### 加锁总结

1. 原则 1：加锁的基本单位是 next-key lock，next-key lock 是前开后闭区间。
2. 原则 2：查找过程中访问到的对象才会加锁。
3. 优化 1：索引上的等值查询，给唯一索引加锁的时候，next-key lock 退化为行锁。
4. 优化 2：索引上的等值查询，向右遍历时且最后一个值不满足等值条件的时候，next-key lock 退化为间隙锁。
5. 一个 bug：唯一索引上的范围查询会访问到不满足条件的第一个值为止。（**非正式**）

## 表空间

表数据可以放到共享表空间，也可以每个表独立的存储一个文件。推荐独立的表文件存储，在drop table时可以直接回收表对应的空间，如果使用的是共享表空间，则不会回收对应的空间。

### 内存复用

数据行复用

+ 数据记录使用delete删除后，并没有从数据页上真正的删除。当重新插入数据时，如果插入的数据在原来删除记录所在的位置，可以复用该条记录所在的数据页位置。

数据页复用

+ 数据页复用和数据记录复用不同，数据页上的记录都被删除后，可以复用到索引树的任何位置。
+ 两个页的利用率很低，页合并后把两个页的数据放到一个数据页上，另外一个数据页就可以标记为可复用。

delete删除后没有被复用的空间，就像**“空洞”**。不止是删除数据会造成空洞，插入数据也会造成空洞。页分裂也可能造成空洞。

### 重建表

为了收缩表空间的大小，需要重建表。

+ 隐式含义：alter table t engine=innodb,ALGORITHM=inplace。
+ 相对含义：alter table t engine=innodb,ALGORITHM=copy。
+ Mysql5.6版本以前的版本重建过程不能更新表数据。
+ 对于大表的重建操作非常消耗CPU和I/O资源，可以采用GitHub开源的gh-ost。

Mysql5.6以前的版本，重建表大致包括创建临时表、完成转存数据、交换表名、删除旧表的操作。在整个DDL的过程中表不能有更新操作。Mysql5.6引入Online DDL操作，对重建表的操作流程做了优化。

1.  建立一个临时文件，扫描表 A 主键的所有数据页； 
2.  用数据页中表 A 的记录生成 B+ 树，存储到临时文件中； 
3.  生成临时文件的过程中，将所有对 A 的操作记录在一个日志文件（row log）中，对应的是图中 state2 的状态； 
4.  临时文件生成后，将日志文件中的操作应用到临时文件，得到一个逻辑数据上与表 A 相同的数据文件，对应的就是图中 state3 的状态； 
5.  用临时文件替换表 A 的数据文件。 

![image-20191024174747205](E:\data\my-document\mysql\assets\image-20191024174747205.png)

由于日志文件记录和重放操作的存在，所以在重建表的过程中，允许对A表进行更新操作。

> 重建大表非常消耗CPU和I/O资源，可以使用GitHub开源的gh-ost操作。

**Online和inplace**

在表重建的过程中，没有把临时数据从引擎拷贝到Server层，是一个“原地”操作，就是inplace。一个大表的重建操作不能使用inplace操作，因为tmp_file也是要占用存储空间的。

重建表的语句alter table t engine=innodb，其实隐含的意思是：

> 隐式含义：alter table t engine=innodb,ALGORITHM=inplace。

跟inplace相对的应的拷贝方式是：

> 相对含义：alter table t engine=innodb,ALGORITHM=copy。

当使用ALGORITHM=copy时，表示强制拷贝。

**inplace和Online是不同的概念**。比如给Innodb表字段增加索引，写法是：

> alter table t add fulltext(filed_name);

这个过程是inplace的，但是会阻塞增删改操作，是非Online的。

两个概念间的逻辑关系可以概括为：

1. DDL过程如果是Online的，就一定是inplace的；
2. 反之未必，也就是说inplace的DDL，有可能不是Online的。截止Mysql8.0，添加全文索引（FULLTEXT index）和空间索引（SPATIAL index）就属于这种情况。

三种操作的区别：

+ alter table t engine=innodb：热create操作，默认是Online、inplace的DDL操作。
+ analyze table t：不重建表，只是对表的索引信息重新统计，不修改数据，该过程加MDL读锁。
+ optimize table t：等于recreate+analyze

## 优化

### optimizer_trace

根据查询optimizer_trace信息，优化SQL查询语句。

```mysql

/* 打开optimizer_trace，只对本线程有效 */
SET optimizer_trace='enabled=on'; 

/* @a保存Innodb_rows_read的初始值 */
select VARIABLE_VALUE into @a from  performance_schema.session_status where variable_name = 'Innodb_rows_read';

/* 执行语句 */
select city, name,age from t where city='杭州' order by name limit 1000; 

/* 查看 OPTIMIZER_TRACE 输出 */
SELECT * FROM `information_schema`.`OPTIMIZER_TRACE`\G

/* @b保存Innodb_rows_read的当前值 */
select VARIABLE_VALUE into @b from performance_schema.session_status where variable_name = 'Innodb_rows_read';

/* 计算Innodb_rows_read差值 */
select @b-@a;
```

### 连接查询

### 排序

非索引字段排序，如果排序数据大于sort_buffer_size时，就会使用到filesort磁盘临时文件辅助排序。filesort使用归并排序，将排序数据划分到多个文件中分别排序，排序完成后再合并到一个文件。

#### 字段排序

一条查询排序语句执行流程如下：

1. 初始化sort_buffer，放入**需要返回结果集**的字段；
2. 根据二级索引获取主键ID，或者通过全表扫描获取数据；
3. 根据主键ID回表，取出需要查询的字段，存入sort_buffer中；
4. 依次获取下一条满足条件的数据；
5. 对sort_buffer中的数据按照排序字段进行排序；
6. 按照排序结果返回数据集给客户端。

查询optimizer_trace获取到信息如下：

![image-20191101145014653](E:\data\my-document\mysql\assets\image-20191101145014653.png)

#### rowid排序

如果将max_length_for_sort_data（控制用于排序的行数据长度）改小，再执行查询语句，执行流程如下：

1. 初始化sort_buffer，放入**排序字段和ID**的字段；
2. 根据二级索引获取主键ID，或者通过全表扫描获取数据；
3. 根据主键ID回表，取出排序字段和ID，存入sort_buffer中；
4. 依次获取下一条满足条件的数据；
5. 对sort_buffer中的数据按照排序字段进行排序；
6. 遍历排序结果，取要返回的前N条数据，并按照ID回表查询返回字段，返回给客户端。

![image-20191101150329590](E:\data\my-document\mysql\assets\image-20191101150329590.png)

两种排序的区别：

+ rowid排序会比字段排序多查询一次数据，就在最后一步返回结果集时的回表；
+ sort_mode变成排序字段和rowid两个字段；
+ number_of_tmp_files更小，因为参与排序的每一行数据更小了，需要的临时文件也更少；
+ Mysql认为内存够大时，会优先选择字段排序，把需要返回的字段都放到sort_buffer，这样排序完就直接返回，不用回表再次查询。

**结果集是逻辑概念，不会耗费内存缓存结果集，直接返回给客户端就好。**

Mysql的设计思想：**如果内存够，就多利用内存，尽量减少磁盘访问。**

可以利用索引减少排序，因为索引是天然有序的。

### 慢查询

#### 索引没设计好

可以采用online DDL。假设有主备两个库，主库A，备库B，可以先在备库执行alter table语句加上索引，再在主库上执行，大致流程如下：

1. 在备库 B 上执行 set sql_log_bin=off，也就是不写 binlog，然后执行 alter table 语句加上索引；
2. 执行主备切换；
3. 这时候主库是 B，备库是 A。在 A 上执行 set sql_log_bin=off，然后执行 alter table 语句加上索引。

当然，在平时做变更的时候，更好的是采用gh-ost这样的方案，但紧急处理是，上面的方案更高效。

#### SQL语句没写好

采用query_rewrite功能改写SQL语句。

```sql

mysql> insert into query_rewrite.rewrite_rules(pattern, replacement, pattern_database) values ("select * from t where id + 1 = ?", "select * from t where id = ? - 1", "db1");

call query_rewrite.flush_rewrite_rules();
```

#### MySql选错了索引

应急方案是加上**force index**。

在实际情况中，索引没设计好和SQL语句写得不够好是常见的问题。在测试环境中，模拟线上环境，通过慢查询日志的输出，查找存在问题的SQL。特别注意Rows_examined字段是否与预期一致。如果是全量的回归测试，可以采用工具**pt-query-digest**（https://www.percona.com/doc/percona-toolkit/3.0/pt-query-digest.html)）帮助检查所有SQL语句的返回结果。

### QPS突增问题

新功能BUG引起的QPS突增。

1. 如果有白名单，把引发问题的客户端从白名单去掉；
2. 如果是单独的用户，将用户删除；
3. 如果新功能和其他功能部署在一起，可以采用SQL重新的方式，将引发问题的SQL重写为“select 1”返回。
   - 如果其他功能也用到这个SQL，可能会误伤；
   - 业务功能可能有多个SQL实现，单独重新一个SQL，会导致后面的业务逻辑失败。

**方案1和方案2都要依赖规范的运维体系：虚拟化、白名单、业务账号分离。**

## 数据

### redo log



### bin log

### 保证数据不丢失

#### binlog写入机制

事务执行过程中，先将日志写入binlog cache，事务提交时，再将binlog cache写入binlog日志文件中。一个事务的binlog是不可拆分的。无论事务有多大，都要确保一次性写入。系统给每一个线程分配一块binlog cache内存，由binlog_cache_size控制大小，超过该大小就要暂存到磁盘。事务提交的时候，执行器把 binlog cache 里的完整事务写入到 binlog 中，并清空 binlog cache。

<img src="https://static001.geekbang.org/resource/image/9e/3e/9ed86644d5f39efb0efec595abb92e3e.png" alt="img" style="zoom: 67%;" />

每个线程有自己 binlog cache，但是共用同一份 binlog 文件。

+ 图中的 write，指的就是指把日志写入到文件系统的 page cache，并没有把数据持久化到磁盘，所以速度比较快。
+ 图中的 fsync，才是将数据持久化到磁盘的操作。一般情况下，我们认为 fsync 才占磁盘的 IOPS。

write 和 fsync 的时机，是由参数 sync_binlog 控制的：

+ sync_binlog=0 的时候，表示每次提交事务都只 write，不 fsync；
+ sync_binlog=1 的时候，表示每次提交事务都会执行 fsync；
+ sync_binlog=N(N>1) 的时候，表示每次提交事务都 write，但累积 N 个事务后才 fsync。

出现I/O瓶颈的场景，可以讲sync_binlog设置成较大值，可以提升写入性能。但是，同样存在一定的风险，如果服务器发生异常重启，会丢失最近N个事务的binlog。

#### redolog写入机制

redo log可能存在三种状态，对应如下图：

<img src="https://static001.geekbang.org/resource/image/9d/d4/9d057f61d3962407f413deebc80526d4.png" alt="img" style="zoom:67%;" />

这三种状态分别是：

+ 存在 redo log buffer 中，物理上是在 MySQL 进程内存中，就是图中的红色部分；
+ 写到磁盘 (write)，但是没有持久化（fsync)，物理上是在文件系统的 page cache 里面，也就是图中的黄色部分；
+ 持久化到磁盘，对应的是 hard disk，也就是图中的绿色部分。

日志写到redo log buffer的速度很快，write到page cache的速度也很快，但是fsync到磁盘的速度就慢很多。

为了控制 redo log 的写入策略，InnoDB 提供了 innodb_flush_log_at_trx_commit 参数，它有三种可能取值：

+ 设置为 0 的时候，表示每次事务提交时都只是把 redo log 留在 redo log buffer 中 ;
+ 设置为 1 的时候，表示每次事务提交时都将 redo log 直接持久化到磁盘；
+ 设置为 2 的时候，表示每次事务提交时都只是把 redo log 写到 page cache。

InnoDB后台线程每隔1秒会将redo log buffer中的日志，调用write写到系统page cache，然后调用fsync持久化到磁盘。**事务执行中间过程的redo log也是直接写到redo log buffer的，这些redo log也会被后台线程一并持久化到磁盘。**

实际上，除了后台线程每秒一次的轮询操作外，还有两种场景会让一个没有提交的事务的 redo log 写入到磁盘中。

+ 一种是，redo log buffer 占用的空间即将达到 innodb_log_buffer_size 一半的时候，后台线程会主动写盘。**注意，由于这个事务并没有提交，所以这个写盘动作只是 write，而没有调用 fsync，也就是只留在了文件系统的 page cache**。
+ 另一种是，并行的事务提交的时候，顺带将这个事务的 redo log buffer 持久化到磁盘。假设一个事务 A 执行到一半将部分redo log写到buffer中，这时候有另外一个线程的事务B提交，如果 innodb_flush_log_at_trx_commit 设置的是 1，那么按照这个参数的逻辑，事务 B 要把 redo log buffer 里的日志全部持久化到磁盘。这时候，就会带上事务A在redo log buffer中的日志一起持久化到磁盘。

**两阶段提交的时候说过，时序上 redo log 先 prepare， 再写 binlog，最后再把 redo log commit。**

如果把innodb_flush_log_at_trx_commit设置成 1，那么redo log在prepare阶段就要持久化一次，因为有一个崩溃恢复逻辑是要依赖于prepare的redo log，再加上binlog来恢复的。每秒一次后台轮询刷盘，再加上崩溃恢复逻辑，InnoDB就认为redo log在 commit 的时候就不需要fsync了，只会write到文件系统的page cache中就够了。通常说MySQL的“**双 1**”配置，就是sync_binlog和innodb_flush_log_at_trx_commit都设置成 1。也就是，一个事务完整提交前，需要等待两次刷盘，一次是redo log（prepare阶段），一次是 binlog。

> 把线上生产库设置成“**非双1**”的业务场景
>
> + 业务高峰期。一般如果有预知的高峰期，DBA 会有预案，把主库设置成“非双 1”。
> + 备库延迟，为了让备库尽快赶上主库。
> + 用备份恢复主库的副本，应用 binlog 的过程，这个跟上一种场景类似。
> + 批量导入数据的时候。
>
> 一般情况下，把生产库改成“非双 1”配置，是设置 innodb_flush_logs_at_trx_commit=2、sync_binlog=1000。

#### 组提交机制

按上面提到的事务提交逻辑，如果MySQL的TPS为两万的话，每秒就会写四万次磁盘，但实际上只会写两万次左右，这就是因为组提交机制。

日志逻辑序列号（log sequence number，LSN）的概念。LSN 是单调递增的，用来对应 redo log 的每一个写入点。每次写入长度为length的 redo log，LSN的值就会加上length。LSN 也会写到InnoDB的数据页中，来确保数据页不会被多次执行重复的redo log。

如图所示，是三个并发事务 (trx1, trx2, trx3) 在 prepare 阶段，都写完redo log buffer，持久化到磁盘的过程，对应的LSN分别是50、120和160。

<img src="https://static001.geekbang.org/resource/image/93/cc/933fdc052c6339de2aa3bf3f65b188cc.png" alt="img" style="zoom: 50%;" />

从图中可以看到：

+ trx1是第一个到达的，会被选为这组的leader；
+ 当trx1要开始写盘的时候，这个组里面已经有了三个事务，这时候LSN也变成了160；
+ trx1去写盘的时候，带的就是LSN=160，因此等trx1返回时，所有LSN小于等于160的redo log，都被持久化到磁盘；
+ 这时trx2和trx3就可以直接返回。

在并发更新场景下，第一个事务写完redo log buffer 以后，接下来这个fsync越晚调用，组员可能越多，节约IOPS的效果就越好。为了让一次fsync带的组员更多，MySQL 有一个很有趣的优化：**拖时间**。如下图所示：

<img src="https://static001.geekbang.org/resource/image/5a/28/5ae7d074c34bc5bd55c82781de670c28.png" alt="img" style="zoom:50%;" />

上图是两阶段提交的分解版：

+ 先把binlog从binlog cache中写到磁盘上的binlog（page cache）文件；
+ 调用fsync持久化。MySQL为了让组提交的效果更好，把redo log做fsync的时间拖到步骤 1 之后。

binlog 也可以组提交。在执行图中第4步把binlog fsync到磁盘时，如果有多个事务的 binlog 已经写完，也一起持久化，这样也可以减少 IOPS 的消耗。通常情况下第 3 步执行得会很快，所以binlog的write和fsync间的间隔时间短，导致能集合到一起持久化的 binlog 比较少，因此 binlog 的组提交的效果通常不如 redo log 的效果好。

提升 binlog 组提交效果，设置 **binlog_group_commit_sync_delay** 和 binlog_group_commit_sync_no_delay_count 实现。

+ **binlog_group_commit_sync_delay** 参数，表示延迟多少微秒后才调用 fsync;
+ **binlog_group_commit_sync_no_delay_count** 参数，表示累积多少次以后才调用 fsync。

这两个条件是或的关系，只要有一个满足条件就会调用 fsync。所以，当binlog_group_commit_sync_delay设置为 0 ，binlog_group_commit_sync_no_delay_count也无效。WAL机制是减少磁盘写，WAL 机制主要得益于两个方面：

+ redo log和binlog都是顺序写，磁盘的顺序写比随机写速度要快；
+ 组提交机制，可以大幅度降低磁盘的 IOPS 消耗。

如果MySQL出现性能瓶颈，而且瓶颈在IO上，可以考虑以下三种方法：

+ 设置 binlog_group_commit_sync_delay 和 binlog_group_commit_sync_no_delay_count 参数，减少 binlog 的写盘次数。这个方法是基于“额外的故意等待”来实现的，因此可能会增加语句的响应时间，但没有丢失数据的风险。
+ 将 sync_binlog 设置为大于 1 的值（比较常见是 100~1000）。存在风险：主机掉电时会丢 binlog 日志。
+ 将 innodb_flush_log_at_trx_commit 设置为 2。存在风险：主机掉电的时候会丢数据。

不建议把innodb_flush_log_at_trx_commit设置成 0。因为把这个参数设置成0，表示redo log只保存在内存中，MySQL 本身异常重启也会丢数据。而redo log写到文件系统的page cache的速度也很快，将这个参数设置成2跟设置成0其实性能差不多，但这样做 MySQL 异常重启时就不会丢数据了，相比之下风险会更小。

## 高可用

### 主备一致

完整的主备同步流程如下图：

<img src="https://static001.geekbang.org/resource/image/a6/a3/a66c154c1bc51e071dd2cc8c1d6ca6a3.png" alt="img" style="zoom: 50%;" />

备库 B 跟主库 A 之间维持了一个长连接。主库 A 内部有一个线程，专门用于服务备库 B 的这个长连接。

一个事务日志同步的完整过程是这样的：

1. 在备库 B 上通过 change master 命令，设置主库 A 的 IP、端口、用户名、密码，以及要从哪个位置开始请求 binlog，这个位置包含文件名和日志偏移量。
2. 在备库 B 上执行 start slave 命令，这时候备库会启动两个线程，就是图中的 io_thread 和 sql_thread。其中 io_thread 负责与主库建立连接。
3. 主库 A 校验完用户名、密码后，开始按照备库 B 传过来的位置，从本地读取 binlog，发给 B。
4. 备库 B 拿到 binlog 后，写到本地文件，称为中转日志（relay log）。
5. sql_thread 读取中转日志，解析出日志里的命令，并执行。

这里需要说明，后来由于多线程复制方案的引入，sql_thread 演化成为了多个线程。

binlog 有两种格式，一种是 statement，一种是 row。可能在其他资料上还会看到第三种格式，叫作 mixed，其实就是前两种格式的混合。

mixed格式存在的场景：

+ 因为有些 statement 格式的 binlog 可能会导致主备不一致，所以要使用 row 格式。
+ row格式的缺点是，很占空间。比如用一个delete语句删掉 10 万行数据，用 statement就是一个 SQL 语句被记录到binlog中，占用几十个字节的空间。但用row格式，就要把这 10 万条记录写到binlog中。不仅会占用更大的空间，同时写binlog也要耗费IO资源，影响执行速度。
+ 所以，MySQL就取了个折中方案，就是mixed格式。mixed格式的意思是，MySQL自己会判断这条SQL语句是否可能引起主备不一致，如果有可能，就用row格式，否则就用statement格式。

#### 循环复制

生产环境中使用双M结构比较多，图示为双M结构主备切换流程：

<img src="https://static001.geekbang.org/resource/image/20/56/20ad4e163115198dc6cf372d5116c956.png" alt="img" style="zoom:50%;" />

可以用下面的逻辑，来解决两个节点间的循环复制的问题：

+ 规定两个库的 server id 必须不同，如果相同，则它们之间不能设定为主备关系；
+ 一个备库接到 binlog 并在重放的过程中，生成与原 binlog 的 server id 相同的新的 binlog；
+ 每个库在收到从自己的主库发过来的日志后，先判断 server id，如果跟自己的相同，表示这个日志是自己生成的，就直接丢弃这个日志。

按照这个逻辑，如果我们设置了双 M 结构，日志的执行流就会变成这样：

+ 从节点 A 更新的事务，binlog 里面记的都是 A 的 server id；
+ 传到节点 B 执行一次以后，节点 B 生成的 binlog 的 server id 也是 A 的 server id；
+ 再传回给节点 A，A 判断到这个 server id 与自己的相同，就不会再处理这个日志

#### 备库延迟

同步有关的时间点主要包括以下三个：

1. 主库A执行完成一个事务，写入binlog，把这个时刻记为T1;
2. 之后传给备库B，把备库 B 接收完这个binlog的时刻记为T2;
3. 备库B执行完成这个事务，把这个时刻记为T3。

所谓主备延迟，就是同一个事务，在备库执行完成的时间和主库执行完成的时间之间的差值，也就是 **T3-T1**。

备库执行show slave status命令，返回的结果显示seconds_behind_master，表示备库延迟时间，精确到秒。每个事务的binlog里面有一个时间字段，记录主库写入时间，备库取出正在执行的事务的时间字段，计算它与当前系统时间的差值得到seconds_behind_master的值。主备库系统时间不同的情况，计算seconds_behind_master时会自动扣掉这个差值。

在网络正常的时候，日志从主库传到备库的时间很短，即T2-T1的值很小，所以，在网络正常的情况下，主备延迟主要是因为备库接收完binlog和执行完这个事务之间的时间差。**主备延迟最直接的表现是，备库消费中转日志（relay log）的速度，比主库生产binlog的速度要慢。**

##### 主备延迟的来源

1. 备库的硬件性能没有主库的硬件性能好。
2. 备库压力大
   + 一主多从。除了备库外，可以多接几个从库，让这些从库来分担读的压力。
   + 通过 binlog 输出到外部系统，比如 Hadoop 这类系统，让外部系统提供统计类查询的能力。
3. 大事务、大表的DDL
   + 将大事务拆分成多个小事务。
4. 备库的并行复制能力

#### 并行复制

### 高可用保证

#### 可靠性优先策略

双 M 结构下，从状态 1 到状态 2 切换的详细过程是这样的：

1. 判断备库 B 现在的 seconds_behind_master，如果小于某个值（比如 5 秒）继续下一步，否则持续重试这一步；
2. 把主库 A 改成只读状态，即把 readonly 设置为 true；
3. 判断备库 B 的 seconds_behind_master 的值，直到这个值变成 0 为止；
4. 把备库 B 改成可读写状态，也就是把 readonly 设置为 false；
5. 把业务请求切到备库 B。

<img src="https://static001.geekbang.org/resource/image/54/4a/54f4c7c31e6f0f807c2ab77f78c8844a.png" alt="img" style="zoom:50%;" />

假设，主库A和备库B间的主备延迟是30分钟，这时候主库A掉电了，HA系统要切换B作为主库。在主动切换的时候，可以等到主备延迟小于5秒的时候再启动切换，但这时候已经别无选择了。

采用可靠性优先策略，就必须得等到备库B的seconds_behind_master=0之后，才能切换。在**异常切换**的情况比刚刚更严重，并不是系统只读、不可写的问题了，而是系统处于完全不可用的状态。因为，主库A掉电后，连接还没有切到备库B。

那能不能直接切换到备库 B，但是保持 B 只读呢？这样也不行。因为，这段时间内，relay log还没有应用完成，如果直接发起主备切换，客户端查询看不到之前执行完成的事务，会认为有“数据丢失”。虽然随着中转日志的继续应用，这些数据会恢复回来，但是对于一些业务来说，查询到“暂时丢失数据的状态”也是不能被接受的。

#### 可用性优先策略

如果我强行把步骤 4、5 调整到最开始执行，也就是说不等主备数据同步，直接把连接切到备库 B，并且让备库 B 可以读写，那么系统几乎就没有不可用时间了。把这个切换流程，暂时称作可用性优先流程。这个切换流程的代价，就是可能出现数据不一致的情况。

假设，现在主库上其他的数据表有大量的更新，导致主备延迟达到 5 秒。在插入一条 c=4 的语句后，发起了主备切换。

**可用性优先策略，且 binlog_format=mixed 时的切换流程和数据结果。**

<img src="https://static001.geekbang.org/resource/image/37/3a/3786bd6ad37faa34aca25bf1a1d8af3a.png" alt="img" style="zoom: 67%;" />

主库 A 和备库 B 上出现了两行不一致的数据。可以看到，这个数据不一致，是由可用性优先流程导致的。

**用可用性优先策略，但设置 binlog_format=row切换流程和数据结果**。

<img src="https://static001.geekbang.org/resource/image/b8/43/b8d2229b2b40dd087fd3b111d1bdda43.png" alt="img" style="zoom:67%;" />

可以得出一些结论：

+ 使用 row 格式的 binlog 时，数据不一致的问题更容易被发现。而使用 mixed 或者 statement 格式的 binlog 时，数据很可能悄悄地就不一致了。如果你过了很久才发现数据不一致的问题，很可能这时的数据不一致已经不可查，或者连带造成了更多的数据逻辑不一致。
+ 主备切换的可用性优先策略会导致数据不一致。因此，大多数情况下，使用可靠性优先策略。毕竟对数据服务来说的话，数据的可靠性一般还是要优于可用性的。

#### 主备切换问题

​		虚线箭头表示的是主备关系，也就是 A 和 A’互为主备， 从库 B、C、D 指向的是主库 A。一主多从的设置，一般用于读写分离，主库负责所有的写入和一部分读，其他的读请求则由从库分担。

<img src="E:\data\my-document\kafka\assets\aadb3b956d1ffc13ac46515a7d619e79.png" alt="img" style="zoom: 50%;" />

##### 基于位点的主备切换

把节点 B 设置成节点 A’的从库的时候，需要执行一条 change master 命令：

```shell
CHANGE MASTER TO 
MASTER_HOST=$host_name 
MASTER_PORT=$port 
MASTER_USER=$user_name 
MASTER_PASSWORD=$password 
MASTER_LOG_FILE=$master_log_name 
MASTER_LOG_POS=$master_log_pos  
```

+ MASTER_HOST、MASTER_PORT、MASTER_USER 和 MASTER_PASSWORD 四个参数，分别代表了主库 A’的 IP、端口、用户名和密码。
+ 最后两个参数 MASTER_LOG_FILE 和 MASTER_LOG_POS 表示，要从主库的 master_log_name 文件的 master_log_pos 这个位置的日志继续同步。而这个位置就是我们所说的同步位点，也就是主库对应的文件名和日志偏移量。

​       原来节点 B 是 A 的从库，本地记录的也是 A 的位点。但是相同的日志，A 的位点和 A’的位点是不同的。因此，从库 B 要切换的时候，就需要先经过“**找同步位点**”的逻辑。这个位点很难精确取到，只能取一个大概位置。考虑到切换过程中不能丢数据，所以找位点的时候，总是要找一个“稍微往前”的，然后再通过判断跳过那些在从库 B 上已经执行过的事务。

找同步点位的方法如下：

1. 等待新主库 A’把中转日志（relay log）全部同步完成；

2. 在 A’上执行 show master status 命令，得到当前 A’上最新的 File 和 Position；

3. 取原主库 A 故障的时刻 T；

4. 用 mysqlbinlog 工具解析 A’的 File，得到 T 时刻的位点。

   ```shell
   mysqlbinlog File --stop-datetime=T --start-datetime=T
   ```

   ![img](https://static001.geekbang.org/resource/image/34/dd/3471dfe4aebcccfaec0523a08cdd0ddd.png)

​        图中，end_log_pos 后面的值“123”，表示的就是 A’这个实例，在 T 时刻写入新的 binlog 的位置。然后，就可以把 123 这个值作为 $master_log_pos ，用在节点 B 的 change master 命令里。

​	**这个值是不精确的。**设想有这么一种情况，假设在 T 这个时刻，主库 A 已经执行完成了一个 insert 语句插入了一行数据 R，并且已经将 binlog 传给了 A’和 B，然后在传完的瞬间主库 A 的主机就掉电了。

这时候系统的状态是这样的：

1. 在从库 B 上，由于同步了 binlog， R 这一行已经存在；
2. 在新主库 A’上， R 这一行也已经存在，日志是写在 123 这个位置之后的；
3. 我们在从库 B 上执行 change master 命令，指向 A’的 File 文件的 123 位置，就会把插入 R 这一行数据的 binlog 又同步到从库 B 去执行。

​        这时候，从库 B 的同步线程就会报告 Duplicate entry ‘id_of_R’ for key ‘PRIMARY’ 错误，提示出现了主键冲突，然后停止同步。

**通常情况下，在切换任务的时候，要先主动跳过这些错误，有两种常用的方法。**

+ 主动跳过一个事务

  ```shell
  set global sql_slave_skip_counter=1;
  start slave;
  ```

  切换过程中，可能会不止重复执行一个事务，所以需要在从库 B 刚开始接到新主库 A’时，持续观察，每次碰到这些错误就停下来，执行一次跳过命令，直到不再出现停下来的情况，以此来跳过可能涉及的所有事务。

+ 通过设置 slave_skip_errors 参数，直接设置跳过指定的错误

  在执行主备切换时，有这么两类错误，是经常会遇到的：

  + 1062 错误是插入数据时唯一键冲突；
  + 1032 错误是删除数据时找不到行。

  因此，可以把 slave_skip_errors 设置为 “1032,1062”，这样中间碰到这两个错误时就直接跳过。这里需要注意的是，这种直接跳过指定错误的方法，针对的是主备切换时，由于找不到精确的同步位点，所以只能采用这种方法来创建从库和新主库的主备关系。这个背景是，我们很清楚在主备切换过程中，直接跳过 1032 和 1062 这两类错误是无损的，所以才可以这么设置 slave_skip_errors 参数。等到主备间的同步关系建立完成，并稳定执行一段时间之后，需要把这个参数设置为空，以免真的出现了主从数据不一致，也跳过了。

##### GTID

MySQL 5.6 版本引入了 GTID，解决主库切换后数据同步的问题。GTID 全称是 **Global Transaction Identifier**，也就是全局事务 ID，是一个事务在提交的时候生成的，是这个事务的唯一标识。它由两部分组成，格式是：

```shell
GTID=server_uuid:gno
```

+ server_uuid 是一个实例第一次启动时自动生成的，是一个全局唯一的值；
+ gno 是一个整数，初始值是 1，每次提交事务的时候分配给这个事务，并加 1。

需要说明一下，在 MySQL 的官方文档里，GTID 格式是这么定义的：

```shell
GTID=source_id:transaction_id
```

这里的 source_id 就是 server_uuid；而后面的这个 transaction_id和事务 id 不是一回事。在 MySQL 里面transaction_id 就是指事务 id，事务 id 是在事务执行过程中分配的，如果这个事务回滚了，事务 id 也会递增，而 gno 是在事务提交的时候才会分配。

在 GTID 模式下，每个事务都会跟一个 GTID 一一对应。这个 GTID 有两种生成方式，而使用哪种方式取决于 session 变量 gtid_next 的值。

1. 如果 gtid_next=automatic，代表使用默认值。这时，MySQL 就会把 server_uuid:gno 分配给这个事务。

   a.  记录 binlog 的时候，先记录一行 SET @@SESSION.GTID_NEXT=‘server_uuid:gno’;

   b.  把这个 GTID 加入本实例的 GTID 集合。

2. 如果 gtid_next 是一个指定的 GTID 的值，比如通过 set gtid_next='current_gtid’指定为 current_gtid，那么就有两种可能：

   a.  如果 current_gtid 已经存在于实例的 GTID 集合中，接下来执行的这个事务会直接被系统忽略；

   b.  如果 current_gtid 没有存在于实例的 GTID 集合中，就将这个 current_gtid 分配给接下来要执行的事务，也就是说系统不需要给这个事务生成新的 GTID，因此 gno 也不用加 1。

注意，一个 current_gtid 只能给一个事务使用。这个事务提交后，如果要执行下一个事务，就要执行 set 命令，把 gtid_next 设置成另外一个 gtid 或者 automatic。这样，每个 MySQL 实例都维护了一个 GTID 集合，用来对应“这个实例执行过的所有事务”。

用一个简单的例子，来和你说明 GTID 的基本用法：

```sql
CREATE TABLE `t` (
  `id` int(11) NOT NULL,
  `c` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

insert into t values(1,1);
```

![img](https://static001.geekbang.org/resource/image/28/c2/28a5cab0079fb12fd5abecd92b3324c2.png)

可以看到，事务的 BEGIN 之前有一条 SET @@SESSION.GTID_NEXT 命令。这时，如果实例 X 有从库，那么将 CREATE TABLE 和 insert 语句的 binlog 同步过去执行，执行事务之前就会先执行这两个 SET 命令， 这样被加入从库的 GTID 集合的，就是图中的这两个 GTID。

假设，现在这个实例 X 是另外一个实例 Y 的从库，并且此时在实例 Y 上执行了下面这条插入语句：

```sql
insert into t values(1,1);
```

并且，这条语句在实例 Y 上的 GTID 是 “aaaaaaaa-cccc-dddd-eeee-ffffffffffff:10”。那么，实例 X 作为 Y 的从库，就要同步这个事务过来执行，显然会出现主键冲突，导致实例 X 的同步线程停止。这时，可以执行下面的这个语句序列：

```shell
set gtid_next='aaaaaaaa-cccc-dddd-eeee-ffffffffffff:10';
begin;
commit;
set gtid_next=automatic;
start slave;
```

其中，前三条语句的作用，是通过提交一个空事务，把这个 GTID 加到实例 X 的 GTID 集合中。如图所示，就是执行完这个空事务之后的 show master status 的结果。

![img](https://static001.geekbang.org/resource/image/c8/57/c8d3299ece7d583a3ecd1557851ed157.png)

可以看到实例 X 的 Executed_Gtid_set 里面，已经加入了这个 GTID。

这样，再执行 start slave 命令让同步线程继续执行，虽然实例 X 上还是会继续执行实例 Y 传过来的事务，但是由于“aaaaaaaa-cccc-dddd-eeee-ffffffffffff:10”已经存在于实例 X 的 GTID 集合中了，所以实例 X 就会直接跳过这个事务，也就不会再出现主键冲突的错误。

在上面的这个语句序列中，start slave 命令之前还有一句 set gtid_next=automatic。这句话的作用是“恢复 GTID 的默认分配行为”，也就是说如果之后有新的事务再执行，就还是按照原来的分配方式，继续分配 gno=3。

##### 基于 GTID 的主备切换

在 GTID 模式下，备库 B 要设置为新主库 A’的从库的语法如下：

```shell
CHANGE MASTER TO 
MASTER_HOST=$host_name 
MASTER_PORT=$port 
MASTER_USER=$user_name 
MASTER_PASSWORD=$password 
master_auto_position=1 
```

其中，master_auto_position=1 就表示这个主备关系使用的是 GTID 协议。可以看到，前面基于位点主备切换的 MASTER_LOG_FILE 和 MASTER_LOG_POS 参数，已经不需要指定了。

##### GTID 和在线 DDL

### 读写分离

## 临时表

<img src="E:\data\my-document\kafka\assets\3cbb2843ef9a84ee582330fb1bd0d6e3.png" alt="img" style="zoom: 50%;" />

临时表的特点：

1. 建表语法是 create temporary table …。
2. 临时表只能被创建它的 session 访问，对其他线程不可见。所以，图中 session A 创建的临时表 t，对于 session B 就是不可见的。
3. 临时表可以与普通表同名。
4. session A 内有同名的临时表和普通表的时候，show create 语句，以及增删改查语句访问的是临时表。
5. show tables 命令不显示临时表。

**临时表就特别适合BLJ（Block Nest-Loop Join）的 join 优化这种场景。**主要原因如下：

1. 不同 session 的临时表是可以重名的，如果有多个 session 同时执行 join 优化，不需要担心表名重复导致建表失败的问题。不需要担心数据删除问题。
2. 如果使用普通表，在流程执行过程中客户端发生了异常断开，或者数据库发生异常重启，还需要专门来清理中间过程中生成的数据表。而临时表由于会自动回收，所以不需要这个额外的操作。

### 临时表的应用

临时表经常被用作复杂查询的优化过程。分库分表系统的跨库查询是一个典型的应用场景。例如，将一个大表 ht，按照字段 f，拆分成 1024 个分表，然后分布到 32 个数据库实例上。

<img src="E:\data\my-document\kafka\assets\ddb9c43526dfd9b9a3e6f8c153478181.jpg" alt="img" style="zoom:50%;" />

这种分库分表系统通常都有一个中间层 proxy，也有一些方案会让客户端直连数据库。在这个架构中，分区 key 的选择是以“**减少跨库和跨表查询**”为依据。如果大部分的语句都会包含 f 的等值条件，那么就要用 f 做分区键。这样，在 proxy 层解析完 SQL 语句，就能确定将这条语句路由到哪个分表做查询。例如下面的语句：

```sql
select v from ht where f=N;
```

可以通过分表规则（比如，N%1024) 来确认应该路由到哪个分表。但是，如果这个表上还有另一个索引 k，并且查询语句是这样的：

```sql
select v from ht where k >= M order by t_modified desc limit 100;
```

由于查询条件里面没有用到分区字段 f，只能到所有的分区中去查找满足条件的所有数据，然后统一做 order by 的操作。这种场景有两种解决方案：

1. 在proxy层代码实现排序

   这种方式的优势是处理速度快，拿到分库的数据以后，直接在内存中参与计算。这个方案的缺点也比较明显：

   + 需要的开发工作量比较大。如果涉及到复杂的操作，比如 group by，甚至 join 这样的操作，对中间层的开发能力要求比较高；
   + 对 proxy 端的压力比较大，尤其是很容易出现内存不够用和 CPU 瓶颈的问题。

2. 把分库分表的数据汇总到一个数据库实例中，实现逻辑操作

   <img src="E:\data\my-document\kafka\assets\f5ebe0f5af37deeb4d0b63d6fb11fc0d.jpg" alt="img" style="zoom:50%;" />

在实践中，通常发现每个分库的计算量都不饱和，所以会直接把临时表 temp_ht 放到 32 个分库中的某一个上。

# 常用工具及命令

**查看连接状态：**show processlist

**重建表：**alter table T engine=InnoDB

**查看数据库状态**：show engine innodb status\G（查看死锁等信息）

**设置事务隔离级别：**SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ

**立即开启事务并确保获得一致性视图：**START TRANSACTION  WITH CONSISTENT SNAPSHOT

**设置保存点：**SAVEPOINT sp

**回滚到保存点：**ROLLBACK TO SAVEPOINT sp

**查看索引：**show index from t

**重新统计索引：**analyze table t

**binlog查看命令**： show binlog events in 'master.000001';

**binlog查看工具：**mysqlbinlog --no-defaults -v -v --base64-output=DECODE-ROWS mysql-bin.000010 | tail -n 20

**恢复数据：**mysqlbinlog master.000001  --start-position=2738 --stop-position=2973 | mysql -h127.0.0.1 -P13000 -u$user -p$pwd;

# 重要参数

**连接超时时间：**wait_timeout 默认8小时

**查询缓存开启类型：**query_cache_type=DEMAND（按需开启缓存）

**每次事务的redo log都直接持久化到磁盘**：innodb_flush_log_at_trx_commit=1（保证异常重启后数据不丢失）

**每次事务的binlog都持久化到磁盘：**sync_binlog=1（保证异常重启后binlog不丢失）

**事务提交级别：**transaction-isolation（READ-COMMITTED、REPEATABLE-READ）

**设置数据库只读：**set global readonly=true

**锁等待超时时间：**innodb_lock_wait_timeout（默认50s）

**死锁检测：**innodb_deadlock_detect=on

**Change Buffer：**innodb_change_buffer_max_size=50（占用buffer pool 50%的空间）

**存储索引（基数）统计方式：**innodb_stats_persistent

+ 默认选择N个数据页统计，当变更的数据行数超过1/M时触发。
+ on：统计信息持久化存储，默认N是20，M是10。
+ off：统计信息存储在内存，默认N是8， M是16。

**磁盘IO能力：**innodb_io_capacity=IOPS

**脏页比例：**innodb_max_dirty_pages_pct（默认75%）

**实际脏页比例计算：**innodb_buffer_pool_pages_dirty/innodb_buffer_pool_pages_total

>mysql> select VARIABLE_VALUE into @a from global_status where VARIABLE_NAME = 'Innodb_buffer_pool_pages_dirty';
>select VARIABLE_VALUE into @b from global_status where VARIABLE_NAME = 'Innodb_buffer_pool_pages_total';
>select @a/@b;

**刷新相邻脏页：**innodb_flush_neighbors（1：刷新 0：不刷新，Mysql8.0默认为0。注意：使用传统机械硬盘时设置为1可以提升效率，SSD等快速磁盘应设置为0）

**数据文件：**innodb_file_per_table（Mysql5.8默认ON，设置为ON，drop table时直接删除对应数据文件，回收空间；如果设置为OFF，不会回收空间）

**排序缓冲：**sort_buffer_size

**排序行长度控制：**max_length_for_sort_data

**内存临时表大小：**tmp_table_size（默认16M）

**磁盘临时表引擎：**internal_tmp_disk_storage_engine（默认InnoDB）

**binlog cache大小配置：**binlog_cache_size

**bin log日志write和fsync的时机：**

> 1、sync_binlog=0 的时候，表示每次提交事务都只 write，不 fsync；
>
> 2、sync_binlog=1 的时候，表示每次提交事务都会执行 fsync；
>
> 3、sync_binlog=N(N>1) 的时候，表示每次提交事务都 write，但累积 N 个事务后才 fsync。

**redo log 写入策略：**innodb_flush_log_at_trx_commit

> 1、设置为 0 的时候，表示每次事务提交时都只是把 redo log 留在 redo log buffer 中 ;
>
> 2、设置为 1 的时候，表示每次事务提交时都将 redo log 直接持久化到磁盘；
>
> 3、设置为 2 的时候，表示每次事务提交时都只是把 redo log 写到 page cache。

**延迟多少微秒后才调用 fsync**：binlog_group_commit_sync_delay 

**累积多少次以后才调用 fsync**：binlog_group_commit_sync_no_delay_count 

**备库执行relay log后生成bin log：**log_slave_updates=on

**GTID 模式的启动**：gtid_mode=on 和 enforce_gtid_consistency=on

**限制并发线程数（并发查询）：**innodb_thread_concurrency 默认为0，表示不限制

**限制SQL的安全性：**sql_safe_updates=on

semi-consistent

## 工具

### 查看binlog日志：

> mysqlbinlog  -vv /var/lib/mysql/logs/mysql-bin.000013 --start-position=423;

### 重放binlog日志：

> mysqlbinlog master.000001  --start-position=2738 --stop-position=2973 | mysql -h127.0.0.1 -P13000 -u$user -p$pwd;

# 系统命令

### 磁盘

**测试磁盘IOPS**： fio -filename=$filename -direct=1 -iodepth 1 -thread -rw=randrw -ioengine=psync -bs=16k 							-size=500M -numjobs=10 -runtime=10 -group_reporting -name=mytest 

