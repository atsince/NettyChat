package com.jianxin.im.conversationlist.notification;

import com.jianxin.im.annotation.LayoutRes;
import com.jianxin.im.annotation.StatusNotificationType;
import com.jianxin.im.conversationlist.notification.viewholder.ConnectionNotificationViewHolder;
import com.jianxin.im.conversationlist.notification.viewholder.StatusNotificationViewHolder;

import java.util.HashMap;
import java.util.Map;


public class StatusNotificationManager {
    private static StatusNotificationManager instance;
    private Map<Class<? extends StatusNotification>, Class<? extends StatusNotificationViewHolder>> notificationViewHolders;

    public synchronized static StatusNotificationManager getInstance() {
        if (instance == null) {
            instance = new StatusNotificationManager();
        }
        return instance;
    }

    private StatusNotificationManager() {
        init();
    }

    private void init() {
        notificationViewHolders = new HashMap<>();
     //   registerNotificationViewHolder(PCOnlineNotificationViewHolder.class);
        registerNotificationViewHolder(ConnectionNotificationViewHolder.class);
    }

    public void registerNotificationViewHolder(Class<? extends StatusNotificationViewHolder> holderClass) {
        StatusNotificationType notificationType = holderClass.getAnnotation(StatusNotificationType.class);
        LayoutRes layoutRes = holderClass.getAnnotation(LayoutRes.class);
        if (notificationType == null || layoutRes == null) {
            throw new IllegalArgumentException("missing annotation");
        }
        notificationViewHolders.put(notificationType.value(), holderClass);
    }

    public Class<? extends StatusNotificationViewHolder> getNotificationViewHolder(StatusNotification notification) {
        return notificationViewHolders.get(notification.getClass());
    }
}
