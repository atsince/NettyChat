package com.jianxin.chat.model.proto;

import com.jianxin.chat.database.converter.ProtoMessageContentConverter;
import com.jianxin.chat.database.converter.StringConverter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "protomMessage")
@TypeConverters({StringConverter.class, ProtoMessageContentConverter.class})
public class ProtoMessage {
    private int conversationType;
    @PrimaryKey
    @NonNull
    private String msgId;
    private String target;
    private int line;
    private String from;
    private ArrayList<String> tos;
    private ProtoMessageContent content;
    private long messageId;
    private int direction;
    private int status;
    private long messageUid;
    private long timestamp;
    private String extend;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public ProtoMessage() {
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

    public String getFrom() {
        return this.from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public ArrayList<String> getTos() {
        return this.tos;
    }

    public void setTos(ArrayList<String> tos) {
        this.tos = tos;
    }

    public ProtoMessageContent getContent() {
        return this.content;
    }

    public void setContent(ProtoMessageContent content) {
        this.content = content;
    }

    public long getMessageId() {
        return this.messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public int getDirection() {
        return this.direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getMessageUid() {
        return this.messageUid;
    }

    public void setMessageUid(long messageUid) {
        this.messageUid = messageUid;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
