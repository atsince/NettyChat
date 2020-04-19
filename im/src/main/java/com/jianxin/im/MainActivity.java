package com.jianxin.im;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jianxin.chat.im.manager.ChatManager;
import com.jianxin.chat.model.Conversation;
import com.jianxin.im.conversation.ConversationActivity;
import com.jianxin.im.conversationlist.ConversationListActivity;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity  {

    private EditText mEditText,mFromUserId,mToUserId;
    private TextView mTextView;

    String userId = "";
    String toUserId="";
    String token = null;
//    String hosts = "[{\"host\":\"192.168.0.102\", \"port\":8855}]";
//    String hosts = "[{\"host\":\"192.168.1.26\", \"port\":8855}]";
  //  String hosts = "[{\"host\":\"192.168.30.106\", \"port\":8096}]";
    String hosts = "[{\"host\":\"39.96.8.22\", \"port\":8096}]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.jianxin.chat.R.layout.activity_main);
        mFromUserId = findViewById(com.jianxin.chat.R.id.et_fromUserId);
        mFromUserId.setText("1566727546");
        mToUserId = findViewById(com.jianxin.chat.R.id.et_toUserId);
        mToUserId.setText("1726406377");
       // 1342298467

    }






    public void connect(View view) {
        userId=mFromUserId.getText().toString();
        token="token_" + userId;
        toUserId=mToUserId.getText().toString();
        ChatManager.init(this.getApplication(),hosts);
        ChatManager.getInstance().connect(userId,token);

    }

    public void goChat(View view) {
        Intent intent=new Intent(MainActivity.this, ConversationActivity.class);
        Conversation conversation =new Conversation(Conversation.ConversationType.Single,toUserId);
        intent.putExtra("conversation",conversation);
        intent.putExtra("conversationTitle",userId+"-"+toUserId);
        startActivity(intent);

    }
    public void goList(View view) {


        Intent intent=new Intent(MainActivity.this, ConversationListActivity.class);
        startActivity(intent);
    }
}
