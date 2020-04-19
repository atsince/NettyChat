package com.jianxin.chat.im;

import com.jianxin.chat.bean.message.Message;

public interface ISendMessageCallback {

    void onSendSuccess(long messsageUid,long serverTime);

    void onSendFail( int errorCode);

    /**
     * 消息已插入本地数据库
     *
     * @param savedTime
     */
    void onSendPrepared(long messsageId, long savedTime);

    /**
     * 发送进度，media类型消息，且媒体大于100k时，才有进度回调
     *
     * @param uploaded
     * @param total
     */
    void onProgress( long uploaded, long total);

    /**
     * media上传之后的url，media类型消息有效
     *
     * @param remoteUrl
     */
    void onMediaUpload( String remoteUrl) ;




}
