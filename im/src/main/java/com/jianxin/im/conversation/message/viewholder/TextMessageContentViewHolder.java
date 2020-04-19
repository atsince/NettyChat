package com.jianxin.im.conversation.message.viewholder;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jianxin.chat.bean.message.TextMessageContent;
import com.jianxin.im.R;
import com.jianxin.im.annotation.EnableContextMenu;
import com.jianxin.im.annotation.MessageContentType;
import com.jianxin.im.annotation.MessageContextMenuItem;
import com.jianxin.im.annotation.ReceiveLayoutRes;
import com.jianxin.im.annotation.SendLayoutRes;
import com.jianxin.im.conversation.ConversationFragment;
import com.jianxin.im.conversation.message.model.UiMessage;
import com.lqr.emoji.MoonUtils;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;



@MessageContentType(value = {
        TextMessageContent.class,

})
@SendLayoutRes(resId = R.layout.conversation_item_text_send)
@ReceiveLayoutRes(resId = R.layout.conversation_item_text_receive)
@EnableContextMenu
public class TextMessageContentViewHolder extends NormalMessageContentViewHolder {
    @BindView(R.id.contentTextView)
    TextView contentTextView;

    public TextMessageContentViewHolder(ConversationFragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(fragment, adapter, itemView);
    }

    @Override
    public void onBind(UiMessage message) {
        MoonUtils.identifyFaceExpression(fragment.getContext(), contentTextView, ((TextMessageContent) message.message.content).getContent(), ImageSpan.ALIGN_BOTTOM);
//        contentTextView.setMovementMethod(new LinkTextViewMovementMethod(new LinkClickListener() {
//            @Override
//            public boolean onLinkClick(String link) {
//              //  WfcWebViewActivity.loadUrl(fragment.getContext(), "", link);
//                return true;
//            }
//        }));
    }

    @OnClick(R.id.contentTextView)
    public void onClickTest(View view) {
        Toast.makeText(fragment.getContext(), "onTextMessage click: " + ((TextMessageContent) message.message.content).getContent(), Toast.LENGTH_SHORT).show();
    }


    @MessageContextMenuItem(tag = MessageContextMenuItemTags.TAG_CLIP, title = "复制", confirm = false, priority = 12)
    public void clip(View itemView, UiMessage message) {
        ClipboardManager clipboardManager = (ClipboardManager) fragment.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager == null) {
            return;
        }
        TextMessageContent content = (TextMessageContent) message.message.content;
        ClipData clipData = ClipData.newPlainText("messageContent", content.getContent());
        clipboardManager.setPrimaryClip(clipData);
    }
}
