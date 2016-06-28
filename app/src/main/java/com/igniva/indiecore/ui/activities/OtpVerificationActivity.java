package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * Created by igniva-andriod-05 on 3/6/16.
 */
public class OtpVerificationActivity extends BaseActivity {

    public static EditText mOtpField;
    private Button mSubmitOtp;
    public static String receivedOtp = "";
    public int callId = 0;
    private String mMobileNumber, mCountryId;
    private int numberLength;
    MyReceiver receiver;
    ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_otp_verification);

        setUpLayout();
        //receiver = new MyReceiver(); // Create the receiver
        // registerReceiver(receiver, new IntentFilter("some.action")); // Register receiver


    }

    @Override
    protected void setUpLayout() {
        try {
            mOtpField = (EditText) findViewById(R.id.et_verification_code);
            mSubmitOtp = (Button) findViewById(R.id.btn_submit);
            Bundle bundle = getIntent().getExtras();
            mMobileNumber = bundle.getString(Constants.MOBILE_NO);
            mCountryId = bundle.getString(Constants.COUNTRY_CODE);
            numberLength = bundle.getInt(Constants.NUMBER_LENGTH);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setDataInViewObjects();

    }

    /**
     * To validate otp field and then execute the api call
     *
     * @param
     * @param
     */

    public void validateAndVerifyOtp() {
        try {
            String otp = mOtpField.getText().toString().trim();
            callId = 1;

            JSONObject payload = null;
//        countryCode":"91","mobileNo":"9816428478","code":"3856","locale":"en"
            try {
                payload = new JSONObject();
                payload.put(Constants.COUNTRY_CODE, mCountryId);
                payload.put(Constants.MOBILE_NO, mMobileNumber);
                payload.put(Constants.OTP_CODE, otp);
                payload.put(Constants.LOCALE, "en");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (otp.length() == 0) {

                Utility.showAlertDialog(Constants.OTP_NOT_VALID, this);
                return;
            } else {

                WebNotificationManager.registerResponseListener(responseHandlerListener);

                WebServiceClient.verifyOtp(this, payload.toString(), responseHandlerListener);

                Log.e("OtpVerificationActivity", "--------" + payload.toString());
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * To resend otp to the user
     *
     * @param
     * @param
     */


    public void resendOtp() {

        JSONObject payload = null;
        callId = 2;

//        {"countryCode":"91","mobileNo":"9816428478"}
        try {
            payload = new JSONObject();
            payload.put(Constants.COUNTRY_CODE, mCountryId);
            payload.put(Constants.MOBILE_NO, mMobileNumber);

            WebNotificationManager.registerResponseListener(responseHandlerListener);

            WebServiceClient.resendOTP(this, payload.toString(), responseHandlerListener);

            Log.e("ResendOtp payload", "------------" + payload.toString());

        } catch (Exception e) {

            e.printStackTrace();
        }

//        {"countryCode":"91","mobileNo":"9816428478"}


    }

    ResponseHandlerListener responseHandlerListener = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            // Always unregister
            WebNotificationManager.unRegisterResponseListener(responseHandlerListener);
            // check for error
            try {

                if (error == null) {

                    // start parsing
                    if (result.getSuccess().equalsIgnoreCase("true") && callId == 1) {

//                    {"token":"HtliAtfS4QTqoCedriJAZi_zOLE5dAfFhl36qwGoa3w","userId":"x6hzRHQYwTKESKLaj",
//                            "newUser":false,"profile":{},"badges":[],"badgeLimit":10,"success":true,"error":null}

                        PreferenceHandler.writeString(OtpVerificationActivity.this, PreferenceHandler.PREF_KEY_USER_TOKEN, result.getToken());
                        PreferenceHandler.writeString(OtpVerificationActivity.this, PreferenceHandler.PREF_KEY_USER_ID, result.getUserId());

                        Intent in = new Intent(OtpVerificationActivity.this, CreateProfileActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.FIRSTNAME, "");
                        bundle.putString(Constants.LASTNAME, "");
                        bundle.putString(Constants.DOB, "");
                        bundle.putString(Constants.DESCRIPTION, "");
                        bundle.putString(Constants.GENDER, "");
                        bundle.putString(Constants.PROFILEPIC, "");
                        bundle.putString(Constants.COVERPIC, "");
                        bundle.putInt(Constants.NUMBER_LENGTH, numberLength);
                        bundle.putString(Constants.COUNTRY_CODE, mCountryId);
                        in.putExtras(bundle);
                        startActivity(in);
                    } else if (result.getSuccess().equalsIgnoreCase("true") && callId == 2) {

                        Toast.makeText(OtpVerificationActivity.this, "Verification code sent successfully", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // display error dialog

                    if (callId == 1) {

                        Toast.makeText(OtpVerificationActivity.this, "You have entered incorrect OTP, Please try again", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(OtpVerificationActivity.this, "Error sending OTP, Please try again", Toast.LENGTH_LONG).show();

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Some unknown error occurred, Please try again later.", Toast.LENGTH_LONG).show();

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
            mOtpField.setText(receivedOtp);
            mOtpField.setSelection(receivedOtp.length());
        } catch (Exception e) {
            e.printStackTrace();
        }

//
//        String otp= mOtpField.getText().toString();
//        if(otp.isEmpty()){
//
//            Utility.showAlertDialog("Please enter OTP",this);
//            return;
//        } else {
//
//        }

    }

    public void updateOtp() {
        setDataInViewObjects();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_submit:
                validateAndVerifyOtp();
                break;

            case R.id.tv_resend:
                resendOtp();

                break;
            default:
                break;
        }

    }

    public class MyReceiver extends BroadcastReceiver {

        //      private final Handler handler; // Handler used to execute code on the UI thread

        public MyReceiver() {
            // this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            // ProgressDialog progressDialog=null;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog = ProgressDialog.show(OtpVerificationActivity.this, "", OtpVerificationActivity.this
                                    .getResources().getString(R.string.please_wait), true,
                            false);

                }
            });
            // Post the UI updating code to our Handler
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(context, "Toast from local broadcast receiver", Toast.LENGTH_SHORT).show();
                    try {
                        if (mOtpField != null) {
                            mOtpField.setText(intent.getStringExtra(Constants.OTP_CODE));
                            mOtpField.setSelection(mOtpField.getText().length());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                }
            }, 1000);


//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(context, "Toast from broadcast receiver", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
        }

    }


}