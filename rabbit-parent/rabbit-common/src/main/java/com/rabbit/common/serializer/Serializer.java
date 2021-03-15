package com.rabbit.common.serializer;

/**
 * 序列化和反序列化的接口
 * @author Wenqi Liang
 * @date 2021/3/15
 */
public interface Serializer {

    byte[] serializeRaw(Object data);

    String serialize(Object data);

    <T> T deserialize(String content);

    <T> T deserialize(byte[] content);

}
