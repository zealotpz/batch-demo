package com.zealotpz.batchdemo.enums;

/**
 * Create by zealotpz on 2020/9/16
 */
public enum CommonEnum {

    SUCCESS("000000", "成功"),
    FAIL("000001", "操作失败"),

    SET_EXAMPLE_CRITERIA("000011", "设定查询条件异常"),


    ;


    private final String code;

    private final String msg;

    CommonEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    }
