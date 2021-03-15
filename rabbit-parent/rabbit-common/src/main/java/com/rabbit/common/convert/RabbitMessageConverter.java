package com.rabbit.common.convert;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * 装饰者模式, 增强GenericMessageConverter功能
 * @author liangwq
 * @date 2021/3/15
 */
public class RabbitMessageConverter implements MessageConverter {

    private GenericMessageConverter delegate;

    private final String defaultExpire = String.valueOf(24 * 60 * 60 * 1000);

    public RabbitMessageConverter(GenericMessageConverter genericMessageConverter) {
        this.delegate = genericMessageConverter;
    }

    @Override
    public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
        messageProperties.setExpiration(defaultExpire);
        return this.delegate.toMessage(o, messageProperties);
    }

    /**
     * 将amqp的message转换为我们自定义的message
     * @param message
     * @return
     * @throws MessageConversionException
     */
    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        com.rabbit.api.Message msg = (com.rabbit.api.Message) this.delegate.fromMessage(message);
        return msg;
    }
}
