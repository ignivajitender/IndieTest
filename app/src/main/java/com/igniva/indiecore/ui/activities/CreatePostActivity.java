package com.igniva.indiecore.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.utils.AsyncResult;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.FileUtils;
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;
import com.igniva.indiecore.utils.WebServiceClientUploadImage;

import org.apache.http.entity.ContentType;
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

import id.zelory.compressor.Compressor;


/**
 * Created by igniva-andriod-05 on 18/7/16.
 */
public class CreatePostActivity extends BaseActivity implements AsyncResult, View.OnClickListener {

    private static final int MEDIA_TYPE_VIDEO = 2;
    private TextView mCamera, mGallery, mUserName, mVideoRecord;
    private EditText mPostText;
    private String mImageMediaId = "";
    private String mVideoMediaId = "";
    private String mContextName;
    private ImageView mIvMediaPost, mUserImage;
    public static final int REQUEST_CAMERA = 100;
    public static final int SELECT_FILE = 200;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 300;
    //private String videoUrl = "";
    private String fileType="";
    private Uri mVideoUri;
    private boolean IsThumbNailUploaded = false;
    private Toolbar mToolbar;
    private String mImagePath;
    private final String BUSINESS = "business";
    private final String PROFILE = "profile";
    private final String TIMELINE = "timeline";
    private Uri fileUri;
    String mBusinessId = "";
    private Bitmap mVideoThumbnail = null;
    public static final String TAG = "CreatePostActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        setUpLayout();
        initToolbar();
        setDataInViewObjects();
    }

    @Override
    protected void setUpLayout() {

        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                mBusinessId = bundle.getString(Constants.BUSINESS_ID);
                mContextName = bundle.getString(Constants.CONTEXT_NAME);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUserName = (TextView) findViewById(R.id.tv_user_name);
        mUserImage = (ImageView) findViewById(R.id.iv_user_img_create_post);

        mCamera = (TextView) findViewById(R.id.tv_camera);
        mCamera.setOnClickListener(this);
        mGallery = (TextView) findViewById(R.id.tv_upload);
        mGallery.setOnClickListener(this);
        mVideoRecord = (TextView) findViewById(R.id.tv_video);
        mVideoRecord.setOnClickListener(this);

        mIvMediaPost = (ImageView) findViewById(R.id.iv_media_post);
        mPostText = (EditText) findViewById(R.id.et_write_post);

    }

    @Override
    protected void setDataInViewObjects() {

        try {
            if (!PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_FIRST_NAME, "").isEmpty()) {
                String Name = (PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_FIRST_NAME, "") + " " + (PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_LAST_NAME, "")).charAt(0) + ".");
                mUserName.setText(Name);
            }
            if (!PreferenceHandler.readString(this, PreferenceHandler.PROFILE_PIC_URL, "").isEmpty()) {
                Glide.with(this).load(WebServiceClient.HTTP_STAGING + PreferenceHandler.readString(this, PreferenceHandler.PROFILE_PIC_URL, ""))
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mUserImage);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_camera:
                cameraEvent();
                break;
            case R.id.tv_upload:
                galleryEvent();
                break;
            case R.id.tv_video:
                recordVideo();
            default:
                break;
        }

    }


    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar_text_bothsides);
            TextView mCancel = (TextView) mToolbar.findViewById(R.id.tv_toolbar_cancel);
            mCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            TextView mTvTitle = (TextView) mToolbar.findViewById(R.id.tv_toolbar_title);
            mTvTitle.setText(getResources().getString(R.string.create_post_activity));


            TextView mPost = (TextView) mToolbar.findViewById(R.id.tv_toolbar_post);
            mPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    createPost();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
