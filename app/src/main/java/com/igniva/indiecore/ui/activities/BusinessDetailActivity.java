package com.igniva.indiecore.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.igniva.indiecore.R;

/**
 * Created by igniva-andriod-05 on 7/7/16.
 */
public class BusinessDetailActivity extends BaseActivity {

    private TextView mTvAbout,mTvTelephoneOne,mTvTelephoneTwo,mTvOpeningHours,mTvWebsite;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_detail);
    }

    @Override
    protected void setUpLayout() {

        mTvAbout=(TextView) findViewById(R.id.tv_about);
        mTvTelephoneOne=(TextView) findViewById(R.id.tv_telephone_one);
        mTvTelephoneTwo=(TextView) findViewById(R.id.tv_telephone_two);
        mTvOpeningHours=(TextView) findViewById(R.id.tv_opening_hours);
        mTvWebsite=(TextView) findViewById(R.id.tv_website);

    }

    @Override
    protected void setDataInViewObjects() {


    }

    @Override
    protected void onClick(View v) {

    }
}
