package com.jianxin.chat.listener;

import android.os.RemoteException;

import com.jianxin.chat.model.ConversationInfo;

import java.util.List;


public interface GetConversationInfoListCallback {
    /**
     * 获取消息会话回调
     *
     * @param conversationInfos 本次回调的消息列表
     * @param hasMore  由于ipc限制，可能一次无法回调所有消息。是否还有消息未回调
     */
    void onSuccess(List<ConversationInfo> conversationInfos, boolean hasMore) throws RemoteException;

    void onFail(int errorCode) throws RemoteException;
}
