package com.jianxin.chat.model.proto;

import com.jianxin.chat.database.converter.ProtoConversationInfoConverter;
import com.jianxin.chat.database.converter.ProtoMessageConverter;
import com.jianxin.chat.database.converter.ProtoUnreadCountConverter;
import com.jianxin.chat.database.converter.StringConverter;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
@TypeConverters({ProtoConversationInfoConverter.class, ProtoMessageConverter.class, ProtoUnreadCountConverter.class})
public class ProtoConversationInfo {
    private int conversationType;
    @PrimaryKey
    @NonNull
    private String target;
    private int line;
    private ProtoMessage lastMessage;
    private long timestamp;
    private String draft;
    private ProtoUnreadCount unreadCount;
    private boolean isTop;
    private boolean isSilent;

    public ProtoConversationInfo() {
    }

    public boolean isSilent() {
        return this.isSilent;
    }

    public void setSilent(boolean silent) {
        this.isSilent = silent;
    }

    public int getConversationType() {
        return this.conversationType;
    }

    public void setConversationType(int conversationType) {
        this.conversationType = conversationType;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public int getLine() {
        return this.line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public ProtoMessage getLastMessage() {
        return this.lastMessage;
    }

    public void setLastMessage(ProtoMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDraft() {
        return this.draft;
    }

    public void setDraft(String draft) {
        this.draft = draft;
    }

    public ProtoUnreadCount getUnreadCount() {
        return this.unreadCount;
    }

    public void setUnreadCount(ProtoUnreadCount unreadCount) {
        this.unreadCount = unreadCount;
    }

    public boolean isTop() {
        return this.isTop;
    }

    public void setTop(boolean top) {
        this.isTop = top;
    }
}
