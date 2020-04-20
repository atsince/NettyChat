package com.jianxin.im;

import android.app.Application;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.jianxin.chat.ImApp;
import com.jianxin.im.common.AppScopeViewModel;
import com.lqr.emoji.LQREmotionKit;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

public class ImChatApp {
    private static ViewModelStore viewModelStore;
    private static ViewModelProvider viewModelProvider;
    public static void init(Application mContext){
        LQREmotionKit.init(mContext, (context, path, imageView) -> Glide.with(context).load(path).apply(new RequestOptions().centerCrop().diskCacheStrategy(DiskCacheStrategy.RESOURCE).dontAnimate()).into(imageView));
        viewModelStore = new ViewModelStore();
        ViewModelProvider.Factory factory = ViewModelProvider.AndroidViewModelFactory.getInstance(mContext);
        viewModelProvider = new ViewModelProvider(viewModelStore, factory);
        ImApp.init(mContext);
    }
    /**
     * 当{@link androidx.lifecycle.ViewModel} 需要跨{@link android.app.Activity} 共享数据时使用
     */
    public static <T extends ViewModel> T getAppScopeViewModel(@NonNull Class<T> modelClass) {
        if (!AppScopeViewModel.class.isAssignableFrom(modelClass)) {
            throw new IllegalArgumentException("the model class should be subclass of AppScopeViewModel");
        }
        return viewModelProvider.get(modelClass);
    }
}
