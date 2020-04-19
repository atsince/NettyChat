package com.jianxin.im.netty.handler;

import com.jianxin.im.netty.NettyTcpClient;
import com.jianxin.im.protobuf.MessageProtobuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * <p>@ProjectName:     NettyChat</p>
 * <p>@ClassName:       HeartbeatHandler.java</p>
 * <p>@PackageName:     com.freddy.im</p>
 * <b>
 * <p>@Description:     心跳任务管理器</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/04/08 01:34</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public class HeartbeatHandler extends SimpleChannelInboundHandler<MessageProtobuf.Msg> {

    private NettyTcpClient imsClient;
    public HeartbeatHandler(NettyTcpClient imsClient) {
        this.imsClient = imsClient;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        System.out.println("======HeartbeatHandler ======userEventTriggered=");
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            switch (state) {
                case READER_IDLE: {
                    // 规定时间内没收到服务端心跳包响应，进行重连操作
                    imsClient.resetConnect(false);
                    break;
                }

                case WRITER_IDLE: {
                    // 规定时间内没向服务端发送心跳包，即发送一个心跳包
                    if (heartbeatTask == null) {
                        heartbeatTask = new HeartbeatTask(ctx);
                    }

                    imsClient.getLoopGroup().execWorkTask(heartbeatTask);
                    break;
                }
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        System.out.println("======HeartbeatHandler ======channelRead=");
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
            System.out.println("==HeartbeatHandler==channelRead==aaa==消息透传");
            ctx.fireChannelRead(msg);
        }
    }

    private HeartbeatTask heartbeatTask;

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, MessageProtobuf.Msg msg) throws Exception {
        System.out.println("======HeartbeatHandler ======messageReceived=");
    }

    private class HeartbeatTask implements Runnable {

        private ChannelHandlerContext ctx;

        public HeartbeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            if (ctx.channel().isActive()) {
                MessageProtobuf.Msg heartbeatMsg = imsClient.getHeartbeatMsg();
                if (heartbeatMsg == null) {
                    return;
                }
                System.out.println("发送心跳消息，message=" + heartbeatMsg + "当前心跳间隔为：" + imsClient.getHeartbeatInterval() + "ms\n");
                imsClient.sendMsg(heartbeatMsg, false);
            }
        }
    }
}
