package com.igniva.indiecore.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.model.BadgesPojo;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.Utility;


/**
 * Created by igniva-andriod-05 on 16/6/16.
 */
public class BadgeDetailActivity extends BaseActivity {
    Toolbar mToolbar;
    private ImageView mBadgeIcon;
    private TextView mBadgeName, mBadgeDesc;
    private ImageView mGetThisBadge;
    private String badgeId;
    BadgesPojo badge;
    Bundle bundle;
    private int index = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_detail);

        setUpLayout();
        setDataInViewObjects();
        initToolbar();

    }

    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            mToolbar.setNavigationIcon(R.drawable.backarrow_icon);
            TextView mTvTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
             mTvTitle.setText(badge.getName());
            //
//            //ImageView back=(ImageView) mToolbar.findViewById(R.id.iv_back);
//            back.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    finish();
//                }
//            });

            TextView mTvNext = (TextView) mToolbar.findViewById(R.id.toolbar_next);
            mTvNext.setVisibility(View.GONE);

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
        } catch (Exception e) {
            e.printStackTrace();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }

    }

    @Override
    protected void setUpLayout() {
        try {
            mBadgeIcon = (ImageView) findViewById(R.id.iv_badge_icon);
            mBadgeName = (TextView) findViewById(R.id.tv_badge_name);
            mBadgeDesc = (TextView) findViewById(R.id.tv_badge_desc);
            mGetThisBadge = (ImageView) findViewById(R.id.btn_get_this_badge);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void setDataInViewObjects() {
        try {
            // BadgesPojo badge = getIntent().getParcelableExtra("BadgeData");
            bundle = getIntent().getExtras();
            badge = (BadgesPojo) bundle.getSerializable("badgePojo");
            index = bundle.getInt("INDEX");


            Log.e("", "" + badge.toString());
            mBadgeName.setText(badge.getName());
            mBadgeDesc.setText(badge.getDescription());
            badgeId = badge.getBadgeId();

            Glide.with(this).load(badge.getIcon())
                    .thumbnail(1f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mBadgeIcon);




            if (index == 1) {
                mGetThisBadge.setVisibility(View.GONE);
             } else if(badge.getActive()==1) {
                mGetThisBadge.setVisibility(View.GONE);

            } else
            {
                    mGetThisBadge.setVisibility(View.VISIBLE);
                }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    protected void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_get_this_badge:

                try {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                    builder1.setMessage("Do you want to add this badge to your list");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            Intent intent = new Intent();
                            intent.putExtra(Constants.BADGEIDS, badgeId);
                            intent.putExtra(Constants.POSITION, bundle.getInt(Constants.POSITION));
                            setResult(2, intent);
                            finish();//finishing activity
                        }
                    });

                    builder1.setNegativeButton("No, Thanks", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Utility.showAlertDialogGetBadge("Do you want to add this badge to your list",this);


                break;
            default:
                break;
        }

    }
}
