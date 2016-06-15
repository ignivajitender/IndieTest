package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;
import android.widget.Toast;

import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;

public class SyncContactsActivity extends  BaseActivity implements View.OnClickListener{

    Toolbar mToolbar;
    ArrayList<String> mNumbers= null;
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
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }

    }


    @Override
    protected void setUpLayout() {


        mNumbers= new ArrayList<String>();

    }

    @Override
    protected void setDataInViewObjects() {

    }



    public void getAllContacts(){

        try {
            String phoneNumber = null;
            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                mNumbers.add(phoneNumber);
            }
            Log.e("List of contacts", "" + mNumbers.toString());
            phones.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void syncContacts(){

        JSONObject syncPayload=null;
        try {
            if (mNumbers.size() == 0) {

                Utility.showAlertDialog("No cOntact available for sync", this);
                return;
            } else {
            syncPayload= new JSONObject();
                syncPayload.put(Constants.TOKEN, PreferenceHandler.readString(SyncContactsActivity.this, Constants.TOKEN, ""));
                syncPayload.put(Constants.USERID,PreferenceHandler.readString(SyncContactsActivity.this, Constants.USERID, ""));
                syncPayload.put(Constants.NUMBER,mNumbers);

                WebNotificationManager.registerResponseListener(responseListner);
                WebServiceClient.syncContacts(this,syncPayload.toString(),responseListner);


                Log.e("SyncContactList","----------"+mNumbers.toString());

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    ResponseHandlerListener responseListner= new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            WebNotificationManager.unRegisterResponseListener(responseListner);

            try {

                if (error == null) {
                    // start parsing
                    if (result.getSuccess().equalsIgnoreCase("true")) {

                        Toast.makeText(getApplicationContext(),"Contacts sync successfully",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SyncContactsActivity.this,BadgesActivity.class));

                    }
                } else {

                    Toast.makeText(getApplicationContext(),"error syncing contacts",Toast.LENGTH_LONG).show();

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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_skip_step:
                startActivity(new Intent(this,BadgesActivity.class));
                break;

            case R.id.iv_syncbtn:
                getAllContacts();
                syncContacts();
                break;
        }
    }
}
