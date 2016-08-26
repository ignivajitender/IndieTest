package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.db.BadgesDb;
import com.igniva.indiecore.model.ContactPojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.model.UsersPojo;
import com.igniva.indiecore.ui.adapters.InviteContactAdapter;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by igniva-andriod-05 on 17/6/16.
 */
public class InviteContactActivity extends BaseActivity {
    Toolbar mToolbar;
    private RecyclerView recyclerView;
    private EditText mEtSearch;
    ContactPojo mContactPojo;
    InviteContactAdapter mInviteContactAdapter;
    ArrayList<ContactPojo> mContactList = null;
    private int mMaxContacts = 0;
    BadgesDb Db;
    String Medium = "2";
    int index = -1;
    private ArrayList<UsersPojo> mSavedUsersList = new ArrayList<UsersPojo>();
    public static ArrayList<String> mSelectedContacts = new ArrayList<String>();
    public static ArrayList<String> mSelectedContactName = new ArrayList<String>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_contacts);
        initToolbar();
        setUpLayout();
        setDataInViewObjects();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            TextView mTvTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
            mTvTitle.setText(getResources().getString(R.string.invite_friends));
            TextView mTvSend = (TextView) mToolbar.findViewById(R.id.toolbar_next);
            mTvSend.setText("Send");

            mTvSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("contact list to send", "" + mSelectedContacts);
                    Log.e("contact list size", "" + mSelectedContacts.size());

                    sendInviteSms();
//                    TODO call this slot buy service
                    if(index==3) {
                        buyBadgeSlot();
                    }
                }
            });
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

//        try {
//            Db = new BadgesDb(this);
//            mSavedUsersList = Db.retrieveSavedUsersList();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            mSelectedContactName.clear();
            mSelectedContacts.clear();
            try {
                Bundle bundle = getIntent().getExtras();
                index = bundle.getInt(Constants.INDEX);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mContactList = new ArrayList<>();
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);

            if (index != 3) {
                try {
                    Db = new BadgesDb(InviteContactActivity.this);
                    mSavedUsersList = Db.retrieveSavedUsersList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            mEtSearch = (EditText) findViewById(R.id.et_search);
            mEtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // System.out.println("Text [" + s + "]");

                    mInviteContactAdapter.getFilter().filter(s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setDataInViewObjects() {
        getAllContacts();
        mInviteContactAdapter = null;

        try {
            /**
             * index=3 --> coming from Invite Activity, the first time
             *  index!=3 --> coming from settings-->SMS
             * */
            if (index == 3) {
                mMaxContacts = 10;

                mInviteContactAdapter = new InviteContactAdapter(this, mContactList, mMaxContacts);
                recyclerView.setAdapter(mInviteContactAdapter);
            } else {
                mMaxContacts = 0;

                for(int i=0;i<mSavedUsersList.size();i++){

                    for(int j=0;j<mContactList.size();j++){

                        if(mSavedUsersList.get(i).getMobileNo().equals(mContactList.get(j).getContactNumber())){
                            mContactList.remove(j);
                        }
                    }
                }

                mInviteContactAdapter = new InviteContactAdapter(this, mContactList, mMaxContacts);
                recyclerView.setAdapter(mInviteContactAdapter);
            }

        } catch (Exception e) {
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    protected void onClick(View v) {

    }


    public void getAllContacts() {

        try {
            String phoneNumber = null;
            InputStream bm = null;
            String name = null;
            String image_uri = null;
            Cursor phones = null;
            String contactID;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,null);
                phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
//

            }
            while (phones.moveToNext()) {
                mContactPojo = new ContactPojo();
                name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String phnNumber = phoneNumber.replace(" ", "");
                phnNumber = phnNumber.replace("-", "");
                phnNumber =phnNumber.replace("(","");
                phnNumber =phnNumber.replace(")","");
                phnNumber="91"+phnNumber;
                mContactPojo.setHasImage(false);
                image_uri = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

                if (image_uri != null) {
                    mContactPojo.setContactIcon(image_uri);
                    mContactPojo.setHasImage(true);
                }

                mContactPojo.setContactName(name);
                mContactPojo.setContactNumber(phnNumber);

                mContactPojo.setSelected(false);
                mContactList.add(mContactPojo);

            }
            Log.e("List of contacts", "" + mContactList.size());
            phones.close();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void sendInviteSms() {
        String mNumber = "";
        try {

            if (mSelectedContacts.size() > mMaxContacts) {
                mNumber = mSelectedContacts.toString();

                if (!Build.MANUFACTURER.contains("Samsung")) {
                    mNumber = mNumber.replace(",", ";");
                }

                mNumber = mNumber.replace("]", "");
                mNumber = mNumber.replace("[", "");
                Log.e("Passed contacts", "++" + mNumber);
                Log.e("PASSED LIST SIZE", "" + mSelectedContacts.size());

                //

                mNumber = mNumber.replace(" ", "");
                Uri smsToContacts = Uri.parse("smsto:" + mNumber);
                Intent intent = new Intent(Intent.ACTION_SENDTO, smsToContacts);
                String message = getResources().getString(R.string.invite_message);
                // message = message.replace("%s", StoresMessage.m_storeName);
                intent.putExtra("sms_body", message);
                startActivity(intent);

            } else {
                if (index == 3) {
                    Utility.showAlertDialog(getResources().getString(R.string.invite_atleast_ten_friend), this);
                    return;
                } else {
                    Utility.showAlertDialog(getResources().getString(R.string.at_least_one_contact), this);
                    return;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.some_unknown_error), Toast.LENGTH_SHORT).show();

        }

    }


    /*
    *   payload to buy a slot
    *   PARAMETER: token, userId, medium, promo_code
    *
    * */

    public String createPayload() {

        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.MEDIUM, Medium);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return payload.toString();
    }


    public void buyBadgeSlot() {

        String payload = createPayload();
        try {
            WebNotificationManager.registerResponseListener(responseHandlerBuyASlot);
            WebServiceClient.buy_a_badge_slot(this, payload, responseHandlerBuyASlot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    ResponseHandlerListener responseHandlerBuyASlot = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            try {
                WebNotificationManager.unRegisterResponseListener(responseHandlerBuyASlot);
                if (error == null) {
                    if (result.getSuccess().equalsIgnoreCase("true")) {
                        PreferenceHandler.writeInteger(InviteContactActivity.this, PreferenceHandler.TOTAL_BADGE_LIMIT, result.getBadgeLimit());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            // Always close the progressdialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }


        }
    };


//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "InviteContact Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.igniva.indiecore.ui.activities/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "InviteContact Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.igniva.indiecore.ui.activities/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
//    }
}
