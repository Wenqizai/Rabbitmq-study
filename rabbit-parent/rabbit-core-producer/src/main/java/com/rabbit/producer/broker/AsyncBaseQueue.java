package com.rabbit.producer.broker;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author liangwq
 * @date 2021/3/15
 */
@Slf4j
public class AsyncBaseQueue {

    private static final int THREAD_SIZE = Runtime.getRuntime().availableProcessors();

    private static final int QUEUE_SIZE = 10000;

    private static ExecutorService senderAsync;



    static {
        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Runnable target;
                Thread t = new Thread(r);
                t.setName("rabbitmq_client_async_sender");
                return t;
            }
        };
        RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                log.error("async sender is error rejected, runnable: {}, executor: {}", r, executor);
            }
        };
        senderAsync = new ThreadPoolExecutor(THREAD_SIZE,
                THREAD_SIZE,
                60L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_SIZE),
                threadFactory,
                rejectedExecutionHandler);
    }

    public static void summit(Runnable runnable) {
        senderAsync.submit(runnable);
    }
}
