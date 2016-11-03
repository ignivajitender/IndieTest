package com.igniva.indiecore.controller;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.gson.Gson;
import com.igniva.indiecore.controller.services.CustomMeteorService;
import com.igniva.indiecore.db.BadgesDb;
import com.igniva.indiecore.model.ChatPojo;
import com.igniva.indiecore.model.InstantChatPojo;
import com.igniva.indiecore.model.UpdateMessagePojo;
import com.igniva.indiecore.ui.activities.ChatActivity;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.PreferenceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.ResultListener;
import im.delight.android.ddp.db.memory.InMemoryDatabase;

import static com.igniva.indiecore.controller.services.CustomMeteorService.mMeteorCommonClass;
import static com.igniva.indiecore.ui.activities.ChatActivity.CHAT_ACTIVITY;
import static com.igniva.indiecore.ui.activities.ChatActivity.isInChatActivity;
import static com.igniva.indiecore.ui.activities.ChatActivity.mCurrentRoomId;
import static com.igniva.indiecore.ui.fragments.MessagesFragment.isMessageFragmenVisible;

/**
 * Created by igniva-android-18 on 26/10/16.
 */

public class MeteorCommonClass implements MeteorCallback {

    //    'subscribeMessages' - token, userId
//    [3:38:25 PM] Lancy Goyal: 'subscribeChats' - token, userId
//    [3:38:57 PM] Lancy Goyal: these are the subscribers now.
//    [3:39:35 PM] Lancy Goyal: 'sendMessage' - token, messageId, roomId, userId, type, text, media, thumb
//    [3:39:53 PM] Lancy Goyal: 'markMessageDelivered' - token, userId, messageId
//    [3:40:08 PM] Lancy Goyal: 'markMessageRead' - token, userId, messageId
//    [3:40:23 PM] Lancy Goyal: 'markRoomRead' - token, userId, roomId
//    [3:40:49 PM] Lancy Goyal: 'getRoomId' - token, userId, secondUser
//    [3:41:05 PM] Lancy Goyal: 'deactiveMyChatRoomStatus' - token, roomId, userId
//    [3:41:19 PM] Lancy Goyal: these are the methods.
//            [3:41:37 PM] Lancy Goyal: Team, could u please test.

    private Meteor mMeteor;
    public static final String LOG_TAG = "MeteorCommonClass";
    private String USER_ID_1;
    private String TOKEN;
    Context context;
    public static HashMap<String, InstantChatPojo> recentChatHashMap = new HashMap<String, InstantChatPojo>();
    private OnChatMsgReceiveListener onChatMsgReceiveListener;
    private OnRecentChatListener onRecentChatListener;
    private OnChatMsgStatusListener onChatMsgStatusListener;

    //HashMap<String, InstantChatPojo> recentChatHashMap = new HashMap<String, InstantChatPojo>();

    private MeteorCommonClass(CustomMeteorService con) {
        this.context = con;
        // enable logging of internal events for the library
        Meteor.setLoggingEnabled(true);
        USER_ID_1 = PreferenceHandler.readString(context, PreferenceHandler.PREF_KEY_USER_ID, "");
        TOKEN = PreferenceHandler.readString(context, PreferenceHandler.PREF_KEY_USER_TOKEN, "");
        // create a new instance "ws://android-ddp-meteor.meteor.com/websocket"
        mMeteor = new Meteor(context, Constants.URLWEBSOCKET, new InMemoryDatabase());
        // register the callback that will handle events and receive messages
        mMeteor.addCallback(this);
    }

    //For chatActivity for dedicated chat screen
    public void setOnChatMsgReceiveListener(OnChatMsgReceiveListener onChatMsgReceiveListener1) {
        this.onChatMsgReceiveListener = onChatMsgReceiveListener1;
    }
    //For DashBoardActivity for Recent chat screen
    public void setOnRecentChatListener(OnRecentChatListener onRecentChatListener1) {
        this.onRecentChatListener = onRecentChatListener1;
    }

    //For DashBoardActivity for Recent chat screen
    public void setOnChatMsgStatusListener(OnChatMsgStatusListener onChatMsgStatusListener1) {
        this.onChatMsgStatusListener = onChatMsgStatusListener1;
    }

    public static MeteorCommonClass getInstance(CustomMeteorService context) {

        if (mMeteorCommonClass == null) {
            mMeteorCommonClass = new MeteorCommonClass(context);
        }
        return mMeteorCommonClass;

    }

    public void connectMeteor() {
        // establish the connection
        mMeteor.connect();
    }

    public void disconnectMeteor() {
        //disconnect chat sever
        if (mMeteor != null && mMeteor.isConnected()) {
            mMeteor.disconnect();
            Log.e(LOG_TAG, "disconnectMeteor");
        }
    }

