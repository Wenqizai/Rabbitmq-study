package com.rabbit.producer.component;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * @author liangwq
 * @date 2021/3/14
 */
@Component
public class RabbitSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 这里就是确认消息的回调监听接口, 用于确认消息是否被broker所收到
     */
    final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {

        /**
         *
         * @param correlationData 作为一个唯一的标志
         * @param b ack 消息是否到达broker, true成功, false失败
         * @param s cause, 失败的一些异常信息
         */
        @Override
        public void confirm(CorrelationData correlationData, boolean b, String s) {
            System.out.println("消息ACK结果:" + b + ", correlationData: " + correlationData.getId());
        }
    };

    /**
     * 对外发送消息的方法
     * @param message 具体的消息内容
     * @param properties 额外的附加属性
     * @throws Exception
     */
    public void send(Object message, Map<String, Object> properties) throws Exception {
        MessageHeaders mhs = new MessageHeaders(properties);
        Message<?> msg = MessageBuilder.createMessage(message, mhs);

        // 指定业务唯一id
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());

        rabbitTemplate.setConfirmCallback(confirmCallback);

        // 发送消息成功后, 执行业务
        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
            @Override
            public org.springframework.amqp.core.Message postProcessMessage(org.springframework.amqp.core.Message message) throws AmqpException {
                System.out.println("post to do:" + message);
                return message;
            }
        };
        rabbitTemplate.convertAndSend("exchange-1",
                "springboot.rabbit",
                message,
                messagePostProcessor,
                correlationData);
    }
}
