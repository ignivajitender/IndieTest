package com.igniva.indiecore;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by igniva-andriod-11 on 2/6/16.
 */
import com.crashlytics.android.Crashlytics;
import com.igniva.indiecore.controller.receivers.NetworkChangeReceiver;
import com.igniva.indiecore.controller.services.CustomMeteorService;
import com.igniva.indiecore.utils.PreferenceHandler;

import io.fabric.sdk.android.Fabric;

public class MyApplication extends Application implements NetworkChangeReceiver.ConnectivityReceiverListener {



    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
    }

    private int appRunCount = 0;

    public int getAppRunCount() {
        return appRunCount;
    }

    public void setAppRunCount(int appRunCount) {
        this.appRunCount = appRunCount;
    }

    public void incrementAppRunCount() {
        appRunCount++;
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Log.e("MyApplication ", "connection " + isConnected);
        if (isConnected) {
            if (!PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, "").isEmpty() && !PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, "").isEmpty()) {
                if (!isMyServiceRunning(CustomMeteorService.class)) {
                    Log.e("MyApplication ", "Service started");
                    Intent serviceIntent = new Intent(this, CustomMeteorService.class);
                    startService(serviceIntent);
                }else{
                    CustomMeteorService.mMeteorCommonClass.connectMeteor();
                }
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
