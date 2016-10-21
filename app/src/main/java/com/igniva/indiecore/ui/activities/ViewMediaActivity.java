package com.igniva.indiecore.ui.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.igniva.indiecore.R;
import com.igniva.indiecore.utils.Constants;

/**
 * Created by igniva-andriod-05 on 21/10/16.
 */
public class ViewMediaActivity extends BaseActivity {
          private  String mMediaPath;
    private ImageView mMediaImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_media);
        setUpLayout();
        setDataInViewObjects();
    }

    @Override
    protected void setUpLayout() {

        try {
            mMediaImage=(ImageView) findViewById(R.id.iv_media_image);
            mMediaPath = getIntent().getStringExtra(Constants.MEDIA_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setDataInViewObjects() {

        if(!mMediaPath.isEmpty()){
            Uri uri=Uri.parse(mMediaPath);
            mMediaImage.setImageURI(uri);
        }

    }
}
