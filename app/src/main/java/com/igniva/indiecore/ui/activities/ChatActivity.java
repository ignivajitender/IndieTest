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
    private String CHAT_ACTIVITY="CHAT_ACTIVITY";
    private final String SUBSCRIBECHATS="subscribeChats";
    private final String SUBSCRIBEMESSAGES="subscribeMessages";
    private final String GETROOMID="getRoomId";
    private final String SENDMESSAGES="sendMessage";
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();
    private final String USER_ID_2 = "9aASkrQE75T588Yyo";
    private String ROOM_ID="";
    private String URL = "ws://indiecorelive.ignivastaging.com:3000/websocket";
    private String LOG_TAG = "ChatActivity";
    private String userId = "";
    private String PAGE="1";
    private String LIMIT="60";
    private String mRoomId ="";
    ChatPojo mChatPojo;

    MessagePojo messagePojo = null;
    ArrayList<ChatPojo> messageList=new ArrayList<>();
    ChatAdapter mChatAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initToolbar();
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
            mEtMessageText =(EditText) findViewById(R.id.et_message_text);
            btnSendMessage=(Button) findViewById(R.id.btn_shoot_message);

            mRoomId =getIntent().getStringExtra(Constants.ROOM_ID);

            mRvChatMessages=(RecyclerView) findViewById(R.id.rv_chat_messages);
            mLlManager=new LinearLayoutManager(this);
            mRvChatMessages.setLayoutManager(mLlManager);

            btnSendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (mEtMessageText.getText().toString().isEmpty()) {
                            Utility.showAlertDialog("Please write a message",ChatActivity.this);
                            return;
                        } else {
//                            sendMessage' - token, messageId, roomId, userId, type, text, media, thumb
                            String messageId = randomString(8);

                            mMeteor.call("sendMessage", new Object[] {TOKEN,messageId, mRoomId,USER_ID_1,"text",mEtMessageText.getText().toString(),"","" }, new ResultListener() {
                                @Override
                                public void onSuccess(String result) {
                                    android.util.Log.d(LOG_TAG,result);
                                }

                                @Override
                                public void onError(String error, String reason, String details) {
                                    android.util.Log.d(LOG_TAG," error is "+error+" reason is "+reason+" details"+details);
                                }
                            });
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
//
//    java.net.URL: chat/pull/rooms
//    TYPE: GET/POST
//    PARAMETER: token, userId
//    RESPONSE:  success, error, error_text, chatList, totalChats
//    STATUS: Testing
//
//    37. Recent Messages - Pull API
//    URL: chat/pull/conversation
//    TYPE: GET/POST
//    PARAMETER: token, userId, roomId, page, limit
//    RESPONSE:  success, error, error_text, messagesList, totalMessages
//    STATUS: Testing

       public String createPayload(){
           JSONObject payload =null;

           try {
//               TODO
               payload=new JSONObject();
               payload.put(Constants.TOKEN, PreferenceHandler.readString(ChatActivity.this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
               payload.put(Constants.USERID, PreferenceHandler.readString(ChatActivity.this, PreferenceHandler.PREF_KEY_USER_ID, ""));
               payload.put(Constants.ROOM_ID,mRoomId);
               payload.put(Constants.PAGE,PAGE);
               payload.put(Constants.LIMIT,LIMIT);


           }catch (Exception e){
               e.printStackTrace();
           }
           return payload.toString();

       }

    public void getRecentMessages(){
        String payload=createPayload();

        try {
            WebNotificationManager.registerResponseListener(responseHandler);
            WebServiceClient.get_recent_messages(this,payload,responseHandler);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    ResponseHandlerListener responseHandler= new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            try{
                WebNotificationManager.unRegisterResponseListener(responseHandler);
                Log.e("Recent chat Rooms","+++++++++++++"+result);
                if(error==null){
                    if(result.getSuccess().equalsIgnoreCase("true")){
                        messageList.addAll(result.getMessagesList());
                        mChatAdapter=null;
                        mChatAdapter=new ChatAdapter(ChatActivity.this,messageList,"");
                        mRvChatMessages.setAdapter(mChatAdapter);
                    }
                }
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }catch (Exception e){
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

        Log.e(LOG_TAG, "ondataadded===newValuesJson" + newValuesJson);
        Log.e(LOG_TAG, "ondataadded===collectionName" + newValuesJson);
        try {
            Gson gson = new Gson();
            messagePojo = gson.fromJson(newValuesJson, MessagePojo.class);
            mChatPojo= new ChatPojo();
            String id =messagePojo.getRoomId();
            if(mRoomId.equals(messagePojo.getRoomId())){
            mChatPojo.setIcon(messagePojo.getIcon());
            mChatPojo.setName(messagePojo.getName());
            mChatPojo.set_id(messagePojo.getUserId());
                mChatPojo.setText(messagePojo.getText());
//                mChatPojo.setText(messagePojo.getLast_message());
            mChatPojo.setRoomId(messagePojo.getRoomId());
            mChatPojo.setMessageId(messagePojo.getLast_message_Id());
            mChatPojo.setRelation(messagePojo.getRelation());
            mChatPojo.setType(messagePojo.getType());
            messageList.add(mChatPojo);
//                mChatAdapter=new ChatAdapter(ChatActivity.this,messageList,"");
//                mRvChatMessages.setAdapter(mChatAdapter);
            mChatAdapter.notifyDataSetChanged();
                mEtMessageText.setText("");

            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }


//        {"type":0,"created_at":{"$date":1473835343918},"date_updated":
//            {"$date":1473842524650},"last_message_Id":"57d9040f9d47291c010041e7",
//                "last_message_by":"J2iyEjdeP5iikLy6q","last_message_type":"Text",
//                "last_message":"Bhduehd","unreadCount":0,"roomId":"sJiMh4DSrwamit42x",
//                "userId":"ojGnuSiX5iMfTMArp","name":"sid awas","icon":null,
//                "relation":"favourite","badges":"OFF"}


        String name=messagePojo.getName();
        String text=messagePojo.getLast_message();
        String tex=messagePojo.getLast_message_by();
        String roomId=messagePojo.getRoomId();
        String userId=messagePojo.getUserId();
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
