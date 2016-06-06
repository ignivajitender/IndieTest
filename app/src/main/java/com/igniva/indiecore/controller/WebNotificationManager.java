package com.igniva.indiecore.controller;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;

import com.igniva.indiecore.controller.WebServiceClient.WebError;

import com.igniva.indiecore.model.ResponsePojo;

public class WebNotificationManager {

    private static final List<ResponseHandlerListener> responseHandlerListeners = new ArrayList<ResponseHandlerListener>();

    public static void onResponseCallReturned(
            ResponsePojo result, WebError error,
            ProgressDialog mProgressDialog) {
        for (int i = responseHandlerListeners.size() - 1; i >= 0; i--) {
            ResponseHandlerListener listener = responseHandlerListeners
                    .get(i);
            listener.onComplete(result, error, mProgressDialog);
        }
    }

    // register listener for handling response
    public static void registerResponseListener(ResponseHandlerListener listener) {
        responseHandlerListeners.add(listener);

    }

    // unregister listener for handling response
    public static void unRegisterResponseListener(ResponseHandlerListener listener) {
        responseHandlerListeners.remove(listener);

    }


}
