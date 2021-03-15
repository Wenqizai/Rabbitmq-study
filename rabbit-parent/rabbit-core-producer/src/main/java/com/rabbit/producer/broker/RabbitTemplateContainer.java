package com.rabbit.producer.broker;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.rabbit.api.Message;
import com.rabbit.api.MessageType;
import com.rabbit.api.exception.MessageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author liangwq
 * @date 2021/3/15
 */
@Component
@Slf4j
public class RabbitTemplateContainer implements RabbitTemplate.ConfirmCallback{

    private Map<String, RabbitTemplate> rabbitMap = Maps.newConcurrentMap();

    @Autowired
    private ConnectionFactory connectionFactory;

    public RabbitTemplate getTemplate(Message message) throws MessageException {
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
    }
}
