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
    + 必须指定：listener.security.protocol.map=CONTROLLER:PLAINTEXT，表示CONTROLLER自定义协议使用明文传输
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
  + 和全局参数作用类似，默认值-1
+ max.message.bytes：设置Broker正常接收Topic最大消息大小

Topic参数设置方式：

+ 创建Topic时进行设置

  ```shell
  bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topictransaction --partitions 1 --replication-factor 1 --configretention.ms=15552000000 --config max.message.bytes=5242880
  ```

  

+ 修改Topic时进行设置

  ```shell
   bin/kafka-configs.sh --zookeeper localhost:2181 --entity-typetopics --entity-name transaction --alter --add-config max.message.bytes=10485760
  ```

  **最好使用第二种方式修改Topic参数，未来可能会统一采用第二种方式。**

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
+ 文件系统类型：ext3，ext4，XFS，ZFS，生产环境最好XFS，ZFS性能比XFS性能更好
+ Swappiness：建议设置为一个较小值，比如1；如果设置为0，物理内存耗光时，会触发OOM Killer组件，会随机挑选进程杀掉
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

![img](E:\data\my-document\kafka\assets\18e487b7e64eeb8d0a487c289d83ab63.png)

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

   ![img](E:\data\my-document\kafka\assets\6c630aaf0b365115897231a4e0a7e1af.png)

   Kafka默认的分区策略就是轮询策略，如果你为配置partitioner.class参数指定分区策略，那么生产者就会按照轮询的方式向主题的所有分区发送消息。

   **轮询策略有非常优秀的负载均衡表现，它总是能保证消息最大限度的被平均分配到所有分区上，故默认情况下它是最合理的分区策略，也是最常用的分区策略之一。**

2. 随机策略(Randomness)

   随机策略就是随意的将消息放置到任意一个分区上，如下图所示。

   ![img](E:\data\my-document\kafka\assets\5b50b76efb8ada0f0779ac3275d215a3.png)

   实现随机策略很简单，只需要两行代码就可以。计算出该主题的分区数，然后随机返回一个小于分区总数的正整数：

   ```java
   List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
   return ThreadLocalRandom.current().nextInt(partitions.size());
   ```

   表面上看随机策略是力求将数据均匀的打散到各个分区，实际表现上看，它要逊色于轮询策略。所以**如果要追求数据的均匀分布，还是使用轮询策略比较好**。

3. 按消息键保存策略

   Kafka 允许为每条消息定义消息键，简称为 Key。这个 Key 的作用非常大，它可以是一个有着明确业务含义的字符串，比如客户代码、部门编号或是业务 ID 等；也可以用来表征消息元数据。特别是在 Kafka 不支持时间戳的年代，在一些场景中，工程师们都是直接将消息创建时间封装进 Key 里面的。一旦消息被定义了 Key，那么你就可以保证同一个 Key 的所有消息都进入到相同的分区里面，由于每个分区下的消息处理都是有顺序的，故这个策略被称为按消息键保序策略，如下图所示。

   ![img](E:\data\my-document\kafka\assets\63aba008c3e3ad6b6dcc20464b600035.png)

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

![img](E:\data\my-document\kafka\assets\11ddc5575eb6e799f456515c75e1d821.png)

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

![img](E:\data\my-document\kafka\assets\cfe20a2cdcb1ae3b304777f7be928068.png)

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

![img](E:\data\my-document\kafka\assets\0c97bed3b6350d73a9403d9448290d37.png)

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

Kafka 拦截器可以应用于包括客户端监控、端到端系统性能检测、消息审计等多种功能在内的场景。

#### 拦截器原理

<img src="E:\data\my-document\kafka\assets\image-20200604163334841.png" alt="image-20200604163334841" style="zoom:50%;" />

#### 拦截器配置

```java

Properties props = new Properties();
List<String> interceptors = new ArrayList<>();
interceptors.add("com.yourcompany.kafkaproject.interceptors.AddTimestampInterceptor"); // 拦截器1
interceptors.add("com.yourcompany.kafkaproject.interceptors.UpdateCounterInterceptor"); // 拦截器2
props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);
……
```

#### 拦截器实现

Producer 端拦截器实现类要继承 org.apache.kafka.clients.producer.ProducerInterceptor 接口。该接口由 Kafka 提供，包括两个核心的方法。

