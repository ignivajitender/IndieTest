package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.db.BadgesDb;
import com.igniva.indiecore.model.BadgesPojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.ui.adapters.BadgesAdapter;
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by igniva-andriod-11 on 8/6/16.
 */
public class BadgesActivity extends IABActivity {
    String LOG_TAG = "BadgesActivity";
    Toolbar mToolbar;
    private GridLayoutManager mGlayout;
    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;
    RecyclerView mRvBadges;
    IabHelper mHelper;
    ArrayList<BadgesPojo> mBadgesList = null;
    LinearLayout mllNext, mLlPrevious;
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
    public static int pageNumber = 1, badgeCount = 20, category = 0, mTotalBadgeCount = 0;
    BadgesAdapter mBadgesAdapter;
    public ArrayList<BadgesPojo> mSelectedBadgeIds = new ArrayList<BadgesPojo>();
    private BadgesDb dbBadges;
    // Current amount of gas in tank, in units
    int mTank;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badges);

        mBadgesList = new ArrayList<BadgesPojo>();
        pageNumber = 1;
        //
        initToolbar();
        setUpLayout();
        setUpIAB();
        //
        String payload = createPayload(pageNumber, badgeCount, category);
        if (payload != null) {
            // Web service Call
            // Step 1 - Register responsehandler
            WebNotificationManager.registerResponseListener(responseHandlerListener);
            // Step 2 - Webservice Call
            WebServiceClient.getBadges(BadgesActivity.this, payload, responseHandlerListener);

        }

    }

    @Override
    protected void setUpLayout() {
        try {
            mGlayout = new GridLayoutManager(BadgesActivity.this, 4);
            mRvBadges = (RecyclerView) findViewById(R.id.recycler_view);


            mllNext = (LinearLayout) findViewById(R.id.ll_next);
            mllNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mllNext.setEnabled(false);
                    updateNextBadges();
                }
            });
            mLlPrevious = (LinearLayout) findViewById(R.id.ll_previous);
            mLlPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePreviousBadges();
                }
            });
            mLlPrevious.setVisibility(View.GONE);

            //
            mRvBadges.setHasFixedSize(true);
            mRvBadges.setLayoutManager(mGlayout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setDataInViewObjects() {
        try {
            mBadgesAdapter = null;
            mRvBadges.setAdapter(mBadgesAdapter);
            //
            mBadgesAdapter = new BadgesAdapter(BadgesActivity.this, mBadgesList, pageNumber, badgeCount, mTotalBadgeCount, mSelectedBadgeIds);
            mRvBadges.setAdapter(mBadgesAdapter);
            // show previous button
            if (pageNumber > 1) {
                mLlPrevious.setVisibility(View.VISIBLE);
            }
            // hide next button
            if ((pageNumber * badgeCount) > mBadgesList.size()) {
                mllNext.setVisibility(View.GONE);
            } else {
                mllNext.setVisibility(View.VISIBLE);
            }

            // Enable buttons
            mllNext.setEnabled(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onClick(View v) {


    }

    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            TextView mTvTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
            mTvTitle.setText(getResources().getString(R.string.badges));
            //

//            ImageView back=(ImageView) mToolbar.findViewById(R.id.iv_back);
//            back.setVisibility(View.GONE);

            TextView mTvNext = (TextView) mToolbar.findViewById(R.id.toolbar_next);
            mTvNext.setText("Done");
            mTvNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (mSelectedBadgeIds.size() > 0) {
                        Log.e("", "------Done");
                        AlertDialog.Builder builder = Utility.showAlertDialogOkNoThanks("Are you sure you want to add these badge(s)", BadgesActivity.this);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Uncomment below two while giving for QA
                                getSelectedBadgesList();
                                mySelectedBadges();

                                // Delete me, i am for testing only
//                                Intent intent = new Intent(BadgesActivity.this, DashBoardActivity.class);
                    // intent.putExtra("Badges", mBadgesList);
//                                startActivity(intent);
                            }
                        });
                        builder.setNegativeButton("No, Thanks", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.create().show();
                    } else {

                        Utility.showToastMessageShort(BadgesActivity.this, "Please select at-least one badge");
                        return;
                    }


//                    Intent intent = new Intent(BadgesActivity.this, DashBoardActivity.class);
//                    // intent.putExtra("Badges", mBadgesList);
//                    startActivity(intent);
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


    public String createPayload(int page, int limit, int category) {
        JSONObject payloadJson = null;https://www.google.co.in/search?client=ubuntu&channel=fs&q=play+store&ie=utf-8&oe=utf-8&gfe_rd=cr&ei=2hv2V5KYFvDs8Afm7Yi4Bg
        try {
            payloadJson = new JSONObject();
            payloadJson.put(Constants.TOKEN, PreferenceHandler.readString(BadgesActivity.this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payloadJson.put(Constants.USERID, PreferenceHandler.readString(BadgesActivity.this, PreferenceHandler.PREF_KEY_USER_ID, ""));
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        mBadgesAdapter.onActivityResult(requestCode, resultCode, data);
    }



    /*
    * payload to save my selected badges at server
    * PARAMETER: token, userId, type, badgeIds (should be in CSV format. for eg. 1,4,5,8)
    *
    * */
    public String createPayload() {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.TYPE, "general");
            String ids = "";
            for (int i = 0; i < mSelectedBadgeIds.size(); i++) {
                ids = ids + "," + mSelectedBadgeIds.get(i).getBadgeId();
            }
            ids = ids.substring(1);
            payload.put(Constants.BADGEIDS, ids);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Log.d(LOG_TAG, " payload is " + payload.toString());

        return payload.toString();
    }

/*
*
* save my badges at server request
*
* */
    public void mySelectedBadges() {
        try {
            String payload = createPayload();

            payload = payload.replace("[", "");
            payload = payload.replace("]", "");
            if (payload != null) {
                // Web service Call
                // Step 1 - Register responsehandler
                Log.d(LOG_TAG, "--" + payload);
                WebNotificationManager.registerResponseListener(responseHandler);
                // Step 2 - Webservice Call
                WebServiceClient.userselectedBadges(BadgesActivity.this, payload, responseHandler);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    ResponseHandlerListener responseHandlerListener = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(responseHandlerListener);
            // check for error
            if (error == null) {
                // start parsing
                if (result.getSuccess().equalsIgnoreCase("true")) {
                    // display in grid
                    mTotalBadgeCount = result.getTotal_badges();
                    if (mBadgesList.size() < mTotalBadgeCount)
                        mBadgesList.addAll(result.getBadges());
                    setDataInViewObjects();
                } else {
                    // display error message
                }
            } else {
                // display error dialog
            }

            // Always close the progressdialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

        }
    };

    ResponseHandlerListener responseHandler = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(responseHandler);

            if (error == null) {
                if (result.getSuccess().equals("true")) {


                    Utility.showToastMessageShort(BadgesActivity.this, "Badge(s) added successfully");
                    insertRecords();
                    Intent intent = new Intent(BadgesActivity.this, DashBoardActivity.class);
                    startActivity(intent);

                } else {

                    AlertDialog.Builder builder=Utility.showAlertDialogBuy(result.getError_text(), BadgesActivity.this);
                    builder.setPositiveButton("Buy a badge slot", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            // start payment
                            onBuyBadgeClicked();
                            // close dialog
                            dialog.dismiss();

                        }
                    });
                    AlertDialog alert11 = builder.create();
                    alert11.show();
                }
//                {"user":{"badgeLimit":10,"selectedBadgeCount":10,"badgesGot":1},"success":true,"error":null}
//                {"user":{"badgeLimit":10,"selectedBadgeCount":5,"badgesGot":5},"success":true,"error":null}
//                {"success":false,"error":10,"error_text":"Your selected badge count is greater."}


            } else {
                Utility.showAlertDialog("Some server error Occurred!", BadgesActivity.this,null);


            }
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

        }
    };


    public ArrayList<BadgesPojo> getSelectedBadgesList() {
        ArrayList<BadgesPojo> selectedBadgesList = new ArrayList<BadgesPojo>();
        selectedBadgesList = mSelectedBadgeIds;
        return selectedBadgesList;
    }


    // User clicked the "Buy Gas" button
    public void onBuyBadgeClicked() {
        Log.d(LOG_TAG, "Buy gas button clicked.");
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
            // setWaitScreen(false);
        }
    }

    public void insertRecords() {
        try {
            dbBadges = new BadgesDb(this);
            ArrayList<BadgesPojo> selectedBadgesList = new ArrayList<BadgesPojo>();
            selectedBadgesList = mSelectedBadgeIds;
//        ArrayList<BadgesPojo> mTotalBadges=new ArrayList<BadgesPojo>();
            dbBadges.insertAllBadges(selectedBadgesList);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    void updateNextBadges() {
        pageNumber = pageNumber + 1;
        Log.d(LOG_TAG, "page no is  is " + pageNumber + " size of list " + mBadgesList.size());
        if (mBadgesList.size() > (pageNumber * badgeCount) || mBadgesList.size() == mTotalBadgeCount) {
            setDataInViewObjects();
        } else {
            String payload = createPayload(pageNumber, badgeCount, category);
            if (payload != null) {
                // Web service Call
                // Step 1 - Register responsehandler
                WebNotificationManager.registerResponseListener(responseHandlerListener);
                // Step 2 - Webservice Call
                WebServiceClient.getBadges(BadgesActivity.this, payload, responseHandlerListener);
            }
        }
    }

    void updatePreviousBadges() {
        pageNumber = pageNumber - 1;
        if (pageNumber == 1) {
            mLlPrevious.setVisibility(View.GONE);
        }
        if (pageNumber < 1) {
            pageNumber = 1;
            mLlPrevious.setVisibility(View.GONE);

        }
        setDataInViewObjects();
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

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);


        public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

            private GestureDetector gestureDetector;
            private BadgesActivity.ClickListener clickListener;

            public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final BadgesActivity.ClickListener clickListener) {
                this.clickListener = clickListener;
                gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                        if (child != null && clickListener != null) {
                            clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                        }
                    }
                });
            }

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                    clickListener.onClick(child, rv.getChildPosition(child));
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        }
    }



    /*
    * In app billing
    *
    *
    *
    * */
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
                mBroadcastReceiver = new IabBroadcastReceiver(BadgesActivity.this);
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

    void complain(String message) {
        android.util.Log.e(LOG_TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        android.app.AlertDialog.Builder bld = new android.app.AlertDialog.Builder(this);
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

//            updateUi();
            setWaitScreen(false);
            android.util.Log.d(LOG_TAG, "Initial inventory query finished; enabling main UI.");
        }
    };


    /**
     * Verifies the developer payload of a purchase.
     */
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
            } else {
                complain("Error while consuming: " + result);
            }
//            updateUi();
            setWaitScreen(false);
            android.util.Log.d(LOG_TAG, "End consumption flow.");
        }
    };
    // Enables or disables the "please wait" screen.
    void setWaitScreen(boolean set) {
        // findViewById(R.id.screen_main).setVisibility(set ? View.GONE : View.VISIBLE);
        //  findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE : View.GONE);
    }

}
