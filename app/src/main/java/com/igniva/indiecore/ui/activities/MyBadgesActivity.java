package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.db.BadgesDb;
import com.igniva.indiecore.model.BadgesPojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.ui.adapters.BadgesAdapter;
import com.igniva.indiecore.ui.adapters.MyBadgesAdapter;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;
import com.igniva.indiecore.utils.imageloader.ImageLoader;

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
    Bundle bundle;
    ArrayList<BadgesPojo> selectedBadges = null;
    private BadgesDb db_badges;
    public static int active=0;
    public static String selectedBadgeId=null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybadges);
        initToolbar();
        setUpLayout();
        setDataInViewObjects();

        Log.e("",""+selectedBadges.toString());
        Log.e("",""+selectedBadges.size());
    }

    @Override
    protected void setUpLayout() {
        mTVMyBadegs = (TextView) findViewById(R.id.tv_my_badge);
        mTVBadgesMArket = (TextView) findViewById(R.id.tv_badge_market);
        mGlMAnager = new GridLayoutManager(MyBadgesActivity.this, 4);
        mRvMyBadges = (RecyclerView) findViewById(R.id.recycler_view_activity_mybadges);
        mRvMyBadges.setHasFixedSize(true);
        mRvMyBadges.setLayoutManager(mGlMAnager);
    }

    @Override
    protected void setDataInViewObjects() {
        selectedBadges = new ArrayList<BadgesPojo>();

          getMyBadges();



//        bundle = getIntent().getExtras();
//        selectedBadges = (ArrayList<BadgesPojo>) bundle.getSerializable(Constants.SELECTED_BADGEIDS);
//
//
        mMyBadgeAdapter = null;
        mRvMyBadges.setAdapter(mMyBadgeAdapter);
        //
        Log.e("","set in adapter"+selectedBadges.size());
        mMyBadgeAdapter = new MyBadgesAdapter(MyBadgesActivity.this, selectedBadges);
        mRvMyBadges.setAdapter(mMyBadgeAdapter);
    }

    @Override
    protected void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_my_badge:


                mTVMyBadegs.setTextColor(Color.parseColor("#FFFFFF"));
                mTVMyBadegs.setBackgroundColor(Color.parseColor("#4598E8"));

                mTVBadgesMArket.setTextColor(Color.parseColor("#4598E8"));
                mTVBadgesMArket.setBackgroundColor(Color.parseColor("#FFFFFF"));
                break;
            case R.id.tv_badge_market:

                mTVMyBadegs.setTextColor(Color.parseColor("#4598E8"));
                mTVMyBadegs.setBackgroundColor(Color.parseColor("#FFFFFF"));

                mTVBadgesMArket.setTextColor(Color.parseColor("#FFFFFF"));
                mTVBadgesMArket.setBackgroundColor(Color.parseColor("#4598E8"));
                break;

            default:

                break;
        }

    }

    public void  getMyBadges(){

        db_badges= new BadgesDb(this);
        selectedBadges=db_badges.retrieveSelectedBadges();
        Log.e("",""+selectedBadges.toString());
        Log.e("",""+selectedBadges.size());
    }

    /*
    * token, userId, badgeId, active (0/1)
    * to on/off the state of a badge
    *
    *
    * */
    public void onOffBadges() {
        try {
            String payload=createPayload();

            if(payload!=null){

                WebNotificationManager.registerResponseListener(responseListner);
                WebServiceClient.onOffMyBadges(this,payload,responseListner);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    ResponseHandlerListener responseListner = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            if(error==null){

            }

        }
    };

    private String createPayload() {
//        token, userId, badgeId, active (0/1)
        JSONObject payload = null;
        try {
            payload= new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this,PreferenceHandler.PREF_KEY_USER_TOKEN,""));
            payload.put(Constants.USERID,PreferenceHandler.readString(this,PreferenceHandler.PREF_KEY_USER_ID,""));
            payload.put(Constants.BADGEID,selectedBadgeId);
            payload.put(Constants.ACTIVE,active);

        }catch (Exception e){
            e.printStackTrace();
        }
        Log.e("","______"+payload.toString());
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

}
