package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
public class RecommendBadgeActivity extends BaseActivity implements View.OnClickListener{
    Toolbar mToolbar;
    private EditText mEtBadgeName;
    private Button mSubmit;
    private String badge_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomend_badge);
        initToolbar();
        setUpLayout();
    }

    @Override
    protected void setUpLayout() {

        mEtBadgeName = (EditText) findViewById(R.id.et_recommended_badge_name);
       mSubmit = (Button) findViewById(R.id.btn_submit);
        mSubmit.setOnClickListener(this);

    }

    @Override
    protected void setDataInViewObjects() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                recommend_a_badge();
                break;
            default:
                break;
        }

    }

    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            mToolbar.setNavigationIcon(R.drawable.backarrow_icon);
            TextView mTvTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
            mTvTitle.setText(getResources().getString(R.string.reccomend_badge));
            //

            TextView mTvNext = (TextView) mToolbar.findViewById(R.id.toolbar_next);
            mTvNext.setVisibility(View.GONE);




            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);



            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }

    }

    public void recommend_a_badge() {
        badge_name = mEtBadgeName.getText().toString().trim();
        try {

            if (badge_name.isEmpty()) {

                Utility.showAlertDialog(getResources().getString(R.string.empty_badge_name), RecommendBadgeActivity.this,null);
                return;
            } else {

                String payload = create_payload();
                if (!payload.isEmpty()) {

                    WebNotificationManager.registerResponseListener(responseHandler);
                    WebServiceClient.recommend_a_badge(this, payload, responseHandler);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ResponseHandlerListener responseHandler = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(responseHandler);

            if (error == null) {
                if (result.getSuccess().equalsIgnoreCase("true")) {

                    Utility.showToastMessageLong(RecommendBadgeActivity.this, "Your badge recommendation submit successfully");
                }else {
                    Utility.showToastMessageLong(RecommendBadgeActivity.this, "Some error occurred please try later");
                }
            }
            // Always close the progressdialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    };

    public String create_payload() {

        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.BADGE_NAME, badge_name);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return payload.toString();
    }
}
