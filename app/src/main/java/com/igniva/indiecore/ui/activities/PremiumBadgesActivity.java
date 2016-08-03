package com.igniva.indiecore.ui.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.OnPremiumBadgeClick;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.BadgesPojo;
import com.igniva.indiecore.model.PremiumBadgePojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.ui.adapters.PremiumBadgesAdapter;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;
import com.igniva.indiecore.utils.iab.IabBroadcastReceiver;
import com.igniva.indiecore.utils.iab.IabHelper;
import com.igniva.indiecore.utils.iab.IabResult;
import com.igniva.indiecore.utils.iab.Inventory;
import com.igniva.indiecore.utils.iab.Purchase;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-05 on 21/7/16.
 */
public class PremiumBadgesActivity extends BaseActivity implements IabBroadcastReceiver.IabBroadcastListener,
        DialogInterface.OnClickListener {

    private ArrayList<BadgesPojo> mPremiumBadgesList;
    private PremiumBadgesAdapter premiumBadgesAdapter;
    public static int pageNumber = 1, badgeCount = 20, category = 2, mTotalBadgeCount = 0;
    private Toolbar mToolbar;
    private RecyclerView mRvPremiumBadges;
    String LOG_TAG = "PremiumBadgesActivity";
    // The helper object
    IabHelper mHelper;
    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;
    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;
    // Will the subscription auto-renew?
    boolean mAutoRenewEnabled = false;
    static final String SKU_GAS = "gas";
    // SKUs for our products: the premium upgrade (non-consumable) and gas (consumable)
    static final String SKU_PREMIUM = "premium";
    // Does the user have the premium upgrade?
    boolean mIsPremium = false;
    // SKU for our subscription (infinite gas)
    static final String SKU_INFINITE_GAS_MONTHLY = "infinite_gas_monthly";
    static final String SKU_INFINITE_GAS_YEARLY = "infinite_gas_yearly";
    // Does the user have an active subscription to the infinite gas plan?
    boolean mSubscribedToInfiniteGas = false;
    // Tracks the currently owned infinite gas SKU, and the options in the Manage dialog
    String mInfiniteGasSku = "";
    String mFirstChoiceSku = "";
    String mSecondChoiceSku = "";
    // How many units (1/4 tank is our unit) fill in the tank.
    static final int TANK_MAX = 4;

    // Current amount of gas in tank, in units
    int mTank;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_badges);
        initToolbar();
        setUpLayout();
        setUpIAB();
    }

    private void setUpIAB() {
        /* base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY
         * (that you got from the Google Play developer console). This is not your
         * developer public key, it's the *app-specific* public key.
         *
         * Instead of just storing the entire literal string here embedded in the
         * program,  construct the key at runtime from pieces or
         * use bit manipulation (for example, XOR with some other string) to hide
         * the actual key.  The key itself is not secret information, but we don't
         * want to make it easy for an attacker to replace the public key with one
         * of their own and then fake messages from the server.
         */
        String base64EncodedPublicKey = getResources().getString(R.string.base_64_key);

        android.util.Log.d(LOG_TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        android.util.Log.d(LOG_TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                android.util.Log.d(LOG_TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().
                mBroadcastReceiver = new IabBroadcastReceiver(PremiumBadgesActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                android.util.Log.d(LOG_TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }
        });
    }


    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar_premium_badges);
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
            TextView mTvTitle = (TextView) mToolbar.findViewById(R.id.toolbar_next);
            mTvTitle.setVisibility(View.GONE);
            ImageView mToolBArImageBtn = (ImageView) mToolbar.findViewById(R.id.toolbar_img);
            TextView title = (TextView) mToolbar.findViewById(R.id.toolbar_title);
            title.setText(getResources().getString(R.string.premium_badges));
            mToolBArImageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PremiumBadgesActivity.this, MyBadgesActivity.class);
                    startActivity(intent);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void setUpLayout() {

        mRvPremiumBadges = (RecyclerView) findViewById(R.id.rv_premium_badges);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvPremiumBadges.setLayoutManager(layoutManager);

        mPremiumBadgesList = new ArrayList<BadgesPojo>();
//        for (int i = 0; i < 7; i++) {
//            premiumBadgePojo = new PremiumBadgePojo();
//            premiumBadgePojo.setBadgeIcon(getResources().getDrawable(R.drawable.adopted_icon, null));
//            premiumBadgePojo.setBadgeName("Albino");
//            premiumBadgePojo.setBadgePrice("$22.00");
//            mPremiumBadgesList.add(premiumBadgePojo);
//        }
        getPremiumBadges(pageNumber,badgeCount,category);


    }


    public String createPayload(int page, int limit, int category) {
        JSONObject payloadJson = null;
        try {
            payloadJson = new JSONObject();
            payloadJson.put(Constants.TOKEN, PreferenceHandler.readString(PremiumBadgesActivity.this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payloadJson.put(Constants.USERID, PreferenceHandler.readString(PremiumBadgesActivity.this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payloadJson.put(Constants.PAGE, page + "");
            payloadJson.put(Constants.LIMIT, limit + "");
            payloadJson.put(Constants.CATEGORY, category + "");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Log.d(LOG_TAG, "paload is " + payloadJson.toString());
        return payloadJson.toString();
    }


    public  void getPremiumBadges(int page, int limit, int category){

        String payload = createPayload(pageNumber, badgeCount, category);
        if (payload != null) {
            // Web service Call
            // Step 1 - Register responsehandler
            WebNotificationManager.registerResponseListener(responseHandlerListener);
            // Step 2 - Webservice Call
            WebServiceClient.getBadges(PremiumBadgesActivity.this, payload, responseHandlerListener);

        }

    }

    ResponseHandlerListener responseHandlerListener= new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(responseHandlerListener);
            try {

                if(error==null){

                    if(result.getSuccess().equalsIgnoreCase("true")){

                        mPremiumBadgesList.clear();

                        for (int i=0;i<mPremiumBadgesList.size();i++){

                            result.getBadges().get(i).setSelected(false);
                        }
                        mPremiumBadgesList.addAll(result.getBadges());
                        setDataInViewObjects();

                    }



                }




            }catch (Exception e){
                e.printStackTrace();
            }
            // Always close the progressdialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

        }
    };


    @Override
    protected void setDataInViewObjects() {

        try {

            Log.e("premium", "" + mPremiumBadgesList.size());
            premiumBadgesAdapter = null;
            premiumBadgesAdapter = new PremiumBadgesAdapter(this, mPremiumBadgesList,onPremiumBadgeClick);
            mRvPremiumBadges.setAdapter(premiumBadgesAdapter);

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    OnPremiumBadgeClick onPremiumBadgeClick= new OnPremiumBadgeClick() {
        @Override
        public void onPremiumBadgeClicked(BadgesPojo mPremiumBadge) {



        }
    };


    // User clicked the "Buy Gas" button
    public void onBuyGasButtonClicked(View arg0) {
    Log.d(LOG_TAG, "Buy gas button clicked.");



        // launch the gas purchase UI flow.
        // We will be notified of completion via mPurchaseFinishedListener
        setWaitScreen(true);
      Log.d(LOG_TAG, "Launching purchase flow for gas.");

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";

        try {
            mHelper.launchPurchaseFlow(this, SKU_GAS, RC_REQUEST,
                    mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error launching purchase flow. Another async operation in progress.");
            setWaitScreen(false);
        }
    }




    protected void onClick(View v) {


    }

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        android.util.Log.d(LOG_TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error querying inventory. Another async operation in progress.");
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
         Log.d(LOG_TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                setWaitScreen(false);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                setWaitScreen(false);
                return;
            }

            android.util.Log.d(LOG_TAG, "Purchase successful.");

            if (purchase.getSku().equals(SKU_GAS)) {
                // bought 1/4 tank of gas. So consume it.
                android.util.Log.d(LOG_TAG, "Purchase is gas. Starting gas consumption.");
                try {
                    mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error consuming gas. Another async operation in progress.");
                    setWaitScreen(false);
                    return;
                }
            }
            else if (purchase.getSku().equals(SKU_PREMIUM)) {
                // bought the premium upgrade!
                android.util.Log.d(LOG_TAG, "Purchase is premium upgrade. Congratulating user.");
                alert("Thank you for upgrading to premium!");
                mIsPremium = true;
                updateUi();
                setWaitScreen(false);
            }
            else if (purchase.getSku().equals(SKU_INFINITE_GAS_MONTHLY)
                    || purchase.getSku().equals(SKU_INFINITE_GAS_YEARLY)) {
                // bought the infinite gas subscription
                android.util.Log.d(LOG_TAG, "Infinite gas subscription purchased.");
                alert("Thank you for subscribing to infinite gas!");
                mSubscribedToInfiniteGas = true;
                mAutoRenewEnabled = purchase.isAutoRenewing();
                mInfiniteGasSku = purchase.getSku();
                mTank = TANK_MAX;
                updateUi();
                setWaitScreen(false);
            }
        }
    };


    void complain(String message) {
        android.util.Log.e(LOG_TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        android.util.Log.d(LOG_TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            android.util.Log.d(LOG_TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            android.util.Log.d(LOG_TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            android.util.Log.d(LOG_TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));

            // First find out which subscription is auto renewing
            Purchase gasMonthly = inventory.getPurchase(SKU_INFINITE_GAS_MONTHLY);
            Purchase gasYearly = inventory.getPurchase(SKU_INFINITE_GAS_YEARLY);
            if (gasMonthly != null && gasMonthly.isAutoRenewing()) {
                mInfiniteGasSku = SKU_INFINITE_GAS_MONTHLY;
                mAutoRenewEnabled = true;
            } else if (gasYearly != null && gasYearly.isAutoRenewing()) {
                mInfiniteGasSku = SKU_INFINITE_GAS_YEARLY;
                mAutoRenewEnabled = true;
            } else {
                mInfiniteGasSku = "";
                mAutoRenewEnabled = false;
            }

            // The user is subscribed if either subscription exists, even if neither is auto
            // renewing
            mSubscribedToInfiniteGas = (gasMonthly != null && verifyDeveloperPayload(gasMonthly))
                    || (gasYearly != null && verifyDeveloperPayload(gasYearly));
            android.util.Log.d(LOG_TAG, "User " + (mSubscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE")
                    + " infinite gas subscription.");
            if (mSubscribedToInfiniteGas) mTank = TANK_MAX;

            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
            Purchase gasPurchase = inventory.getPurchase(SKU_GAS);
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                android.util.Log.d(LOG_TAG, "We have gas. Consuming it.");
                try {
                    mHelper.consumeAsync(inventory.getPurchase(SKU_GAS), mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error consuming gas. Another async operation in progress.");
                }
                return;
            }

            updateUi();
            setWaitScreen(false);
            android.util.Log.d(LOG_TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    // Enables or disables the "please wait" screen.
    void setWaitScreen(boolean set) {
       // findViewById(R.id.screen_main).setVisibility(set ? View.GONE : View.VISIBLE);
      //  findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE : View.GONE);
    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    // updates UI to reflect model
    public void updateUi() {
        /*// update the car color to reflect premium status or lack thereof
        ((ImageView)findViewById(R.id.free_or_premium)).setImageResource(mIsPremium ? R.drawable.premium : R.drawable.free);

        // "Upgrade" button is only visible if the user is not premium
        findViewById(R.id.upgrade_button).setVisibility(mIsPremium ? View.GONE : View.VISIBLE);

        ImageView infiniteGasButton = (ImageView) findViewById(R.id.infinite_gas_button);
        if (mSubscribedToInfiniteGas) {
            // If subscription is active, show "Manage Infinite Gas"
            infiniteGasButton.setImageResource(R.drawable.manage_infinite_gas);
        } else {
            // The user does not have infinite gas, show "Get Infinite Gas"
            infiniteGasButton.setImageResource(R.drawable.get_infinite_gas);
        }

        // update gas gauge to reflect tank status
        if (mSubscribedToInfiniteGas) {
            ((ImageView)findViewById(R.id.gas_gauge)).setImageResource(R.drawable.gas_inf);
        }
        else {
            int index = mTank >= TANK_RES_IDS.length ? TANK_RES_IDS.length - 1 : mTank;
            ((ImageView)findViewById(R.id.gas_gauge)).setImageResource(TANK_RES_IDS[index]);
        }*/
    }

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            android.util.Log.d(LOG_TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                android.util.Log.d(LOG_TAG, "Consumption successful. Provisioning.");
                mTank = mTank == TANK_MAX ? TANK_MAX : mTank + 1;
                //saveData();
                alert("You filled 1/4 tank. Your tank is now " + String.valueOf(mTank) + "/4 full!");
            }
            else {
                complain("Error while consuming: " + result);
            }
            updateUi();
            setWaitScreen(false);
            android.util.Log.d(LOG_TAG, "End consumption flow.");
        }
    };

}
