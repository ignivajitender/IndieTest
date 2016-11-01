package com.igniva.indiecore.controller.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.igniva.indiecore.controller.MeteorCommonClass;


public class CustomMeteorService extends Service {
    public static final String LOG_TAG = "CustomMeteorService";
    public final IBinder localBinder = new CustomMeteorService.LocalBinder();
    public static MeteorCommonClass mMeteorCommonClass;

    @Override
    public IBinder onBind(final Intent intent) {
        return localBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMeteorCommonClass = MeteorCommonClass.getInstance(this);
        mMeteorCommonClass.connectMeteor();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags,
                              final int startId) {
        return Service.START_STICKY;
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        mMeteorCommonClass.disconnectMeteor();
        super.onDestroy();
    }

    public class LocalBinder extends Binder {
        public CustomMeteorService getService() {
            return CustomMeteorService.this;
        }
    }
}
