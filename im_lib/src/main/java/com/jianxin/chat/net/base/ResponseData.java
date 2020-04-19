package com.jianxin.chat.net.base;

/**
 * Created by imndx on 2017/12/15.
 */

public class ResponseData<T> extends StatusResult {
    T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
