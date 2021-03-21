package com.rabbit.producer.broker;
import java.util.Date;

import com.rabbit.api.Message;
import com.rabbit.api.MessageType;
import com.rabbit.producer.constant.BrokerMessageConst;
import com.rabbit.producer.constant.BrokerMessageStatus;
import com.rabbit.producer.entity.BrokerMessage;
import com.rabbit.producer.service.MessageStoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 消费发送的实现类
 *
 * @author liangwq
 * @date 2021/3/15
 */
@Service
@Slf4j
public class RabbitBrokerImpl implements RabbitBroker {

    @Autowired
    private RabbitTemplateContainer rabbitTemplateContainer;
    @Autowired
    private MessageStoreService messageStoreService;

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
            String messageType = message.getMessageType();
            CorrelationData correlationData = new CorrelationData(String.format(
                    "%s#%s#%s",
                    message.getMessageId(),
                    System.currentTimeMillis(),
                    messageType
            ));
            RabbitTemplate rabbitTemplate = rabbitTemplateContainer.getTemplate(message);
            rabbitTemplate.convertAndSend(topic, routingKey, message, correlationData);
            log.info("RabbitBrokerImpl.sendKernel# send to rabbitmq, messageId: {}", message.getMessageId());
        });
    }

    @Override
    public void confirmSend(Message message) {
        message.setMessageType(MessageType.CONFIRM);
        sendKernel(message);
    }

    @Override
    public void reliantSend(Message message) {

        message.setMessageType(MessageType.RELIANT);

        // 查询数据库该消息是否已经被插入
        BrokerMessage bm = messageStoreService.query(message.getMessageId());

        if (bm == null) {
            // 1. 把数据库的消息发送日志先记录好
            Date now = new Date();
            BrokerMessage brokerMessage = new BrokerMessage();
            brokerMessage.setMessageId(message.getMessageId());
            brokerMessage.setStatus(BrokerMessageStatus.SENDING.getCode());
            // tryCount 在最开始发送的时候不需要进行设置
            brokerMessage.setNextRetry(DateUtils.addMinutes(now, BrokerMessageConst.TIMEOUT));
            brokerMessage.setCreateTime(now);
            brokerMessage.setUpdateTime(now);
            brokerMessage.setMessage(message);
            messageStoreService.insert(brokerMessage);
        }

        // 2. 执行真正的发送消息逻辑
        sendKernel(message);
    }

    @Override
    public void sendMessage() {

    }
}
