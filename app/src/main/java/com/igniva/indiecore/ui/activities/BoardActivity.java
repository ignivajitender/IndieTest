package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.OnCommentClickListner;
import com.igniva.indiecore.controller.OnContactCardClickListner;
import com.igniva.indiecore.controller.OnDeletePostClickListner;
import com.igniva.indiecore.controller.OnDisLikeClickListner;
import com.igniva.indiecore.controller.OnLikeClickListner;
import com.igniva.indiecore.controller.OnMediaPostClickListner;
import com.igniva.indiecore.controller.OnNeutralClickListner;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.ChatListPojo;
import com.igniva.indiecore.model.PeoplesPojo;
import com.igniva.indiecore.model.PostPojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.ui.adapters.ChatListAdapter;
import com.igniva.indiecore.ui.adapters.PeoplesTabAdapter;
import com.igniva.indiecore.ui.adapters.WallPostAdapter;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by igniva-andriod-05 on 27/9/16.
 */
public class BoardActivity extends BaseActivity implements View.OnClickListener {
    public static final String mActionTypeLike = "like";
    public static final String mActionTypeDislike = "dislike";
    public static final String mActionTypeNeutral = "neutral";
    public final static String BUSINESS = "business";
    private Toolbar mToolbar;
    private TextView mTvTitle;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private boolean isLoading;
    private int totalPostCount = 0;
    private int PAGE = 1;
    private int LIMIT = 20;
    private int action = 0;
    public static int POSITION = -1;
    private TextView mChat, mBoard, mPeople, mCreatePost, mUserName, mComingSoon;
    private LinearLayout mLlBoard;
    private ImageView mUserImage;
    private ImageView starImage;
    private String LOG_TAG = "LOG_TAG";
    private WallPostAdapter mAdapter;
    private ArrayList<PostPojo> mWallPostList = new ArrayList<PostPojo>();
    private ArrayList<PeoplesPojo> mPeoplesList = new ArrayList<>();
    private PeoplesTabAdapter mPeoplesTabAdapter;
    private LinearLayoutManager mLlManager;
    private LinearLayoutManager mLlManagerChatRoom;
    private WallPostAdapter mWallPostAdapter;
    private RecyclerView mRvWallPosts, mRvPeoples;
    private String mBusinessId = "";
    private String mBusinessName = "";
    private String postID = "-1";
    private String BLOCK="block";
    private String REMOVE="remove";
    private ImageView mIvDelete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        initToolbar();
        setUpLayout();
    }


    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar_with_icon);
            mTvTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title_img);

            //
            ImageView mTvNext = (ImageView) mToolbar.findViewById(R.id.toolbar_img);
            mTvNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    startActivity(new Intent(BoardActivity.this, MyBadgesActivity.class));
                    Utility.showToastMessageShort(BoardActivity.this, getResources().getString(R.string.coming_soon));
                }
            });
            //

            ImageView backarrow = (ImageView) mToolbar.findViewById(R.id.toolbar_img_left);
            backarrow.setImageResource(R.drawable.backarrow_icon);
            backarrow.setOnClickListener(new View.OnClickListener() {
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
        try {
            mBusinessId = getIntent().getStringExtra(Constants.BUSINESS_ID);
            mBusinessName = getIntent().getStringExtra(Constants.BUSINESS_NAME);
            mTvTitle.setText(mBusinessName);

        } catch (Exception e) {
            e.printStackTrace();
        }
        mRvWallPosts = (RecyclerView) findViewById(R.id.rv_users_posts);
        mLlManager = new LinearLayoutManager(this);
        mRvWallPosts.setLayoutManager(mLlManager);

        mRvPeoples = (RecyclerView) findViewById(R.id.rv_peoples);
        mChat = (TextView) findViewById(R.id.tv_chat);
        mChat.setOnClickListener(this);
        mBoard = (TextView) findViewById(R.id.tv_board);
        mBoard.setOnClickListener(this);
        mPeople = (TextView) findViewById(R.id.tv_people);
        mPeople.setOnClickListener(this);
        mCreatePost = (TextView) findViewById(R.id.tv_create_post);
        mCreatePost.setOnClickListener(this);
        mUserName = (TextView) findViewById(R.id.tv_user_name_chat_fragment);
        mUserImage = (ImageView) findViewById(R.id.iv_user_img_chat_fragment);

        mComingSoon = (TextView) findViewById(R.id.tv_cuming_soon_Boardactivity);

        mLlBoard = (LinearLayout) findViewById(R.id.ll_board);
        mRvWallPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                try {
                    //check for scroll down
                    if (dy > 0) {
                        visibleItemCount = mLlManager.getChildCount();
                        totalItemCount = mLlManager.getItemCount();
                        pastVisibleItems = mLlManager.findFirstVisibleItemPosition();
                        if (!isLoading) {

                            Log.d(LOG_TAG, "lis size is " + mWallPostList.size() + " ======++++++ visibleItemCount " + visibleItemCount + " pastVisibleItems " + pastVisibleItems + " totalItemCount " + totalItemCount);
                            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                isLoading = true;
                                //Do pagination.. i.e. fetch new data
                                if (mWallPostList.size() < 1) {
                                    PAGE = 1;
                                    viewAllPost();
                                }
                                if (mWallPostList.size() < totalPostCount) {
                                    PAGE += 1;
                                    viewAllPost();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                    }
            }
        });
        setDataInViewObjects();

    }


    @Override
    protected void onResume() {
        super.onResume();
//            if(mWallPostList.size()==0){
        viewAllPost();
//            }
    }

    @Override
    protected void setDataInViewObjects() {

        try {
            if (!PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_FIRST_NAME, "").isEmpty()) {
                String Name = (PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_FIRST_NAME, "") + " " + (PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_LAST_NAME, "")).charAt(0) + ".");
                mUserName.setText(Name);
            }
            if (!PreferenceHandler.readString(this, PreferenceHandler.PROFILE_PIC_URL, "").isEmpty()) {
                Glide.with(this).load(WebServiceClient.HTTP_STAGING + PreferenceHandler.readString(this, PreferenceHandler.PROFILE_PIC_URL, ""))
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mUserImage);
            }

            updateBoardUi();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_chat:
                updateChatUi();
                break;
            case R.id.tv_board:
                updateBoardUi();
                break;
            case R.id.tv_people:
                updatePeopleUi();
                break;
            case R.id.tv_create_post:
                Intent intent = new Intent(BoardActivity.this, CreatePostActivity.class);
                intent.putExtra(Constants.BUSINESS_ID, mBusinessId);
                startActivity(intent);
                break;

            default:
                break;
        }
    }


    public void updateChatUi() {
        try {
            mLlManagerChatRoom = new LinearLayoutManager(this);
            mLlBoard.setVisibility(View.GONE);
            mRvWallPosts.setVisibility(View.GONE);
            mChat.setTextColor(Color.parseColor("#FFFFFF"));
            mChat.setBackgroundColor(Color.parseColor("#1C6DCE"));

            mBoard.setTextColor(Color.parseColor("#1C6DCE"));
            mBoard.setBackgroundResource(R.drawable.simple_border_line_style);

            mPeople.setTextColor(Color.parseColor("#1C6DCE"));
            mPeople.setBackgroundResource(R.drawable.simple_border_line_style);
            mComingSoon.setVisibility(View.VISIBLE);
            mComingSoon.setText(getResources().getString(R.string.no_conversations_found));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateBoardUi() {
        try {
            mLlBoard.setVisibility(View.VISIBLE);
            mComingSoon.setVisibility(View.GONE);
            mRvPeoples.setVisibility(View.GONE);
            mRvWallPosts.setVisibility(View.VISIBLE);
            mLlManager = new LinearLayoutManager(this);
            mRvWallPosts.setLayoutManager(mLlManager);
            mChat.setTextColor(Color.parseColor("#1C6DCE"));
            mChat.setBackgroundResource(R.drawable.simple_border_line_style);

            mBoard.setTextColor(Color.parseColor("#FFFFFF"));
            mBoard.setBackgroundColor(Color.parseColor("#1C6DCE"));

            mPeople.setTextColor(Color.parseColor("#1C6DCE"));
            mPeople.setBackgroundResource(R.drawable.simple_border_line_style);


            mAdapter = null;
            if (mWallPostList.size() > 0) {
                mAdapter = new WallPostAdapter(this, mWallPostList, Constants.CHATFRAGMENT, onLikeClickListner, onDisLikeClickListner, onNeutralClickListner, onCommentClickListner, onMediaPostClickListner, onDeleteClickListner);
                mRvWallPosts.setAdapter(mAdapter);
            }else {
                mComingSoon.setVisibility(View.VISIBLE);
                mComingSoon.setText(getResources().getString(R.string.no_post_found));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePeopleUi() {

        try {
            mLlBoard.setVisibility(View.GONE);
            mRvPeoples.setVisibility(View.VISIBLE);
            mLlManager = new LinearLayoutManager(this);
            mRvPeoples.setLayoutManager(mLlManager);
            mComingSoon.setVisibility(View.GONE);
            mComingSoon.setText(getResources().getString(R.string.coming_soon));
            mChat.setTextColor(Color.parseColor("#1C6DCE"));
            mChat.setBackgroundResource(R.drawable.simple_border_line_style);

            mBoard.setTextColor(Color.parseColor("#1C6DCE"));
            mBoard.setBackgroundResource(R.drawable.simple_border_line_style);

            mPeople.setTextColor(Color.parseColor("#FFFFFF"));
            mPeople.setBackgroundColor(Color.parseColor("#1C6DCE"));

            if (mPeoplesList.size() > 0) {
                mPeoplesTabAdapter = null;
                mPeoplesTabAdapter = new PeoplesTabAdapter(this, mPeoplesList, onStarClick,onBlockClick);
                mRvPeoples.setAdapter(mPeoplesTabAdapter);
            } else {
                get_all_peoples();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * payload to get all peoples in a business
     *
     * @return
     */

    public String generatePayload() {

        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.BUSINESS_ID, mBusinessId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return payload.toString();
    }


    /**
     * method to get all peoples of a business
     *
     * @return
     */
    public void get_all_peoples() {
        try {
            String payload = generatePayload();
            WebNotificationManager.registerResponseListener(response_peoples);
            WebServiceClient.get_business_peoples(this, payload, response_peoples);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    ResponseHandlerListener response_peoples = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            try {
                WebNotificationManager.unRegisterResponseListener(response_peoples);

                if (error == null) {

                    if (result.getSuccess().equalsIgnoreCase("true")) {
                        if (mPeoplesList.size() > 0) {
                            mPeoplesList.clear();
                        }
                        mPeoplesList.addAll(result.getPeoples());
                        if (mPeoplesList.size() > 0) {
                            mPeoplesTabAdapter = null;
                            mPeoplesTabAdapter = new PeoplesTabAdapter(BoardActivity.this, mPeoplesList, onStarClick,onBlockClick);
                            mRvPeoples.setAdapter(mPeoplesTabAdapter);
                        } else {
                            mComingSoon.setVisibility(View.VISIBLE);
                            mComingSoon.setText(getResources().getString(R.string.no_peoples_found));
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    };

    /**
     * create payload to get all post of a business
     * @Params:token, userId, roomId, postType, page, limit
     */
    public String createPayload() {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.ROOM_ID, mBusinessId);
            payload.put(Constants.POST_TYPE, BUSINESS);
            payload.put(Constants.PAGE, PAGE + "");
            payload.put(Constants.LIMIT, LIMIT + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return payload.toString();
    }

    /**
     * to get all the post of this business wall
     */
    public void viewAllPost() {
        try {
            String payload = createPayload();
            if (!payload.isEmpty()) {
                WebNotificationManager.registerResponseListener(responseHandler);
                WebServiceClient.view_all_posts(this, payload, responseHandler);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * posts response and list inflation
     */
    ResponseHandlerListener responseHandler = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(responseHandler);
            try {
                if (error == null) {
                    if (result.getSuccess().equalsIgnoreCase("true")) {
                        totalPostCount = result.getTotalPosts();
                        if (mWallPostList != null)
                            mWallPostList.clear();
                        mWallPostList.addAll(result.getPostList());

                        if (mWallPostList.size() > 0) {
                            try {
                                mComingSoon.setVisibility(View.GONE);
                                mWallPostAdapter = null;
                                mWallPostAdapter = new WallPostAdapter(BoardActivity.this, mWallPostList, Constants.CHATFRAGMENT, onLikeClickListner, onDisLikeClickListner, onNeutralClickListner, onCommentClickListner, onMediaPostClickListner, onDeleteClickListner);
                                mWallPostAdapter.notifyDataSetChanged();
                                mRvWallPosts.setAdapter(mWallPostAdapter);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            mComingSoon.setVisibility(View.VISIBLE);
                            mComingSoon.setText(getResources().getString(R.string.no_post_found));
                        }
                        isLoading = false;
                    }
                }

                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * create payload to like unlike a post
     * token, userId, type(like/dislike/neutral), post_id
     */
    public String createPayload(String type, String postId) {

        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(BoardActivity.this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(BoardActivity.this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.TYPE, type);
            payload.put(Constants.POST_ID, postId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return payload.toString();
    }


    /**
     * like/unlike/neutral action to a post
     * @parms post_id
     */
    public void likeUnlikePost(String type, String postId) {

        String payload = createPayload(type, postId);

        if (!payload.isEmpty()) {
            WebNotificationManager.registerResponseListener(responseHandlerLike);
            WebServiceClient.like_unlike_post(BoardActivity.this, payload, responseHandlerLike);
        }
    }

    /**
     * like unlike response
     */
    ResponseHandlerListener responseHandlerLike = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(responseHandlerLike);
            try {

                if (error == null) {

                    if (result.getSuccess().equalsIgnoreCase("true")) {

                        if (action == 1) {
                            //action like
                            int num_like = mWallPostList.get(POSITION).getLike();
                            int a = num_like;
                            if (result.getLike() == 1) {
                                mWallPostList.get(POSITION).setAction(Constants.LIKE);
                                mWallPostList.get(POSITION).setLike(a + 1);
                            } else if (result.getLike() == 0) {
                                mWallPostList.get(POSITION).setAction(null);
                                if (a > 0) {
                                    mWallPostList.get(POSITION).setLike(a - 1);
                                }
                            }


                        } else if (action == 2) {
                            //action dislike
                            int num_dislike = mWallPostList.get(POSITION).getDislike();
                            int b = num_dislike;

                            if (result.getDislike() == 1) {
                                mWallPostList.get(POSITION).setAction(Constants.DISLIKE);
                                mWallPostList.get(POSITION).setDislike(b + 1);

                            } else if (result.getDislike() == 0) {
                                mWallPostList.get(POSITION).setAction(null);
                                if (b > 0) {
                                    mWallPostList.get(POSITION).setDislike(b - 1);
                                }

                            }


                        } else if (action == 3) {
                            //    action neutral

                            int num_neutral = mWallPostList.get(POSITION).getNeutral();
                            int c = num_neutral;

                            if (result.getNeutral() == 1) {
                                mWallPostList.get(POSITION).setAction(Constants.NEUTRAL);
                                mWallPostList.get(POSITION).setNeutral(c + 1);
                            } else if (result.getNeutral() == 0) {
                                mWallPostList.get(POSITION).setAction(null);
                                if (c > 0) {
                                    mWallPostList.get(POSITION).setNeutral(c - 1);
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
     * create payload to flag/remove a post
     */
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

    /**
     * remove post call
     */
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


    /**
     * response Remove post
     */
    ResponseHandlerListener responseRemovePost = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(responseRemovePost);

            try {
                if (error == null) {
                    if (result.getSuccess().equalsIgnoreCase("true")) {

                        mIvDelete.setVisibility(View.GONE);
                        mWallPostList.remove(POSITION);
                        mWallPostAdapter.notifyDataSetChanged();
//                        Utility.showToastMessageLong(BoardActivity.this, "post removed");

                    } else {
                        Utility.showToastMessageLong(BoardActivity.this, getResources().getString(R.string.some_unknown_error));
                    }

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
     * flag post call
     */
    public void flagPost(String postId) {

        String payload = genratePayload(postId);
        if (payload != null) {

            WebNotificationManager.registerResponseListener(responseFlagPost);
            WebServiceClient.flag_a_post(BoardActivity.this, payload, responseFlagPost);

        }

    }


    /**
     * response flag post
     */
    ResponseHandlerListener responseFlagPost = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(responseFlagPost);
            try {

                if (error == null) {
                    if (result.getSuccess().equalsIgnoreCase("true")) {

                        mIvDelete.setVisibility(View.GONE);
                        Utility.showToastMessageLong(BoardActivity.this, "post reported");

                    }

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

    OnLikeClickListner onLikeClickListner = new OnLikeClickListner() {
        @Override
        public void onLikeClicked(TextView like, int position, String postId, String type) {
            try {
                POSITION = position;
                action = 1;
                likeUnlikePost(mActionTypeLike, mWallPostList.get(position).getPostId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    OnDisLikeClickListner onDisLikeClickListner = new OnDisLikeClickListner() {
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

    OnNeutralClickListner onNeutralClickListner = new OnNeutralClickListner() {
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


    OnCommentClickListner onCommentClickListner = new OnCommentClickListner() {
        @Override
        public void onCommentClicked(TextView comment, int position, String postId, String type) {
            try {
                POSITION = position;
                Bundle bundle = new Bundle();
                bundle.putSerializable("POST", mWallPostList.get(position));
                Intent intent = new Intent(BoardActivity.this, CommentActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    OnDeletePostClickListner onDeleteClickListner = new OnDeletePostClickListner() {
        @Override
        public void ondeletePostClicked(ImageView delete, int position, String postId, String ACTION) {
            try {
                POSITION = position;
                mIvDelete = delete;
                if (ACTION.equalsIgnoreCase("DELETE")) {
//TODO
                    showRemovePostAlertDialog(getResources().getString(R.string.delete_post), BoardActivity.this, postId,REMOVE);
//                    removePost(BoardActivity.this, postId);

                } else if (ACTION.equalsIgnoreCase("REPORT")) {

                    flagPost(postId);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    OnMediaPostClickListner onMediaPostClickListner = new OnMediaPostClickListner() {
        @Override
        public void onMediaPostClicked(ImageView media, int position, String postId, String type) {
            try {
                POSITION = position;
                Bundle bundle = new Bundle();
                bundle.putSerializable("POST", mWallPostList.get(position));
                Intent intent = new Intent(BoardActivity.this, CommentActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    /**
     * payload to set a user favourite
     *
     * @Params:-token, userId, personId, businessId
     */
    public String generatePayload(String userID) {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.BUSINESS_ID, mBusinessId);
            payload.put(Constants.PERSON_ID, userID);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return payload.toString();
    }

    /**
     * set A user Favourite
     */
    public void set_favourite(String UserID) {

        try {
            String payload = generatePayload(UserID);
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

                        if (result.getFavourite() == 1) {
                            for (int i = 0; i < mPeoplesList.size(); i++) {
                                if (mPeoplesList.get(i).getUserId().equalsIgnoreCase(result.getPersonId())) {
                                    mPeoplesList.get(i).setRelation("favourite");

                                }
                            }
                        } else {
                            for (int i = 0; i < mPeoplesList.size(); i++) {
                                if (mPeoplesList.get(i).getUserId().equalsIgnoreCase(result.getPersonId())) {
                                    mPeoplesList.get(i).setRelation("unKnown");
                                }
                            }
                        }
                        mPeoplesTabAdapter.notifyDataSetChanged();
                    } else {
                        Utility.showToastMessageLong(BoardActivity.this, getResources().getString(R.string.some_unknown_error));
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


    OnContactCardClickListner onStarClick = new OnContactCardClickListner() {
        @Override
        public void onContactCardClicked(ImageView view, int position, String userId) {
            try {
                starImage = view;
                set_favourite(userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    OnContactCardClickListner onBlockClick = new OnContactCardClickListner() {
        @Override
        public void onContactCardClicked(ImageView view, int position, String userId) {
            try {
//                starImage = view;
                POSITION=position;
                showRemovePostAlertDialog(getResources().getString(R.string.block_user),BoardActivity.this,userId,BLOCK);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    public  void showRemovePostAlertDialog(String message, final Context context, final String Id, final String method){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage( message);
        builder1.setCancelable(true);
        builder1.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(method.equalsIgnoreCase(REMOVE)) {
                    removePost(Id);
                }else {
                    blockUser(Id);
                }
            }
        });
        builder1.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                try {
                    mIvDelete.setVisibility(View.GONE);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    /**
     * create payload to block an user
     * token, userId, personId
     */
    public String createPayload(String userId) {
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
//        mIvBlockUser.setVisibility(View.GONE);
        String payload = createPayload(userId);
        if (payload != null) {
            WebNotificationManager.registerResponseListener(blockResponseHandler);
            WebServiceClient.block_a_user(BoardActivity.this, payload, blockResponseHandler);
        }
    }

    ResponseHandlerListener blockResponseHandler = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(blockResponseHandler);

            if (error == null) {
                if (result.getSuccess().equalsIgnoreCase("true")) {
                    mPeoplesList.remove(POSITION);
                       mPeoplesTabAdapter.notifyDataSetChanged();
                    Utility.showToastMessageShort(BoardActivity.this, "User blocked");

                }
            }
            //            finish the dialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    };


}
