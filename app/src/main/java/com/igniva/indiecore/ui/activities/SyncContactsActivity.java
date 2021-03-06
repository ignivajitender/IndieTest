package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.db.BadgesDb;
import com.igniva.indiecore.model.BadgesPojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.model.UsersPojo;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;

public class SyncContactsActivity extends BaseActivity implements View.OnClickListener {

    private static final String LOG_TAG = "SyncContactsActivity";
    Toolbar mToolbar;
    ArrayList<String> mNumbersList = null;
    private int numberLength;
    private TextView mStartSync;
    private String mCountryCode;
    private String COUNTRY_PREFIX = "";
    FABProgressCircle fabProgressCircle;
    public ArrayList<UsersPojo> mUSers = new ArrayList<UsersPojo>();
    private int INDEX = 0;
    FloatingActionButton fab;
    ImageView img_btn, mIvSyncContacts;
    BadgesDb dbContacts;
    TextView mTvSkipStep;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_contacts);
        initToolbar();
        setUpLayout();
    }

    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            TextView mTvTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
            mTvTitle.setText(getResources().getString(R.string.contacts_sync));
            //
            TextView mTvNext = (TextView) mToolbar.findViewById(R.id.toolbar_next);
            mTvNext.setVisibility(View.GONE);
            //

            mStartSync = (TextView) findViewById(R.id.tv_start_sync);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }

//        fabProgressCircle =(FABProgressCircle)findViewById(R.id.fabProgressCircle);
//        fab = (FloatingActionButton) findViewById(R.id.fab);
//        img_btn=(ImageView)findViewById(R.id.img_btn);
//        img_btn.setVisibility(View.GONE);
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                fabProgressCircle.show();
//                mStartSync.setText("Syncing..");
//                getAllContacts();
//                syncContacts();
//            }
//        });

    }



    @Override
    protected void setUpLayout() {
        try {

            mNumbersList = new ArrayList<String>();
            Bundle bundle = getIntent().getExtras();
            INDEX = bundle.getInt(Constants.INDEX);


            numberLength = PreferenceHandler.readInteger(SyncContactsActivity.this, PreferenceHandler.PREF_KEY_NUMBER_LENGTH, 0);
            mCountryCode = PreferenceHandler.readString(SyncContactsActivity.this, PreferenceHandler.PREF_KEY_COUNTRY_CODE, "");
            mTvSkipStep = (TextView) findViewById(R.id.tv_skip_step);
            mTvSkipStep.setOnClickListener(this);
            mIvSyncContacts = (ImageView) findViewById(R.id.iv_syncbtn);
            mIvSyncContacts.setOnClickListener(this);
            img_btn = (ImageView) findViewById(R.id.img_btn);
            img_btn.setOnClickListener(this);
            COUNTRY_PREFIX = mCountryCode.trim();
            if(INDEX==20){
                mTvSkipStep.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setDataInViewObjects() {

    }

    public void getAllContacts() {

        try {
            String phoneNumber = null;
            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String phnNumber = phoneNumber.replace(" ", "");
                phnNumber = phnNumber.replace("  ", "");
                phnNumber = phnNumber.replace(":", "");
                phnNumber = phnNumber.replace("-", "");
                phnNumber = phnNumber.replace("+", "");
                phnNumber = phnNumber.replace(")", "");
                phnNumber = phnNumber.replace("(", "");

                phnNumber = phnNumber.trim();

                if (phnNumber.length() == numberLength) {
                    phnNumber = COUNTRY_PREFIX.trim() + phnNumber.trim();
                }
                mNumbersList.add(phnNumber);
                Log.e("Number list after appending 91", "" + mNumbersList.toString());
            }

            phones.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void syncContacts() {

        String syncPayload = null;
        try {
            if (mNumbersList.size() == 0) {

                Utility.showAlertDialog(getResources().getString(R.string.no_contact_to_sync), this,null);
                return;
            } else {
                syncPayload = createPayload();
                WebNotificationManager.registerResponseListener(responseListner);
                WebServiceClient.syncContacts(this, syncPayload, responseListner);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    ResponseHandlerListener responseListner = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            WebNotificationManager.unRegisterResponseListener(responseListner);

            try {
                if (error == null) {
                    // start parsing
                    if (result.getSuccess().equalsIgnoreCase("true")) {
                        mUSers = result.getUsers();
                        insertUsers();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.contact_sync_successful), Toast.LENGTH_LONG).show();
                        if (INDEX == 22) {
                            startActivity(new Intent(SyncContactsActivity.this, BadgesActivity.class));

                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.contact_sync_successful), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.contact_sync_failure), Toast.LENGTH_LONG).show();
                }
                // Always close the progressdialog
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        ;
    };


    public void insertUsers() {
        try {
            dbContacts = new BadgesDb(this);
            ArrayList<UsersPojo> indiecoreUsers = new ArrayList<UsersPojo>();
            indiecoreUsers = mUSers;
            dbContacts.insertAllContacts(indiecoreUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_skip_step:
                startActivity(new Intent(this, BadgesActivity.class));
                break;
            case R.id.iv_syncbtn:
                getAllContacts();
                syncContacts();
                break;
        }
    }

    public String createPayload() {
        JSONObject payloadJson = null;
        try {
            payloadJson = new JSONObject();
            payloadJson.put(Constants.TOKEN, PreferenceHandler.readString(SyncContactsActivity.this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payloadJson.put(Constants.USERID, PreferenceHandler.readString(SyncContactsActivity.this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            String mNumber = "";
            int sizeOfList = mNumbersList.size();
            for (int i = 0; i < sizeOfList; i++) {
                mNumber = mNumber + "," + mNumbersList.get(i).trim();
            }
            mNumber = mNumber.substring(1);
            payloadJson.put(Constants.NUMBER, mNumber);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Log.d(LOG_TAG, "paload is " + payloadJson.toString());
        return payloadJson.toString();
    }

}
