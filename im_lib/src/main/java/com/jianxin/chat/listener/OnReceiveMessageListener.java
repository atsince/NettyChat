package com.jianxin.chat.listener;

import com.jianxin.chat.bean.message.Message;

import java.util.List;



/**
 * * 当消息为{@link 、.message.core.PersistFlag#No_Persist}也进行通知，当不需要是，需要自行处理
 */
public interface OnReceiveMessageListener {
    void onReceiveMessage(List<Message> messages, boolean hasMore);
}
