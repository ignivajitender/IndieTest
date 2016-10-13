package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.db.BadgesDb;
import com.igniva.indiecore.model.ChatListPojo;
import com.igniva.indiecore.model.ChatPojo;
import com.igniva.indiecore.model.CollectionChatPojo;
import com.igniva.indiecore.model.CollectionPojo;
import com.igniva.indiecore.model.InstantChatPojo;
import com.igniva.indiecore.model.MessageIdPojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.ui.adapters.ChatAdapter;
import com.igniva.indiecore.ui.adapters.ChatListAdapter;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.ResultListener;
import im.delight.android.ddp.db.memory.InMemoryDatabase;

/**
 * Created by igniva-andriod-05 on 20/9/16.
 */
public class ChatActivity extends BaseActivity implements MeteorCallback {

    Toolbar mToolbar;

    public static final String URL = "ws://indiecorelive.ignivastaging.com:3000/websocket";
    public static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final String CHAT_ACTIVITY = "CHAT_ACTIVITY";
    public static final String SUBSCRIBECHATS = "subscribeChats";
    public static final String SUBSCRIBEMESSAGES = "subscribeMessages";
    public static final String GETROOMID = "getRoomId";
    public static final String MARK_MESSAGE_DELIVERED = "markMessageDelivered";
    public static final String MARK_ROOM_READ = "markRoomRead";
    public static final String MARK_MESSAGE_READ="markMessageRead";
    public static final String SENDMESSAGES = "sendMessage";
    public static final String LOG_TAG = "ChatActivity";
    private boolean IsClicked = false;
    BadgesDb dbBadges;
    SQLiteDatabase db = null;
    private Meteor mMeteor;
    private LinearLayout mLlsendMessage, mLlOpenMedia;
    private EditText mEtMessageText;
    private TextView mTitle, mTvLoadMore;
    private RecyclerView mRvChatMessages;
    private LinearLayoutManager mLlManager;
    private String USER_ID_1;
    private String TOKEN;
    static SecureRandom rnd = new SecureRandom();
    private String USER_ID_2 = "";
    private int PAGE = 1;
    private int LIMIT = 60;
    private String mRoomId = "";
    private String mUserName = "";
    private String mMessageId = "";
    private int mTotalMessages=0;
    private int mIndex = 0;
    ChatPojo mChatPojo;

    InstantChatPojo messagePojo = null;

