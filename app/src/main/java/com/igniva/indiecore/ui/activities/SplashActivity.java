package com.igniva.indiecore.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.MeteorCommonClass;
import com.igniva.indiecore.controller.services.CustomMeteorService;
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;
import com.igniva.indiecore.utils.gcm.RegistrationIntentService;

/**
 * Created by igniva-andriod-02 on 24/2/16.
 */
public class SplashActivity extends BaseActivity
{
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    LinearLayout mLlNext;
    private ImageView appLogo;
    private ImageView mGetStarted;
    private Animation animFadein;
    private boolean isReceiverRegistered;
    private String TAG="SplashActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setUpLayout();


        // If user already login then start service
        if (!PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, "").isEmpty() && !PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, "").isEmpty() && !Utility.isMyServiceRunning(SplashActivity.this, MeteorCommonClass.class)) {
            Intent serviceIntent = new Intent(this, CustomMeteorService.class);
            startService(serviceIntent);
        }

     //   Log.e(TAG,"Above GCM");
        // GCM CODE
//        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Log.e(TAG,"inside BroadcastReceiver ");
////                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
//                SharedPreferences sharedPreferences =
//                        PreferenceManager.getDefaultSharedPreferences(context);
//                boolean sentToken = sharedPreferences
//                        .getBoolean(RegistrationIntentService.SENT_TOKEN_TO_SERVER, false);
//                if (sentToken) {
////                    mInformationTextView.setText(getString(R.string.gcm_send_message));
//                } else {
////                    mInformationTextView.setText(getString(R.string.token_error_message));
//                }
//            }
//        };

        // Registering BroadcastReceiver
//        registerReceiver();
//        if (checkPlayServices()) {
//            Log.e(TAG,"checkPlayServices ");
//            // Start IntentService to register this application with GCM.
//            Intent intent = new Intent(this, RegistrationIntentService.class);
//            startService(intent);
//        }
//       //  String gcmId= PreferenceHandler.readString(this,PreferenceHandler.PREF_KEY_GCMID,"");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateNextScreen();
            }
        },1500);
    }



    @Override
    protected void onResume() {
        super.onResume();
//        registerReceiver();
    }

    @Override
    protected void onPause() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
//        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            Log.e(TAG,"inside registerReceiver ");
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(RegistrationIntentService.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void setUpLayout() {
        try {
            mLlNext = (LinearLayout) findViewById(R.id.ll_next_screen);
            mLlNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateNextScreen();
                }
            });
            // mGetStarted.setOnClickListener(this);
        } catch (Exception e){

            e.printStackTrace();
        }
    }

    @Override
    protected void setDataInViewObjects() {

    }


    public void onClick(View v) {
        switch (v.getId()){

            case R.id.im_getStarted:
                    //navigateNextScreen();
          break;

            default:
                break;
        }
    }

   void navigateNextScreen(){
       try {

           if(!PreferenceHandler.readString(SplashActivity.this,PreferenceHandler.PREF_KEY_USER_TOKEN,"").isEmpty() && !PreferenceHandler.readString(SplashActivity.this,PreferenceHandler.PREF_KEY_USER_ID,"").isEmpty() ){
               Intent intent = new Intent(SplashActivity.this, DashBoardActivity.class);
               startActivity(intent);
               finish();
           }else {
               Intent intent = new Intent(SplashActivity.this, EnterMobileActivity.class);
               startActivity(intent);
               finish();
           }

       }catch (Exception e){
           e.printStackTrace();
       }
    }
}