package com.rabbit.producer;

import com.rabbit.producer.component.RabbitSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liangwq
 * @date 2021/3/14
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProducerApplicationTest {

    @Autowired
    private RabbitSender rabbitSender;

    @Test
    public void testSender() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("attr1", "12345");
        properties.put("attr1", "abcde");
        rabbitSender.send("hello rabbitmq!", properties);

        Thread.sleep(10000);
    }
}
