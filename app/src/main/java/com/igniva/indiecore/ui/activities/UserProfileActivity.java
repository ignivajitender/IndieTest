package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
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
 * Created by igniva-andriod-05 on 5/8/16.
 */
public class UserProfileActivity extends BaseActivity implements View.OnClickListener {
    private TextView mTvUserName, mTvUserLocation, mTvUserDescription, mTvNoPostAvailable,mTvLabel;
    private ImageView mIvUserIcon, mIvCoverPic, mIvStar, mIvDropDown, mIvBlockUser;
    private TextView mTvTitle;
    private RecyclerView mRvUserPost;
    private boolean isLoading;
    private int totalPostCount=0;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private LinearLayoutManager mLlManger = new LinearLayoutManager(this);
    private GridLayoutManager mGlManager = new GridLayoutManager(UserProfileActivity.this, 4);
    private ArrayList<PostPojo> mUserWallPostList = new ArrayList<PostPojo>();
    private WallPostAdapter mWallPostAdapter;
    private MyBadgesAdapter mMutualBadgeAdapter;
    private String LOG_TAG = "UserProfileActivity";
    private String PROFILE = "profile";
    private  int PAGE = 1, LIMIT = 20;
    private Toolbar mToolbar;
    private String mUserId = "";
    private String mBusinessId = "";
    private int INDEX = 0;
    int action = 0;
    public static int POSITION = -1;
    public String postID = "-1";
    WallPostAdapter.RecyclerViewHolders mHolder;
    public static final String mActionTypeLike = "like";
    public static final String mActionTypeDislike = "dislike";
    public static final String mActionTypeNeutral = "neutral";
    private ResponsePojo userDetails = new ResponsePojo();
    private boolean IsBlockIconVisible = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initToolbar();
        setUpLayout();
        setDataInViewObjects();
    }

    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            mTvTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title_img);
            //
            ImageView mIvNext = (ImageView) mToolbar.findViewById(R.id.toolbar_img);
            mIvNext.setImageResource(R.drawable.chat);
            mIvNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.showToastMessageShort(UserProfileActivity.this, getResources().getString(R.string.coming_soon));
                }
            });


            ImageView mBack = (ImageView) mToolbar.findViewById(R.id.toolbar_img_left);
            mBack.setImageResource(R.drawable.backarrow_icon);
            mBack.setOnClickListener(new View.OnClickListener() {
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


    @Override
    protected void setUpLayout() {

        mTvUserName = (TextView) findViewById(R.id.tv_user_name_activity_user_profile);
        mTvUserLocation = (TextView) findViewById(R.id.tv_user_address_activity_user_profile);
        mTvUserDescription = (TextView) findViewById(R.id.tv_desc_activity_user_profile);
        mIvUserIcon = (ImageView) findViewById(R.id.iv_user_img_activity_user_profile);
        mIvCoverPic = (ImageView) findViewById(R.id.iv_cover_pic_activity_user_profile);
        mRvUserPost = (RecyclerView) findViewById(R.id.rv_posts_activity_user_profile);
        mTvNoPostAvailable = (TextView) findViewById(R.id.tv_no_post_available);
        mTvLabel=(TextView) findViewById(R.id.tv_label_activity_user_profile);
        mIvStar = (ImageView) findViewById(R.id.iv_star_activity_user_profile);
        mIvStar.setOnClickListener(this);
        mIvBlockUser = (ImageView) findViewById(R.id.iv_block_user_user_profile_activity);
        mIvBlockUser.setOnClickListener(this);

        mIvDropDown = (ImageView) findViewById(R.id.iv_dropdown_activity_user_profile);
        mIvDropDown.setOnClickListener(this);
        mRvUserPost.setLayoutManager(mLlManger);
        try {
//            index=12 coming from Chats Fragment PhoneBook Tab
//            index=11 coming from wall post user icon click
            Bundle bundle = getIntent().getExtras();
            mUserId = bundle.getString(Constants.USERID);
            INDEX = bundle.getInt(Constants.INDEX);
            mBusinessId = bundle.getString(Constants.BUSINESS_ID);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (INDEX == 12) {
            mIvStar.setVisibility(View.INVISIBLE);
        }
        if (mUserId.equalsIgnoreCase(PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""))) {
            mIvStar.setVisibility(View.INVISIBLE);
            mIvDropDown.setVisibility(View.INVISIBLE);
        }

        getUserProfile(mUserId);

        mRvUserPost.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                try {
                    if (dy > 0) //check for scroll down
                    {
                        visibleItemCount = mLlManger.getChildCount();
                        totalItemCount = mLlManger.getItemCount();
                        pastVisibleItems = mLlManger.findFirstVisibleItemPosition();

                        if (!isLoading) {

                            Log.d(LOG_TAG, " visibleItemCount " + visibleItemCount + " pastVisibleItems " + pastVisibleItems + " totalItemCount " + totalItemCount);
                            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                isLoading = true;
                                //Do pagination.. i.e. fetch new data
                                if (mUserWallPostList.size() < 1) {
                                    PAGE=1;
                                    viewAllPost();
                                }
                                if (mUserWallPostList.size() < totalPostCount) {
                                    Log.e("list size",""+mUserWallPostList.size());
                                    PAGE+=1;
                                    viewAllPost();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void setDataInViewObjects() {

    }


     /*
    * payload to call user profile service
     * @params token, userId
    * */

    public String createPayload(String userId) {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return payload.toString();
    }


    /*
    * service call to view user profile
    * */
    public void getUserProfile(String user_id) {

        try {
            String payload = createPayload(user_id);

            if (payload != null) {

                WebNotificationManager.registerResponseListener(responseUserProfile);
                WebServiceClient.view_user_profile(UserProfileActivity.this, payload, user_id, responseUserProfile);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ResponseHandlerListener responseUserProfile = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            try {
                WebNotificationManager.unRegisterResponseListener(responseUserProfile);

                if (error == null) {
                    if (result.getSuccess().equalsIgnoreCase("true")) {

                        userDetails = result;
                        try {
                            String name = userDetails.getProfile().getFirstName() + " " + userDetails.getProfile().getLastName();
                            mTvTitle.setText(name);
                            mTvUserName.setText(name);
                            mTvUserDescription.setText(userDetails.getProfile().getDesc());
                            try {
                                if (!userDetails.getProfile().getProfilePic().isEmpty()) {
                                    Glide.with(UserProfileActivity.this).load(WebServiceClient.HTTP_STAGING + userDetails.getProfile().getProfilePic())
                                            .thumbnail(1f)
                                            .crossFade()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(mIvUserIcon);
                                } else {
                                    mIvUserIcon.setImageResource(R.drawable.default_user);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                if (!userDetails.getProfile().getCoverPic().isEmpty()) {
                                    Glide.with(UserProfileActivity.this).load(WebServiceClient.HTTP_STAGING + userDetails.getProfile().getCoverPic())
                                            .thumbnail(1f)
                                            .crossFade()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(mIvCoverPic);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {

                                String countryCode=userDetails.getLocation().getCountryCode();
                                String[] rl=UserProfileActivity.this.getResources().getStringArray(R.array.countryList);
                                for(int i=0;i<rl.length;i++){
                                    String[] g=rl[i].split(",");
                                    if(g[1].trim().equals(countryCode.trim())){
                                       String countryName=g[0];
                                        mTvUserLocation.setText(countryName);
                                        break;  }

                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            } catch (Resources.NotFoundException e) {
                                e.printStackTrace();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.e("------------", "" + userDetails.getProfile().getFirstName() + userDetails.getProfile().getLastName());

                    } else {

                    }
                }

                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

                if(userDetails.is_favourite() && INDEX!=11){

                    mTvLabel.setText("Mutual Badges");
                    setMutualBadges(result.getBadges());
                }else {
                    viewAllPost();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };



//    set mutual Badges in recycler view
    public void setMutualBadges(ArrayList<BadgesPojo> mMutualBadges){
        try {
            mRvUserPost.setLayoutManager(mGlManager);
            mMutualBadgeAdapter=null;
            mMutualBadgeAdapter= new MyBadgesAdapter(this,mMutualBadges,onCardClickListner);
            mRvUserPost.setAdapter(mMutualBadgeAdapter);

        }catch (Exception e){
            e.printStackTrace();
        }

    }



    OnCardClickListner onCardClickListner=new OnCardClickListner() {
        @Override
        public void onCardClicked(ImageView view, int position) {

        }
    };


    /*
  *
  * create payload to get all post of a business
  *@Params  token, userId, roomId, postType, page, limit
  * */
    public String createPayload() {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.ROOM_ID, mUserId);
            payload.put(Constants.POST_TYPE, PROFILE);
            payload.put(Constants.PAGE, PAGE + "");
            payload.put(Constants.LIMIT, LIMIT + "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return payload.toString();
    }

    /*
    *
    *
    * to get all the post of this business wall
    *
    * */
    public void viewAllPost() {
        String payload = createPayload();
        if (!payload.isEmpty()) {

            WebNotificationManager.registerResponseListener(responseHandler);
            WebServiceClient.view_all_posts(this, payload, responseHandler);
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
                        mRvUserPost.setVisibility(View.VISIBLE);

                        if (mUserWallPostList != null)
//                            mUserWallPostList.clear();
                        mUserWallPostList.addAll(result.getPostList());

                        if (mUserWallPostList.size() > 0) {
                            try {
                                mWallPostAdapter = null;
                                mWallPostAdapter = new WallPostAdapter(UserProfileActivity.this, mUserWallPostList, Constants.USERPROFILEACTIVITY,onLikeClickListner,onDisLikeClickListner,onNeutralClickListner,onCommentClickListner,onMediaPostClickListner,onDeletePostClickListner);
                                mRvUserPost.setAdapter(mWallPostAdapter);
                                mRvUserPost.getRecycledViewPool().clear();
                                mWallPostAdapter.notifyDataSetChanged();


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {

                            mTvNoPostAvailable.setVisibility(View.VISIBLE);
                        }

                    }

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

    OnLikeClickListner onLikeClickListner =new OnLikeClickListner() {
        @Override
        public void onLikeClicked(TextView like, int position, String postId, String type) {
            try {
                POSITION=position;
                action = 1;
                likeUnlikePost(mActionTypeLike, postId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    OnDisLikeClickListner onDisLikeClickListner =new OnDisLikeClickListner() {
        @Override
        public void onDisLikeClicked(TextView dislike, int position, String postId, String type) {
            try {
                POSITION=position;
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
                POSITION=position;
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
            Bundle bundle = new Bundle();
            bundle.putSerializable("POST", mUserWallPostList.get(position));
            Intent intent = new Intent(UserProfileActivity.this, CommentActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);

        }
    };

    OnMediaPostClickListner onMediaPostClickListner= new OnMediaPostClickListner() {
        @Override
        public void onMediaPostClicked(ImageView media, int position, String postId, String type) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("POST", mUserWallPostList.get(position));
            Intent intent = new Intent(UserProfileActivity.this, CommentActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    OnDeletePostClickListner onDeletePostClickListner= new OnDeletePostClickListner() {
        @Override
        public void ondeletePostClicked(ImageView delete, int position, String postId, String type) {
            try {
                POSITION = position;
                removePost(postId);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //
// create payload to like unlike a post
//   token, userId, type(like/dislike/neutral), post_id
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
        //create payload with parameters is to be call here

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
                            int num_like = mUserWallPostList.get(POSITION).getLike();
                            int a = num_like;
                            if (result.getLike() == 1) {
                                mUserWallPostList.get(POSITION).setAction(Constants.LIKE);
                                mUserWallPostList.get(POSITION).setLike(a + 1);
                            } else if (result.getLike() == 0) {
                                mUserWallPostList.get(POSITION).setAction(null);
                                if (a > 0) {
                                    mUserWallPostList.get(POSITION).setLike(a - 1);
                                }
                            }


                        } else if (action == 2) {
                            //action dislike
                            int num_dislike = mUserWallPostList.get(POSITION).getDislike();
                            int b = num_dislike;
                            if (result.getDislike() == 1) {
                                mUserWallPostList.get(POSITION).setAction(Constants.DISLIKE);
                                mUserWallPostList.get(POSITION).setDislike(b + 1);

                            } else if (result.getDislike() == 0) {
                                mUserWallPostList.get(POSITION).setAction(null);
                                if (b > 0) {
                                    mUserWallPostList.get(POSITION).setDislike(b - 1);
                                }

                            }


                        } else if (action == 3) {
                            //    action neutral

                            int num_neutral = mUserWallPostList.get(POSITION).getNeutral();
                            int c = num_neutral;

                            if (result.getNeutral() == 1) {
                                mUserWallPostList.get(POSITION).setAction(Constants.NEUTRAL);
                                mUserWallPostList.get(POSITION).setNeutral(c + 1);
                            } else if (result.getNeutral() == 0) {
                                mUserWallPostList.get(POSITION).setAction(null);
                                if (c > 0) {
                                    mUserWallPostList.get(POSITION).setNeutral(c - 1);
                                }
                            }
                        }
                        mWallPostAdapter.notifyDataSetChanged();
                    }


                    // Refresh adapter
                    mWallPostAdapter.notifyDataSetChanged();
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

    /**
    * payload to set a user favourite
    *@Params:-token, userId, personId, businessId
    *
    * */
    public String generatePayload() {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.BUSINESS_ID, mBusinessId);
            payload.put(Constants.PERSON_ID, mUserId);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return payload.toString();
    }

    /**
     * set A user Favourite
     *
     */
    public void set_favourite() {

        try {
            String payload = generatePayload();
            if (payload != null) {

                WebNotificationManager.registerResponseListener(favouriteResponseHandler);
                WebServiceClient.set_favourite(this, payload, favouriteResponseHandler);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    ResponseHandlerListener favouriteResponseHandler = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            try {
                WebNotificationManager.unRegisterResponseListener(favouriteResponseHandler);

                if (error == null) {
                    if (result.getSuccess().equalsIgnoreCase("true")) {

                        Utility.showToastMessageLong(UserProfileActivity.this, "Set Favourite");
                    } else {
                        Utility.showToastMessageLong(UserProfileActivity.this, getResources().getString(R.string.some_unknown_error));
                    }
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


    /*
    * create payload to block an user
    * token, userId, personId
    * */
    public String generatePayload(String userId) {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.PERSON_ID, userId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return payload.toString();
    }


    public void blockUser(String userId) {
        mIvBlockUser.setVisibility(View.GONE);
        String payload = generatePayload(userId);
        if (payload != null) {

            WebNotificationManager.registerResponseListener(blockResponseHandler);
            WebServiceClient.block_a_user(UserProfileActivity.this, payload, blockResponseHandler);
        }

    }

    ResponseHandlerListener blockResponseHandler = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(blockResponseHandler);

            if (error == null) {

                if (result.getSuccess().equalsIgnoreCase("true")) {

Utility.showToastMessageShort(UserProfileActivity.this,"User blocked");

                }
            }


            //            finish the dialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_star_activity_user_profile:
                set_favourite();
                break;

            case R.id.iv_dropdown_activity_user_profile:

                if (IsBlockIconVisible == false) {

                    mIvBlockUser.setVisibility(View.VISIBLE);
                    IsBlockIconVisible = true;
                } else {
                    mIvBlockUser.setVisibility(View.GONE);
                    IsBlockIconVisible = false;
                }

                break;

            case R.id.iv_block_user_user_profile_activity:
            blockUser(mUserId);
                break;
            default:
                break;
        }
    }

      /*
    * create payload to flag/remove a post
    *
    *
    * */


    public String genratePayload(String postId) {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
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
                WebServiceClient.remove_a_post(this, payload, responseRemovePost);
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

//                        mHolder.mDeletePost.setVisibility(View.GONE);
                        mUserWallPostList.remove(POSITION);
//                        mWallPostAdapter = new WallPostAdapter(getActivity(), mWallPostList, onListItemClickListner);
                        mWallPostAdapter.notifyDataSetChanged();
//                      mRvWallPosts.setAdapter(mWallPostAdapter);


                        Utility.showToastMessageLong(UserProfileActivity.this, "post removed");

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
}
