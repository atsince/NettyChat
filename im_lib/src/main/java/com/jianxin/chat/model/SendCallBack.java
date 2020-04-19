package com.jianxin.chat.model;

import com.jianxin.chat.bean.message.Message;
import com.jianxin.chat.im.ISendMessageCallback;
import com.jianxin.chat.model.proto.ProtoMessage;

public class SendCallBack {

    private ProtoMessage message;
    private ISendMessageCallback callback;
    public ProtoMessage getMessage() {
        return message;
    }

    public void setMessage(ProtoMessage message) {
        this.message = message;
    }

    public ISendMessageCallback getCallback() {
        return callback;
    }

    public void setCallback(ISendMessageCallback callback) {
        this.callback = callback;
    }
}
