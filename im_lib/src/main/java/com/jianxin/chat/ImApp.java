package com.jianxin.chat;

import android.content.Context;
import android.os.Handler;

public class ImApp {

    private static Context mContext;//上下文
    private static long mMainThreadId;//主线程id
    private static Handler mHandler;//主线程Handler


 public static void init(Context context){
     mContext = context.getApplicationContext();
     mMainThreadId = android.os.Process.myTid();
     mHandler = new Handler();
 }

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context mContext) {
        ImApp.mContext = mContext;
    }

    public static long getMainThreadId() {
        return mMainThreadId;
    }

    public static Handler getMainHandler() {
        return mHandler;
    }
}
