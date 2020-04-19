package com.jianxin.chat.im;

import com.jianxin.chat.bean.message.Message;
import com.jianxin.chat.im.manager.ProtoDataMaganer;
import com.jianxin.chat.listener.IGetMessageCallback;
import com.jianxin.chat.model.Conversation;
import com.jianxin.chat.utils.CThreadPoolExecutor;

import androidx.lifecycle.MutableLiveData;

/**
 * <p>@ProjectName:     NettyChat</p>
 * <p>@ClassName:       MessageProcessor.java</p>
 * <p>@PackageName:     com.freddy.chat.im</p>
 * <b>
 * <p>@Description:     消息处理器</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/04/10 03:27</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public class MessageProcessor implements IMessageProcessor {
    private static final String TAG = MessageProcessor.class.getSimpleName();
    MutableLiveData<Message> receiveMessage=new MutableLiveData<>();
    private MessageProcessor() {

    }

    private static class MessageProcessorInstance {
        private static final IMessageProcessor INSTANCE = new MessageProcessor();
    }

    public MutableLiveData<Message> getReceiveMessageLiveData(){
        return receiveMessage;
    }
    public static IMessageProcessor getInstance() {
        return MessageProcessorInstance.INSTANCE;
    }

    /**
     * 接收消息
     * @param message
     */
    @Override
    public void receiveMsg(final Message message) {
      getReceiveMessageLiveData().postValue(message);
    }

    /**
     * 发送消息
     *
     * @param message
     */
    @Override
    public void sendMsg(final Message message,ISendMessageCallback listener) {
        CThreadPoolExecutor.runInBackground(new Runnable() {

            @Override
            public void run() {

                ProtoDataMaganer.getInstance().sendMessage(message,listener);
            }
        });
    }

    @Override
    public void getMessagesAsync(Conversation conversation, long fromIndex, boolean before, int count, String withUser, IGetMessageCallback callback) {
        CThreadPoolExecutor.runInBackground(new Runnable() {

            @Override
            public void run() {

                ProtoDataMaganer.getInstance().getMessagesAsync(conversation,fromIndex,before,count,withUser,callback);
            }
        });
    }


}
