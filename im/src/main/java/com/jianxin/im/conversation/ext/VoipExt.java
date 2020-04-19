package com.jianxin.im.conversation.ext;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.view.View;

import com.jianxin.chat.model.Conversation;
import com.jianxin.im.R;
import com.jianxin.im.WfcBaseActivity;
import com.jianxin.im.annotation.ExtContextMenuItem;
import com.jianxin.im.conversation.ext.core.ConversationExt;



public class VoipExt extends ConversationExt {

    @ExtContextMenuItem(title = "视频通话")
    public void video(View containerView, Conversation conversation) {
        String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!((WfcBaseActivity) activity).checkPermission(permissions)) {
                activity.requestPermissions(permissions, 100);
                return;
            }
        }
        switch (conversation.type) {
            case Single:
                videoChat(conversation.target);
                break;
            case Group:
               // ((ConversationFragment) fragment).pickGroupMemberToVoipChat(false);
                break;
            default:
                break;
        }
    }

    @ExtContextMenuItem(title = "语音通话")
    public void audio(View containerView, Conversation conversation) {
        String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!((WfcBaseActivity) activity).checkPermission(permissions)) {
                activity.requestPermissions(permissions, 100);
                return;
            }
        }
        switch (conversation.type) {
            case Single:
                audioChat(conversation.target);
                break;
            case Group:
               // ((ConversationFragment) fragment).pickGroupMemberToVoipChat(true);
                break;
            default:
                break;
        }
    }

    private void audioChat(String targetId) {
     //   WfcUIKit.singleCall(activity, targetId, true);
    }

    private void videoChat(String targetId) {
     //   WfcUIKit.singleCall(activity, targetId, false);
    }

    @Override
    public int priority() {
        return 99;
    }

    @Override
    public int iconResId() {
        return R.mipmap.ic_func_video;
    }

    @Override
    public boolean filter(Conversation conversation) {
        if (conversation.type == Conversation.ConversationType.Single ) {
            return false;
        }
        return true;
    }


    @Override
    public String title(Context context) {
        return "视频通话";
    }
}
