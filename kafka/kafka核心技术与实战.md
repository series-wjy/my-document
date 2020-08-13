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

#### 命令行工具

```shell

$ bin/kafka-consumer-groups.sh --bootstrap-server <Kafka broker连接信息> --describe --group <group名称>
```

#### Consumer API

```java

public static Map<TopicPartition, Long> lagOf(String groupID, String bootstrapServers) throws TimeoutException {
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        try (AdminClient client = AdminClient.create(props)) {
            // 获取给定消费者组的最新消费消息的位移
            ListConsumerGroupOffsetsResult result = client.listConsumerGroupOffsets(groupID);
            try {
                Map<TopicPartition, OffsetAndMetadata> consumedOffsets = result.partitionsToOffsetAndMetadata().get(10, TimeUnit.SECONDS);
                props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // 禁止自动提交位移
                props.put(ConsumerConfig.GROUP_ID_CONFIG, groupID);
                props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
                props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
                try (final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
                    // 获取订阅分区的最新消息位移
                    Map<TopicPartition, Long> endOffsets = consumer.endOffsets(consumedOffsets.keySet());
                    // 执行相应的减法操作，获取 Lag 值并封装进一个 Map 对象
                    return endOffsets.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(),
                            entry -> entry.getValue() - consumedOffsets.get(entry.getKey()).offset()));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // 处理中断异常
                // ...
                return Collections.emptyMap();
            } catch (ExecutionException e) {
                // 处理ExecutionException
                // ...
                return Collections.emptyMap();
            } catch (TimeoutException e) {
                throw new TimeoutException("Timed out when getting lag for consumer group " + groupID);
            }
        }
    }
```

#### JMX监控指标

Kafka 消费者提供了一个名为 kafka.consumer:type=consumer-fetch-manager-metrics,client-id=“{client-id}”的 JMX 指标，有两组属性：records-lag-max 和 records-lead-min，它们分别表示此消费者在测试窗口时间内曾经达到的最大的 Lag 值和最小的 Lead 值。一旦监测到 Lead 越来越小，甚至是快接近于 0 ，就一定要小心，这可能预示着消费者端要丢消息了。Kafka 消费者还在分区级别提供了额外的 JMX 指标，用于单独监控分区级别的 Lag 和 Lead 值。

## 四、深入kafka内核

### 4.1 副本机制

#### 副本定义和角色

副本机制的好处：

1. **提供数据冗余**。即使系统部分组件失效，系统依然能够继续运转，因而增加了整体可用性以及数据持久性。
2. **提供高伸缩性**。支持横向扩展，能够通过增加机器的方式来提升读性能，进而提高读操作吞吐量。
3. **改善数据局部性**。允许将数据放入与用户地理位置相近的地方，从而降低系统延时。

​        kafka只提供第一种副本机制带来的优势，是 Kafka 确保系统高可用和消息高持久性的重要基石。所谓副本（Replica），本质就是一个只能追加写消息的提交日志。根据 Kafka 副本机制的定义，同一个分区下的所有副本保存有相同的消息序列，这些副本分散保存在不同的 Broker 上，从而能够对抗部分 Broker 宕机带来的数据不可用。

<img src="E:\data\my-document\kafka\assets\b600557f4f11dcc644813f46cbbc03d3.png" alt="img" style="zoom:80%;" />

kafka基于领导者（Leader-based）的副本机制。

<img src="E:\data\my-document\kafka\assets\2fa6fef8d596f046b628a3befa8d6d9f.png" alt="img" style="zoom: 33%;" />

1. 在 Kafka 中，副本分成两类：领导者副本（Leader Replica）和追随者副本（Follower Replica）。每个分区在创建时都要选举一个副本，称为领导者副本，其余的副本为追随者副本。
2. Kafka 的副本机制比其他分布式系统要更严格一些。在 Kafka 中，**追随者副本是不对外提供服务的**。追随者副本不能响应消费者和生产者的读写请求。所有的请求都必须由**领导者副本**来处理。追随者副本不处理客户端请求，它唯一的任务就是从领导者副本异步拉取消息，并写入到自己的提交日志中，从而实现与领导者副本的同步。
3. 当领导者副本所在的 Broker 宕机时，Kafka 依托于 ZooKeeper 提供的监控功能能够实时感知到，并立即开启新一轮的领导者选举，从追随者副本中选一个作为新的领导者。旧的 Leader 副本重启回来后，只能作为追随者副本加入到集群中。

这种副本机制有两个方面的好处：

1. **方便实现“Read-your-writes”**。所谓 Read-your-writes，顾名思义就是，当你使用生产者 API 向 Kafka 成功写入消息后，马上使用消费者 API 去读取刚才生产的消息。举个例子，比如发微博时，发完一条微博，肯定是希望能立即看到，这就是典型的 Read-your-writes 场景。如果允许追随者副本对外提供服务，由于副本同步是异步的，因此有可能出现追随者副本还没有从领导者副本那里拉取到最新的消息，从而使得客户端看不到最新写入的消息。
2. **方便实现单调读（Monotonic Reads）**。就是对于一个消费者用户而言，因为副本的同步时间存在差异，在多次消费消息时，是从不同的副本读取的话，有可能出现某条消息一会儿存在一会儿不存在。

#### In-sync Replicas（ISR）

ISR 不只是追随者副本集合，它必然包括 Leader 副本。甚至在某些情况下，ISR 只有 Leader 这一个副本。

<img src="E:\data\my-document\kafka\assets\df4824e3ae53e7aebd03c38d8859aae0.png" alt="img" style="zoom:33%;" />

​        Kafka 判断 Follower 是否与 Leader 同步的标准就是 Broker 端参数 **replica.lag.time.max.ms** 参数值，默认10秒。如果这个同步过程的速度持续慢于 Leader 副本的消息写入速度，那么在 replica.lag.time.max.ms 时间后，此 Follower 副本就会被认为是与 Leader 副本不同步，Kafka 会自动收缩 ISR 集合，将该副本“踢出”ISR。当 Follower 追上了 Leader 的进度，那么它会重新被加回 ISR 的。这也表明，ISR 是一个动态调整的集合。

​        Kafka 把所有不在 ISR 中的存活副本都称为非同步副本。非同步副本落后 Leader 太多，如果选择这些副本作为新 Leader，可能出现数据丢失。选举这种副本的过程称为 Unclean Leader Election。Broker 端参数 **unclean.leader.election.enable** 控制是否允许 Unclean 领导者选举。**建议将该参数设置为false**。

### 4.2 请求处理流程

​        Reactor 模式是事件驱动架构的一种实现方式，特别适合应用于处理多个客户端并发向服务器端发送请求的场景。Reactor 模式的架构如下图所示：

<img src="E:\data\my-document\kafka\assets\654b83dc6b24d89c138938c15d2e8352.png" alt="img" style="zoom: 33%;" />

​        多个客户端会发送请求给到 Reactor。Reactor 有个请求分发线程 Dispatcher，也就是图中的 Acceptor，它会将不同的请求下发到多个工作线程中处理。

Kafka 画一张类似的图的话，那它应该是这个样子的：

<img src="E:\data\my-document\kafka\assets\e1ae8884999175dac0c6e21beb2f7e6e.png" alt="img" style="zoom: 33%;" />

​        Kafka 的 Broker 端有个 SocketServer 组件，类似于 Reactor 模式中的 Dispatcher，它有对应的 Acceptor 线程和一个工作线程池，在 Kafka 中，这个工作线程池有个专属的名字，叫**网络线程池**。Kafka 提供了 Broker 端参数 **num.network.threads**，用于调整该网络线程池的线程数。其默认值是 3，表示每台 Broker 启动时会创建 3 个网络线程，专门处理客户端发送的请求。Acceptor 线程采用轮询的方式将入站请求公平地发到所有网络线程中。

