package com.igniva.indiecore.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import com.igniva.indiecore.utils.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.ImageView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.igniva.indiecore.R;
import com.igniva.indiecore.utils.gcm.RegistrationIntentService;

/**
 * Created by igniva-andriod-02 on 24/2/16.
 */
public class SplashActivity extends BaseActivity
{
    private ImageView appLogo;
    private ImageView mGetStarted;
    private Animation animFadein;
    private boolean isReceiverRegistered;
    private String TAG="SplashActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setUpLayout();


        Log.e(TAG,"Above GCM");
        // GCM CODE
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e(TAG,"inside BroadcastReceiver ");
//                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(RegistrationIntentService.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
//                    mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
//                    mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };

        // Registering BroadcastReceiver
//        registerReceiver();
//        if (checkPlayServices()) {
//            Log.e(TAG,"checkPlayServices ");
//            // Start IntentService to register this application with GCM.
//            Intent intent = new Intent(this, RegistrationIntentService.class);
//            startService(intent);
//        }
//       //  String gcmId= PreferenceHandler.readString(this,PreferenceHandler.PREF_KEY_GCMID,"");
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
        mGetStarted=(ImageView) findViewById(R.id.im_getStarted);
       // mGetStarted.setOnClickListener(this);
    }

    @Override
    protected void setDataInViewObjects() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.im_getStarted:
                Intent intent= new Intent(SplashActivity.this,EnterMobileActivity.class);
                startActivity(intent);
                finish();
                break;

            default:
                break;
        }
    }
}