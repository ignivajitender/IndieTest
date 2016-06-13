package com.igniva.indiecore.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.igniva.indiecore.R;

public class SyncContactsActivity extends  BaseActivity implements View.OnClickListener{

    Toolbar mToolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_contacts);
        initToolbar();
    }

    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            TextView mTvTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
            mTvTitle.setText(getResources().getString(R.string.contact_number));
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


    @Override
    protected void setUpLayout() {

    }

    @Override
    protected void setDataInViewObjects() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_skip_step:
                startActivity(new Intent(this,BadgesActivity.class));
                break;
        }
    }
}
