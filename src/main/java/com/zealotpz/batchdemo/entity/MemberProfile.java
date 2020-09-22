package com.zealotpz.batchdemo.entity;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "`imms_member`", name = "t_member_profile")
public class MemberProfile {

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    private Long id;

    /**
     * 会员编号
     */
    @Column(name = "merchant_no")
    private String merchantNo;

    /**
     * 会员编号
     */
    @Column(name = "member_no")
    private String memberNo;

    /**
     * 会员编号
     */
    @Column(name = "mobile")
    private String mobile;

    /**
     * 会员姓名
     */
    @Column(name = "member_name")
    private String memberName;

    /**
     * 身份证号码
     */
    @Column(name = "id_card_number")
    private String idCardNumber;

    /**
     * 头像
     */
    @Column(name = "avatar")
    private String avatar;

    /**
     * 会员性别 1-男 2-女
     */
    @Column(name = "gender")
    private Short gender;

    /**
     * 会员出生日期
     */
    @Column(name = "birthday")
    private LocalDate birthday;

    /**
     * 会员年龄
     */
    @Column(name = "age")
    private Short age;

    /**
     * 会员生日 MMdd
     */
    @Column(name = "birthdate")
    private Short birthdate;

    /**
     * 会员生肖
     */
    @Column(name = "chinese_zodiac")
    private String chineseZodiac;

    /**
     * 会员星座
     */
    @Column(name = "constellation")
    private String constellation;

    /**
     * 常驻城市编码
     */
    @Column(name = "city")
    private String city;

    /**
     * 会员等级
     */
    @Column(name = "level")
    private Short level;

    /**
     * 旧等级(升级/降级前)
     **/
    @Transient
    private Short oldLevel;

    /**
     * 当前积分
     */
    @Column(name = "integral")
    private Integer integral;

    /**
     * 累计积分
     */
    @Column(name = "integral_total")
    private Integer integralTotal;

    /**
     * 当前成长值
     */
    @Column(name = "growth")
    private Integer growth;

    /**
     * 注册门店编号
     */
    @Column(name = "shop_no")
    private String shopNo;

    /**
     * 注册门店名称
     */
    @Column(name = "shop_name")
    private String shopName;

    /**
     * 注册渠道 1-POS录入 2-小程序注册,3-公众号注册
     */
    @Column(name = "channel")
    private Short channel;

    /**
     * 注册日期 yyyyMMdd
     */
    @Column(name = "register_date")
    private Integer registerDate;

    /**
     * 公众号关注状态：1-未关注 2-已关注 3-取消关注
     */
    @Column(name = "follow_status")
    private Short followStatus;

    /**
     * 首次关注/取消关注时间
     */
    @Column(name = "gmt_follow")
    private Short gmtFollow;

    /**
     * 累计消费金额
     */
    @Column(name = "consume_amount")
    private BigDecimal consumeAmount;

    /**
     * 累计消费次数
     */
    @Column(name = "consume_count")
    private Integer consumeCount;

    /**
     * 首次消费日期 yyyyMMdd
     */
    @Column(name = "first_consume_date")
    private Integer firstConsumeDate;

    /**
     * 最近消费日期 yyyyMMdd
     */
    @Column(name = "last_consume_date")
    private Integer lastConsumeDate;

    /**
     * 单笔最近消费金额
     */
    @Column(name = "single_last_amount")
    private BigDecimal singleLastAmount;

    /**
     * 单笔最高消费金额
     */
    @Column(name = "single_highest_amount")
    private BigDecimal singleHighestAmount;

    /**
     * 单笔平均消费金额
     */
    @Column(name = "single_average_amount")
    private BigDecimal singleAverageAmount;

    /**
     * 客户类型 RFM
     */
    @Column(name = "member_type")
    private Integer memberType;

    /**
     * 最近更新时间
     */
    @JsonIgnore
    @Column(name = "gmt_modify")
    private LocalDateTime gmtModify;

    /**
     * 注册时间
     */
    @JsonIgnore
    @Column(name = "gmt_create")
    private LocalDateTime gmtCreate;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}

