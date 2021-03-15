package com.rabbit.producer.broker;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.rabbit.api.Message;
import com.rabbit.api.MessageType;
import com.rabbit.api.exception.MessageException;
import com.rabbit.api.exception.MessageRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * RabbitTemplate池化封装
 * 每一个topic, 对应一个RabbitTemplate
 * 1. 提高发送的效率
 * 2. 可以根据不同的需求定制化不同的RabbitTemplate, 比如每一个topic都有特定的routingKey
 * @author liangwq
 * @date 2021/3/15
 */
@Component
@Slf4j
public class RabbitTemplateContainer implements RabbitTemplate.ConfirmCallback{

    private Map<String, RabbitTemplate> rabbitMap = Maps.newConcurrentMap();
    private Splitter splitter = Splitter.on("#");

    @Autowired
    private ConnectionFactory connectionFactory;

    public RabbitTemplate getTemplate(Message message) throws MessageRuntimeException {
        Preconditions.checkNotNull(message);
        String topic = message.getTopic();
        RabbitTemplate rabbitTemplate = rabbitMap.get(topic);
        if (rabbitTemplate != null) {
            return rabbitTemplate;
        }

        log.info("#RabbitTemplateContainer.getTemplate# topic: {} is not exists, create one", topic);

        RabbitTemplate newRabbitTemplate = new RabbitTemplate(connectionFactory);
        newRabbitTemplate.setExchange(topic);
        newRabbitTemplate.setRoutingKey(message.getRoutingKey());
        newRabbitTemplate.setRetryTemplate(new RetryTemplate());

        // 对于message的序列化方式
//        newRabbitTemplate.setMessageConverter(messageConverter);

        // 非迅速投递的消息都需要confirm
        String messageType = message.getMessageType();
        if (!MessageType.RAPID.equals(messageType)) {
            newRabbitTemplate.setConfirmCallback(this);
        }
        rabbitMap.putIfAbsent(topic, newRabbitTemplate);
        return newRabbitTemplate;
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        // 具体消息的应答
        List<String> strings = splitter.splitToList(correlationData.getId());
        String messageId = strings.get(0);
        Long sendTime = Long.parseLong(strings.get(1));
        if (ack) {
            log.info("send message is OK, confirm messageId: {}, sendTime: {}", messageId, sendTime);
        } else {
            log.error("send message is FAIL, confirm messageId: {}, sendTime: {}", messageId, sendTime);
        }
    }
}