当网络线程接收到请求后，Kafka 在这个环节又做了一层异步线程池的处理，如下图：

<img src="E:\data\my-document\kafka\assets\d8a7d6f0bdf9dc3af4ff55ff79b42068.png" alt="img" style="zoom:33%;" />

​        当网络线程拿到请求后，它不是自己处理，而是将请求放入到一个共享请求队列中。Broker 端还有个 IO 线程池，负责从该队列中取出请求，执行真正的处理。如果是 PRODUCE 生产请求，则将消息写入到底层的磁盘日志中；如果是 FETCH 请求，则从磁盘或页缓存中读取消息。

​        IO 线程池处中的线程才是执行请求逻辑的线程。Broker 端参数 **num.io.threads** 控制了这个线程池中的线程数。**目前该参数默认值是 8，表示每台 Broker 启动后自动创建 8 个 IO 线程处理请求**。可以根据实际硬件条件设置此线程池的个数。如果机器上 CPU 资源非常充裕，可以调大该参数，允许更多的并发请求被同时处理。当 IO 线程处理完请求后，会将生成的响应发送到网络线程池的响应队列中，然后由对应的网络线程负责将 Response 返还给客户端。

​		**请求队列和响应队列的差别**：请求队列是所有网络线程共享的，而响应队列则是每个网络线程专属的。这么设计的原因就在于，Dispatcher 只是用于请求分发而不负责响应回传，因此只能让每个网络线程自己发送 Response 给客户端，所以这些 Response 也就没必要放在一个公共的地方。

​		图中有一个叫 Purgatory 的组件，这是 Kafka 中著名的“炼狱”组件。它是用来**缓存延时请求（Delayed Request）**的。所谓延时请求，就是那些一时未满足条件不能立刻处理的请求。比如设置了 acks=all 的 PRODUCE 请求，一旦设置了 acks=all，那么该请求就必须等待 ISR 中所有副本都接收了消息后才能返回，此时处理该请求的 IO 线程就必须等待其他 Broker 的写入结果。当请求不能立刻处理时，它就会暂存在 Purgatory 中。稍后一旦满足了完成条件，IO 线程会继续处理该请求，并将 Response 放入对应网络线程的响应队列中。

​		Kafka 社区把 PRODUCE 和 FETCH 这类请求称为数据类请求，把 LeaderAndIsr、StopReplica 这类请求称为控制类请求。控制类请求有这样一种能力：**它可以直接令数据类请求失效**！如果数据类请求和控制类请求使用同一个请求缓存，那么有可能造成请求的积压和不断重试，这样就不能快速的响应客户端。

​		社区于 2.3 版本正式实现了**数据类请求和控制类请求的分离**。Kafka Broker 启动后，会在后台分别创建两套网络线程池和 IO 线程池的组合，它们分别处理数据类请求和控制类请求。至于所用的 Socket 端口，自然是使用不同的端口了，你需要提供不同的 listeners 配置，显式地指定哪套端口用于处理哪类请求。

### 4.3 消费者组重平衡全流程解析

​		**重平衡过程是通过消费者端的心跳线程（Heartbeat Thread）通知消费者端。**消费者端参数 heartbeat.interval.ms 的真实用途，从字面上看，它就是设置了心跳的间隔时间，但这个参数的真正作用是**控制重平衡通知的频率**。

#### 消费者组状态机

​		Kafka 为消费者组定义了 5 种状态，它们分别是：Empty、Dead、PreparingRebalance、CompletingRebalance 和 Stable。这 5 种状态的含义如下表格：

![img](E:\data\my-document\kafka\assets\3c281189cfb1d87173bc2d4b8149f38b.jpeg)

​			状态机的状态流转如下图：

<img src="E:\data\my-document\kafka\assets\f16fbcb798a53c21c3bf1bcd5b72b006.png" alt="img"  />

​		一个消费者组最开始是 Empty 状态，当重平衡过程开启后，它会被置于 PreparingRebalance 状态等待成员加入，之后变更到 CompletingRebalance 状态等待分配方案，最后流转到 Stable 状态完成重平衡。当有新成员加入或已有成员退出时，消费者组的状态从 Stable 直接跳到 PreparingRebalance 状态，此时，所有现存成员就必须重新申请加入组。当所有成员都退出组后，消费者组状态变更为 Empty。

​		Kafka 定期自动删除过期位移的条件就是，组要处于 Empty 状态。因此，如果你的消费者组停掉了很长时间（超过 7 天），那么 Kafka 很可能就把该组的位移数据删除了。在Kafka 的日志中一定经常看到下面这个输出：	

> Removed ✘✘✘ expired offsets in ✘✘✘ milliseconds.

这就是 Kafka 在尝试定期删除过期位移。只有 Empty 状态下的组，才会执行过期位移删除的操作。

#### 消费者端重平衡流程

​		在消费者端，重平衡分为两个步骤：分别是**加入组**和**等待领导者消费者（Leader Consumer）分配方案**。这两个步骤分别对应两类特定的请求：JoinGroup 请求和 SyncGroup 请求。**领导者消费者的任务是收集所有成员的订阅信息，然后根据这些信息，制定具体的分区消费分配方案。**

​		选出领导者之后，协调者会把消费者组订阅信息封装进 JoinGroup 请求的响应体中，然后发给领导者，由领导者统一做出分配方案后，进入到下一步：发送 SyncGroup 请求。在这一步中，领导者向协调者发送 SyncGroup 请求，将刚刚做出的分配方案发给协调者。值得注意的是，其他成员也会向协调者发送 SyncGroup 请求，只不过请求体中并没有实际的内容。这一步的主要目的是让协调者接收分配方案，然后统一以 SyncGroup 响应的方式分发给所有成员，这样组内所有成员就都知道自己该消费哪些分区了。

下图形象地说明一下 JoinGroup 请求的处理过程：

<img src="E:\data\my-document\kafka\assets\e7d40ce1c34d66ec36bfdaaa3ec9611f.png" alt="img" style="zoom: 33%;" />

下图描述的是 SyncGroup 请求的处理流程：

<img src="E:\data\my-document\kafka\assets\6252b051450c32c143f03410f6c2b75d.png" alt="img" style="zoom:33%;" />

​		SyncGroup 请求的主要目的，就是让协调者把领导者制定的分配方案下发给各个组内成员。当所有成员都成功接收到分配方案后，消费者组进入到 Stable 状态，即开始正常的消费工作。

#### Broker 端重平衡场景剖析

##### 场景一：新成员入组

​		新成员入组是指组处于 Stable 状态后，有新成员加入。如果是全新启动一个消费者组，Kafka 是有一些自己的优化，流程上会有些许的不同。这里讨论的是，组稳定了之后有新成员加入的情形。

​		当协调者收到新的 JoinGroup 请求后，它会通过心跳请求响应的方式通知组内现有的所有成员，强制它们开启新一轮的重平衡。具体的过程和之前的客户端重平衡流程是一样的。现在，我用一张时序图来说明协调者一端是如何处理新成员入组的。

<img src="E:\data\my-document\kafka\assets\62f85fb0b0f06989dd5a6f133599ca33.png" alt="img" style="zoom: 50%;" />

##### 场景二：组成员主动离组

​		所谓主动离组，就是指消费者实例所在线程或进程调用 close() 方法主动通知协调者它要退出。这个场景就涉及到了第三类请求：LeaveGroup 请求。协调者收到 LeaveGroup 请求后，依然会以心跳响应的方式通知其他成员，因此就不再赘述了，还是直接用一张图来说明。

![img](E:\data\my-document\kafka\assets\867245cbf6cfd26573aba1816516b26b.png)

##### 场景三：组成员崩溃离组

