package com.jianxin.chat.listener;

import android.os.RemoteException;

import com.jianxin.chat.bean.message.Message;
import com.jianxin.chat.model.ConversationInfo;

import java.util.List;


public interface GetConversationInfoCallback {
    /**
     * 获取消息会话回调
     *
     * @param conversationInfo 本次回调的消息会话
     */
    void onSuccess(ConversationInfo conversationInfo) throws RemoteException;

    void onFail(int errorCode) throws RemoteException;
}
