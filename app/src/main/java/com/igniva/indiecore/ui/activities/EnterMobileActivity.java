package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.db.BadgesDb;
import com.igniva.indiecore.model.BadgesPojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.ui.adapters.PremiumBadgesAdapter;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;
import com.igniva.indiecore.utils.Validation;
import com.igniva.indiecore.controller.WebServiceClient.WebError;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;

import org.json.JSONObject;

import com.igniva.indiecore.R;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

/**
 * Created by igniva-andriod-05 on 2/6/16.
 */
public class EnterMobileActivity extends BaseActivity {

    private LinearLayout mLlTopNextEvent;
    private Button mButtonNext;
    String countryId, mobileNumber;
    private EditText mEtMobileNumber, mEtCountryCode;
    Toolbar mToolbar;
    BadgesDb dbBadges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_mobile);
        Fabric.with(this, new Crashlytics());
        initToolbar();
        setUpLayout();
        setDataInViewObjects();

    }

    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            TextView mTvTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
            mTvTitle.setText(getResources().getString(R.string.contact_number));
            TextView mTvNext = (TextView) mToolbar.findViewById(R.id.toolbar_next);
            mTvNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    validateMobileNumber();
                }
            });

            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }

    }

    /**
     * To validate country code and mobile number fields and to execute login call
     * @param
     * @param
     *
     */
    private void validateMobileNumber() {

        try {
            countryId = mEtCountryCode.getText().toString();
            mobileNumber = mEtMobileNumber.getText().toString();
            // TODO Validate Mobile Number
            String payload = createPayload();

            if (countryId.isEmpty()) {
                Utility.showAlertDialog(Constants.COUNTRY_CODE_VALIDATION, this,null);
                return;
            } else if (mobileNumber.isEmpty()) {

                Utility.showAlertDialog(Constants.MOBILE_NUMBER_VALIDATION, this,null);
                return;
            } else if(mobileNumber.length()<10) {
                Utility.showAlertDialog(Constants.TEN_DIGIT_MOBILENUMBER_VALIDATION,this,null);
                return;
            }
            else
                {
                    if (payload != null) {
                        // Web service Call
                        // Step 1 - Register responsehandler
                        WebNotificationManager.registerResponseListener(responseHandlerListener);
                        // Step 2 - Webservice Call
                        WebServiceClient.getLogin(EnterMobileActivity.this, payload.toString(), responseHandlerListener);
                        Log.e("EnterMobileActivity payload","------"+payload);
                    } else {
                        // TODO show error dialog
                    }

                }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void setUpLayout() {
          try {
        mLlTopNextEvent = (LinearLayout) findViewById(R.id.ll_next);
        //mLlTopNextEvent.setOnClickListener(onClick());

        mButtonNext = (Button) findViewById(R.id.btn_next);
        // mButtonNext.setOnClickListener(this);

        mEtMobileNumber = (EditText) findViewById(R.id.et_mobile_number);
              mEtMobileNumber.setText("9816428478");
        mEtMobileNumber.requestFocus();

        mEtCountryCode = (EditText) findViewById(R.id.et_country_code);
        //  mEtCountryCode.setText(countryId);
//              mEtCountryCode.setText("91");
    } catch (Exception e){
        e.printStackTrace();
    }
    }

    @Override
    protected void setDataInViewObjects() {
        try {
            String CountryID="";
            String CountryZipCode="";
            TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            if (manager.getSimState() != TelephonyManager.SIM_STATE_ABSENT) {
                CountryID= manager.getSimCountryIso().toUpperCase();
                String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
                for(int i=0;i<rl.length;i++){
                    String[] g=rl[i].split(",");
                    if(g[1].trim().equals(CountryID.trim())){
                        CountryZipCode=g[0];
                        break;  }
                }
                mEtCountryCode.setText(CountryZipCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onClick(View v) {


    }

    ResponseHandlerListener responseHandlerListener = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebError error, ProgressDialog mProgressDialog) {
            // Always unregister
            WebNotificationManager.unRegisterResponseListener(responseHandlerListener);
            // check for error
            try {
                if (error == null) {
                    // start parsing
                    if (result.getSuccess().equalsIgnoreCase("true")) {
                        PreferenceHandler.writeString(EnterMobileActivity.this,PreferenceHandler.PREF_KEY_MOBILE_NUMBER,mobileNumber);
                        PreferenceHandler.writeString(EnterMobileActivity.this,PreferenceHandler.PREF_KEY_COUNTRY_CODE,countryId);
                        PreferenceHandler.writeInteger(EnterMobileActivity.this,PreferenceHandler.PREF_KEY_NUMBER_LENGTH,mobileNumber.length());


                        // TODO check if test user

                        if(result.getUserId()==null){

                            Intent in = new Intent(EnterMobileActivity.this, OtpVerificationActivity.class);
//                            Bundle bundle = new Bundle();
//                            bundle.putString(Constants.MOBILE_NO, mobileNumber);
//                            bundle.putString(Constants.COUNTRY_CODE, countryId);
//                            bundle.putInt(Constants.NUMBER_LENGTH,mobileNumber.length());
//                            in.putExtras(bundle);
                            startActivity(in);

                        } else if(result.getNewUser().equalsIgnoreCase("False")){

                            insertRecords(result.getBadges());
                            // save in preferences
                            PreferenceHandler.writeString(EnterMobileActivity.this, PreferenceHandler.PREF_KEY_USER_TOKEN, result.getToken());
                            PreferenceHandler.writeString(EnterMobileActivity.this, PreferenceHandler.PREF_KEY_USER_ID, result.getUserId());
                            PreferenceHandler.writeString(EnterMobileActivity.this,PreferenceHandler.COVER_PIC_URL,result.getProfile().getCoverPic());
                            PreferenceHandler.writeString(EnterMobileActivity.this,PreferenceHandler.PROFILE_PIC_URL,result.getProfile().getProfilePic());
                            PreferenceHandler.writeString(EnterMobileActivity.this,PreferenceHandler.PREF_KEY_GENDER,result.getProfile().getGender());
                            PreferenceHandler.writeString(EnterMobileActivity.this,PreferenceHandler.PREF_KEY_FIRST_NAME,result.getProfile().getFirstName());
                            PreferenceHandler.writeString(EnterMobileActivity.this,PreferenceHandler.PREF_KEY_LAST_NAME,result.getProfile().getLastName());
                            PreferenceHandler.writeString(EnterMobileActivity.this,PreferenceHandler.PREF_KEY_DOB,result.getProfile().getDob());
                            PreferenceHandler.writeString(EnterMobileActivity.this,PreferenceHandler.PREF_KEY_DESCRIPTION,result.getProfile().getDesc());
                            PreferenceHandler.writeInteger(EnterMobileActivity.this,PreferenceHandler.TOTAL_BADGE_LIMIT,result.getBadgeLimit());
                            //
                            Intent in = new Intent(EnterMobileActivity.this, DashBoardActivity.class);
                            Bundle bundle = new Bundle();

                            bundle.putString(Constants.FIRSTNAME, result.getProfile().getFirstName());
                            bundle.putString(Constants.LASTNAME, result.getProfile().getLastName());
                            bundle.putString(Constants.DOB, result.getProfile().getDob());
                            bundle.putString(Constants.DESCRIPTION, result.getProfile().getDesc());
                            bundle.putString(Constants.GENDER, result.getProfile().getGender());
                            bundle.putInt(Constants.INDEX,1);
                            bundle.putString(Constants.PROFILEPIC,result.getProfile().getProfilePic());
                            bundle.putString(Constants.COVERPIC,result.getProfile().getCoverPic());
                            bundle.putInt(Constants.NUMBER_LENGTH,mobileNumber.length());
                            bundle.putString(Constants.COUNTRY_CODE,countryId);
                            in.putExtras(bundle);
                            startActivity(in);
                        }
                    } else {

                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.concurrent_verification_message), Toast.LENGTH_LONG).show();
                        // display error message
                    }
                } else {
                    // display error dialog
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.some_unknown_error), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e){
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.some_unknown_error), Toast.LENGTH_LONG).show();

            e.printStackTrace();
        }

            // Always close the progressdialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    };


/*
* insert  badges record  in the db for a existing user
*
* */


    public void insertRecords(ArrayList<BadgesPojo> userBadges) {
        try{
            dbBadges = new BadgesDb(this);
            dbBadges.insertAllBadges(userBadges);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * To build json to send with  login call
     *
     * @return
     */

    public String createPayload() {
        JSONObject payloadJson = null;
        try {
            payloadJson = new JSONObject();
            payloadJson.put(Constants.DEVICETYPE, "android");
            payloadJson.put(Constants.DEVICETOKEN, "gcm id----2vgfwufhyiewjfkhwbs");
            payloadJson.put(Constants.COUNTRY_CODE, countryId);
            payloadJson.put(Constants.MOBILE_NO, mobileNumber);
            payloadJson.put(Constants.LOCALE, "en");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return payloadJson.toString();
    }

}
