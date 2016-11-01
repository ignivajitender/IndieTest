package com.igniva.indiecore.controller.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.igniva.indiecore.model.ChatPojo;
import com.igniva.indiecore.model.InstantChatPojo;
import com.igniva.indiecore.ui.activities.ChatActivity;
import com.igniva.indiecore.ui.activities.DashBoardActivity;

import java.util.HashMap;


/**
 * Created by igniva-android-18 on 27/10/16.
 */

public class CommonBroadCastReceiver {
    private Context context;

    public CommonBroadCastReceiver(Context context) {
        this.context = context;
    }

    public void registerReceiver() {
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver,
                new IntentFilter("MsgByMeteorCommonClass"));
    }

    public void unRegisterReceiver() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            // Get extra data included in the Intent
            //Please add try catch may be crash here for null instance
            ChatPojo chatPojo1 = (ChatPojo) intent.getParcelableExtra("chatPojo");
            HashMap<String, InstantChatPojo> recentChatHashMap = (HashMap<String, InstantChatPojo>) intent.getParcelableExtra("recentChatData");
           if (chatPojo1 != null ) {
                ((ChatActivity) context).addNewMsgToList(chatPojo1);
            } else if (recentChatHashMap != null) {
                ((DashBoardActivity) context).addRecentMsg(recentChatHashMap);
            }

        }
    };

}