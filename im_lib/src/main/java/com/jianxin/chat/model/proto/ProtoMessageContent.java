package com.jianxin.chat.model.proto;

import com.jianxin.chat.database.converter.StringConverter;

import java.util.ArrayList;

import androidx.room.Entity;
import androidx.room.TypeConverters;

@Entity
@TypeConverters(StringConverter.class)
public class ProtoMessageContent {

    private int type;
    private String searchableContent;
    private String pushContent;
    private String content;
    private byte[] binaryContent;
    private String localContent;
    private int mediaType;
    private String remoteMediaUrl;
    private String localMediaPath;
    private int mentionedType;
    private ArrayList<String> mentionedTargets;
    private String extra;

    public ProtoMessageContent() {
    }

    public int getMentionedType() {
        return this.mentionedType;
    }

    public void setMentionedType(int mentionedType) {
        this.mentionedType = mentionedType;
    }

    public ArrayList<String> getMentionedTargets() {
        return this.mentionedTargets;
    }

    public void setMentionedTargets(ArrayList<String> mentionedTargets) {
        this.mentionedTargets = mentionedTargets;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSearchableContent() {
        return this.searchableContent;
    }

    public void setSearchableContent(String searchableContent) {
        this.searchableContent = searchableContent;
    }

    public String getPushContent() {
        return this.pushContent;
    }

    public void setPushContent(String pushContent) {
        this.pushContent = pushContent;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getBinaryContent() {
        return this.binaryContent;
    }

    public void setBinaryContent(byte[] binaryContent) {
        this.binaryContent = binaryContent;
    }

    public String getLocalContent() {
        return this.localContent;
    }

    public void setLocalContent(String localContent) {
        this.localContent = localContent;
    }

    public int getMediaType() {
        return this.mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public String getRemoteMediaUrl() {
        return this.remoteMediaUrl;
    }

    public void setRemoteMediaUrl(String remoteMediaUrl) {
        this.remoteMediaUrl = remoteMediaUrl;
    }

    public String getLocalMediaPath() {
        return this.localMediaPath;
    }

    public void setLocalMediaPath(String localMediaPath) {
        this.localMediaPath = localMediaPath;
    }

    public String getExtra() {
        return this.extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
