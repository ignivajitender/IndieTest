package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.ChatPojo;
import com.igniva.indiecore.model.MessagePojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.ui.adapters.ChatAdapter;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.ArrayList;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.ResultListener;
import im.delight.android.ddp.db.memory.InMemoryDatabase;

/**
 * Created by igniva-andriod-05 on 20/9/16.
 */
public class ChatActivity extends BaseActivity implements MeteorCallback {

    Toolbar mToolbar;
    private Meteor mMeteor;
    private Button btnSendMessage;
    private EditText mEtMessageText;
    private RecyclerView mRvChatMessages;
    private LinearLayoutManager mLlManager;
    // Sidharth
    private String USER_ID_1;
    private String TOKEN;
    private String CHAT_ACTIVITY = "CHAT_ACTIVITY";
    private final String SUBSCRIBECHATS = "subscribeChats";
    private final String SUBSCRIBEMESSAGES = "subscribeMessages";
    private final String GETROOMID = "getRoomId";
    private final String SENDMESSAGES = "sendMessage";
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();
    private String USER_ID_2 = "";
    private String ROOM_ID = "";
    private String URL = "ws://indiecorelive.ignivastaging.com:3000/websocket";
    private String LOG_TAG = "ChatActivity";
    private String userId = "";
    private String PAGE = "1";
    private String LIMIT = "60";
    private String mRoomId = "";
    private int mIndex = 0;
    ChatPojo mChatPojo;

    MessagePojo messagePojo = null;
    ArrayList<ChatPojo> messageList = new ArrayList<>();
    ChatAdapter mChatAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initToolbar();
//         onConnect(true);
        setUpLayout();

        getRecentMessages();

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
            TextView title = (TextView) mToolbar.findViewById(R.id.toolbar_title);
            title.setText("chat");
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
            mIndex = getIntent().getIntExtra(Constants.INDEX, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mEtMessageText = (EditText) findViewById(R.id.et_message_text);
            btnSendMessage = (Button) findViewById(R.id.btn_shoot_message);
            mRvChatMessages = (RecyclerView) findViewById(R.id.rv_chat_messages);
            mLlManager = new LinearLayoutManager(this);
            mRvChatMessages.setLayoutManager(mLlManager);

            try {
                if(USER_ID_2!=null) {
                    mMeteor.call(GETROOMID, new Object[]{TOKEN, USER_ID_1, USER_ID_2}, new ResultListener() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(LOG_TAG, result);
                            try {
                                JSONObject json = new JSONObject(result);
                                mRoomId = json.getString("_id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(String error, String reason, String details) {
                            Log.d(LOG_TAG, " error is " + error + " reason is " + reason + " details" + details);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            btnSendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (mEtMessageText.getText().toString().isEmpty()) {
                            Utility.showAlertDialog("Please write a message", ChatActivity.this);
                            return;
                        } else {
                            String messageId = randomString(8);
                            mMeteor.call("sendMessage", new Object[]{TOKEN, messageId, mRoomId, USER_ID_1, "text", mEtMessageText.getText().toString(), "", ""}, new ResultListener() {
                                @Override
                                public void onSuccess(String result) {
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

    private String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public String createPayload() {
        JSONObject payload = null;

        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(ChatActivity.this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(ChatActivity.this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.ROOM_ID, mRoomId);
            payload.put(Constants.PAGE, PAGE);
            payload.put(Constants.LIMIT, LIMIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return payload.toString();

    }

    public void getRecentMessages() {
        String payload = createPayload();

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
                Log.e("Recent chat Rooms", "+++++++++++++" + result);
                if (error == null) {
                    if (result.getSuccess().equalsIgnoreCase("true")) {
                        messageList.addAll(result.getMessagesList());
                        mChatAdapter = null;
                        mChatAdapter = new ChatAdapter(ChatActivity.this, messageList, "");
                        mRvChatMessages.setAdapter(mChatAdapter);
                        mRvChatMessages.smoothScrollToPosition(messageList.size() - 1);
                    }
                }

                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void setDataInViewObjects() {

    }


    @Override
    public void onConnect(boolean signedInAutomatically) {
        try {

            mMeteor.subscribe(SUBSCRIBECHATS, new Object[]{TOKEN, USER_ID_1});
            mMeteor.subscribe(SUBSCRIBEMESSAGES, new Object[]{TOKEN, USER_ID_1});


            if (mIndex == 44) {
                try {
                    mMeteor.call(GETROOMID, new Object[]{TOKEN, USER_ID_1, USER_ID_2}, new ResultListener() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(LOG_TAG, result);
                            try {
                                JSONObject json = new JSONObject(result);
                                mRoomId = json.getString("_id");
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
            messagePojo = gson.fromJson(newValuesJson, MessagePojo.class);
            mChatPojo = new ChatPojo();
            String rd = messagePojo.getRoomId();

            if (mRoomId!=null) {
                if (mRoomId.equals(messagePojo.getRoomId())) {
                    mChatPojo.setIcon(messagePojo.getIcon());
                    mChatPojo.setName(messagePojo.getName());
                    mChatPojo.set_id(messagePojo.getUserId());
                    mChatPojo.setText(messagePojo.getText());
                    mChatPojo.setRoomId(messagePojo.getRoomId());
                    mChatPojo.setMessageId(messagePojo.getLast_message_Id());
                    mChatPojo.setRelation(messagePojo.getRelation());
                    mChatPojo.setType(messagePojo.getType());
                    messageList.add(mChatPojo);

                    runThread();

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void runThread() {
        runOnUiThread(new Thread(new Runnable() {
            public void run() {

                try {
                    mChatAdapter.notifyDataSetChanged();
                    mEtMessageText.setText("");
                    mRvChatMessages.smoothScrollToPosition(messageList.size() - 1);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
    }


    @Override
    public void onDataChanged(String collectionName, String documentID, String updatedValuesJson, String removedValuesJson) {
        Log.e(LOG_TAG, "" + updatedValuesJson);
//        Gson gson = new Gson();
//        messagePojo = gson.fromJson(updatedValuesJson, MessagePojo.class);


    }

    @Override
    public void onDataRemoved(String collectionName, String documentID) {
        Log.e(LOG_TAG, "" + collectionName);

    }
}
