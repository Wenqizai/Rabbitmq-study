package com.rabbit.producer.task;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.rabbit.producer.broker.RabbitBroker;
import com.rabbit.producer.constant.BrokerMessageConst;
import com.rabbit.producer.constant.BrokerMessageStatus;
import com.rabbit.producer.entity.BrokerMessage;
import com.rabbit.producer.service.MessageStoreService;
import com.rabbit.task.annotation.ElasticJobConfig;
import com.rabbit.task.annotation.EnableElasticJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author liangwq
 * @date 2021/3/20
 */
@Slf4j
@Component
@ElasticJobConfig(
        name = "com.rabbit.producer.task.RetryMessageDataflowJob",
        cron = "0/10 * * * * ?",
        description = "可靠性投递消息补偿任务",
        overwrite = true,
        shardingTotalCount = 1
)
public class RetryMessageDataflowJob implements DataflowJob {

    @Autowired
    private MessageStoreService messageStoreService;
    @Autowired
    private RabbitBroker rabbitBroker;
    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY_COUNT = 3;

    /**
     * 抓取数据到list中
     * @param shardingContext
     * @return
     */
    @Override
    public List fetchData(ShardingContext shardingContext) {
        List<BrokerMessage> list = messageStoreService.fetchTimeOutMessage4Retry(BrokerMessageStatus.SENDING);
        log.info("---------@@@@@ 抓取数据集合, 数量: {} @@@@@---------" + list.size());
        return list;
    }

    /**
     * 获取fetchData中的list, 执行任务
     * @param shardingContext
     * @param list
     */
    @Override
    public void processData(ShardingContext shardingContext, List list) {
        List<BrokerMessage> dataList = list;
        dataList.forEach(brokerMessage -> {
            String messageId = brokerMessage.getMessageId();
            // 重试次数大于等于3, 设置该条记录为fail, 不再重试
            if (brokerMessage.getTryCount() >= MAX_RETRY_COUNT) {
                messageStoreService.failure(messageId);
                log.warn(" ---------消息设置为最终失败, 消息ID: {} ", messageId);
            } else {
                // 每次重发消息, 都设置重发次数
                messageStoreService.updateTryCount(messageId);
                // 重发消息
                rabbitBroker.reliantSend(brokerMessage.getMessage());
            }
        });
    }
}
