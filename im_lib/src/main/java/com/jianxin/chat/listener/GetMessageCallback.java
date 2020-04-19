package com.jianxin.chat.listener;

import com.jianxin.chat.bean.message.Message;

import java.util.List;


public interface GetMessageCallback {
    /**
     * 获取消息回调
     *
     * @param messages 本次回调的消息列表
     * @param hasMore  由于ipc限制，可能一次无法回调所有消息。是否还有消息未回调
     */
    void onSuccess(List<Message> messages, boolean hasMore);

    void onFail(int errorCode);
}
