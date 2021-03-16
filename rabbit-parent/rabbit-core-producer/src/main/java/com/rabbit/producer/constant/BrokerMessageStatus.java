package com.rabbit.producer.constant;

/**
 * 消息的发送状态
 * @author Wenqi Liang
 * @date 2021/3/16
 * @desc
 */
public enum BrokerMessageStatus {

    SENDING("0"),
    SEND_OK("1"),
    SEND_FAIL("2"),
    SEND_FAIL_A_MOMENT("3");

    private String code;

    BrokerMessageStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
