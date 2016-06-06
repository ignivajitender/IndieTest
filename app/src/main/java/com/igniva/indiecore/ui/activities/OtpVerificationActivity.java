package com.igniva.indiecore.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.igniva.indiecore.R;
import com.igniva.indiecore.utils.Utility;

/**
 * Created by igniva-andriod-05 on 3/6/16.
 */
public class OtpVerificationActivity extends BaseActivity implements View.OnClickListener{

    private EditText mOtpField;
    private Button  mSubmitOtp;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
     requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verification_activity);
    }

    @Override
    protected void setUpLayout() {

        mOtpField=(EditText) findViewById(R.id.et_verification_code);

        mSubmitOtp=(Button) findViewById(R.id.btn_submit);
        mSubmitOtp.setOnClickListener(this);

    }

    @Override
    protected void setDataInViewObjects() {
        String otp= mOtpField.getText().toString();
        if(otp.isEmpty()){

            Utility.showAlertDialog("Please enter OTP",this);
            return;
        } else {

        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_submit:
                setDataInViewObjects();
                break;
            default:
                break;
        }

    }
}
