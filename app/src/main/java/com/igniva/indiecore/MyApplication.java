package com.igniva.indiecore;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.igniva.indiecore.controller.receivers.NetworkChangeReceiver;
import com.igniva.indiecore.controller.services.CustomMeteorService;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;

/**
 * Created by igniva-andriod-11 on 2/6/16.
 */

public class MyApplication extends Application implements NetworkChangeReceiver.ConnectivityReceiverListener {


    private int appRunCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        NetworkChangeReceiver.connectivityReceiverListener = this;
    }

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
                if (!Utility.isMyServiceRunning(this, CustomMeteorService.class)) {
                    Log.e("MyApplication ", "Service started");
                    Intent serviceIntent = new Intent(this, CustomMeteorService.class);
                    startService(serviceIntent);
                } else {
                    if (!CustomMeteorService.mMeteorCommonClass.isConnected()) {
                        CustomMeteorService.mMeteorCommonClass.connectMeteor();
                    }
                }
            }
        }
    }

}
