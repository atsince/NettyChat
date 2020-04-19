package com.jianxin.im.conversationlist.viewholder;

import android.view.View;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.jianxin.chat.model.Conversation;
import com.jianxin.chat.model.ConversationInfo;
import com.jianxin.im.annotation.ConversationInfoType;
import com.jianxin.im.annotation.EnableContextMenu;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;


@ConversationInfoType(type = Conversation.ConversationType.Single, line = 0)
@EnableContextMenu
public class SingleConversationViewHolder extends ConversationViewHolder {
    public SingleConversationViewHolder(Fragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(fragment, adapter, itemView);
    }

    @Override
    protected void onBindConversationInfo(ConversationInfo conversationInfo) {
//        UserInfo userInfo = ChatManagerHolder.gChatManager.getUserInfo(conversationInfo.conversation.target, false);
//        UserViewModel userViewModel = ViewModelProviders.of(fragment).get(UserViewModel.class);
//        String name = userViewModel.getUserDisplayName(userInfo);
//        String portrait;
//        portrait = userInfo.portrait;
//        GlideApp
//                .with(fragment)
//                .load(portrait)
//                .placeholder(UIUtils.getRoundedDrawable(R.mipmap.avatar_def, 4))
//                .transforms(new CenterCrop(), new RoundedCorners(UIUtils.dip2Px(4)))
//                .into(portraitImageView);
//        nameTextView.setText(name);
    }

}
