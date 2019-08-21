# Kafka分布式消息系统

## 1、Kafka简介

Apache Kafka是一个快速、可扩展的、高吞吐的、可容错的分布式“发布-订阅”消息系统，使用Scala和Java语言编写，可以将消息从一个端点传到另一个端点，较之传统的消息中间件（例如ActiveMQ、RabbitMQ），Kafka具有高吞吐量、内置分区、支持消息副本和高容错的特性，非常适合大规模消息处理应用。

## 2、Kafka体系架构

![1557298787490](D:\data\document\kafka\assets\1557298787490.png)

如上图所示，一个典型的Kafka体系架构包括若干Producer（可以是服务器日志，业务数据，页面前端产生的page view等等），若干broker（Kafka支持水平扩展，一般broker数量越多，集群吞吐率越高），若干Consumer (Group)，以及一个Zookeeper集群。Kafka通过Zookeeper管理集群配置，选举leader，以及在consumer group发生变化时进行rebalance。Producer使用push(推)模式将消息发布到broker，Consumer使用pull(拉)模式从broker订阅并消费消息。

|      名称       | 解释                                                         |
| :-------------: | :----------------------------------------------------------- |
|     Record      | Kafka处理的消息对象                                          |
|     Broker      | 消息中间件处理节点，一个Kafka节点就是一个broker，一个或者多个Broker可以组成一个Kafka集群 |
|      Topic      | Kafka根据topic对消息进行归类，发布到Kafka集群的每条消息都需要指定一个topic |
|    Producer     | 消息生产者，向Broker发送消息的客户端                         |
|    Consumer     | 消息消费者，从Broker读取消息的客户端                         |
| Consumer Group  | 每个Consumer属于一个特定的Consumer Group，一条消息可以发送到多个不同的Consumer Group，但是一个Consumer Group中只能有一个Consumer能够消费该消息 |
|    Partition    | 物理上的概念，一个topic可以分为多个partition，每个partition内部是有序的 |
|     Replica     | Kafka中同一条消息能够被拷贝到多个地方提供数据冗余，这些地方就是所谓的副本。副本还可以分为leader副本和follower副本，各自有不同的角色。副本是分区层级下的，即每个分区可以配置多个副本实现高可用。 |
| Consumer Offset | 表征消费者消费进度，每个消费者有自己的消费者位移。           |
|    Rebalance    | 消费者组内某个消费者挂掉后，其他消费者自动重新分配订阅主题分区的过程。Rebalance是Kafka实现高可用的重要手段。 |

一张图描绘列表中提到的概念，帮助你形象化的理解这些概念：

![img](D:\data\document\kafka\assets\06dbe05a9ed4e5bcc191bbdb985352df.png)

## 3、应用场景

**（1）用户活动追踪**

**（2）日志聚合**

**（3）限流削峰**

​	

## 4、Kafka高吞吐率实现

Kafka是分布式消息系统，需要处理海量的消息，Kafka的设计是把所有的消息都写入速度低容量大的硬盘，以此来换取更强的存储能力，但实际上，使用硬盘并没有带来过多的性能损失。kafka主要使用了以下几个方式实现了超高的吞吐率。

**顺序读写**

kafka的消息是不断追加到文件中的，这个特性使kafka可以充分利用磁盘的顺序读写性能，顺序读写不需要硬盘磁头的寻道时间，只需很少的扇区旋转时间，所以速度远快于随机读写。

**零拷贝**

先简单了解下文件系统的操作流程，例如一个程序要把文件内容发送到网络,这个程序是工作在用户空间，文件和网络socket属于硬件资源，两者之间有一个内核空间在操作系统内部，整个过程为：

![1557299864665](D:\data\document\kafka\assets\1557299864665.png)

在Linux kernel2.2 之后出现了一种叫做”零拷贝(zero-copy)”系统调用机制，就是跳过“用户缓冲区”的拷贝，建立一个磁盘空间和内存的直接映射，数据不再复制到“用户态缓冲区”系统上下文切换减少为2次，可以提升一倍的性能。

![1557299914192](D:\data\document\kafka\assets\1557299914192.png)

