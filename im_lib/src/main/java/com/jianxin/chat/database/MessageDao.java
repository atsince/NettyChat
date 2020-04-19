package com.jianxin.chat.database;

import com.jianxin.chat.model.proto.ProtoMessage;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MessageDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void  insertMessages(ProtoMessage... protoMessage);
    @Update
    int updateMessage(ProtoMessage... protoMessage);
    @Delete
    void deleteWords(ProtoMessage... protoMessage);

    @Query("SELECT * FROM protomMessage WHERE target LIKE :target AND ConversationType LIKE :conversionType AND  timestamp <:timestamp ORDER BY timestamp DESC LIMIT 20")
    List<ProtoMessage> getMessages(int conversionType,String target,long timestamp);
}
