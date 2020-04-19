// IConnectionStatusChanged.aidl
package com.jianxin.chat.client;



interface ISendMessageCallback {
    void onSuccess(long messageId, long servertime);
    void onFailure(int errorCode);
    void onPrepared(long messageId, long savedTime);
    void onProgress(long uploaded, long total);
    void onMediaUploaded(String remoteUrl);
}
