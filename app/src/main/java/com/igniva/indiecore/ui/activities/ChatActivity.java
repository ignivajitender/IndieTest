package com.igniva.indiecore.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.OnImageDownloadClick;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.db.BadgesDb;
import com.igniva.indiecore.model.ChatPojo;
import com.igniva.indiecore.model.InstantChatPojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.model.UpdateMessagePojo;
import com.igniva.indiecore.ui.adapters.ChatAdapter;
import com.igniva.indiecore.utils.AsyncResult;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;
import com.igniva.indiecore.utils.WebServiceClientUploadImage;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;
import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.ResultListener;
import im.delight.android.ddp.db.memory.InMemoryDatabase;

/**
 * Created by igniva-andriod-05 on 20/9/16.
 */
public class ChatActivity extends BaseActivity implements  MeteorCallback {

    Toolbar mToolbar;
    public static final String URL = "ws://indiecorelive.ignivastaging.com:3000/websocket";
    public static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final String CHAT_ACTIVITY = "CHAT_ACTIVITY";
    public static final String SUBSCRIBECHATS = "subscribeChats";
    public static final String SUBSCRIBEMESSAGES = "subscribeMessages";
    public static final String GETROOMID = "getRoomId";
    public static final String MARK_MESSAGE_DELIVERED = "markMessageDelivered";
    public static final String MARK_ROOM_READ = "markRoomRead";
    public static final String MARK_MESSAGE_READ = "markMessageRead";
    public static final String SENDMESSAGES = "sendMessage";
    public static  final String PHOTO="Photo";
    public static  final String VIDEO="Video";
    public static  final String TEXT="Text";
    public static final int SELECT_FILE = 500;
    public static final int REQUEST_CAMERA = 600;
    final CharSequence[] items = { "Take Photo", "Choose from Gallery",
            "Cancel" };
    public static final String LOG_TAG = "ChatActivity";
    private boolean IsClicked = false;
    private BadgesDb dbBadges;
    private boolean isInFront;
    private Meteor mMeteor;
    private LinearLayout mLlsendMessage, mLlOpenMedia;
    private EditText mEtMessageText;
    private TextView mTitle, mTvLoadMore;
    private RecyclerView mRvChatMessages;
    private LinearLayoutManager mLlManager;
    private String USER_ID_1;
    private String TOKEN;
    private String USER_ID_2;
    private String mImagePath;
    private  String base64Encoded;
    private int PAGE = 1;
    private int LIMIT = 60;
    private String mRoomId = "";
    private String mUserName = "";
    private String mMessageId = "";
    private int mTotalMessages = 0;
    private int mIndex = 0;
    ChatPojo mChatPojo;
    InstantChatPojo instantChatPojo = null;
    UpdateMessagePojo updateMessagePojo = null;
    ArrayList<ChatPojo> messageList = new ArrayList<>();
    ChatAdapter mChatAdapter = null;
    ProgressBar mProgressBar=null;

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

            loadMessages(mRoomId);
//            try {
//                // set a custom ScrollListner to your RecyclerView
//                mRvChatMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                    @Override
//                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                        // Get the first visible item
//                        int firstVisibleItem = mLlManager.findFirstVisibleItemPosition();
//                        //Now you can use this index to manipulate your TextView
//                        if (firstVisibleItem == 0 && mTotalMessages > messageList.size()) {
//                            mTvLoadMore.setVisibility(View.VISIBLE);
//                        } else {
//                            mTvLoadMore.setVisibility(View.GONE);
//                        }
//
//                    }
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            mTvLoadMore = (TextView) findViewById(R.id.tv_load_more);
//            mTvLoadMore.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    PAGE = PAGE + 1;
//                    if (mTotalMessages - messageList.size() > 60) {
//                        LIMIT = LIMIT + 60;
//                    } else {
//                        LIMIT = mTotalMessages - messageList.size();
//                    }
//                    getRecentMessages(mRoomId, PAGE, LIMIT);
//
//                }
//            });
            mTitle.setText(mUserName);

            mLlOpenMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage();
//                    Utility.showToastMessageShort(ChatActivity.this, getResources().getString(R.string.coming_soon));
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

