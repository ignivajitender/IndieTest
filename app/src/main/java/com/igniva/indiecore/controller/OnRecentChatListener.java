package com.igniva.indiecore.controller;

import com.igniva.indiecore.model.ChatPojo;
import com.igniva.indiecore.model.InstantChatPojo;

import java.util.HashMap;

/**
 * Created by igniva-android-18 on 1/11/16.
 */

public interface OnRecentChatListener {
    void onRecentChat(HashMap<String, InstantChatPojo> recentChatHashMap);
}
