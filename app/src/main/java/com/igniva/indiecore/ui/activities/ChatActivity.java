package com.igniva.indiecore.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.google.gson.Gson;
import com.igniva.indiecore.R;
import com.igniva.indiecore.model.MessagePojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
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
    // Sidharth
    private String USER_ID_1;
    private String TOKEN;
    private final String SUBSCRIBECHATS="subscribeChats";
    private final String SUBSCRIBEMESSAGES="subscribeMessages";
    private final String GETROOMID="getRoomId";
    private final String SENDMESSAGES="sendMessage";
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();
    // Jatin
    private final String USER_ID_2 = "9aASkrQE75T588Yyo";
    private String ROOM_ID="";
    private String URL = "ws://indiecorelive.ignivastaging.com:3000/websocket";
    private String LOG_TAG = "ChatActivity";
    String userId = "";
    MessagePojo messagePojo = null;
    ArrayList<MessagePojo> messageList=new ArrayList<>();
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
            mEtMessageText =(EditText) findViewById(R.id.et_message_text);
            btnSendMessage=(Button) findViewById(R.id.btn_shoot_message);
            btnSendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (mEtMessageText.getText().toString().isEmpty()) {
                            Utility.showAlertDialog("Please write a message",ChatActivity.this);
                            return;
                        } else {
                            String messageId = randomString(8);
                            mMeteor.call(SENDMESSAGES, new Object[]{TOKEN, messageId, ROOM_ID, USER_ID_1, "text", mEtMessageText.getText().toString(), new ResultListener() {
                                        @Override
                                        public void onSuccess(String result) {
                                            Log.d(LOG_TAG, " error is " + result);
                                        }

                                        @Override
                                        public void onError(String error, String reason, String details) {
                                            Log.d(LOG_TAG, " error is " + error + " reason is " + reason + " details" + details);

                                        }
                                    }}
                            );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            userId = getIntent().getStringExtra(Constants.PERSON_ID);
            String tring = PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, "");
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
  private   String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    @Override
    protected void setDataInViewObjects() {

    }

    @Override
    public void onConnect(boolean signedInAutomatically) {
        try {
//            System.out.println("Connected");
//            System.out.println("Is logged in: " + mMeteor.isLoggedIn());
//            System.out.println("User ID: " + mMeteor.getUserId());
//
            if (signedInAutomatically) {
                System.out.println("Successfully logged in automatically");
            }else {
                mMeteor.subscribe(SUBSCRIBECHATS,new Object[]{TOKEN,USER_ID_1});
                 mMeteor.subscribe(SUBSCRIBEMESSAGES, new Object[]{TOKEN, USER_ID_1});
                try {
                    mMeteor.call(GETROOMID, new Object[]{TOKEN, USER_ID_1, USER_ID_2}, new ResultListener() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(LOG_TAG, result);
                            try {
                                JSONObject json= new JSONObject(result);
                                ROOM_ID=json.getString("_id");
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

        Log.e(LOG_TAG, "ondataadded" + newValuesJson);
        Gson gson = new Gson();
        messagePojo = gson.fromJson(newValuesJson, MessagePojo.class);

         String name=messagePojo.getName();
        String text=messagePojo.getLast_message();
        String tex=messagePojo.getLast_message_by();

    }

    @Override
    public void onDataChanged(String collectionName, String documentID, String updatedValuesJson, String removedValuesJson) {
        Log.e(LOG_TAG, "" + updatedValuesJson);
        Gson gson = new Gson();
        messagePojo = gson.fromJson(updatedValuesJson, MessagePojo.class);

        String name=messagePojo.getName();
        String text=messagePojo.getLast_message();
    }

    @Override
    public void onDataRemoved(String collectionName, String documentID) {
        Log.e(LOG_TAG, "" + collectionName);

    }
}