​		崩溃离组是指消费者实例出现严重故障，突然宕机导致的离组。它和主动离组是有区别的，因为后者是主动发起的离组，协调者能马上感知并处理。但崩溃离组是被动的，协调者通常需要等待一段时间才能感知到，这段时间一般是由消费者端参数 **session.timeout.ms** 控制的。也就是说，Kafka 一般不会超过 session.timeout.ms 就能感知到这个崩溃。后面处理崩溃离组的流程与之前是一样的，看下面这张图。

![img](E:\data\my-document\kafka\assets\bc00d35060e1a4216e177e5b361ad40c.png)

##### 场景四：重平衡时协调者对组内成员提交位移的处理

​		正常情况下，每个组内成员都会定期汇报位移给协调者。当重平衡开启时，协调者会给予成员一段缓冲时间，要求每个成员必须在这段时间内快速地上报自己的位移信息，然后再开启正常的 JoinGroup/SyncGroup 请求发送。使用一张图来说明。

![img](E:\data\my-document\kafka\assets\83b77094d4170b9057cedfed9cdb33be.png)

### 4.4 kafka控制器

#### 控制器组件

​		控制器组件（Controller），是 Apache Kafka 的核心组件。它的主要作用是在 Apache ZooKeeper 的帮助下管理和协调整个 Kafka 集群。

控制器的职责大致可以分为 5 种：

1. 主题管理（创建、删除、增加分区）

   这里的主题管理，就是指控制器帮助我们完成对 Kafka 主题的创建、删除以及分区增加的操作。当执行 kafka-topics 脚本时，大部分的后台工作都是控制器来完成的。

2. 分区重分配

   分区重分配主要是指，kafka-reassign-partitions 脚本提供的对已有主题分区进行细粒度的分配功能。

3. Preferred 领导者选举

   Preferred 领导者选举主要是 Kafka 为了避免部分 Broker 负载过重而提供的一种换 Leader 的方案。

4. 集群成员管理（新增 Broker、Broker 主动关闭、Broker 宕机）

   自动检测新增 Broker、Broker 主动关闭及被动宕机。这种自动检测是依赖于前面提到的 Watch 功能和 ZooKeeper 临时节点组合实现的。比如，控制器组件会利用 Watch 机制检查 ZooKeeper 的 /brokers/ids 节点下的子节点数量变更。目前，当有新 Broker 启动后，它会在 /brokers 下创建专属的 znode 节点。一旦创建完毕，ZooKeeper 会通过 Watch 机制将消息通知推送给控制器，这样，控制器就能自动地感知到这个变化，进而开启后续的新增 Broker 作业。侦测 Broker 存活性则是依赖于刚刚提到的另一个机制：临时节点。每个 Broker 启动后，会在 /brokers/ids 下创建一个临时 znode。当 Broker 宕机或主动关闭后，该 Broker 与 ZooKeeper 的会话结束，这个 znode 会被自动删除。同理，ZooKeeper 的 Watch 机制将这一变更推送给控制器，这样控制器就能知道有 Broker 关闭或宕机了，从而进行后续操作。

5. 数据服务

   向其他 Broker 提供数据服务。控制器上保存了最全的集群元数据信息，其他所有 Broker 会定期接收控制器发来的元数据更新请求，从而更新其内存中的缓存数据。

控制器中到底保存的数据：

<img src="E:\data\my-document\kafka\assets\38ff78fdeb2a86943ae60f15c3ad28c8.jpg" alt="img" style="zoom: 25%;" />

比较重要的数据有：

+ 所有主题信息。包括具体的分区信息，比如领导者副本是谁，ISR 集合中有哪些副本等。所有 Broker 信息。
+ 包括当前都有哪些运行中的 Broker，哪些正在关闭中的 Broker 等。
+ 所有涉及运维任务的分区。包括当前正在进行 Preferred 领导者选举以及分区重分配的分区列表。

**值得注意的是，这些数据其实在 ZooKeeper 中也保存了一份。**

#### 故障转移

​		故障转移指的是，当运行中的控制器突然宕机或意外终止时，Kafka 能够快速地感知到，并立即启用备用控制器来代替之前失败的控制器。

<img src="E:\data\my-document\kafka\assets\128903a88ea1c9dd27f6a62e496b44ed.jpg" alt="img" style="zoom: 25%;" />

#### 控制器内部设计原理

​		在 Kafka 0.11 版本之前，控制器的设计繁琐，代码混乱，导致社区中很多控制器方面的 Bug 都无法修复。控制器是多线程的设计，会在内部创建很多个线程。比如，控制器需要为每个 Broker 都创建一个对应的 Socket 连接，然后再创建一个专属的线程，用于向这些 Broker 发送特定请求。如果集群中的 Broker 数量很多，那么控制器端需要创建的线程就会很多。另外，控制器连接 ZooKeeper 的会话，也会创建单独的线程来处理 Watch 机制的通知回调。除了以上这些线程，控制器还会为主题删除创建额外的 I/O 线程。比起多线程的设计，更糟糕的是，这些线程还会访问共享的控制器缓存数据。为了保护数据安全性，控制器不得不在代码中大量使用 ReentrantLock 同步机制，这就进一步拖慢了整个控制器的处理速度。

​		鉴于这些原因，社区于 0.11 版本重构了控制器的底层设计，最大的改进就是，把多线程的方案改成了单线程加事件队列的方案。

<img src="E:\data\my-document\kafka\assets\b14c6f2d246cbf637f2fda5dae1688e5.png" alt="img" style="zoom: 33%;" />

​		社区引入了一个**事件处理线程**，统一处理各种控制器事件，控制器将操作全部封装成一个个独立的事件，发送到事件队列中，供此线程消费。这就是**单线程 + 队列的实现方式**。值得注意的是，这里的单线程不代表之前提到的所有线程都被去掉了，控制器只是把**缓存状态变更**方面的工作委托给了这个线程而已。这个方案的最大好处在于，控制器缓存中保存的状态只被一个线程处理，因此不再需要重量级的线程同步机制来维护线程安全，Kafka 不用再担心多线程并发访问的问题。针对控制器的第二个改进，将之前同步操作 ZooKeeper 全部改为异步操作。ZooKeeper 本身的 API 提供了同步写和异步写两种方式。之前控制器操作 ZooKeeper 使用的是同步的 API，性能很差，集中表现为，当有大量主题分区发生变更时，ZooKeeper 容易成为系统的瓶颈。新版本 Kafka 采用异步 API 写入 ZooKeeper，性能有了很大的提升。根据社区的测试，改成异步之后，ZooKeeper 写入提升了 10 倍！

​		老版本 Broker 对接收的所有请求统一处理。这种设计对于控制器发送的请求非常不公平，因为这类请求应该有更高的优先级。举个简单的例子，假设删除了某个主题，那么控制器就会给该主题所有副本所在的 Broker 发送一个名为 StopReplica 的请求。如果此时 Broker 上存有大量积压的 Produce 请求，那么这个 StopReplica 请求只能排队等。如果这些 Produce 请求就是要向该主题发送消息的话，会存在明显的弊端：主题都要被删除了，还要处理这些 Produce 请求。此时最合理的处理顺序应该是，赋予 StopReplica 请求更高的优先级，使它能够得到抢占式的处理。自 2.2 开始，Kafka 正式支持这种不同优先级请求的处理。Kafka 将控制器发送的请求与普通数据类请求分开，实现了控制器请求单独处理的逻辑。

### 4.5  高水位和Leader Epoch

#### 高水位

<img src="E:\data\my-document\kafka\assets\fb2c9e883b78c5d10b09b4a9773b8c13.png" alt="img" style="zoom: 33%;" />

图中标注“Completed”的蓝色部分代表已完成的工作，标注“In-Flight”的红色部分代表正在进行中的工作，两者的边界就是水位线。

