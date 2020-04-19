package com.jianxin.im.conversationlist;

import android.view.View;

import com.jianxin.chat.bean.ConnectionStatus;
import com.jianxin.chat.im.manager.ChatManager;
import com.jianxin.chat.model.Conversation;
import com.jianxin.im.AppContext;
import com.jianxin.im.R;
import com.jianxin.im.conversationlist.notification.ConnectionStatusNotification;
import com.jianxin.im.conversationlist.notification.StatusNotificationViewModel;
import com.jianxin.im.widget.ProgressFragment;

import java.util.Arrays;
import java.util.List;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;


public class ConversationListFragment extends ProgressFragment {
    private RecyclerView recyclerView;
    private ConversationListAdapter adapter;
    private static final List<Conversation.ConversationType> types = Arrays.asList(Conversation.ConversationType.Single,
        Conversation.ConversationType.Group,
        Conversation.ConversationType.Channel);
    private static final List<Integer> lines = Arrays.asList(0);

    private ConversationListViewModel conversationListViewModel;
   // private SettingViewModel settingViewModel;
    private LinearLayoutManager layoutManager;

    @Override
    protected int contentLayout() {
        return R.layout.conversationlist_frament;
    }

    @Override
    protected void afterViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        init();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (adapter != null && isVisibleToUser) {
            reloadConversations();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadConversations();
    }

    private void init() {
        adapter = new ConversationListAdapter(this);
        conversationListViewModel = ViewModelProviders
            .of(getActivity(), new ConversationListViewModelFactory(types, lines))
            .get(ConversationListViewModel.class);
        conversationListViewModel.conversationListLiveData().observe(this, conversationInfos -> {
            showContent();
            adapter.setConversationInfos(conversationInfos);
        });
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

//        UserViewModel userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
//        userViewModel.userInfoLiveData().observe(this, new Observer<List<UserInfo>>() {
//            @Override
//            public void onChanged(List<UserInfo> userInfos) {
//                int start = layoutManager.findFirstVisibleItemPosition();
//                int end = layoutManager.findLastVisibleItemPosition();
//                adapter.notifyItemRangeChanged(start, end - start + 1);
//            }
//        });
//        GroupViewModel groupViewModel = ViewModelProviders.of(this).get(GroupViewModel.class);
//        groupViewModel.groupInfoUpdateLiveData().observe(this, new Observer<List<GroupInfo>>() {
//            @Override
//            public void onChanged(List<GroupInfo> groupInfos) {
//                int start = layoutManager.findFirstVisibleItemPosition();
//                int end = layoutManager.findLastVisibleItemPosition();
//                adapter.notifyItemRangeChanged(start, end - start + 1);
//            }
//        });

        StatusNotificationViewModel statusNotificationViewModel = AppContext.getAppScopeViewModel(StatusNotificationViewModel.class);
        statusNotificationViewModel.statusNotificationLiveData().observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                adapter.updateStatusNotification(statusNotificationViewModel.getNotificationItems());
            }
        });
        conversationListViewModel.connectionStatusLiveData().observe(this, status -> {
            ConnectionStatusNotification connectionStatusNotification = new ConnectionStatusNotification();
            switch (status) {
                case ConnectionStatus.ConnectionStatusConnecting:
                    connectionStatusNotification.setValue("正在连接...");
                    statusNotificationViewModel.showStatusNotification(connectionStatusNotification);
                    break;
                case ConnectionStatus.ConnectionStatusReceiveing:
                    connectionStatusNotification.setValue("正在同步...");
                    statusNotificationViewModel.showStatusNotification(connectionStatusNotification);
                    break;
                case ConnectionStatus.ConnectionStatusConnected:
                    statusNotificationViewModel.hideStatusNotification(connectionStatusNotification);
                    break;
                case ConnectionStatus.ConnectionStatusUnconnected:
                    connectionStatusNotification.setValue("连接失败");
                    statusNotificationViewModel.showStatusNotification(connectionStatusNotification);
                    break;
                default:
                    break;
            }
        });
//        settingViewModel = new ViewModelProvider(this).get(SettingViewModel.class);
//        settingViewModel.settingUpdatedLiveData().observe(this, o -> {
//            if (ChatManager.getInstance().getConnectionStatus() == ConnectionStatus.ConnectionStatusReceiveing) {
//                return;
//            }
//            conversationListViewModel.reloadConversationList(true);
//            conversationListViewModel.reloadConversationUnreadStatus();
//
//            List<PCOnlineInfo> infos = ChatManager.getInstance().getPOnlineInfos();
//            statusNotificationViewModel.clearStatusNotificationByType(PCOnlineStatusNotification.class);
//            if (infos.size() > 0) {
//                for (PCOnlineInfo info : infos) {
//                    PCOnlineStatusNotification notification = new PCOnlineStatusNotification(info);
//                    statusNotificationViewModel.showStatusNotification(notification);
//                }
//            }
//        });
    }

    private void reloadConversations() {
        if (ChatManager.getInstance().getConnectionStatus() == ConnectionStatus.ConnectionStatusReceiveing) {
            return;
        }
        conversationListViewModel.reloadConversationList();
        conversationListViewModel.reloadConversationUnreadStatus();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
