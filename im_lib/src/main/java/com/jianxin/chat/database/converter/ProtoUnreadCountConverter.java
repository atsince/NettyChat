package com.jianxin.chat.database.converter;

import com.alibaba.fastjson.JSON;
import com.jianxin.chat.model.proto.ProtoConversationInfo;
import com.jianxin.chat.model.proto.ProtoUnreadCount;

import androidx.room.TypeConverter;

public class ProtoUnreadCountConverter {
    @TypeConverter
    public ProtoUnreadCount stringToObject(String value ){
        ProtoUnreadCount unreadCount= JSON.parseObject(value,ProtoUnreadCount.class);

        return unreadCount;
    }

    @TypeConverter
    public String objectToString(ProtoUnreadCount unreadCount ) {

        return JSON.toJSONString(unreadCount);
    }
}
