package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.BadgesPojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.ui.adapters.BadgesAdapter;
import com.igniva.indiecore.utils.Log;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by igniva-andriod-11 on 8/6/16.
 */
public class BadgesActivity extends BaseActivity {
    String LOG_TAG="BadgesActivity";
    Toolbar mToolbar;
    private GridLayoutManager mGlayout;
    RecyclerView mRvBadges;
    ArrayList<BadgesPojo> mBadgesList = new ArrayList<BadgesPojo>();
    LinearLayout mllNext, mLlPrevious;
    int pageNumber = 1, badgeCount = 20, category = 0,mTotalBadgeCount=0;
    BadgesAdapter mBadgesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badges);
        //
        initToolbar();
        setUpLayout();
        //
        String payload = createPayload(pageNumber, badgeCount, category);
        if (payload != null) {
            // Web service Call
            // Step 1 - Register responsehandler
            WebNotificationManager.registerResponseListener(responseHandlerListener);
            // Step 2 - Webservice Call
            WebServiceClient.getBadges(BadgesActivity.this, payload, responseHandlerListener);
        }
    }

    @Override
    protected void setUpLayout() {

        mGlayout = new GridLayoutManager(BadgesActivity.this, 4);
        mRvBadges = (RecyclerView) findViewById(R.id.recycler_view);
        mllNext = (LinearLayout) findViewById(R.id.ll_next);
        mllNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNextBadges();
            }
        });
        mLlPrevious = (LinearLayout) findViewById(R.id.ll_previous);
        mLlPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePreviousBadges();
            }
        });
        //
        mRvBadges.setHasFixedSize(true);
        mRvBadges.setLayoutManager(mGlayout);
    }

    @Override
    protected void setDataInViewObjects() {
        try {
            mBadgesAdapter = null;
            mRvBadges.setAdapter(mBadgesAdapter);
            //
            mBadgesAdapter = new BadgesAdapter(BadgesActivity.this, mBadgesList, pageNumber*badgeCount,mTotalBadgeCount);
            mRvBadges.setAdapter(mBadgesAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onClick(View v) {


    }

    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            TextView mTvTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
            mTvTitle.setText(getResources().getString(R.string.badges));
            //
            TextView mTvNext = (TextView) mToolbar.findViewById(R.id.toolbar_next);
            mTvNext.setVisibility(View.GONE);
            //
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
    }


    public String createPayload(int page, int limit, int category) {
        JSONObject payloadJson = null;
        try {
            payloadJson = new JSONObject();
            payloadJson.put("token", "a5s3qIXm3qX-RSlbwZBlyRSOgTpCCSJc9gbvHxT5Vx8");
            payloadJson.put("userId", "Y77qed6yd7gzfZoXM");
            payloadJson.put("page", page + "");
            payloadJson.put("limit", limit + "");
            payloadJson.put("category", category + "");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Log.d(LOG_TAG,"paload is "+payloadJson.toString());
        return payloadJson.toString();
    }


    ResponseHandlerListener responseHandlerListener = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(responseHandlerListener);
            // check for error
            if (error == null) {
                // start parsing
                if (result.getSuccess().equalsIgnoreCase("true")) {
                    // display in grid
                    mTotalBadgeCount=result.getTotal_badges();
                    mBadgesList.addAll(result.getBadges());
                    setDataInViewObjects();
                } else {
                    // display error message
                }
            } else {
                // display error dialog
            }

            // Always close the progressdialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    };


    void updateNextBadges() {

        pageNumber = pageNumber + 1;
        badgeCount = badgeCount;

        String payload = createPayload(pageNumber, badgeCount, category);
        if (payload != null) {
            // Web service Call
            // Step 1 - Register responsehandler
            WebNotificationManager.registerResponseListener(responseHandlerListener);
            // Step 2 - Webservice Call
            WebServiceClient.getBadges(BadgesActivity.this, payload, responseHandlerListener);
        }

    }

    void updatePreviousBadges() {

        pageNumber = pageNumber - 1 ;
        if(pageNumber<1)
            pageNumber=1;
        setDataInViewObjects();

    }
}
