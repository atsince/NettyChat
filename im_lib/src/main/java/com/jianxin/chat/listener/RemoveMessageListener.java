package com.jianxin.chat.listener;


import com.jianxin.chat.bean.message.Message;

public interface RemoveMessageListener {
    void onMessagedRemove(Message message);
}
