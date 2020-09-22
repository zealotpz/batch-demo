package com.zealotpz.batchdemo.listener;

import com.zealotpz.batchdemo.entity.MemberProfile;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.stereotype.Component;

/**
 * description: 读数据监听
 *
 * @author: zealotpz
 * create: 2019-12-23 16:11
 **/

@Slf4j
@Component
public class MemberItemReadListener implements ItemReadListener<MemberProfile> {

    @Override
    public void beforeRead() {

    }

    @SneakyThrows
    @Override
    public void afterRead(MemberProfile item) {
        log.info("afterRead {}", item.getMemberNo());
    }

    @Override
    public void onReadError(Exception ex) {
        log.info("onReadError", ex);
    }
}
