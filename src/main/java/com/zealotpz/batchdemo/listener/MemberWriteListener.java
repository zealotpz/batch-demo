package com.zealotpz.batchdemo.listener;

import com.zealotpz.batchdemo.entity.MemberProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * description: 写数据监听
 *
 * @author: zealotpz
 * create: 2019-12-23 16:11
 **/

@Slf4j
@Component
public class MemberWriteListener implements ItemWriteListener<MemberProfile> {


    @Override
    public void beforeWrite(List<? extends MemberProfile> items) {
        for (MemberProfile member : items) {
            log.info("beforeWrite  {}", member.toString()
            );
        }
    }

    @Override
    public void afterWrite(List<? extends MemberProfile> items) {

        if (!items.isEmpty()) {
            for (MemberProfile member : items) {
                log.info("afterWrite1  {}", member.toString());
//                memberGrantBagHandler(member);
            }
        } else {
            log.info("afterWrite empty");
        }
    }

    @Override
    public void onWriteError(Exception exception, List<? extends MemberProfile> items) {
        for (MemberProfile member : items) {
            log.error("onWriteError" + exception.getMessage(), exception);
        }
    }
}
