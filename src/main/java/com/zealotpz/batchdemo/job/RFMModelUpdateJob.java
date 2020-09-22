package com.zealotpz.batchdemo.job;

import com.zealotpz.batchdemo.entity.MemberProfile;
import com.zealotpz.batchdemo.entity.RFMMolderDTO;
import com.zealotpz.batchdemo.listener.MemberBatchJobListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * description: 每日更新会员 RFM 信息,
 *
 * <p>
 * * 执行步骤:
 * * 1. 每日 01:10 统计金额
 * * 2. 每日 01:45 计算 RFM 平均值
 * * 3. 每日 02:15 统计全部会员,更新 MemberType
 * * 4. 每日 02:45 统计用户分析数据,生成统计数据
 * </p>
 *
 * @author: zealotpz
 * create: 2020-01-06 17:58
 **/


@Slf4j
@Configuration
public class RFMModelUpdateJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private MemberBatchJobListener memberBatchJobListener;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;


//      +-----+----+-----+-----------+-------------------+
//      |  R  |  F |  M  |  客户类别  |  营销策略          |
//      +------------------------------------------------+
//      | ↑✅ | ↑✅ | ↑✅ |  重点保护  | 客户关怀，商品预留  |
//      +------------------------------------------------+
//      | ↑✅ | ↓❌ | ↑✅ |  增加频次  | 吸引来店           |
//      +------------------------------------------------+
//      | ↓❌ | ↑✅ | ↑✅ |  流失预警  | 会员升级，积分兑换  |
//      +------------------------------------------------+
//      | ↓❌ | ↓❌ | ↑✅ |  重要挽留  | 特别奖券           |
//      +------------------------------------------------+
//      | ↑✅ | ↑✅ | ↓❌ |  提升金额  | 交叉销售，向上销售  |
//      +------------------------------------------------+
//      | ↑✅ | ↓❌ | ↓❌ |  一般发展  | 吸引来店，新品推荐  |
//      +------------------------------------------------+
//      | ↓❌ | ↑✅ | ↓❌ |  一般保持  | 新品推荐，商品预警  |
//      +------------------------------------------------+
//      | ↓❌ | ↓❌ | ↓❌ |   流失    |  持续沟通，赢回     |
//      +------------------------------------------------+

    // 最近一次消费(Recency)
    // 消费频率(Frequency)
    // 消费金额(Monetary)

    // R = 有交易记录的客户最近一次交易时间距离当前时间的天数➗客户数(有交易的)
    // F = 总订单数➗总会员数
    // M = 总订单实付金额➗总会员数
    // 会员 RFM 与平均值比较 高↑,低↓

    @Bean("rfmModelJob")
    public Job rfmModelJob(@Qualifier("rfmModelStep") Step rfmModelStep) {
        return jobBuilderFactory.get("rfmModelJob")
                .start(rfmModelStep) //更新会员表,会员类型(RFM)
                .listener(memberBatchJobListener) //任务监听
                .build();
    }

    @Bean("rfmModelStep")
    public Step rfmModelStep(@Qualifier("getRFMMolderListReader") MyBatisCursorItemReader<RFMMolderDTO> reader,
                             @Qualifier("updateMemberTypeProcessor") ItemProcessor<RFMMolderDTO, MemberProfile> updateMemberTypeProcessor,
                             @Qualifier("writerMemberType") MyBatisBatchItemWriter<MemberProfile> writerMemberType) {
        return stepBuilderFactory.get("rfmModelStep")
                .<RFMMolderDTO, MemberProfile>chunk(100) //chunk通俗的讲类似于SQL的commit; 这里表示处理(processor)100条后写入(writer)一次。
                // 读操作
                .reader(reader).faultTolerant().skip(Exception.class).skipLimit(5)
                // 写操作
                .processor(updateMemberTypeProcessor)
                // 配置错误容忍
                .faultTolerant()
                // 重试2次，2次过后还是异常的话，则任务会结束; 异常的次数为reader，processor和writer中的总数
                .retryLimit(2)
                .writer(writerMemberType).faultTolerant().skip(Exception.class).skipLimit(5)
                .build();
    }

    /**
     * 查询参数绑定
     */
    @StepScope
    @Bean("rfmParameters")
    public Map<String, Object> rfmParameters(@Value("#{jobParameters['merchantNo']}") String merchantNo,
                                             @Value("#{jobParameters['avgR']}") String avgR,
                                             @Value("#{jobParameters['avgF']}") String avgF,
                                             @Value("#{jobParameters['avgM']}") String avgM) {
        Map<String, Object> map = new HashMap<>();
        map.put("merchantNo", merchantNo);
        map.put("avgR", avgR);
        map.put("avgF", avgF);
        map.put("avgM", avgM);
        return map;
    }

    //读取数据,mysql
    @Bean("getRFMMolderListReader")
    @StepScope
    public MyBatisCursorItemReader<RFMMolderDTO> reader(@Value("#{@rfmParameters}") Map<String, Object> rfmParameters) {
        return new MyBatisCursorItemReaderBuilder<RFMMolderDTO>()
                .sqlSessionFactory(sqlSessionFactory)
                .queryId("com.zealotpz.batchdemor.mapper.MemberProfileMapper.getRFMMolderList")
                .parameterValues(rfmParameters)
                .build();
    }

    //数据处理逻辑
    @Bean("updateMemberTypeProcessor")
    @StepScope
    public ItemProcessor<RFMMolderDTO, MemberProfile> updateMemberTypeProcessor(@Value("#{@rfmParameters}") Map<String, Object> rfmParameters) {
        return rfmMolder -> {
            log.info("---- 处理 RFM模型 ---->{}", rfmMolder.getMemberNo());

            String avgR = String.valueOf(rfmParameters.get("avgR")); //最近消费时间(平均值)
            BigDecimal avgF = new BigDecimal(String.valueOf(rfmParameters.get("avgF"))); //总消费订单数(平均值)
            BigDecimal avgM = new BigDecimal(String.valueOf(rfmParameters.get("avgM"))); //总消费金额(平均值)

            //大于平均值 = 1, 否则 = 0
            String R = null == rfmMolder.getRecencyStamp() ? "0" : rfmMolder.getRecencyStamp().isAfter(LocalDateTime.parse(avgR.replace("T", " "), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))) ? "1" : "0";
            String F = null == rfmMolder.getTransCount() ? "0" : rfmMolder.getTransCount().compareTo(avgF) > 0 ? "1" : "0";
            String M = null == rfmMolder.getTransAmount() ? "0" : rfmMolder.getTransAmount().compareTo(avgM) > 0 ? "1" : "0";

            String RFM = String.format("%s%s%s", R, F, M);
            int memberType;

            switch (RFM) {
                case "111":
                    memberType = 1;
                    break;
                case "101":
                    memberType = 2;
                    break;
                case "011":
                    memberType = 3;
                    break;
                case "001":
                    memberType = 4;
                    break;
                case "110":
                    memberType = 5;
                    break;
                case "100":
                    memberType = 6;
                    break;
                case "010":
                    memberType = 7;
                    break;
                case "000":
                    memberType = 8;
                    break;
                default:
                    memberType = 0;
                    break;
            }

            //TODO 客户类型
            return MemberProfile.builder().merchantNo(rfmMolder.getMerchantNo()).memberNo(rfmMolder.getMemberNo())
                    .memberType(memberType).build();
        };
    }

    /**
     * 更新数据,mysql
     */
    @Bean("writerMemberType")
    public MyBatisBatchItemWriter<MemberProfile> writerMemberType() {
        return new MyBatisBatchItemWriterBuilder<MemberProfile>()
                .sqlSessionFactory(sqlSessionFactory)
                .statementId("com.zealotpz.batchdemor.mapper.MemberProfileMapper.batchUpdateMemberType")
                .build();
    }

}