+ onSend：该方法会在消息发送之前被调用。
+ onAcknowledgement：该方法会在消息成功提交或发送失败之后被调用。onAcknowledgement 的调用要早于 发送回调callback 的调用。值得注意的是，**这个方法和 onSend 不是在同一个线程中被调用的**，因此如果你在这两个方法中调用了某个共享可变对象，一定要保证线程安全哦。还有一点很重要，这个方法处在 Producer 发送的主路径中，所以最好别放一些太重的逻辑进去，否则会造成 Producer TPS 直线下降。

Consumer端拦截器实现类要继承org.apache.kafka.clients.consumer.ConsumerInterceptor 接口。该接口由 Kafka 提供，包括两个核心的方法。

+ onConsume：该方法在消息返回给 Consumer 程序之前调用。
+ onCommit：Consumer 在提交位移之后调用该方法。通常可以在该方法中做一些记账类的动作，比如打日志等。

### 3.5 生产者管理TCP连接

#### TCP连接创建的时机

+ 创建Producer时创建

​		**在创建 KafkaProducer 实例时，生产者应用会在后台创建并启动一个名为 Sender 的线程，该 Sender 线程开始运行时首先会创建与 Broker 的连接。**Sender线程会跟**broker.servers**中配置的所有broker创建TCP连接。连接创建完成后，Producer会向集群发送METADATA请求，尝试获取集群的元数据信息。

+ 更新元数据后（可能）

  当 Producer 更新了集群的元数据信息之后，如果发现与某些 Broker 当前没有连接，那么就会创建一个 TCP 连接。Producer 通过 **metadata.max.age.ms** 参数定期地去更新元数据信息。该参数的默认值是 300000，即 5 分钟，不管集群那边是否有变化，Producer 每 5 分钟都会强制刷新一次元数据以保证它是最及时的数据

+ 消息发送时（可能）

  当要发送消息时，Producer 发现尚不存在与目标 Broker 的连接，也会创建一个TCP连接。

#### 关闭TCP连接

+ 用户主动关闭

  杀掉进程或者调用producer.close()。

+ Kafka自动关闭

  与 Producer 端参数 **connections.max.idle.ms** 的值有关。默认情况下该参数值是 9 分钟，如果在 9 分钟内没有任何请求“流过”某个 TCP 连接，那么 Kafka 会主动帮你把该 TCP 连接关闭。用户可以在 Producer 端设置 **connections.max.idle.ms=-1** 禁掉这种机制。一旦被设置成 -1，TCP 连接将成为永久长连接。这只是软件层面的“长连接”机制，由于 Kafka 创建的这些 Socket 连接都开启了 keepalive，因此 keepalive 探活机制还是会遵守的。

### 3.6 消费者管理TCP连接

**TCP 连接是在调用 KafkaConsumer.poll 方法时被创建**。在 poll 方法内部有 3 个时机可以创建 TCP 连接：

1. **发起 FindCoordinator 请求时**

   Coordinator驻留在 Broker 端的内存中，负责消费者组的组成员管理和各个消费者的位移提交管理。当消费者程序首次启动调用 poll 方法时，向 Kafka 集群发送一个 FindCoordinator 请求，向 Kafka 集群获取管理它的协调者 Broker 。FindCoordinator 向当前负载最小的那台 Broker 发送请求。

2. **连接协调者时**

   Broker 处理完 FindCoordinator 请求，返回对应的响应结果（Response），通知消费者真正的协调者 Broker ，消费者会创建连向该 Broker 的 Socket 连接。只有成功连入协调者，协调者才能开启正常的组协调操作，比如加入组、等待组分配方案、心跳请求处理、位移获取、位移提交等。

3. **消费数据时**

   消费者会为每个要消费的分区创建与该分区领导者副本所在 Broker 连接的 TCP。

#### 创建连接的数量

消费者程序会创建 3 类 TCP 连接：

1. 确定协调者和获取集群元数据。
2. 连接协调者，令其执行组成员管理操作。
3. 执行实际的消息获取。

**当第三类 TCP 连接成功创建后，消费者程序就会废弃第一类 TCP 连接**，之后在定期请求元数据时，它会改为使用第三类 TCP 连接。也就是说，第一类 TCP 连接会在后台被默默地关闭掉。对一个运行了一段时间的消费者程序来说，只会有后面两类 TCP 连接存在。

