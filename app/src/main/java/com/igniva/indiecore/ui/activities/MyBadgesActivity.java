package com.igniva.indiecore.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.igniva.indiecore.R;
import com.igniva.indiecore.utils.Utility;

/**
 * Created by igniva-andriod-11 on 15/6/16.
 */
public class MyBadgesActivity extends BaseActivity{
    Toolbar mToolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybadges);
        initToolbar();
    }

    @Override
    protected void setUpLayout() {


    }

    @Override
    protected void setDataInViewObjects() {

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
            mTvNext.setText("Done");
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
