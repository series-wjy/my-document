# kafka核心技术与实战

## 重要参数配置

### Borker端参数

#### 配置存储信息

+ log.dirs：配置多个路径如：/home/kafka1,/home/kafka2,/home/kafka3（最好是挂载到不同的物理磁盘）
  + 提升读写能力
  + 实现故障转移（1.1版本提供自动故障转移，将坏掉的磁盘数据转移到正常磁盘）
+ log.dir：补充log.dirs参数

#### Zookeeper相关配置

+ zookeeper.connect：zk1:2181,zk2:2181,zk3:2181
  + chroot：多套kafka集群使用同一套zookeeper集群时配置该参数，如：zk1:2181,zk2:2181,zk3:2181/kafka1

#### Broker连接相关参数

+ listeners：指定外部连接kafka的协议
  + 逗号分隔的三元组，<协议名称，主机名，端口号>
  + PLAINTEXT明文，SSL或TLS加密传输
  + 自定义协议
    + 指定listener.security.protocol.map=CONTROLLER:PLAINTEXT，表示CONTROLLER自定义协议使用明文传输
+ advertised.listeners：Broker对外发布
+ -- host.name/port：过期参数

#### Topic管理参数

+ auto.create.topics.enable：是否允许自动创建topic
  + 设置成false

+ unclean.leader.election.enable：是否允许Unclean Leader选举
  + true：可能丢失数据
  + 默认false：不允许落后太多的partition当选Leader

+ auto.leader.rebalance.enable：是否允许定期进行Leader选举
  + 设置成false：Leader选举的对集群环境影响很大，并且定期换Leader本质上没有任何收益

#### 数据留存方面

+ log.retention.{hour|minutes|ms}：优先级ms最高，minutes次之，hour最低
  + 通常设置hour级别
+ log.retention.bytes：Broker为消息保存的总磁盘大小
  + 默认-1：不设置大小限制
  + 云服务构建多租户Kafka集群，限制每个租户使用的磁盘空间大小
+ message.max.bytes：控制Broker接收最大消息的大小
  + 默认1000012，不到1MB，需要改大

#### 数据压缩

compression.type：默认producer，表示默认使用生产者端压缩算法；如果配置为其他，可能产生解压和重压缩操作

### Topic级别参数

根据不同业务设置不同的Topic级别参数，Topic级别参数会覆盖Broker全局参数

+ retention.ms：设置Topic消息保存时间
  + 默认7天
+ retention.bytes：设置Topic预留磁盘空间
  + 和全局参数作用类似
+ max.message.bytes：设置Broker正常接收Topic最大消息大小

Topic参数设置方式：

+ 创建Topic时进行设置

  ```shell
  bin/kafka-topics.sh--bootstrap-serverlocalhost:9092--create--topictransaction--partitions1--replication-factor1--configretention.ms=15552000000--configmax.message.bytes=5242880
  ```

  

+ 修改Topic时进行设置

  ```shell
   bin/kafka-configs.sh--zookeeperlocalhost:2181--entity-typetopics--entity-nametransaction--alter--add-configmax.message.bytes=10485760
  ```

  

### JVM参数

Kafka2.0.0版本开始放弃对Java7的支持

+ 堆参数：-Xmx：建议6G（业界公认合理值）
+ GC参数：
  + CPU资源充裕：-XX:+UseConcMarkSweepGC
  + 否则，使用吞吐量收集器：-XX:+UseParallelGC
  + Java9：使用默认G1收集器

Kafka JVM参数配置

```shell
$> export KAFKA_HEAP_OPTS=--Xms6g  --Xmx6g
$> export  KAFKA_JVM_PERFORMANCE_OPTS= -server -XX:+UseG1GC -XX:MaxGCPauseMillis=20 -XX:InitiatingHeapOccupancyPercent=35 -XX:+ExplicitGCInvokesConcurrent -Djava.awt.headless=true
$> bin/kafka-server-start.sh config/server.properties
```

### 操作系统参数

+ 文件描述符限制：ulimit -n 1000000
+ 文件系统类型：ext3，ext4，XFS，ZFS，生产环境最好XFS，ZFS性能比XFS性能更好，有条件可以试试
+ Swappiness：建议设置1，如果设置为0，物理内存耗光时，会触发OOM Killer组件，会随机挑选进程杀掉
+ 提交时间： flush落盘时间，定期刷脏页数据（Page Cache），默认5秒，可以适当调高

### 生产者端参数

compression.type：启用指定类型的压缩算法

## 无消息丢失配置

1. producer端

   + 使用producer.send(msg, callback)带有回调通知的方法，不要用producer.send(msg)。

   + Producer端参数，设置acks=all。表示所有Broker副本都要接受消息，该消息才算“已提交”；

   + Producer端参数，设置retires为一个较大的数。自动重试消息发送，避免消息丢失。

2. Broker端参数

   + 设置unclean.leader.election.enable=false。避免消息同步落后原来Leader太多的Broker当选新的Leader。

   + Broker端参数，设置replication.factor>=3。保证消息的冗余。

   + Broker端参数，设置min.insync.repicas>1。控制消息至少被写入多少副本才算“已提交”。生成环境切记不要使用默认值1。

   + 确保replication.factor>min.insync.repicas。如果两者相等，那么只要有一个副本挂机，整个分区就无法正常工作，降低了系统的可用性。推荐设置replication.factor=min.insync.repicas+1。

3. Consumer端参数

   + 设置enable.auto.commit=false。确保消息消费完成再提交，并采用手动提交位移的方式。这对于**单Consumer、多线程处理的场景至关重要**。

## 客户端实践及原理剖析

### 生产者消息分区机制原理剖析

#### 分区的目的

Kafka的消息组织结构实际上是三层结构：主题->分区->消息。主题中的一条消息只会保存到某一个分区中，而不会在多个分区中保存多份。官网上的这张图清晰的展示Kafka的三级结构，如图所示：

![img](D:\data\document\kafka\assets\18e487b7e64eeb8d0a487c289d83ab63.png)

分区的作用就是提供负载均衡的能力，实现系统的高伸缩性（Scalability）。不同的分区被放到不同的Broker上，数据的读写也是针对分区粒度，这样每个Broker都能独立的执行各自分区的读写请求操作。并且，还可以通过增加Broker来提高系统的整体吞吐量。

这种分区的思想在其他分布式中间件中同样存在，比如MongoDB和Elasticsearch中叫分片Shard，Hbase中叫Region，在Cassandra中叫作vnode。

除了提供负载均衡这种核心功能外，利用分区还可以实现一些业务需求，比如实现业务级的消息顺序问题。

#### 分区策略

**分区策略就是决定生产者将消息发送到哪个分区的算法。**Kafka提供了默认的分区策略，同时也支持自定义分区策略。



### 生产者压缩算法

### 无消息丢失配置

### 拦截器

### 生产者管理TCP连接

### 消费者管理TCP连接

### 幂等生产者和事务生产者

### 消费者组

### 位移主题

### 如何避免消费者组重平衡

### Kafka中的位移提交

### 多线程开发消费者实例

### 实现消费者组消费进度监控

## 深入kafka内核

## 管理与监控