package com.jianxin.im.conversation.message.viewholder;

import android.util.Log;
import android.util.SparseArray;

import com.jianxin.chat.bean.core.ContentTag;
import com.jianxin.chat.bean.message.MessageContent;
import com.jianxin.im.annotation.LayoutRes;
import com.jianxin.im.annotation.MessageContentType;
import com.jianxin.im.annotation.ReceiveLayoutRes;
import com.jianxin.im.annotation.SendLayoutRes;


public class MessageViewHolderManager {
    private static MessageViewHolderManager instance = new MessageViewHolderManager();

    private MessageViewHolderManager() {
        init();
    }

    public static MessageViewHolderManager getInstance() {
        return instance;
    }

    private void init() {
//        registerMessageViewHolder(AudioMessageContentViewHolder.class);
//        registerMessageViewHolder(FileMessageContentViewHolder.class);
//        registerMessageViewHolder(ImageMessageContentViewHolder.class);
//        registerMessageViewHolder(StickerMessageContentViewHolder.class);
        registerMessageViewHolder(TextMessageContentViewHolder.class);
//        registerMessageViewHolder(VideoMessageContentViewHolder.class);
//        registerMessageViewHolder(VoipMessageViewHolder.class);
//        registerMessageViewHolder(SimpleNotificationMessageContentViewHolder.class);
//        registerMessageViewHolder(RecallMessageContentViewHolder.class);
    }

    private SparseArray<Class<? extends MessageContentViewHolder>> messageViewHolders = new SparseArray<>();

    public void registerMessageViewHolder(Class<? extends MessageContentViewHolder> clazz) {
        MessageContentType contentType = clazz.getAnnotation(MessageContentType.class);
        if (contentType == null) {
            throw new IllegalArgumentException("the message content viewHolder must be annotated with MessageContentType");
        }

        SendLayoutRes sendLayoutRes = clazz.getAnnotation(SendLayoutRes.class);
        ReceiveLayoutRes receiveLayoutRes = clazz.getAnnotation(ReceiveLayoutRes.class);
        LayoutRes layoutRes = clazz.getAnnotation(LayoutRes.class);

        // TODO
        if (sendLayoutRes == null && receiveLayoutRes == null && layoutRes == null) {
            throw new IllegalArgumentException("the message content viewHolder must have LayoutRes annotation");
        }

        Class<? extends MessageContent> clazzes[] = contentType.value();
        for (Class<? extends MessageContent> notificationClazz : clazzes) {
            ContentTag contentTag = notificationClazz.getAnnotation(ContentTag.class);
            if (messageViewHolders.get(contentTag.type()) == null) {
                messageViewHolders.put(contentTag.type(), clazz);
            } else {
                Log.e(MessageViewHolderManager.class.getSimpleName(), "re-register message view holder " + clazz.getSimpleName());
            }
        }
    }

    public Class<? extends MessageContentViewHolder> getMessageContentViewHolder(int messageType) {
        Class clazz = messageViewHolders.get(messageType);
        if (clazz == null) {
            //return UnkownMessageContentViewHolder.class;
        }
        return clazz;
    }
}
