package com.rabbit.api.exception;

/**
 * @author liangwq
 * @date 2021/3/14
 */
public class MessageException extends Exception {

    private static final long serialVersionUID = 939529170930145380L;

    public MessageException() {
        super();
    }

    public MessageException(String message) {
        super(message);
    }

    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageException(Throwable cause) {
        super(cause);
    }

}
