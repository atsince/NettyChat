package com.jianxin.im.netty.handler;

import com.jianxin.im.netty.NettyTcpClient;
import com.jianxin.im.protobuf.MessageProtobuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * <p>@ProjectName:     NettyChat</p>
 * <p>@ClassName:       HeartbeatRespHandler.java</p>
 * <p>@PackageName:     com.freddy.im</p>
 * <b>
 * <p>@Description:     心跳消息响应处理handler</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/04/08 01:08</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public class HeartbeatRespHandler extends SimpleChannelInboundHandler<MessageProtobuf.Msg> {

    private NettyTcpClient imsClient;

    public HeartbeatRespHandler(NettyTcpClient imsClient) {
        this.imsClient = imsClient;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("==HeartbeatRespHandler==channelRead==cccc==");

        MessageProtobuf.Msg heartbeatRespMsg = (MessageProtobuf.Msg) msg;
        if (heartbeatRespMsg == null || heartbeatRespMsg.getHead() == null) {
            return;
        }

        MessageProtobuf.Msg heartbeatMsg = imsClient.getHeartbeatMsg();
        if (heartbeatMsg == null || heartbeatMsg.getHead() == null) {
            return;
        }

        int heartbeatMsgType = heartbeatMsg.getHead().getMsgType();
        if (heartbeatMsgType == heartbeatRespMsg.getHead().getMsgType()) {
            System.out.println("收到服务端心跳响应消息，message=" + heartbeatRespMsg);
        } else {
            // 消息透传
            System.out.println("==HeartbeatRespHandler==channelRead==cccc==消息透传");
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, MessageProtobuf.Msg msg) throws Exception {
        System.out.println("HeartbeatRespHandler===messageReceived===cccc");
    }
}
