package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.OnCardClickListner;
import com.igniva.indiecore.controller.OnCommentClickListner;
import com.igniva.indiecore.controller.OnDeletePostClickListner;
import com.igniva.indiecore.controller.OnDisLikeClickListner;
import com.igniva.indiecore.controller.OnLikeClickListner;
import com.igniva.indiecore.controller.OnMediaPostClickListner;
import com.igniva.indiecore.controller.OnNeutralClickListner;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.db.BadgesDb;
import com.igniva.indiecore.model.BadgesPojo;
import com.igniva.indiecore.model.PostPojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.ui.adapters.MyBadgesAdapter;
import com.igniva.indiecore.ui.adapters.WallPostAdapter;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-05 on 2/8/16.
 */
public class MyProfileActivity extends BaseActivity implements View.OnClickListener {

    Toolbar mToolbar;
    //    (like/dislike/neutral), post_id
    public static final String mActionTypeLike = "like";
    public static final String mActionTypeDislike = "dislike";
    public static final String mActionTypeNeutral = "neutral";
    private ImageView mCoverImage, mUserImage, mDropDown;
    private TextView mUserName, mUserLocation, mTvDesc, mTvPosts, mTvBadges, mTitle;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private ImageView mPostDelete;
    private BadgesDb db_badges;
    private boolean isLoading;
    private int totalPostCount=0;
    private ArrayList<PostPojo> mMyWallPostList = new ArrayList<PostPojo>();
    private WallPostAdapter mWallPostAdapter;
    private ImageView onOffImage;
    private RecyclerView mRvMyWallPosts;
    LinearLayoutManager mLlManager;
    GridLayoutManager mGlManager;
    ArrayList<BadgesPojo> mSelectedBadgesList = new ArrayList<BadgesPojo>();
    MyBadgesAdapter mMyBadgeAdapter;
    public static int POSITION = -1;
    int mSelectedPosition = -1;
    public String postID = "-1";
    private int action = 0;
    String LOG_TAG = "MyProfileActivity";
    public static int PAGE = 1, LIMIT = 20;
    String PROFILE = "profile";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        initToolbar();
        setUpLayout();
        setDataInViewObjects();
    }

    @Override
    protected void setUpLayout() {
        try {
            mLlManager = new LinearLayoutManager(this);
            mCoverImage = (ImageView) findViewById(R.id.iv_cover_pic_activity_my_profile);
            mUserImage = (ImageView) findViewById(R.id.iv_user_img_activity_my_profile);
            mUserName = (TextView) findViewById(R.id.tv_user_name_activity_my_profile);
            mUserLocation = (TextView) findViewById(R.id.tv_user_address_activity_my_profile);
            mDropDown = (ImageView) findViewById(R.id.iv_dropdown_activity_my_profile);
            mTvDesc = (TextView) findViewById(R.id.tv_desc_activity_my_profile);
            mTvPosts = (TextView) findViewById(R.id.tv_posts_activity_my_profile);
            mTvPosts.setOnClickListener(this);
            mTvBadges = (TextView) findViewById(R.id.tv_badges_activity_my_profile);
            mTvBadges.setOnClickListener(this);
            mRvMyWallPosts = (RecyclerView) findViewById(R.id.rv_posts_activity_my_profile);
            mRvMyWallPosts.setLayoutManager(mLlManager);
            updatePostTabUi();
            mDropDown=(ImageView) findViewById(R.id.iv_dropdown_activity_my_profile);
            mDropDown.setVisibility(View.GONE);


            mRvMyWallPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if (dy > 0) //check for scroll down
                    {
                        visibleItemCount = mLlManager.getChildCount();
                        totalItemCount = mLlManager.getItemCount();
                        pastVisibleItems = mLlManager.findFirstVisibleItemPosition();

                        if (!isLoading) {
                            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                isLoading = true;
                                //Do pagination.. i.e. fetch new data
                                if (mMyWallPostList.size() < 1) {
                                    PAGE=1;
                                    viewMyPost();
                                }
                                if (mMyWallPostList.size() < totalPostCount) {
                                    PAGE+=1;
                                     viewMyPost();
                                }
                            }
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void setDataInViewObjects() {


        try {
            if (!PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_FIRST_NAME, "").isEmpty()) {
                String Name = ((PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_FIRST_NAME, "")) + " " + (PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_LAST_NAME, "")));
                mUserName.setText(Name);
                mTitle.setText(Name);
            }
            if (PreferenceHandler.readString(this, PreferenceHandler.PROFILE_PIC_URL, null) != null) {

                Glide.with(this).load(WebServiceClient.HTTP_STAGING + PreferenceHandler.readString(this, PreferenceHandler.PROFILE_PIC_URL, ""))
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mUserImage);

            } else {
                mUserImage.setImageResource(R.drawable.default_user);

            }
            if (PreferenceHandler.readString(this, PreferenceHandler.COVER_PIC_URL, null) != null) {


                Glide.with(this).load(WebServiceClient.HTTP_STAGING + PreferenceHandler.readString(this, PreferenceHandler.COVER_PIC_URL, ""))
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mCoverImage);

            } else {
                mCoverImage.setImageResource(R.drawable.default_user);
            }
            mTvDesc.setText(PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_DESCRIPTION, ""));


            try {
                String countryCode=PreferenceHandler.readString(this,PreferenceHandler.PREF_KEY_COUNTRY_CODE,"");
                String[] rl=MyProfileActivity.this.getResources().getStringArray(R.array.countryList);
                for(int i=0;i<rl.length;i++){
                    String[] g=rl[i].split(",");
                    if(g[1].trim().equals(countryCode.trim())){
                        String countryName=g[0];
                        mUserLocation.setText(countryName);
                        break;  }

                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title_img);
//            mTitle.setText(getResources().getString(R.string.my_profile));
            //
            ImageView mTvNext = (ImageView) mToolbar.findViewById(R.id.toolbar_img);
            mTvNext.setVisibility(View.GONE);

            ImageView mMyProfile = (ImageView) mToolbar.findViewById(R.id.toolbar_img_left);
            mMyProfile.setImageResource(R.drawable.backarrow_icon);
            mMyProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
    }


    public void updatePostTabUi() {

        try {
            mLlManager = new LinearLayoutManager(this);
            mRvMyWallPosts.setLayoutManager(mLlManager);
            mTvPosts.setTextColor(Color.parseColor("#FFFFFF"));
            mTvPosts.setBackgroundColor(Color.parseColor("#1C6DCE"));
            mTvBadges.setTextColor(Color.parseColor("#1C6DCE"));
            mTvBadges.setBackgroundResource(R.drawable.simple_border_line_style);

            if (mMyWallPostList.size() == 0) {
                viewMyPost();
            } else {
                mWallPostAdapter = null;
                mWallPostAdapter = new WallPostAdapter(MyProfileActivity.this, mMyWallPostList, Constants.MYPROFILEACTIVITY,onLikeClickListner,onDisLikeClickListner,onNeutralClickListner,onCommentClickListner,onMediaPostClickListner,onDeletePostClickListner);
                mRvMyWallPosts.setAdapter(mWallPostAdapter);


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateBadgeTabUi() {
        try {
            mGlManager = new GridLayoutManager(this, 3);
            mRvMyWallPosts.setLayoutManager(mGlManager);
            mTvPosts.setTextColor(Color.parseColor("#1C6DCE"));
            mTvPosts.setBackgroundResource(R.drawable.simple_border_line_style);
            mTvBadges.setTextColor(Color.parseColor("#FFFFFF"));
            mTvBadges.setBackgroundColor(Color.parseColor("#1C6DCE"));
            if (mSelectedBadgesList.size() == 0) {
                getMyBadges();
            } else {
                mRvMyWallPosts.setAdapter(mMyBadgeAdapter);
                mMyBadgeAdapter = new MyBadgesAdapter(MyProfileActivity.this, mSelectedBadgesList, onCardClickListner);
                mRvMyWallPosts.setAdapter(mMyBadgeAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_posts_activity_my_profile:
                updatePostTabUi();
                break;
            case R.id.tv_badges_activity_my_profile:
                updateBadgeTabUi();
                break;
            default:
                break;


        }
    }


    /*
  *
  * create payload to get all post of a business
  * token, userId, roomId, postType, page, limit
  * */
    public String createPayload() {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.ROOM_ID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.POST_TYPE, PROFILE);
            payload.put(Constants.PAGE, PAGE + "");
            payload.put(Constants.LIMIT, LIMIT + "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return payload.toString();
    }

    /*
    * to get all the post of this business wall
    *
    * */
    public void viewMyPost() {
        String payload = createPayload();
        if (!payload.isEmpty()) {

            WebNotificationManager.registerResponseListener(responseHandler);
            WebServiceClient.view_all_posts(MyProfileActivity.this, payload, responseHandler);
        }
    }

    /*
    *
    * posts response and list inflation
    *
    * */
    ResponseHandlerListener responseHandler = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(responseHandler);
            try {

                if (error == null) {

                    if (result.getSuccess().equalsIgnoreCase("true")) {
                            totalPostCount=result.getTotalPosts();
//                        if (mMyWallPostList != null)
//                            mMyWallPostList.clear();
                        mMyWallPostList.addAll(result.getPostList());

                        if (mMyWallPostList.size() > 0) {
                            try {
                                mWallPostAdapter = null;
                                mWallPostAdapter = new WallPostAdapter(MyProfileActivity.this, mMyWallPostList, Constants.MYPROFILEACTIVITY,onLikeClickListner,onDisLikeClickListner,onNeutralClickListner,onCommentClickListner,onMediaPostClickListner,onDeletePostClickListner);
                                mRvMyWallPosts.setAdapter(mWallPostAdapter);


                                mRvMyWallPosts.getRecycledViewPool().clear();
                                mWallPostAdapter.notifyDataSetChanged();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    } else {


                    }
                } else {

                }
          isLoading=false;
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void getMyBadges() {
        try {
            db_badges = new BadgesDb(this);
            mSelectedBadgesList = db_badges.retrieveSelectedBadges();
            mMyBadgeAdapter = null;
            mRvMyWallPosts.setAdapter(mMyBadgeAdapter);
            mMyBadgeAdapter = new MyBadgesAdapter(MyProfileActivity.this, mSelectedBadgesList, onCardClickListner);
            mRvMyWallPosts.setAdapter(mMyBadgeAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * create payload to call activate/inactivate a badge
    *    Params:token, userId, badgeId, active (0/1)
        * */
    private String genratePayload(String badgeId, String badgestatus) {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.BADGEID, badgeId);
            payload.put(Constants.ACTIVE, badgestatus);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("", "______" + payload.toString());
        return payload.toString();
    }

    OnCardClickListner onCardClickListner = new OnCardClickListner() {
        @Override
        public void onCardClicked(ImageView view, int position) {
            onOffImage = view;
            if (mSelectedBadgesList.get(position).getActive() == 0) {
                mSelectedBadgesList.get(position).setActive(1);

                String payload = genratePayload(mSelectedBadgesList.get(position).getBadgeId(), String.valueOf(mSelectedBadgesList.get(position).getActive()));
                try {

                    if (payload != null) {
                        mSelectedPosition = position;
                        mSelectedBadgesList.get(position).setActive(1);
                        WebNotificationManager.registerResponseListener(responseListner);
                        WebServiceClient.onOffMyBadges(MyProfileActivity.this, payload, responseListner);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mSelectedBadgesList.get(position).setActive(0);
                String payload = genratePayload(mSelectedBadgesList.get(position).getBadgeId(), String.valueOf(mSelectedBadgesList.get(position).getActive()));
                try {

                    if (payload != null) {
                        mSelectedPosition = position;
                        mSelectedBadgesList.get(position).setActive(0);
                        WebNotificationManager.registerResponseListener(responseListner);
                        WebServiceClient.onOffMyBadges(MyProfileActivity.this, payload, responseListner);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


    ResponseHandlerListener responseListner = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            if (error == null) {

                if (result.getSuccess().equalsIgnoreCase("true")) {


                    if (result.getActive() == 1) {
                        onOffImage.setImageResource(R.drawable.badge_on);
                        updateRecord();


                    } else {
                        onOffImage.setImageResource(R.drawable.badge_off);
                        updateRecord();
                    }


                } else {
                    Utility.showToastMessageLong(MyProfileActivity.this,getResources().getString(R.string.some_unknown_error));
                }
            }
            // Always close the progressdialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

        }
    };

    public void updateRecord() {
        try {
            db_badges = new BadgesDb(this);
            BadgesPojo selectedBadge;

            selectedBadge = mSelectedBadgesList.get(mSelectedPosition);
            selectedBadge.getName();
            selectedBadge.getBadgeId();

            db_badges.updateSingleRow(selectedBadge);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    OnLikeClickListner onLikeClickListner =new OnLikeClickListner() {
        @Override
        public void onLikeClicked(TextView like, int position, String postId, String type) {
            try {
                Utility.showToastMessageShort(MyProfileActivity.this,"Like Clicked");
                POSITION = position;
                action = 1;
                likeUnlikePost(mActionTypeLike, mMyWallPostList.get(position).getPostId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    OnDisLikeClickListner onDisLikeClickListner =new OnDisLikeClickListner() {
        @Override
        public void onDisLikeClicked(TextView dislike, int position, String postId, String type) {
            try {
                POSITION = position;
                action = 2;
                likeUnlikePost(mActionTypeDislike, postId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    OnNeutralClickListner onNeutralClickListner =new OnNeutralClickListner() {
        @Override
        public void onNeutralClicked(TextView neutral, int position, String postId, String type) {
            try {
                POSITION = position;
                action = 3;
                likeUnlikePost(mActionTypeNeutral, postId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    OnCommentClickListner onCommentClickListner= new OnCommentClickListner() {
        @Override
        public void onCommentClicked(TextView comment, int position, String postId, String type) {
            POSITION = position;
            Bundle bundle = new Bundle();
            bundle.putSerializable("POST", mMyWallPostList.get(position));
            Intent intent = new Intent(MyProfileActivity.this, CommentActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);

        }
    };

    OnMediaPostClickListner onMediaPostClickListner= new OnMediaPostClickListner() {
        @Override
        public void onMediaPostClicked(ImageView media, int position, String postId, String type) {
            POSITION = position;
            Bundle bundle = new Bundle();
            bundle.putSerializable("POST", mMyWallPostList.get(position));
            Intent intent = new Intent(MyProfileActivity.this, CommentActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    OnDeletePostClickListner onDeletePostClickListner= new OnDeletePostClickListner() {
        @Override
        public void ondeletePostClicked(ImageView delete, int position, String postId, String type) {
            try {
                mPostDelete=delete;
                POSITION = position;
                Utility.showToastMessageShort(MyProfileActivity.this, " position is " + position);
                removePost(postId);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    /*
    * create payload to flag/remove a post
    *
    *
    * */
    public String genratePayload(String postId) {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(MyProfileActivity.this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(MyProfileActivity.this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.POST_ID, postId);


        } catch (Exception e) {

            e.printStackTrace();
        }

        return payload.toString();
    }

    /*
    * remove post call
    *
    * */
    public void removePost(String postId) {
        try {


            String payload = genratePayload(postId);
            if (!payload.isEmpty()) {

                WebNotificationManager.registerResponseListener(responseRemovePost);
                WebServiceClient.remove_a_post(MyProfileActivity.this, payload, responseRemovePost);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*
    * response Remove post
    *
    *
    * */
    ResponseHandlerListener responseRemovePost = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(responseRemovePost);

            try {
                if (error == null) {
                    if (result.getSuccess().equalsIgnoreCase("true")) {
                        mPostDelete.setVisibility(View.GONE);
                        mMyWallPostList.remove(POSITION);
                        mWallPostAdapter.notifyDataSetChanged();
                        Utility.showToastMessageLong(MyProfileActivity.this, "post removed");

                    } else {

                    }

                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            finish the dialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }


        }
    };


    //
// create payload to like unlike a post
//Params:token, userId, type(like/dislike/neutral), post_id
// */
    public String createPayload(String type, String postId) {

        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.TYPE, type);
            payload.put(Constants.POST_ID, postId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return payload.toString();
    }


    /*
    * like/unlike/neutral action to a post
    * @parms post_id
    *
    * */
    public void likeUnlikePost(String type, String postId) {
        String payload = createPayload(type, postId);
        if (!payload.isEmpty()) {
            WebNotificationManager.registerResponseListener(responseHandlerLike);
            WebServiceClient.like_unlike_post(this, payload, responseHandlerLike);
        }
    }

    /*
    * like unlike response
    * */
    ResponseHandlerListener responseHandlerLike = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(responseHandlerLike);

            try {
                if (error == null) {

                    if (result.getSuccess().equalsIgnoreCase("true")) {

                        if (action == 1) {
                            //action like
                            int num_like = mMyWallPostList.get(POSITION).getLike();

                            int a = num_like;
                            if (result.getLike() == 1) {
                                mMyWallPostList.get(POSITION).setAction(Constants.LIKE);
                                mMyWallPostList.get(POSITION).setLike(a + 1);
                            } else if (result.getLike() == 0) {
                                mMyWallPostList.get(POSITION).setAction(null);
                                if (a > 0) {
                                    mMyWallPostList.get(POSITION).setLike(a - 1);
                                }
                            }


                        } else if (action == 2) {
                            //action dislike
                            int num_dislike = mMyWallPostList.get(POSITION).getDislike();
                            int b = num_dislike;

                            if (result.getDislike() == 1) {
                                mMyWallPostList.get(POSITION).setAction(Constants.DISLIKE);
                                mMyWallPostList.get(POSITION).setDislike(b + 1);

                            } else if (result.getDislike() == 0) {
                                mMyWallPostList.get(POSITION).setAction(null);
                                if (b > 0) {
                                    mMyWallPostList.get(POSITION).setDislike(b - 1);
                                }

                            }


                        } else if (action == 3) {
                            //    action neutral

                            int num_neutral = mMyWallPostList.get(POSITION).getNeutral();
                            int c = num_neutral;

                            if (result.getNeutral() == 1) {
                                mMyWallPostList.get(POSITION).setAction(Constants.NEUTRAL);
                                mMyWallPostList.get(POSITION).setNeutral(c + 1);
                            } else if (result.getNeutral() == 0) {
                                mMyWallPostList.get(POSITION).setAction(null);
                                if (c > 0) {
                                    mMyWallPostList.get(POSITION).setNeutral(c - 1);
                                }
                            }
                        }
                        mWallPostAdapter.notifyDataSetChanged();

                    }

                    mWallPostAdapter.notifyDataSetChanged();
                }
//            finish the dialog
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    };
}
