package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
public class MyBadgesActivity extends BaseActivity implements View.OnClickListener {
    Toolbar mToolbar;
    private TextView mTVMyBadges, mTVBadgesMarket, mTvDone;
    private RecyclerView mRvMyBadges;
    private GridLayoutManager mGlManager;
    private ImageView mTvNext;
    private BadgesDb dbBadges;
    private ImageView onOffImage;
    private BadgesDb db_badges;
    private boolean IsBadgesRetrieved = true;
    public static int TotalBadgeCount = 10;
    public int mPageNumber = 1, mBadgeCount = 20, category = 0, mTotalBadgeCount = 0;
    boolean isLoading;
    int buttonIndex = 1;
    int mSelectedPosition = -1;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    MyBadgesAdapter mMyBadgeAdapter;
    BadgesMarketAdapter mBadgeMarketAdapter;
    Bundle bundle;
    ArrayList<BadgesPojo> mSelectedBadgesList = null;
    ArrayList<BadgesPojo> mBadgeMarketList = null;
    public ArrayList<BadgesPojo> mSelectedBadgeIds = new ArrayList<BadgesPojo>();
    String LOG_TAG = "MyBadgeActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybadges);

        mBadgeMarketList = new ArrayList<>();
        mSelectedBadgesList = new ArrayList<>();

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
            mTVMyBadges = (TextView) findViewById(R.id.tv_my_badge);
            mTVMyBadges.setOnClickListener(this);
            mTVBadgesMarket = (TextView) findViewById(R.id.tv_badge_market);
            mTVBadgesMarket.setOnClickListener(this);

            findViewById(R.id.btn_load_more).setOnClickListener(this);

            mGlManager = new GridLayoutManager(MyBadgesActivity.this, 4);
            mRvMyBadges = (RecyclerView) findViewById(R.id.recycler_view_activity_mybadges);
            mRvMyBadges.setHasFixedSize(true);
            mRvMyBadges.setLayoutManager(mGlManager);

