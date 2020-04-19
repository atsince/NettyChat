package com.jianxin.chat.im.manager;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.jianxin.chat.bean.ErrorCode;
import com.jianxin.chat.bean.core.MessageStatus;
import com.jianxin.chat.bean.core.MessageType;
import com.jianxin.chat.bean.message.Message;
import com.jianxin.chat.database.AppDataBase;
import com.jianxin.chat.database.ConversationInfoDao;
import com.jianxin.chat.database.MessageDao;
import com.jianxin.chat.im.client.IMSClientBootstrap;
import com.jianxin.chat.im.ISendMessageCallback;
import com.jianxin.chat.im.MessageBuilder;
import com.jianxin.chat.im.MessageProcessor;
import com.jianxin.chat.listener.GetConversationInfoCallback;
import com.jianxin.chat.listener.GetConversationInfoListCallback;
import com.jianxin.chat.listener.IGetMessageCallback;
import com.jianxin.chat.model.Conversation;
import com.jianxin.chat.model.ConversationInfo;
import com.jianxin.chat.model.SendCallBack;
import com.jianxin.chat.model.proto.ProtoConversationInfo;
import com.jianxin.chat.model.proto.ProtoConvert;
import com.jianxin.chat.model.proto.ProtoMessage;
import com.jianxin.chat.net.Callback;
import com.jianxin.chat.net.Constant;
import com.jianxin.chat.net.OKHttpHelper;
import com.jianxin.chat.net.base.ResponseData;
import com.jianxin.chat.net.response.ResponseProtoMessage;
import com.jianxin.chat.utils.CThreadPoolExecutor;
import com.jianxin.im.protobuf.MessageProtobuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import androidx.room.Room;

public class ProtoDataMaganer {
    public static final String TAG = "ProtoDataMaganer";
    private ProtoConvert protoConvert;
    private MessageDao msgDao = null;
    private ConversationInfoDao mConversationInfoDao = null;
    private static ProtoDataMaganer instance = null;
    private Map<String, SendCallBack> callbackMap = new ConcurrentHashMap<>();//发送消息回调集合
    private Map<String, ProtoConversationInfo> protoConversationInfoMap = new ConcurrentHashMap<>();
    private ArrayList<ConversationInfo> conversationInfos=new ArrayList<>();
    private Map<String, ConversationInfo> conversationInfoMap = new ConcurrentHashMap<>();
    private ProtoDataMaganer() {
        protoConvert = new ProtoConvert();
    }

    public static ProtoDataMaganer getInstance() {
        if (instance == null) {
            synchronized (ProtoDataMaganer.class) {
                if (instance == null) {
                    instance = new ProtoDataMaganer();
                }
            }
        }
        return instance;
    }


    public void init(Context context, String userId, String token, String hosts, int appStatus) {
        AppDataBase db = Room.databaseBuilder(context, AppDataBase.class, userId).build();
        msgDao = db.MsgDao();
        mConversationInfoDao = db.ConversationInfoDao();
        initConversationInfoMap();
        OKHttpHelper.init(context);
        IMSClientBootstrap.getInstance().init(userId, token, hosts, appStatus);
    }
private void initConversationInfoMap(){
    protoConversationInfoMap.clear();
    conversationInfos.clear();
    conversationInfoMap.clear();
    callbackMap.clear();
    List<ProtoConversationInfo> conversationInfos= mConversationInfoDao.getConversationInfoList();
    for(ProtoConversationInfo conversationInfo:conversationInfos){
        protoConversationInfoMap.put(conversationInfo.getTarget(),conversationInfo);
    }
}
    public void sendMessage(Message message, ISendMessageCallback listener) {


        ProtoMessage protoMessage = null;
        protoMessage = protoConvert.convertProtoMessage(message);
        // 同步会话表，生成messageId

        if (handConversationInfo(protoMessage)) {
            // 回调准备完成
            listener.onSendPrepared(protoMessage.getMessageId(), System.currentTimeMillis());
        } else {
            listener.onSendFail(ErrorCode.DATABASE_ERROR);
            return;
        }

        boolean isActive = IMSClientBootstrap.getInstance().isActive();
        if (isActive) {
            SendCallBack sendCallBack = new SendCallBack();
            sendCallBack.setCallback(listener);
            sendCallBack.setMessage(protoMessage);
            callbackMap.put(message.getMsgId(), sendCallBack);
            IMSClientBootstrap.getInstance().sendMessage(MessageBuilder.getProtoBufMessageBuilderByAppMessage(protoMessage).build());
        } else {
            listener.onSendFail(ErrorCode.SERVICE_EXCEPTION);
        }
    }

    public void registerMessageContent(String msgContentCls) {

        protoConvert.registerMessageContent(msgContentCls);
    }

