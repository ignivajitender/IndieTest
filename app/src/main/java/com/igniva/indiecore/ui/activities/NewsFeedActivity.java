package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.igniva.indiecore.controller.OnDeletePostClickListner;
import com.igniva.indiecore.controller.OnDisLikeClickListner;
import com.igniva.indiecore.controller.OnLikeClickListner;
import com.igniva.indiecore.controller.OnMediaPostClickListner;
import com.igniva.indiecore.controller.OnNeutralClickListner;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.PostPojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.ui.adapters.WallPostAdapter;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-05 on 2/11/16.
 */
public class NewsFeedActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    public static final String mActionTypeLike = "like";
    public static final String mActionTypeDislike = "dislike";
    public static final String mActionTypeNeutral = "neutral";
    private SwipeRefreshLayout swipeContainer;
    private ImageView mPostDelete;
    private ImageView mIvUserImage;
    private TextView mTvUserName;
    private LinearLayout mLlCreatePost;
    private LinearLayoutManager mLlManager;
    private ArrayList<PostPojo> mMyWallPostList = new ArrayList<PostPojo>();
    private WallPostAdapter mWallPostAdapter;
    private RecyclerView mRvTimeLinePost;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private boolean isLoading;
    private int totalPostCount = 0;
    private static int PAGE = 1, LIMIT = 20;
    private String PROFILE = "profile";
    private String TIMELINE = "timeline";
    private int POSITION = -1;
    private int action = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);
        initToolbar();
        setUpLayout();
    }

    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar_with_icon_news_feed);
            TextView mTvTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title_img);
            mTvTitle.setText(getResources().getString(R.string.news_feed));
            //
            ImageView mIvBadges = (ImageView) mToolbar.findViewById(R.id.toolbar_img);
            mIvBadges.setVisibility(View.INVISIBLE);
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


    @Override
    protected void setUpLayout() {
        try {
            mLlManager = new LinearLayoutManager(this);
            mLlCreatePost = (LinearLayout) findViewById(R.id.ll_lower_nf);
            mLlCreatePost.setOnClickListener(this);
            mTvUserName = (TextView) findViewById(R.id.tv_user_name_nf);
            mIvUserImage = (ImageView) findViewById(R.id.iv_user_img_nf);
            mRvTimeLinePost = (RecyclerView) findViewById(R.id.rv_user_post_nf);
            mRvTimeLinePost.setLayoutManager(mLlManager);
            swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override

                public void onRefresh() {
                    swipeContainer.setRefreshing(true);
                    viewMyPost();
                }

            });

            // Configure the refreshing colors

            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,

                    android.R.color.holo_green_light,

                    android.R.color.holo_orange_light,

                    android.R.color.holo_red_light);



        setDataInViewObjects();
    }

    catch(
    Exception e
    )

    {
        e.printStackTrace();
    }

}


    @Override
    protected void onResume() {
        viewMyPost();
        super.onResume();
    }

    @Override
    protected void setDataInViewObjects() {

        try {
            if (!PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_FIRST_NAME, "").isEmpty()) {
                String Name = ((PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_FIRST_NAME, "")) + " " + (PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_LAST_NAME, "")));
                mTvUserName.setText(Name);
            }
            if (PreferenceHandler.readString(this, PreferenceHandler.PROFILE_PIC_URL, null) != null) {

                Glide.with(this).load(WebServiceClient.HTTP_STAGING + PreferenceHandler.readString(this, PreferenceHandler.PROFILE_PIC_URL, ""))
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mIvUserImage);

            } else {
                mIvUserImage.setImageResource(R.drawable.default_user);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * create payload to get all post of a user
     * token, userId, roomId, postType, page, limit
     */
    public String createPayload() {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.ROOM_ID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.POST_TYPE, TIMELINE);
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
    public void viewMyPost() {
        try {
            String payload = createPayload();
            if (!payload.isEmpty()) {

                WebNotificationManager.registerResponseListener(responseHandler);
                WebServiceClient.view_all_posts(NewsFeedActivity.this, payload, responseHandler);
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
                        if (mMyWallPostList.size() > 0) {
                            mMyWallPostList.clear();
                        }
                        mMyWallPostList.addAll(result.getPostList());

                        if (mMyWallPostList.size() > 0) {
                            try {
                                mWallPostAdapter = null;
                                mWallPostAdapter = new WallPostAdapter(NewsFeedActivity.this, mMyWallPostList, Constants.NEWSFEEDACTIVITY, onLikeClickListner, onDisLikeClickListner, onNeutralClickListner, onCommentClickListner, onMediaPostClickListner, onDeletePostClickListner);
                                mRvTimeLinePost.setAdapter(mWallPostAdapter);

                                swipeContainer.setRefreshing(false);

//                                mRvTimeLinePost.getRecycledViewPool().clear();
//                                mWallPostAdapter.notifyDataSetChanged();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
                isLoading = false;
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    OnLikeClickListner onLikeClickListner = new OnLikeClickListner() {
        @Override
        public void onLikeClicked(TextView like, int position, String postId, String type) {
            try {
                POSITION = position;
                action = 1;
                likeUnlikePost(mActionTypeLike, mMyWallPostList.get(position).getPostId());
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
            POSITION = position;
            Bundle bundle = new Bundle();
            bundle.putSerializable("POST", mMyWallPostList.get(position));
            Intent intent = new Intent(NewsFeedActivity.this, CommentActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);

        }
    };

    OnMediaPostClickListner onMediaPostClickListner = new OnMediaPostClickListner() {
        @Override
        public void onMediaPostClicked(ImageView media, int position, String postId, String type) {
            POSITION = position;
            Bundle bundle = new Bundle();
            bundle.putSerializable("POST", mMyWallPostList.get(position));
            Intent intent = new Intent(NewsFeedActivity.this, CommentActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    OnDeletePostClickListner onDeletePostClickListner = new OnDeletePostClickListner() {
        @Override
        public void ondeletePostClicked(ImageView delete, int position, String postId, String type) {
            try {
                mPostDelete = delete;
                POSITION = position;
                removePost(postId);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * create payload to flag/remove a post
     */
    private String genratePayload(String postId) {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(NewsFeedActivity.this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(NewsFeedActivity.this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.POST_ID, postId);


        } catch (Exception e) {

            e.printStackTrace();
        }

        return payload.toString();
    }

    /**
     * remove post call
     */
    private void removePost(String postId) {
        try {


            String payload = genratePayload(postId);
            if (!payload.isEmpty()) {

                WebNotificationManager.registerResponseListener(responseRemovePost);
                WebServiceClient.remove_a_post(NewsFeedActivity.this, payload, responseRemovePost);
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
                        mPostDelete.setVisibility(View.GONE);
                        mMyWallPostList.remove(POSITION);
                        mWallPostAdapter.notifyDataSetChanged();
                        Utility.showToastMessageLong(NewsFeedActivity.this, "post removed");

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


    /**
     * // create payload to like unlike a post
     * //Params:token, userId, type(like/dislike/neutral), post_id
     * //
     */
    private String createPayload(String type, String postId) {

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


    /**
     * like/unlike/neutral action to a post
     *
     * @parms post_id
     */
    private void likeUnlikePost(String type, String postId) {
        String payload = createPayload(type, postId);
        if (!payload.isEmpty()) {
            WebNotificationManager.registerResponseListener(responseHandlerLike);
            WebServiceClient.like_unlike_post(this, payload, responseHandlerLike);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_lower_nf:
                try {
                    Intent intent = new Intent(NewsFeedActivity.this, CreatePostActivity.class);
                    intent.putExtra(Constants.BUSINESS_ID, PreferenceHandler.readString(NewsFeedActivity.this, PreferenceHandler.PREF_KEY_USER_ID, ""));
                    intent.putExtra(Constants.CONTEXT_NAME, Constants.NEWS_FEED_ACTIVITY);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
