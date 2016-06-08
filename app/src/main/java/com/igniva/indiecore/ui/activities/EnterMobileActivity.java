package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.utils.Validation;
import com.igniva.indiecore.controller.WebServiceClient.WebError;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import org.json.JSONObject;
import com.igniva.indiecore.R;

/**
 * Created by igniva-andriod-05 on 2/6/16.
 */
public class EnterMobileActivity extends BaseActivity {

    private LinearLayout mLlTopNextEvent;
    private Button mButtonNext;
    String countryId;
    private EditText mEtMobileNumber, mEtCountryCode;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_mobile);

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

    private void validateMobileNumber() {

        // TODO Validate Mobile Number
        String payload=createPayload();
        if (payload!=null) {
            // Web service Call
            // Step 1 - Register responsehandler
            WebNotificationManager.registerResponseListener(responseHandlerListener);
            // Step 2 - Webservice Call
            WebServiceClient.getLogin(EnterMobileActivity.this, payload.toString(), responseHandlerListener);
        }else{
            // TODO show error dialog
        }

    }


    @Override
    protected void setUpLayout() {


        try {
//        String locale = getApplicationContext().getResources().getConfiguration().locale.getCountry();

            TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            if (manager.getSimState() != TelephonyManager.SIM_STATE_ABSENT) {

                countryId = manager.getSimCountryIso().toUpperCase();

            } else {


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mLlTopNextEvent = (LinearLayout) findViewById(R.id.ll_next);
        //mLlTopNextEvent.setOnClickListener(onClick());

        mButtonNext = (Button) findViewById(R.id.btn_next);
        // mButtonNext.setOnClickListener(this);

        mEtMobileNumber = (EditText) findViewById(R.id.et_mobile_number);
        mEtMobileNumber.requestFocus();

        mEtCountryCode = (EditText) findViewById(R.id.et_country_code);
        //  mEtCountryCode.setText(countryId);

    }

    @Override
    protected void setDataInViewObjects() {


        if (Validation.isValidMobile(this, mEtCountryCode, mEtMobileNumber)) {
            Intent in = new Intent(EnterMobileActivity.this, OtpVerificationActivity.class);
            startActivity(in);
            finish();
        }

//        if (countryCode.isEmpty()) {
//            Utility.showAlertDialog("Please Enter Country Code!", mEtCountryCode, this);
//            return;
//        } else if (mobileNumber.isEmpty()) {
//
//            Utility.showAlertDialog("Please Enter Mobile Number!", mEtMobileNumber, this);
//            return;
//        } else {
//
//            Intent in = new Intent(EnterMobileActivity.this, OtpVerificationActivity.class);
//            startActivity(in);
//            finish();
//        }
    }

    public void registerMobileNumber() {
        try {


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View v) {

    }

    ResponseHandlerListener responseHandlerListener = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebError error, ProgressDialog mProgressDialog) {
            // Always unregister
            WebNotificationManager.unRegisterResponseListener(responseHandlerListener);
            // check for error
            if (error==null){
                // start parsing
                if (result.getSuccess().equalsIgnoreCase("true"))
                startActivity(new Intent(EnterMobileActivity.this, OtpVerificationActivity.class));
                else{
                    // display error message
                }
            }else{
                // display error dialog
            }

            // Always close the progressdialog
            if (mProgressDialog!=null&&mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }
        }
    };

    public String createPayload(){
        JSONObject payloadJson = null;
        try {
            payloadJson = new JSONObject();
            payloadJson.put("deviceType", "android");
            payloadJson.put("deviceToken", "2vgfwufhyiewjfkhwbs");
            payloadJson.put("countryCode", "91");
            payloadJson.put("mobileNo", "9056428478");
            payloadJson.put("locale", "en");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return  payloadJson.toString();
    }

}
