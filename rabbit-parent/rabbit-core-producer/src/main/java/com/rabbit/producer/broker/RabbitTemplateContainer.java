package com.rabbit.producer.broker;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.rabbit.api.Message;
import com.rabbit.api.MessageType;
import com.rabbit.api.exception.MessageRuntimeException;
import com.rabbit.common.convert.GenericMessageConverter;
import com.rabbit.common.convert.RabbitMessageConverter;
import com.rabbit.common.serializer.Serializer;
import com.rabbit.common.serializer.SerializerFactory;
import com.rabbit.common.serializer.impl.JacksonSerializerFactory;
import com.rabbit.producer.service.MessageStoreService;
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
 *
 * @author liangwq
 * @date 2021/3/15
 */
@Component
@Slf4j
public class RabbitTemplateContainer implements RabbitTemplate.ConfirmCallback {
    /**
     * String -> Topic
     */
    private Map<String, RabbitTemplate> rabbitMap = Maps.newConcurrentMap();
    private Splitter splitter = Splitter.on("#");

    private SerializerFactory serializerFactory = JacksonSerializerFactory.INSTANCE;

    @Autowired
    private ConnectionFactory connectionFactory;
    @Autowired
    private MessageStoreService messageStoreService;

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

        // 添加序列化和反序列化和converter对象
        Serializer serializer = serializerFactory.create();
        GenericMessageConverter genericMessageConverter = new GenericMessageConverter(serializer);
        RabbitMessageConverter rabbitMessageConverter = new RabbitMessageConverter(genericMessageConverter);
        newRabbitTemplate.setMessageConverter(rabbitMessageConverter);


        // 非迅速投递的消息都需要confirm
        String messageType = message.getMessageType();
        if (!MessageType.RAPID.equals(messageType)) {
            newRabbitTemplate.setConfirmCallback(this);
        }
        rabbitMap.putIfAbsent(topic, newRabbitTemplate);
        return newRabbitTemplate;
    }

    /**
     * 无论是confirm消息还是reliant消息, 发送消息以后broker都会去回调confirm
     * @param correlationData
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        // 具体消息的应答
        List<String> strings = splitter.splitToList(correlationData.getId());
        String messageId = strings.get(0);
        Long sendTime = Long.parseLong(strings.get(1));
        String messageType = strings.get(2);
        if (ack) {
            // 当Broker返回ACK成功时, 就是更新一下日志表对应的消息发送状态为SEND_OK

            // 如果当前消息类型为reliant, confirm后就去数据库将消息状态更新为success
            if (MessageType.RELIANT.equals(messageType)) {
                messageStoreService.success(messageId);
            }
            log.info("send message is OK, confirm messageId: {}, sendTime: {}", messageId, sendTime);
        } else {
            log.error("send message is FAIL, confirm messageId: {}, sendTime: {}", messageId, sendTime);
        }
    }
}
