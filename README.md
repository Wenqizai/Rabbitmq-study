# MQ应用

`使用场景`

1. 服务解耦
2. 削峰填谷
3. 异步化缓冲

`思考点`

1. **生产端可靠性投递**
2. **消费端幂等(重复消费问题)**
3. 高可用，低延迟，可靠性
4. 消息堆积能力
5. 扩展性（扩容）

`技术选型`

1. 各个MQ性能，优缺点，相应的业务场景
2. 集群架构模式，分布式，可扩展，高可用，可维护性
3. 综合成本问题，集群规模，人员成本
4. 未来的方向、规划、思考

# AMQP

`定义`

AMQP，即Advanced Message Queuing Protocol，一个提供统一消息服务的应用层标准高级消息队列协议，是应用层协议的一个开放标准，为面向消息的中间件设计。

`模型`

![amqp](https://i.loli.net/2021/03/13/smPoATgCxBXO5yz.jpg)

`核心概念`

**Server：**又称Broker，接受客户端的连接，实现AMQP实体服务

**Connection：**连接，应用程序与Broker的网络连接

**Channel：**网络通道，几乎所有操作都在Channel中进行，Channel是进行消息读写的通道。客户端可建立多个Channel，每个Channel代表一个会话任务。

**Message：**消息，服务器和应用程序之间传送的数据，由Properties和Body组成。Properties可以对消息进行修饰，比如消息的优先级，延迟等高级特性；Body就是消息体内容。

**Virtual host：**虚拟地址，用于进行逻辑隔离，最上层的消息路由。（一个Virtual Host里面可以由若干个Exchange和Queue，同一个Virtual Host里面不能有相同名称的Exchange或Queue）

**Exchange：**交换机，接收消息，根据路由键转发消息到绑定的队列。

**Binding：**Exchange和Queue之间的虚拟连接，binding中可以包含Routing key。

**Routing key：**一个路由规则，虚拟机可用它来确定如何路由一个特定消息。

**Queue：**也称为Message Queue，消息队列，保存消息并将它转发给消费者。

# RabbitMQ

## 集群架构

1. 主备模式
2. 远程模式
3. 镜像模式
4. 多活模式

[集群搭建](https://blog.csdn.net/qq_28533563/article/details/107932737)

> 主备模式

主/备方案：主节点挂了，从节点提供服务。（类似ActiveMQ+Zookeeper做主备）

<img src="https://i.loli.net/2021/03/13/IYZ59lnhXLz1jAB.png" alt="image-20210313105720083" style="zoom:50%;" />

> 远程模式（用的少）

1. 远距离通信和复制，可以实现双活的一种模式，简称Shovel模式。
2. Shovel：就是可以把消息进行不同数据中心的复制工作，可以跨地域的让两个mq集群互联。（即一台mq的消息处理不过来，就将消息转移到另外一台mq处理）

![image-20210313110856442](https://i.loli.net/2021/03/13/LPWoO5Hc7MCbNfE.png)

> 镜像模式（大厂都在用）

镜像模式可以保证数据100%不丢失，==高可靠，数据同步，3节点==。

对队列上的所有操作都是先应用到主节点上，再传播到镜像节点上。这包含了排队发布消息，传递消息给消费者，跟踪消费者的确认等。

缺点：不支持横向扩容

```md
# 节点越多，性能消耗越大，官方建议：对于3个节点的集群，复制到2个节点，对于5个节点的集群，复制到3个节点。
It will put additional strain on all cluster nodes, 
including network I/O, disk I/O and disk space usage.
```

[参考博客](https://www.cnblogs.com/rouqinglangzi/p/10815227.html)

<img src="https://i.loli.net/2021/03/13/bJlkaW2SX5NHDhz.png" alt="image-20210313111548865" style="zoom:50%;" />

> 多活模式

1. 这种模式也是实现异地数据复制的主流模式，因为Shovel模式配置复杂，故一般实现异地集群都是使用这种双活/多活模型实现。
2. 依赖于RabbitMQ的federation插件，可以实现持续的可靠AMQP数据通信，实际配置与应用非常简单。

<img src="https://i.loli.net/2021/03/13/5MDC4khN7BWzxsY.png" alt="image-20210313113921933" style="zoom:50%;" />

## 整体架构

![架构图](https://i.loli.net/2021/03/14/9fqt4ZWDBJPi5cU.jpg)

## 幂等性

1. 唯一ID+指纹码机制，利用数据库主键去重
2. 利用Redis的原子性去实现

`唯一ID+指纹码`

- 唯一ID+指纹码机制，利用数据库主键去重
- SELECT COUNT(*) FROM T_ORDER WHERE ID = 唯一ID + 指纹码
- 好处：实现简单（查库有则丢弃，没有则insert）
- 坏处：高并发下有数据库写入的性能瓶颈
- 解决方案：跟进ID进行分库分表进行算法路由

`Redis原子性`

- 考虑数据是否入库，关键解决问题就是Redis和MySQL之间如何做到原子性？
- 如果数据不入库，那么都存储到缓存中，如何设置将数据同步到MySQL的策略？

## 技术要点脑图

![RabbitMQ](https://i.loli.net/2021/03/16/3l8QsiRIHUWmovX.png)

[1. 基础概念](https://mp.weixin.qq.com/s?__biz=MzkwOTIxNDQ3OA==&mid=2247533527&idx=1&sn=a4f6770b495de53b49a384824bcf63ef&source=41#wechat_redirect)

[2. Exchange类型: Direct、fanout、topic](https://mp.weixin.qq.com/s/tM4R-2QdNATkoC1BukV8Ew)

[3. 基础知识与应用场景](https://mp.weixin.qq.com/s/OfxE6cx1hRTM_WkilT8uiQ)

## 整合Spring Boot

> 配置

[配置详情](https://blog.csdn.net/weixin_44012722/article/details/108517667)

```properties
# 是否启用消息确认模式(生产者可靠性投递)
spring.rabbitmq.publisher-confirms=true

# 设置return消息模式, 注意要和mandatory一起配合使用(消息是否路由到queue?)
spring.rabbitmq.publisher-returns=true
spring.rabbitmq.template.mandatory=true
```

```properties
# 表示消费者消费成功消息以后需要手工的进行签收(ack), 默认为auto
spring.rabbitmq.listener.simple.acknowledge-mode=manual
spring.rabbitmq.listener.simple.concurrency=5
spring.rabbitmq.listener.simple.max-concurrency=10
spring.rabbitmq.listener.simple.prefetch=1
```

## 消息功能的实现

<img src="https://i.loli.net/2021/03/14/YSmQoLzeUvqVJfh.png" alt="image-20210314170402385" style="zoom:50%;" />

## 可靠性投递

[实现方案](https://blog.csdn.net/weixin_41922349/article/details/106332591)

# Kafka

1. Kafka时LinkedIn开源的分布式消息系统，目前归属于Apache顶级项目；
2. Kafka是基于Pull模式来处理消息消费，最求高吞吐量，一开始的目的就是用于日志收集和传输；
3. 0.8版本开始支持复制，不支持事务，对消息的重复、丢失、错误没有严格要求，适合产生大量数据的互联网服务的数据收集任务。

`特点`

1. 分布式
2. 跨平台
3. 实时性
4. 伸缩性

`高性能原因`

1. 顺序写，Page Cache空中接力，高效读写
2. 后台异步、主动Flush
3. 高性能，高吞吐
4. 预读策略，IO策略

[kafka高性能原因](https://insights.thoughtworks.cn/apache-kafka/)

[kafka为什么这么快？](https://xie.infoq.cn/article/c06fea629926e2b6a8073e2f0)

## 架构

<img src="https://i.loli.net/2021/03/13/9ojB3rSuMtHidwv.png" alt="image-20210313122057776" style="zoom:50%;" />