​		在 Kafka 中，水位的概念有一点不同。Kafka 的水位不是时间戳，更与时间无关。它是和位置信息绑定的，它是用消息位移来表征的。另外，Kafka 源码使用的表述是高水位（High Watermark），值得注意的是，Kafka 中也有低水位（Low Watermark），它是与 Kafka 删除消息相关联的概念。

#### 高水位的作用

高水位的作用主要有 2 个：

1. 定义消息可见性，即用来标识分区下的哪些消息是可以被消费者消费的。
2. 帮助 Kafka 完成副本同步。

<img src="E:\data\my-document\kafka\assets\c2243d5887f0ca7a20a524914b85a8dd.png" alt="img" style="zoom: 33%;" />

​		在分区高水位以下的消息被认为是已提交消息，反之就是未提交消息。消费者只能消费已提交消息，即图中位移小于 8 的所有消息。注意，这里不讨论 Kafka 事务，因为事务机制会影响消费者所能看到的消息的范围，它不只是简单依赖高水位来判断。它依靠一个名为 LSO（Log Stable Offset）的位移值来判断事务型消费者的可见性。**位移值等于高水位的消息也属于未提交消息。也就是说，高水位上的消息是不能被消费者消费的。**

​		图中还有一个日志末端位移的概念，即 Log End Offset，简写是 LEO。它表示副本写入下一条消息的位移值。注意，数字 15 所在的方框是虚线，这就说明，这个副本当前只有 15 条消息，位移值是从 0 到 14，下一条新消息的位移是 15。显然，介于高水位和 LEO 之间的消息就属于未提交消息。这也表明一个重要的事实：**同一个副本对象，其高水位值不会大于 LEO 值。**

​		**高水位和 LEO 是副本对象的两个重要属性**。Kafka 所有副本都有对应的高水位和 LEO 值，而不仅仅是 Leader 副本。只不过 Leader 副本比较特殊，**Kafka 使用 Leader 副本的高水位来定义所在分区的高水位**。也就是说，分区的高水位就是其 Leader 副本的高水位。

#### 高水位更新机制

际上，在 Leader 副本所在的 Broker 上，还保存了其他 Follower 副本的 LEO 值。

<img src="E:\data\my-document\kafka\assets\be0c738f34e3cd1d95d509f16cbb7f82.png" alt="img" style="zoom: 33%;" />

​		Broker 0 上保存了某分区的 Leader 副本和所有 Follower 副本的 LEO 值，而 Broker 1 上仅仅保存了该分区的某个 Follower 副本。Kafka 把 Broker 0 上保存的这些 Follower 副本又称为远程副本（Remote Replica）。Kafka 副本机制在运行过程中，会更新 Broker 1 上 Follower 副本的高水位和 LEO 值，同时也会更新 Broker 0 上 Leader 副本的高水位和 LEO 以及所有远程副本的 LEO，**但它不会更新远程副本的高水位值，也就是图中标记为灰色的部分**。远程副本的主要作用是，**帮助 Leader 副本确定其高水位，也就是分区高水位**。

<img src="E:\data\my-document\kafka\assets\c81e888761b5f04822216845be981649.jpeg" alt="img" style="zoom: 80%;" />

 Leader 副本保持同步的判断条件有两个：

1. 该远程 Follower 副本在 ISR 中。
2. 该远程 Follower 副本 LEO 值落后于 Leader 副本 LEO 值的时间，不超过 Broker 端参数 **replica.lag.time.max.ms** 的值。如果使用默认值的话，就是不超过 10 秒。

​        这两个条件看起来好像是一回事，因为目前某个副本能否进入 ISR 就是靠第 2 个条件判断的。但有时候，会发生这样的情况：即 Follower 副本已经“追上”了 Leader 的进度，却不在 ISR 中，比如某个刚刚重启回来的副本。如果 Kafka 只判断第 1 个条件的话，就可能出现某些副本具备了“进入 ISR”的资格，但却尚未进入到 ISR 中的情况。此时，分区高水位值就可能超过 ISR 中副本 LEO，而高水位 > LEO 的情形是不被允许的。

Leader 副本和 Follower 副本两个维度，来总结一下高水位和 LEO 的更新机制。

**Leader副本**

处理生产者请求的逻辑如下：

1. 写入消息到本地磁盘。
2. 更新分区高水位值。
   + i. 获取 Leader 副本所在 Broker 端保存的所有远程副本 LEO 值（LEO-1，LEO-2，……，LEO-n）。
   + ii. 获取 Leader 副本高水位值：currentHW。
   + iii. 更新 currentHW = max{currentHW, min（LEO-1, LEO-2, ……，LEO-n）}。

处理 Follower 副本拉取消息的逻辑如下：

1. 读取磁盘（或页缓存）中的消息数据。
2. 使用 Follower 副本发送请求中的位移值更新远程副本 LEO 值。
3. 更新**分区高水位**值（具体步骤与处理生产者请求的步骤相同）。

**Follower副本**

从 Leader 拉取消息的处理逻辑如下：

1. 写入消息到本地磁盘。
2. 更新 LEO 值。
3. 更新高水位值。
   + i. 获取 Leader 发送的高水位值：currentHW。
   + ii. 获取步骤 2 中更新过的 LEO 值：currentLEO。
   + iii. 更新高水位为 min(currentHW, currentLEO)。

#### 副本同步机制解析

​		当生产者发送一条消息时，Leader 和 Follower 副本对应的高水位的更新流程。

首先是初始状态。下面这张图中的 remote LEO 就是刚才的远程副本的 LEO 值。在初始状态时，所有值都是 0。

<img src="E:\data\my-document\kafka\assets\2ecec2915d1a52f136517d15192a4c72.png" alt="img" style="zoom: 67%;" />

当生产者给主题分区发送一条消息后，状态变更为：

<img src="E:\data\my-document\kafka\assets\42841bfd3d5d4fa8560e176cb9d20b5b.png" alt="img" style="zoom:67%;" />

此时，Leader 副本成功将消息写入了本地磁盘，故 LEO 值被更新为 1。

Follower 再次尝试从 Leader 拉取消息。和之前不同的是，这次有消息可以拉取了，因此状态进一步变更为：

<img src="E:\data\my-document\kafka\assets\f65911a5c247ad83826788fd275e1ade.png" alt="img" style="zoom:67%;" />

这时，Follower 副本也成功地更新 LEO 为 1。此时，Leader 和 Follower 副本的 LEO 都是 1，但各自的高水位依然是 0，还没有被更新。它们需要在下一轮的拉取中被更新，如下图所示：

<img src="E:\data\my-document\kafka\assets\f30a4651605352db542b76b3512df110.png" alt="img" style="zoom:67%;" />

在新一轮的拉取请求中，由于位移值是 0 的消息已经拉取成功，因此 Follower 副本这次请求拉取的是位移值 =1 的消息。Leader 副本接收到此请求后，更新远程副本 LEO 为 1，然后更新 Leader 高水位为 1。做完这些之后，它会将当前已更新过的高水位值 1 发送给 Follower 副本。Follower 副本接收到以后，也将自己的高水位值更新成 1。至此，一次完整的消息同步周期就结束了。Kafka 就是利用这样的机制，实现了 Leader 和 Follower 副本之间的同步。

#### Leader Epoch

​		**Follower 副本的高水位更新**需要一轮额外的拉取请求才能实现。如果把上面那个例子扩展到多个 Follower 副本，情况可能更糟，也许需要多轮拉取请求。也就是说，Leader 副本高水位更新和 Follower 副本高水位更新在时间上是存在错配的。这种错配是很多“数据丢失”或“数据不一致”问题的根源。基于此，社区在 0.11 版本正式引入了 Leader Epoch 概念，来规避因高水位更新错配导致的各种不一致问题。

