package com.igniva.indiecore.controller.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.igniva.indiecore.MyApplication;
import com.igniva.indiecore.model.ChatPojo;
import com.igniva.indiecore.model.InstantChatPojo;
import com.igniva.indiecore.ui.activities.ChatActivity;
import com.igniva.indiecore.ui.activities.DashBoardActivity;
import com.igniva.indiecore.utils.Constants;

import java.util.HashMap;

public class CustomReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get extra data included in the Intent
        //Please add try catch may be crash here for null instance

//        String messageid = (String) intent.getStringExtra(Constants.MESSAGE_ID);
//        String methodName=(String) intent.getStringExtra(Constants.METHOD_NAME);
//        ChatPojo chatPojo1 = (ChatPojo) intent.getParcelableExtra("chatPojo");
//
//        HashMap<String, InstantChatPojo> recentChatHashMap = (HashMap<String, InstantChatPojo>) intent.getSerializableExtra("recentChatData");
//        if (chatPojo1 != null) {
//            Context c = ((MyApplication) context.getApplicationContext()).getCurrentContext();
//            ((ChatActivity) c).addNewMsgToList(chatPojo1);
//        } else if (recentChatHashMap != null) {
//            Context c = ((MyApplication) context.getApplicationContext()).getCurrentContext();
//            ((DashBoardActivity) c).addRecentMsg(recentChatHashMap);
//        }
//        if(messageid!=null && methodName!=null){
//            Context c = ((MyApplication) context.getApplicationContext()).getCurrentContext();
//            ((ChatActivity) c).updateMessageStatus(messageid,methodName);
//        }

    }
}
