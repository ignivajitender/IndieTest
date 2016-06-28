package com.igniva.indiecore.ui.activities;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.igniva.indiecore.R;
import com.igniva.indiecore.model.ContactPojo;
import com.igniva.indiecore.ui.adapters.InviteContactAdapter;
import com.igniva.indiecore.utils.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by igniva-andriod-05 on 17/6/16.
 */
public class InviteContactActivity extends BaseActivity {
    Toolbar mToolbar;
    RecyclerView recyclerView;
    private EditText mEtSearch;
    ContactPojo mContatctPojo;
    InviteContactAdapter mInviteContactAdapter;
    ArrayList<ContactPojo> mContactList = null;
    ContactPojo obj;
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
            //
            TextView mTvSend = (TextView) mToolbar.findViewById(R.id.toolbar_next);
            mTvSend.setText("Send");

            mTvSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    onClickWhatsApp(v);

                    Log.e("contact list to send", "" + mSelectedContacts);
                    Log.e("contact list size", "" + mSelectedContacts.size());

                    sendInviteSms();
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
        try {

            mSelectedContactName.clear();
            mSelectedContacts.clear();

            mContactList = new ArrayList<ContactPojo>();
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

            final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);


            obj = new ContactPojo();


            mEtSearch = (EditText) findViewById(R.id.et_search);
            mEtSearch.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    System.out.println("Text [" + s + "]");

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
            mInviteContactAdapter = new InviteContactAdapter(this, mContactList);
            recyclerView.setAdapter(mInviteContactAdapter);

        } catch (Exception e) {


        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onClick(View v) {

    }


    public void getAllContacts() {

        try {
            String phoneNumber = null;
            InputStream bm=null;
            String name=null;
            String image_uri=null;
            Cursor phones = null;
            String contactID;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,null);
                phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
//

            }
            while (phones.moveToNext()) {
                image_uri=null;
                mContatctPojo = new ContactPojo();
               name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String phnNumber = phoneNumber.replace(" ", "");
                phnNumber = phnNumber.replace("-", "");
                 mContatctPojo.setHasImage(false);

                contactID = phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID));

//                 bm=getPhotoThumbnailInputStream(contactID);
//            Bitmap    b = BitmapFactory.decodeStream(bm);

                image_uri = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

                if (image_uri != null) {
                   mContatctPojo.setContactIcon(image_uri);
                    mContatctPojo.setHasImage(true);
                }

                mContatctPojo.setContactName(name);
                mContatctPojo.setContactNumber(phnNumber);

                mContatctPojo.setSelected(false);

                mContactList.add(mContatctPojo);

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
            mNumber = mSelectedContacts.toString();

            if (!Build.MANUFACTURER.contains("Samsung")) {
                mNumber = mNumber.replace(",", ";");
            }
            mNumber = mNumber.replace("]", "");
            mNumber = mNumber.replace("[", "");
            Log.e("Passed contacts", "++" + mNumber);
            Log.e("PASSED LIST SIZE", "" + mSelectedContacts.size());
            Uri smsToContacts = Uri.parse("sms to:" + Uri.encode(mNumber));
            Intent intent = new Intent(Intent.ACTION_SENDTO, smsToContacts);
            String message = "hello..please download this amazing app from.www.google-playstore/indiecore/";
            // message = message.replace("%s", StoresMessage.m_storeName);
            intent.putExtra("sms_body", message);
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Some error occured", Toast.LENGTH_SHORT).show();

        }

    }


    public void onClickWhatsApp(View view) {

        PackageManager pm = getPackageManager();
        try {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = "YOUR TEXT HERE";

            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }

    }


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
