package com.jianxin.chat.bean.message;

import android.os.Parcel;


import com.jianxin.chat.bean.core.ContentTag;
import com.jianxin.chat.bean.core.MessageContentType;
import com.jianxin.chat.bean.core.MessagePayload;
import com.jianxin.chat.bean.core.PersistFlag;

import static com.jianxin.chat.bean.core.MessageContentType.ContentType_Text;

/**
 * Created by heavyrain lee on 2017/12/6.
 */

@ContentTag(type = ContentType_Text, flag = PersistFlag.Persist_And_Count)
public class TextMessageContent extends MessageContent {
    private String content;

    public TextMessageContent() {
    }

    public TextMessageContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = new MessagePayload();
        payload.content=content;
        payload.searchableContent = content;
        payload.mentionedType = mentionedType;
        payload.mentionedTargets = mentionedTargets;
        return payload;
    }


    @Override
    public void decode(MessagePayload payload) {
        content=payload.content;
       // content = payload.searchableContent;
        mentionedType = payload.mentionedType;
        mentionedTargets = payload.mentionedTargets;
    }

    @Override
    public String digest(Message message) {
        return content;
    }




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.content);
    }

    protected TextMessageContent(Parcel in) {
        super(in);
        this.content = in.readString();
    }

    public static final Creator<TextMessageContent> CREATOR = new Creator<TextMessageContent>() {
        @Override
        public TextMessageContent createFromParcel(Parcel source) {
            return new TextMessageContent(source);
        }

        @Override
        public TextMessageContent[] newArray(int size) {
            return new TextMessageContent[size];
        }
    };
}