#### 关闭TCP连接

+ 手动调用 **KafkaConsumer.close()** 方法，或者是执行 Kill 命令；
+ Kafka 自动关闭是由消费者端参数 **connection.max.idle.ms** 控制的，该参数的默认值是 9 分钟，即如果某个 Socket 连接上连续 9 分钟都没有任何请求的话，消费者会强行关闭这个 Socket 连接。

使用循环的方式来调用 poll 方法消费消息，那么上面提到的所有请求都会被定期发送到 Broker，因此这些 Socket 连接上总是能保证有请求在发送，从而也就实现了“长连接”的效果。

将 **connection.max.idle.ms** 设置成 -1，即禁用定时关闭的案例，如果是这样的话，这些 TCP 连接将不会被定期清除，只会成为永久的“僵尸”连接。

### 3.7 幂等生产者和事务生产者

消息交付可靠性保障，是指 Kafka 对 Producer 和 Consumer 要处理的消息提供什么样的承诺。常见的承诺有以下三种：

+ 最多一次（at most once）：消息可能会丢失，但绝不会被重复发送。
+ 至少一次（at least once）：消息不会丢失，但有可能被重复发送。
+ 精确一次（exactly once）：消息不会丢失，也不会被重复发送。

**Kafka默认提供第二种可靠性保证**。

#### 幂等性

幂等性最大的优势在于可以安全地重试任何幂等性操作，不会破坏的系统状态。

用空间去换时间的优化思路，即在 Broker 端多保存一些字段，Broker端会自动去重，Kafka实现幂等性Producer的方法：

```java
props.put(“enable.idempotence”, ture);
//或者
props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG， true)。
```

幂等性Producer的作用范围：

+ 只能保证单分区幂等性
+ 只能保证单回话幂等性

#### 事务

+ 事务型 Producer 能够保证将消息原子性地写入到多个分区中。这批消息要么全部写入成功，要么全部失败。
+ 事务型 Producer 也不惧进程的重启。Producer 重启回来后，Kafka 依然保证它们发送消息的精确一次处理。

设置事务型 Producer 的方法：

+ 和幂等性 Producer 一样，开启 enable.idempotence = true。
+ 设置 Producer 端参数 transactional. id。最好为其设置一个有意义的名字。

Producer代码需要作出相应的调整：

```java
producer.initTransactions();
try {
    producer.beginTransaction();
    producer.send(record1);
    producer.send(record2);
    producer.commitTransaction();
} catch (KafkaException e) {
    producer.abortTransaction();
}
```

实际上即使写入失败，Kafka 也会把它们写入到底层的日志中，也就是说 Consumer 还是会看到这些消息。因此在 Consumer 端，读取事务型 Producer 发送的消息也需要一些变更，设置 isolation.level 参数的值。当前这个参数有两个可选值：

+ read_uncommitted：这是默认值，表明 Consumer 能够读取到 Kafka 写入的任何消息，不论事务型 Producer 提交事务还是终止事务，其写入的消息都可以读取。很显然，如果你用了事务型 Producer，那么对应的 Consumer 就不要使用这个值。
+ read_committed：表明 Consumer 只会读取事务型 Producer 成功提交事务写入的消息。当然，也能看到非事务型 Producer 写入的所有消息。

### 3.8 消费者组

**Consumer Group 是 Kafka 提供的可扩展且具有容错性的消费者机制。**

Consumer Group 有下面这三个特性：

+ Consumer Group 下可以有一个或多个 Consumer 实例。这里的实例可以是一个单独的进程，也可以是同一进程下的线程。在实际场景中，使用进程更为常见一些。
+ Group ID 是一个字符串，在一个 Kafka 集群中，它标识唯一的一个 Consumer Group。
+ Consumer Group 下所有实例订阅的主题的单个分区，只能分配给组内的某个 Consumer 实例消费。这个分区当然也可以被其他的 Group 消费。

#### 两种消息引擎模型

+ 点对点模型

  传统的消息队列模型的缺陷在于消息一旦被消费，就会从队列中被删除，而且只能被下游的一个 Consumer 消费。严格说，这不算是缺陷，只能算是一个特性。很显然，这种模型的伸缩性（scalability）很差，因为下游的多个 Consumer 都要“抢”这个共享消息队列的消息。

