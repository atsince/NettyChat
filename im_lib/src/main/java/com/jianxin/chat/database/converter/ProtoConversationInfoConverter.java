package com.jianxin.chat.database.converter;

import com.alibaba.fastjson.JSON;
import com.jianxin.chat.model.proto.ProtoConversationInfo;
import com.jianxin.chat.model.proto.ProtoMessage;

import androidx.room.TypeConverter;

public class ProtoConversationInfoConverter {
    @TypeConverter
    public ProtoConversationInfo stringToObject(String value ){
        ProtoConversationInfo protoConversationInfo= JSON.parseObject(value,ProtoConversationInfo.class);

        return protoConversationInfo;
    }

    @TypeConverter
    public String objectToString(ProtoConversationInfo protoConversationInfo ) {

        return JSON.toJSONString(protoConversationInfo);
    }
}
