package com.zealotpz.batchdemo.amqp.receiver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zealotpz.batchdemo.enums.CommonEnum;
import com.zealotpz.batchdemo.expection.MyExpection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * description: 通过消息队列接收,触发跑批任务
 *
 * @author: zealotpz
 * create: 2020-09-22
 **/
@Slf4j
@Component
public class Receiver {

    @Resource
    JobLauncher jobLauncher;
    @Autowired
    Job rfmModelJob;

    @Autowired
    private RedisTemplate redisTemplate;

    // ↓↓↓↓↓ 跑批任务 ↓↓↓↓↓
    @RabbitListener(queues = "memberBatchTaskQueue")
    public void memberBatchHandler(Message message) {
        String messageId = message.getMessageProperties().getMessageId();
        String messageJsonStr = new String(message.getBody());

        try {
            log.info("收到消息 会员跑批任务: {}----{}", messageId, messageJsonStr);

            JSONObject msgJson = JSON.parseObject(messageJsonStr);
            //商户号
            String merchantNo = msgJson.getString("merchantNo");
            Assert.notNull(merchantNo, "不能为空");

            switch (msgJson.getString("msgType")) {
                case "rfmModel":
                    log.info("收到 Batch 消息 统计RFM 模型-------------- merchantNo:[{}]", merchantNo);
                    //FIXME npe
                    if (null == redisTemplate.opsForValue().get("avgRFM")) {
                        log.info("RFM 平均值不存在");
                        throw new MyExpection(CommonEnum.FAIL.getMsg());
                    }

                    JSONObject jsonObject = (JSONObject) redisTemplate.opsForValue().get("avgRFM");
                    String avgR = jsonObject.getString("avgR"); //最近消费时间(平均值)
                    BigDecimal avgF = jsonObject.getBigDecimal("avgF"); //总消费订单数(平均值)
                    BigDecimal avgM = jsonObject.getBigDecimal("avgM"); //总消费金额(平均值)

                    JobParameters rfmModelJobParameters = new JobParametersBuilder()
                            .addString("merchantNo", merchantNo)
                            .addString("avgR", avgR)
                            .addString("avgF", String.valueOf(avgF))
                            .addString("avgM", String.valueOf(avgM))
                            .addDate("date", new Date())
                            .toJobParameters();

                    jobLauncher.run(rfmModelJob, rfmModelJobParameters);
                    break;
                default:
                    log.error("memberBatchTaskQueue: 无匹配类型 {}", msgJson.getString("msgType"));
                    break;
            }
        } catch (Exception e) {
            String errorMsg = "处理报告信息错误:" + e.getMessage();
            log.error(errorMsg, e);
            //TODO do something
        }
    }

}
