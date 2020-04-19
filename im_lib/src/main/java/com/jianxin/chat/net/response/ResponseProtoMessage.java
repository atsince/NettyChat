package com.jianxin.chat.net.response;

import com.jianxin.chat.model.proto.ProtoMessage;

import java.util.ArrayList;

public class ResponseProtoMessage {
    ArrayList<ProtoMessage> messageList;

    public ArrayList<ProtoMessage> getMessageList() {
        return messageList;
    }

    public void setMessageList(ArrayList<ProtoMessage> messageList) {
        this.messageList = messageList;
    }
}
