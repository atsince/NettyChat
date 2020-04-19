package com.jianxin.im.conversation;

import com.jianxin.chat.im.manager.ChatManager;
import com.jianxin.chat.bean.message.Message;
import com.jianxin.chat.listener.GetMessageCallback;
import com.jianxin.chat.model.Conversation;
import com.jianxin.im.common.AppScopeViewModel;
import com.jianxin.im.conversation.message.model.UiMessage;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class ConversationViewModel extends ViewModel implements AppScopeViewModel {
    private MutableLiveData<Conversation> clearConversationMessageLiveData;

    public MutableLiveData<Conversation> clearConversationMessageLiveData() {
        if (clearConversationMessageLiveData == null) {
            clearConversationMessageLiveData = new MutableLiveData<>();
        }
        return clearConversationMessageLiveData;
    }

    public ConversationViewModel() {
    }

    @Override
    protected void onCleared() {
    }

    public MutableLiveData<List<UiMessage>> loadOldMessages(Conversation conversation, String withUser, long fromMessageId, long fromMessageUid, int count) {
        MutableLiveData<List<UiMessage>> result = new MutableLiveData<>();
        ChatManager.getInstance().getWorkHandler().post(() -> {
            ChatManager.getInstance().getMessages(conversation, fromMessageId, true, count, withUser, new GetMessageCallback() {
                @Override
                public void onSuccess(List<Message> messageList, boolean hasMore) {
                    if (messageList != null && !messageList.isEmpty()) {
                        List<UiMessage> messages = new ArrayList<>();
                        for (Message msg : messageList) {
                            messages.add(new UiMessage(msg));
                        }
                        result.postValue(messages);
                    } else {
                        ChatManager.getInstance().getRemoteMessages(conversation, fromMessageUid,count, new GetMessageCallback() {
                            @Override
                            public void onSuccess(List<Message> messages,boolean more) {
                                if (messages != null && !messages.isEmpty()) {
                                    List<UiMessage> msgs = new ArrayList<>();
                                    for (Message msg : messages) {
                                        msgs.add(new UiMessage(msg));
                                    }
                                    result.postValue(msgs);
                                } else {
                                    result.postValue(new ArrayList<UiMessage>());
                                }
                            }

                            @Override
                            public void onFail(int errorCode) {
                                result.postValue(new ArrayList<UiMessage>());
                            }
                        });
                    }
                }

                @Override
                public void onFail(int errorCode) {

                }
            });
        });
        return result;
    }

    public LiveData<List<Message>> loadRemoteHistoryMessage(Conversation conversation, long fromMessageUid, int count) {
        MutableLiveData<List<Message>> data = new MutableLiveData<>();
        ChatManager.getInstance().getWorkHandler().post(() -> {
            ChatManager.getInstance().getRemoteMessages(conversation, fromMessageUid, count, new GetMessageCallback() {
                @Override
                public void onSuccess(List<Message> messages,boolean more) {
                    data.setValue(messages);
                }

                @Override
                public void onFail(int errorCode) {
                    data.setValue(new ArrayList<>());
                }
            });
        });
        return data;
    }
//
//    public MutableLiveData<List<UiMessage>> loadAroundMessages(Conversation conversation, String withUser, long focusIndex, int count) {
//        MutableLiveData<List<UiMessage>> result = new MutableLiveData<>();
//        ChatManager.getInstance().getWorkHandler().post(() -> {
//            List<Message> oldMessageList = ChatManager.getInstance().getMessages(conversation, focusIndex, true, count, withUser);
//            List<UiMessage> oldMessages = new ArrayList<>();
//            if (oldMessageList != null) {
//                for (Message msg : oldMessageList) {
//                    oldMessages.add(new UiMessage(msg));
//                }
//            }
//            Message message = ChatManager.getInstance().getMessage(focusIndex);
//            List<Message> newMessageList = ChatManager.getInstance().getMessages(conversation, focusIndex, false, count, withUser);
//            List<UiMessage> newMessages = new ArrayList<>();
//            if (newMessageList != null) {
//                for (Message msg : newMessageList) {
//                    newMessages.add(new UiMessage(msg));
//                }
//            }
//
//            List<UiMessage> messages = new ArrayList<>();
//            messages.addAll(oldMessages);
//            if (message != null) {
//                messages.add(new UiMessage(message));
//            }
//            messages.addAll(newMessages);
//            result.postValue(messages);
//        });
//
//        return result;
//    }

    public MutableLiveData<List<UiMessage>> loadNewMessages(Conversation conversation, String withUser, long startIndex, int count) {
        MutableLiveData<List<UiMessage>> result = new MutableLiveData<>();
        ChatManager.getInstance().getWorkHandler().post(() -> {
            ChatManager.getInstance().getMessages(conversation, startIndex, false, count, withUser, new GetMessageCallback() {
                @Override
                public void onSuccess(List<Message> messageList, boolean hasMore) {
                    List<UiMessage> messages = new ArrayList<>();
                    if (messageList != null) {
                        for (Message msg : messageList) {
                            messages.add(new UiMessage(msg));
                        }
                    }
                    result.postValue(messages);

                }

                @Override
                public void onFail(int errorCode) {

                }
            });
        });
        return result;
    }

    public void clearUnreadStatus(Conversation conversation) {
        ChatManager.getInstance().clearUnreadStatus(conversation);
    }

    public void clearConversationMessage(Conversation conversation) {
        ChatManager.getInstance().clearMessages(conversation);
        if (clearConversationMessageLiveData != null) {
            clearConversationMessageLiveData.setValue(conversation);
        }
    }

//    public ConversationInfo getConversationInfo(Conversation conversation) {
//        return ChatManager.getInstance().getConversation(conversation);
//    }


    public MutableLiveData<List<UiMessage>> getMessages(Conversation conversation, String withUser) {
        return loadOldMessages(conversation, withUser, Long.MAX_VALUE, Long.MAX_VALUE, 20);
    }
//
//    public void saveDraft(Conversation conversation, String draftString) {
//        ChatManager.Instance().setConversationDraft(conversation, draftString);
//    }
//
//    public void setConversationSilent(Conversation conversation, boolean silent) {
//        ChatManager.Instance().setConversationSilent(conversation, silent);
//    }

}
