package com.jianxin.chat.net.base;

/**
 * Created by imndx on 2017/12/16.
 */

/**
 * 用来表示result的状态，上层基本不用关注
 */
public class StatusResult {
    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return code == "00000000";
    }
}
