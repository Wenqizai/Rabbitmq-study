package com.rabbit.api;

/**
 * 消费者监听者
 * @author Wenqi Liang
 * @date 2021/3/14
 */
public interface MessageListener {

    void onMessage(Message message);

}
