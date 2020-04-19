package com.jianxin.chat.database.converter;

import com.alibaba.fastjson.JSON;
import com.jianxin.chat.model.proto.ProtoMessage;
import com.jianxin.chat.model.proto.ProtoMessageContent;


import androidx.room.TypeConverter;

public class ProtoMessageContentConverter {
    @TypeConverter
    public ProtoMessageContent stringToObject(String value ){
        ProtoMessageContent protoMessageContent= JSON.parseObject(value,ProtoMessageContent.class);

        return protoMessageContent;
    }

    @TypeConverter
    public String objectToString(ProtoMessageContent protoMessageContent ) {

        return JSON.toJSONString(protoMessageContent);
    }

}
