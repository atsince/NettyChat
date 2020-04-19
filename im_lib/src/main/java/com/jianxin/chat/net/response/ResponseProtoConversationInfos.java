package com.jianxin.chat.net.response;

import com.jianxin.chat.model.proto.ProtoConversationInfo;
import com.jianxin.chat.model.proto.ProtoMessage;

import java.util.ArrayList;

public class ResponseProtoConversationInfos {
    ArrayList<ProtoConversationInfo> conversationInfos;

    public ArrayList<ProtoConversationInfo> getConversationInfos() {
        return conversationInfos;
    }

    public void setConversationInfos(ArrayList<ProtoConversationInfo> conversationInfos) {
        this.conversationInfos = conversationInfos;
    }
}
