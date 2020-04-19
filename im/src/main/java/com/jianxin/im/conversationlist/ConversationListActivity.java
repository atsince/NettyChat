package com.jianxin.im.conversationlist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jianxin.im.R;
import com.jianxin.im.WfcBaseActivity;

public class ConversationListActivity extends WfcBaseActivity {


    @Override
    protected int contentLayout() {
        return R.layout.fragment_container_activity;
    }

    @Override
    protected void afterViews() {
        super.afterViews();
        ConversationListFragment conversationListFragment=new ConversationListFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.containerFrameLayout, conversationListFragment, "conversationList")
                .commit();
    }
}
