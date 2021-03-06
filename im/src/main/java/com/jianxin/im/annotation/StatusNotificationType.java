package com.jianxin.im.annotation;

import com.jianxin.im.conversationlist.notification.StatusNotification;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StatusNotificationType {
    Class<? extends StatusNotification> value();
}
