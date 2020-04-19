package com.jianxin.chat.utils;

import android.content.Context;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.UUID;

public class DeviceUtil {

    public static synchronized String getDeviceId(Context context) {

        String imei = null;
        try (
                RandomAccessFile fw = new RandomAccessFile(context.getFilesDir().getAbsoluteFile() + "/.wfcClientId", "rw");
        ) {

            FileChannel chan = fw.getChannel();
            FileLock lock = chan.lock();
            imei = fw.readLine();
            if (TextUtils.isEmpty(imei)) {
                // 迁移就的clientId
                imei = PreferenceManager.getDefaultSharedPreferences(context).getString("mars_core_uid", "");
                if (TextUtils.isEmpty(imei)) {
                    try {
                        imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (TextUtils.isEmpty(imei)) {
                        imei = UUID.randomUUID().toString();
                    }
                    imei += System.currentTimeMillis();
                }
                fw.writeBytes(imei);
            }
            lock.release();
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("getClientError", "" + ex.getMessage());
        }

        return imei;
    }
}
