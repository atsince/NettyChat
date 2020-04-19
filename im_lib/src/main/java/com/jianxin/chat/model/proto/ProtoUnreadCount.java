package com.jianxin.chat.model.proto;

import androidx.room.Entity;

@Entity
public class ProtoUnreadCount {
    private int unread;
    private int unreadMention;
    private int unreadMentionAll;

    public ProtoUnreadCount() {
    }

    public int getUnread() {
        return this.unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public int getUnreadMention() {
        return this.unreadMention;
    }

    public void setUnreadMention(int unreadMention) {
        this.unreadMention = unreadMention;
    }

    public int getUnreadMentionAll() {
        return this.unreadMentionAll;
    }

    public void setUnreadMentionAll(int unreadMentionAll) {
        this.unreadMentionAll = unreadMentionAll;
    }
}
