package com.igniva.indiecore.ui.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.OnCardClickListner;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.BusinessPojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.ui.adapters.BusinessListAdapter;
import com.igniva.indiecore.ui.adapters.FindBusinessAdapter;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by igniva-andriod-05 on 1/7/16.
 */
public class CheckInFragment extends BaseFragment {

    private int sort = 1;
    private static final int limit = 20;
    private int page = 1;
    private int businessCount=0;
    private int totalBusinessCount=0;
    private TextView mTvTrending, mTvNearby, mTvFind, mTvSearch;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private EditText mEtSearch;
    private GridLayoutManager mGlManager;
    private LinearLayoutManager mLlManager;
    private boolean isLoading;
    private int buttonIndex=-1;
    private String LOG_TAG="CHATFRAGMENT";
    LinearLayout mLlMapContainer, mLlSearchContainer;
    ArrayList<BusinessPojo> mBusinessList;
    ArrayList<BusinessPojo> mFindBusinessResultList;
    ArrayList<BusinessPojo> mNearbyList;
    RecyclerView mRvBusinessGrid;
    View rootView;
    BusinessListAdapter mBusinessAdapter;
    FindBusinessAdapter mFindBusinessAdapter;
    ImageView mIvBusiness;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_checkin, container, false);
        setUpLayout();
        return rootView;
    }
    @Override
    protected void setUpLayout() {

        try {
            mBusinessList = new ArrayList<>();
            mFindBusinessResultList = new ArrayList<>();
            mGlManager = new GridLayoutManager(getActivity(), 3);
            mLlMapContainer = (LinearLayout) rootView.findViewById(R.id.ll_maps_container);
            mLlSearchContainer = (LinearLayout) rootView.findViewById(R.id.ll_Search);
            mTvTrending = (TextView) rootView.findViewById(R.id.tv_trending);
            mTvNearby = (TextView) rootView.findViewById(R.id.tv_nearby);
            mTvFind = (TextView) rootView.findViewById(R.id.tv_find);
            mEtSearch = (EditText) rootView.findViewById(R.id.et_search);
            mTvSearch = (TextView) rootView.findViewById(R.id.tv_search);
            mTvSearch.setOnClickListener(onClickListener);
            mTvTrending.setOnClickListener(onClickListener);
            mTvNearby.setOnClickListener(onClickListener);
            mTvFind.setOnClickListener(onClickListener);

            mRvBusinessGrid = (RecyclerView) rootView.findViewById(R.id.rv_business);
            mRvBusinessGrid.setLayoutManager(mGlManager);


            mRvBusinessGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    try{
                    if (buttonIndex != 3)
                        //check for scroll down
                        if (dy > 0) {
                            visibleItemCount = mGlManager.getChildCount();
                            totalItemCount = mGlManager.getItemCount();
                            pastVisibleItems = mGlManager.findFirstVisibleItemPosition();
                            if (!isLoading) {
                                Log.d(LOG_TAG, "lis size is " + mBusinessList.size() + " ======++++++ visibleItemCount " + visibleItemCount + " pastVisibleItems " + pastVisibleItems + " totalItemCount " + totalItemCount);
                                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                    isLoading = true;
                                    //Do pagination.. i.e. fetch new data
                                    if (mBusinessList.size() < 1) {
                                        page = 1;
                                        getBusinesses(limit, page);
                                    }
                                    if (mBusinessList.size() < totalBusinessCount) {
                                        page += 1;
                                        getBusinesses(limit, page);
                                    }
                                    if (buttonIndex == 2) {
                                        updateNearbyUI();
                                    }
                                }
                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            try {

                SupportMapFragment mapFragment =
                        (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        if (googleMap != null) {

//                            37.77493,-122.419415
                            // We will provide our own zoom controls.
                            googleMap.getUiSettings().setZoomControlsEnabled(false);
                            // hide my location button
                            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                            //
                            googleMap.setMyLocationEnabled(true);

                            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                            Criteria criteria = new Criteria();

                            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                            if (location != null)
                            {
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(location.getLatitude(), location.getLongitude()), 13));

                                googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()
                                )).title("My Location"));

                                Log.e("myCurrentLAtLong---","Latitude"+location.getLatitude()+"Longitude"+location.getLongitude());

                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                                        .zoom(10)                   // Sets the zoom
                                                        // Sets the orientation of the camera to eas
                                        // Sets the tilt of the camera to 30 degrees
                                        .build();                   // Creates a CameraPosition from the builder
                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                            }
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        getBusinesses(limit,page);
        updateTrendingUI();
    }


    @Override
    protected void setDataInViewObjects() {

    }

    public void getBusinesses(int limit,int page) {
        String payload = createPayload(limit,page);
        try {
            if (payload != null) {
                WebNotificationManager.registerResponseListener(responseHandler);
                WebServiceClient.getBusinessList(getActivity(), payload, responseHandler);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void findBusinesses() {
        String payload = createPayload(sort, limit, page);
        try {
            if (payload != null) {
                WebNotificationManager.registerResponseListener(responseHandlerFindBusiness);
                WebServiceClient.getBusinessList(getActivity(), payload, responseHandlerFindBusiness);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * get all  business response handler
    *
    * */
    ResponseHandlerListener responseHandler = new ResponseHandlerListener() {

        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            WebNotificationManager.unRegisterResponseListener(responseHandler);
            if (error == null) {
                try {
                    if (result.getSuccess().equalsIgnoreCase("true")) {

                        businessCount=result.getBusiness_count();
                        totalBusinessCount=result.getTotal_count();
                        mBusinessList.addAll(result.getBusiness_list());
                        try {
                            mFindBusinessAdapter = null;
                            mBusinessAdapter = null;
                            Log.e("", "setting bin adpter" + mBusinessList.size());
                            if (mBusinessList.size() > 0) {
                                mBusinessAdapter = new BusinessListAdapter(getActivity(), mBusinessList, onCardClickListner);
                                mRvBusinessGrid.setAdapter(mBusinessAdapter);
                            }

                            isLoading = false;
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }

    };


    ResponseHandlerListener responseHandlerFindBusiness = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            try {
                WebNotificationManager.unRegisterResponseListener(responseHandlerFindBusiness);

                try {
                    mFindBusinessResultList.addAll(result.getBusiness_list());

                    mFindBusinessAdapter = null;

                    if (mFindBusinessResultList.size() > 0) {
                        mFindBusinessAdapter = new FindBusinessAdapter(getActivity(), mFindBusinessResultList, onCardClickListner);
                        mRvBusinessGrid.setAdapter(mFindBusinessAdapter);
                    } else {
                        Utility.showToastMessageLong(getActivity(), getResources().getString(R.string.no_result_found));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utility.showToastMessageLong(getActivity(), getResources().getString(R.string.no_result_found));
                    Log.e("ccccccccccccccccccccccccc",""+2);
                }


                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utility.showToastMessageLong(getActivity(), getResources().getString(R.string.no_result_found));
                Log.e("dddddddddddddddddddddddddddddddd",""+3);

            }
        }
    };

    /*
    * badges on off response handler
    *
    * */
    ResponseHandlerListener responseListner = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            try {

                if (error == null) {
                    if (result.getSuccess().equalsIgnoreCase("true")) {
                        if (result.getBadge_status() == 1) {
                            mIvBusiness.setImageResource(R.drawable.badge_on);
                        } else {
                            mIvBusiness.setImageResource(R.drawable.badge_off);
                        }
                    }
                } else {
                    Utility.showToastMessageLong(getActivity(), "Some error occurred.Please try later");
                }

                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /*
    * create payload to on off the status of badges for a business location
    *
    * token, userId, businessId, badge_status
    * */
    public String createPayload(String businessId, String badgeStatus) {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.BUSINESS_ID, businessId);
            payload.put(Constants.BADGE_STATUS, badgeStatus);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return payload.toString();
    }


    /*
    *
    * to get business around
    *  token, userId, location, latlong, sort, limit, page
    * */
    public String createPayload(int limit,int page) {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.LOCATION, "Piccadilly Circus");
            payload.put(Constants.LATLONG, "37.77493,-122.419415");
            payload.put(Constants.SORT, sort+"");
            payload.put(Constants.LIMIT, limit+"");
            payload.put(Constants.PAGE, page+"");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return payload.toString();
    }


    public String createPayload(int sort, int limit, int page) {
        JSONObject payload = null;
        sort = 0;
        page += page;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.LOCATION, mEtSearch.getText().toString().trim());
            payload.put(Constants.SORT, String.valueOf(sort));
            payload.put(Constants.LIMIT, String.valueOf(limit));
            payload.put(Constants.PAGE, String.valueOf(page));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return payload.toString();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_trending:
                    buttonIndex=1;
                    updateTrendingUI();
                    break;
                case R.id.tv_nearby:
                    buttonIndex=2;
                    updateNearbyUI();
                    break;
                case R.id.tv_find:
                    buttonIndex=3;
                    updateFindUI();
                    break;
                case R.id.tv_search:
                    findBusinesses();
                    break;

                default:
                    break;

            }

        }
    };

    void updateTrendingUI() {
        try {
            mGlManager = new GridLayoutManager(getActivity(), 3);
            mRvBusinessGrid.setLayoutManager(mGlManager);
            mLlMapContainer.setVisibility(View.VISIBLE);
            mLlSearchContainer.setVisibility(View.GONE);
            mTvTrending.setTextColor(Color.parseColor("#FFFFFF"));
            mTvTrending.setBackgroundColor(Color.parseColor("#1C6DCE"));

            mTvNearby.setTextColor(Color.parseColor("#1C6DCE"));
            mTvNearby.setBackgroundResource(R.drawable.simple_border_line_style);

            mTvFind.setTextColor(Color.parseColor("#1C6DCE"));
            mTvFind.setBackgroundResource(R.drawable.simple_border_line_style);
            try {
                mFindBusinessAdapter = null;
                mBusinessAdapter = null;
                Log.e("", "setting bin adpter" + mBusinessList.size());
                if (mBusinessList.size() > 0) {
                    mBusinessAdapter = new BusinessListAdapter(getActivity(), mBusinessList, onCardClickListner);
                    mRvBusinessGrid.setAdapter(mBusinessAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void updateNearbyUI() {
        try {
            mGlManager = new GridLayoutManager(getActivity(), 3);
            mRvBusinessGrid.setLayoutManager(mGlManager);
            mLlMapContainer.setVisibility(View.VISIBLE);
            mLlSearchContainer.setVisibility(View.GONE);
            mTvTrending.setTextColor(Color.parseColor("#1C6DCE"));
            mTvTrending.setBackgroundResource(R.drawable.simple_border_line_style);

            mTvNearby.setTextColor(Color.parseColor("#FFFFFF"));
            mTvNearby.setBackgroundColor(Color.parseColor("#1C6DCE"));

            mTvFind.setTextColor(Color.parseColor("#1C6DCE"));
            mTvFind.setBackgroundResource(R.drawable.simple_border_line_style);


            // sort ArrayList
            mNearbyList = null;
            int sizeOfBusiness = mBusinessList.size();
            mNearbyList = new ArrayList<BusinessPojo>();
            for (int i = 0; i < sizeOfBusiness; i++) {
                mNearbyList.add(mBusinessList.get(i));
            }

            Collections.sort(mNearbyList, new Comparator<BusinessPojo>() {
                @Override
                public int compare(BusinessPojo c1, BusinessPojo c2) {
                    return Double.compare(c1.getDistance(), c2.getDistance());
                }
            });

            try {
                mFindBusinessAdapter = null;
                mBusinessAdapter = null;
                //  mRvBusinessGrid.setAdapter(mBusinessAdapter);
                Log.e("", "setting bin adpter" + mBusinessList.size());
                if (mNearbyList.size() > 0) {
                    mBusinessAdapter = new BusinessListAdapter(getActivity(), mNearbyList, onCardClickListner);
                    mRvBusinessGrid.setAdapter(mBusinessAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void updateFindUI() {
        try {
            mLlManager = new LinearLayoutManager(getActivity());
            mRvBusinessGrid.setLayoutManager(mLlManager);
            mLlMapContainer.setVisibility(View.GONE);
            mLlSearchContainer.setVisibility(View.VISIBLE);
            mTvTrending.setTextColor(Color.parseColor("#1C6DCE"));
            mTvTrending.setBackgroundResource(R.drawable.simple_border_line_style);
            mTvNearby.setTextColor(Color.parseColor("#1C6DCE"));
            mTvNearby.setBackgroundResource(R.drawable.simple_border_line_style);
            mTvFind.setTextColor(Color.parseColor("#FFFFFF"));
            mTvFind.setBackgroundColor(Color.parseColor("#1C6DCE"));

            try {
                mFindBusinessAdapter = null;
                mBusinessAdapter = null;
                Log.e("", "setting bin adpter" + mBusinessList.size());
                if (mBusinessList.size() > 0) {
                    mFindBusinessAdapter = new FindBusinessAdapter(getActivity(), mBusinessList, onCardClickListner);
                    mRvBusinessGrid.setAdapter(mFindBusinessAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mEtSearch.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        System.out.println("Text [" + s + "]");

                        mFindBusinessAdapter.getFilter().filter(s.toString());
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onLocationChanged(Location location) {
//        Toast.makeText(getActivity(), "Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude(), Toast.LENGTH_LONG).show();
//        mLocation=location;
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//        Log.i("LOCATION", "Provider " + provider + " has now status: " + status);
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//        Log.i("++", "Provider " + provider + " is enabled");
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//        Log.i("", "Provider " + provider + " is disabled");
//        Log.i("", "Provider " + provider + " is disabled");
//    }


    OnCardClickListner onCardClickListner = new OnCardClickListner() {
        @Override
        public void onCardClicked(ImageView view, int position) {
            mIvBusiness = view;
            if (mBusinessList.get(position).getBadge_status() == 0) {

//                this should be done on server request success
                // view.setImageResource(R.drawable.badge_on);
                mBusinessList.get(position).setBadge_status(1);

                String payload = createPayload(mBusinessList.get(position).getBusiness_id(), String.valueOf(mBusinessList.get(position).getBadge_status()));
                try {

                    if (payload != null) {
                        Log.e("on payload", "++" + payload.toString());

                        WebNotificationManager.registerResponseListener(responseListner);
                        WebServiceClient.onOffBusinessBadges(getActivity(), payload, responseListner);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
//                view.setImageResource(R.drawable.badge_off);
                mBusinessList.get(position).setBadge_status(0);
                String payload = createPayload(mBusinessList.get(position).getBusiness_id(), String.valueOf(mBusinessList.get(position).getBadge_status()));
                try {

                    if (payload != null) {
                        Log.e("off payload", "++" + payload.toString());
                        WebNotificationManager.registerResponseListener(responseListner);
                        WebServiceClient.onOffBusinessBadges(getActivity(), payload, responseListner);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

}
