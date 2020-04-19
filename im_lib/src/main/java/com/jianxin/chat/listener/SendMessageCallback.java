package com.jianxin.chat.listener;

public interface SendMessageCallback {
    void onSuccess(long messageUid, long timestamp);

    void onFail(int errorCode);

    void onPrepare(long messageId, long savedTime);

     void onProgress(long uploaded, long total) ;


     void onMediaUpload(String remoteUrl) ;

}
