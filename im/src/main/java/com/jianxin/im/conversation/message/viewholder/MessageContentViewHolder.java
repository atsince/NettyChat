package com.jianxin.im.conversation.message.viewholder;

import android.view.View;
import android.widget.TextView;

import com.jianxin.chat.bean.message.Message;
import com.jianxin.im.R;
import com.jianxin.im.conversation.ConversationFragment;
import com.jianxin.im.conversation.ConversationMessageAdapter;
import com.jianxin.im.conversation.message.model.UiMessage;
import com.jianxin.im.utils.TimeUtils;
import com.jianxin.im.viewmodel.MessageViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;


public abstract class MessageContentViewHolder extends RecyclerView.ViewHolder {
    @NonNull
    protected ConversationFragment fragment;
    protected View itemView;
    protected UiMessage message;
    protected int position;
    protected RecyclerView.Adapter adapter;
    protected MessageViewModel messageViewModel;

    @BindView(R.id.timeTextView)
    TextView timeTextView;


    public MessageContentViewHolder(@NonNull ConversationFragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(itemView);
        this.fragment = fragment;
        this.itemView = itemView;
        this.adapter = adapter;
        messageViewModel = ViewModelProviders.of(fragment).get(MessageViewModel.class);
        ButterKnife.bind(this, itemView);
    }

    public void onBind(UiMessage message, int position) {
        setMessageTime(message.message, position);
    }

    /**
     * @param uiMessage
     * @param tag
     * @return 返回true，将从context menu中排除
     */

    public abstract boolean contextMenuItemFilter(UiMessage uiMessage, String tag);

    public void onViewRecycled() {
        // you can do some clean up here
    }

    protected void setMessageTime(Message item, int position) {
        long msgTime = item.timeStamp;
        if (position > 0) {
            Message preMsg = ((ConversationMessageAdapter) adapter).getItem(position - 1).message;
            long preMsgTime = preMsg.timeStamp;
            if (msgTime - preMsgTime > (5 * 60 * 1000)) {
                timeTextView.setVisibility(View.VISIBLE);
                timeTextView.setText(TimeUtils.getMsgFormatTime(msgTime));
            } else {
                timeTextView.setVisibility(View.GONE);
            }
        } else {
            timeTextView.setVisibility(View.VISIBLE);
            timeTextView.setText(TimeUtils.getMsgFormatTime(msgTime));
        }
    }

}
