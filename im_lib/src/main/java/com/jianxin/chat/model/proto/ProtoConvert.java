package com.jianxin.chat.model.proto;

import android.text.TextUtils;

import com.jianxin.chat.bean.core.ContentTag;
import com.jianxin.chat.bean.core.MessageDirection;
import com.jianxin.chat.bean.core.MessagePayload;
import com.jianxin.chat.bean.core.MessageStatus;
import com.jianxin.chat.bean.core.PersistFlag;
import com.jianxin.chat.bean.message.Message;
import com.jianxin.chat.bean.message.MessageContent;
import com.jianxin.chat.bean.message.UnknownMessageContent;
import com.jianxin.chat.model.Conversation;
import com.jianxin.chat.model.ConversationInfo;
import com.jianxin.chat.model.UnreadCount;

import java.util.HashMap;
import java.util.Map;

public class ProtoConvert {
    public Map<Integer, Class<? extends MessageContent>> contentMapper = new HashMap<>();

    public void registerMessageContent(String msgContentCls) {
        Class cls = null;
        try {
            cls = Class.forName(msgContentCls);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ContentTag tag = (ContentTag) cls.getAnnotation(ContentTag.class);
        if (tag != null) {
            Class curClazz = contentMapper.get(tag.type());
            if (curClazz != null && !curClazz.equals(cls)) {
                throw new IllegalArgumentException("messageContent type duplicate " + msgContentCls);
            }
            contentMapper.put(tag.type(), cls);
        } else {
            throw new IllegalStateException("ContentTag annotation must be set!");
        }
    }

    public ProtoMessage convertProtoMessage(Message msg) {
        ProtoMessage protoMessage = new ProtoMessage();

        if (msg.conversation != null) {
            protoMessage.setConversationType(msg.conversation.type.getValue());
            protoMessage.setTarget(msg.conversation.target);
            protoMessage.setLine(msg.conversation.line);
        }
        protoMessage.setFrom(msg.sender);
        protoMessage.setTos(msg.toUsers);
        MessagePayload payload = msg.content.encode();
        payload.extra = msg.content.extra;
        payload.contentType = msg.content.getClass().getAnnotation(ContentTag.class).type();
        protoMessage.setContent(payload.toProtoContent());
        protoMessage.setMessageId(msg.messageId);
        protoMessage.setMsgId(msg.getMsgId());
        protoMessage.setMessageUid(msg.messageUid);
        protoMessage.setDirection(msg.direction.ordinal());
        protoMessage.setStatus(msg.status.value());
        protoMessage.setTimestamp(msg.timeStamp);
        return protoMessage;
    }

    public Message convertMessage(ProtoMessage protoMessage) {
        if (protoMessage == null || TextUtils.isEmpty(protoMessage.getTarget())) {
            return null;
        }
        Message msg = new Message();
        msg.setMsgId(protoMessage.getMsgId());
        msg.setMessageId(protoMessage.getMessageId());
        msg.setMessageUid(protoMessage.getMessageUid());
        msg.setConversation(new Conversation(Conversation.ConversationType.type(protoMessage.getConversationType()), protoMessage.getTarget(), protoMessage.getLine()));
        msg.setSender(protoMessage.getFrom());
        msg.toUsers = protoMessage.getTos();
        msg.setStatus(MessageStatus.status(protoMessage.getStatus()));
        msg.setTimeStamp(protoMessage.getTimestamp());
        msg.setDirection(MessageDirection.direction(protoMessage.getDirection()));
        if (protoMessage.getContent() != null) {
            msg.content = contentOfType(protoMessage.getContent().getType());
            MessagePayload payload = new MessagePayload(protoMessage.getContent());
            try {
                msg.content.decode(payload);
                msg.content.extra = payload.extra;
            } catch (Exception e) {
                e.printStackTrace();
                if (msg.content.getPersistFlag() == PersistFlag.Persist || msg.content.getPersistFlag() == PersistFlag.Persist_And_Count) {
                    msg.content = new UnknownMessageContent();
                    ((UnknownMessageContent) msg.content).setOrignalPayload(payload);
                } else {
                    return null;
                }
            }

        }
        return msg;
    }

    private MessageContent contentOfType(int type) {
        Class<? extends MessageContent> cls = contentMapper.get(type);
        if (cls != null) {
            try {
                return cls.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return new UnknownMessageContent();
    }


    public ConversationInfo convertConversationInfo(ProtoConversationInfo protoConversationInfo) {
        if (protoConversationInfo == null || TextUtils.isEmpty(protoConversationInfo.getTarget())) {
            return null;
        }
        ConversationInfo conversationInfo = new ConversationInfo();
        conversationInfo.conversation = new Conversation(Conversation.ConversationType.type(protoConversationInfo.getConversationType()), protoConversationInfo.getTarget());
        conversationInfo.timestamp = protoConversationInfo.getTimestamp();
        conversationInfo.isTop = protoConversationInfo.isTop();
        conversationInfo.draft = protoConversationInfo.getDraft();
        conversationInfo.isSilent = protoConversationInfo.isSilent();
        conversationInfo.unreadCount= covertUnReadCount(protoConversationInfo.getUnreadCount());
        ProtoMessage protoMessage = protoConversationInfo.getLastMessage();
        if (protoMessage != null) {
            conversationInfo.lastMessage = convertMessage(protoMessage);
        }
        return conversationInfo;
    }
public UnreadCount covertUnReadCount(ProtoUnreadCount protoUnreadCount){
    UnreadCount unreadCount=new UnreadCount();
    if(protoUnreadCount!=null){
        unreadCount.unread=protoUnreadCount.getUnread();
        unreadCount.unreadMention=protoUnreadCount.getUnreadMention();
        unreadCount.unreadMentionAll=protoUnreadCount.getUnreadMentionAll();
    }
    return unreadCount;
}

    public ProtoConversationInfo convertConversationInfo(ConversationInfo conversationInfo) {
        if (conversationInfo == null || conversationInfo.conversation==null) {
            return null;
        }
        ProtoConversationInfo protoConversationInfo = new ProtoConversationInfo();
        protoConversationInfo.setTarget(conversationInfo.conversation.target);
        protoConversationInfo.setConversationType(conversationInfo.conversation.type.getValue());
        protoConversationInfo.setTimestamp(conversationInfo.timestamp);
        protoConversationInfo.setTop(conversationInfo.isTop);
        protoConversationInfo.setDraft(conversationInfo.draft);
        protoConversationInfo.setSilent(conversationInfo.isSilent);
        Message lastMessage = conversationInfo.lastMessage;
        if (lastMessage != null) {
            protoConversationInfo.setLastMessage(convertProtoMessage(lastMessage));
        }
        return protoConversationInfo;
    }
}
