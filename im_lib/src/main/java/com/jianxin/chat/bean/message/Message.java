package com.jianxin.chat.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

import com.jianxin.chat.bean.core.MessageDirection;
import com.jianxin.chat.bean.core.MessageStatus;
import com.jianxin.chat.model.Conversation;

import java.util.ArrayList;
import java.util.UUID;

import androidx.room.Entity;

@Entity
public class Message implements Parcelable {

   public String msgId;
    public long messageId;
    public long messageUid;
    public Conversation conversation;
    public String sender;
    public ArrayList<String> toUsers;
    /**
     * 消息在会话中定向发送给指定用户
     */
    public MessageContent content;
    public MessageDirection direction=MessageDirection.Send;
    public MessageStatus status;
    public long timeStamp;
    public String extra;

    public Message() {

    }

    protected Message(Parcel in) {
        msgId=in.readString();
        messageId = in.readLong();
        messageUid=in.readLong();
        conversation = in.readParcelable(Conversation.class.getClassLoader());
        sender = in.readString();
        this.toUsers = in.createStringArrayList();
        content = in.readParcelable(MessageContent.class.getClassLoader());
        int tmpDirection = in.readInt();
        this.direction = tmpDirection == -1 ? null : MessageDirection.values()[tmpDirection];
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : MessageStatus.values()[tmpStatus];
        timeStamp = in.readLong();
        extra = in.readString();
    }
     public String digest() {
        return content.digest(this);
    }
    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public long getMessageUid() {
        return messageUid;
    }

    public void setMessageUid(long messageUid) {
        this.messageUid = messageUid;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public MessageContent getContent() {
        return content;
    }

    public void setContent(MessageContent content) {
        this.content = content;
    }

    public MessageDirection getDirection() {
        return direction;
    }

    public void setDirection(MessageDirection direction) {
        this.direction = direction;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(msgId);
        dest.writeLong(messageId);
        dest.writeLong(messageUid);
        dest.writeParcelable(conversation, flags);
        dest.writeString(sender);
        dest.writeList(this.toUsers);
        dest.writeParcelable(content, flags);
        dest.writeInt(this.direction == null ? -1 : this.direction.ordinal());
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
        dest.writeLong(timeStamp);
        dest.writeString(extra);
    }
}
