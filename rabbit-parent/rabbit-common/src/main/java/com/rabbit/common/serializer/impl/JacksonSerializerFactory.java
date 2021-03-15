package com.rabbit.common.serializer.impl;

import com.rabbit.api.Message;
import com.rabbit.common.serializer.Serializer;
import com.rabbit.common.serializer.SerializerFactory;

/**
 * @author liangwq
 * @date 2021/3/15
 */
public class JacksonSerializerFactory implements SerializerFactory {

    public static final JacksonSerializerFactory INSTANCE = new JacksonSerializerFactory();

    @Override
    public Serializer create() {
        return JacksonSerializer.createParametricType(Message.class);
    }

}
