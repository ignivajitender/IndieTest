package com.igniva.indiecore;

import android.app.Application;

/**
 * Created by igniva-andriod-11 on 2/6/16.
 */
public class MyApplication extends Application {

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