    public void receiveMsg(MessageProtobuf.Msg msg) {
        CThreadPoolExecutor.runInBackground(new Runnable() {

            @Override
            public void run() {
                try {
                    ProtoMessage protoMessage = MessageBuilder.getMessageByProtobuf(msg);
                    if (protoMessage.getConversationType() == MessageType.SERVER_MSG_SENT_STATUS_REPORT.getValue()) {
                        SendCallBack sendCallBack = callbackMap.get(protoMessage.getMsgId());
                        if (sendCallBack != null) {
                            protoMessage.setConversationType(sendCallBack.getMessage().getConversationType());
                            if (updateConversationInfo(protoMessage, sendCallBack.getMessage())) {
                                if (protoMessage.getStatus() == MessageStatus.Sent.value()) {
                                    sendCallBack.getCallback().onSendSuccess(protoMessage.getMessageUid(), protoMessage.getTimestamp());
                                } else if (protoMessage.getStatus() == MessageStatus.Send_Failure.value()) {
                                    sendCallBack.getCallback().onSendFail(ErrorCode.SERVICE_EXCEPTION);
                                }
                            } else {
                                sendCallBack.getCallback().onSendFail(ErrorCode.SERVICE_EXCEPTION);
                            }
                            callbackMap.remove(sendCallBack);
                        }
                        return;
                    }
                    // 处理 消息from target
                    String from = protoMessage.getFrom();
                    protoMessage.setFrom(protoMessage.getTarget());
                    protoMessage.setTarget(from);
                    protoMessage.setStatus(MessageStatus.Sent.value());
                    if (handConversationInfo(protoMessage)) {
                        MessageProcessor.getInstance().receiveMsg(protoConvert.convertMessage(protoMessage));
                    } else {
                        Log.e(TAG, "接收数据存储数据库异常" + protoMessage.toString());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "消息处理出错，reason=" + e.getMessage());
                }
            }
        });

    }

