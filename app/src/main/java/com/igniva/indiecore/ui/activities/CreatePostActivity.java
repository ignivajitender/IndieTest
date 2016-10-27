package com.igniva.indiecore.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextClock;
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
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;
import com.igniva.indiecore.utils.WebServiceClientUploadImage;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import id.zelory.compressor.Compressor;


/**
 * Created by igniva-andriod-05 on 18/7/16.
 */
public class CreatePostActivity extends BaseActivity implements AsyncResult,View.OnClickListener {

    private TextView mCamera, mGallary, mUserName;
    private EditText mPostText;
    private String mMediaPostId = "";
    private ImageView mIvMediaPost, mUserImage;
    public static final int REQUEST_CAMERA = 100;
    public static final int SELECT_FILE = 200;
    private Toolbar mToolbar;
    private String mImagePath;
    public final static String BUSINESS = "business";
    String mBusinessId="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        initToolbar();
        setUpLayout();
        setDataInViewObjects();
    }

    @Override
    protected void setUpLayout() {

        try {
            Bundle bundle = getIntent().getExtras();
            mBusinessId = bundle.getString(Constants.BUSINESS_ID);

        } catch (Exception e) {
            e.printStackTrace();
        }
        mUserName = (TextView) findViewById(R.id.tv_user_name);
        mUserImage = (ImageView) findViewById(R.id.iv_user_img_create_post);

        mCamera = (TextView) findViewById(R.id.tv_camera);
        mCamera.setOnClickListener(this);
        mGallary = (TextView) findViewById(R.id.tv_upload);
        mGallary.setOnClickListener(this);
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
     * @param;  token, userId, roomId, postType, media_url, text
     * @return
     */
    public String createPayload() {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.ROOM_ID, mBusinessId);
            payload.put(Constants.POST_TYPE, BUSINESS);

            if (!mMediaPostId.isEmpty()) {
                payload.put(Constants.MEDIA, mMediaPostId);
            }

            if (!mPostText.getText().toString().isEmpty()) {
                payload.put(Constants.TEXT,mPostText.getText().toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return payload.toString();
    }

    /**
     * create post
     *
     */
    public void createPost() {
        try {
            if (!mMediaPostId.isEmpty() || !mPostText.getText().toString().isEmpty()) {
                String payload = createPayload();
                if (!payload.isEmpty()) {

                    WebNotificationManager.registerResponseListener(responseHandler);
                    WebServiceClient.create_a_post(this, payload, responseHandler);

                }
            }else {

                Utility.showAlertDialog(getResources().getString(R.string.add_post),CreatePostActivity.this,null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    ResponseHandlerListener responseHandler = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            WebNotificationManager.unRegisterResponseListener(responseHandler);
            if (error == null) {
                if(result.getSuccess().equalsIgnoreCase("true")){

                    Utility.showToastMessageLong(CreatePostActivity.this,"Posted successfully");
                    finish();

                }else {
                    Utility.showToastMessageLong(CreatePostActivity.this,getResources().getString(R.string.some_unknown_error));

                }

            }else {
                Utility.showToastMessageLong(CreatePostActivity.this,getResources().getString(R.string.some_unknown_error));
            }
            // Always close the progressdialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }


        }
    };

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


    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = getImage(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        uploadBitmapAsMultipart(bm);
        mIvMediaPost.setVisibility(View.VISIBLE);
        mIvMediaPost.setImageBitmap(bm);

    }

    /**
    * get image bitmap from data
    * */
    public Bitmap getImage(Intent data){
        Uri imgUri;
        Bitmap bitmap = null;
        File imageFile;
        try {
            Uri capturedImageUri=data.getData();
            if(capturedImageUri!=null) {
                bitmap = Utility.getBitmapFromUri(this, data.getData());
            }else {
                bitmap= (Bitmap) data.getExtras().get("data");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
//            bitmap=getBitmapFromUri(data.getData());
            imgUri=getImageUri(this,bitmap);
            mImagePath=getPath(imgUri);
            imageFile  = new File(mImagePath);
            bitmap=Compressor.getDefault(this).compressToBitmap(imageFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return  bitmap ;

    }


    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }


    /**
     * get path from uri
     *
     * @param uri
     * @return
     */
    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
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

            if (Utility.isInternetConnection(CreatePostActivity.this)) {
                new WebServiceClientUploadImage(CreatePostActivity.this, this, WebServiceClient.HTTP_UPLOAD_IMAGE, reqEntity, 3,Constants.UPLOAD).execute();
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
            mMediaPostId = obj.optString("fileId");
            Log.e("Media Id ", "" + mMediaPostId);
        } catch (Exception e) {

        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
//
//    public String getRealPathFromURI(Uri uri) {
//        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//        cursor.moveToFirst();
//        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//        return cursor.getString(idx);
//    }


}
