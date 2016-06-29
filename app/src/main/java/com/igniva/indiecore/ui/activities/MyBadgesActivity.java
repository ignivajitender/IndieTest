package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.db.BadgesDb;
import com.igniva.indiecore.model.BadgesPojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.ui.adapters.BadgesMarketAdapter;
import com.igniva.indiecore.ui.adapters.MyBadgesAdapter;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 15/6/16.
 */
public class MyBadgesActivity extends BaseActivity {
    Toolbar mToolbar;
    private TextView mTVMyBadegs, mTVBadgesMArket;
    private RecyclerView mRvMyBadges;
    private GridLayoutManager mGlMAnager;
    MyBadgesAdapter mMyBadgeAdapter;
    BadgesMarketAdapter mBadgeMarketAdpter;
    Bundle bundle;
    ArrayList<BadgesPojo> mSelectedBadgesList = null;
    ArrayList<BadgesPojo> mBadgeMarketList = null;
    private BadgesDb db_badges;
    public static int active = 0;
    public static String selectedBadgeId = null;
    String LOG_TAG = "MyBadgeActivity";
    int buttonIndex = 1;
    public int mPageNumber = 1, mBadgeCount = 20, category = 0, mTotalBadgeCount = 0;

    int pastVisiblesItems, visibleItemCount, totalItemCount;
    boolean isLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybadges);

        mBadgeMarketList = new ArrayList<BadgesPojo>();
        mSelectedBadgesList = new ArrayList<BadgesPojo>();

        initToolbar();
        setUpLayout();
        setDataInViewObjects();

        Log.e("", "" + mSelectedBadgesList.toString());
        Log.e("", "" + mSelectedBadgesList.size());
    }


    @Override
    protected void setUpLayout() {
        mTVMyBadegs = (TextView) findViewById(R.id.tv_my_badge);
        mTVBadgesMArket = (TextView) findViewById(R.id.tv_badge_market);
        mGlMAnager = new GridLayoutManager(MyBadgesActivity.this, 4);
        mRvMyBadges = (RecyclerView) findViewById(R.id.recycler_view_activity_mybadges);
        mRvMyBadges.setHasFixedSize(true);
        mRvMyBadges.setLayoutManager(mGlMAnager);

        mRvMyBadges.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(LOG_TAG, " dx is " + dx + " dy is " + dy);

                if (buttonIndex == 2) {
                    if (dy > 0) //check for scroll down
                    {
                        visibleItemCount = mGlMAnager.getChildCount();
                        totalItemCount = mGlMAnager.getItemCount();
                        pastVisiblesItems = mGlMAnager.findFirstVisibleItemPosition();


                        if (!isLoading) {

                            Log.d(LOG_TAG, " visibleItemCount " + visibleItemCount + " pastVisiblesItems " + pastVisiblesItems + " totalItemCount " + totalItemCount);
                            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                isLoading = true;
                                //Do pagination.. i.e. fetch new data
                                if (mBadgeMarketList.size() < 1) {
                                    findViewById(R.id.btn_load_more).performClick();
                                }
                                if (mBadgeMarketList.size() < mTotalBadgeCount) {
                                    findViewById(R.id.btn_load_more).performClick();
                                }
                            }
                        }
                    }
                }
            }
        });

    }

    @Override
    protected void setDataInViewObjects() {

        if (buttonIndex == 1) {

            if (mSelectedBadgesList.size() < 1) {
                getMyBadges();
            }

            mMyBadgeAdapter = null;
            mRvMyBadges.setAdapter(mMyBadgeAdapter);
            //
            Log.e("", "set in adapter" + mSelectedBadgesList.size());
            mMyBadgeAdapter = new MyBadgesAdapter(MyBadgesActivity.this, mSelectedBadgesList);
            mRvMyBadges.setAdapter(mMyBadgeAdapter);
            // Hide Load More Button
            findViewById(R.id.btn_load_more).setVisibility(View.GONE);
        } else if (buttonIndex == 2) {

            findViewById(R.id.btn_load_more).setVisibility(View.VISIBLE);
            if (mBadgeMarketList.size() < 1) {
                // Service Call
                getBadgeMarketBadges();
            } else {
                // Display content only
                updateBadgesMarket();
            }


//            else if (mBadgeMarketList.size() < mTotalBadgeCount) {
//                // Service Call
//                getBadgeMarketBadges();
//            }

//            mBadgeMarketAdpter = null;
//            Log.e(LOG_TAG,"setting bin adpter"+mBadgeMarketList.size());
//            mBadgeMarketAdpter = new BadgesMarketAdapter(MyBadgesActivity.this, mBadgeMarketList);
//            mRvMyBadges.setAdapter(mBadgeMarketAdpter);

        }
    }

    @Override
    protected void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_my_badge:
                buttonIndex = 1;

                setDataInViewObjects();
                mTVMyBadegs.setTextColor(Color.parseColor("#FFFFFF"));
                mTVMyBadegs.setBackgroundColor(Color.parseColor("#4598E8"));

                mTVBadgesMArket.setTextColor(Color.parseColor("#4598E8"));
                mTVBadgesMArket.setBackgroundColor(Color.parseColor("#FFFFFF"));
                break;
            case R.id.tv_badge_market:
                buttonIndex = 2;
                setDataInViewObjects();
                mTVMyBadegs.setTextColor(Color.parseColor("#4598E8"));
                mTVMyBadegs.setBackgroundColor(Color.parseColor("#FFFFFF"));

                mTVBadgesMArket.setTextColor(Color.parseColor("#FFFFFF"));
                mTVBadgesMArket.setBackgroundColor(Color.parseColor("#4598E8"));
                break;
            case R.id.btn_load_more:
                mPageNumber += 1;
                getBadgeMarketBadges();

                break;
            default:

                break;
        }

    }

    public void getMyBadges() {
        db_badges = new BadgesDb(this);
        mSelectedBadgesList = db_badges.retrieveSelectedBadges();
        Log.e("", "" + mSelectedBadgesList.toString());
        Log.e("", "" + mSelectedBadgesList.size());
        if (mSelectedBadgesList.size() < 40) {
            int count = 40 - mSelectedBadgesList.size();
            for (int i = 0; i < count; i++)
                mSelectedBadgesList.add(new BadgesPojo());
        }
    }

    /*
    * token, userId, badgeId, active (0/1)
    * to on/off the state of a badge
    *
    *
    * */
    public void onOffBadges() {
        try {
            String payload = createPayload();
            if (payload != null) {
                WebNotificationManager.registerResponseListener(responseListner);
                WebServiceClient.onOffMyBadges(this, payload, responseListner);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ResponseHandlerListener responseListner = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            if (error == null) {

            }
            // Always close the progressdialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

        }
    };

    private String createPayload() {
//        token, userId, badgeId, active (0/1)
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.BADGEID, selectedBadgeId);
            payload.put(Constants.ACTIVE, active);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("", "______" + payload.toString());
        return payload.toString();
    }

    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar_with_icon);
            TextView mTvTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title_img);
            mTvTitle.setText(getResources().getString(R.string.my_badges));
            //
            ImageView mTvNext = (ImageView) mToolbar.findViewById(R.id.toolbar_img);
            mTvNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.showToastMessageShort(MyBadgesActivity.this, getResources().getString(R.string.coming_soon));
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


    void updateBadgesMarket() {
        mBadgeMarketAdpter = null;
        Log.e(LOG_TAG, "setting bin adpter" + mBadgeMarketList.size());
        mBadgeMarketAdpter = new BadgesMarketAdapter(MyBadgesActivity.this, mBadgeMarketList);
        mRvMyBadges.setAdapter(mBadgeMarketAdpter);
    }

    public void getBadgeMarketBadges() {
        try {
            String payload_badge_market = createPayload(mPageNumber, mBadgeCount, category);
            if (payload_badge_market != null) {
                WebNotificationManager.registerResponseListener(responseHandlerListner);
                WebServiceClient.getBadges(this, payload_badge_market, responseHandlerListner);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    ResponseHandlerListener responseHandlerListner = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            if (error == null) {
                if (result.getSuccess().equalsIgnoreCase("true")) {
                    mTotalBadgeCount = result.getTotal_badges();

                    if (mBadgeMarketList.size() < mTotalBadgeCount) {
                        mBadgeMarketList.addAll(result.getBadges());
                    }

                    if (mBadgeMarketList.size() >= mTotalBadgeCount) {
                        findViewById(R.id.btn_load_more).setVisibility(View.GONE);
                    }

                    Log.e(LOG_TAG, "" + mBadgeMarketList.size());
                    updateBadgesMarket();
                    isLoading = false;

                }
            }
            // Always close the progressdialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    };

    public String createPayload(int page, int limit, int category) {
        JSONObject payloadJson = null;
        try {
            payloadJson = new JSONObject();
            payloadJson.put(Constants.TOKEN, PreferenceHandler.readString(MyBadgesActivity.this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payloadJson.put(Constants.USERID, PreferenceHandler.readString(MyBadgesActivity.this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payloadJson.put(Constants.PAGE, page + "");
            payloadJson.put(Constants.LIMIT, limit + "");
            payloadJson.put(Constants.CATEGORY, category + "");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Log.d(LOG_TAG, "paload is " + payloadJson.toString());
        return payloadJson.toString();
    }


}