所谓 Leader Epoch，大致可以认为是 Leader 版本。它由两部分数据组成：

1. Epoch。一个单调增加的版本号。每当副本领导权发生变更时，都会增加该版本号。小版本号的 Leader 被认为是过期 Leader，不能再行使 Leader 权力。
2. 起始位移（Start Offset）。Leader 副本在该 Epoch 值上写入的首条消息的位移。

​        举个例子来说明一下 Leader Epoch。假设现在有两个 Leader Epoch<0, 0> 和 <1, 120>，那么，第一个 Leader Epoch 表示版本号是 0，这个版本的 Leader 从位移 0 开始保存消息，一共保存了 120 条消息。之后，Leader 发生了变更，版本号增加到 1，新版本的起始位移是 120。

​		Kafka Broker 会在内存中为每个分区都缓存 Leader Epoch 数据，同时它还会定期地将这些信息持久化到一个 checkpoint 文件中。当 Leader 副本写入消息到磁盘时，Broker 会尝试更新这部分缓存。如果该 Leader 是首次写入消息，那么 Broker 会向缓存中增加一个 Leader Epoch 条目，否则就不做更新。这样，每次有 Leader 变更时，新的 Leader 副本会查询这部分缓存，取出对应的 Leader Epoch 的起始位移，以避免数据丢失和不一致的情况。

Leader Epoch 是如何防止数据丢失的：

<img src="E:\data\my-document\kafka\assets\69f8ccf346b568a7310c69de9863ca42.png" alt="img" style="zoom: 33%;" />

​		单纯依赖高水位是怎么造成数据丢失的。开始时，副本 A 和副本 B 都处于正常状态，A 是 Leader 副本。某个使用了默认 acks 设置的生产者程序向 A 发送了两条消息，A 全部写入成功，此时 Kafka 会通知生产者说两条消息全部发送成功。

​		现在假设 Leader 和 Follower 都写入了这两条消息，而且 Leader 副本的高水位也已经更新了，但 Follower 副本高水位还未更新——这是可能出现的。Follower 端高水位的更新与 Leader 端有时间错配。倘若此时副本 B 所在的 Broker 宕机，当它重启回来后，副本 B 会执行日志截断操作，将 LEO 值调整为之前的高水位值，也就是 1。这就是说，位移值为 1 的那条消息被副本 B 从磁盘中删除，此时副本 B 的底层磁盘文件中只保存有 1 条消息，即位移值为 0 的那条消息。

​		当执行完截断操作后，副本 B 开始从 A 拉取消息，执行正常的消息同步。如果就在拉取消息的同一时刻，副本 A 所在的 Broker 宕机了，那么 Kafka 只能让副本 B 成为新的 Leader，此时，当 A 回来后，需要执行相同的日志截断操作，即将高水位调整为与 B 相同的值，也就是 1。这样操作之后，位移值为 1 的那条消息就从这两个副本中被永远地抹掉了。这就是这张图要展示的数据丢失场景。

​		严格来说，这个场景发生的前提是 Broker 端参数 **min.insync.replicas** 设置为 1。此时一旦消息被写入到 Leader 副本的磁盘，就会被认为是“已提交状态”，但现有的时间错配问题导致 Follower 端的高水位更新是有滞后的。如果在这个短暂的滞后时间窗口内，接连发生 Broker 宕机，那么这类数据的丢失就是不可避免的。

如何利用 Leader Epoch 机制来规避这种数据丢失：

<img src="E:\data\my-document\kafka\assets\1078956136267ca958d82bfa16d825e1.png" alt="img" style="zoom: 33%;" />

​		场景和之前大致是类似，只不过引用 Leader Epoch 机制后，Follower 副本 B 重启回来后，需要向 A 发送一个特殊的请求去获取 Leader 的 LEO 值。在这个例子中，该值为 2。当获知到 Leader LEO=2 后，B 发现该 LEO 值不比它自己的 LEO 值小，而且缓存中也没有保存任何起始位移值 > 2 的 Epoch 条目，因此 B 无需执行任何日志截断操作。这是对高水位机制的一个明显改进，即副本是否执行日志截断不再依赖于高水位进行判断。

​		现在，副本 A 宕机了，B 成为 Leader。同样地，当 A 重启回来后，执行与 B 相同的逻辑判断，发现也不用执行日志截断，至此位移值为 1 的那条消息在两个副本中均得到保留。后面当生产者程序向 B 写入新消息时，副本 B 所在的 Broker 缓存中，会生成新的 Leader Epoch 条目：[Epoch=1, Offset=2]。之后，副本 B 会使用这个条目帮助判断后续是否执行日志截断操作。这样，通过 Leader Epoch 机制，Kafka 完美地规避了这种数据丢失场景。

**这套机制防止的是根据HW做日志截断出现数据不一致，不能防止任何情况下副本都正常工作。**

## 五、管理与监控

### 主题日常管理

**创建主题**

```shell

bin/kafka-topics.sh --bootstrap-server broker_host:port --create --topic my_topic_name  --partitions 1 --replication-factor 1
```

社区推荐使用 --bootstrap-server 而非 --zookeeper 的原因主要有两个：

1. 使用 --zookeeper 会绕过 Kafka 的安全体系。这就是说，即使你为 Kafka 集群设置了安全认证，限制了主题的创建，如果你使用 --zookeeper 的命令，依然能成功创建任意主题，不受认证体系的约束。这显然是 Kafka 集群的运维人员不希望看到的。
2. 使用 --bootstrap-server 与集群进行交互，越来越成为使用 Kafka 的标准姿势。换句话说，以后会有越来越少的命令和 API 需要与 ZooKeeper 进行连接。这样，我们只需要一套连接信息，就能与 Kafka 进行全方位的交互，不用像以前一样，必须同时维护 ZooKeeper 和 Broker 的连接信息。

**查询主题列表**

```shell
bin/kafka-topics.sh --bootstrap-server broker_host:port --list
```

**查询单个主题详细数据**

```shell
bin/kafka-topics.sh --bootstrap-server broker_host:port --describe --topic <topic_name>
```

**增加分区**

```shell
bin/kafka-topics.sh --bootstrap-server broker_host:port --alter --topic <topic_name> --partitions <新分区数>
```

这里要注意的是，指定的分区数一定要比原有分区数大，否则 Kafka 会抛出 InvalidPartitionsException 异常。

**修改主题级别参数**

```shell
bin/kafka-configs.sh --zookeeper zookeeper_host:port --entity-type topics --entity-name <topic_name> --alter --add-config max.message.bytes=10485760
```

为什么这个脚本就要指定 --zookeeper，而不是 --bootstrap-server 呢？其实，这个脚本也能指定 --bootstrap-server 参数，只是它是用来设置动态参数的。设置常规的主题级别参数，还是使用 --zookeeper。

**变更副本数**

使用自带的 kafka-reassign-partitions 脚本，帮助我们增加主题的副本数。

**修改主题限速**

要达到这个目的，必须先设置 Broker 端参数 leader.replication.throttled.rate 和 follower.replication.throttled.rate，命令如下：

```shell
bin/kafka-configs.sh --zookeeper zookeeper_host:port --alter --add-config 'leader.replication.throttled.rate=104857600,follower.replication.throttled.rate=104857600' --entity-type brokers --entity-name 0
```

命令结尾处的 --entity-name 就是 Broker ID。倘若该主题的副本分别在 0、1、2、3 多个 Broker 上，那么你还要依次为 Broker 1、2、3 执行这条命令。

设置好这个参数之后，我们还需要为该主题设置要限速的副本。在这个例子中，想要为所有副本都设置限速，因此统一使用通配符 * 来表示，命令如下：

```shell
bin/kafka-configs.sh --zookeeper zookeeper_host:port --alter --add-config 'leader.replication.throttled.replicas=*,follower.replication.throttled.replicas=*' --entity-type topics --entity-name test
```

