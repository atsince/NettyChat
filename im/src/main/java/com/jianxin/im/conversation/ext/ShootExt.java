package com.jianxin.im.conversation.ext;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.jianxin.chat.model.Conversation;
import com.jianxin.im.R;
import com.jianxin.im.WfcBaseActivity;
import com.jianxin.im.annotation.ExtContextMenuItem;
import com.jianxin.im.conversation.ext.core.ConversationExt;

import java.io.File;


import static android.app.Activity.RESULT_OK;

public class ShootExt extends ConversationExt {

    /**
     * @param containerView 扩展view的container
     * @param conversation
     */
    @ExtContextMenuItem(title = "拍摄")
    public void shoot(View containerView, Conversation conversation) {
        String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!((WfcBaseActivity) activity).checkPermission(permissions)) {
                activity.requestPermissions(permissions, 100);
                return;
            }
        }
//        Intent intent = new Intent(activity, TakePhotoActivity.class);
//        startActivityForResult(intent, 100);
//        TypingMessageContent content = new TypingMessageContent(TypingMessageContent.TYPING_CAMERA);
//        messageViewModel.sendMessage(conversation, content);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String path = data.getStringExtra("path");
            if (TextUtils.isEmpty(path)) {
                Toast.makeText(activity, "拍照错误, 请向我们反馈", Toast.LENGTH_SHORT).show();
                return;
            }
            if (data.getBooleanExtra("take_photo", true)) {
                //照片
               // messageViewModel.sendImgMsg(conversation, ImageUtils.genThumbImgFile(path), new File(path));
            } else {
                //小视频
              //  messageViewModel.sendVideoMsg(conversation, new File(path));
            }
        }
    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public int iconResId() {
        return R.mipmap.ic_func_shot;
    }

    @Override
    public String title(Context context) {
        return "拍摄";
    }
}
