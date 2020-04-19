package com.jianxin.im.conversationlist.notification.viewholder;

import android.view.View;

import com.jianxin.im.conversationlist.notification.StatusNotification;

import androidx.fragment.app.Fragment;

public abstract class StatusNotificationViewHolder {
    protected Fragment fragment;

    public StatusNotificationViewHolder(Fragment fragment) {
        this.fragment = fragment;
    }

    public abstract void onBind(View view, StatusNotification notification);
}