**主题分区迁移**

同样是使用 kafka-reassign-partitions 脚本，对主题各个分区的副本进行“手术”般的调整，比如把某些分区批量迁移到其他 Broker 上。

**删除主题**

```shell
bin/kafka-topics.sh --bootstrap-server broker_host:port --delete  --topic <topic_name>
```

### Kafka动态配置

#### 动态参数

Broker Configs表中增加了 Dynamic Update Mode 列。该列有 3 类值，分别是 read-only、per-broker 和 cluster-wide。

+ **read-only**。被标记为 read-only 的参数和原来的参数行为一样，只有重启 Broker，才能令修改生效。
+ **per-broker**。被标记为 per-broker 的参数属于动态参数，修改它之后，只会在对应的 Broker 上生效。
+ **cluster-wide**。被标记为 cluster-wide 的参数也属于动态参数，修改它之后，会在整个集群范围内生效，也就是说，对所有 Broker 都生效。你也可以为具体的 Broker 修改 cluster-wide 参数。

#### 使用场景

动态 Broker 参数的使用场景通常包括但不限于以下几种：

+ 动态调整 Broker 端各种线程池大小，实时应对突发流量。
+ 动态调整 Broker 端连接信息或安全配置信息。
+ 动态更新 SSL Keystore 有效期。
+ 动态调整 Broker 端 Compact 操作性能。
+ 实时变更 JMX 指标收集器 (JMX Metrics Reporter)。

把静态参数加入的话，cluster-wide、per-broker 和 static 参数的优先级是这样的：per-broker 参数 > cluster-wide 参数 > static 参数 > Kafka 默认值。

#### 配置动态参数

以参数**unclean.leader.election.enable**为例，演示动态参数配置。

设置cluster-wide范围值：

```shell
$ bin/kafka-configs.sh --bootstrap-server kafka-host:port --entity-type brokers --entity-default --alter --add-config unclean.leader.election.enable=true
Completed updating default config for brokers in the cluster,
```

**要设置 cluster-wide 范围的动态参数，需要显式指定 entity-default**。现在，使用下面的命令来查看一下刚才的配置是否成功。

```shell
$ bin/kafka-configs.sh --bootstrap-server kafka-host:port --entity-type brokers --entity-default --describe
Default config for brokers in the cluster are:
  unclean.leader.election.enable=true sensitive=false synonyms={DYNAMIC_DEFAULT_BROKER_CONFIG:unclean.leader.election.enable=true}
```

删除参数:

```shell
# 删除cluster-wide范围参数
$ bin/kafka-configs.sh --bootstrap-server kafka-host:port --entity-type brokers --entity-default --alter --delete-config unclean.leader.election.enable
Completed updating default config for brokers in the cluster,


# 查看cluster-wide范围参数
$ bin/kafka-configs.sh --bootstrap-server kafka-host:port  --entity-type brokers --entity-default --describe
Default config for brokers in the cluster are:
```



设置per-broker范围值：

```shell
$ bin/kafka-configs.sh --bootstrap-server kafka-host:port --entity-type brokers --entity-name 1 --alter --add-config unclean.leader.election.enable=false
Completed updating config for broker: 1.
```

查看是否生效：

```shell
$ bin/kafka-configs.sh --bootstrap-server kafka-host:port --entity-type brokers --entity-name 1 --describe
Configs for broker 1 are:
  unclean.leader.election.enable=false sensitive=false synonyms={DYNAMIC_BROKER_CONFIG:unclean.leader.election.enable=false, DYNAMIC_DEFAULT_BROKER_CONFIG:unclean.leader.election.enable=true, DEFAULT_CONFIG:unclean.leader.election.enable=false}
```

删除参数：

```shell
# 删除per-broker范围参数
$ bin/kafka-configs.sh --bootstrap-server kafka-host:port --entity-type brokers --entity-name 1 --alter --delete-config unclean.leader.election.enable
Completed updating config for broker: 1.


# 查看Broker 1上的动态参数配置
$ bin/kafka-configs.sh --bootstrap-server kafka-host:port  --entity-type brokers --entity-name 1 --describe
Configs for broker 1 are:
```



### 特殊主题的管理与运维

​		Kafka 内部主题 __consumer_offsets 和 __transaction_state。这两个内部主题默认都有 50 个分区，因此，分区子目录会非常得多。

​		在 Kafka 0.11 之前，当 Kafka 自动创建该主题时，会综合考虑当前运行的 Broker 台数和 Broker 端参数 offsets.topic.replication.factor 值，然后取两者的较小值作为该主题的副本数，但这就违背了用户设置 offsets.topic.replication.factor 的初衷。这正是很多用户感到困扰的地方：集群中有 100 台 Broker，offsets.topic.replication.factor 也设成了 3，为什么 __consumer_offsets 主题只有 1 个副本？其实，这就是因为这个主题是在只有一台 Broker 启动时被创建的。在 0.11 版本之后，社区修正了这个问题。也就是说，0.11 之后，Kafka 会严格遵守 offsets.topic.replication.factor 值。如果当前运行的 Broker 数量小于 offsets.topic.replication.factor 值，Kafka 会创建主题失败，并显式抛出异常。

如果该主题的副本值已经是 1 了，可以通过下面的方法修改副本值。

第 1 步是创建一个 json 文件，显式提供 50 个分区对应的副本数。注意，replicas 中的 3 台 Broker 排列顺序不同，目的是将 Leader 副本均匀地分散在 Broker 上。该文件具体格式如下：

```json
{"version":1, "partitions":[
 {"topic":"__consumer_offsets","partition":0,"replicas":[0,1,2]}, 
  {"topic":"__consumer_offsets","partition":1,"replicas":[0,2,1]},
  {"topic":"__consumer_offsets","partition":2,"replicas":[1,0,2]},
  {"topic":"__consumer_offsets","partition":3,"replicas":[1,2,0]},
  ...
  {"topic":"__consumer_offsets","partition":49,"replicas":[0,1,2]}
]}`
```

第 2 步是执行 kafka-reassign-partitions 脚本，命令如下：

```shell
bin/kafka-reassign-partitions.sh --zookeeper zookeeper_host:port --reassignment-json-file reassign.json --execute
```

**查看消费者组提交的位移数据**

```shell
bin/kafka-console-consumer.sh --bootstrap-server kafka_host:port --topic __consumer_offsets --formatter "kafka.coordinator.group.GroupMetadataManager\$OffsetsMessageFormatter" --from-beginning
```

**直接读取主题消息**

```shell
bin/kafka-console-consumer.sh --bootstrap-server kafka_host:port --topic __consumer_offsets --formatter "kafka.coordinator.group.GroupMetadataManager\$GroupMetadataMessageFormatter" --from-beginning
```

对于内部主题 __transaction_state ，只需要指定kafka.coordinator.transaction.TransactionLog\$TransactionLogMessageFormatter 即可，方法是相同的。

### 重设消费者组位移

重设位移的维度主要有两个：

1. 位移维度。这是指根据位移值来重设。直接把消费者的位移值重设成我们给定的位移值。

2. 时间维度。我们可以给定一个时间，让消费者把位移调整成大于该时间的最小位移；也可以给出一段时间间隔，比如 30 分钟前，然后让消费者直接将位移调回 30 分钟之前的位移值。

   <img src="E:\data\my-document\kafka\assets\eb469122e5af2c9f6baebb173b56bed5.jpeg" alt="img" style="zoom: 80%;" />

#### **通过消费者 API 来实现**

```java
void seek(TopicPartition partition, long offset);
void seek(TopicPartition partition, OffsetAndMetadata offsetAndMetadata);
void seekToBeginning(Collection<TopicPartition> partitions);
void seekToEnd(Collection<TopicPartition> partitions);
```

​		根据方法的定义，每次调用 seek 方法只能重设一个分区的位移。OffsetAndMetadata 类封装了 Long 型的位移和自定义元数据的复合类，一般情况下，自定义元数据为空，因此基本上可以认为这个类表征的主要是消息的位移值。seek 的变种方法 seekToBeginning 和 seekToEnd 则拥有一次重设多个分区的能力。

**Earliest 策略的实现方式**

```java
Properties consumerProperties = new Properties();
consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupID);
consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);

