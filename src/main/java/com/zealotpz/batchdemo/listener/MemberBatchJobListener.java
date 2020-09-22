package com.zealotpz.batchdemo.listener;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * description: 会员批处理任务监听
 *
 * @author: zealotpz
 * create: 2019-12-24 11:30
 **/

@Slf4j
@Component
public class MemberBatchJobListener implements JobExecutionListener {

    private long startTime;

    @Value("${batch.phone}")
    private String dingPhone;


    //    @BeforeJob
    @Override
    public void beforeJob(JobExecution jobExecution) {
        startTime = System.currentTimeMillis();
        log.info("MemberBatch Job before " + jobExecution.getJobParameters());
    }

    @SneakyThrows
    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("MemberBatch Job STATUS : {}", jobExecution.getStatus());
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("JOB FINISHED");
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.info("JOB FAILED");
        }
        log.info("MemberBatch Job Cost Time : {}ms", (System.currentTimeMillis() - startTime));

        // 发送通知-钉钉
        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            String format = String.format("<font color=#3DD400 size=6 face=\"黑体\"> Member模块批处理 </font> " +
                            "\n\n ##### **机器 IP:**[%s]" +
                            "\n\n ##### **任务名称:**[%s]  \n\n ##### **任务状态:**<font color=#009D53, size=3 face=\"黑体\"> [%s] </font>  \n\n ##### **步骤名称:**[%s] " +
                            "\n\n ##### **读取数据Count:**[%s] \n\n ##### **写入数据Count:**[%s] \n\n ##### **写入失败数据Count:**[%s] \n\n ##### **提交数据Count:**[%s]" +
                            "\n\n ##### **耗时:**:[%s]ms", getIp(), jobExecution.getJobInstance().getJobName(), jobExecution.getStatus(), stepExecution.getStepName(),
                    stepExecution.getReadCount(), stepExecution.getWriteCount(), stepExecution.getWriteSkipCount(), stepExecution.getCommitCount(),
                    (System.currentTimeMillis() - startTime));
            //只发送异常通知
            if (BatchStatus.FAILED == jobExecution.getStatus()) {
                //TODO 发送钉钉通知
//                DingTalkUtil.send("👀👇", dingPhone);
                format = String.format("<font color=#3DD400 size=6 face=\"黑体\"> Member模块批处理 </font> " +
                                "\n\n ##### **机器 IP:**[%s]" +
                                "\n\n ##### **任务名称:**[%s]  \n\n ##### **任务状态:**<font color=#FF2E2E, size=3 face=\"黑体\"> [%s] </font>  \n\n ##### **步骤名称:**[%s] " +
                                "\n\n ##### **读取数据Count:**[%s] \n\n ##### **写入数据Count:**[%s] \n\n ##### **写入失败数据Count:**[%s] \n\n ##### **提交数据Count:**[%s]" +
                                "\n\n ##### **耗时:**:[%s]ms", getIp(), jobExecution.getJobInstance().getJobName(), jobExecution.getStatus(), stepExecution.getStepName(),
                        stepExecution.getReadCount(), stepExecution.getWriteCount(), stepExecution.getWriteSkipCount(), stepExecution.getCommitCount(),
                        (System.currentTimeMillis() - startTime));
            }
            //TODO 发送钉钉通知
//            DingTalkUtil.sendMD(format, "138xxxxxxxx");
        }
    }

    private String getIp() throws SocketException {
        String ip = "";
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
            NetworkInterface network = en.nextElement();
            String name = network.getName();
            if (!name.contains("docker") && !name.contains("lo")) {
                for (Enumeration<InetAddress> enumIpAddr = network.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    //获得IP
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ipaddress = inetAddress.getHostAddress();
                        if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
                            log.info("----->" + ipaddress);
                            if (!"127.0.0.1".equals(ip)) {
                                ip = ipaddress;
                            }
                        }
                    }
                }
            }
        }
        return ip;
    }


}