+ 发布 / 订阅模型

  发布 / 订阅模型倒是允许消息被多个 Consumer 消费，但它的问题也是伸缩性不高，因为每个订阅者都必须要订阅主题的所有分区。这种全量订阅的方式既不灵活，也会影响消息的真实投递效果。

#### Kafka的消费者组模型

​		Kafka 的 Consumer Group 订阅了多个主题后，组内的每个实例不要求一定要订阅主题的所有分区，它只会消费部分分区中的消息。Consumer Group 之间彼此独立，互不影响，它们能够订阅相同的一组主题而互不干涉。

​		**Kafka 使用 Consumer Group 这种机制，同时实现了传统消息引擎系统的两大模型**：如果所有实例都属于同一个 Group，那么它实现的就是消息队列模型；如果所有实例分别属于不同的 Group，那么它实现的就是发布 / 订阅模型。

​		**理想情况下，Consumer 实例的数量应该等于该 Group 订阅主题的分区总数。**

#### Rebalance

​		Rebalance 本质上是一种协议，规定了一个 Consumer Group 下的所有 Consumer 如何达成一致，来分配订阅 Topic 的每个分区。

Rebalance 的触发条件有 3 个：

+ 组成员数发生变更。比如有新的 Consumer 实例加入组或者离开组，抑或是有 Consumer 实例崩溃被“踢出”组。

+ 订阅主题数发生变更。Consumer Group 可以使用正则表达式的方式订阅主题，比如 consumer.subscribe(Pattern.compile(“t.*c”)) 就表明该 Group 订阅所有以字母 t 开头、字母 c 结尾的主题。在 Consumer Group 的运行过程中，你新创建了一个满足这样条件的主题，那么该 Group 就会发生 Rebalance。

+ 订阅主题的分区数发生变更。Kafka 当前只能允许增加一个主题的分区数。当分区数增加时，就会触发订阅该主题的所有 Group 开启 Rebalance。

  ![image-20200604163257298](E:\data\my-document\kafka\assets\image-20200604163257298.png)

Rebalance存在的问题：

+ 首先，Rebalance 过程对 Consumer Group 消费过程有极大的影响。在 Rebalance 过程中，所有 Consumer 实例都会停止消费，等待 Rebalance 完成。
+ 其次，目前 Rebalance 的设计是所有 Consumer 实例共同参与，全部重新分配所有分区。其实更高效的做法是尽量减少分配方案的变动。这种方案没有发生变动的Consumer实例连接这些分区所在 Broker 的 TCP 连接就可以复用，不用重新创建连接对应 Broker 的 Socket 资源。
+ 最后，Rebalance 非常慢。最好的解决方案就是避免 Rebalance 的发生。

### 3.9 位移主题

**当 Kafka 集群中的第一个 Consumer 程序启动时，Kafka 会自动创建位移主题。**

位移主题的消息格式有 3 种消息格式：

+ 用于保存位移值的消息。
+ 用于保存 Consumer Group 信息的消息。
+ 用于删除 Group 过期位移甚至是删除 Group 的消息。

这些消息只出现在源码中而没有暴露出来。第 1 种格式是用来注册 Consumer Group 的。第 2 种格式有个专属的名称：tombstone 消息，即墓碑消息，也称 delete mark。它的主要特点是它的消息体是 null，即空消息体。一旦某个 Consumer Group 下的所有 Consumer 实例都停止了，而且它们的位移数据都已被删除时，Kafka 会向位移主题的对应分区写入 tombstone 消息，表明要彻底删除这个 Group 的信息。

#### 过期消息

Kafka 使用 Compact 策略来删除位移主题中的过期消息，避免该主题无限期膨胀。对于同一个 Key 的两条消息 M1 和 M2，如果 M1 的发送时间早于 M2，那么 M1 就是过期消息。Compact 的过程就是扫描日志的所有消息，剔除那些过期的消息，然后把剩下的消息整理在一起。

<img src="E:\data\my-document\kafka\assets\image-20200604163217915.png" alt="image-20200604163217915" style="zoom:50%;" />

**Kafka 提供了专门的后台线程定期地巡检待 Compact 的主题，看看是否存在满足条件的可删除数据。这个后台线程叫 Log Cleaner。**很多实际生产环境中都出现过位移主题无限膨胀占用过多磁盘空间的问题，如果你的环境中也有这个问题，建议检查一下 Log Cleaner 线程的状态，通常都是这个线程挂掉了导致的。

