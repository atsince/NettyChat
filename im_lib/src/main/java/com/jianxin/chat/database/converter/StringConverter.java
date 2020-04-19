package com.jianxin.chat.database.converter;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.room.TypeConverter;

public class StringConverter {
 
    @TypeConverter
   public ArrayList<String> stringToObject(String value ){
        ArrayList<String> list=new ArrayList<>();
        if(!TextUtils.isEmpty(value)){
            if(value.contains(",")){
                String [] array=value.split(",");
                for(int i=0;i<array.length;i++){
                    list.add(array[i]);
                }
            }else{
                list.add(value);
            }
        }
        return list;
    }
 
    @TypeConverter
    public String objectToString(ArrayList<String> list ) {
        StringBuilder builder = new StringBuilder();
        if(list!=null){
        for(int i=0;i<list.size();i++){
            builder.append(list.get(i));
            if(i!=list.size()-1){
                builder.append(",");
            }
        }
        }
        return builder.toString();
    }

    }
