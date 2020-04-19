package com.jianxin.im.conversation.message.viewholder;

import android.view.View;

import com.jianxin.im.conversation.ConversationFragment;

import androidx.recyclerview.widget.RecyclerView;

public abstract class NotificationMessageContentViewHolder extends MessageContentViewHolder {
    public NotificationMessageContentViewHolder(ConversationFragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(fragment, adapter, itemView);
    }
}
