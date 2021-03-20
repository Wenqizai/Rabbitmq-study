package com.bfxy.esjob.task.test;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.rabbit.task.annotation.ElasticJobConfig;
import org.springframework.stereotype.Component;

@Component
@ElasticJobConfig(
			name = "com.bfxy.esjob.task.test.TestJob",
			cron = "0/5 * * * * ?",
			description = "测试定时任务",
			overwrite = true,
			shardingTotalCount = 5
		)
public class TestJob implements SimpleJob {

	@Override
	public void execute(ShardingContext shardingContext) {
		System.err.println("执行Test job.");
	}

}
