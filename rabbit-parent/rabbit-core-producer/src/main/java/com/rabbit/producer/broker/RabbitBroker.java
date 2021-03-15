package com.rabbit.producer.broker;

import com.rabbit.api.Message;

/**
 * 具体发送不同种类消息的接口
 * @author Wenqi Liang
 * @date 2021/3/15
 */
public interface RabbitBroker {

    void rapidSend(Message message);

    void confirmSend(Message message);

    void reliantSend(Message message);

    void sendMessage();

}
