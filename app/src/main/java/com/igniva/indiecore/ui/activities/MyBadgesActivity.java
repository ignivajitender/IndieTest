package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.OnCardClickListner;
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
    private TextView mTVMyBadegs, mTVBadgesMArket, mTvDone;
    private RecyclerView mRvMyBadges;
    private GridLayoutManager mGlMAnager;
    private ImageView mTvNext;
    private BadgesDb dbBadges;
    private ImageView onOffImage;
    private BadgesDb db_badges;
    public  int active = 0;
    public  String selectedBadgeId = null;
    public int mPageNumber = 1, mBadgeCount = 20, category = 0, mTotalBadgeCount = 0;
    boolean isLoading;
    int buttonIndex = 1;
    int mSelectedPostion=-1;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    MyBadgesAdapter mMyBadgeAdapter;
    BadgesMarketAdapter mBadgeMarketAdpter;
    Bundle bundle;
    ArrayList<BadgesPojo> mSelectedBadgesList = null;
    ArrayList<BadgesPojo> mBadgeMarketList = null;
    String LOG_TAG = "MyBadgeActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybadges);

        mBadgeMarketList = new ArrayList<BadgesPojo>();
        mSelectedBadgesList = new ArrayList<BadgesPojo>();

        initToolbar();
        setUpLayout();
        if (getIntent().hasExtra("CLICKED")) {
            buttonIndex = 2;
        }
        setDataInViewObjects();

    }


    @Override
    protected void setUpLayout() {

        try {
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
            //
            mRvMyBadges.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                    return false;
                }

                @Override
                public void onTouchEvent(RecyclerView rv, MotionEvent e) {

                    rv.findViewById(R.id.iv_badge_on_off);
                }

                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        buttonIndex = 2;
        setDataInViewObjects();
    }

    @Override
    protected void setDataInViewObjects() {
        try {
            if (buttonIndex == 1) {

                mTvNext.setVisibility(View.VISIBLE);
                mTvDone.setVisibility(View.GONE);
                mTVMyBadegs.setTextColor(Color.parseColor("#FFFFFF"));
                mTVMyBadegs.setBackgroundColor(Color.parseColor("#4598E8"));

                mTVBadgesMArket.setTextColor(Color.parseColor("#4598E8"));
                mTVBadgesMArket.setBackgroundColor(Color.parseColor("#FFFFFF"));
                if (mSelectedBadgesList.size() < 1) {
                    getMyBadges();
                }

                mMyBadgeAdapter = null;
                mRvMyBadges.setAdapter(mMyBadgeAdapter);
                //
                Log.e("", "set in adapter" + mSelectedBadgesList.size());
                mMyBadgeAdapter = new MyBadgesAdapter(MyBadgesActivity.this, mSelectedBadgesList, onCardClickListner);
                mRvMyBadges.setAdapter(mMyBadgeAdapter);
//                mMyBadgeAdapter.
                // Hide Load More Button
                findViewById(R.id.btn_load_more).setVisibility(View.GONE);
            } else if (buttonIndex == 2) {
                mTvNext.setVisibility(View.GONE);
                mTvDone.setVisibility(View.VISIBLE);
                mTVMyBadegs.setTextColor(Color.parseColor("#4598E8"));
                mTVMyBadegs.setBackgroundColor(Color.parseColor("#FFFFFF"));

                mTVBadgesMArket.setTextColor(Color.parseColor("#FFFFFF"));
                mTVBadgesMArket.setBackgroundColor(Color.parseColor("#4598E8"));
                //findViewById(R.id.btn_load_more).setVisibility(View.VISIBLE);
                if (mBadgeMarketList.size() < 1) {
                    // Service Call
                    getBadgeMarketBadges();
                } else {
                    // Display content only
                    updateBadgesMarket();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_my_badge:
                buttonIndex = 1;


                setDataInViewObjects();

                break;
            case R.id.tv_badge_market:
                buttonIndex = 2;

                setDataInViewObjects();

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
        try {
            db_badges = new BadgesDb(this);
            mSelectedBadgesList = db_badges.retrieveSelectedBadges();
            Log.e("", "" + mSelectedBadgesList.toString());
            Log.e("", "" + mSelectedBadgesList.size());
            if (mSelectedBadgesList.size() < 10) {
                int count = 10 - mSelectedBadgesList.size();
                for (int i = 0; i < count; i++) {
                    BadgesPojo badgesPojo = new BadgesPojo();

                    mSelectedBadgesList.add(badgesPojo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * token, userId, badgeId, active (0/1)
    * to on/off the state of a badge
    *
    *
    * */
//    public void onOffBadges() {
//        try {
//
//            if (payload != null) {
//                WebNotificationManager.registerResponseListener(responseListner);
//                WebServiceClient.onOffMyBadges(this, payload, responseListner);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    ResponseHandlerListener responseListner = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            if (error == null) {

            if(result.getSuccess().equalsIgnoreCase("true")){


                if(result.getActive()==1){
                    onOffImage.setImageResource(R.drawable.badge_on);
                    updateRecord();


                }else {
                    onOffImage.setImageResource(R.drawable.badge_off);
                    updateRecord();
                }



              }else {
                Utility.showToastMessageLong(MyBadgesActivity.this,"Some error occurred.Please try later");
            }
            }
            // Always close the progressdialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

        }
    };


    public void updateRecord(){
        try {
            dbBadges = new BadgesDb(this);
            BadgesPojo selectedBadge;

            selectedBadge = mSelectedBadgesList.get(mSelectedPostion);
          Log.d(LOG_TAG,"ACtive or not"+selectedBadge.getActive());
            selectedBadge.getName();
            selectedBadge.getBadgeId();

            dbBadges.updateSingleRow(selectedBadge);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String createPayload(String badgeId,String badgestatus) {
//        token, userId, badgeId, active (0/1)
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.BADGEID, badgeId);
            payload.put(Constants.ACTIVE, badgestatus);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("", "______" + payload.toString());
        return payload.toString();
    }

    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar_with_icon);

            mTvDone = (TextView) findViewById(R.id.toolbar_done);
            mTvDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.showToastMessageShort(MyBadgesActivity.this, getResources().getString(R.string.coming_soon));

                }
            });
            TextView mTvTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title_img);
            mTvTitle.setText(getResources().getString(R.string.my_badges));
            //
            mTvNext = (ImageView) mToolbar.findViewById(R.id.toolbar_img);
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
        try {
            mBadgeMarketAdpter = null;
            Log.e(LOG_TAG, "setting bin adpter" + mBadgeMarketList.size());
            mBadgeMarketAdpter = new BadgesMarketAdapter(MyBadgesActivity.this, mBadgeMarketList);
            mRvMyBadges.setAdapter(mBadgeMarketAdpter);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            WebNotificationManager.unRegisterResponseListener(responseHandlerListner);
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


    OnCardClickListner onCardClickListner = new OnCardClickListner() {
        @Override
        public void onCardClicked(ImageView view, int position) {
//            Utility.showToastMessageShort(MyBadgesActivity.this, "Position is " + position);
            onOffImage=view;
            if (mSelectedBadgesList.get(position).getActive() == 0) {
//                view.setImageResource(R.drawable.badge_on);
                mSelectedBadgesList.get(position).setActive(1);

                String payload = createPayload(mSelectedBadgesList.get(position).getBadgeId(),String.valueOf(mSelectedBadgesList.get(position).getActive()));
                try {

                    if (payload != null) {
//                        Log.e("on payload","++"+payload.toString());
                         mSelectedPostion=position;
                        mSelectedBadgesList.get(position).setActive(1);
                        WebNotificationManager.registerResponseListener(responseListner);
                        WebServiceClient.onOffMyBadges(MyBadgesActivity.this, payload, responseListner);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
//                view.setImageResource(R.drawable.badge_off);
                mSelectedBadgesList.get(position).setActive(0);
                String payload = createPayload(mSelectedBadgesList.get(position).getBadgeId(),String.valueOf(mSelectedBadgesList.get(position).getActive()));
                try {

                    if (payload != null) {
                        Log.e("off payload","++"+payload.toString());
                        mSelectedPostion=position;
                        mSelectedBadgesList.get(position).setActive(0);
                        WebNotificationManager.registerResponseListener(responseListner);
                        WebServiceClient.onOffMyBadges(MyBadgesActivity.this, payload, responseListner);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

}