### 3.10 避免消费者组重平衡

#### 确定Consumer Group的Coordinator

​        **所有 Broker 都有各自的 Coordinator 组件。**那么，Consumer Group 通过 Kafka 内部位移主题 \__consumer_offsets 确定为它服务的Coordinator在哪个Broker上。

Kafka 为某个 Consumer Group 确定 Coordinator 所在的 Broker 的算法有 2 个步骤：

+ 第 1 步：确定由位移主题的哪个分区来保存该 Group 数据：partitionId=Math.abs(groupId.hashCode() % offsetsTopicPartitionCount)。
+ 第 2 步：找出该分区 Leader 副本所在的 Broker，该 Broker 即为对应的 Coordinator。

​        Kafka 会计算该 Group 的 group.id 参数的哈希值。比如有个 Group 的 group.id 为 “test-group”，那么它的 hashCode 值就是 627841412。其次，Kafka 会计算 __consumer_offsets 的分区数，默认是 50 个分区，之后将group.id的哈希值对分区数进行取模加求绝对值计算，即 abs(627841412 % 50) = 12。这样就确定位移主题的分区 12 负责保存这个 Consumer Group 的数据。找出位移主题分区 12 的 Leader 副本在哪个 Broker 上，这个 Broker就是该Consumer Group的 Coordinator。在实际使用过程中，Consumer 应用程序，特别是 Java Consumer API，能够自动发现并连接正确的 Coordinator。知晓这个算法的最大意义在于，它能够帮助我们解决定位问题。当 Consumer Group 出现问题，需要快速排查 Broker 端日志时，能够根据这个算法准确定位 Coordinator 对应的 Broker。

#### 规避Rebalance

Rebalance 发生的时机有三个：

+ 组成员数量发生变化
+ 订阅主题数量发生变化订
+ 阅主题的分区数发生变化

后两个通常是运维主动操作，此时Rebalance无法避免。组成员的数量变化通常是引起Rebalance的主要原因，这个变化通常又分为组成员的增加和组成员的减少，通常增加组成员的操作都是计划内的，我们要避免的是**组成员的减少**。

​        当 Consumer Group 完成 Rebalance 后，每个 Consumer 定期地向 Coordinator 发送心跳请求。如果某个 Consumer 实例不能及时发送心跳请求，Coordinator 就认为该 Consumer 已经“死”了，将其从 Group 中移除，开启新一轮 Rebalance。Consumer 端参数 **session.timeout.ms**，默认值是 10 秒，即 Coordinator 在 10 秒之内没有收到 Group 下某 Consumer 的心跳，就认为这个 Consumer 已经挂了。session.timeout.ms 决定了 Consumer 存活的时间间隔。

​        Consumer 提供控制发送心跳请求频率的参数 **heartbeat.interval.ms**。这个值设置得越小，Consumer 发送心跳请求的频率就越高。频繁地发送心跳请求会额外消耗带宽资源，但好处是能够更加快速地知晓当前是否应该开启 Rebalance，因为，目前 Coordinator 通知各个 Consumer 实例开启 Rebalance 的方法，就是将 **REBALANCE_NEEDED** 标志封装进心跳请求的响应体中。

​        Consumer 端参数 **max.poll.interval.ms**，控制 Consumer 实际消费能力对 Rebalance 的影响。限定 Consumer 端应用程序两次调用 poll 方法的最大时间间隔。它的默认值是 5 分钟，表示 Consumer 程序如果在 5 分钟之内无法消费完 poll 方法返回的消息，那么 Consumer 会主动发起“离开组”的请求，Coordinator 也会开启新一轮 Rebalance。

##### 非必要的Rebalance

+ 第一类非必要 Rebalance 是未能及时发送心跳，导致 Consumer 被“踢出”Group 而引发。设置 session.timeout.ms 和 heartbeat.interval.ms 的值。推荐参数设置：
  + 设置 session.timeout.ms = 6s。
  + 设置 heartbeat.interval.ms = 2s。
  + 保证 Consumer 实例在被判定为“dead”之前，能够发送至少 3 轮的心跳请求，即 session.timeout.ms >= 3 * heartbeat.interval.ms。
