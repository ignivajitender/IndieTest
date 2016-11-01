package com.igniva.indiecore.controller;

/**
 * Created by igniva-android-18 on 27/10/16.
 */

public interface ChatResultListener {
    void onSuccess(String result);

    void onError(String error, String reason, String details);
}
