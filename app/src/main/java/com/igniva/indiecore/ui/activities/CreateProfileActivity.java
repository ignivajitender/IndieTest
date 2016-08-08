package com.igniva.indiecore.ui.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by siddharth-05 on 3/6/16.
 */
public class CreateProfileActivity extends BaseActivity implements AsyncResult,View.OnClickListener {

    Toolbar mToolbar;
    private DatePicker datePicker;
    private Calendar calendar;
    private ImageView mImCameraProfilebtn, mImCameraCoverbtn;
    private ImageView mIvProfileImage, mIvCoverImage;
    private TextView mTvDateOfBirth, mTvMale, mTvFemale, mTvOther;
    private EditText mEtFirstName, mEtLastName, mEtDescription;
    private int mYear, mMonth, mDay, mCurrentYear;
    public static final int REQUEST_CAMERA = 100;
    public static final int SELECT_FILE = 200;
    private String userChosenTask = "";
    private String mCountryCode;
    private int PIC_INDEX_CODE = 0;
    private int numberLenth;
    int index = -1;
    Calendar cal;
    String profileImageUrl = "";
    String CoverImageUrl = "";
    String gender = "male";
    String LOG_TAG = "CreateProfileActivity";
    CreateProfileActivity mCreateProfile;
    String firstName="";
    String lastName="";
    String dob="";
    String description="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        setUpLayout();
        initToolbar();

