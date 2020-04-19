// IOnReceiveMessage.aidl
package com.jianxin.chat.listener;

// Declare any non-default types here with import statements
import com.jianxin.chat.bean.message.Message;

interface IOnReceiveMessageListener {
    void onReceive(in List<Message> messages, boolean hasMore);
}
