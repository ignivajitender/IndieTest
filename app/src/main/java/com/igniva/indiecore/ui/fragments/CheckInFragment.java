//package com.igniva.indiecore.ui.fragments;
//
//import android.app.Dialog;
//import android.content.IntentSender;
//import android.location.Criteria;
//import android.location.Location;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdate;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.igniva.indiecore.Manifest;
//import com.igniva.indiecore.R;
//
///**
// * Created by igniva-andriod-05 on 1/7/16.
// */
//public class CheckInFragment extends Fragment implements LocationListener {
//
//
//    View rootView;
//    GoogleMap googleMap;
//
//    /*
//     * Define a request code to send to Google Play services This code is
//     * returned in Activity.onActivityResult
//     */
//    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        rootView = inflater.inflate(R.layout.fragment_checkin, container, false);
//
//        if (!isGooglePlayServicesAvailable()) {
//
//        }
//        SupportMapFragment supportMapFragment =
//                (SupportMapFragment) getFragmentManager().findFragmentById(R.id.googleMap);
//        googleMap = supportMapFragment.getMap();
//        googleMap.setMyLocationEnabled(true);
//        LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        String bestProvider = locationManager.getBestProvider(criteria, true);
//        Location location = locationManager.getLastKnownLocation(bestProvider);
//        if (location != null) {
//            onLocationChanged(location);
//        }
//        locationManager.requestLocationUpdates(bestProvider, 20000, 0, (android.location.LocationListener) getActivity());
//
//
//        return rootView;
//    }
//
//    private boolean isGooglePlayServicesAvailable() {
//        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
//        if (ConnectionResult.SUCCESS == status) {
//            return true;
//        } else {
//            GooglePlayServicesUtil.getErrorDialog(status, getActivity(), 0).show();
//            return false;
//        }
//    }
//    @Override
//    public void onLocationChanged(Location location) {
//        double latitude = location.getLatitude();
//        double longitude = location.getLongitude();
//        LatLng latLng = new LatLng(latitude, longitude);
//        googleMap.addMarker(new MarkerOptions().position(latLng));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//        Log.e("" + latitude , " Longitude:" + longitude);
//    }
//}
