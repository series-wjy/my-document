---
typora-root-url: assets
---

# MySQL架构

​	

## 逻辑架构图

![1550649215844](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1550649215844.png)

## 物理架构

**mysql物理结构上分为数据索引文件和日志文件**

### 日志文件（顺序IO）

通过命令查看数据库中日志的使用信息：

```
show variables  like 'log_%';
```

+ 错误日志（err log）

  默认开启，5.5.7版本以后无法关闭错误日志。默认错误日志名称：hostname.err

  log_warnings只能使用0|1定义开关是否启动警告信息

+ 二进制日志（bin log）

  默认关闭，通过以下配置开启：

  ```
  log-bin=mysql-bin
  ```

  ​	其中mysql-bin是binlog日志文件的basename，binlog日志文件的名称：mysql-bin-000001.log。binlog文件记录所有的ddl和dml语句，语句以事件的形式保存，保存了数据的变更顺序，binlog还包括了每个更新语句的执行时间信息。

  ​	如果是ddl语句直接记录到binlog，如果是dml语句，必须通过事务提交以后，才会记录到binlog中。

  ​	binlog主要用作数据恢复、mysql主从复制、数据备份。

+ 通用查询日志（general query log）

  ​	默认关闭，一旦开启所有的用户操作都会写入。建议调试才开启。

+ 慢查询日志（slow query log）

  ​	默认关闭，通过以下配置开启：

  ```
  slow_query_log=on
  long_query_time=10
  ```

+ 事务日志（redo/undo log）

  ​	redo日志文件名为：ib_logfile0和ib_logfile1，默认在表空间目录下

  ​	undo日志存放的事务回滚段的信息

+ 中继日志（relay log）

  ​	是用作主从复制环境中产生的日志，主要作用是为了从机可以从中继日志中获取到主机同步过来的sql语句，然后执行到从机中。

### 数据文件（随机IO）

**查看数据文件：**

```
show variables like '%datadir%';
```

+ InnoDB数据文件
  + .frm文件：主要存放跟表结构定义相关的数据信息
  + .ibd文件：使用独享表空间存储表数据和索引数据，一张表对应一个文件
  + ibdata文件：使用共享表空间存储表数据和索引数据，所有表共享一个或多个ibdata文件
+ MyISAM数据文件
  + .frm文件：主要存放跟表结构定义相关的数据信息
  + .myd文件：主要存储表数据信息
  + .myi文件：主要存储表数据文件中任何索引的数据树