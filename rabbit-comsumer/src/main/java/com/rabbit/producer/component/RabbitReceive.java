package com.rabbit.producer.component;


import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * @author liangwq
 * @date 2021/3/14
 */
@Component
@PropertySource("classpath:rabbitmq.properties")
public class RabbitReceive {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${rabbitmq.Queue}", durable = "${rabbitmq.Queue.durable}"),
            exchange = @Exchange(name = "${rabbitmq.exchange}",
                    durable = "${rabbitmq.exchange.durable}",
                    type = "${rabbitmq.exchange.type}",
                    ignoreDeclarationExceptions = "${rabbitmq.exchange.ignoreDeclarationExceptions}"),
            key = "${rabbitmq.key}"
    ))
    @RabbitHandler
    public void onMessage(Message message, Channel channel) throws Exception {
        // 1. 收到消息以后进行业务端消费处理
        System.out.println("--------------");
        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        System.out.println("消费消息:" + message.getPayload());

        // 2. 处理成功之后获取deliveryTag并进行手工的ack操作, 因为配置文件配置了手动签收
        channel.basicAck(deliveryTag, false);

    }

}
