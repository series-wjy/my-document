# kafka核心技术与实战

## 一、重要参数配置

### 1.1 Borker端参数

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

### 1.2 Topic级别参数

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

  

### 1.3 JVM参数

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

### 1.4 操作系统参数

+ 文件描述符限制：ulimit -n 1000000
+ 文件系统类型：ext3，ext4，XFS，ZFS，生产环境最好XFS，ZFS性能比XFS性能更好，有条件可以试试
+ Swappiness：建议设置1，如果设置为0，物理内存耗光时，会触发OOM Killer组件，会随机挑选进程杀掉
+ 提交时间： flush落盘时间，定期刷脏页数据（Page Cache），默认5秒，可以适当调高

### 1.5 生产者端参数

compression.type：启用指定类型的压缩算法

## 二、无消息丢失配置

### 2.1 producer端

+ 使用producer.send(msg, callback)带有回调通知的方法，不要用producer.send(msg)。

+ Producer端参数，设置acks=all。表示所有Broker副本都要接受消息，该消息才算“已提交”；

+ Producer端参数，设置retires为一个较大的数。自动重试消息发送，避免消息丢失。

### 2.2 Broker端参数

+ 设置unclean.leader.election.enable=false。避免消息同步落后原来Leader太多的Broker当选新的Leader。

+ Broker端参数，设置replication.factor>=3。保证消息的冗余。

+ Broker端参数，设置min.insync.repicas>1。控制消息至少被写入多少副本才算“已提交”。生成环境切记不要使用默认值1。

+ 确保replication.factor>min.insync.repicas。如果两者相等，那么只要有一个副本挂机，整个分区就无法正常工作，降低了系统的可用性。推荐设置replication.factor=min.insync.repicas+1。

### 2.3 Consumer端参数

+ 设置enable.auto.commit=false。确保消息消费完成再提交，并采用手动提交位移的方式。这对于**单Consumer、多线程处理的场景至关重要**。

## 三、客户端实践及原理剖析

### 3.1 生产者消息分区机制

#### 分区的目的

Kafka的消息组织结构实际上是三层结构：主题->分区->消息。主题中的一条消息只会保存到某一个分区中，而不会在多个分区中保存多份。官网上的这张图清晰的展示Kafka的三级结构，如图所示：

![img](D:\data\document\kafka\assets\18e487b7e64eeb8d0a487c289d83ab63.png)

分区的作用就是提供负载均衡的能力，实现系统的高伸缩性（Scalability）。不同的分区被放到不同的Broker上，数据的读写也是针对分区粒度，这样每个Broker都能独立的执行各自分区的读写请求操作。并且，还可以通过增加Broker来提高系统的整体吞吐量。

这种分区的思想在其他分布式中间件中同样存在，比如MongoDB和Elasticsearch中叫分片Shard，Hbase中叫Region，在Cassandra中叫作vnode。

除了提供负载均衡这种核心功能外，利用分区还可以实现一些业务需求，比如实现业务级的消息顺序问题。

#### Kafka的分区策略

**分区策略就是决定生产者将消息发送到哪个分区的算法。**Kafka提供了默认的分区策略，同时也支持自定义分区策略。自定义分区策略需要实现org.apache.kafka.clients.producer.Partitioner接口。该接口有两个方法：partition()和close()方法，通常只需要实现最重要的partition方法。

```java
public interface Partitioner extends Configurable, Closeable {

    /**
     * Compute the partition for the given record.
     *
     * @param topic The topic name
     * @param key The key to partition on (or null if no key)
     * @param keyBytes The serialized key to partition on( or null if no key)
     * @param value The value to partition on or null
     * @param valueBytes The serialized value to partition on or null
     * @param cluster The current cluster metadata
     */
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster);

    /**
     * This is called when partitioner is closed.
     */
    public void close();

}
```

实现了Partitioner接口，同时定义好partition()方法，在producer端设置partitioner.class参数为你的自定义分区实现类的全限定名，生产者程序就会按照你的逻辑代码来对消息进行分区了。

#### 常见的分区策略

1. 轮询策略(Round-robin)

   即顺序分配。比如下图，有三个分区，那么第一条消息发送到分区0，第二条消息发送到分区1，第三条消息发送到分区2，第四条消息再次发送到分区0，依次类推。

   ![img](D:\data\document\kafka\assets\6c630aaf0b365115897231a4e0a7e1af.png)

   Kafka默认的分区策略就是轮询策略，如果你为配置partitioner.class参数指定分区策略，那么生产者就会按照轮询的方式向主题的所有分区发送消息。

   **轮询策略有非常优秀的负载均衡表现，它总是能保证消息最大限度的被平均分配到所有分区上，故默认情况下它是最合理的分区策略，也是最常用的分区策略之一。**

