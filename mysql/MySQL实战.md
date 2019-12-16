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

  ​	Binlog有两种模式，statement 格式的话是记sql语句， row格式会记录行的内容，记两条，更新前和更新后都有。

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
   + 如果是，则提交事务；
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

​	事务就是要保证一组数据库操作，要么全部成功，要么全部失败。事务支持是在存储引擎层实现的。

+ **隔离性和隔离级别**

  + MySQL默认隔离级别“可重复读”，Oracle默认隔离级别“提交读”。
  + show variables like 'transaction-isolation';查看事务隔离级别。

+ **事务隔离的实现（可重复读隔离级别）**

  + MySQL每一个更新操作的同时，会记录一个回滚操作。回滚段示意如下（执行顺序1->2->3->4）：

    ![1551011937733](E:\kaikeba\wjy\mysql\assets\1551011937733.png)

  + 不同时刻启动的事务，会有不同的read-view；同一条记录在系统中存在多个版本，就是数据库的多版本并发控制（MVCC）。

  + 回滚段在系统判定不需要的时候，会删除回滚段（当系统中没有比这个回滚段更新的read-view的时候）。

  + 如果存在长事务，意味着系统里存在很多老的事务视图，在这个事务提前之前，它用到的回滚段记录都必须保留，会占用大量存储空间

  + 在MySQL5.5之前，回滚日志跟数据字典一起存放在ibdata文件里面，即使事务提交，回滚段被清理，文件也不会变小。

  + 长事务还会占用锁资源，可能拖垮整个库。

+ **事务的启动方式**

  + 事务的开启方式有两种：

    + 显示启动事务，begin或start transaction，提交语句是commit，回滚语句是rollback。
    + set autocommit=0，关闭自动提交；意味着你只执行一个select，事务就开始了，直到执行commit或rollback，或者断开连接。

  + 在autocommit=1的情况下，执行begin显示开始一个事务，执行commit提交事务。如果执行commit work and chain，则提交事务并启动下一个事务。

  + 可以在infomation_schema库的innodb_trx表中查询长事务：

    ```sql
    select * from information_schema.innodb_trx where TIME_TO_SEC(timediff(now(),trx_started))>60
    ```

+ **如何避免长事务**

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


#### **change buffer的适用场景：**

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
+ 应用场景：做全库逻辑备份
  + 主库执行：主库处于只读状态，业务停摆。
  + 从库执行：从库不能同步主库binlog，存在主从延迟。
+ 通过一致性读来备份数据（Innodb）：mysqldump使用-single-transaction参数启动事务，确保生成一致性视图。

### 表级锁

#### 表锁

+ 语法：lock tables xxx read/write。
+ 表锁对当前线程的下的操作也有限制。

#### 元数据锁

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

加锁阶段和解锁阶段，加锁阶段只加锁，解锁阶段只解锁。只有在事务结束的时候，才解锁。因此，要把最容易引起锁冲突，影响并发度的锁，尽量放到事务的最后执行。

### 死锁和死锁检测

+ 死锁出现的解决策略（一般采用策略2）：
  + 策略1：直接进入等待，直到超时。
  + 策略2：发起死锁检测，发现死锁后，主动回滚死锁链条中的某一个事务，让其他事务继续执行。
+ 采用策略2会有**热点数据更新导致的性能问题**
  + 热点数据更新，每一条线程都要发起死锁检测，导致大量消耗CPU资源。
  + 解决方案：
    + 关闭死锁检测
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

# 常用命令

**查看连接状态：**show processlist

**重建表：**alter table T engine=InnoDB

**查看数据库状态**：show engine innodb status\G（查看死锁等信息）

**设置事务隔离级别：**SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ

**立即开启事务并确保获得一致性视图：**START TRANSACTION  WITH CONSISTENT SNAPSHOT

**设置保存点：**SAVEPOINT sp

**回滚到保存点：**ROLLBACK TO SAVEPOINT sp

**查看索引：**show index from t

**重新统计索引：**analyze table t

**binlog查看：**mysqlbinlog --no-defaults -v -v --base64-output=DECODE-ROWS mysql-bin.000010 | tail -n 20

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

**刷新相邻脏页：**innodb_flush_neighbors（1：刷新 0：不刷新，Mysql5.8默认为0。注意：使用传统机械硬盘时设置为1可以提升效率，SSD等快速磁盘应设置为0）

**数据文件：**innodb_file_per_table（Mysql5.8默认ON，设置为ON，drop table时直接删除对应数据文件，回收空间；如果设置为OFF，不会回收空间）

**排序缓冲：**sort_buffer_size

**排序行长度控制：**max_length_for_sort_data

**内存临时表大小：**tmp_table_size（默认16M）

**磁盘临时表引擎：**internal_tmp_disk_storage_engine（默认InnoDB）

semi-consistent

# 系统命令

### 磁盘

**测试磁盘IOPS**： fio -filename=$filename -direct=1 -iodepth 1 -thread -rw=randrw -ioengine=psync -bs=16k 							-size=500M -numjobs=10 -runtime=10 -group_reporting -name=mytest 

