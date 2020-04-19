package com.jianxin.chat.im.manager;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.jianxin.chat.bean.ErrorCode;
import com.jianxin.chat.bean.core.ContentTag;
import com.jianxin.chat.bean.core.MessageStatus;
import com.jianxin.chat.bean.message.Message;
import com.jianxin.chat.bean.message.MessageContent;
import com.jianxin.chat.client.ISendMessageCallback;
import com.jianxin.chat.listener.GetConversationInfoCallback;
import com.jianxin.chat.listener.GetConversationInfoListCallback;
import com.jianxin.chat.listener.GetMessageCallback;
import com.jianxin.chat.listener.IGetConversationInfoListCallback;
import com.jianxin.chat.listener.IGetMessageCallback;
import com.jianxin.chat.listener.IOnConnectionStatusChangeListener;
import com.jianxin.chat.listener.IOnReceiveMessageListener;
import com.jianxin.chat.listener.OnClearMessageListener;
import com.jianxin.chat.listener.OnConnectionStatusChangeListener;
import com.jianxin.chat.listener.OnConversationInfoUpdateListener;
import com.jianxin.chat.listener.OnMessageUpdateListener;
import com.jianxin.chat.listener.OnRecallMessageListener;
import com.jianxin.chat.listener.OnReceiveMessageListener;
import com.jianxin.chat.listener.OnRemoveConversationListener;
import com.jianxin.chat.listener.OnSendMessageListener;
import com.jianxin.chat.listener.RemoveMessageListener;
import com.jianxin.chat.listener.SendMessageCallback;
import com.jianxin.chat.model.Conversation;
import com.jianxin.chat.model.ConversationInfo;
import com.jianxin.chat.model.NullConversationInfo;
import com.jianxin.chat.model.UnreadCount;
import com.jianxin.chat.remote.IMServiceStatusListener;
import com.jianxin.chat.remote.IRemoteClient;
import com.jianxin.chat.remote.RemoteService;
import com.jianxin.chat.utils.DeviceUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import static android.content.Context.BIND_AUTO_CREATE;

public class ChatManager {

    private static final String TAG = ChatManager.class.getName();

    private String SERVER_HOST;

    private static IRemoteClient mClient;

    private static ChatManager INST;
    private static Context gContext;

    private String userId;
    private String token;
    private Handler mainHandler;
    private Handler workHandler;
    private String deviceToken;
    private int connectionStatus;
    private String clientId;
    private boolean isBackground = true;
    private List<OnConnectionStatusChangeListener> onConnectionStatusChangeListeners = new ArrayList<>();
    private List<IMServiceStatusListener> imServiceStatusListeners = new ArrayList<>();
    private List<OnSendMessageListener> sendMessageListeners = new ArrayList<>();
    private List<OnReceiveMessageListener> onReceiveMessageListeners = new ArrayList<>();
    private List<OnMessageUpdateListener> messageUpdateListeners = new ArrayList<>();
    private List<OnClearMessageListener> clearMessageListeners = new ArrayList<>();
    private List<OnConversationInfoUpdateListener> conversationInfoUpdateListeners = new ArrayList<>();
    private List<OnRecallMessageListener> recallMessageListeners = new ArrayList<>();
    private List<RemoveMessageListener> removeMessageListeners = new ArrayList<>();
    private List<OnRemoveConversationListener> removeConversationListeners = new ArrayList<>();
    public static ChatManager getInstance(){

        return INST;
    }

    private ChatManager(String serverHost) {
        this.SERVER_HOST = serverHost;
    }

