package com.jianxin.chat.remote;

/**
 * im 进程状态监听
 */
public interface IMServiceStatusListener {
    void onServiceConnected();

    void onServiceDisconnected();
}
