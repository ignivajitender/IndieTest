package com.igniva.indiecore.controller;

import com.igniva.indiecore.model.ChatPojo;

/**
 * Created by igniva-android-18 on 1/11/16.
 */

public interface OnChatMsgReceiveListener {
    void onChatMsgRecieved(ChatPojo chatPojo);
}
