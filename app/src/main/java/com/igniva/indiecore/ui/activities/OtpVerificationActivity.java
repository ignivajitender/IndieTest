package com.igniva.indiecore.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.igniva.indiecore.R;

/**
 * Created by igniva-andriod-05 on 3/6/16.
 */
public class OtpVerificationActivity extends BaseActivity {

    public static EditText mOtpField;
    private Button  mSubmitOtp;
    public static String receivedOtp="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verification_activity);

        setUpLayout();
    }

    @Override
    protected void setUpLayout() {

        mOtpField=(EditText) findViewById(R.id.et_verification_code);
        mSubmitOtp=(Button) findViewById(R.id.btn_submit);


    }

    @Override
    protected void onResume() {
        super.onResume();

        setDataInViewObjects();

    }

    @Override
    protected void setDataInViewObjects() {

        try {

            mOtpField.setText(receivedOtp);

            mOtpField.setSelection(receivedOtp.length());

        } catch (Exception e){

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

    public  void updateOtp(){

        setDataInViewObjects();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_submit:
               startActivity(new Intent(OtpVerificationActivity.this,CreateProfileActivity.class));
                break;
            default:
                break;
        }

    }
}