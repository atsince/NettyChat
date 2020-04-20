package com.jianxin.im.viewmodel;

import com.jianxin.chat.im.manager.ChatManager;
import com.jianxin.chat.remote.IMServiceStatusListener;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// application scope
public class IMServiceStatusViewModel extends ViewModel implements IMServiceStatusListener {
    private MutableLiveData<Boolean> imServiceStatusLiveData = new MutableLiveData<>();

    public IMServiceStatusViewModel() {
        ChatManager.getInstance().addIMServiceStatusListener(this);
    }


    @Override
    protected void onCleared() {
        ChatManager.getInstance().removeIMServiceStatusListener(this);
    }

    public MutableLiveData<Boolean> imServiceStatusLiveData() {
        boolean binded = ChatManager.getInstance().isIMServiceConnected();
        imServiceStatusLiveData.setValue(binded);
        return imServiceStatusLiveData;
    }

    @Override
    public void onServiceConnected() {
        imServiceStatusLiveData.postValue(true);
    }

    @Override
    public void onServiceDisconnected() {
        imServiceStatusLiveData.postValue(false);
    }
}
