package com.jianxin.chat.remote;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import com.jianxin.chat.im.manager.ProtoDataMaganer;
import com.jianxin.chat.bean.message.Message;
import com.jianxin.chat.bean.message.TextMessageContent;
import com.jianxin.chat.client.ISendMessageCallback;
import com.jianxin.chat.im.MessageProcessor;
import com.jianxin.chat.listener.GetConversationInfoCallback;
import com.jianxin.chat.listener.GetConversationInfoListCallback;
import com.jianxin.chat.listener.IGetConversationInfoCallback;
import com.jianxin.chat.listener.IGetConversationInfoListCallback;
import com.jianxin.chat.listener.IGetMessageCallback;
import com.jianxin.chat.listener.IOnConnectionStatusChangeListener;
import com.jianxin.chat.listener.IOnReceiveMessageListener;
import com.jianxin.chat.model.Conversation;
import com.jianxin.chat.model.ConversationInfo;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

public class RemoteService extends Service  {
    private static final String TAG="RemoteService";
    private int mConnectionStatus;
    private String mBackupDeviceToken;
    private int mBackupPushType;

    private Handler handler;

    private boolean logined;
    private String mUserId;
    private String clientId;
    private String mHost;

    IOnReceiveMessageListener receiveListener =null;
    ClientServiceStub mBinder=new ClientServiceStub();
    List<Message> messageList=new ArrayList<>();
    private Observer<Message> messageReceiveLiveDatObserver = new Observer<Message>() {
        @Override
        public void onChanged(@Nullable Message message) {
            messageList.clear();
            messageList.add(message);
            try {
                mBinder.receiveMsg(messageList);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        MessageProcessor.getInstance().getReceiveMessageLiveData().observeForever(messageReceiveLiveDatObserver);
        try {

            mBinder.registerMessageContent(TextMessageContent.class.getName());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private class ClientServiceStub extends IRemoteClient.Stub{


        @Override
        public void receiveMsg(List<Message> messages) throws RemoteException {
            if(receiveListener !=null){
                receiveListener.onReceive(messages,true);
            }
        }

        @Override
        public void sendMsg(Message message) throws RemoteException {
            send(message,null,0);
        }

        @Override
        public void send(Message msg, ISendMessageCallback callback, int expireDuration) throws RemoteException {
            MessageProcessor.getInstance().sendMsg(msg, new com.jianxin.chat.im.ISendMessageCallback() {
                @Override
                public void onSendSuccess(long messsageUid,long serverTime) {
                    if(callback!=null){
                        try {
                            callback.onSuccess(messsageUid, serverTime);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onSendFail( int errorCode) {
                    try {
                        callback.onFailure(errorCode);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSendPrepared(long messageId, long savedTime) {
                    try {
                        callback.onPrepared(messageId,savedTime);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onMediaUpload( String remoteUrl) {
                    try {
                        callback.onMediaUploaded(remoteUrl);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onProgress(long uploaded, long total) {
                    try {
                        callback.onProgress(uploaded,total);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public boolean connect(String userId, String token) throws RemoteException {
            mUserId = userId;
            clientId=token;
            ProtoDataMaganer.getInstance().init(getApplicationContext(),userId, token, mHost, 1);
            return true;
        }

        @Override
        public void disconnect(boolean clearSession) throws RemoteException {

        }

        @Override
        public void setForeground(int isForeground) throws RemoteException {

        }

        @Override
        public void onNetworkChange() throws RemoteException {

        }

        @Override
        public void setServerAddress(String host) throws RemoteException {
            mHost = host;
        }

        @Override
        public void registerMessageContent(String msgContentCls) throws RemoteException {
            try {
               ProtoDataMaganer.getInstance().registerMessageContent(msgContentCls);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void setOnReceiveMessageListener(IOnReceiveMessageListener listener) throws RemoteException {
               receiveListener=listener;
        }

        @Override
        public void setOnConnectionStatusChangeListener(IOnConnectionStatusChangeListener listener) throws RemoteException {

        }

        @Override
        public void getMessagesAsync(Conversation conversation, long fromIndex, boolean before, int count, String withUser, IGetMessageCallback callback) throws RemoteException {
             ProtoDataMaganer.getInstance().getMessagesAsync(conversation,fromIndex,before,count,withUser,callback);
        }

        @Override
        public void getRemoteMessages(Conversation conversation, long beforeMessageUid,boolean before, int count, IGetMessageCallback callback) throws RemoteException {
            ProtoDataMaganer.getInstance().getRemoteMessages(mUserId,conversation,beforeMessageUid,true,count,callback);
        }

        @Override
        public boolean clearUnreadStatus(int conversationType, String target, int line) throws RemoteException {
            return false;
        }

        @Override
        public void clearMessages(int conversationType, String target, int line) throws RemoteException {

        }

        @Override
        public void getConversationList(int[] conversationTypes, int[] lines, IGetConversationInfoListCallback conversationInfoListCallback) throws RemoteException {
               ProtoDataMaganer.getInstance().getConversationList(conversationTypes, lines, new GetConversationInfoListCallback() {
                   @Override
                   public void onSuccess(List<ConversationInfo> conversationInfos, boolean hasMore) throws RemoteException {
                       conversationInfoListCallback.onSuccess(conversationInfos,hasMore);
                   }

                   @Override
                   public void onFail(int errorCode) throws RemoteException {
                       conversationInfoListCallback.onFailure(errorCode);
                   }
               });
        }

        @Override
        public void getConversation(int conversationType, String target, int line, IGetConversationInfoCallback conversationInfoCallback) throws RemoteException {
        ProtoDataMaganer.getInstance().getConversation(conversationType, target, line, new GetConversationInfoCallback() {
            @Override
            public void onSuccess(ConversationInfo conversationInfo) throws RemoteException {
                conversationInfoCallback.onSuccess(conversationInfo);
            }

            @Override
            public void onFail(int errorCode) throws RemoteException {
                conversationInfoCallback.onFailure(errorCode);
            }
        });
        }


        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        MessageProcessor.getInstance().getReceiveMessageLiveData().removeObserver(messageReceiveLiveDatObserver);
    }


}
