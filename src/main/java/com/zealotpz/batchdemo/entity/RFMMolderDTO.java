package com.zealotpz.batchdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * description: RFM 模型
 *
 * @author: zealotpz
 **/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RFMMolderDTO {

    //商户号
    private String merchantNo;

    /**
     * 会员编号
     */
    private String memberNo;

    /**
     * 客户类型-RFM 模型
     */
    private String memberType;

    // 最近一次消费时间(Recency)
    private LocalDateTime recencyStamp;

    // 会员总消费(次数)频率(Frequency)
    private BigDecimal transCount;

    // 会员总消费金额(Monetary)
    private BigDecimal transAmount;

}
