package com.jianxin.chat.im;

import com.jianxin.chat.bean.message.Message;
import com.jianxin.chat.listener.IGetMessageCallback;
import com.jianxin.chat.model.Conversation;
import com.jianxin.chat.model.proto.ProtoMessage;

import androidx.lifecycle.MutableLiveData;

/**
 * <p>@ProjectName:     NettyChat</p>
 * <p>@ClassName:       IMessageProcessor.java</p>
 * <p>@PackageName:     com.freddy.chat.im</p>
 * <b>
 * <p>@Description:     消息处理器接口</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/04/10 00:11</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public interface IMessageProcessor {
    MutableLiveData<Message> getReceiveMessageLiveData();
    void receiveMsg(Message message);
    void sendMsg(Message message,ISendMessageCallback listener);
    void getMessagesAsync(Conversation conversation, long fromIndex, boolean before, int count, String withUser, IGetMessageCallback callback);

}
