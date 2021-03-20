package com.rabbit.producer.autoconfigure;

import com.rabbit.task.annotation.EnableElasticJob;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring自动装配这个类
 * @author liangwq
 * @date 2021/3/14
 */
@EnableElasticJob
@Configuration
@ComponentScan("com.rabbit.producer")
public class RabbitProducerAutoConfiguration {
}
