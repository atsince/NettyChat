package com.jianxin.chat.database;

import com.jianxin.chat.model.proto.ProtoConversationInfo;
import com.jianxin.chat.model.proto.ProtoMessage;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ConversationInfoDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void  insertConversationInfo(ProtoConversationInfo... protoConversationInfo);
    @Update
    int updateConversationInfo(ProtoConversationInfo... protoConversationInfo);
    @Delete
    void deleteConversationInfo(ProtoConversationInfo... protoConversationInfo);

    @Query("SELECT * FROM ProtoConversationInfo WHERE target LIKE :target AND ConversationType LIKE :conversationType ")

    ProtoConversationInfo getConversationInfo(int conversationType, String target);


    @Query("SELECT * FROM ProtoConversationInfo  ORDER BY timestamp DESC ")
    List<ProtoConversationInfo> getConversationInfoList();
}