    private synchronized boolean updateConversationInfo(ProtoMessage receiveMessage, ProtoMessage sendMessage) {
        boolean flag = true;
        try {
            //如果是最后一条消息，同步会话
            ProtoConversationInfo conversationInfo = protoConversationInfoMap.get(receiveMessage.getTarget());
            if (conversationInfo != null && conversationInfo.getLastMessage().getMsgId().equals(receiveMessage.getMsgId())) {
                ProtoMessage lastMessage = conversationInfo.getLastMessage();
                lastMessage.setTimestamp(receiveMessage.getTimestamp());
                lastMessage.setMessageUid(receiveMessage.getMessageUid());
                lastMessage.setStatus(receiveMessage.getStatus());
                conversationInfo.setLastMessage(lastMessage);
                protoConversationInfoMap.put(receiveMessage.getTarget(), conversationInfo);
                // 同步内存会话
                if(conversationInfoMap.containsKey(conversationInfo.getTarget())){
                    ConversationInfo info= conversationInfoMap.get(conversationInfo.getTarget());
                    info.timestamp=conversationInfo.getTimestamp();
                    info.lastMessage=protoConvert.convertMessage(conversationInfo.getLastMessage());

                }else{
                    ConversationInfo info=protoConvert.convertConversationInfo(conversationInfo);
                    conversationInfoMap.put(info.conversation.target,info);
                    conversationInfos.add(info);
                }
                CThreadPoolExecutor.runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        // 同步数据库
                        mConversationInfoDao.updateConversationInfo(conversationInfo);
                        msgDao.updateMessage(lastMessage);
                    }
                });
            } else {
                // 不是最后一条消息，只处理回调消息状态
                sendMessage.setTimestamp(receiveMessage.getTimestamp());
                sendMessage.setMessageUid(receiveMessage.getMessageUid());
                sendMessage.setStatus(receiveMessage.getStatus());
                CThreadPoolExecutor.runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        // 同步数据库
                        msgDao.updateMessage(sendMessage);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    private synchronized boolean handConversationInfo(ProtoMessage protoMessage) {
        boolean flag = true;
        try {
            // 同步会话表，生成messageId
            ProtoConversationInfo conversationInfo = protoConversationInfoMap.get(protoMessage.getTarget());
            if (conversationInfo == null) {
                conversationInfo = new ProtoConversationInfo();
                conversationInfo.setConversationType(protoMessage.getConversationType());
                conversationInfo.setLine(protoMessage.getLine());
                conversationInfo.setTarget(protoMessage.getTarget());
                conversationInfo.setTimestamp(System.currentTimeMillis());
                protoMessage.setMessageId(1);
                conversationInfo.setLastMessage(protoMessage);
            } else {
                ProtoMessage pm = conversationInfo.getLastMessage();
                if (pm == null) {
                    conversationInfo = new ProtoConversationInfo();
                    protoMessage.setMessageId(1);
                    conversationInfo.setLastMessage(protoMessage);
                } else {
                    protoMessage.setMessageId(pm.getMessageId() + 1);
                    conversationInfo.setLastMessage(protoMessage);
                }
            }
            protoConversationInfoMap.put(protoMessage.getTarget(), conversationInfo);
            // 同步内存会话
             if(conversationInfoMap.containsKey(conversationInfo.getTarget())){
                 ConversationInfo info= conversationInfoMap.get(conversationInfo.getTarget());
                 info.timestamp=conversationInfo.getTimestamp();
                 info.lastMessage=protoConvert.convertMessage(conversationInfo.getLastMessage());

             }else{
                 ConversationInfo info=protoConvert.convertConversationInfo(conversationInfo);
                 conversationInfoMap.put(info.conversation.target,info);
                 conversationInfos.add(info);
             }
            ProtoConversationInfo finalConversationInfo = conversationInfo;
             //同步数据库
            CThreadPoolExecutor.runInBackground(new Runnable() {
                @Override
                public void run() {
                    mConversationInfoDao.insertConversationInfo(finalConversationInfo);
                    msgDao.insertMessages(finalConversationInfo.getLastMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }


    public synchronized void getMessagesAsync(Conversation conversation, long fromIndex, boolean before, int count, String withUser, IGetMessageCallback callback) {
        try {

                List<ProtoMessage> list = msgDao.getMessages(conversation.type.getValue(), conversation.target, fromIndex);
                ArrayList<Message> messages = new ArrayList<>();
                boolean more = true;
                if (list != null && list.size() > 0) {
                    for (int i = list.size() - 1; i >= 0; i--) {
                        Message message = protoConvert.convertMessage(list.get(i));
                        messages.add(message);
                    }
                }
                if (messages.size() < count) {
                    more = false;
                }
                callback.onSuccess(messages, more);
            } catch(RemoteException e){
                e.printStackTrace();
                try {
                    callback.onFailure(ErrorCode.DATABASE_ERROR);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }

    }

    public void getRemoteMessages(String userId, Conversation conversation, long beforeMessageUid, boolean before, int count, IGetMessageCallback callback) {
        Map<String, Object> data = new HashMap<>();
        data.put("from", userId);
        data.put("target", conversation.target);
        data.put("seq", beforeMessageUid);
        if (before) {
            data.put("direct", -1);
        } else {
            data.put("direct", 1);
        }

        data.put("count", count);
        OKHttpHelper.post(Constant.CHAT_RECORD_LIST, data, new Callback<ResponseData<ResponseProtoMessage>>() {
            @Override
            public void onSuccess(ResponseData<ResponseProtoMessage> listResponseData) {
                if (listResponseData.isSuccess()) {

                    List<ProtoMessage> protoMessageList = listResponseData.getData().getMessageList();

                    ArrayList<Message> messages = new ArrayList<>();
                    boolean more = true;
                    if (protoMessageList != null && protoMessageList.size() > 0) {
                        if (protoMessageList != null && protoMessageList.size() > 0) {
                            for (int i = protoMessageList.size() - 1; i >= 0; i--) {
                                ProtoMessage protoMessage = protoMessageList.get(i);
                                if (protoMessage.getFrom() == userId) {
                                    protoMessage.setDirection(0);
                                } else {
                                    protoMessage.setDirection(1);
                                }
                                Message message = protoConvert.convertMessage(protoMessage);
                                messages.add(message);
                            }
                        }

                    }
                    if (messages.size() < count) {
                        more = false;
                    }

                    try {
                        callback.onSuccess(messages, more);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(String code, String message) {
                try {
                    callback.onFailure(ErrorCode.SERVICE_EXCEPTION);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getConversationList(int[] conversationTypes, int[] lines, GetConversationInfoListCallback conversationInfoCallback) {
        try {
            boolean more = false;
            if (conversationInfos.size() != 0) {
                conversationInfoCallback.onSuccess(conversationInfos, more);
            } else {
              conversationInfos.clear();
              conversationInfoMap.clear();
            List<ProtoConversationInfo> list = mConversationInfoDao.getConversationInfoList();
            if (list != null && list.size() > 0) {
                for (int i = list.size() - 1; i >= 0; i--) {
                    ConversationInfo conversationInfo = protoConvert.convertConversationInfo(list.get(i));
                    conversationInfoMap.put(conversationInfo.conversation.target,conversationInfo);
                    conversationInfos.add(conversationInfo);
                }
            }
            conversationInfoCallback.onSuccess(conversationInfos, more);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            try {
                conversationInfoCallback.onFail(ErrorCode.DATABASE_ERROR);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void getConversation(int conversationType, String target, int line, GetConversationInfoCallback conversationInfoCallback) throws RemoteException {
        try {
            ProtoConversationInfo protoConversationInfo = mConversationInfoDao.getConversationInfo(conversationType, target);
            ConversationInfo conversationInfo = null;
            if (protoConversationInfo != null) {
                conversationInfo = protoConvert.convertConversationInfo(protoConversationInfo);

            }
            conversationInfoCallback.onSuccess(conversationInfo);
        } catch (RemoteException e) {
            e.printStackTrace();
            try {
                conversationInfoCallback.onFail(ErrorCode.DATABASE_ERROR);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }
}
