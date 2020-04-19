package com.jianxin.im.netty.handler;

import com.alibaba.fastjson.JSONObject;
import com.jianxin.im.netty.IMSConfig;
import com.jianxin.im.netty.NettyTcpClient;
import com.jianxin.im.protobuf.MessageProtobuf;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * <p>@ProjectName:     NettyChat</p>
 * <p>@ClassName:       TCPReadHandler.java</p>
 * <p>@PackageName:     com.freddy.im.netty</p>
 * <b>
 * <p>@Description:     消息接收处理handler</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/04/07 21:40</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public class TCPReadHandler extends SimpleChannelInboundHandler<MessageProtobuf.Msg> {

    private NettyTcpClient imsClient;

    public TCPReadHandler(NettyTcpClient imsClient) {
        this.imsClient = imsClient;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.err.println("TCPReadHandler channelInactive()");
        Channel channel = ctx.channel();
        if (channel != null) {
            System.err.println("channel id："+channel.id().asLongText());

            channel.close();
            ctx.close();
        }

        // 触发重连
        imsClient.resetConnect(false);
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.err.println("TCPReadHandler exceptionCaught()");
        Channel channel = ctx.channel();
        if (channel != null) {
            channel.close();
            ctx.close();
        }

        // 触发重连
        imsClient.resetConnect(false);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("====TCPReadHandler==xxxx==");

        MessageProtobuf.Msg message = (MessageProtobuf.Msg) msg;
        if (message == null || message.getHead() == null) {
            return;
        }

        int msgType = message.getHead().getMsgType();
        if (msgType == imsClient.getServerSentReportMsgType()) {
            int statusReport = message.getHead().getStatus();
            System.out.println(String.format("服务端状态报告：「%d」, 1代表成功，0代表失败", statusReport));
           if (statusReport == IMSConfig.DEFAULT_REPORT_SERVER_SEND_MSG_SUCCESSFUL) {
                System.out.println("收到服务端消息发送状态报告，message=" + message + "，从超时管理器移除");
                imsClient.getMsgTimeoutTimerManager().remove(message.getHead().getMsgId());
          }
        } else {
            // 其它消息
            // 收到消息后，立马给服务端回一条消息接收状态报告
            System.out.println("收到消息，message=" + message);
            MessageProtobuf.Msg receivedReportMsg = buildReceivedReportMsg(message.getHead().getMsgId());
            if(receivedReportMsg != null) {
                imsClient.sendMsg(receivedReportMsg);
            }
        }

        // 接收消息，由消息转发器转发到应用层
        imsClient.getMsgDispatcher().receivedMsg(message);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, MessageProtobuf.Msg msg) throws Exception {
    }

    /**
     * 构建客户端消息接收状态报告
     * @param msgId
     * @return
     */
    private MessageProtobuf.Msg buildReceivedReportMsg(String msgId) {


        MessageProtobuf.Msg.Builder builder = MessageProtobuf.Msg.newBuilder();
        MessageProtobuf.Head.Builder headBuilder = MessageProtobuf.Head.newBuilder();
        headBuilder.setMsgId(msgId);
        headBuilder.setMsgType(imsClient.getClientReceivedReportMsgType());
        headBuilder.setTimestamp(System.currentTimeMillis());
        builder.setHead(headBuilder.build());

        return builder.build();
    }
}
