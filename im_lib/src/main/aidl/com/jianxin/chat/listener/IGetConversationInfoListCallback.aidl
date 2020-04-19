// IConnectionStatusChanged.aidl
package com.jianxin.chat.listener;

import com.jianxin.chat.bean.message.Message;
import com.jianxin.chat.model.ConversationInfo;
interface IGetConversationInfoListCallback {
    void onSuccess(in List<ConversationInfo> conversationInfos, in boolean hasMore);
    void onFailure(in int errorCode);
}
