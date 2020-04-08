package com.freddy.chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.freddy.chat.bean.SingleMessage;
import com.freddy.chat.event.CEvent;
import com.freddy.chat.event.CEventCenter;
import com.freddy.chat.event.Events;
import com.freddy.chat.event.I_CEventListener;
import com.freddy.chat.im.IMSClientBootstrap;
import com.freddy.chat.im.MessageProcessor;
import com.freddy.chat.im.MessageType;
import com.freddy.chat.utils.CThreadPoolExecutor;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements I_CEventListener {

    private EditText mEditText,mFromUserId,mToUserId;
    private TextView mTextView;

    String userId = "1111";
    String toUserId="";
    String token = "token_" + userId;
//    String hosts = "[{\"host\":\"192.168.0.102\", \"port\":8855}]";
//    String hosts = "[{\"host\":\"192.168.1.26\", \"port\":8855}]";
  //  String hosts = "[{\"host\":\"192.168.30.106\", \"port\":8096}]";
    String hosts = "[{\"host\":\"39.96.8.22\", \"port\":8096}]";

    private static final String[] EVENTS = {
            Events.CHAT_SINGLE_MESSAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFromUserId = findViewById(R.id.et_fromUserId);
        mToUserId = findViewById(R.id.et_toUserId);
        mEditText = findViewById(R.id.et_content);
        mTextView = findViewById(R.id.tv_msg);

    }

    public void sendMsg(View view) {
        SingleMessage message = new SingleMessage();
        message.setMsgId(UUID.randomUUID().toString());
        message.setMsgType(MessageType.SINGLE_CHAT.getMsgType());
        message.setMsgContentType(MessageType.MessageContentType.TEXT.getMsgContentType());
        message.setFromId(userId);
        message.setToId(toUserId);
        message.setTimestamp(System.currentTimeMillis());
        message.setContent(mEditText.getText().toString());

        MessageProcessor.getInstance().sendMsg(message);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CEventCenter.unregisterEventListener(this, EVENTS);
    }

    @Override
    public void onCEvent(String topic, int msgCode, int resultCode, Object obj) {
        switch (topic) {
            case Events.CHAT_SINGLE_MESSAGE: {
                final SingleMessage message = (SingleMessage) obj;
                CThreadPoolExecutor.runOnMainThread(new Runnable() {

                    @Override
                    public void run() {
                        mTextView.setText(message.getContent());
                    }
                });
                break;
            }

            default:
                break;
        }
    }

    public void connect(View view) {
        userId=mFromUserId.getText().toString();
        toUserId=mToUserId.getText().toString();
        IMSClientBootstrap.getInstance().init(userId, token, hosts, 1);
        CEventCenter.registerEventListener(this, EVENTS);
    }
}