String topic = "test";  // 要重设位移的Kafka主题 
try (final KafkaConsumer<String, String> consumer = 
  new KafkaConsumer<>(consumerProperties)) {
         consumer.subscribe(Collections.singleton(topic));
         consumer.poll(0);
         consumer.seekToBeginning(
  consumer.partitionsFor(topic).stream().map(partitionInfo ->          
  new TopicPartition(topic, partitionInfo.partition()))
  .collect(Collectors.toList()));
} 
```

这段代码中有几个比较关键的部分，需要注意一下：

1. 要创建消费者程序，要禁止自动提交位移。
2. 组 ID 要设置成要重设的消费者组的组 ID。
3. 调用 seekToBeginning 方法时，需要一次性构造主题的所有分区对象。
4. 最重要的是，一定要调用带长整型的 poll 方法，而不要调用 consumer.poll(Duration.ofSecond(0))。

虽然社区已经不推荐使用 poll(long) 了，但短期内应该不会移除它，所以可以放心使用。另外，为了避免重复，在后面的实例中，只给出最关键的代码。

**Latest策略实现方式**

```java
consumer.seekToEnd(
  consumer.partitionsFor(topic).stream().map(partitionInfo ->          
  new TopicPartition(topic, partitionInfo.partition()))
  .collect(Collectors.toList()));
```

**Current策略实现方式**

```java
consumer.partitionsFor(topic).stream().map(info -> 
  new TopicPartition(topic, info.partition()))
  .forEach(tp -> {
  long committedOffset = consumer.committed(tp).offset();
  consumer.seek(tp, committedOffset);
});
```

需要借助 KafkaConsumer 的 committed 方法来获取当前提交的最新位移。这段代码首先调用 partitionsFor 方法获取给定主题的所有分区，然后依次获取对应分区上的已提交位移，最后通过 seek 方法重设位移到已提交位移处。

**Specified-Offset 策略实现方式**

```java
long targetOffset = 1234L;
for (PartitionInfo info : consumer.partitionsFor(topic)) {
  TopicPartition tp = new TopicPartition(topic, info.partition());
  consumer.seek(tp, targetOffset);
}
```

**Shift-By-N 策略实现方式**

```java
for (PartitionInfo info : consumer.partitionsFor(topic)) {
         TopicPartition tp = new TopicPartition(topic, info.partition());
  // 假设向前跳123条消息
         long targetOffset = consumer.committed(tp).offset() + 123L; 
         consumer.seek(tp, targetOffset);
}
```

**DateTime 策略实现方式**

需要借助另一个方法：**KafkaConsumer.  offsetsForTimes** 方法。假设我们要重设位移到 2019 年 6 月 20 日晚上 8 点。代码实现如下：

```java
long ts = LocalDateTime.of(
  2019, 6, 20, 20, 0).toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
Map<TopicPartition, Long> timeToSearch = 
         consumer.partitionsFor(topic).stream().map(info -> 
  new TopicPartition(topic, info.partition()))
  .collect(Collectors.toMap(Function.identity(), tp -> ts));

for (Map.Entry<TopicPartition, OffsetAndTimestamp> entry : 
  consumer.offsetsForTimes(timeToSearch).entrySet()) {
  consumer.seek(entry.getKey(), entry.getValue().offset());
}
```

**Duration 策略实现方式**

```java
Map<TopicPartition, Long> timeToSearch = consumer.partitionsFor(topic).stream()
         .map(info -> new TopicPartition(topic, info.partition()))
         .collect(Collectors.toMap(Function.identity(), tp -> System.currentTimeMillis() - 30 * 1000  * 60));

for (Map.Entry<TopicPartition, OffsetAndTimestamp> entry : 
         consumer.offsetsForTimes(timeToSearch).entrySet()) {
         consumer.seek(entry.getKey(), entry.getValue().offset());
}
```

#### 通过 kafka-consumer-groups 命令行脚本来实现

比起 API 的方式，用命令行重设位移要简单得多。针对上面的 7 种策略，有 7 个对应的参数。

Earliest 策略直接指定**–to-earliest**

```shell
bin/kafka-consumer-groups.sh --bootstrap-server kafka-host:port --group test-group --reset-offsets --all-topics --to-earliest –execute
```

Latest 策略直接指定**–to-latest**

```shell
bin/kafka-consumer-groups.sh --bootstrap-server kafka-host:port --group test-group --reset-offsets --all-topics --to-latest --execute
```

Current 策略直接指定**–to-current**

```shell
bin/kafka-consumer-groups.sh --bootstrap-server kafka-host:port --group test-group --reset-offsets --all-topics --to-current --execute
```

Specified-Offset 策略直接指定**–to-offset**

```shell
bin/kafka-consumer-groups.sh --bootstrap-server kafka-host:port --group test-group --reset-offsets --all-topics --to-offset <offset> --execute
```

Shift-By-N 策略直接指定**–shift-by N**

```shell
bin/kafka-consumer-groups.sh --bootstrap-server kafka-host:port --group test-group --reset-offsets --shift-by <offset_N> --execute
```

DateTime 策略直接指定**–to-datetime**

```shell
bin/kafka-consumer-groups.sh --bootstrap-server kafka-host:port --group test-group --reset-offsets --to-datetime 2019-06-20T20:00:00.000 --execute
```

 Duration 策略直接指定**–by-duration**

```shell
bin/kafka-consumer-groups.sh --bootstrap-server kafka-host:port --group test-group --reset-offsets --by-duration PT0H30M0S --execute
```

### 常用脚本

#### 生产消息

```shell
$ bin/kafka-console-producer.sh --broker-list kafka-host:port --topic test-topic --request-required-acks -1 --producer-property compression.type=lz4
```

#### 消费消息

```shell
$ bin/kafka-console-consumer.sh --bootstrap-server kafka-host:port --topic test-topic --group test-group --from-beginning --consumer-property enable.auto.commit=false 
```

#### 测试生产者性能

```shell
$ bin/kafka-producer-perf-test.sh --topic test-topic --num-records 10000000 --throughput -1 --record-size 1024 --producer-props bootstrap.servers=kafka-host:port acks=-1 linger.ms=2000 compression.type=lz4

2175479 records sent, 435095.8 records/sec (424.90 MB/sec), 131.1 ms avg latency, 681.0 ms max latency.
4190124 records sent, 838024.8 records/sec (818.38 MB/sec), 4.4 ms avg latency, 73.0 ms max latency.
10000000 records sent, 737463.126844 records/sec (720.18 MB/sec), 31.81 ms avg latency, 681.00 ms max latency, 4 ms 50th, 126 ms 95th, 604 ms 99th, 672 ms 99.9th.
```

打印出测试生产者的吞吐量 (MB/s)、消息发送延时以及各种分位数下的延时。一般情况下，消息延时不是一个简单的数字，而是一组分布。或者说，应该关心延时的概率分布情况，仅仅知道一个平均值是没有意义的。这就是这里计算分位数的原因。通常我们关注到 99th 分位就可以了。比如在上面的输出中，99th 值是 604ms，这表明测试生产者生产的消息中，有 99% 消息的延时都在 604ms 以内。你完全可以把这个数据当作这个生产者对外承诺的 SLA。

#### 测试消费者性能

```shell
$ bin/kafka-consumer-perf-test.sh --broker-list kafka-host:port --messages 10000000 --topic test-topic
start.time, end.time, data.consumed.in.MB, MB.sec, data.consumed.in.nMsg, nMsg.sec, rebalance.time.ms, fetch.time.ms, fetch.MB.sec, fetch.nMsg.sec
2019-06-26 15:24:18:138, 2019-06-26 15:24:23:805, 9765.6202, 1723.2434, 10000000, 1764602.0822, 16, 5651, 1728.1225, 1769598.3012
```

#### 查看主题消息总数

```shell
$ bin/kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list kafka-host:port --time -2 --topic test-topic

