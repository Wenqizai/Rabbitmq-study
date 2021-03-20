package com.rabbit.task.autoconfigure;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

/**
 * @author liangwq
 * @date 2021/3/20
 */
@ConfigurationProperties(prefix = "elastic.job.zk")
@Data
public class JobZookeeperProperties {

    private String namespace;

    private String serverLists;

    private int connectionTimeoutMilliseconds = 15000;

    private int baseSleepTimeMilliseconds = 1000;

    private int maxSleepTimeMilliseconds  = 3000;

    private int sessionTimeoutMilliseconds = 60000;

    private int maxRetries = 3;

    private String digest = "";

}
