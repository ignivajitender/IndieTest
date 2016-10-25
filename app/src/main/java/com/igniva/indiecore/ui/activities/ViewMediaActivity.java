package com.igniva.indiecore.ui.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.utils.Constants;

/**
 * Created by igniva-andriod-05 on 21/10/16.
 */
public class ViewMediaActivity extends BaseActivity {
   private Toolbar mToolbar;
    private String mMediaPath;
    private ImageView mMediaImage;
    private TextView mMediaNotAvailable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_media);
        initToolbar();
        setUpLayout();
        setDataInViewObjects();
    }


    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar_media_activity);
            TextView mTvTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
            mTvTitle.setVisibility(View.GONE);
            //
            TextView mTvNext = (TextView) mToolbar.findViewById(R.id.toolbar_next);
            mTvNext.setText("DONE");
            mTvNext.setOnClickListener(new View.OnClickListener() {
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
    @Override
    protected void setUpLayout() {

        try {
            mMediaImage = (ImageView) findViewById(R.id.iv_media_image);
            mMediaPath = getIntent().getStringExtra(Constants.MEDIA_PATH);
            mMediaNotAvailable=(TextView) findViewById(R.id.tv_media_not_available);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setDataInViewObjects() {
        try {
            if ( mMediaPath!=null && !mMediaPath.isEmpty()) {
                Uri uri = Uri.parse(mMediaPath);
                Glide.with(this).load(uri.getPath())
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mMediaImage);
                mMediaImage.setRotation((float) 90.0);
            }else {
                mMediaImage.setVisibility(View.GONE);
                mMediaNotAvailable.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