+ 第二类非必要 Rebalance 是 Consumer 消费时间过长导致的。
  + 如果Consumer中包含耗时操作，需要将max.poll.interval.ms设置大一些，为下游操作预留足够的时间。

​        如果恰当地设置了这几个参数，却还是出现了 Rebalance，那么建议排查一下 **Consumer 端的 GC** 表现，比如是否出现了频繁的 Full GC 导致的长时间停顿，从而引发了 Rebalance。

### 3.11 Kafka中的位移提交

​		Consumer 需要向 Kafka 汇报自己的位移数据，汇报过程被称为**提交位移**（Committing Offsets）。因为 Consumer 能够同时消费多个分区的数据，所以位移的提交实际上是在分区粒度上进行的，即 **Consumer 需要为分配给它的每个分区提交各自的位移数据**。

#### 自动提交

​		Consumer 端有个参数 **enable.auto.commit**，设置为 true（默认值 true），默认自动提交位移。如果启用了自动提交，Consumer 端还有个参数：**auto.commit.interval.ms**。默认值 5 秒，表明 Kafka 每 5 秒会自动提交一次位移。

```java

Properties props = new Properties();
props.put("bootstrap.servers", "localhost:9092");
props.put("group.id", "test");
props.put("enable.auto.commit", "true");
props.put("auto.commit.interval.ms", "2000");
props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
consumer.subscribe(Arrays.asList("foo", "bar"));
     while (true) {
         ConsumerRecords<String, String> records = consumer.poll(100);
         for (ConsumerRecord<String, String> record : records)
             System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
     }
```

​	一旦设置了 **enable.auto.commit** 为 true，Kafka 会保证在开始调用 poll 方法时，提交上次 poll 返回的所有消息。从顺序上来说，poll 方法的逻辑是先提交上一批消息的位移，再处理下一批消息，因此它能保证不出现消费丢失的情况。但自动提交位移的一个问题在于，**可能会出现重复消费**。

​		在默认情况下，Consumer 每 5 秒自动提交一次位移。假设提交位移之后的 3 秒发生 Rebalance 操作。在 Rebalance 之后，所有 Consumer 从上一次提交的位移处继续消费，但该位移已经是 3 秒前的位移数据了，故在 Rebalance 发生前 3 秒消费的所有数据都要重新再消费一次。虽然能够通过减少 **auto.commit.interval.ms** 的值来提高提交频率，但这只能缩小重复消费的时间窗口，不可能完全消除它。**这是自动提交机制的一个缺陷**。

#### 手动提交

​		开启手动提交位移的方法，设置 **enable.auto.commit** 为 false，需要调用相应的 API 手动提交位移。最简单的 API 是 **KafkaConsumer#commitSync()**。该方法会提交 **KafkaConsumer#poll()** 返回的最新位移。

```java

while (true) {
    ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
    process(records); // 处理消息
    try {
        consumer.commitSync();
    } catch (CommitFailedException e) {
        handle(e); // 处理提交失败异常
    }
}
```

​		手动提交位移的好处就在于更加灵活，能够把控位移提交的时机和频率。但是也有一个缺陷，就是在调用 commitSync() 时，Consumer 程序会**处于阻塞状态**，直到远端的 Broker 返回提交结果才会结束。在任何系统中，因为程序而非资源限制而导致的阻塞都可能是系统的瓶颈，会影响整个应用程序的 TPS。当然，也可以选择拉长提交间隔，但这样做的后果是 Consumer 的提交频率下降，在下次 Consumer 重启回来后，会有更多的消息被重新消费。

#### 同步提交

#### 异步提交

​		Kafka 社区为手动提交位移提供了另一个 API 方法：**KafkaConsumer#commitAsync()**，是一个异步操作。调用 commitAsync() 之后，会立即返回，不会阻塞，因此不会影响 Consumer 应用的 TPS。由于它是异步的，Kafka 提供了回调函数（callback），供你实现提交之后的逻辑，比如记录日志或处理异常等。

```java

while (true) {
    ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
    process(records); // 处理消息
    consumer.commitAsync((offsets, exception) -> {
  	if (exception != null)
  		handle(exception);
  	});
}
```

​		commitAsync 的问题在于，出现问题时它不会自动重试。因为是异步操作，倘若提交失败后自动重试，那么它重试时提交的位移值可能早已经“过期”或不是最新值了。因此，异步提交的重试其实没有意义，所以 commitAsync 是不会重试的。

