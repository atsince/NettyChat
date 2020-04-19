package com.jianxin.im.conversationlist;

import android.os.RemoteException;

import com.jianxin.chat.bean.message.Message;
import com.jianxin.chat.im.manager.ChatManager;
import com.jianxin.chat.listener.GetConversationInfoListCallback;
import com.jianxin.chat.listener.OnClearMessageListener;
import com.jianxin.chat.listener.OnConnectionStatusChangeListener;
import com.jianxin.chat.listener.OnConversationInfoUpdateListener;
import com.jianxin.chat.listener.OnRecallMessageListener;
import com.jianxin.chat.listener.OnReceiveMessageListener;
import com.jianxin.chat.listener.OnRemoveConversationListener;
import com.jianxin.chat.listener.OnSendMessageListener;
import com.jianxin.chat.listener.RemoveMessageListener;
import com.jianxin.chat.model.Conversation;
import com.jianxin.chat.model.ConversationInfo;
import com.jianxin.chat.model.UnreadCount;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


/**
 * how
 * 1. observe conversationInfoLiveData in your activity or fragment, but if you still not called getConversationList,
 * just ignore the data.
 * 2. call getConversationList
 */
public class ConversationListViewModel extends ViewModel implements OnReceiveMessageListener,
        OnSendMessageListener,
        OnRecallMessageListener,
        RemoveMessageListener,
        OnConversationInfoUpdateListener,
        OnRemoveConversationListener,
        OnConnectionStatusChangeListener,
        OnClearMessageListener {
    private MutableLiveData<List<ConversationInfo>> conversationListLiveData;
    private MutableLiveData<UnreadCount> unreadCountLiveData;
    private MutableLiveData<Integer> connectionStatusLiveData = new MutableLiveData<>();

    private List<Conversation.ConversationType> types;
    private List<Integer> lines;

    public ConversationListViewModel(List<Conversation.ConversationType> types, List<Integer> lines) {
        super();
        this.types = types;
        this.lines = lines;
        ChatManager.getInstance().addOnReceiveMessageListener(this);
        ChatManager.getInstance().addSendMessageListener(this);
        ChatManager.getInstance().addConversationInfoUpdateListener(this);
        ChatManager.getInstance().addRecallMessageListener(this);
        ChatManager.getInstance().addConnectionChangeListener(this);
        ChatManager.getInstance().addRemoveMessageListener(this);
        ChatManager.getInstance().addClearMessageListener(this);
        ChatManager.getInstance().addRemoveConversationListener(this);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        ChatManager.getInstance().removeOnReceiveMessageListener(this);
        ChatManager.getInstance().removeSendMessageListener(this);
        ChatManager.getInstance().removeConversationInfoUpdateListener(this);
        ChatManager.getInstance().removeConnectionChangeListener(this);
        ChatManager.getInstance().removeRecallMessageListener(this);
        ChatManager.getInstance().removeRemoveMessageListener(this);
        ChatManager.getInstance().removeClearMessageListener(this);
        ChatManager.getInstance().removeRemoveConversationListener(this);
    }

    private AtomicInteger loadingCount = new AtomicInteger(0);

    public void reloadConversationList() {
        reloadConversationList(false);
    }

    public void reloadConversationList(boolean force) {
        if (conversationListLiveData == null) {
            return;
        }
        if (!force) {
            int count = loadingCount.get();
            if (count > 0) {
                return;
            }
        }
        loadingCount.incrementAndGet();

        ChatManager.getInstance().getWorkHandler().post(() -> {
            loadingCount.decrementAndGet();
            ChatManager.getInstance().getConversationList(types, lines, new GetConversationInfoListCallback() {
                @Override
                public void onSuccess(List<ConversationInfo> conversationInfos, boolean hasMore) throws RemoteException {
                    conversationListLiveData.postValue(conversationInfos);
                }

                @Override
                public void onFail(int errorCode) throws RemoteException {

                }
            });

        });
    }



    public MutableLiveData<List<ConversationInfo>> conversationListLiveData() {
        if (conversationListLiveData == null) {
            conversationListLiveData = new MutableLiveData<>();
        }
        ChatManager.getInstance().getWorkHandler().post(() -> {
             ChatManager.getInstance().getConversationList(types, lines, new GetConversationInfoListCallback() {
                @Override
                public void onSuccess(List<ConversationInfo> conversationInfos, boolean hasMore) throws RemoteException {
                    conversationListLiveData.postValue(conversationInfos);
                }

                @Override
                public void onFail(int errorCode) throws RemoteException {

                }
            });

        });

        return conversationListLiveData;
    }

    public MutableLiveData<UnreadCount> unreadCountLiveData() {
        if (unreadCountLiveData == null) {
            unreadCountLiveData = new MutableLiveData<>();
        }

        reloadConversationUnreadStatus();
        return unreadCountLiveData;
    }

    public MutableLiveData<Integer> connectionStatusLiveData() {
        return connectionStatusLiveData;
    }

    public void reloadConversationUnreadStatus() {
        ChatManager.getInstance().getWorkHandler().post(() -> {
           ChatManager.getInstance().getConversationList(types, lines, new GetConversationInfoListCallback() {
                @Override
                public void onSuccess(List<ConversationInfo> conversationInfos, boolean hasMore) throws RemoteException {
                    if (conversationInfos != null) {
                        UnreadCount unreadCount = new UnreadCount();
                        for (ConversationInfo info : conversationInfos) {
                            if (!info.isSilent) {
                                unreadCount.unread += info.unreadCount.unread;
                            }
                            unreadCount.unreadMention += info.unreadCount.unreadMention;
                            unreadCount.unreadMentionAll += info.unreadCount.unreadMentionAll;
                        }
                        postUnreadCount(unreadCount);
                    }
                }

                @Override
                public void onFail(int errorCode) throws RemoteException {

                }
            });

        });
    }

    private void postUnreadCount(UnreadCount unreadCount) {
        if (unreadCountLiveData == null) {
            return;
        }
        unreadCountLiveData.postValue(unreadCount);
    }

    @Override
    public void onReceiveMessage(List<Message> messages, boolean hasMore) {
        reloadConversationList(true);
        reloadConversationUnreadStatus();
    }

    @Override
    public void onRecallMessage(Message message) {
        reloadConversationList();
        reloadConversationUnreadStatus();
    }


    @Override
    public void onSendSuccess(Message message) {
        reloadConversationList();
    }

    @Override
    public void onSendFail(Message message, int errorCode) {
        reloadConversationList();
    }

    @Override
    public void onSendPrepare(Message message, long savedTime) {
        Conversation conversation = message.conversation;
        if (types.contains(conversation.type) && lines.contains(conversation.line)) {
            if (message.messageId > 0) {
                reloadConversationList();
            }
        }
    }

    public void removeConversation(ConversationInfo conversationInfo, boolean clearMsg) {
        ChatManager.getInstance().clearUnreadStatus(conversationInfo.conversation);
        ChatManager.getInstance().removeConversation(conversationInfo.conversation, clearMsg);
    }

    public void clearMessages(Conversation conversation) {
        ChatManager.getInstance().clearMessages(conversation);
    }


    public void setConversationTop(ConversationInfo conversationInfo, boolean top) {
        ChatManager.getInstance().setConversationTop(conversationInfo.conversation, top);
    }

    @Override
    public void onMessagedRemove(Message message) {
        reloadConversationList();
        reloadConversationUnreadStatus();
    }

    @Override
    public void onConversationDraftUpdate(ConversationInfo conversationInfo, String draft) {
        reloadConversationList();
    }

    @Override
    public void onConversationTopUpdate(ConversationInfo conversationInfo, boolean top) {
        reloadConversationList();
    }

    @Override
    public void onConversationSilentUpdate(ConversationInfo conversationInfo, boolean silent) {
        reloadConversationList();
    }

    @Override
    public void onConversationUnreadStatusClear(ConversationInfo conversationInfo) {
        reloadConversationList();
        reloadConversationUnreadStatus();
    }

    @Override
    public void onConnectionStatusChange(int status) {
        connectionStatusLiveData.postValue(status);
    }

    @Override
    public void onClearMessage(Conversation conversation) {
        reloadConversationList();
        reloadConversationUnreadStatus();
    }

    @Override
    public void onConversationRemove(Conversation conversation) {
        reloadConversationList();
        reloadConversationUnreadStatus();
    }
}
