package com.jianxin.im.annotation;

import com.jianxin.chat.bean.message.MessageContent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



/**
 * 用于设置消息UI({@link conversation.message.viewholder.MessageContentViewHolder})和消息体({@link MessageContent})对应关系
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MessageContentType {
    Class<? extends MessageContent>[] value();
}
