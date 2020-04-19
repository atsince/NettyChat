package com.jianxin.im.conversationlist.viewholder;

import android.view.View;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.jianxin.chat.model.Conversation;
import com.jianxin.chat.model.ConversationInfo;
import com.jianxin.im.R;
import com.jianxin.im.annotation.ConversationInfoType;
import com.jianxin.im.annotation.EnableContextMenu;
import com.jianxin.im.utils.UIUtils;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


@ConversationInfoType(type = Conversation.ConversationType.Single, line = 0)
@EnableContextMenu
public class UnknownConversationViewHolder extends ConversationViewHolder {
    public UnknownConversationViewHolder(Fragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(fragment, adapter, itemView);
    }

    @Override
    protected void onBindConversationInfo(ConversationInfo conversationInfo) {
//        GlideApp
//                .with(fragment)
//                .load(UIUtils.getRoundedDrawable(R.mipmap.avatar_def, 4))
//                .transforms(new CenterCrop(), new RoundedCorners(UIUtils.dip2Px(4)))
//                .into(portraitImageView);
        nameTextView.setText("未知会话类型(" + conversationInfo.conversation.type.getValue() + ")或线路(" + conversationInfo.conversation.line + ")");
    }

}