    ChatListPojo chatListPojo=null;
    MessageIdPojo messageIDPojo = null;
    private ArrayList<ChatListPojo> chatRoomList = new ArrayList<>();
    ArrayList<ChatPojo> messageList = new ArrayList<>();
    private ChatListAdapter mChatListAdapter;
    ChatAdapter mChatAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initToolbar();
        setUpLayout();

    }

    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar_chat_activity);
            mToolbar.setNavigationIcon(R.drawable.backarrow_icon);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            TextView next = (TextView) mToolbar.findViewById(R.id.toolbar_next);
            next.setVisibility(View.GONE);
            mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        } catch (Exception e) {
            e.printStackTrace();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
    }

    @Override
    protected void setUpLayout() {
        try {
            mRoomId = getIntent().getStringExtra(Constants.ROOM_ID);
            USER_ID_2 = getIntent().getStringExtra(Constants.PERSON_ID);
            mUserName = getIntent().getStringExtra(Constants.NAME);
            mIndex = getIntent().getIntExtra(Constants.INDEX, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            mEtMessageText = (EditText) findViewById(R.id.et_message_text);
            mLlsendMessage = (LinearLayout) findViewById(R.id.ll_shoot_message);
            mLlOpenMedia = (LinearLayout) findViewById(R.id.ll_add_media);
            mRvChatMessages = (RecyclerView) findViewById(R.id.rv_chat_messages);

            mLlManager = new LinearLayoutManager(this);
            mRvChatMessages.setLayoutManager(mLlManager);
            try {
                // set a custom ScrollListner to your RecyclerView
                mRvChatMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        // Get the first visible item
                        int firstVisibleItem = mLlManager.findFirstVisibleItemPosition();
                        //Now you can use this index to manipulate your TextView
                        if (firstVisibleItem == 0 &&mTotalMessages>messageList.size()) {
                            mTvLoadMore.setVisibility(View.VISIBLE);
                        } else {
                            mTvLoadMore.setVisibility(View.GONE);
                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                if(!mRoomId.isEmpty()) {
                    dbBadges = new BadgesDb(this);
                    messageList = dbBadges.retrieveUserChat(mRoomId, this);
                    if (messageList.size() > 0) {
                        mChatAdapter = new ChatAdapter(ChatActivity.this, messageList, CHAT_ACTIVITY, mMessageId);
                        mRvChatMessages.setAdapter(mChatAdapter);
                        mRvChatMessages.smoothScrollToPosition(messageList.size() - 1);
                    } else {
                        getRecentMessages(mRoomId,PAGE,LIMIT);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            mTvLoadMore = (TextView) findViewById(R.id.tv_load_more);
            mTvLoadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PAGE=PAGE+1;
                   if(mTotalMessages-messageList.size()>60){
                       LIMIT=LIMIT+60;
                   }else {
                      LIMIT=mTotalMessages-messageList.size();
                   }
                    getRecentMessages(mRoomId,PAGE,LIMIT);

                }
            });
            mTitle.setText(mUserName);

            mLlOpenMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.showToastMessageShort(ChatActivity.this, getResources().getString(R.string.coming_soon));
                }
            });

            mLlsendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (mEtMessageText.getText().toString().isEmpty()) {
                            Utility.showAlertDialog(getResources().getString(R.string.add_message), ChatActivity.this, null);
                            return;
                        } else if (!Utility.isInternetConnection(ChatActivity.this)) {
                            new Utility().showNoInternetDialog(ChatActivity.this);
                            return;
                        } else {

                            String messageId =mRoomId+randomString();
                            IsClicked = true;
                            mMeteor.call(SENDMESSAGES, new Object[]{TOKEN, messageId, mRoomId, USER_ID_1, "text", mEtMessageText.getText().toString(), "", ""}, new ResultListener() {
                                @Override
                                public void onSuccess(String result) {
                                    mMessageId = result;
                                    android.util.Log.d(LOG_TAG, result);


                                }

                                @Override
                                public void onError(String error, String reason, String details) {
                                    android.util.Log.d(LOG_TAG, " error is " + error + " reason is " + reason + " details" + details);
                                }
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            USER_ID_1 = PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, "");
            TOKEN = PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, "");
            // enable logging of internal events for the library
            Meteor.setLoggingEnabled(true);
            // create a new instance "ws://android-ddp-meteor.meteor.com/websocket"
            mMeteor = new Meteor(this, URL, new InMemoryDatabase());
            // register the callback that will handle events and receive messages
            mMeteor.addCallback(this);
            // establish the connection
            mMeteor.connect();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String randomString() {
        Long tsLong = System.currentTimeMillis()/1000;
        return tsLong.toString();
    }

    public String createPayload(String mRoomId,int page,int limit) {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(ChatActivity.this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(ChatActivity.this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.ROOM_ID, mRoomId);
            payload.put(Constants.PAGE, page+"");
            payload.put(Constants.LIMIT, limit+"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return payload.toString();
    }

    public void getRecentMessages(String mRoomId,int page,int limit) {
        String payload = createPayload(mRoomId,page,limit);

        try {
            WebNotificationManager.registerResponseListener(responseHandler);
            WebServiceClient.get_recent_messages(this, payload, responseHandler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ResponseHandlerListener responseHandler = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            try {
                WebNotificationManager.unRegisterResponseListener(responseHandler);
                if (error == null) {
                    if (result.getSuccess().equalsIgnoreCase("true")) {
                        mTotalMessages=result.getTotalMessages();
                        // clear previous list
                        if (messageList != null) {
                            messageList.clear();
                        }
                        messageList.addAll(result.getMessagesList());
                        mChatAdapter = null;
                        if (messageList.size() > 0) {
                            try {
                                insertAllMessages(messageList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            setDataInViewObjects();
                        }
                    }
                }

                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        }
    };


    @Override
    protected void setDataInViewObjects() {

        try {
                dbBadges = new BadgesDb(this);
                messageList.clear();
                messageList = dbBadges.retrieveUserChat(mRoomId, this);

              Collections.reverse(messageList);

            if (messageList.size() > 0) {
                    mChatAdapter = new ChatAdapter(ChatActivity.this, messageList, CHAT_ACTIVITY, mMessageId);
                    mRvChatMessages.setAdapter(mChatAdapter);
                    mRvChatMessages.smoothScrollToPosition(messageList.size() - 1);
                }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

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


    @Override
    public void onConnect(boolean signedInAutomatically) {
        try {
            mMeteor.subscribe(SUBSCRIBECHATS, new Object[]{TOKEN, USER_ID_1});
            mMeteor.subscribe(SUBSCRIBEMESSAGES, new Object[]{TOKEN, USER_ID_1});
            mMeteor.call(MARK_ROOM_READ, new Object[]{TOKEN, USER_ID_1, mRoomId});

            if (mIndex == 44) {
                try {
                    mMeteor.call(GETROOMID, new Object[]{TOKEN, USER_ID_1, USER_ID_2}, new ResultListener() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(LOG_TAG, result);
                            try {
                                JSONObject json = new JSONObject(result);
                                mRoomId = json.getString("_id");
                                if (messageList.size() == 0) {
                                    getRecentMessages(mRoomId,PAGE,LIMIT);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(String error, String reason, String details) {
                            Log.d(LOG_TAG, " error is " + error + " reason is " + reason + " details" + details);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onException(Exception e) {
        Log.e(LOG_TAG, "" + e);
    }

    @Override
    public void onDataAdded(String collectionName, String documentID, String newValuesJson) {
        try {

            Gson gson = new Gson();
                messagePojo = gson.fromJson(newValuesJson, InstantChatPojo.class);
                mChatPojo = new ChatPojo();
                String date = convertDate(messagePojo.getDate_updated().get$date(), "hh:mm");
                if (mRoomId != null) {
                    if (mRoomId.equals(messagePojo.getRoomId()) && !messagePojo.getText().isEmpty()) {
                        mChatPojo.setIcon(messagePojo.getIcon());
                        mChatPojo.setName(messagePojo.getName());
                        mChatPojo.set_id(messagePojo.getUserId());
                        mChatPojo.setText(messagePojo.getText());
                        mChatPojo.setRoomId(messagePojo.getRoomId());
                        mChatPojo.setMessageId(messagePojo.getMessageId());
                        mChatPojo.setRelation(messagePojo.getRelation());
                        mChatPojo.setDate_updated(date);
                        mChatPojo.setType(messagePojo.getType());
                        messageList.add(mChatPojo);
                        insertSingleMessage(mChatPojo);
                    }
                }
                runThread();




//            try {
//                if(collectionName.equalsIgnoreCase("chat")){
//                    chatListPojo = gson.fromJson(newValuesJson, ChatListPojo.class);
//                    chatRoomList.add(chatListPojo);
//                    mChatListAdapter.notifyDataSetChanged();
//
//                    }
//            } catch (JsonSyntaxException e) {
//                e.printStackTrace();
//            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void runThread() {
        runOnUiThread(new Thread(new Runnable() {
            public void run() {
                try {
                    if (messageList.size() > 0) {
                        try {
                            mChatAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mEtMessageText.setText("");
                        mRvChatMessages.smoothScrollToPosition(messageList.size() - 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    @Override
    public void onDataChanged(String collectionName, String documentID, String updatedValuesJson, String removedValuesJson) {
        Log.e(LOG_TAG, "" + updatedValuesJson);
        Gson gson = new Gson();
        messageIDPojo = gson.fromJson(updatedValuesJson, MessageIdPojo.class);

        mMeteor.call(MARK_MESSAGE_DELIVERED, new Object[]{TOKEN, USER_ID_1, messageIDPojo.getLast_message_Id()});

        mMeteor.call(MARK_MESSAGE_READ, new Object[]{TOKEN, USER_ID_1, messageIDPojo.getLast_message_Id()});

        mMessageId = messageIDPojo.getLast_message_Id();

        try {
            runThread();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDataRemoved(String collectionName, String documentID) {
        Log.e(LOG_TAG, "" + collectionName);

    }

    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }


    public void insertAllMessages(ArrayList<ChatPojo> userMessages) {
        try{
            dbBadges = new BadgesDb(this);
            dbBadges.insertAllMessages(userMessages);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void insertSingleMessage(ChatPojo userMessages) {
        try{
            dbBadges = new BadgesDb(this);
            dbBadges.insertMessage(userMessages);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
