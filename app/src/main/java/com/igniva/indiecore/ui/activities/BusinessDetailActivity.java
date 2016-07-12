package com.igniva.indiecore.ui.activities;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.igniva.indiecore.R;
import com.igniva.indiecore.model.BusinessPojo;

/**
 * Created by igniva-andriod-05 on 7/7/16.
 */
public class BusinessDetailActivity extends BaseActivity implements OnMapReadyCallback {

    private TextView mTvTitle,mTvBUsinessName,mTvBusinessAddress,mTvAbout,mTvTelephoneOne,mTvTelephoneTwo,mTvOpeningHours,mTvWebsite;
    private ImageView mBUsinessImage,mIvBUsinessRating,mBusinessCoverPic;
    double lattitude=0;
    double longitude=0;
     Bundle bundle;
    Toolbar mToolbar;
    BusinessPojo businessPojo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_detail);
        initToolbar();

        setUpLayout();
        setDataInViewObjects();

    }

    @Override
    protected void setUpLayout() {
        mTvBUsinessName=(TextView) findViewById(R.id.tv_business_name);
        mTvBusinessAddress=(TextView) findViewById(R.id.tv_business_address);
        mTvAbout=(TextView) findViewById(R.id.tv_about);

        mBUsinessImage=(ImageView) findViewById(R.id.iv_business_image);
        mTvTelephoneOne=(TextView) findViewById(R.id.tv_telephone_one);
        mTvTelephoneTwo=(TextView) findViewById(R.id.tv_telephone_two);
        mTvOpeningHours=(TextView) findViewById(R.id.tv_opening_hours);
        mTvWebsite=(TextView) findViewById(R.id.tv_website);
        mBusinessCoverPic=(ImageView) findViewById(R.id.iv_cover_pic);
        mIvBUsinessRating=(ImageView) findViewById(R.id.iv_busiessrating);



    }

    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
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
            mTvTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title_img);

        } catch (Exception e) {
            e.printStackTrace();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
    }

    @Override
    protected void setDataInViewObjects() {


        bundle=getIntent().getExtras();
        businessPojo=(BusinessPojo) bundle.getSerializable("businessPojo");
        mTvBUsinessName.setText(businessPojo.getName());


        mTvBusinessAddress.setText(businessPojo.getLocation().getAddress().toString());

        Glide.with(this).load(businessPojo.getImage_url())
                .thumbnail(1f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mBUsinessImage);
        mTvTelephoneOne.setText(businessPojo.getPhone());
        mTvTelephoneTwo.setText(businessPojo.getDisplay_phone());
        mTvAbout.setText(businessPojo.getSnippet_text());

        Glide.with(this).load(businessPojo.getRating_img_url())
                .thumbnail(1f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mIvBUsinessRating);

        Glide.with(this).load(businessPojo.getImage_url())
                .thumbnail(1f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mBusinessCoverPic);

        lattitude=Double.parseDouble(businessPojo.getLocation().getCoordinate().getLatitude());
        longitude=Double.parseDouble(businessPojo.getLocation().getCoordinate().getLongitude());

        try {

            SupportMapFragment mapFragment =
                    (SupportMapFragment)this.getSupportFragmentManager().findFragmentById(R.id.map_business_detail);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    if (googleMap != null) {
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(lattitude,longitude
                        )).title("My Location"));

                        // We will provide our own zoom controls.
                        googleMap.getUiSettings().setZoomControlsEnabled(false);
                        // hide my location button
                        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        //
                        googleMap.setMyLocationEnabled(true);
                        // Show Sydney
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lattitude, longitude), 10));
                    }
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
        mTvTitle.setText(businessPojo.getName());

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            googleMap.addMarker(new MarkerOptions().position(new LatLng(lattitude,longitude
            )).title("My Location"));
        }
    }
    @Override
    protected void onClick(View v) {

    }
}
