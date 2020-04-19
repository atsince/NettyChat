// IRemoteClient.aidl
package com.jianxin.chat.remote;

// Declare any non-default types here with import statements
import com.jianxin.chat.bean.message.Message;
import com.jianxin.chat.listener.IOnConnectionStatusChangeListener;
import com.jianxin.chat.listener.IOnReceiveMessageListener;
import com.jianxin.chat.client.ISendMessageCallback;
import com.jianxin.chat.listener.IGetRemoteMessageCallback;
import com.jianxin.chat.listener.IGetConversationInfoCallback;
import com.jianxin.chat.listener.IGetConversationInfoListCallback;
import com.jianxin.chat.listener.IGetMessageCallback;
import com.jianxin.chat.model.Conversation;
import com.jianxin.chat.model.UnreadCount;
import com.jianxin.chat.model.ConversationInfo;
interface IRemoteClient {

    void receiveMsg(in List<Message> messages);
    void sendMsg(in Message message);
    oneway void send(in Message msg, in ISendMessageCallback callback, in int expireDuration);
    boolean connect(in String userId, in String token);
    void disconnect(in boolean clearSession);
    void setForeground(in int isForeground);
    void onNetworkChange();
    void setServerAddress(in String host);
    oneway void registerMessageContent(in String msgContentCls);
    oneway void setOnReceiveMessageListener(in IOnReceiveMessageListener listener);
    oneway void setOnConnectionStatusChangeListener(in IOnConnectionStatusChangeListener listener);

    oneway void getMessagesAsync(in Conversation conversation, in long fromIndex, in boolean before, in int count, in String withUser, in IGetMessageCallback callback);

    oneway void getRemoteMessages(in Conversation conversation, in long beforeMessageUid, in boolean before,in int count, in IGetMessageCallback callback);

    boolean clearUnreadStatus(in int conversationType, in String target, in int line);
    void clearMessages(in int conversationType, in String target, in int line);



     oneway void getConversationList(in int[] conversationTypes, in int[] lines,in IGetConversationInfoListCallback callback);
     oneway void getConversation(in int conversationType, in String target, in int line,in IGetConversationInfoCallback callback);
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