//            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
    }

    /**
     * payload to create a post
     * Room id is business room id in case of business and user id in case of time line
     *
     * @return
     * @param; token, userId, roomId, postType, media_url, text
     */
    public String createPayload() {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.ROOM_ID, mBusinessId);
            if (mContextName.equalsIgnoreCase(Constants.NEWS_FEED_ACTIVITY)) {
                payload.put(Constants.POST_TYPE, TIMELINE);
            } else if (mContextName.equalsIgnoreCase(Constants.BOARD_ACTIVITY)) {
                payload.put(Constants.POST_TYPE, BUSINESS);
            }

            if (!mPostText.getText().toString().isEmpty()) {
                payload.put(Constants.TEXT, mPostText.getText().toString());
            }
            try {
                if(!mImageMediaId.isEmpty()) {
                    if (fileType.contains("video")) {
                        payload.put(Constants.MEDIA, mVideoMediaId);
                        payload.put(Constants.THUMBNAIL, mImageMediaId);
                    } else {
                        payload.put(Constants.MEDIA, mImageMediaId);
                    }
                    if (fileType.contains("video")) {
                        payload.put(Constants.MEDIA, mVideoMediaId);
                        payload.put(Constants.THUMBNAIL, mImageMediaId);
                    } else {
                        payload.put(Constants.MEDIA, mImageMediaId);
                    }


               /* if (videoUrl.contains(".mp4")) {
                    payload.put(Constants.MEDIA, mVideoMediaId);
                    payload.put(Constants.THUMBNAIL, mImageMediaId);
                } else {
                    payload.put(Constants.MEDIA, mImageMediaId);
               }*/
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return payload.toString();
    }

    /**
     * create post
     */
    public void createPost() {
        try {
            if (!mImageMediaId.isEmpty() || !mPostText.getText().toString().isEmpty()) {
                String payload = createPayload();
                if (!payload.isEmpty()) {

                    WebNotificationManager.registerResponseListener(responseHandler);
                    WebServiceClient.create_a_post(this, payload, responseHandler);

                }
            } else {

                Utility.showAlertDialog(getResources().getString(R.string.add_post), CreatePostActivity.this, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    ResponseHandlerListener responseHandler = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            WebNotificationManager.unRegisterResponseListener(responseHandler);
            if (error == null) {
                if (result.getSuccess().equalsIgnoreCase("true")) {

                    Utility.showToastMessageLong(CreatePostActivity.this, "Posted successfully");
                    finish();

                } else {
                    Utility.showToastMessageLong(CreatePostActivity.this, getResources().getString(R.string.some_unknown_error));

                }

            } else {
                Utility.showToastMessageLong(CreatePostActivity.this, getResources().getString(R.string.some_unknown_error));
            }
            // Always close the progressdialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }


        }
    };

    /**
     * pic media from gallery
     */
    public void galleryEvent() {
        try {
            if (Build.VERSION.SDK_INT < 19) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/* video/*");
                startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_picture)), SELECT_FILE);
            } else {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
                startActivityForResult(intent, SELECT_FILE);
            }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE)
                    onSelectFromGalleryResult(data);
                else if (requestCode == REQUEST_CAMERA)
                    onCaptureImageResult(data);
                else if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE)
                    onVideoRecord(data);
            } else {
                Utility.showToastMessageShort(this, getResources().getString(R.string.operation_abort));
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    private void onVideoRecord(Intent data) {
        try {
            // mVideoUri = data.getData();
            mVideoUri = fileUri;
            String video_path = FileUtils.getPath(this, mVideoUri);
            mVideoThumbnail = ThumbnailUtils.createVideoThumbnail(video_path, MediaStore.Video.Thumbnails.MINI_KIND);
            if (mVideoThumbnail != null) {
                uploadBitmapAsMultipart(mVideoThumbnail);
                IsThumbNailUploaded = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /**
     * upload video to server
     */
    private void uploadVideoToServer(Uri uri) {
        try {
//            Charset utf8 = Charset.forName("utf-8");
            String video_path = FileUtils.getPath(this, uri);
            if (mVideoThumbnail != null) {
                mIvMediaPost.setVisibility(View.VISIBLE);
                mIvMediaPost.setImageBitmap(mVideoThumbnail);
            }

            if (Utility.isInternetConnection(CreatePostActivity.this)) {
                new WebServiceClientUploadImage(CreatePostActivity.this, this, WebServiceClient.HTTP_UPLOAD_IMAGE, video_path, 3, Constants.UPLOAD_VIDEO).execute();
            } else {
                // open dialog here
                new Utility().showNoInternetDialog((Activity) CreatePostActivity.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        mVideoUri = data.getData();
        String path = data.getData().getPath();
        if (data != null) {
            try {
//                in case of video first we have to upload its thumbnail to server
//                after that we have to start video upload
                if (mVideoUri.toString().contains("video")) {
                    String video_path = FileUtils.getPath(this, mVideoUri);
                    mVideoThumbnail = ThumbnailUtils.createVideoThumbnail(video_path, MediaStore.Video.Thumbnails.MINI_KIND);
                    if (mVideoThumbnail != null) {
                        uploadBitmapAsMultipart(mVideoThumbnail);
                        IsThumbNailUploaded = true;
                    }
                } else {
                    bm = getImage(data);
                    uploadBitmapAsMultipart(bm);
                    mIvMediaPost.setVisibility(View.VISIBLE);
                    mIvMediaPost.setImageBitmap(bm);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * get image bitmap from data
     */
    public Bitmap getImage(Intent data) {
        Uri imgUri;
        Bitmap bitmap = null;
        File imageFile;
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
            imgUri = getImageUri(this, bitmap);
            mImagePath = FileUtils.getPath(this,imgUri);
            imageFile = new File(mImagePath);
            bitmap = Compressor.getDefault(this).compressToBitmap(imageFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;

    }

    private void onCaptureImageResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = getImage(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mIvMediaPost.setVisibility(View.VISIBLE);
        uploadBitmapAsMultipart(bm);
        mIvMediaPost.setImageBitmap(bm);

    }


    private void uploadBitmapAsMultipart(Bitmap myBitmap) {
        ContentBody contentPart = null;
        try {
//            Charset utf8 = Charset.forName("utf-8");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ContentType contentType = ContentType.create(ContentType.create("Image/jpeg").getMimeType());


            if (mImagePath != null) {
                if (mImagePath.endsWith(".png")) {
                    myBitmap.compress(Bitmap.CompressFormat.PNG, 80, bos);
                    contentPart = new ByteArrayBody(bos.toByteArray(), contentType, "Image.png");
                } else {
                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                    contentPart = new ByteArrayBody(bos.toByteArray(), contentType, "Image.jpg");
                }
            } else {
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                contentPart = new ByteArrayBody(bos.toByteArray(), contentType, "Image.jpg");
            }
            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            reqEntity.addPart("fileToUpload", contentPart);

            if (Utility.isInternetConnection(CreatePostActivity.this)) {
                new WebServiceClientUploadImage(CreatePostActivity.this, this, WebServiceClient.HTTP_UPLOAD_IMAGE, reqEntity, 3, Constants.UPLOAD).execute();
            } else {
                // open dialog here
                new Utility().showNoInternetDialog((Activity) CreatePostActivity.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onTaskResponse(Object result, int urlResponseNo) {
        try {
            JSONObject jsonObject = new JSONObject(result.toString());
            Log.e("response media uplaod", jsonObject.toString());
            JSONArray file = jsonObject.getJSONArray("files");
            JSONObject obj = file.getJSONObject(0);
           // videoUrl = obj.optString("url");
            fileType=obj.optString("type");
            if(fileType.contains("video")){
                mVideoMediaId = obj.optString("fileId");
            }else{
                mImageMediaId = obj.optString("fileId");
            }

            if (IsThumbNailUploaded) {
                uploadVideoToServer(mVideoUri);
            }
            IsThumbNailUploaded = false;

            /*if (obj.optString("url").contains(".mp4")) {
                mVideoMediaId = obj.optString("fileId");
            }else{
                mImageMediaId = obj.optString("fileId");
            }

            if (IsThumbNailUploaded) {
                mIvMediaPost.setVisibility(View.VISIBLE);
                mIvMediaPost.setImageBitmap(mVideoThumbnail);
                uploadVideoToServer(mVideoUri);
            }
            IsThumbNailUploaded = false;
            }*/
            Log.e("Media Id ", "" + mImageMediaId);
        } catch (Exception e) {

        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = this.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }


}
