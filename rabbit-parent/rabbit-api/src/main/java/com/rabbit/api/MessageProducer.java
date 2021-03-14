package com.rabbit.api;

import com.rabbit.api.exception.MessageRuntimeException;

import java.util.List;

/**
 * 消息生产者
 * @author liangwq
 * @date 2021/3/14
 */
public interface MessageProducer {
    /**
     * 消息的发送, 附带SendCallback回调执行响应的业务逻辑处理
     * @param message
     * @param sendCallback
     * @throws MessageRuntimeException
     */
    void send(Message message, SendCallback sendCallback) throws MessageRuntimeException;

    /** 消息的发送
     *
     * @param message
     * @throws MessageRuntimeException
     */
    void send(Message message) throws MessageRuntimeException;

    /**
     * 消息的批量发送
     * @param messages
     * @throws MessageRuntimeException
     */
    void send(List<Message> messages) throws MessageRuntimeException;

}