2. 随机策略(Randomness)

   随机策略就是随意的将消息放置到任意一个分区上，如下图所示。

   ![img](D:\data\document\kafka\assets\5b50b76efb8ada0f0779ac3275d215a3.png)

   实现随机策略很简单，只需要两行代码就可以。计算出该主题的分区数，然后随机返回一个小于分区总数的正整数：

   ```java
   List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
   return ThreadLocalRandom.current().nextInt(partitions.size());
   ```

   表面上看随机策略是力求将数据均匀的打散到各个分区，实际表现上看，它要逊色于轮询策略。所以**如果要追求数据的均匀分布，还是使用轮询策略比较好**。

3. 按消息键保存策略

   Kafka 允许为每条消息定义消息键，简称为 Key。这个 Key 的作用非常大，它可以是一个有着明确业务含义的字符串，比如客户代码、部门编号或是业务 ID 等；也可以用来表征消息元数据。特别是在 Kafka 不支持时间戳的年代，在一些场景中，工程师们都是直接将消息创建时间封装进 Key 里面的。一旦消息被定义了 Key，那么你就可以保证同一个 Key 的所有消息都进入到相同的分区里面，由于每个分区下的消息处理都是有顺序的，故这个策略被称为按消息键保序策略，如下图所示。

   ![img](D:\data\document\kafka\assets\63aba008c3e3ad6b6dcc20464b600035.png)

   如果要实现随机策略版的 partition 方法，很简单，只需要两行代码即可：

   ```java
   List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
   return Math.abs(key.hashCode()) % partitions.size();
   ```

   实际上，前面提到的 Kafka 默认分区策略实际上同时实现了两种策略：如果指定了 Key，那么默认实现按消息键保序策略；如果没有指定 Key，则使用轮询策略。从上图中可以明显看出，自定义Key分区策略可以实现相同的键发送到相同的分区中，可以实现单分区内消息的有序性。

4. 其他分区策略

   按IP地址进行分区，假设Kafka集群分为南北两个片区，将消息生产者请求根据南北不同的片区查找对应的leader分区，随机挑选一个进行发送。

   ```java
   List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
   return partitions.stream().filter(p -> isSouth(p.leader().host())).map(PartitionInfo::partition).findAny().get();
   ```

### 3.2 消息压缩算法

压缩（compression）是一种用时间去换空间的经典 trade-off 思想，就是用 CPU 时间去换磁盘空间或网络 I/O 传输量，希望以较小的 CPU 开销带来更少的磁盘占用或更少的网络 I/O 传输。在 Kafka 中也是一样。

Kafka的消息层次分为两层：消息集合（Message Set）以及消息（Message）。消息集合中包含若干个日志项，日志项才是真正保存消息的地方。Kafka底层的消息日志由一系列的消息集合日志项组成。Kafka在消息集合这个层面进行写入操作。如下所示就是Kafka消息在磁盘中的消息格式

```
baseOffset: int64
batchLength: int32
partitionLeaderEpoch: int32
magic: int8 (current magic value is 2)
crc: int32
attributes: int16
    bit 0~2:
        0: no compression
        1: gzip
        2: snappy
        3: lz4
        4: zstd
    bit 3: timestampType
    bit 4: isTransactional (0 means not transactional)
    bit 5: isControlBatch (0 means not a control batch)
    bit 6~15: unused
lastOffsetDelta: int32
firstTimestamp: int64
maxTimestamp: int64
producerId: int64
producerEpoch: int16
baseSequence: int32
records: [Record]
```

目前 Kafka 共有两大类消息格式，社区分别称之为 V1 版本和 V2 版本。V2 版本是 Kafka 0.11.0.0 中正式引入的。V2版相对于V1版有两大方面的改进：

+ 将消息的公共部分抽取出来放到外层的消息集合里面。
+ 改变保存消息压缩的方法。V1版是针对单条消息压缩后保存到消息集合里面，V2版则是对整个消息集合进行压缩。

显然V2版的压缩效果要比V1版好。下图是测试不同版本对消息进行压缩的结果对比。

![img](D:\data\document\kafka\assets\11ddc5575eb6e799f456515c75e1d821.png)

#### 何时压缩

