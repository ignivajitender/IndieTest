package com.igniva.indiecore.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.igniva.indiecore.MyApplication;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.ChatResultListener;
import com.igniva.indiecore.controller.OnChatMsgReceiveListener;
import com.igniva.indiecore.controller.OnChatMsgStatusListener;
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
import com.igniva.indiecore.utils.AsyncResultDownload;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import id.zelory.compressor.Compressor;

import static com.igniva.indiecore.controller.services.CustomMeteorService.mMeteorCommonClass;
import static com.igniva.indiecore.ui.fragments.MessagesFragment.isMessageFragmenVisible;

/**
 * Created by igniva-andriod-05 on 20/9/16.
 */
public class ChatActivity extends BaseActivity implements OnChatMsgReceiveListener, OnChatMsgStatusListener {

    Toolbar mToolbar;
    public static final String CHAT_ACTIVITY = "CHAT_ACTIVITY";
    public static final String PHOTO = "Photo";
    public static final String VIDEO = "Video";
    public static final String TEXT = "Text";
    public static final int SELECT_FILE = 500;
    public static final int REQUEST_CAMERA = 600;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 300;
    final CharSequence[] items = {"Take Photo", "Choose from Gallery","Record Video",
            "Cancel"};
    private static final int MEDIA_TYPE_VIDEO = 2;
    private Uri fileUri;
    public static final String LOG_TAG = "ChatActivity";
    private BadgesDb dbBadges;
    public static boolean isInChatActivity;
    public static String imagePath;
    private LinearLayout mLlsendMessage, mLlOpenMedia;
    private EditText mEtMessageText;
    private TextView mTitle, mTvLoadMore;
    private RecyclerView mRvChatMessages;
    private LinearLayoutManager mLlManager;
    private String USER_ID_1;
    private String TOKEN;
    private String mMediaId;
    private String USER_ID_2;
    private String mImagePath;
    private String base64Encoded;
    private String STATUS = "status";
    private String MESSAGE = "Message";
    private int NOT_SENT = 0;
    private int SENT = 1;
    private int DELIVERED = 2;
    private int READ = 3;
    private int PAGE = 1;
    private int LIMIT = 60;
    private Uri imgUri;
    private Bitmap bitmap = null;
    private File imageFile;
    private int THUMBSIZE = 70;
    private String mRoomId = "";
    private String mUserName = "";
    private String mMessageId = "";
    private String mDownloadedImagePath = null;
    private String myImage;
    private int mTotalMessages = 0;
    private int mIndex = 0;
    ChatPojo mChatPojo;
    ArrayList<ChatPojo> messageList = new ArrayList<>();
    ChatAdapter mChatAdapter = null;
    ProgressBar mProgressBar = null;
    public static String mCurrentRoomId = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initToolbar();
        setUpLayout();
        //To Make or Get Room Meteor
        makeOrGetRoomIdMeteor();
        mMeteorCommonClass.setOnChatMsgReceiveListener(this);
        mMeteorCommonClass.setOnChatMsgStatusListener(this);
    }


    private void initToolbar() {
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
                    //  finish();
                    onBackPressed();
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

    private void makeOrGetRoomIdMeteor() {
        mMeteorCommonClass.makeRoomMeteor(mRoomId);
        if (mIndex == 44) {
            mMeteorCommonClass.getRoomIdMeteor(USER_ID_2, new ChatResultListener() {
                @Override
                public void onSuccess(String result) {
                    Log.d(LOG_TAG, result);
                    try {
                        mRoomId = result;
                        if (messageList.size() == 0) {
                            loadMessages(mRoomId);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error, String reason, String details) {
                    Log.d(LOG_TAG, " error is " + error + " reason is " + reason + " details" + details);
                }
            });
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
            mLlManager.setStackFromEnd(true);
            mRvChatMessages.setLayoutManager(mLlManager);
            if (mRoomId != null) {
                loadMessages(mRoomId);
            }

            mTvLoadMore = (TextView) findViewById(R.id.tv_load_more);
            mTitle.setText(mUserName);

            mLlOpenMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage();
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

                            final String messageId = mRoomId + Utility.randomString();

                            //send Msg
                            Object[] object = new Object[]{TOKEN, messageId, mRoomId, USER_ID_1, TEXT, mEtMessageText.getText().toString(), "", ""};
                            mMeteorCommonClass.sendMsgMeteor(object, new ChatResultListener() {
                                @Override
                                public void onSuccess(String result) {
                                    mMessageId = result;
                                    long timeInMillis = System.currentTimeMillis();
                                    Calendar cal1 = Calendar.getInstance();
                                    cal1.setTimeInMillis(timeInMillis);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                    String date = dateFormat.format(cal1.getTime());
                                    ChatPojo chatPojo = new ChatPojo();
                                    chatPojo.setMessageId(messageId);
                                    chatPojo.setText(mEtMessageText.getText().toString().trim());
                                    chatPojo.setType("Text");
                                    chatPojo.setDate_updated(date);
                                    chatPojo.setRoomId(mRoomId);
                                    chatPojo.setRelation("self");
                                    chatPojo.setIcon(myImage);
                                    chatPojo.setStatus(SENT);
                                    addNewMsgToList(chatPojo);
                                    Log.d(LOG_TAG, result);
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
                }
            });

            USER_ID_1 = PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, "");
            TOKEN = PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, "");
            myImage = PreferenceHandler.readString(this, PreferenceHandler.PROFILE_PIC_URL, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * pic media from gallery
     */
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
     */
    public void cameraEvent() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recordVideo() {

        try {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            fileUri = Utility.getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



            @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
                try {
                    if (resultCode == Activity.RESULT_OK) {
                        if (requestCode == SELECT_FILE)
                            onSelectFromGalleryResult(data);
                        else if (requestCode == REQUEST_CAMERA)
                            onCaptureImageResult(data);
                        else if(requestCode ==CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE)
                            onVideoRecord(data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

    private void onVideoRecord(Intent data) {

        Utility.showToastMessageShort(this,""+data);
        Log.e("VideoPathChatActivity",""+data.getData());
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
                    bm = getImage(data);
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
                new WebServiceClientUploadImage(ChatActivity.this, asyncResult, url, reqEntity, 3, Constants.UPLOAD).execute();
            } else {
                // open dialog here
                new Utility().showNoInternetDialog((Activity) ChatActivity.this);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    AsyncResult asyncResult = new AsyncResult() {
        @Override
        public void onTaskResponse(Object result, int urlResponseNo) {
            {
                try {
                    JSONObject jsonObject = new JSONObject(result.toString());
                    JSONArray file = jsonObject.getJSONArray("files");
                    JSONObject obj = file.getJSONObject(0);
                    String mMediaPostId = obj.optString("fileId");
                    try {
                        if (!mMediaPostId.isEmpty()) {
                            final String messageId = mRoomId + Utility.randomString();
                            //send Msg
                            Object[] object = new Object[]{TOKEN, messageId, mRoomId, USER_ID_1, PHOTO, "", mMediaPostId, base64Encoded};
                            mMeteorCommonClass.sendMsgMeteor(object, new ChatResultListener() {
                                @Override
                                public void onSuccess(String result) {
                                    mMessageId = result;
                                    long timeInMillis = System.currentTimeMillis();
                                    Calendar cal1 = Calendar.getInstance();
                                    cal1.setTimeInMillis(timeInMillis);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                    String date = dateFormat.format(cal1.getTime());
                                    ChatPojo ChatPojo = new ChatPojo();
                                    ChatPojo.setIcon(myImage);
                                    ChatPojo.setUserId(USER_ID_1);
                                    ChatPojo.setText("");
                                    ChatPojo.setThumb(base64Encoded);
                                    ChatPojo.setRoomId(mRoomId);
                                    ChatPojo.setMessageId(messageId);
                                    ChatPojo.setRelation("self");
                                    ChatPojo.setDate_updated(date);
                                    ChatPojo.setStatus(SENT);
                                    ChatPojo.setImagePath(ChatActivity.imagePath);
                                    ChatPojo.setType(PHOTO);
                                    addNewMsgToList(ChatPojo);
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


    /**
     * get image bitmap from data
     */
    public Bitmap getImage(Intent data) {
        try {
            Uri capturedImageUri = data.getData();
            if (capturedImageUri != null) {
                bitmap = Utility.getBitmapFromUri(this, data.getData());
            } else {
                bitmap = (Bitmap) data.getExtras().get("data");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {

            imgUri = Utility.getImageUri(this, bitmap);
            mImagePath = Utility.getPath(ChatActivity.this, imgUri);
            imagePath = mImagePath;
            imageFile = new File(mImagePath);


            bitmap = Compressor.getDefault(this).compressToBitmap(imageFile);

//            ExifInterface ei = new ExifInterface(mImagePath);
//            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_UNDEFINED);
//
//            switch(orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                  bitmap=rotateImage(bitmap, 90);
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    bitmap=  rotateImage(bitmap, 180);
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    bitmap=rotateImage(bitmap, 270);
//                    break;
//                case ExifInterface.ORIENTATION_NORMAL:
//                    bitmap=rotateImage(bitmap, 0);
//                default:
//                    break;
//            }
            Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mImagePath),
                    THUMBSIZE, THUMBSIZE);
            base64Encoded = Utility.encodeTobase64(ThumbImage);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;

    }
//    public static Bitmap rotateImage(Bitmap source, float angle) {
//        Matrix matrix = new Matrix();
//        matrix.postRotate(angle);
//        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
//                true);
//    }

    private void loadMessages(String mRoomId) {
        try {
            if (!mRoomId.isEmpty()) {
                dbBadges = new BadgesDb(this);
                messageList = dbBadges.retrieveUserChat(mRoomId, this);
//                Collections.reverse(messageList);
                if (messageList.size() > 0) {
                    mChatAdapter = new ChatAdapter(ChatActivity.this, messageList, CHAT_ACTIVITY, mMessageId, onImageDownload, MESSAGE, -1);

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
        public void onDownloadClick(ProgressBar progressBar, int position, String mediaID, String messageId, boolean IsDownloaded) {
            try {
                if (mDownloadedImagePath == null) {
                    mMediaId = mediaID;
                    new WebServiceClientUploadImage(progressBar, ChatActivity.this, asyncResultDownload, mediaID, Constants.DOWNLOAD, 77, messageId).execute();
                    mProgressBar = progressBar;
                } else {
                    Intent intent = new Intent(ChatActivity.this, ViewMediaActivity.class);
                    intent.putExtra(Constants.MEDIA_PATH, mDownloadedImagePath);
                    startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    AsyncResultDownload asyncResultDownload = new AsyncResultDownload() {
        @Override
        public void onDownloadTaskResponse(Object result, int urlResponseNo, Object messageId, Object mediaId) {
            try {
                mProgressBar.setVisibility(View.GONE);
                mChatPojo = new ChatPojo();
                mChatPojo.setImagePath(result.toString());
                mDownloadedImagePath = result.toString();
                mChatPojo.setMessageId(messageId.toString());
                updateMediaPath(mChatPojo);
                for (int i = messageList.size() - 1; i > 0; i--) {
                    if (messageId.toString().equalsIgnoreCase(messageList.get(i).getMessageId())) {
                        messageList.get(i).setImagePath(mDownloadedImagePath);
                        if (mChatAdapter != null) {
                            mChatAdapter.notifyDataSetChanged();
                        }
                        break;
                    }
                }
                mDownloadedImagePath = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

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
        try {
            isInChatActivity = true;
            isMessageFragmenVisible = false;
            mCurrentRoomId = mRoomId;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void setDataInViewObjects() {
        try {
            dbBadges = new BadgesDb(this);
            messageList.clear();
            messageList = dbBadges.retrieveUserChat(mRoomId, this);
//            Collections.reverse(messageList);
            if (messageList.size() > 0) {
                mChatAdapter = new ChatAdapter(ChatActivity.this, messageList, CHAT_ACTIVITY, mMessageId, onImageDownload, MESSAGE, -1);
                mRvChatMessages.setAdapter(mChatAdapter);
                mRvChatMessages.smoothScrollToPosition(messageList.size() - 1);
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

    public void updateMediaPath(ChatPojo userMessages) {
        try {
            dbBadges = new BadgesDb(this);
            dbBadges.updateMediaPath(userMessages);
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
                } else if (items[item].equals("Record Video")) {
                  recordVideo();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void addNewMsgToList(ChatPojo mChatPojo1) {
        messageList.add(mChatPojo1);
        if (mChatAdapter != null) {
            mChatAdapter.notifyDataSetChanged();
            mEtMessageText.setText("");
            mRvChatMessages.smoothScrollToPosition(messageList.size() - 1);
        } else {
            mChatAdapter = new ChatAdapter(ChatActivity.this, messageList, CHAT_ACTIVITY, mMessageId, onImageDownload, MESSAGE, -1);
            mRvChatMessages.setAdapter(mChatAdapter);
        }
    }

    public void updateMessageStatus(String documentId, String methodName) {
        try {
            mChatPojo = new ChatPojo();
            for (int i = messageList.size() - 1; i > 0; i--) {
                if (documentId.equalsIgnoreCase(messageList.get(i).getMessageId())) {
                    if (methodName.equalsIgnoreCase(Constants.MARK_MESSAGE_DELIVERED)) {
                        messageList.get(i).setStatus(DELIVERED);
                        mChatPojo.setStatus(DELIVERED);
                        mChatPojo.setMessageId(documentId);
                        dbBadges.updateMessageStatus(mChatPojo);
                    } else {
                        messageList.get(i).setStatus(READ);
                        mChatPojo.setStatus(READ);
                        mChatPojo.setMessageId(documentId);
                        dbBadges.updateMessageStatus(mChatPojo);
                    }
                    if (mChatAdapter != null) {
                        mChatAdapter.notifyDataSetChanged();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        mCurrentRoomId = null;
        if (mIndex != 44) {
            isMessageFragmenVisible = true;
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
        }
        isInChatActivity = false;
        super.onBackPressed();
    }

    @Override
    public void onChatMsgRecieved(ChatPojo chatPojo) {
        addNewMsgToList(chatPojo);
    }


    @Override
    public void onChatMsgStatus(String messageId, String methodName) {
        updateMessageStatus(messageId, methodName);
    }

}
