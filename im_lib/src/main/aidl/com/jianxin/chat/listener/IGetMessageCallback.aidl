// IConnectionStatusChanged.aidl
package com.jianxin.chat.listener;

import com.jianxin.chat.bean.message.Message;

interface IGetMessageCallback {
    void onSuccess(in List<Message> messages, in boolean hasMore);
    void onFailure(in int errorCode);
}
