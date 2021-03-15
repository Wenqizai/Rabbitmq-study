package com.rabbit.producer.broker;

import com.google.common.base.Preconditions;
import com.rabbit.api.Message;
import com.rabbit.api.MessageProducer;
import com.rabbit.api.MessageType;
import com.rabbit.api.SendCallback;
import com.rabbit.api.exception.MessageRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 发送消息的实际实现类
 * @author liangwq
 * @date 2021/3/14
 */
@Component
public class ProducerClient implements MessageProducer {

    @Autowired
    private RabbitBroker rabbitBroker;

    @Override
    public void send(Message message) throws MessageRuntimeException {
        Preconditions.checkNotNull(message.getTopic());
        String messageType = message.getMessageType();
        switch (messageType) {
            case MessageType.RAPID:
                rabbitBroker.rapidSend(message);
                break;
            case MessageType.CONFIRM:
                rabbitBroker.confirmSend(message);
                break;
            case MessageType.RELIANT:
                rabbitBroker.reliantSend(message);
                break;
            default:
                break;
        }
    }

    @Override
    public void send(List<Message> messages) throws MessageRuntimeException {

    }

    @Override
    public void send(Message message, SendCallback sendCallback) throws MessageRuntimeException {

    }
}