        setDataInViewObjects();
    }

    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            TextView mTvTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
            TextView mTvNext = (TextView) mToolbar.findViewById(R.id.toolbar_next);

            if (index == 1) {
                mTvTitle.setText(getResources().getString(R.string.create_profile));
            } else {
                mTvTitle.setText(getResources().getString(R.string.update_profile));
                mTvNext.setText("Update");
            }


            mTvNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    validateAndPostData();
                }
            });

            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }

    }

    public void getDateOfBirth() {

        try {
            mDay = cal.get(Calendar.DAY_OF_MONTH);
            mMonth = cal.get(Calendar.MONTH);
            mYear = cal.get(Calendar.YEAR);
            DateDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void setUpLayout() {

        cal = Calendar.getInstance();
        try {
            mEtFirstName = (EditText) findViewById(R.id.et_first_name);
            mEtLastName = (EditText) findViewById(R.id.et_last_name);
            mEtDescription = (EditText) findViewById(R.id.et_description);
            mTvDateOfBirth = (TextView) findViewById(R.id.tv_dob);
            mIvProfileImage = (ImageView) findViewById(R.id.iv_profile);
            mIvCoverImage = (ImageView) findViewById(R.id.iv_cover_pic);
            mTvMale = (TextView) findViewById(R.id.textView);
            mTvFemale = (TextView) findViewById(R.id.tv_female);
            mTvOther = (TextView) findViewById(R.id.tv_other);
            mTvMale.setBackgroundResource(R.drawable.left_rounded_corner_selected);
            mTvMale.setTextColor(Color.parseColor("#ffffff"));

            mImCameraCoverbtn=(ImageView) findViewById(R.id.iv_camerabtn_coverpic);
            mImCameraCoverbtn.setOnClickListener(this);


            mImCameraProfilebtn=(ImageView) findViewById(R.id.iv_camerabtn_profile_pic);
            mImCameraProfilebtn.setOnClickListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Bundle bundle = getIntent().getExtras();
            index = bundle.getInt(Constants.INDEX);
            numberLenth = bundle.getInt(Constants.NUMBER_LENGTH);
            mCountryCode = bundle.getString(Constants.COUNTRY_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void DateDialog() {

        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int month = monthOfYear + 1;
                mTvDateOfBirth.setText(month + "/" + dayOfMonth + "/" + year);
                mYear = year;
            }
        };

        DatePickerDialog dpDialog = new DatePickerDialog(this, listener, mYear, mMonth, mDay);
        dpDialog.show();

    }


    private void selectImage() {
        try {
            final CharSequence[] items = {getResources().getString(R.string.take_photo), getResources().getString(R.string.choose_from_gallary)
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateProfileActivity.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(getResources().getString(R.string.upload_image));
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    boolean result = true;
                    if (items[item].equals(getResources().getString(R.string.take_photo))) {
                        userChosenTask = getResources().getString(R.string.take_photo);
                        if (result)
                            cameraIntent();
                    } else if (items[item].equals(getResources().getString(R.string.choose_from_gallary))) {
                        userChosenTask = getResources().getString(R.string.choose_from_gallary);
                        if (result)
                            galleryIntent();
                    }
                }

            });

            builder.setPositiveButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cameraIntent() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void galleryIntent() {
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChosenTask.equals(getResources().getString(R.string.take_photo)))
                        cameraIntent();
                    else if (userChosenTask.equals(getResources().getString(R.string.choose_from_gallary)))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
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

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (PIC_INDEX_CODE == 2) {
            uploadBitmapAsMultipart(bm);
            mIvCoverImage.setImageBitmap(bm);
        } else {
            uploadBitmapAsMultipart(bm);
            mIvProfileImage.setImageBitmap(bm);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (PIC_INDEX_CODE == 2) {

            uploadBitmapAsMultipart(thumbnail);
            mIvCoverImage.setImageBitmap(thumbnail);

        } else {
            uploadBitmapAsMultipart(thumbnail);
            mIvProfileImage.setImageBitmap(thumbnail);
        }
    }


    public void validateAndPostData() {

        try {
             firstName = mEtFirstName.getText().toString().trim();
             lastName = mEtLastName.getText().toString().trim();
             description = mEtDescription.getText().toString().trim();
             dob = mTvDateOfBirth.getText().toString();

            int ageCal = 0;
            if (!dob.isEmpty()) {
                int dobLength = dob.length();
                String afterSubString = dob.substring(dobLength - 4);
                int b = Integer.parseInt(afterSubString);

                int a = cal.get(Calendar.YEAR);

                ageCal = (a - b);
            }
            if (firstName.length() == 0) {
                Utility.showAlertDialog(Constants.ENTER_FIRST_NAME, this);
                return;
            } else if (lastName.length() == 0) {
                Utility.showAlertDialog(Constants.ENTER_LAST_NAME, this);
                return;
            } else if (mTvDateOfBirth.getText().toString().trim().length() == 0) {

                Utility.showAlertDialog(Constants.ENTER_DOB, this);
                return;
            } else if (ageCal <= 18) {

                Utility.showAlertDialog(Constants.AGE_SHOULD_BE_EIGHTEENPLUS, this);
                return;
            } else if (description.length() == 0) {

                Utility.showAlertDialog(Constants.ENTER_DESCRIPTION, this);
                return;
            } else {

                JSONObject json = null;
                try {
                    json = new JSONObject();
                    json.put(Constants.TOKEN, PreferenceHandler.readString(CreateProfileActivity.this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
                    json.put(Constants.USERID, PreferenceHandler.readString(CreateProfileActivity.this, PreferenceHandler.PREF_KEY_USER_ID, ""));
                    json.put(Constants.FIRSTNAME, firstName);
                    json.put(Constants.LASTNAME, lastName);
                    json.put(Constants.GENDER, gender);
//                    json.put(Constants.DOB, URLEncoder.encode(mTvDateOfBirth.getText().toString(),"UTF-8"));
                    json.put(Constants.DOB, mTvDateOfBirth.getText().toString());

                    json.put(Constants.DESCRIPTION, description);
                    if (!PreferenceHandler.readString(this, PreferenceHandler.PROFILE_PIC_URL, "").isEmpty()) {
                        json.put(Constants.PROFILEPIC, PreferenceHandler.readString(this, PreferenceHandler.PROFILE_PIC_URL, ""));
                    }
                    if (!PreferenceHandler.readString(this, PreferenceHandler.COVER_PIC_URL, "").isEmpty()) {
                        json.put(Constants.COVERPIC, PreferenceHandler.readString(this, PreferenceHandler.COVER_PIC_URL, ""));
                    }
                    WebNotificationManager.registerResponseListener(responseHandlerListener);

                    WebServiceClient.createProfile(CreateProfileActivity.this, json.toString(), responseHandlerListener);
                    Log.e("CREATEPROFILE", "--" + json.toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ResponseHandlerListener responseHandlerListener = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {


            WebNotificationManager.unRegisterResponseListener(responseHandlerListener);
            // check for error
            if (error == null) {
                // start parsing
                if (result.getSuccess().equalsIgnoreCase("true")) {
                    if (index == 1) {


                        PreferenceHandler.writeString(CreateProfileActivity.this,PreferenceHandler.PREF_KEY_GENDER,gender);
                        PreferenceHandler.writeString(CreateProfileActivity.this,PreferenceHandler.PREF_KEY_FIRST_NAME,firstName);
                        PreferenceHandler.writeString(CreateProfileActivity.this,PreferenceHandler.PREF_KEY_LAST_NAME,lastName);
                        PreferenceHandler.writeString(CreateProfileActivity.this,PreferenceHandler.PREF_KEY_DOB,dob);
                        PreferenceHandler.writeString(CreateProfileActivity.this,PreferenceHandler.PREF_KEY_DESCRIPTION,description);


                        Intent intent = new Intent(CreateProfileActivity.this, SyncContactsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt(Constants.NUMBER_LENGTH, numberLenth);
                        bundle.putString(Constants.COUNTRY_CODE, mCountryCode);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {

                        PreferenceHandler.writeString(CreateProfileActivity.this,PreferenceHandler.PREF_KEY_GENDER,gender);
                        PreferenceHandler.writeString(CreateProfileActivity.this,PreferenceHandler.PREF_KEY_FIRST_NAME,firstName);
                        PreferenceHandler.writeString(CreateProfileActivity.this,PreferenceHandler.PREF_KEY_LAST_NAME,lastName);
                        PreferenceHandler.writeString(CreateProfileActivity.this,PreferenceHandler.PREF_KEY_DOB,dob);
                        PreferenceHandler.writeString(CreateProfileActivity.this,PreferenceHandler.PREF_KEY_DESCRIPTION,description);
                        Toast.makeText(CreateProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // display error message
                    Toast.makeText(CreateProfileActivity.this, "Error in update", Toast.LENGTH_SHORT).show();

                }
            } else {
                Toast.makeText(CreateProfileActivity.this, "Error in update", Toast.LENGTH_SHORT).show();
                // display error dialog
            }

            // Always close the progressdialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    };

    @Override
    protected void setDataInViewObjects() {

        try {

            if(!PreferenceHandler.readString(this,PreferenceHandler.PREF_KEY_FIRST_NAME,"").isEmpty()) {
                mEtFirstName.setText(PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_FIRST_NAME, ""));
                mEtLastName.setText(PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_LAST_NAME, ""));
                mTvDateOfBirth.setText(PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_DOB, ""));
                mEtDescription.setText(PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_DESCRIPTION, ""));
                //


                if (PreferenceHandler.readString(this, PreferenceHandler.PROFILE_PIC_URL, null) != null) {


                    Glide.with(this).load(WebServiceClient.HTTP_STAGING + PreferenceHandler.readString(this, PreferenceHandler.PROFILE_PIC_URL, ""))
                            .thumbnail(1f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(mIvProfileImage);

//                    imageLoader.DisplayImage(WebServiceClient.HTTP_STAGING + PreferenceHandler.readString(this, PreferenceHandler.PROFILE_PIC_URL, ""), mIvProfileImage);
                } else {
                    mIvProfileImage.setImageResource(R.drawable.default_user);

                }
                if (PreferenceHandler.readString(this, PreferenceHandler.COVER_PIC_URL, null) != null) {


                    Glide.with(this).load(WebServiceClient.HTTP_STAGING + PreferenceHandler.readString(this, PreferenceHandler.COVER_PIC_URL, ""))
                            .thumbnail(1f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(mIvCoverImage);

                } else {
                    mIvCoverImage.setImageResource(R.drawable.default_user);
                }

                if (PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_GENDER, "").length() > 0) {
                    if (PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_GENDER, "").equalsIgnoreCase("male")) {
                        mTvMale.performClick();
                    } else if (PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_GENDER, "").equalsIgnoreCase("female")) {
                        mTvFemale.performClick();
                    } else {
                        mTvOther.performClick();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_datepicker:
                getDateOfBirth();
                break;
            case R.id.tv_dob:
                getDateOfBirth();
                break;

            case R.id.iv_camerabtn_profile_pic:
                PIC_INDEX_CODE = 1;
                selectImage();
                break;

            case R.id.iv_camerabtn_coverpic:
                PIC_INDEX_CODE = 2;
                selectImage();
                break;

            case R.id.textView:
                gender = "male";
                mTvMale.setBackgroundResource(R.drawable.left_rounded_corner_selected);
                mTvMale.setTextColor(Color.parseColor("#ffffff"));
                mTvFemale.setBackgroundResource(R.drawable.rectangle);
                mTvFemale.setTextColor(Color.parseColor("#1C6DCE"));
                mTvOther.setBackgroundResource(R.drawable.right_rounded_corners);
                mTvOther.setTextColor(Color.parseColor("#1C6DCE"));

                break;
            case R.id.tv_female:
                gender = "female";
                mTvFemale.setBackgroundResource(R.drawable.rectangle_selected);
                mTvFemale.setTextColor(Color.parseColor("#ffffff"));
                mTvMale.setBackgroundResource(R.drawable.left_rounded_corners);
                mTvMale.setTextColor(Color.parseColor("#1C6DCE"));
                mTvOther.setBackgroundResource(R.drawable.right_rounded_corners);
                mTvOther.setTextColor(Color.parseColor("#1C6DCE"));
                break;
            case R.id.tv_other:
                gender = "other";
                mTvOther.setBackgroundResource(R.drawable.right_rounded_corner_selected);
                mTvOther.setTextColor(Color.parseColor("#ffffff"));
                mTvFemale.setBackgroundResource(R.drawable.rectangle);
                mTvFemale.setTextColor(Color.parseColor("#1C6DCE"));
                mTvMale.setBackgroundResource(R.drawable.left_rounded_corners);
                mTvMale.setTextColor(Color.parseColor("#1C6DCE"));
                break;

            default:
                break;
        }
    }


    private void uploadBitmapAsMultipart(Bitmap myBitmap) {
        ContentBody contentPart = null;
        try {

            Uri uri = getImageUri(this, myBitmap);
            String imagePath = getRealPathFromURI(uri);
            String url = WebServiceClient.HTTP_UPLOAD_IMAGE;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            if (imagePath.endsWith(".png")) {
                myBitmap.compress(Bitmap.CompressFormat.PNG, 80, bos);
                contentPart = new ByteArrayBody(bos.toByteArray(), "Image.png");
            } else {
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                contentPart = new ByteArrayBody(bos.toByteArray(), "Image.jpg");
            }

            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            reqEntity.addPart("fileToUpload", contentPart);

            if (Utility.isInternetConnection(CreateProfileActivity.this)) {
                new WebServiceClientUploadImage(CreateProfileActivity.this, this, url, reqEntity, 3).execute();
            } else {
                // open dialog here
                new Utility().showNoInternetDialog((Activity) CreateProfileActivity.this);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskResponse(Object result, int urlResponseNo) {


        if (PIC_INDEX_CODE == 2) {
            try {
                JSONObject jsonObject = new JSONObject(result.toString());
                JSONArray file = jsonObject.getJSONArray("files");
                JSONObject obj = file.getJSONObject(0);
                CoverImageUrl = obj.optString("url");
                PreferenceHandler.writeString(this, PreferenceHandler.COVER_PIC_URL, CoverImageUrl);
                Log.e("coverImage", "" + CoverImageUrl);
            } catch (Exception e) {

            }

        } else {

//            ProfileImageRespons

            try {
                JSONObject jsonObject = new JSONObject(result.toString());
                JSONArray file = jsonObject.getJSONArray("files");
                JSONObject obj = file.getJSONObject(0);
                profileImageUrl = obj.optString("url");
                PreferenceHandler.writeString(this, PreferenceHandler.PROFILE_PIC_URL, profileImageUrl);
                Log.e("profileImage", "" + profileImageUrl);

            } catch (Exception e) {

            }


        }

    }


}