test-topic:0:0
test-topic:1:0

$ bin/kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list kafka-host:port --time -1 --topic test-topic

test-topic:0:5500000
test-topic:1:5500000
```

使用 Kafka 提供的工具类 GetOffsetShell 来计算给定主题特定分区当前的最早位移和最新位移，将两者的差值累加起来，就能得到该主题当前总的消息数。对于本例来说，test-topic 总的消息数为 5500000 + 5500000，等于 1100 万条。

#### 查看消息文件数据

```shell
$ bin/kafka-dump-log.sh --files ../data_dir/kafka_1/test-topic-1/00000000000000000000.log 
Dumping ../data_dir/kafka_1/test-topic-1/00000000000000000000.log
Starting offset: 0
baseOffset: 0 lastOffset: 14 count: 15 baseSequence: -1 lastSequence: -1 producerId: -1 producerEpoch: -1 partitionLeaderEpoch: 0 isTransactional: false isControl: false position: 0 CreateTime: 1561597044933 size: 1237 magic: 2 compresscodec: LZ4 crc: 646766737 isvalid: true
baseOffset: 15 lastOffset: 29 count: 15 baseSequence: -1 lastSequence: -1 producerId: -1 producerEpoch: -1 partitionLeaderEpoch: 0 isTransactional: false isControl: false position: 1237 CreateTime: 1561597044934 size: 1237 magic: 2 compresscodec: LZ4 crc: 3751986433 isvalid: true
......
```

如果只是指定 --files，那么该命令显示的是消息批次（RecordBatch）或消息集合（MessageSet）的元数据信息，比如创建时间、使用的压缩算法、CRC 校验值等。

**如果想深入看一下每条具体的消息，那么就需要显式指定 --deep-iteration 参数。**

```shell
$ bin/kafka-dump-log.sh --files ../data_dir/kafka_1/test-topic-1/00000000000000000000.log --deep-iteration
Dumping ../data_dir/kafka_1/test-topic-1/00000000000000000000.log
Starting offset: 0
baseOffset: 0 lastOffset: 14 count: 15 baseSequence: -1 lastSequence: -1 producerId: -1 producerEpoch: -1 partitionLeaderEpoch: 0 isTransactional: false isControl: false position: 0 CreateTime: 1561597044933 size: 1237 magic: 2 compresscodec: LZ4 crc: 646766737 isvalid: true
| offset: 0 CreateTime: 1561597044911 keysize: -1 valuesize: 1024 sequence: -1 headerKeys: []
| offset: 1 CreateTime: 1561597044932 keysize: -1 valuesize: 1024 sequence: -1 headerKeys: []
| offset: 2 CreateTime: 1561597044932 keysize: -1 valuesize: 1024 sequence: -1 headerKeys: []
| offset: 3 CreateTime: 1561597044932 keysize: -1 valuesize: 1024 sequence: -1 headerKeys: []
| offset: 4 CreateTime: 1561597044932 keysize: -1 valuesize: 1024 sequence: -1 headerKeys: []
| offset: 5 CreateTime: 1561597044932 keysize: -1 valuesize: 1024 sequence: -1 headerKeys: []
| offset: 6 CreateTime: 1561597044932 keysize: -1 valuesize: 1024 sequence: -1 headerKeys: []
| offset: 7 CreateTime: 1561597044932 keysize: -1 valuesize: 1024 sequence: -1 headerKeys: []
| offset: 8 CreateTime: 1561597044932 keysize: -1 valuesize: 1024 sequence: -1 headerKeys: []
| offset: 9 CreateTime: 1561597044932 keysize: -1 valuesize: 1024 sequence: -1 headerKeys: []
| offset: 10 CreateTime: 1561597044932 keysize: -1 valuesize: 1024 sequence: -1 headerKeys: []
| offset: 11 CreateTime: 1561597044932 keysize: -1 valuesize: 1024 sequence: -1 headerKeys: []
| offset: 12 CreateTime: 1561597044932 keysize: -1 valuesize: 1024 sequence: -1 headerKeys: []
| offset: 13 CreateTime: 1561597044933 keysize: -1 valuesize: 1024 sequence: -1 headerKeys: []
| offset: 14 CreateTime: 1561597044933 keysize: -1 valuesize: 1024 sequence: -1 headerKeys: []
baseOffset: 15 lastOffset: 29 count: 15 baseSequence: -1 lastSequence: -1 producerId: -1 producerEpoch: -1 partitionLeaderEpoch: 0 isTransactional: false isControl: false position: 1237 CreateTime: 1561597044934 size: 1237 magic: 2 compresscodec: LZ4 crc: 3751986433 isvalid: true
......
```

在上面的输出中，以竖线开头的就是消息批次下的消息信息。如果你还想看消息里面的实际数据，那么还需要指定  **--print-data-log** 参数，如下所示：

```shell
$ bin/kafka-dump-log.sh --files ../data_dir/kafka_1/test-topic-1/00000000000000000000.log --deep-iteration --print-data-log
```

#### 查询消费者组位移

```shell
$ bin/kafka-consumer-groups.sh --bootstrap-server broker-ip:port --describe --group test-group
```

![img](https://static001.geekbang.org/resource/image/f4/ee/f4b7d92cdebff84998506afece1f61ee.png)

图中的 CURRENT-OFFSET 表示该消费者当前消费的最新位移，LOG-END-OFFSET 表示对应分区最新生产消息的位移，LAG 列是两者的差值。CONSUMER-ID 是 Kafka 消费者程序自动生成的一个 ID。截止到 2.2 版本，都无法干预这个 ID 的生成过程。如果运行该命令时，这个消费者程序已经终止了，那么此列的值为空。

### 常见主题错误处理

1. **主题删除失败**

   造成主题删除失败的原因有很多，最常见的原因有两个：副本所在的 Broker 宕机了；待删除主题的部分分区依然在执行迁移过程。如果是因为前者，通常你重启对应的 Broker 之后，删除操作就能自动恢复；如果是因为后者，那就比较麻烦，很可能两个操作会相互干扰。不管什么原因，一旦你碰到主题无法删除的问题，可以采用这样的方法：

   + 第 1 步，手动删除 ZooKeeper 节点 /admin/delete_topics 下以待删除主题为名的 znode。
   + 第 2 步，手动删除该主题在磁盘上的分区目录。
   + 第 3 步，在 ZooKeeper 中执行 rmr  /controller，触发 Controller 重选举，刷新 Controller 缓存。

   在执行最后一步时，一定要谨慎，因为它可能造成大面积的分区 Leader 重选举。事实上，仅仅执行前两步也是可以的，只是 Controller 缓存中没有清空待删除主题罢了，也不影响使用。

2. **__consumer_offsets 占用太多的磁盘**

   一旦发现这个主题消耗了过多的磁盘空间，那么，一定要显式地用 jstack 命令查看一下 kafka-log-cleaner-thread 前缀的线程状态。通常情况下，这都是因为该线程挂掉了，无法及时清理此内部主题。倘若真是这个原因导致的，那我们就只能重启相应的 Broker 了。另外，请注意保留出错日志，因为这通常都是 Bug 导致的，最好提交到社区看一下。
   
   