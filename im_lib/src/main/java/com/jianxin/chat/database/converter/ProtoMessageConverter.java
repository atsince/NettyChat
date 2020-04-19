package com.jianxin.chat.database.converter;

import com.alibaba.fastjson.JSON;
import com.jianxin.chat.model.proto.ProtoConversationInfo;
import com.jianxin.chat.model.proto.ProtoMessage;

import androidx.room.TypeConverter;

public class ProtoMessageConverter {
    @TypeConverter
    public ProtoMessage stringToObject(String value ){
        ProtoMessage protoMessage= JSON.parseObject(value,ProtoMessage.class);

        return protoMessage;
    }

    @TypeConverter
    public String objectToString(ProtoMessage protoMessage ) {

        return JSON.toJSONString(protoMessage);
    }
}
