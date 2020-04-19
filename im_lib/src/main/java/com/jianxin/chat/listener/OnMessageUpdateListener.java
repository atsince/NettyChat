package com.jianxin.chat.listener;


import com.jianxin.chat.bean.message.Message;

public interface OnMessageUpdateListener {
    /**
     * messageContent 更新
     *
     * @param messageid
     * @param content
     */
    void onMessageUpdate(Message message);
}