**文件分段** 
      kafka的队列topic被分为了多个区partition，每个partition又分为多个段segment，所以一个队列中的消息实际上是保存在N多个片段文件中通过分段的方式，每次文件操作都是对一个小文件的操作，非常轻便，同时也增加了并行处理能力。

![1557299942589](D:\data\document\kafka\assets\1557299942589.png)

**批量发送**

​      Kafka允许进行批量发送消息，先将消息缓存在内存中，然后一次请求批量发送出去比如可以指定缓存的消息达到某个量的时候就发出去，或者缓存了固定的时间后就发送出去如100条消息就发送，或者每5秒发送一次这种策略将大大减少服务端的I/O次数。

**数据压缩**

​      Kafka还支持对消息集合进行压缩，Producer可以通过GZIP或Snappy格式对消息集合进行压缩压缩的好处就是减少传输的数据量，减轻对网络传输的压力Producer压缩之后，在Consumer需进行解压，虽然增加了CPU的工作，但在对大数据处理上，瓶颈在网络上而不是CPU，所以这个成本很值得。

## 5、Kafka集群搭建

在生产环境中，为了防止单点问题，Kafka都是以集群的方式出现的，搭建一个简单的Kafka集群需要三个Kafka主机，即三个broker。

### 5.1 下载Kafka安装包

![1557300261052](D:\data\document\kafka\assets\1557300261052.png)

### 5.2 安装并配置第一台主机

**（1）上传并解压**

将下载好的Kafka安装包上传到CentOs虚拟机，并解压：

![1557301711267](D:\data\document\kafka\assets\1557301711267.png)

**（2）创建软连接**

![1557301841782](D:\data\document\kafka\assets\1557301841782.png)

**（3）修改配置文件**

Kafka安装目录下有一个config/server.properties文件，修改该文件。

![1557302012221](D:\data\document\kafka\assets\1557302012221.png)

![1557302090650](D:\data\document\kafka\assets\1557302090650.png)

![1557302301014](D:\data\document\kafka\assets\1557302301014.png)

### 5.3 再克隆两台Kafka

以刚刚配置的Broker主机为模板，再克隆两台Kafka主机。克隆以后需要修改server.properties中的broker.id、listeners。

![1557302414705](D:\data\document\kafka\assets\1557302414705.png)

![1557302385210](D:\data\document\kafka\assets\1557302385210.png)

### 5.4 Kafka的启动与停止

**（1）启动zookeeper**

![1557302953739](D:\data\document\kafka\assets\1557302953739.png)

**（2）启动Kafka**

在命令后添加-daemon参数，可以让Kafka进程以守护进程的方式启动，这样就不占用窗口。

![1557303382514](D:\data\document\kafka\assets\1557303382514.png)

**（3）停止Kafka**

![1557303461181](D:\data\document\kafka\assets\1557303461181.png)

### 5.5 Kafka的基本操作

**（1）创建topic**

kafka-topics.sh --create --bootstrap-server 192.168.56.21:9092 --replication-factor 1 --partitions 1 --topic travelsky

![1557304157879](D:\data\document\kafka\assets\1557304157879.png)

**(2)查看topic**

kafka-topics.sh --list --bootstrap-server 192.168.56.21:9092

![1557304234334](D:\data\document\kafka\assets\1557304234334.png)

**(3)发送消息**

该命令会创建一个producer，然后由它生产消息。

kafka-console-producer.sh --broker-list 192.168.56.21:9092 --topic travelsky

![1557305791756](D:\data\document\kafka\assets\1557305791756.png)

**（4）消费消息**

kafka-console-consumer.sh --bootstrap-server 192.168.56.22:9092 --topic travelsky --from-beginning

![1557305897276](D:\data\document\kafka\assets\1557305897276.png)

**（5）继续生产消息**

![1557306102197](D:\data\document\kafka\assets\1557306102197.png)

![1557306133883](D:\data\document\kafka\assets\1557306133883.png)

**（6）删除topic**

kafka-topics.sh --delete --bootstrap-server 192.168.56.21:9092 --topic travelsky

![1557306486987](D:\data\document\kafka\assets\1557306486987.png)

