package com.jianxin.im.netty.handler;

import com.jianxin.im.netty.NettyTcpClient;
import com.jianxin.im.netty.handler.HeartbeatRespHandler;
import com.jianxin.im.netty.handler.LoginAuthRespHandler;
import com.jianxin.im.netty.handler.TCPReadHandler;
import com.jianxin.im.protobuf.MessageProtobuf;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * <p>@ProjectName:     NettyChat</p>
 * <p>@ClassName:       TCPChannelInitializerHandler.java</p>
 * <p>@PackageName:     com.freddy.im.netty</p>
 * <b>
 * <p>@Description:     Channel初始化配置</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/04/05 07:11</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public class TCPChannelInitializerHandler extends ChannelInitializer<SocketChannel> {

    private NettyTcpClient imsClient;

    public TCPChannelInitializerHandler(NettyTcpClient imsClient) {
        this.imsClient = imsClient;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();


        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        pipeline.addLast(new ProtobufDecoder(MessageProtobuf.Msg.getDefaultInstance()));
        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast(new ProtobufEncoder());


        pipeline.addLast(LoginAuthRespHandler.class.getSimpleName(), new LoginAuthRespHandler(imsClient));
        // 心跳消息响应处理handler
        pipeline.addLast(HeartbeatRespHandler.class.getSimpleName(), new HeartbeatRespHandler(imsClient));
        // 接收消息处理handler
        pipeline.addLast(TCPReadHandler.class.getSimpleName(), new TCPReadHandler(imsClient));
    }
}