#### 组合提交

需要将 commitSync 和 commitAsync 组合使用才能到达最理想的效果，原因有两个：

+ 可以利用 commitSync 的自动重试来规避那些瞬时错误，比如网络的瞬时抖动，Broker 端 GC 等。因为这些问题都是短暂的，自动重试通常都会成功，因此，不想自己重试，而是希望 Kafka Consumer 帮我们做这件事。
+ 不希望程序总处于阻塞状态，影响 TPS。

```java

try {
        while(true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            process(records); // 处理消息
            commitAysnc(); // 使用异步提交规避阻塞
        }
    } catch(Exception e) {
    	handle(e); // 处理异常
    } finally {
    	try {
        	consumer.commitSync(); // 最后一次提交使用同步阻塞式提交
    	} finally {
        	consumer.close();
    	}
	}
```

​		对于常规性、阶段性的手动提交，调用 commitAsync() 避免程序阻塞，而在 Consumer 要关闭前，调用 commitSync() 方法执行同步阻塞式的位移提交，以确保 Consumer 关闭前能够保存正确的位移数据。将两者结合后，我们既实现了异步无阻塞式的位移管理，也确保了 Consumer 位移的正确性。

#### 精确提交位移

​		设想一个场景： poll 方法返回的不是 500 条消息，而是 5000 条。那么，肯定不想把这 5000 条消息都处理完之后再提交位移，因为一旦中间出现差错，之前处理的全部都要重来一遍。这类似于数据库中的事务处理。很多时候，希望将一个大事务分割成若干个小事务分别提交，这能够有效减少错误恢复的时间。

​		Kafka Consumer API 为手动提交提供了两个方法：commitSync(Map) 和 commitAsync(Map)。它们的参数是一个 Map 对象，键就是 TopicPartition，即消费的分区，而值是一个 OffsetAndMetadata 对象，保存的主要是位移数据。

```java

private Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
int count = 0;
……
while (true) {
    ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
    for (ConsumerRecord<String, String> record: records) {
        process(record);  // 处理消息
        offsets.put(new TopicPartition(record.topic(), record.partition()),
                    new OffsetAndMetadata(record.offset() + 1);
        if(count % 100 == 0) {
           consumer.commitAsync(offsets, null); // 回调处理逻辑是null 
        }
        count++;
	}
}
```

#### CommitFailedException 

##### 场景1：消费超时

要防止这种场景下抛出异常，需要简化消息处理逻辑。有 4 种方法。

1. **缩短单条消息处理的时间**。比如，之前下游系统消费一条消息的时间是 100 毫秒，优化后下降到 50 毫秒，那么Consumer 端的 TPS 就提升一倍。
2. **增加 Consumer 端允许下游系统消费一批消息的最大时长**。取决于 Consumer 端参数 **max.poll.interval.ms** 。在最新版的 Kafka 中，该参数的默认值是 5 分钟。如果消费逻辑不能简化，那么提高该参数值是一个不错的办法。Kafka 0.10.1.0 之前的版本是没有这个参数的，因此如果你依然在使用 0.10.1.0 之前的客户端 API，那么你需要增加 **session.timeout.ms** 参数的值。不幸的是，session.timeout.ms 参数还有其他的含义，因此增加该参数的值可能会有其他方面的“不良影响”，这也是社区在 0.10.1.0 版本引入 max.poll.interval.ms 参数，将这部分含义从 session.timeout.ms 中剥离出来的原因之一。
3. **减少下游系统一次性消费的消息总数。**取决于 Consumer 端参数 **max.poll.records** 的值。该参数的默认值是 500 条，表明调用一次 KafkaConsumer.poll 方法，最多返回 500 条消息。该参数规定了单次 poll 方法能够返回的消息总数的上限。如果前两种方法都不适用的话，降低此参数值是避免 CommitFailedException 异常最简单的手段。
4. **下游系统使用多线程来加速消费**。下游系统手动创建多个消费线程处理 poll 方法返回的一批消息。之前使用 Kafka Consumer 消费数据更多是单线程的，所以当消费速度无法匹配 Kafka Consumer 消息返回的速度时，就会抛出 CommitFailedException 异常。如果是多线程，就可以灵活地控制线程数量，随时调整消费承载能力，再配以目前多核硬件条件，该方法可以有效的预防CommitFailedException 的产生。很多主流的大数据流处理框架使用这个方法，比如 Apache Flink 在集成 Kafka 时，就创建了多个 KafkaConsumerThread 线程，自行处理多线程间的数据消费。不过，凡事有利就有弊，这个方法实现起来并不容易，特别是在多个线程间如何处理位移提交这个问题上，更是极容易出错。

