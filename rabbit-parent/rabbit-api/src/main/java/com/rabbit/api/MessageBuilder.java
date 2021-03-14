package com.rabbit.api;

import com.rabbit.api.exception.MessageRuntimeException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author liangwq
 * @date 2021/3/14
 */
public class MessageBuilder {

    private String messageId;
    private String topic;
    private String routingKey = "";
    private Map<String, Object> attributes = new HashMap<>();
    private int delayMills;
    private String messageType = MessageType.CONFIRM;

    private MessageBuilder() {

    }

    public static MessageBuilder create() {
        return new MessageBuilder();
    }

    public MessageBuilder withMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public MessageBuilder withTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public MessageBuilder withRoutingKey(String routingKey) {
        this.routingKey = routingKey;
        return this;
    }

    public MessageBuilder withAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }

    public MessageBuilder withAttribute(String key, Object value) {
        this.attributes.put(key, value);
        return this;
    }

    public MessageBuilder withDelayMills(Integer delayMills) {
        this.delayMills = delayMills;
        return this;
    }

    public MessageBuilder withMessageType(String messageType) {
        this.messageType = messageType;
        return this;
    }


    public Message build() {

        if (messageId == null) {
            messageId = UUID.randomUUID().toString();
        }

        if (topic == null) {
            throw new MessageRuntimeException("this topic is null");
        }

        return new Message(messageId, topic, routingKey, attributes, delayMills, messageType);
    }

}
