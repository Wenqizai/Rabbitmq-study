package com.rabbit.producer.broker;

import com.rabbit.api.Message;
import com.rabbit.api.MessageProducer;
import com.rabbit.api.SendCallback;
import com.rabbit.api.exception.MessageRuntimeException;

import java.util.List;

/**
 * @author liangwq
 * @date 2021/3/14
 */
public class ProducerClient implements MessageProducer {

    @Override
    public void send(Message message, SendCallback sendCallback) throws MessageRuntimeException {

    }

    @Override
    public void send(Message message) throws MessageRuntimeException {

    }

    @Override
    public void send(List<Message> messages) throws MessageRuntimeException {

    }
}
