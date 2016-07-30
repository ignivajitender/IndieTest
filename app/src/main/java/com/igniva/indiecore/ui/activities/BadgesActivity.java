package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by igniva-andriod-11 on 8/6/16.
 */
public class BadgesActivity extends BaseActivity {
    String LOG_TAG = "BadgesActivity";
    Toolbar mToolbar;
    private GridLayoutManager mGlayout;
    RecyclerView mRvBadges;
    ArrayList<BadgesPojo> mBadgesList = null;
    LinearLayout mllNext, mLlPrevious;
    public static int pageNumber = 1, badgeCount = 20, category = 0, mTotalBadgeCount = 0;
    BadgesAdapter mBadgesAdapter;
    public ArrayList<BadgesPojo> mSelectedBadgeIds = new ArrayList<BadgesPojo>();
    private BadgesDb dbBadges;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badges);

        mBadgesList = new ArrayList<BadgesPojo>();
        pageNumber = 1;
        //
        initToolbar();
        setUpLayout();
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
        JSONObject payloadJson = null;
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
//        mBadgesAdapter.notifyDataSetChanged();
    }


    public String createPayload() {
        JSONObject payload = null;
//        PARAMETER: token, userId, type, badgeIds (should be in CSV format. for eg. 1,4,5,8)
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
                    Intent intnet = new Intent(BadgesActivity.this, DashBoardActivity.class);
                    startActivity(intnet);

                } else {

                    Utility.showAlertDialog(result.getError_text(), BadgesActivity.this);


                }
//                {"user":{"badgeLimit":10,"selectedBadgeCount":10,"badgesGot":1},"success":true,"error":null}
//                {"user":{"badgeLimit":10,"selectedBadgeCount":5,"badgesGot":5},"success":true,"error":null}
//                {"success":false,"error":10,"error_text":"Your selected badge count is greater."}


            } else {
                Utility.showAlertDialog("Some server error Occurred!", BadgesActivity.this);


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
}
