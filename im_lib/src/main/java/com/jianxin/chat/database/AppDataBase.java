package com.jianxin.chat.database;

import com.jianxin.chat.model.proto.ProtoConversationInfo;
import com.jianxin.chat.model.proto.ProtoMessage;

import androidx.room.Database;
import androidx.room.RoomDatabase;
@Database(entities = {ProtoMessage.class, ProtoConversationInfo.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract MessageDao MsgDao();
    public abstract ConversationInfoDao ConversationInfoDao();
}
