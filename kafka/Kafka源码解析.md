# Kafka源码解析

## 源码结构

+ **bin 目录**：保存 Kafka 工具行脚本，我们熟知的 kafka-server-start 和 kafka-console-producer 等脚本都存放在这里。

+ **clients 目录**：保存 Kafka 客户端代码，比如生产者和消费者的代码都在该目录下。

+ **config 目录**：保存 Kafka 的配置文件，其中比较重要的配置文件是 server.properties。

+ **connect 目录**：保存 Connect 组件的源代码。Kafka Connect 组件是用来实现 Kafka 与外部系统之间的实时数据传输的。

+ **core 目录**：保存 Broker 端代码。Kafka 服务器端代码全部保存在该目录下。

+ **streams 目录**：保存 Streams 组件的源代码。Kafka Streams 是实现 Kafka 实时流处理的组件。