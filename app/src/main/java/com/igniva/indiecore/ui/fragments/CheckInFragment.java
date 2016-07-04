package com.igniva.indiecore.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.igniva.indiecore.R;

/**
 * Created by igniva-andriod-05 on 1/7/16.
 */
public class CheckInFragment extends BaseFragment {

    LinearLayout mLlMapContainer,mLlSearchContainer;
    TextView mTvTrending,mTvNearby,mTvFind;
    View rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         rootView = inflater.inflate(R.layout.fragment_checkin, container, false);
        setUpLayout();
        setDataInViewObjects();
        return rootView;
    }

    @Override
    protected void setUpLayout() {
        mLlMapContainer=(LinearLayout)rootView.findViewById(R.id.ll_maps_container);
        mLlSearchContainer=(LinearLayout)rootView.findViewById(R.id.ll_Search);
        mTvTrending=(TextView) rootView.findViewById(R.id.tv_trending);
        mTvNearby=(TextView) rootView.findViewById(R.id.tv_nearby);
        mTvFind=(TextView) rootView.findViewById(R.id.tv_find);

        mTvTrending.setOnClickListener(onClickListener);
        mTvNearby.setOnClickListener(onClickListener);
        mTvFind.setOnClickListener(onClickListener);

        updateTrendingUI();
    }

    @Override
    protected void setDataInViewObjects() {

    }

    @Override
    protected void onClick(View v) {

    }


    private View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
      switch (v.getId()){
          case R.id.tv_trending:
              updateTrendingUI();
              break;
          case R.id.tv_nearby:
              updateNearbygUI();
              break;
          case R.id.tv_find:
              updateFindUI();
              break;

          default:
              break;

      }

        }
    };


    void updateTrendingUI(){
        mLlMapContainer.setVisibility(View.VISIBLE);
        mLlSearchContainer.setVisibility(View.GONE);

    }
    void updateNearbygUI(){
        mLlMapContainer.setVisibility(View.VISIBLE);
        mLlSearchContainer.setVisibility(View.GONE);
    }
    void updateFindUI(){
        mLlMapContainer.setVisibility(View.GONE);
        mLlSearchContainer.setVisibility(View.VISIBLE);
    }
}
