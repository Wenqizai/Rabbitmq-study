package com.rabbit.api;

/**
 * 消息类型类
 * @author liangwq
 * @date 2021/3/14
 */
public final class MessageType {
    /**
     * 迅速消息: 不需要保障消息可靠性, 也不需要做confirm确认
     */
    public static final String RAPID = "0";
    /**
     * 确认消息: 不需要保障消息的可靠性, 但是会做消息的confirm确认
     */
    public static final String CONFIRM = "1";
    /**
     * 可靠消息: 一定要保障消息的100%可靠性投递, 不允许有任何消息的丢失
     * PS: 保障数据库和所发的消息是原子性的(最终的一致性)
     */
    public static final String RELIANT = "2";


}
