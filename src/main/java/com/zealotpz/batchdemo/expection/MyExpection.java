package com.zealotpz.batchdemo.expection;


import com.zealotpz.batchdemo.enums.CommonEnum;

/**
 * description: 自定义异常
 *
 * @author: zealotpz
 * create: 2020-09-16 14:50
 **/
public class MyExpection extends RuntimeException {
    protected String code;

    public MyExpection(String code, String message) {
        super(message);
        this.code = code;
    }

    public MyExpection(String message) {
        this(CommonEnum.FAIL.getCode(), message);
    }

    public MyExpection(CommonEnum commonEnum){
        this(commonEnum.getCode(), commonEnum.getMsg());
    }

    public MyExpection(String code, String message, Object... args) {
        super(String.format(message, args));
        this.code = code;
    }

    public MyExpection(String message, Throwable cause) {
        super(message, cause);
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
