// IConnectionStatusChanged.aidl
package com.jianxin.chat.listener;

import com.jianxin.chat.bean.message.Message;
import com.jianxin.chat.model.ConversationInfo;
interface IGetConversationInfoCallback {
    void onSuccess(in ConversationInfo conversationInfos);
    void onFailure(in int errorCode);
}
