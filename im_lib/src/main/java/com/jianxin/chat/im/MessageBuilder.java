package com.jianxin.chat.im;

import com.jianxin.chat.bean.core.MessageDirection;
import com.jianxin.chat.model.proto.ProtoMessage;
import com.jianxin.chat.model.proto.ProtoMessageContent;
import com.jianxin.chat.utils.StringUtil;
import com.jianxin.chat.im.netty.protobuf.MessageProtobuf;

/**
 * <p>@ProjectName:     BoChat</p>
 * <p>@ClassName:       MessageBuilder.java</p>
 * <p>@PackageName:     com.bochat.app.message</p>
 * <b>
 * <p>@Description:     消息转换</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/02/07 17:26</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public class MessageBuilder {






    /**
     * 根据业务消息对象获取protoBuf消息对应的builder
     *
     * @param message
     * @return
     */
    public static MessageProtobuf.Msg.Builder getProtoBufMessageBuilderByAppMessage(ProtoMessage message) {
        MessageProtobuf.Msg.Builder builder = MessageProtobuf.Msg.newBuilder();
        MessageProtobuf.Head.Builder headBuilder = MessageProtobuf.Head.newBuilder();
        headBuilder.setMsgType(message.getConversationType());
        headBuilder.setStatus(message.getStatus());
        headBuilder.setMsgId(message.getMsgId());
        headBuilder.setFrom(message.getFrom());
        headBuilder.setTarget(message.getTarget());
        headBuilder.setTimestamp(message.getTimestamp());

        if (!StringUtil.isEmpty(message.getExtend())){
            headBuilder.setExtend(message.getExtend());
        }
        builder.setHead(headBuilder);

        if (message.getContent()!=null){
            MessageProtobuf.Body.Builder bodyBuilder=MessageProtobuf.Body.newBuilder();
            ProtoMessageContent protoMessageContent =message.getContent();

            bodyBuilder.setType(protoMessageContent.getType());
            if(!StringUtil.isEmpty(protoMessageContent.getContent())){
                bodyBuilder.setContent(protoMessageContent.getContent());
            }
            if(!StringUtil.isEmpty(protoMessageContent.getRemoteMediaUrl())){
                bodyBuilder.setUrl(protoMessageContent.getRemoteMediaUrl());
            }
            if(!StringUtil.isEmpty(protoMessageContent.getExtra())){
                bodyBuilder.setExtra(protoMessageContent.getExtra());
            }
            builder.setBody(bodyBuilder.build());
        }

        return builder;
    }

    /**
     * 通过protobuf消息对象获取业务消息对象
     *
     * @param protobufMessage
     * @return
     */
    public static ProtoMessage getMessageByProtobuf(
            MessageProtobuf.Msg protobufMessage) {
        MessageProtobuf.Head protoHead = protobufMessage.getHead();
        MessageProtobuf.Body body=protobufMessage.getBody();
        ProtoMessage message = new ProtoMessage();
        message.setDirection(MessageDirection.Receive.value());
        message.setConversationType(protoHead.getMsgType());
        message.setStatus(protoHead.getStatus());
        message.setMessageUid(protoHead.getSeq());
        message.setMsgId(protobufMessage.getHead().getMsgId());
        message.setFrom(protoHead.getFrom());
        message.setTarget(protoHead.getTarget());
        message.setTimestamp(protoHead.getTimestamp());
        message.setExtend(protoHead.getExtend());

        if(body!=null){
            ProtoMessageContent content=new ProtoMessageContent();
            content.setType(body.getType());
            if(!StringUtil.isEmpty(body.getContent())){
                content.setContent(body.getContent());
            }
            if(!StringUtil.isEmpty(body.getUrl())){
                content.setRemoteMediaUrl(body.getUrl());
            }
            if(!StringUtil.isEmpty(body.getExtra())){
               content.setExtra(body.getExtra());
            }
            message.setContent(content);
        }
        return message;
    }

}