            try {
                TotalBadgeCount = PreferenceHandler.readInteger(this, PreferenceHandler.TOTAL_BADGE_LIMIT, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }


            mRvMyBadges.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (buttonIndex == 2) {
                        if (dy > 0) //check for scroll down
                        {
                            visibleItemCount = mGlManager.getChildCount();
                            totalItemCount = mGlManager.getItemCount();
                            pastVisibleItems = mGlManager.findFirstVisibleItemPosition();

                            if (!isLoading) {

                                Log.d(LOG_TAG, " visibleItemCount " + visibleItemCount + " pastVisibleItems " + pastVisibleItems + " totalItemCount " + totalItemCount);
                                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        mBadgeMarketAdapter.onActivityResult(requestCode, resultCode, data);
//        mBadgesAdapter.notifyDataSetChanged();
    }


    @Override
    protected void setDataInViewObjects() {
        try {
            if (buttonIndex == 1) {

                mTvNext.setVisibility(View.VISIBLE);
                mTvDone.setVisibility(View.GONE);
                mTVMyBadges.setTextColor(Color.parseColor("#FFFFFF"));
                mTVMyBadges.setBackgroundColor(Color.parseColor("#4598E8"));

                mTVBadgesMarket.setTextColor(Color.parseColor("#4598E8"));
                mTVBadgesMarket.setBackgroundColor(Color.parseColor("#FFFFFF"));
                if (IsBadgesRetrieved) {
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
                mTVMyBadges.setTextColor(Color.parseColor("#4598E8"));
                mTVMyBadges.setBackgroundColor(Color.parseColor("#FFFFFF"));

                mTVBadgesMarket.setTextColor(Color.parseColor("#FFFFFF"));
                mTVBadgesMarket.setBackgroundColor(Color.parseColor("#4598E8"));
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_my_badge:
                buttonIndex = 1;
                setDataInViewObjects();
                break;
            case R.id.tv_badge_market:
                IsBadgesRetrieved = false;
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
            TotalBadgeCount = PreferenceHandler.readInteger(this, PreferenceHandler.TOTAL_BADGE_LIMIT, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            db_badges = new BadgesDb(this);
            mSelectedBadgesList = db_badges.retrieveSelectedBadges();
            Log.e("", "" + mSelectedBadgesList.toString());
            Log.e("", "" + mSelectedBadgesList.size());

            if (mSelectedBadgesList.size() < TotalBadgeCount) {
                TotalBadgeCount = TotalBadgeCount - mSelectedBadgesList.size();
                for (int i = 0; i < TotalBadgeCount; i++) {
                    BadgesPojo badgesPojo = new BadgesPojo();
                    mSelectedBadgesList.add(badgesPojo);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ResponseHandlerListener responseListner = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
                  WebNotificationManager.unRegisterResponseListener(responseListner);
            try {
                if (error == null) {

                    if (result.getSuccess().equalsIgnoreCase("true")) {


                        if (result.getActive() == 1) {
                            onOffImage.setImageResource(R.drawable.badge_on);
                            updateRecord();
                        } else {
                            onOffImage.setImageResource(R.drawable.badge_off);
                            updateRecord();
                        }
                    } else {
                        Utility.showAlertDialog(result.getError_text(), MyBadgesActivity.this,null);
                    }
                }
                // Always close the progress dialog
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    public void updateRecord() {
        try {
            dbBadges = new BadgesDb(this);
            BadgesPojo selectedBadge;
            selectedBadge = mSelectedBadgesList.get(mSelectedPosition);
            selectedBadge.getName();
            selectedBadge.getBadgeId();

            dbBadges.updateSingleRow(selectedBadge);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String createPayload(String badgeId, String badgestatus) {
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
                    IsBadgesRetrieved = true;
                    if (mSelectedBadgeIds.size() < 1) {
                        Utility.showAlertDialog(getResources().getString(R.string.select_a_badge), MyBadgesActivity.this,null);
                        return;
                    } else {
                        saveMySelectedBadges();
                    }
                }
            });
            TextView mTvTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title_img);
            mTvTitle.setText(getResources().getString(R.string.my_badges));
            //
            mTvNext = (ImageView) mToolbar.findViewById(R.id.toolbar_img);
            mTvNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyBadgesActivity.this, PremiumBadgesActivity.class);
                    startActivity(intent);
                }
            });
            ImageView mBack = (ImageView) mToolbar.findViewById(R.id.toolbar_img_left);
            mBack.setImageResource(R.drawable.backarrow_icon);
            mBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
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
            mBadgeMarketAdapter = null;
            mBadgeMarketAdapter = new BadgesMarketAdapter(MyBadgesActivity.this, mBadgeMarketList, mSelectedBadgeIds);
            mRvMyBadges.setAdapter(mBadgeMarketAdapter);
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
                    updateBadgesMarket();
                    isLoading = false;

                }else {
                    Utility.showAlertDialog(result.getError_text(), MyBadgesActivity.this,null);
                }
            }
            // Always close the progress dialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    };

    public String createPayload(int page, int limit, int category) {
        JSONObject payloadJson;
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
            onOffImage = view;
            if (mSelectedBadgesList.get(position).getActive() == 0) {
//                view.setImageResource(R.drawable.badge_on);
                mSelectedBadgesList.get(position).setActive(1);

                String payload = createPayload(mSelectedBadgesList.get(position).getBadgeId(), String.valueOf(mSelectedBadgesList.get(position).getActive()));
                try {
                    if (payload != null) {
//                        Log.e("on payload","++"+payload.toString());
                        mSelectedPosition = position;
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
                String payload = createPayload(mSelectedBadgesList.get(position).getBadgeId(), String.valueOf(mSelectedBadgesList.get(position).getActive()));
                try {
                    if (payload != null) {
                        Log.e("off payload", "++" + payload.toString());
                        mSelectedPosition = position;
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


    /*
* payload to save my selected badges at server
* PARAMETER: token, userId, type, badgeIds (should be in CSV format. for eg. 1,4,5,8)
*
* */
    public String createPayload() {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.TYPE, "general");
            String ids = "";
            for (int i = 0; i < mSelectedBadgeIds.size(); i++) {
                ids = ids + "," + mSelectedBadgeIds.get(i).getBadgeId();
            }
            ids = ids.substring(1);
            payload.put(Constants.BADGEIDS, ids);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Log.d(LOG_TAG, " payload is " + payload.toString());

        return payload.toString();
    }

    /*
    *
    * save my badges at server request
    *
    * */
    public void saveMySelectedBadges() {
        try {
            String payload = createPayload();
            payload = payload.replace("[", "");
            payload = payload.replace("]", "");
            if (payload != null) {
                // Web service Call
                // Step 1 - Register responsehandler
                Log.d(LOG_TAG, "--" + payload);
                WebNotificationManager.registerResponseListener(responseHandler);
                // Step 2 - Webservice Call
                WebServiceClient.userselectedBadges(MyBadgesActivity.this, payload, responseHandler);
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
                if (result.getSuccess().equals("true")) {
                    insertRecords();
                    PreferenceHandler.writeInteger(MyBadgesActivity.this, PreferenceHandler.TOTAL_BADGE_LIMIT, result.getUser().getBadgeLimit());
                    Utility.showToastMessageShort(MyBadgesActivity.this, "Badge(s) added successfully");

                    for (int i = 0; i < mBadgeMarketList.size(); i++) {

                        if (mBadgeMarketList.get(i).isSelected()){
                            mBadgeMarketList.remove(i);
                            i = i - 1;
                        }
                    }

                    mBadgeMarketAdapter.notifyDataSetChanged();

                } else {

                    Utility.showAlertDialogBuy(result.getError_text(), MyBadgesActivity.this);

                }

            } else {
                Utility.showAlertDialog("Some server error Occurred!", MyBadgesActivity.this,null);
            }
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    };

    public void insertRecords() {
        try {
            dbBadges = new BadgesDb(this);
            ArrayList<BadgesPojo> selectedBadgesList = new ArrayList<>();
            selectedBadgesList = mSelectedBadgeIds;
//        ArrayList<BadgesPojo> mTotalBadges=new ArrayList<BadgesPojo>();
            dbBadges.insertAllBadges(selectedBadgesList);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
