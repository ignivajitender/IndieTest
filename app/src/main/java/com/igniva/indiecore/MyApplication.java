package com.igniva.indiecore;

import android.app.Application;

/**
 * Created by igniva-andriod-11 on 2/6/16.
 */
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MyApplication extends Application {

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

}
