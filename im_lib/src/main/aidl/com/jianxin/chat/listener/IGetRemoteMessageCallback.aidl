// IConnectionStatusChanged.aidl
package com.jianxin.chat.listener;

import com.jianxin.chat.bean.message.Message;

interface IGetRemoteMessageCallback {
    void onSuccess(in List<Message> messages);
    void onFailure(in int errorCode);
}
