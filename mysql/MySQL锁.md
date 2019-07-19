# MySQL锁

## 锁的介绍

锁分为：全局锁、表级锁、页级锁和行级锁，全局锁和表级锁是由server层提供的机制，所有存储引擎共用。

全局锁：锁整个database，常用场景：数据备份。

表级锁：锁整个table

行级锁：锁一行数据（只有InnoDB和xtradb存储引擎支持）



+ 按锁的粒度：表级锁、行级锁。
+ 按锁的功能：共享写锁、排他写锁。
+ 悲观锁（排他写锁）、乐观锁（通过version字段实现）。

InnoDB和MyISAM的区别就是行锁和事务支持。

## 全局锁

不用控制，server层自动控制加锁。

## 表级锁

+ 表锁

  + 表共享读锁（Table read lock）

  + 表独占写锁（Table write lock）

  + 查看表锁：show status like 'table%'

    ![1551091980200](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1551091980200.png)

    + table_locks_immediate：产生表级锁的次数
    + table_locks_waited：出现表级锁争用而发生等待的次数

+ 元数据锁MDL

  + MySQL5.5引入了MDL，当对一个表执行增删改查操作是，加MDL读锁；当需要对表结构变更时，加MDL写锁。
  + 读锁之间不互斥，读锁和写锁、写锁和写锁之间是互斥的，用来保证变更表结构操作的安全性。
  + MDL锁在语句开始执行时申请，但语句执行完毕以后不会马上释放，需要等到事务提交才会释放。

## 行级锁

+ 共享锁和排他锁都是行锁，意向锁都是表锁，应用中会用到共享锁和排他锁，意向锁是MySQL内部使用的，不需要干预。

+ 对于UPDATE、DELETE、INSERT语句，InnoDB会自动给涉及的数据集加排他锁（X）；对于SELECT语句，可以显示加锁，共享锁（S）：SELECT ... LOCK IN SHARE MODE ，排他锁（X）：SELECT ... FOR UPDATE。

+ InnoDB是通过给索引项加锁来实现的，这意味着：只有通过索引条件检索数据，InnoDB才使用行锁，否则，将使用表锁。

+ 为了让行锁和表锁共存，InnoDB也使用了意向锁概念。意向锁的主要作用是处理行[锁和](https://www.baidu.com/s?wd=%E9%94%81%E5%92%8C&tn=24004469_oem_dg&rsv_dl=gh_pl_sl_csd)表锁之间的矛盾，能够显示“某个事务正在某一行上持有了锁，或者准备去持有锁”。

  |                  | 共享锁（S） | 排他锁（X） | 意向共享锁（IS） | 意向排他锁（IX） |
  | ---------------- | ----------- | ----------- | ---------------- | ---------------- |
  | 共享锁（S）      | 兼容        | 冲突        | 兼容             | 冲突             |
  | 排他锁（X）      | 冲突        | 冲突        | 冲突             | 冲突             |
  | 意向共享锁（IS） | 兼容        | 冲突        | 兼容             | 兼容             |
  | 意向排他锁（IX） | 冲突        | 冲突        | 兼容             | 兼容             |

+ InnoDB行级锁争用状态查看：show status like 'innodb_row_lock%'

  ![1551092492838](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1551092492838.png)

  ```
  innodb_row_lock_current_waits：当前正在等待锁定的数量
  innodb_row_lock_time：从系统启动到现在锁定总时间长度（等待总时长）
  innodb_row_lock_time_avg：每次等待所花的平均时间（平均等待时长）
  innodb_row_lock_time_max：从系统启动到现在等待最长的一次所花时间
  innodb_row_lock_waits：系统启动后到现在总共等待次数（等待总次数）
  ```

  InnoDB锁包括：record lock、GAP锁、next-key lock

  + **record lock**：锁住索引而非记录本身
  + **gap lock**：索引之间的间隙加锁，或者某一条索引记录之前或者之后加锁，不包括索引本身。gap lock的机制主要解决可重复读模式下的幻读问题。
    + 事务隔离级别为REPEATABLE-READ，innodb_locks_unsafe_for_binlog参数为0，且sql走的索引为非唯一索引
    + 事务隔离级别为REPEATABLE-READ，innodb_locks_unsafe_for_binlog参数为0，且sql是一个范围的当前读操作，这时即使不是非唯一索引也会加gap lock
  + **next-key lock**：在默认情况下，mysql的事务隔离级别是可重复读，并innodb_locks_unsafe_for_binlog参数为0，这时默认采用next-key locks。所谓Next-Key Locks，就是Record lock和gap lock的结合，即除了锁住记录本身，还要再锁住索引之间的间隙。

## 加锁原理分析

+ **MVCC和LBCC**

  + MVCC：Multi-Version Concurrency Control，基于多版本的并发控制。
    + 读不加锁，读写不冲突
  + LBCC：Lock-Base Concurrenty Control，基于锁的并发控制。

+ **快照读和当前读**

  + MVCC并发控制中，读操作分为：快照读（snapshot read）与当前读（current read）。
    + 快照读：读取记录的可见版本（可能是历史版本），读不加锁。
      + 普通的select查询属于快照读，不加锁（有例外）
    + 当前读：读取记录的最新版本，并且当前返回的记录都会加锁，保证其他事务不会再修改这条记录。
      + DML语句和select...lock in share mode、select...for update属于当前读。

+ 针对一条当前读的SQL语句，InnoDB与MySQL Server的交互是一条一条进行的，因此，加锁也是一条一条进行的。先对一条满足条件的记录加锁，返回给MySQL Server，做一些DML操作；然后在读取下一条加锁，直到读取完毕。

  ![1551095841032](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1551095841032.png)

+ **Two-Phase Locking**
  + 锁的操作分为两阶段：加锁阶段和解锁阶段，并且保证加锁阶段与解锁阶段不相交。**加锁阶段只加锁，不解锁；解锁阶段只解锁，不加锁**。