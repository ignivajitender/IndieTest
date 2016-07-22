package com.igniva.indiecore.ui.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.igniva.indiecore.R;
import com.igniva.indiecore.model.PremiumBadgePojo;
import com.igniva.indiecore.ui.adapters.PremiumBadgesAdapter;
import com.igniva.indiecore.utils.Log;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-05 on 21/7/16.
 */
public class PremiumBadgesActivity extends BaseActivity {

     private ArrayList<PremiumBadgePojo> mPremiumBadgesList;
    private PremiumBadgesAdapter premiumBadgesAdapter;
    PremiumBadgePojo premiumBadgePojo;
    private Toolbar mToolbar;
    private RecyclerView mRvPremiumBadges;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_badges);
        initToolbar();
        setUpLayout();
    }


    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar_premium_badges);
            mToolbar.setNavigationIcon(R.drawable.backarrow_icon);

            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
           TextView mTvTitle = (TextView) mToolbar.findViewById(R.id.toolbar_next);
            mTvTitle.setVisibility(View.GONE);
            ImageView mToolBArImageBtn=(ImageView)mToolbar.findViewById(R.id.toolbar_img);
            TextView title=(TextView) mToolbar.findViewById(R.id.toolbar_title);
            title.setText(getResources().getString(R.string.premium_badges));
            mToolBArImageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent(PremiumBadgesActivity.this,MyBadgesActivity.class);
                    startActivity(intent);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void setUpLayout() {

        mRvPremiumBadges=(RecyclerView) findViewById(R.id.rv_premium_badges);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvPremiumBadges.setLayoutManager(layoutManager);

        mPremiumBadgesList= new ArrayList<PremiumBadgePojo>();
        for (int i=0;i<7;i++){
            premiumBadgePojo= new PremiumBadgePojo();
            premiumBadgePojo.setBadgeIcon(getResources().getDrawable(R.drawable.adopted_icon,null));
            premiumBadgePojo.setBadgeName("Albino");
            premiumBadgePojo.setBadgePrice("$22.00");
            mPremiumBadgesList.add(premiumBadgePojo);
        }

        setDataInViewObjects();

    }

    @Override
    protected void setDataInViewObjects() {

        try {

            Log.e("premium",""+mPremiumBadgesList.size());
            premiumBadgesAdapter=null;
            premiumBadgesAdapter= new PremiumBadgesAdapter(this,mPremiumBadgesList);
            mRvPremiumBadges.setAdapter(premiumBadgesAdapter);

        }catch (Exception e){

            e.printStackTrace();
        }

    }

    @Override
    protected void onClick(View v) {

    }
}
