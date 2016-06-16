package com.igniva.indiecore.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.igniva.indiecore.R;
import com.igniva.indiecore.model.BadgesPojo;
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.imageloader.ImageLoader;

/**
 * Created by igniva-andriod-05 on 16/6/16.
 */
public class BadgeDetailActivity extends BaseActivity{
    Toolbar mToolbar;
    private ImageView mBadgeIcon;
    private TextView mBadgeName,mBadgeDesc;
    private Button mGetThisBadge;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_detail);

        initToolbar();
        setUpLayout();
        setDataInViewObjects();
    }
    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            TextView mTvTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
            mTvTitle.setText("Badge Name to set");
            //
            TextView mTvNext = (TextView) mToolbar.findViewById(R.id.toolbar_next);
            mTvNext.setVisibility(View.GONE);

            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }

    }
    @Override
    protected void setUpLayout() {
        mBadgeIcon=(ImageView) findViewById(R.id.iv_badge_icon);
        mBadgeName=(TextView) findViewById(R.id.tv_badge_name);
        mBadgeDesc=(TextView) findViewById(R.id.tv_badge_desc);
        mGetThisBadge=(Button) findViewById(R.id.btn_get_this_badge);

    }

    @Override
    protected void setDataInViewObjects() {
try {
    ImageLoader imageLoader=new ImageLoader(BadgeDetailActivity.this);

    BadgesPojo badge = getIntent().getParcelableExtra("BadgeData");

    Log.e("",""+badge.toString());
    mBadgeName.setText(badge.getName());
    mBadgeDesc.setText(badge.getDescription());
    imageLoader.DisplayImage(badge.getIcon(),mBadgeIcon);


}catch (Exception e){
    e.printStackTrace();
}



    }

    @Override
    protected void onClick(View v) {

    }
}