    @Override
    public void onConnect(boolean signedInAutomatically) {
        try {
            Log.e(LOG_TAG + "connection", "onConnect");
            mMeteor.subscribe(Constants.SUBSCRIBECHATS, new Object[]{TOKEN, USER_ID_1});
            mMeteor.subscribe(Constants.SUBSCRIBEMESSAGES, new Object[]{TOKEN, USER_ID_1});
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*//jatin dev
        //mMeteor.subscribe(SUBSCRIBECHATS, new Object[]{"Xbp7h9obl61hrSpLooaqsMwa3Dx1SPgIdAGTAvC59QJ", "9aASkrQE75T588Yyo"});
        //mMeteor.subscribe(SUBSCRIBEMESSAGES, new Object[]{"Xbp7h9obl61hrSpLooaqsMwa3Dx1SPgIdAGTAvC59QJ", "9aASkrQE75T588Yyo"});
        //Sidharth
        mMeteor.subscribe(SUBSCRIBECHATS, new Object[]{"qim1ubwU4cFX_dqJ88ir77OdxYN9WzsF3tuKqxQx1hE", "8zNfvFtxzDyXqjbMc"});
        mMeteor.subscribe(SUBSCRIBEMESSAGES, new Object[]{"qim1ubwU4cFX_dqJ88ir77OdxYN9WzsF3tuKqxQx1hE", "8zNfvFtxzDyXqjbMc"});
*/
    }

    @Override
    public void onDisconnect() {
        Log.e(LOG_TAG + "connection", "onDisconnect");
    }

    @Override
    public void onException(Exception e) {
        Log.e(LOG_TAG, e.getMessage());
    }

    @Override
    public void onDataAdded(String collectionName, String documentID, String newValuesJson) {
        Log.e(LOG_TAG, "onDataAdded " + collectionName + " " + documentID + " " + newValuesJson);
        Gson gson = new Gson();
        try {
            InstantChatPojo instantChatPojo = gson.fromJson(newValuesJson, InstantChatPojo.class);
            processMsg(collectionName, instantChatPojo);

           /* if (collectionName.equalsIgnoreCase("message") && !ChatActivity.isInChatActivity) {
           //send broadcast Msg
                recentChatHashMap.put(instantChatPojo.getRoomId(), instantChatPojo);
                sendMessage();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataChanged(String collectionName, String documentID, String updatedValuesJson, String removedValuesJson) {
        Log.e(LOG_TAG, "onDataChanged " + collectionName + " " + documentID + " " + updatedValuesJson + " " + removedValuesJson);
//        Gson gson = new Gson();
//        UpdateMessagePojo updateMessagePojo = gson.fromJson(updatedValuesJson, UpdateMessagePojo.class);
//        if (collectionName.equalsIgnoreCase("message")&&isInChatActivity) {
//         sendDelieverStatusChat(documentID,Constants.MARK_MESSAGE_DELIVERED);
//        }

        if (collectionName.equalsIgnoreCase("message") && isInChatActivity) {
            //sendDelieverStatusChat(documentID, Constants.MARK_MESSAGE_DELIVERED);
            onChatMsgStatusListener.onChatMsgStatus(documentID,Constants.MARK_MESSAGE_DELIVERED);
        }
    }

    @Override
    public void onDataRemoved(String collectionName, String documentID) {
        Log.e(LOG_TAG, "onDataRemoved CollectionName" + collectionName + " " + documentID);
//        if (collectionName.equalsIgnoreCase("message")&&isInChatActivity) {
//            sendDelieverStatusChat(documentID,Constants.MARK_MESSAGE_READ);
//        }
    }

    public void makeRoomMeteor(String mRoomId) {
        mMeteor.call(Constants.MARK_ROOM_READ, new Object[]{TOKEN, USER_ID_1, mRoomId});
    }

    public void getRoomIdMeteor(String USER_ID_2, final ChatResultListener listener) {
        try {
            mMeteor.call(Constants.GETROOMID, new Object[]{TOKEN, USER_ID_1, USER_ID_2}, new ResultListener() {
                @Override
                public void onSuccess(String result) {
                    Log.d(LOG_TAG, result);
                    try {
                        JSONObject json = new JSONObject(result);
                                     /*mRoomId = json.getString("_id");
                                   if (messageList.size() == 0) {
                                        loadMessages(mRoomId);
                                    }*/
                        listener.onSuccess(json.getString("_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error, String reason, String details) {
                    Log.d(LOG_TAG, " error is " + error + " reason is " + reason + " details" + details);
                    listener.onError(error, reason, details);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //To Send Msg
    public void sendMsgMeteor(Object[] object, final ChatResultListener listener) {
        mMeteor.call(Constants.SENDMESSAGES, object, new ResultListener() {
            @Override
            public void onSuccess(String result) {
                listener.onSuccess(result);
                Log.d(LOG_TAG, result);

            }

            @Override
            public void onError(String error, String reason, String details) {
                listener.onError(error, reason, details);
                Log.d(LOG_TAG, " error is " + error + " reason is " + reason + " details" + details);
            }
        });
    }

    public void msgDelieverdMeteor(String msgId) {

        mMeteor.call(Constants.MARK_MESSAGE_DELIVERED, new Object[]{TOKEN, USER_ID_1, msgId});
    }

    public void msgReadMeteor(String msgId) {
        mMeteor.call(Constants.MARK_MESSAGE_READ, new Object[]{TOKEN, USER_ID_1, msgId});
    }


    public void processMsg(String collectionName, InstantChatPojo instantChatPojo) {
        try {
            ChatPojo mChatPojo = new ChatPojo();
            long timeInMillis = System.currentTimeMillis();
            Calendar cal1 = Calendar.getInstance();
            cal1.setTimeInMillis(timeInMillis);
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            String date = dateFormat.format(cal1.getTime());
            if (collectionName.equalsIgnoreCase("message")) {
                if (instantChatPojo.getType().equalsIgnoreCase("Text")) {
                    mChatPojo.setIcon(instantChatPojo.getIcon());
                    mChatPojo.setName(instantChatPojo.getName());
                    mChatPojo.setUserId(instantChatPojo.getUserId());
                    mChatPojo.setText(instantChatPojo.getText());
                    mChatPojo.setRoomId(instantChatPojo.getRoomId());
                    mChatPojo.setMessageId(instantChatPojo.getMessageId());
                    mChatPojo.setRelation(instantChatPojo.getRelation());
                    mChatPojo.setDate_updated(date);
                    mChatPojo.setStatus(instantChatPojo.getStatus());
                    mChatPojo.setBadges(instantChatPojo.getBadges());
                    mChatPojo.setType(instantChatPojo.getType());
                    insertSingleMessage(mChatPojo);
//                    if (mCurrentRoomId != null && mCurrentRoomId.equalsIgnoreCase(instantChatPojo.getRoomId()) && isInChatActivity) {
//                        //dedicate chat is on for image send msg by broadcast
//                        sendReceivedChat(mChatPojo);
//                    }
//                    recentChatHashMap.put(instantChatPojo.getRoomId(), instantChatPojo);
//                    if (isMessageFragmenVisible && !instantChatPojo.getRelation().equalsIgnoreCase("self")) {
//                        sendReceivedRecentChat(recentChatHashMap);
//                    }
                    if (mCurrentRoomId != null && mCurrentRoomId.equalsIgnoreCase(instantChatPojo.getRoomId()) && isInChatActivity && !instantChatPojo.getRelation().equalsIgnoreCase("self")) {
                        //dedicate chat is on for text send msg by broadcast
                        //sendReceivedChat(mChatPojo);
                        onChatMsgReceiveListener.onChatMsgRecieved(mChatPojo);

                    }
                    recentChatHashMap.put(instantChatPojo.getRoomId(), instantChatPojo);
                    if (isMessageFragmenVisible && !instantChatPojo.getRelation().equalsIgnoreCase("self")) {
                        //sendReceivedRecentChat(recentChatHashMap);
                        onRecentChatListener.onRecentChat(recentChatHashMap);
                    }

                } else if (instantChatPojo.getType().equalsIgnoreCase("Photo")) {
                    mChatPojo.setIcon(instantChatPojo.getIcon());
                    mChatPojo.setName(instantChatPojo.getName());
                    mChatPojo.setUserId(instantChatPojo.getUserId());
                    mChatPojo.setMedia(instantChatPojo.getMedia());
                    mChatPojo.setText(instantChatPojo.getText());
                    mChatPojo.setThumb(instantChatPojo.getThumb());
                    mChatPojo.setRoomId(instantChatPojo.getRoomId());
                    mChatPojo.setMessageId(instantChatPojo.getMessageId());
                    mChatPojo.setRelation(instantChatPojo.getRelation());
                    mChatPojo.setDate_updated(date);
                    mChatPojo.setStatus(instantChatPojo.getStatus());
                    mChatPojo.setImagePath(ChatActivity.imagePath);
                    mChatPojo.setBadges(instantChatPojo.getBadges());
                    mChatPojo.setType(instantChatPojo.getType());
                    insertSingleMessage(mChatPojo);
                    ChatActivity.imagePath="";

//                    if (mCurrentRoomId != null && mCurrentRoomId.equalsIgnoreCase(instantChatPojo.getRoomId()) && isInChatActivity) {
//                        //dedicate chat is on for image send msg by broadcast
//                        sendReceivedChat(mChatPojo);
//                    }
//                    recentChatHashMap.put(instantChatPojo.getRoomId(), instantChatPojo);
//                    if (isMessageFragmenVisible && !instantChatPojo.getRelation().equalsIgnoreCase("self")) {
//                        sendReceivedRecentChat(recentChatHashMap);
//                    }
//                }
//
//                if (!instantChatPojo.getRelation().equalsIgnoreCase("self")) {
//                    mMeteorCommonClass.msgDelieverdMeteor(instantChatPojo.getMessageId());
//                }
//                if (isInChatActivity && !instantChatPojo.getRelation().equalsIgnoreCase("self")) {
//                    mMeteorCommonClass.msgReadMeteor(instantChatPojo.getMessageId());
//                }

                    if (mCurrentRoomId != null && mCurrentRoomId.equalsIgnoreCase(instantChatPojo.getRoomId()) && isInChatActivity && !instantChatPojo.getRelation().equalsIgnoreCase("self")) {
                        //dedicate chat is on for image send msg by broadcast
                        //sendReceivedChat(mChatPojo);
                        onChatMsgReceiveListener.onChatMsgRecieved(mChatPojo);
                    }
                    recentChatHashMap.put(instantChatPojo.getRoomId(), instantChatPojo);
                    if (isMessageFragmenVisible && !instantChatPojo.getRelation().equalsIgnoreCase("self")) {
                        //sendReceivedRecentChat(recentChatHashMap);
                        onRecentChatListener.onRecentChat(recentChatHashMap);
                    }
                }

                if (!instantChatPojo.getRelation().equalsIgnoreCase("self")) {
                    mMeteorCommonClass.msgDelieverdMeteor(instantChatPojo.getMessageId());
                }
                if (isInChatActivity && !instantChatPojo.getRelation().equalsIgnoreCase("self")) {
                    mMeteorCommonClass.msgReadMeteor(instantChatPojo.getMessageId());
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }


    public void insertAllMessages(ArrayList<ChatPojo> userMessages) {
        try {
            BadgesDb dbBadges = new BadgesDb(context);
            dbBadges.insertAllMessages(userMessages);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void insertSingleMessage(ChatPojo userMessages) {
        try {
            BadgesDb dbBadges = new BadgesDb(context);
            dbBadges.insertMessage(userMessages);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*//Send to chatpojo msg to ChatActivity (dedicated Chat)
    private void sendReceivedChat(ChatPojo chatPojo) {
        Intent intent = new Intent("MsgByMeteorCommonClass");
        // You can also include some extra data.
        intent.putExtra("chatPojo", chatPojo);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void sendReceivedRecentChat(HashMap<String, InstantChatPojo> recentChatHashMap) {
        Intent intent = new Intent("MsgByMeteorCommonClass");
        // You can also include some extra data.
        intent.putExtra("recentChatData", recentChatHashMap);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }*/

    //Send to chatpojo msg to ChatActivity (dedicated Chat)
//    private void sendReceivedChat(ChatPojo chatPojo) {
//        //Intent intent = new Intent("MsgByMeteorCommonClass");
//        Intent intent = new Intent();
//        // You can also include some extra data.
//        intent.putExtra("chatPojo", chatPojo);
//        intent.setAction("com.igniva.indiecore.mybroadcast");
//        context.sendBroadcast(intent);
//        //LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//    }
//
//    //Send to chatpojo msg to ChatActivity (dedicated Chat)
//    private void sendDelieverStatusChat(String messageId,String methodName) {
//        //Intent intent = new Intent("MsgByMeteorCommonClass");
//        Intent intent = new Intent();
//        // You can also include some extra data.
//        intent.putExtra(Constants.MESSAGE_ID,messageId);
//        intent.putExtra(Constants.METHOD_NAME,methodName);
//        intent.setAction("com.igniva.indiecore.mybroadcast");
//        context.sendBroadcast(intent);
//        //LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//    }
//
//    public void sendReceivedRecentChat(HashMap<String, InstantChatPojo> recentChatHashMap) {
//        //Intent intent = new Intent("MsgByMeteorCommonClass");
//        Intent intent = new Intent();
//        // You can also include some extra data.
//        intent.putExtra("recentChatData", recentChatHashMap);
//        intent.setAction("com.igniva.indiecore.mybroadcast");
//        context.sendBroadcast(intent);
//        //LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//    }

}