package com.jianxin.chat.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

import com.jianxin.chat.bean.core.ContentTag;
import com.jianxin.chat.bean.core.MessagePayload;
import com.jianxin.chat.bean.core.PersistFlag;
import com.jianxin.chat.bean.message.Message;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by heavyrain lee on 2017/12/6.
 */

public abstract class MessageContent implements Parcelable {


    public abstract MessagePayload encode();

    public abstract void decode(MessagePayload payload);

    public abstract String digest(Message message);

    //0 普通消息, 1 部分提醒, 2 提醒全部
    public int mentionedType;

    //提醒对象，mentionedType 1时有效
    public ArrayList<String> mentionedTargets;
    public String extra;
    public String pushContent;

    final public int getType() {
        ContentTag tag = getClass().getAnnotation(ContentTag.class);
        if (tag != null) {
            return tag.type();
        }
        return -1;
    }

    final public PersistFlag getPersistFlag() {
        ContentTag tag = getClass().getAnnotation(ContentTag.class);
        if (tag != null) {
            return tag.flag();
        }
        return PersistFlag.No_Persist;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mentionedType);
        dest.writeStringList(this.mentionedTargets);
        dest.writeString(this.extra);
        dest.writeString(this.pushContent);
    }

    public MessageContent() {
    }

    protected MessageContent(Parcel in) {
        this.mentionedType = in.readInt();
        this.mentionedTargets = in.createStringArrayList();
        this.extra = in.readString();
        this.pushContent = in.readString();
    }
}
