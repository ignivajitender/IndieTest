package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ThemedSpinnerAdapter;

import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;

import org.json.JSONObject;

/**
 * Created by igniva-andriod-05 on 11/7/16.
 */
public class RecommendBadgeActivity extends BaseActivity {

    private EditText mEtBadgeName;
    private Button mSubmit;
    private String badge_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomend_badge);
    }

    @Override
    protected void setUpLayout() {

        mEtBadgeName=(EditText) findViewById(R.id.et_recommended_badge_name);
        mSubmit=(Button) findViewById(R.id.btn_submit);

    }

    @Override
    protected void setDataInViewObjects() {

    }

    @Override
    protected void onClick(View v) {


    }

    public void recommend_a_badge(){
        badge_name =mEtBadgeName.getText().toString().trim();
try {

    if (badge_name.isEmpty()) {

        Utility.showAlertDialog(getResources().getString(R.string.empty_badge_name), RecommendBadgeActivity.this);
        return;
    } else {

        String payload = create_payload();
        if (!payload.isEmpty()) {

            WebNotificationManager.registerResponseListener(responseHandler);
            WebServiceClient.recommend_a_badge(this, payload, responseHandler);
        }

    }
      }catch (Exception e){
    e.printStackTrace();
     }
     }

    ResponseHandlerListener responseHandler = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(responseHandler);

            if(error!=null){

                Utility.showToastMessageLong(RecommendBadgeActivity.this,"Your badge recommendation submit successfully");
            }
        }
    };




    public String create_payload(){

        JSONObject payload=null;
        try {
            payload= new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this,PreferenceHandler.PREF_KEY_USER_TOKEN,""));
            payload.put(Constants.USERID,PreferenceHandler.readString(this,PreferenceHandler.PREF_KEY_USER_ID,""));
            payload.put(Constants.BADGE_NAME,badge_name);

        }catch (Exception e){
            e.printStackTrace();
        }
    return payload.toString();
    }
}