Kafka中，压缩发生在两个地方：生产者端和Broker端。

生产者程序中配置 compression.type 参数即表示启用指定类型的压缩算法。比如下面这段程序代码展示了如何构建一个开启 GZIP 的 Producer 对象：

```java
 Properties props = new Properties();
 props.put("bootstrap.servers", "localhost:9092");
 props.put("acks", "all");
 props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
 props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
 // 开启 GZIP 压缩
 props.put("compression.type", "gzip");
 
 Producer<String, String> producer = new KafkaProducer<>(props);

```

关键的代码是 props.put(“compression.type”, “gzip”)，它表明该 Producer 的压缩算法使用的是 GZIP。这样 Producer 启动后生产的每个消息集合都是经 GZIP 压缩过的，因此能很好地节省网络传输带宽以及 Kafka Broker 端的磁盘占用。

大部分情况下，Broker端接收到Producer发来的消息，都是原样保存，不做任何修改。但是有两种例外的情况：

1. Broker端指定了和Producer端不同的压缩算法。
   + Broker端也有一个compression.type参数，这个参数的默认值是producer。表示默认采用生产者端的压缩算法。如果Broker端配置了和生产者端不同的压缩算法，就会发生解压缩/再压缩操作，会给Broker端服务器带来压力。
2. Broker端发生消息格式转换
   + 消息格式转换其实是为了兼容老版本的消费者程序。为了兼容老版本消息格式，Broker端会对新版本消息解压缩再重新压缩。这个过程不光是解压/压缩的性能损耗，还会丧失Kafka的Zero Copy特性。

#### 何时解压

Broker端收到消息后，要么原样保存，要么对消息解压/压缩后在保存。Consumer端获取消息后，由Consumer自行解压还原之前的消息。Consumer如何知道用哪种压缩算法进行解压呢？其实就在消息集合里面封装了压缩算法属性，Consumer获取到压缩算法后，就可以根据相应的压缩算法进行解压。

除了Consumer端的解压缩，Broker端也会解压消息。这里的场景和之前提到的因为消息转换而解压缩是不同的。这里的解压缩是为了对消息进行各种验证。这种解压缩对Broker端的性能是有影响的，特别是对CPU而言。

