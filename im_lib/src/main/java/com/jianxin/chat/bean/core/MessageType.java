package com.jianxin.chat.bean.core;

public enum MessageType {

    /*
     * 握手消息
     */
    HANDSHAKE(1001),

    /*
     * 心跳消息
     */
    HEARTBEAT(1002),

    /*
     * 客户端提交的消息接收状态报告
     */
    CLIENT_MSG_RECEIVED_STATUS_REPORT(1009),

    /*
     * 服务端返回的消息发送状态报告
     */
    SERVER_MSG_SENT_STATUS_REPORT(1010),

    /**
     * 单聊消息
     */
    SINGLE_CHAT(2001),

    /**
     * 群聊消息
     */
    GROUP_CHAT(3001),


    // 聊天室
    CHAT_ROOM(4001),

    Channel(5001),
    // 聊天室
    UNKONW(6001);

    private int value;

    MessageType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MessageType type(int type) {
        MessageType messageType = null;
        switch (type) {
            case 1001:
                messageType = HANDSHAKE;
                break;
            case 1002:
                messageType = HEARTBEAT;
                break;
            case 1009:
                messageType = CLIENT_MSG_RECEIVED_STATUS_REPORT;
                break;
            case 1010:
                messageType = SERVER_MSG_SENT_STATUS_REPORT;
                break;
            case 2001:
                messageType = SINGLE_CHAT;
                break;
            case 3001:
                messageType = GROUP_CHAT;
                break;
            case 4001:
                messageType = CHAT_ROOM;
                break;
            default:
                messageType = UNKONW;
                break;
        }
        return messageType;
    }
}
