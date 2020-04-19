package com.jianxin.im.annotation;

import com.jianxin.chat.model.Conversation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * 用户设置会话UI({@link })和会话({@link } + 会话线路)之间的对应关系
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ConversationInfoType {
    Conversation.ConversationType type();

    int line();
}
