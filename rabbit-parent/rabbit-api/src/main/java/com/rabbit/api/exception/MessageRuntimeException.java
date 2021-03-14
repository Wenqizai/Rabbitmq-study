package com.rabbit.api.exception;

/**
 * @author liangwq
 * @date 2021/3/14
 */
public class MessageRuntimeException extends RuntimeException {
    
    private static final long serialVersionUID = 4331329610640510547L;

    public MessageRuntimeException() {
        super();
    }

    public MessageRuntimeException(String message) {
        super(message);
    }

    public MessageRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageRuntimeException(Throwable cause) {
        super(cause);
    }
    
}
