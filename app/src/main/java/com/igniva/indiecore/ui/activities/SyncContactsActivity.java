package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
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

public class SyncContactsActivity extends  BaseActivity implements View.OnClickListener,FABProgressListener{

    Toolbar mToolbar;
    ArrayList<String> mNumbers= null;
    private int numberLength;
    private TextView mStartSync;
    private String mCountryCode;
    private  String COUNTRY_PREFIX="";
    FABProgressCircle fabProgressCircle;
    FloatingActionButton fab;
    ImageView img_btn;
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

            mStartSync =(TextView) findViewById(R.id.tv_start_sync);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }

        fabProgressCircle =(FABProgressCircle)findViewById(R.id.fabProgressCircle);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        img_btn=(ImageView)findViewById(R.id.img_btn);
        img_btn.setVisibility(View.GONE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabProgressCircle.show();
                mStartSync.setText("Syncing..");
                getAllContacts();
                syncContacts();
            }
        });

    }


    @Override
    public void onFABProgressAnimationEnd() {
        img_btn.setVisibility(View.VISIBLE);
        mStartSync.setText("successful");
        Snackbar.make(fabProgressCircle, "Complete", Snackbar.LENGTH_LONG).show();
//        Toast.makeText(getApplicationContext(),getResources().getString(R.string.contact_sync_successful),Toast.LENGTH_LONG).show();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SyncContactsActivity.this,BadgesActivity.class));

            }
        }, 3000);
        //fabProgressCircle.setForeground(getResources().getDrawable(R.drawable.done));

    }

    @Override
    protected void setUpLayout() {
try{

        mNumbers= new ArrayList<String>();
        Bundle bundle=getIntent().getExtras();
        numberLength=bundle.getInt(Constants.NUMBER_LENGTH,0);
        mCountryCode=bundle.getString(Constants.COUNTRY_CODE);

        COUNTRY_PREFIX=mCountryCode;
}catch (Exception e){
    e.printStackTrace();
}
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
                String phnNumber=phoneNumber.replace(" ","");
                phnNumber=phnNumber.replace("-","");

                if(phnNumber.length()==numberLength){

                  phnNumber=COUNTRY_PREFIX+phnNumber;

                }
                mNumbers.add(phnNumber);
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

                Utility.showAlertDialog(getResources().getString(R.string.no_contact_to_sync), this);
                return;
            } else {
            syncPayload= new JSONObject();
                syncPayload.put(Constants.TOKEN, PreferenceHandler.readString(SyncContactsActivity.this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
                syncPayload.put(Constants.USERID,PreferenceHandler.readString(SyncContactsActivity.this, PreferenceHandler.PREF_KEY_USER_ID, ""));

               String mNumber=mNumbers.toString().substring(1,mNumbers.toString().length()-1);

                syncPayload.put(Constants.NUMBER,mNumber);

                WebNotificationManager.registerResponseListener(responseListner);
                WebServiceClient.syncContacts(this,syncPayload.toString(),responseListner);


                Log.e("SyncContactList","----------"+mNumber);

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

                        fabProgressCircle.beginFinalAnimation();
                        fabProgressCircle.attachListener(SyncContactsActivity.this);


                    }
                } else {

                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.contact_sync_failure),Toast.LENGTH_LONG).show();

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

//            case R.id.iv_syncbtn:
//                getAllContacts();
//                syncContacts();
//                break;
        }
    }
}