##### 场景2：Consumer配置问题

​		Kafka Java Consumer 端提供名为 Standalone Consumer 的**独立消费者**。它没有消费者组的概念，每个消费者实例都是独立工作的，彼此之间无联系。需要注意，独立消费者的位移提交机制和消费者组是一样的，因此独立消费者的位移提交也必须遵守之前说的那些规定，比如独立消费者也要指定 group.id 参数才能提交位移。消费者组和独立消费者在使用之前都要指定 group.id		

​		假如**应用中同时出现了设置相同 group.id 值的消费者组程序和独立消费者程序**，当独立消费者程序手动提交位移时，Kafka 就会立即抛出 CommitFailedException 异常，因为 Kafka 无法识别这个具有相同 group.id 的消费者实例，于是就向它返回一个错误，表明它不是消费者组内合法的成员。

### 3.12 多线程开发消费者实例

​		KafkaConsumer 类不是线程安全的 (thread-safe)。所有的网络 I/O 处理都是发生在用户主线程中，在使用过程中必须要确保线程安全。不能在多个线程中共享同一个 KafkaConsumer 实例，否则程序会抛出 ConcurrentModificationException 异常。KafkaConsumer 中 wakeup() 是例外，可以在其他线程中安全地调用 KafkaConsumer.wakeup() 来唤醒 Consumer。

##### 多线程方案

1. **消费者程序启动多个线程，每个线程维护专属的 KafkaConsumer 实例，负责完整的消息获取、消息处理流程。**如下图所示：

   ![img](E:\data\my-document\kafka\assets\25c8e38237117c57047997ecba5dd52c.png)

2. **消费者程序使用单或多线程获取消息，同时创建多个消费线程执行消息处理逻辑。**获取消息的线程可以是一个，也可以是多个，每个线程维护专属的 KafkaConsumer 实例，处理消息则交由特定的线程池来做，从而实现消息获取与消息处理的真正解耦。具体架构如下图所示：

   ![img](E:\data\my-document\kafka\assets\deb0d00a5ede97f270cf42a255287fc1.png)

   两种方案的优缺点：

   <img src="E:\data\my-document\kafka\assets\4070c15055bf275c44cb7b470fb1f850.jpeg" alt="img" style="zoom:80%;" />

   ##### 方案1示例代码

   ```java
   
   public class KafkaConsumerRunner implements Runnable {
        private final AtomicBoolean closed = new AtomicBoolean(false);
        private final KafkaConsumer consumer;
   
   
        public void run() {
            try {
                consumer.subscribe(Arrays.asList("topic"));
                while (!closed.get()) {
         			ConsumerRecords records = 
           		consumer.poll(Duration.ofMillis(10000));
                    //  执行消息处理逻辑
                }
            } catch (WakeupException e) {
                // Ignore exception if closing
                if (!closed.get()) throw e;
            } finally {
                consumer.close();
            }
        }
   
   
        // Shutdown hook which can be called from a separate thread
        public void shutdown() {
            closed.set(true);
            consumer.wakeup();
        }
   ```

   ##### 方案2示例代码

   ```java
   
   private final KafkaConsumer<String, String> consumer;
   private ExecutorService executors;
   ...
   
   
   private int workerNum = ...;
   executors = new ThreadPoolExecutor(
     workerNum, workerNum, 0L, TimeUnit.MILLISECONDS,
     new ArrayBlockingQueue<>(1000), 
     new ThreadPoolExecutor.CallerRunsPolicy());
   
   
   ...
   while (true)  {
     ConsumerRecords<String, String> records = 
       consumer.poll(Duration.ofSeconds(1));
     for (final ConsumerRecord record : records) {
       executors.submit(new Worker(record));
     }
   }
   ..
   ```

   

### 3.13 实现消费者组消费进度监控

## 四、深入kafka内核

## 五、管理与监控