package com.jianxin.chat.listener;

import com.jianxin.chat.bean.message.Message;

import java.util.List;


public interface GetRemoteMessageCallback {
    void onSuccess(List<Message> messages,boolean more);

    void onFail(int errorCode);
}
