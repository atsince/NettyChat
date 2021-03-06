package com.jianxin.chat.listener;


import com.jianxin.chat.model.ConversationInfo;

public interface OnConversationInfoUpdateListener {

    void onConversationDraftUpdate(ConversationInfo conversationInfo, String draft);

    void onConversationTopUpdate(ConversationInfo conversationInfo, boolean top);

    void onConversationSilentUpdate(ConversationInfo conversationInfo, boolean silent);

    /**
     * @param conversationInfo
     */
    void onConversationUnreadStatusClear(ConversationInfo conversationInfo);

    // 可能是receive、send、recall 触发
    // 有个问题，未读消息基数做不了，还是得send、receive、recall三个回调来做
    //void onConversationLatestMessageUpdate(ConversationInfo conversationInfo, Message message);
}
