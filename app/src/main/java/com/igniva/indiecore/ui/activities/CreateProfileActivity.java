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
import com.igniva.indiecore.utils.imageloader.ImageLoader;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

/**
 * Created by siddharth-05 on 3/6/16.
 */
public class CreateProfileActivity extends BaseActivity implements AsyncResult {

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
    private String userChoosenTask = "";
    private int PIC_INDEX_CODE = 0;
    private int numberLenth;
    Calendar cal;
    String profileImageUrl = "";
    String CoverImageUrl = "";
    String gender = "male";
    CreateProfileActivity mCreateProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        initToolbar();
        setUpLayout();
        setDataInViewObjects();
    }

    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            TextView mTvTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
            mTvTitle.setText(getResources().getString(R.string.contact_number));
            //
            TextView mTvNext = (TextView) mToolbar.findViewById(R.id.toolbar_next);
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


        mDay = cal.get(Calendar.DAY_OF_MONTH);
        mMonth = cal.get(Calendar.MONTH);
        mYear = cal.get(Calendar.YEAR);
        DateDialog();
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
        final CharSequence[] items = {getResources().getString(R.string.take_photo),getResources().getString(R.string.choose_from_gallary)
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateProfileActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getResources().getString(R.string.upload_image));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = true;
                if (items[item].equals(getResources().getString(R.string.take_photo))) {
                    userChoosenTask = getResources().getString(R.string.take_photo);
                    if (result)
                        cameraIntent();
                } else if (items[item].equals(getResources().getString(R.string.choose_from_gallary))) {
                    userChoosenTask = getResources().getString(R.string.choose_from_gallary);
                    if (result)
                        galleryIntent();
                }
            }

        });

        builder.setPositiveButton(getResources().getString(R.string.cancal), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals(getResources().getString(R.string.take_photo)))
                        cameraIntent();
                    else if (userChoosenTask.equals(getResources().getString(R.string.choose_from_gallary)))
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
            String firstName = mEtFirstName.getText().toString().trim();
            String lastName = mEtLastName.getText().toString().trim();
            String description = mEtDescription.getText().toString().trim();

            int a = cal.get(Calendar.YEAR);
            int b = mYear;

            int ageCal = (a - b);

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
                    if (!profileImageUrl.isEmpty()) {
                        json.put(Constants.PROFILEPIC, profileImageUrl);
                    }
                    if (!CoverImageUrl.isEmpty()) {
                        json.put(Constants.COVERPIC, CoverImageUrl);
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
                    Intent intent = new Intent(CreateProfileActivity.this, SyncContactsActivity.class);
                    intent.putExtra(Constants.NUMBER_LENGTH,numberLenth);
                    startActivity(intent);
                }
                else {
                    // display error message
                }
            } else {
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

            Bundle bundle = getIntent().getExtras();
            String firstName = bundle.getString(Constants.FIRSTNAME);
            String lastName = bundle.getString(Constants.LASTNAME);
            String dateOfBirth = bundle.getString(Constants.DOB);
            String desc = bundle.getString(Constants.DESCRIPTION);
            String gender = bundle.getString(Constants.GENDER);
            String profilePicUrl=bundle.getString(Constants.PROFILEPIC);
            String coverPic= bundle.getString(Constants.COVERPIC);
            numberLenth=bundle.getInt(Constants.NUMBER_LENGTH);

            mEtFirstName.setText(firstName);
            mEtLastName.setText(lastName);
            mTvDateOfBirth.setText(dateOfBirth);
            mEtDescription.setText(desc);
            //
            if(gender.length()>0){
                if(gender.equalsIgnoreCase("male")){
                    mTvMale.performClick();
                }else if (gender.equalsIgnoreCase("female")){
                    mTvFemale.performClick();
                } else{
                    mTvOther.performClick();
                }


                ImageLoader imageLoader=new ImageLoader(CreateProfileActivity.this);

                if(profilePicUrl!=null){
                    Log.e("Url Profile Image",""+profilePicUrl);
                    imageLoader.DisplayImage(WebServiceClient.HTTP_STAGING+profilePicUrl,mIvProfileImage);
                }
                if(coverPic!=null) {
                    Log.e("Url cover Image",""+coverPic);
                    imageLoader.DisplayImage(WebServiceClient.HTTP_STAGING+coverPic, mIvCoverImage);
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
        ContentBody contentPart=null;
        try {

            Uri uri = getImageUri(this,myBitmap);
            String imagePath=getRealPathFromURI(uri);
            String url = WebServiceClient.HTTP_UPLOAD_IMAGE;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            if (imagePath.endsWith(".png")) {
                myBitmap.compress(Bitmap.CompressFormat.PNG, 80, bos);
                contentPart = new ByteArrayBody(bos.toByteArray(), "Image.png");
            }
            else {
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
                Log.e("profileImage", "" + profileImageUrl);

            } catch (Exception e) {

            }


        }

    }


    private class Url {
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        private String url;
    }

}
