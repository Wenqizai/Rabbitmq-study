package com.rabbit.producer.broker;

import com.rabbit.api.Message;
import com.rabbit.api.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消费发送的实现类
 *
 * @author liangwq
 * @date 2021/3/15
 */
@Component
@Slf4j
public class RabbitBrokerImpl implements RabbitBroker {

    @Autowired
    private RabbitTemplateContainer rabbitTemplateContainer;

    /**
     * 迅速发消息
     *
     * @param message
     */
    @Override
    public void rapidSend(Message message) {
        message.setMessageType(MessageType.RAPID);
        sendKernel(message);
    }

    /**
     * 发送消息的核心方法, 使用异步线程池进行发送消息
     *
     * @param message
     */
    private void sendKernel(Message message) {
        AsyncBaseQueue.summit(() -> {
            String routingKey = message.getRoutingKey();
            String topic = message.getTopic();
            CorrelationData correlationData = new CorrelationData(String.format(
                    "%s#%s",
                    message.getMessageId(),
                    System.currentTimeMillis()
            ));
            RabbitTemplate rabbitTemplate = rabbitTemplateContainer.getTemplate(message);
            rabbitTemplate.convertAndSend(topic, routingKey, message, correlationData);
            log.info("RabbitBrokerImpl.sendKernel# send to rabbitmq, messageId: {}", message.getMessageId());
        });
    }

    @Override
    public void confirmSend(Message message) {

    }

    @Override
    public void reliantSend(Message message) {

    }

    @Override
    public void sendMessage() {

    }
}