**注意：**在Kafka的2.4版本中已经规避了这个问题，[可以点击这里查看](https://issues.apache.org/jira/browse/KAFKA-8106)。

#### 压缩算法对比

在 Kafka 2.1.0 版本之前，Kafka 支持 3 种压缩算法：GZIP、Snappy 和 LZ4。从 2.1.0 开始，Kafka 正式支持 Zstandard 算法（简写为 zstd）。它是 Facebook 开源的一个压缩算法，能够提供超高的压缩比（compression ratio）。

一个压缩算法的优劣，有两个重要的指标：

+ 压缩比：原先占 100 份空间的东西经压缩之后变成了占 20 份空间，那么压缩比就是 5，显然压缩比越高越好；
+ 压缩 / 解压缩吞吐量：比如每秒能压缩或解压缩多少 MB 的数据。同样地，吞吐量也是越高越好。

![img](D:\data\document\kafka\assets\cfe20a2cdcb1ae3b304777f7be928068.png)

在实际使用中，GZIP、Snappy、LZ4 甚至是 zstd 的表现各有千秋。对于 Kafka 性能测试结果与上图一致，即在吞吐量方面：LZ4 > Snappy > zstd 和 GZIP；而在压缩比方面，zstd > LZ4 > GZIP > Snappy。具体到物理资源，使用 Snappy 算法占用的网络带宽最多，zstd 最少，毕竟 zstd 就是要提供超高的压缩比；在 CPU 使用率方面，各个算法表现得差不多。

#### 最佳实践

我们已经知道 Producer 端完成消息压缩，那么启用压缩的一个条件就是 Producer 程序运行机器上的 CPU 资源要很充足。如果 Producer 运行机器本身 CPU 已经消耗殆尽了，那么启用消息压缩无疑是雪上加霜，只会适得其反。

其次 CPU 资源充足这一条件，如果你的环境中带宽资源有限，那么也建议开启压缩。带宽资源比CPU和内存还要稀缺，千兆网络中 Kafka 集群经常出现带宽资源耗尽的情况。如果客户端机器 CPU 资源有很多富余，强烈建议开启 zstd 压缩，这样能极大地节省网络资源消耗。

最后是解压缩，我们对不可抗拒的解压缩无能为力，但至少能规避掉那些意料之外的解压缩。就像前面说的，因为要兼容老版本而引入的解压缩操作就属于这类。所以在条件允许的范围内，尽量保证客户端和Kafka集群的版本一致。

### 3.3 无消息丢失配置

**一句话概括，Kafka 只对“已提交”的消息（committed message）做有限度的持久化保证**。

+ **已提交的消息**

  Kafka的若干个Broker接收到消息，并成功写入到日志文件，并反馈给Producer这条消息已提交成功。到此，这条消息就是“已提交”消息。

+ **有限度的持久化保证**

  消息提交到N个Broker，并且这N个Broker中至少有一个存活。

#### 消息丢失案例

生产者端丢失消息

Kafka Producer是异步发送消息，如果调用producer.send(msg)发送消息，它会立即返回，但此时不能认为消息发送已经成功完成。

**Producer端要使用带有回调通知的发送API，也就是说不要使用producer.send(msg)，而要使用producer.send(msg, callback)**。

消费者端丢失消息

Consumer 端丢失数据主要体现在 Consumer 端要消费的消息不见了。Consumer 程序有个“位移”的概念，表示的是这个 Consumer 当前消费到的 Topic 分区的位置。下面这张图来自于官网，它清晰地展示了 Consumer 端的位移数据。

![img](D:\data\document\kafka\assets\0c97bed3b6350d73a9403d9448290d37.png)

错误的使用位移就会导致消息丢失，正确使用位移的方式是：**先消费消息，再更新位移。**如果先更新位移，再读取消息，就可能导致消息丢失。

还有一种就是自动提交位移数据，导致消息丢失。就是说没有确认消息时真正的消费成功，就盲目的提交位移数据。

这个问题的解决方案也很简单：**如果是多线程异步处理消费消息，Consumer 程序不要开启自动提交位移，而是要应用程序手动提交位移。**单个 Consumer 程序使用多线程来消费消息说起来容易，写成代码却异常困难，因为很难正确地处理位移的更新，也就是说避免无消费消息丢失很简单，但极易出现消息被消费了多次的情况。

#### 最佳实践

1. 不要使用 producer.send(msg)，而要使用 producer.send(msg, callback)。记住，一定要使用带有回调通知的 send 方法。
2. 设置 acks = all。acks 是 Producer 的一个参数，代表了你对“已提交”消息的定义。如果设置成 all，则表明所有副本 Broker 都要接收到消息，该消息才算是“已提交”。这是最高等级的“已提交”定义。
3. 设置 retries 为一个较大的值。这里的 retries 同样是 Producer 的参数，对应前面提到的 Producer 自动重试。当出现网络的瞬时抖动时，消息发送可能会失败，此时配置了 retries > 0 的 Producer 能够自动重试消息发送，避免消息丢失。
4. 设置 unclean.leader.election.enable = false。这是 Broker 端的参数，它控制的是哪些 Broker 有资格竞选分区的 Leader。如果一个 Broker 落后原先的 Leader 太多，那么它一旦成为新的 Leader，必然会造成消息的丢失。故一般都要将该参数设置成 false，即不允许这种情况的发生。
5. 设置 replication.factor >= 3。这也是 Broker 端的参数。其实这里想表述的是，最好将消息多保存几份，毕竟目前防止消息丢失的主要机制就是冗余。
6. 设置 min.insync.replicas > 1。这依然是 Broker 端参数，控制的是消息至少要被写入到多少个副本才算是“已提交”。设置成大于 1 可以提升消息持久性。在实际环境中千万不要使用默认值 1。
7. 确保 replication.factor > min.insync.replicas。如果两者相等，那么只要有一个副本挂机，整个分区就无法正常工作了。我们不仅要改善消息的持久性，防止数据丢失，还要在不降低可用性的基础上完成。推荐设置成 replication.factor = min.insync.replicas + 1。
8. 确保消息消费完成再提交。Consumer 端有个参数 enable.auto.commit，最好把它设置成 false，并采用手动提交位移的方式。就像前面说的，这对于单 Consumer 多线程处理的场景而言是至关重要的。

### 3.4 拦截器

### 3.5 生产者管理TCP连接

### 3.6 消费者管理TCP连接

### 3.7 幂等生产者和事务生产者

### 3.8 消费者组

### 3.9 位移主题

### 3.10 避免消费者组重平衡

### 3.11 Kafka中的位移提交

### 3.12 多线程开发消费者实例

### 3.13 实现消费者组消费进度监控

## 四、深入kafka内核

## 五、管理与监控