                            String messageId = mRoomId + Utility.randomString();
                            IsClicked = true;
                            mMeteor.call(SENDMESSAGES, new Object[]{TOKEN, messageId, mRoomId, USER_ID_1,TEXT, mEtMessageText.getText().toString(), "", ""}, new ResultListener() {
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


    /**
     * pic media from gallery
     *
     * */
    public void galleryEvent() {
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * click image from camera
     * */
    public void cameraEvent() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }
    private void onCaptureImageResult(Intent data) {
        Bitmap bm;
        if (data != null) {
            try {
                bm = getImage(data);
                uploadBitmapAsMultipart(bm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void onSelectFromGalleryResult(Intent data) {
        try {
            Bitmap bm;
            if (data != null) {
                try {
                    bm=getImage(data);
                    uploadBitmapAsMultipart(bm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    private void uploadBitmapAsMultipart(Bitmap myBitmap) {
        ContentBody contentPart = null;
        try {
            String url = WebServiceClient.HTTP_UPLOAD_IMAGE;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Log.e(LOG_TAG,"++++++____________++++++++++++++_____________.png"+mImagePath);
            if (mImagePath.endsWith(".png")) {
                myBitmap.compress(Bitmap.CompressFormat.PNG, 80, bos);
                contentPart = new ByteArrayBody(bos.toByteArray(), "Image.png");
            } else {
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                contentPart = new ByteArrayBody(bos.toByteArray(), "Image.jpg");
            }

            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            reqEntity.addPart("fileToUpload", contentPart);

            if (Utility.isInternetConnection(ChatActivity.this)) {
                new WebServiceClientUploadImage(ChatActivity.this, asyncResult, url, reqEntity, 3,Constants.UPLOAD).execute();
            } else {
                // open dialog here
                new Utility().showNoInternetDialog((Activity) ChatActivity.this);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    AsyncResult asyncResult=new AsyncResult() {
        @Override
        public void onTaskResponse(Object result, int urlResponseNo) {
            {
                try {
                    JSONObject jsonObject = new JSONObject(result.toString());
                    com.igniva.indiecore.utils.Log.e("response media uplaod", jsonObject.toString());
                    JSONArray file = jsonObject.getJSONArray("files");
                    JSONObject obj = file.getJSONObject(0);
                    String  mMediaPostId = obj.optString("fileId");
                    com.igniva.indiecore.utils.Log.e("Media Id ", "" + mMediaPostId);
                    Log.e(LOG_TAG,"+++++++++============"+mMediaPostId);

                    try {
                        if(!mMediaPostId.isEmpty()) {
                            String messageId = mRoomId + Utility.randomString();
                            mMeteor.call(SENDMESSAGES, new Object[]{TOKEN, messageId, mRoomId, USER_ID_1, PHOTO, "", mMediaPostId,base64Encoded}, new ResultListener() {
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
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } catch (Exception e) {

                }
            }
        }
    };




    /**
     * get image bitmap from data
     * */
    public Bitmap getImage(Intent data){
        Uri imgUri;
        Bitmap bitmap = null;
        File imageFile;
        int THUMBSIZE=70;
        try {
            bitmap=Utility.getBitmapFromUri(this,data.getData());
            imgUri=Utility.getImageUri(this,bitmap);
            mImagePath=Utility.getPath(ChatActivity.this,imgUri);
            imageFile  = new File(mImagePath);
            bitmap=Compressor.getDefault(this).compressToBitmap(imageFile);
            Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mImagePath),
                    THUMBSIZE, THUMBSIZE);
            base64Encoded =Utility.encodeTobase64(ThumbImage);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return  bitmap ;

    }
//    public Uri getImageUri(Context inContext, Bitmap inImage) {
//
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
//        return Uri.parse(path);
//    }
//
//
//    public static String encodeTobase64(Bitmap image)
//    {
//        Bitmap immagex=image;
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        immagex.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        byte[] b = baos.toByteArray();
//        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
//        return imageEncoded;
//    }
//
//    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
//        ParcelFileDescriptor parcelFileDescriptor =
//                getContentResolver().openFileDescriptor(uri, "r");
//        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
//        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
//        parcelFileDescriptor.close();
//        return image;
//    }
//
//    /**
//     * get path from uri
//     *
//     * @param uri
//     * @return
//     */
//    public String getPath(Uri uri)
//    {
//        String[] projection = { MediaStore.Images.Media.DATA };
//        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//        if (cursor == null) return null;
//        int column_index =cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        String s=cursor.getString(column_index);
//        cursor.close();
//        return s;
//    }

    private void loadMessages(String mRoomId) {
        try {
            if (!mRoomId.isEmpty()) {
                dbBadges = new BadgesDb(this);
                messageList = dbBadges.retrieveUserChat(mRoomId, this);
//                Collections.reverse(messageList);
                if (messageList.size() > 0) {
                    mChatAdapter = new ChatAdapter(ChatActivity.this, messageList, CHAT_ACTIVITY, mMessageId, onImageDownload);
                    mRvChatMessages.setAdapter(mChatAdapter);
                    mRvChatMessages.smoothScrollToPosition(messageList.size() - 1);
                } else {
                    getRecentMessages(mRoomId, PAGE, LIMIT);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    OnImageDownloadClick onImageDownload = new OnImageDownloadClick() {
        @Override
        public void onDownloadClick(ProgressBar progressBar, int position, String mediaID) {
            try {
                new WebServiceClientUploadImage(progressBar,ChatActivity.this,asyncResult2 ,mediaID,Constants.DOWNLOAD,3).execute();
                     mProgressBar=progressBar;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    AsyncResult asyncResult2=new AsyncResult() {
        @Override
        public void onTaskResponse(Object result, int urlResponseNo) {
             mProgressBar.setVisibility(View.GONE);
            Log.e("+++++++++++++ondownlaodimageActivity",""+result);
        }
    } ;


    public String createPayload(String mRoomId, int page, int limit) {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(ChatActivity.this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(ChatActivity.this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.ROOM_ID, mRoomId);
            payload.put(Constants.PAGE, page + "");
            payload.put(Constants.LIMIT, limit + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return payload.toString();
    }

    public void getRecentMessages(String mRoomId, int page, int limit) {
        String payload = createPayload(mRoomId, page, limit);

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
                        mTotalMessages = result.getTotalMessages();
                        // clear previous list
                        if (messageList != null) {
                            messageList.clear();
                        }
                        messageList.addAll(result.getMessagesList());
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
    public void onResume() {
        super.onResume();
        isInFront = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isInFront = false;
    }

    @Override
    protected void setDataInViewObjects() {

        try {
            dbBadges = new BadgesDb(this);
            messageList.clear();
            messageList = dbBadges.retrieveUserChat(mRoomId, this);
//            Collections.reverse(messageList);
            if (messageList.size() > 0) {
                mChatAdapter = new ChatAdapter(ChatActivity.this, messageList, CHAT_ACTIVITY, mMessageId, onImageDownload);
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
                                    loadMessages(mRoomId);
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
        Gson gson = new Gson();
        Log.e(LOG_TAG, "ondataadded+++collectionName" + collectionName);
        Log.e(LOG_TAG, "ondataadded+++documentID" + documentID);
        Log.e(LOG_TAG, "ondataadded+++newValuesJson" + newValuesJson);
        try {
            instantChatPojo = gson.fromJson(newValuesJson, InstantChatPojo.class);
            mChatPojo = new ChatPojo();
//            TODO use UTC time converter
            String date = convertDate(instantChatPojo.getDate_updated().get$date(), "hh:mm");
            if (mRoomId != null && collectionName.equalsIgnoreCase("message")) {
                if (mRoomId.equals(instantChatPojo.getRoomId())) {
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

//                        for(int i=0;i<messageList.size();i++){
//                            if(!messageList.get(i).getMessageId().contains(instantChatPojo.getMessageId())){
//                                messageList.add(mChatPojo);
//                            }
//                        }
                        if (IsClicked) {
                            messageList.add(mChatPojo);
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
                        mChatPojo.setBadges(instantChatPojo.getBadges());
                        mChatPojo.setType(instantChatPojo.getType());
                        mChatPojo.setImagePath(mImagePath);
                        insertSingleMessage(mChatPojo);

//                        for(int i=0;i<messageList.size();i++){
//                            if(!messageList.get(i).getMessageId().contains(instantChatPojo.getMessageId())){
//                                messageList.add(mChatPojo);
//                            }
//                        }

                        if (IsClicked) {
                            messageList.add(mChatPojo);
                        }
                    }
                } else {
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
                        mChatPojo.setBadges(instantChatPojo.getBadges());
                        mChatPojo.setType(instantChatPojo.getType());
                        insertSingleMessage(mChatPojo);
                    }
                }
            }

//            if (instantChatPojo.getStatus() == 1) {
//
//                for(int i=0;i<messageList.size();i++){
//                    if(messageList.get(i).getMessageId().equals(mChatPojo.getMessageId())){
//                        messageList.get(i).setStatus(1);
//                        dbBadges.updateChatRow(mChatPojo);
//                    }
//                }
//                mChatAdapter.notifyDataSetChanged();
//            }
//            if (instantChatPojo.getStatus() == 2) {
//                for(int i=0;i<messageList.size();i++){
//                    if(messageList.get(i).getMessageId().equals(mChatPojo.getMessageId())){
//                        messageList.get(i).setStatus(2);
//                        dbBadges.updateChatRow(mChatPojo);
//
//                    }
//                }
//                mChatAdapter.notifyDataSetChanged();
//            }
            runThread();
            try {
                if (!instantChatPojo.getRelation().equalsIgnoreCase("self")) {
                    mMeteor.call(MARK_MESSAGE_DELIVERED, new Object[]{TOKEN, USER_ID_1, instantChatPojo.getMessageId()});
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (isInFront && !instantChatPojo.getRelation().equalsIgnoreCase("self")) {
                    mMeteor.call(MARK_MESSAGE_READ, new Object[]{TOKEN, USER_ID_1, instantChatPojo.getMessageId()});
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

//        try{
//                messagePojo = gson.fromJson(newValuesJson, MessagePojo.class);
//                mChatPojo = new ChatPojo();
//                String date = convertDate(messagePojo.getDate_updated().get$date(), "hh:mm");
//                if (messagePojo.getRoomId() != null) {
//                    mChatPojo.setIcon(messagePojo.getIcon());
//                    mChatPojo.setName(messagePojo.getName());
//                    mChatPojo.set_id(messagePojo.getUserId());
//                    mChatPojo.setText(messagePojo.getLast_message());
//                    mChatPojo.setRoomId(messagePojo.getRoomId());
//                    mChatPojo.setMessageId(messagePojo.getLast_message_Id());
//                    mChatPojo.setRelation(messagePojo.getRelation());
//                    mChatPojo.setDate_updated(date);
//                    mChatPojo.setType(messagePojo.getType());
//                    insertSingleMessage(mChatPojo);
//                }
//        }catch (Exception e){
//            e.printStackTrace();
//        }


    }

    private void runThread() {
        runOnUiThread(new Thread(new Runnable() {
            public void run() {
                try {
                    if (messageList.size() > 0) {
                        try {

                            if (mChatAdapter != null) {
                                mChatAdapter.notifyDataSetChanged();
                            } else {
                                mChatAdapter = new ChatAdapter(ChatActivity.this, messageList, CHAT_ACTIVITY, null, onImageDownload);
                                mRvChatMessages.setAdapter(mChatAdapter);
                            }

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
        Log.e(LOG_TAG, "ondatachnaged+++" + collectionName);
        Log.e(LOG_TAG, "ondatachnaged+++" + documentID);
        Log.e(LOG_TAG, "ondatachnaged+++" + updatedValuesJson);
        Log.e(LOG_TAG, "ondatachnaged----" + removedValuesJson);
        try {
            Gson gson = new Gson();
            updateMessagePojo = gson.fromJson(updatedValuesJson, UpdateMessagePojo.class);

//            mMessageId = updateMessagePojo.getLast_message_Id();
//            if( mMessageId!=null) {
//                for (int i = 0; i < messageList.size(); i++) {
//                    if (mMessageId.equals(messageList.get(i).getMessageId())) {
//                        messageList.get(i).setStatus(2);
//                        mChatPojo.setStatus(2);
//                        dbBadges.updateChatRow(mChatPojo);
//
//                    }
//                }
//                mChatAdapter.notifyDataSetChanged();
//
//            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataRemoved(String collectionName, String documentID) {
        Log.e(LOG_TAG, "onDataRemoved++" + collectionName);
        Log.e(LOG_TAG, "documentID++" + documentID);

    }

    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }


    public void insertAllMessages(ArrayList<ChatPojo> userMessages) {
        try {
            dbBadges = new BadgesDb(this);
            dbBadges.insertAllMessages(userMessages);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void insertSingleMessage(ChatPojo userMessages) {
        try {
            dbBadges = new BadgesDb(this);
            dbBadges.insertMessage(userMessages);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void selectImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
//                boolean result=Utility.checkPermission(MainActivity.this);
                if (items[item].equals("Take Photo")) {
                        cameraEvent();
                } else if (items[item].equals("Choose from Gallery")) {
                        galleryEvent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

}
