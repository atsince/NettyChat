package com.jianxin.im;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.jianxin.im.common.AppScopeViewModel;
import com.lqr.emoji.LQREmotionKit;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.multidex.MultiDexApplication;

public class AppContext extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
       ImChatApp.init(this);
    }

}
