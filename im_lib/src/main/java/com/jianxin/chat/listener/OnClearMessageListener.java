package com.jianxin.chat.listener;


import com.jianxin.chat.model.Conversation;

/**
 * 会话消息被清空回调
 */
public interface OnClearMessageListener {
    void onClearMessage(Conversation conversation);
}