    public static void init(Application context, String imServerHost){
        if (INST != null) {
            // TODO: Already initialized
            return;
        }
        gContext = context.getApplicationContext();
        INST = new ChatManager(imServerHost);
        INST.mainHandler = new Handler();
        HandlerThread thread = new HandlerThread("workHandler");
        thread.start();
        INST.workHandler = new Handler(thread.getLooper());
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            public void onForeground() {
                INST.isBackground = false;
                if (mClient == null) {
                    return;
                }
                try {
                    mClient.setForeground(1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            public void onBackground() {
                INST.isBackground = true;
                if (mClient == null) {
                    return;
                }
                try {
                    mClient.setForeground(0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        INST.checkRemoteService();

    }
    /**
     * 工作线程handler
     *
     * @return
     */
    public Handler getWorkHandler() {
        return workHandler;
    }


    /**
     * 获取主线程handler
     *
     * @return
     */
    public Handler getMainHandler() {
        return mainHandler;
    }

     public String getUserId(){
        return userId;
}
    public void sendMessage(Message message){
        try {
            mClient.sendMsg(message);
            Log.e(TAG,"sendMessage=="+message.getContent());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void validateMessageContent(Class<? extends MessageContent> msgContentClazz) {
        try {
            Constructor c = msgContentClazz.getConstructor();
            if (c.getModifiers() != Modifier.PUBLIC) {
                throw new IllegalArgumentException("the default constructor of your custom messageContent class should be public");
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("custom messageContent class must have a default constructor");
        }
        ContentTag tag = msgContentClazz.getAnnotation(ContentTag.class);
        if (tag == null) {
            throw new IllegalArgumentException("custom messageContent class must have a ContentTag annotation");
        }
    }

    /**
     * 注册自自定义消息
     *
     * @param msgContentCls 自定义消息实现类，可参考自定义消息文档
     */
    public void registerMessageContent(Class<? extends MessageContent> msgContentCls) {

        validateMessageContent(msgContentCls);
        msgList.add(msgContentCls.getName());
        if (!checkRemoteService()) {
            return;
        }

        try {
            mClient.registerMessageContent(msgContentCls.getName());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private List<String> msgList = new ArrayList<>();
    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mClient = IRemoteClient.Stub.asInterface(service);
           try {
                mClient.setServerAddress(SERVER_HOST);

               for (String msgName : msgList) {
                   mClient.registerMessageContent(msgName);
               }

               mClient.setForeground(1);
               mClient.setOnReceiveMessageListener(new IOnReceiveMessageListener.Stub() {
                  @Override
                  public void onReceive(List<Message> messages, boolean hasMore) throws RemoteException {
                      for(OnReceiveMessageListener receiveMessageListener :onReceiveMessageListeners){
                          receiveMessageListener.onReceiveMessage(messages,hasMore);
                      }
                  }
              });


                mClient.setOnConnectionStatusChangeListener(new IOnConnectionStatusChangeListener.Stub() {
                    @Override
                    public void onConnectionStatusChange(int connectionStatus) throws RemoteException {
                        ChatManager.this.onConnectionStatusChange(connectionStatus);
                    }
                });



               if (mClient != null) {
                   try {
                        mClient.connect(userId, token);
                   } catch (RemoteException e) {
                       e.printStackTrace();
                   }
               }
//                if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(token)) {
//                    mClient.connect(userId, token);
//                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            mainHandler.post(() -> {
                for (IMServiceStatusListener listener : imServiceStatusListeners) {
                    listener.onServiceConnected();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("chatManager", "onServiceDisconnected");
            mClient = null;
            checkRemoteService();
            mainHandler.post(() -> {
                for (IMServiceStatusListener listener : imServiceStatusListeners) {
                    listener.onServiceDisconnected();
                }
            });
        }
    };
    /**
     * 添加会话更新监听
     *
     * @param listener
     */
    public void addConversationInfoUpdateListener(OnConversationInfoUpdateListener listener) {
        if (listener == null) {
            return;
        }
        conversationInfoUpdateListeners.add(listener);
    }

    /**
     * 删除会话监听
     *
     * @param listener
     */
    public void removeConversationInfoUpdateListener(OnConversationInfoUpdateListener listener) {
        conversationInfoUpdateListeners.remove(listener);
    }

    /**
     * 添加消息撤回监听
     *
     * @param listener
     */
    public void addRecallMessageListener(OnRecallMessageListener listener) {
        if (listener == null) {
            return;
        }
        recallMessageListeners.add(listener);
    }

    /**
     * 删除消息撤回监听
     *
     * @param listener
     */
    public void removeRecallMessageListener(OnRecallMessageListener listener) {
        recallMessageListeners.remove(listener);
    }

    /**
     * 添加主动删除消息监听
     *
     * @param listener
     */
    public void addRemoveMessageListener(RemoveMessageListener listener) {
        if (listener == null) {
            return;
        }
        removeMessageListeners.add(listener);
    }
    /**
     * 添加删除会话监听
     *
     * @param listener
     */
    public void addRemoveConversationListener(OnRemoveConversationListener listener) {
        if (listener == null) {
            return;
        }
        removeConversationListeners.add(listener);
    }

    /**
     * 移除删除会话监听
     *
     * @param listener
     */
    public void removeRemoveConversationListener(OnRemoveConversationListener listener) {
        removeConversationListeners.remove(listener);
    }
    /**
     * 删除删除消息监听
     *
     * @param listener
     */
    public void removeRemoveMessageListener(RemoveMessageListener listener) {
        removeMessageListeners.remove(listener);
    }
    /**
     * 添加im服务进程监听监听
     *
     * @param listener
     */
    public void addIMServiceStatusListener(IMServiceStatusListener listener) {
        if (listener == null) {
            return;
        }
        imServiceStatusListeners.add(listener);
    }
    /**
     * IM服务进程是否bind成功
     *
     * @return
     */
    public boolean isIMServiceConnected() {
        return mClient != null;
    }
    /**
     * 移除im服务进程状态监听
     *
     * @param listener
     */
    public void removeIMServiceStatusListener(IMServiceStatusListener listener) {
        imServiceStatusListeners.remove(listener);
    }
    public Context getApplicationContext() {
        return gContext;
    }
    private boolean checkRemoteService() {
        if (INST != null) {
            if (mClient != null) {
                return true;
            }

            Intent intent = new Intent(gContext, RemoteService.class);
            intent.putExtra("clientId", getClientId());
            boolean result = gContext.bindService(intent, serviceConnection, BIND_AUTO_CREATE);
            if (!result) {
                Log.e(TAG, "Bind service failure");
            }
        } else {
            Log.e(TAG, "Chat manager not initialized");
        }

        return false;
    }

    /**
     * 获取clientId, 野火IM用clientId唯一表示用户设备
     */
    public synchronized String getClientId() {
        if (this.clientId != null) {
            return this.clientId;
        }
        this.clientId = DeviceUtil.getDeviceId(gContext);
        return this.clientId;
    }
    public boolean connect(String userId, String token) {
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(token)) {
            throw new IllegalArgumentException("userId and token must be empty!");
        }
        this.userId = userId;
        this.token = token;
        if (mClient != null) {
            try {
                return mClient.connect(this.userId, this.token);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * 添加连接状态监听
     *
     * @param listener
     */
    public void addConnectionChangeListener(OnConnectionStatusChangeListener listener) {
        if (listener == null) {
            return;
        }
        if (!onConnectionStatusChangeListeners.contains(listener)) {
            onConnectionStatusChangeListeners.add(listener);
        }
    }
    /**
     * 删除连接状态监听
     *
     * @param listener
     */
    public void removeConnectionChangeListener(OnConnectionStatusChangeListener listener) {
        if (listener == null) {
            return;
        }
        onConnectionStatusChangeListeners.remove(listener);
    }
    /**
     * 添加消息更新监听
     *
     * @param listener
     */
    public void addOnMessageUpdateListener(OnMessageUpdateListener listener) {
        if (listener == null) {
            return;
        }
        messageUpdateListeners.add(listener);
    }

    /**
     * 删除消息更新监听
     *
     * @param listener
     */
    public void removeOnMessageUpdateListener(OnMessageUpdateListener listener) {
        messageUpdateListeners.remove(listener);
    }

    /**
     * 添加删除消息监听
     *
     * @param listener
     */
    public void addClearMessageListener(OnClearMessageListener listener) {
        if (listener == null) {
            return;
        }

        clearMessageListeners.add(listener);
    }

    /**
     * 移除删除消息监听
     *
     * @param listener
     */
    public void removeClearMessageListener(OnClearMessageListener listener) {
        clearMessageListeners.remove(listener);
    }
    /**
     * 连接状态回调
     *
     * @param status 连接状态
     */
    private void onConnectionStatusChange(final int status) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                connectionStatus = status;
                Iterator<OnConnectionStatusChangeListener> iterator = onConnectionStatusChangeListeners.iterator();
                OnConnectionStatusChangeListener listener;
                while (iterator.hasNext()) {
                    listener = iterator.next();
                    listener.onConnectionStatusChange(status);
                }
            }
        });
    }
    /**
     * 获取当前的连接状态
     *
     */
    public int getConnectionStatus() {
        return connectionStatus;
    }

    /**
     * 添加发送消息监听
     *
     * @param listener
     */
    public void addSendMessageListener(OnSendMessageListener listener) {
        if (listener == null) {
            return;
        }
        sendMessageListeners.add(listener);
    }

    /**
     * 删除发送消息监听
     *
     * @param listener
     */
    public void removeSendMessageListener(OnSendMessageListener listener) {
        sendMessageListeners.remove(listener);
    }
    public void sendMessage(final Message msg, final SendMessageCallback callback) {
        sendMessage(msg,0,callback);
    }

    /**
     * 添加新消息监听, 记得调用{@link #removeOnReceiveMessageListener(OnReceiveMessageListener)}删除监听
     *
     * @param listener
     */
    public void addOnReceiveMessageListener(OnReceiveMessageListener listener) {
        if (listener == null) {
            return;
        }
        onReceiveMessageListeners.add((listener));
    }

    /**
     * 删除消息监听
     *
     * @param listener
     */
    public void removeOnReceiveMessageListener(OnReceiveMessageListener listener) {
        if (listener == null) {
            return;
        }
        onReceiveMessageListeners.remove(listener);
    }

    /**
     * 收到新消息
     *
     * @param messages
     * @param hasMore  是否还有更多消息待收取
     */
    private void onReceiveMessage(final List<Message> messages, final boolean hasMore) {
        mainHandler.post(() -> {
            Iterator<OnReceiveMessageListener> iterator = onReceiveMessageListeners.iterator();
            OnReceiveMessageListener listener;
            while (iterator.hasNext()) {
                listener = iterator.next();
                listener.onReceiveMessage(messages, hasMore);
            }

            // 消息数大于时，认为是历史消息同步，不通知群被删除
            if (messages.size() > 10) {
                return;
            }
//            for (Message message : messages) {
//                if ((message.content instanceof QuitGroupNotificationContent && ((QuitGroupNotificationContent) message.content).operator.equals(getUserId()))
//                        || (message.content instanceof KickoffGroupMemberNotificationContent && ((KickoffGroupMemberNotificationContent) message.content).kickedMembers.contains(getUserId()))
//                        || message.content instanceof DismissGroupNotificationContent) {
//                    for (OnRemoveConversationListener l : removeConversationListeners) {
//                        l.onConversationRemove(message.conversation);
//                    }
//                }
//            }
        });
    }
    /**
     * 发送消息
     *
     * @param msg            消息
     * @param callback       发送状态回调
     * @param expireDuration 0, 永不过期；否则，规定时间内，对方未收到，则丢弃；单位是毫秒
     */
    public void sendMessage(final Message msg, final int expireDuration, final SendMessageCallback callback) {
        msg.setStatus(MessageStatus.Sending);
        msg.setMsgId(UUID.randomUUID().toString());
        msg.setTimeStamp( System.currentTimeMillis());
        msg.setSender(userId);
        if (!checkRemoteService()) {
            if (callback != null) {
                msg.setStatus(MessageStatus.Send_Failure);
                callback.onFail(ErrorCode.SERVICE_DIED);
            }
            for (OnSendMessageListener listener : sendMessageListeners) {
                listener.onSendFail(msg, ErrorCode.SERVICE_DIED);
            }
            return;
        }
//
//        if (msg.content instanceof MediaMessageContent) {
//            String localPath = ((MediaMessageContent) msg.content).localPath;
//            if (!TextUtils.isEmpty(localPath)) {
//                File file = new File(localPath);
//                if (!file.exists()) {
//                    if (callback != null) {
//                        callback.onFail(ErrorCode.FILE_NOT_EXIST);
//                    }
//                    return;
//                }
//
//                if (file.length() > 100 * 1024 * 1024) {
//                    if (callback != null) {
//                        callback.onFail(ErrorCode.FILE_TOO_LARGE);
//                    }
//                    return;
//                }
//            }
//        }

        try {
        mClient.send(msg, new ISendMessageCallback.Stub() {
            @Override
            public void onSuccess( long messageUid, long servertime) throws RemoteException {
                msg.setTimeStamp(servertime);
                msg.setMessageUid(messageUid);
                msg.setStatus( MessageStatus.Sent);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onSuccess(messageUid, servertime);
                        }
                        for (OnSendMessageListener listener : sendMessageListeners) {
                            listener.onSendSuccess(msg);
                        }
                    }
                });
            }

            @Override
            public void onFailure(int errorCode) throws RemoteException {
                msg.setStatus(MessageStatus.Send_Failure);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onFail(errorCode);
                        }
                        for (OnSendMessageListener listener : sendMessageListeners) {
                            listener.onSendFail(msg, errorCode);
                        }
                    }
                });
            }

            @Override
            public void onPrepared( long messageId, long savedTime) throws RemoteException {
                msg.setMessageId(messageId);
                msg.setTimeStamp(savedTime);
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onPrepare(messageId, savedTime);
                    }
                    for (OnSendMessageListener listener : sendMessageListeners) {
                        listener.onSendPrepare(msg, savedTime);
                    }
                });
            }

            @Override
            public void onProgress(long uploaded, long total) throws RemoteException {
                if (callback != null) {
                    mainHandler.post(() -> callback.onProgress(uploaded, total));
                }

                mainHandler.post(() -> {
                    for (OnSendMessageListener listener : sendMessageListeners) {
                        listener.onProgress(msg, uploaded, total);
                    }
                });
            }

            @Override
            public void onMediaUploaded(String remoteUrl) throws RemoteException {
//                MediaMessageContent mediaMessageContent = (MediaMessageContent) msg.content;
//                mediaMessageContent.remoteUrl = remoteUrl;
//                if (msg.messageId == 0) {
//                    return;
//                }
//                if (callback != null) {
//                    mainHandler.post(() -> callback.onMediaUpload(remoteUrl));
//                }
//                mainHandler.post(() -> {
//                    for (OnSendMessageListener listener : sendMessageListeners) {
//                        listener.onMediaUpload(msg, remoteUrl);
//                    }
//                });
            }
        },expireDuration);
        } catch (RemoteException e) {
            e.printStackTrace();
            if (callback != null) {
                msg.setStatus( MessageStatus.Send_Failure);
                callback.onFail(ErrorCode.SERVICE_EXCEPTION);
            }
            for (OnSendMessageListener listener : sendMessageListeners) {
                listener.onSendFail(msg, ErrorCode.SERVICE_EXCEPTION);
            }
        }
    }

    /**
     * 获取会话消息
     *
     * @param conversation
     * @param fromIndex    消息起始id(messageId)
     * @param before       true, 获取fromIndex之前的消息，即更旧的消息；false，获取fromIndex之后的消息，即更新的消息。都不包含fromIndex对应的消息
     * @param count        获取消息条数
     * @param withUser     只有会话类型为{@link .ConversationType#}时生效, channel主用来查询和某个用户的所有消息
     * @param callback     消息回调，当消息比较多，或者消息体比较大时，可能会回调多次
     */
    public void getMessages(Conversation conversation, long fromIndex, boolean before, int count, String withUser, GetMessageCallback callback) {
        if (callback == null) {
            return;
        }
        if (!checkRemoteService()) {
            callback.onFail(ErrorCode.SERVICE_DIED);
            return;
        }

        try {
            mClient.getMessagesAsync(conversation, fromIndex, before, count, withUser, new IGetMessageCallback.Stub() {
                @Override
                public void onSuccess(List<Message> messages, boolean hasMore) throws RemoteException {
                    callback.onSuccess(messages, hasMore);
                }

                @Override
                public void onFailure(int errorCode) throws RemoteException {
                    callback.onFail(errorCode);
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
            callback.onFail(ErrorCode.SERVICE_EXCEPTION);
        }
    }
    /**
     * 获取远程历史消息
     *
     * @param conversation    会话
     * @param beforeMessageId 起始消息的消息id
     * @param count           获取消息的条数
     * @param callback
     */
    public void getRemoteMessages(Conversation conversation, long beforeMessageId, int count, GetMessageCallback callback) {
        if (!checkRemoteService()) {
            return;
        }

        try {
            mClient.getRemoteMessages(conversation, beforeMessageId,true, count, new IGetMessageCallback.Stub() {
                @Override
                public void onSuccess(List<Message> messages,boolean more) throws RemoteException {
                    if (callback != null) {
                        mainHandler.post(() -> {
                            callback.onSuccess(messages,more);
                        });
                    }
                }

                @Override
                public void onFailure(int errorCode) throws RemoteException {
                    if (callback != null) {
                        mainHandler.post(() -> {
                            callback.onFail(errorCode);
                        });
                    }
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /**
     * 清除会话消息
     *
     * @param conversation
     */
    public void clearMessages(Conversation conversation) {
        if (!checkRemoteService()) {
            return;
        }

        try {
            mClient.clearMessages(conversation.type.getValue(), conversation.target, conversation.line);

            for (OnClearMessageListener listener : clearMessageListeners) {
                listener.onClearMessage(conversation);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /**
     * 清除指定会话的未读状态
     *
     * @param conversation
     */
    public void clearUnreadStatus(Conversation conversation) {
        if (!checkRemoteService()) {
            return;
        }

//        try {
//
//            if (mClient.clearUnreadStatus(conversation.type.getValue(), conversation.target, conversation.line)) {
//                ConversationInfo conversationInfo = getConversation(conversation);
//                conversationInfo.unreadCount = new UnreadCount();
//                for (OnConversationInfoUpdateListener listener : conversationInfoUpdateListeners) {
//                    listener.onConversationUnreadStatusClear(conversationInfo);
//                }
//            }
//
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
    }
    /**
     * 获取会话信息
     *
     * @param conversation
     * @return
     */
    public  void getConversation(Conversation conversation) {
//        ConversationInfo conversationInfo = null;
//        if (!checkRemoteService()) {
//            Log.e(TAG, "Remote service not available");
//
//        }
//
//        try {
//            conversationInfo = mClient.getConversation(conversation.type.getValue(), conversation.target, conversation.line);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        conversationInfo = conversationInfo != null ? conversationInfo : new NullConversationInfo(conversation);
//        return conversationInfo;
    }
    /**
     * 获取会话列表
     *
     * @param conversationTypes 获取哪些类型的会话
     * @param lines             获取哪些会话线路
     * @return
     */
    @NonNull
    public void getConversationList(List<Conversation.ConversationType> conversationTypes, List<Integer> lines, GetConversationInfoListCallback getConversationInfoListCallback) {
        if (!checkRemoteService()) {
            Log.e(TAG, "Remote service not available");
            try {
                getConversationInfoListCallback.onSuccess(new ArrayList<>(),false);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return;
        }

        if (conversationTypes == null || conversationTypes.size() == 0 ||
                lines == null || lines.size() == 0) {
            Log.e(TAG, "Invalid conversation type and lines");
            try {
                getConversationInfoListCallback.onSuccess(new ArrayList<>(),false);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return;
        }

        int[] intypes = new int[conversationTypes.size()];
        int[] inlines = new int[lines.size()];
        for (int i = 0; i < conversationTypes.size(); i++) {
            intypes[i] = conversationTypes.get(i).ordinal();
        }

        for (int j = 0; j < lines.size(); j++) {
            inlines[j] = lines.get(j);
        }

        try {
             mClient.getConversationList(intypes, inlines, new IGetConversationInfoListCallback.Stub() {
                 @Override
                 public void onSuccess(List<ConversationInfo> conversationInfos, boolean hasMore) throws RemoteException {
                     getConversationInfoListCallback.onSuccess(conversationInfos,hasMore);
                 }

                 @Override
                 public void onFailure(int errorCode) throws RemoteException {
                     getConversationInfoListCallback.onFail(errorCode);
                 }
             });
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
    /**
     * 删除会话
     *
     * @param conversation
     * @param clearMsg     是否同时删除该会话的所有消息
     */
    public void removeConversation(Conversation conversation, boolean clearMsg) {
        if (!checkRemoteService()) {
            return;
        }

//        try {
//            mClient.removeConversation(conversation.type.ordinal(), conversation.target, conversation.line, clearMsg);
//            for (OnRemoveConversationListener listener : removeConversationListeners) {
//                listener.onConversationRemove(conversation);
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
    }

    public void setConversationTop(Conversation conversation, boolean top) {
       // setConversationTop(conversation, top, null);
    }

    /**
     * 会话置顶
     *
     * @param conversation
     * @param top          true，置顶；false，取消置顶
     */
//    public void setConversationTop(Conversation conversation, boolean top, GeneralCallback callback) {
//        if (!checkRemoteService()) {
//            return;
//        }
//
//        try {
//            mClient.setConversationTop(conversation.type.ordinal(), conversation.target, conversation.line, top, new IGeneralCallback.Stub() {
//                @Override
//                public void onSuccess() throws RemoteException {
//                    ConversationInfo conversationInfo = getConversation(conversation);
//                    mainHandler.post(() -> {
//                        for (OnConversationInfoUpdateListener listener : conversationInfoUpdateListeners) {
//                            listener.onConversationTopUpdate(conversationInfo, top);
//                        }
//                    });
//                    if (callback != null) {
//                        callback.onSuccess();
//                    }
//                }
//
//                @Override
//                public void onFailure(int errorCode) throws RemoteException {
//                    if (callback != null) {
//                        mainHandler.post(() -> callback.onFail(errorCode));
//                    }
//                }
//            });
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//
//    }

}
