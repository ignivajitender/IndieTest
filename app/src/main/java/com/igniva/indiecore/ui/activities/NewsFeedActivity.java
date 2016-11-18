package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
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
import com.igniva.indiecore.utils.AsyncResult;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;
import com.igniva.indiecore.utils.WebServiceClientUploadImage;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by igniva-andriod-05 on 2/11/16.
 */
public class NewsFeedActivity extends BaseActivity implements View.OnClickListener {

    public static final String mActionTypeLike = "like";
    public static final String mActionTypeDislike = "dislike";
    public static final String mActionTypeNeutral = "neutral";
    private static int PAGE = 1, LIMIT = 20;
    public final String TAG = "NewsFeedActivity";
    int pastVisibleItems, visibleItemCount, totalItemCount;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    //Twitter
    TwitterAuthClient authClient;
    private Toolbar mToolbar;
    private SwipeRefreshLayout swipeContainer;
    private ImageView mPostDelete;
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

                        mPostDelete.setVisibility(View.GONE);
                        if (result.getFlag() == 1) {
                            Utility.showToastMessageLong(NewsFeedActivity.this, getResources().getString(R.string.flagged));
                        } else {
                            Utility.showToastMessageLong(NewsFeedActivity.this, getResources().getString(R.string.unflagged));
                        }

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
    private ImageView mIvUserImage;
    private TextView mTvUserName;
    private LinearLayout mLlCreatePost;
    private LinearLayoutManager mLlManager;
    private ArrayList<PostPojo> mMyWallPostList = new ArrayList<PostPojo>();
    private WallPostAdapter mWallPostAdapter;
    private RecyclerView mRvTimeLinePost;
    private boolean isLoading;
    private int totalPostCount = 0;
    private String PROFILE = "profile";
    private String TIMELINE = "timeline";
    private String REMOVE = "remove";
    private int POSITION = -1;
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

   /* //Twitter
    private static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";
    private static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
    private static Twitter twitter;
    private static RequestToken requestToken;*/
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
    OnDeletePostClickListner onDeletePostClickListner = new OnDeletePostClickListner() {
        @Override
        public void ondeletePostClicked(ImageView delete, int position, String postId, String ACTION) {
            try {
                mPostDelete = delete;
                POSITION = position;
                if (ACTION.equalsIgnoreCase("DELETE")) {
//TODO
                    showRemovePostAlertDialog(getResources().getString(R.string.delete_post), NewsFeedActivity.this, postId, REMOVE);
//                    removePost(BoardActivity.this, postId);

                } else if (ACTION.equalsIgnoreCase("REPORT")) {

                    flagPost(postId);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private int action = 0;
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
    private String textMsgForSocialSite = "";
    private String imagePathForSocialSite = "";
    AsyncResult asyncResult = new AsyncResult() {

        @Override
        public void onTaskResponse(Object result, int urlResponseNo) {
            try {
                android.util.Log.e(TAG, result.toString()); // result is local image path
                // shareFeedToTwitter(textMsgForSocialSite, result.toString());
                imagePathForSocialSite = result.toString();
                //To Logout the session
                logOutFromTwitter();
                if (PreferenceHandler.readString(NewsFeedActivity.this, Constants.PREF_KEY_OAUTH_TOKEN, "").isEmpty()) {
                    twitterLogin();
                } else {
                    new UpdateTwitterStatus().execute(textMsgForSocialSite, result.toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);
        initToolbar();
        setUpLayout();
        setUpFacebookSdk();
        setUpTwitterSdk();
    }

    private void setUpTwitterSdk() {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(Constants.TWITTER_KEY, Constants.TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig));
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
        } catch (
                Exception e
                )

        {
            e.printStackTrace();
        }

    }

    private void setUpFacebookSdk() {
        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.e(TAG, "onSuccess");
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, error.getMessage());
            }
        });
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

    public void showRemovePostAlertDialog(String message, final Context context, final String Id, final String method) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (method.equalsIgnoreCase(REMOVE)) {
                    removePost(Id);
                }
            }
        });
        builder1.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                try {
                    mPostDelete.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

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
     * flag post call
     */
    public void flagPost(String postId) {

        String payload = genratePayload(postId);
        if (payload != null) {

            WebNotificationManager.registerResponseListener(responseFlagPost);
            WebServiceClient.flag_a_post(NewsFeedActivity.this, payload, responseFlagPost);

        }

    }

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

    public void selectSharingType(final String textMsg, final String imageUrl, final String videoUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Constants.SHARE);
        final CharSequence[] items = {"Facebook", "Twitter",
                "Cancel"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Facebook")) {
                    shareFeedToFacebook(textMsg, imageUrl, videoUrl);
                } else if (items[item].equals("Twitter")) {

                    textMsgForSocialSite = textMsg;
                    if (textMsgForSocialSite == null) {
                        textMsgForSocialSite = "";
                    }

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        new WebServiceClientUploadImage(null, NewsFeedActivity.this, asyncResult, WebServiceClient.HTTP_STAGING + imageUrl, Constants.DOWNLOAD).execute();
                    } else if (videoUrl != null && !videoUrl.isEmpty()) {
                        //Download Video
                        //new WebServiceClientUploadImage(null, NewsFeedActivity.this, asyncResult, WebServiceClient.HTTP_STAGING + imageUrl, Constants.DOWNLOAD).execute();
                    } else {
                        logOutFromTwitter(); // it is just clear the session
                        if (PreferenceHandler.readString(NewsFeedActivity.this, Constants.PREF_KEY_OAUTH_TOKEN, "").isEmpty()) {
                            twitterLogin();
                        } else {
                            new UpdateTwitterStatus().execute(textMsgForSocialSite, null);
                        }

                    }
                    //shareFeedToTwitter(textMsg, imageUrl, videoUrl);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void logOutFromTwitter() {
        PreferenceHandler.writeString(NewsFeedActivity.this, Constants.PREF_KEY_OAUTH_TOKEN, "");
        PreferenceHandler.writeString(NewsFeedActivity.this, Constants.PREF_KEY_OAUTH_SECRET, "");
    }

    /*private void shareFeedToTwitter(String textMsg, String imagePath) {

        if (textMsg == null) {
            textMsg = "";
        }

        *//*URL url = null;
        try {
            url = new URL(WebServiceClient.HTTP_STAGING + imageUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }*//*


        File myImageFile = new File(imagePath);
        Uri myImageUri = Uri.fromFile(myImageFile);
        Intent twitterIntent = new TweetComposer.Builder(this)
                .text(textMsg)
                .image(myImageUri).createIntent();
        startActivityForResult(twitterIntent, Constants.STARTACTIVITYFORRESULTFOR_TWITTER);

    }*/


    private void shareFeedToFacebook(String textMsg, String imageUrl, String videoUrl) {

        //if we want to logout the fb session use below code
        //LoginManager.getInstance().logOut();

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            if (textMsg == null) {
                textMsg = "";
            }
            if (imageUrl != null && !imageUrl.isEmpty()) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle(textMsg)
                        .setImageUrl(Uri.parse(WebServiceClient.HTTP_STAGING + imageUrl))
                        .build();

                shareDialog.show(linkContent);
            } else if (videoUrl != null && !videoUrl.isEmpty()) {
                //Todo here i am shring video as url
               /* ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle(textMsg)
                        .setContentUrl(Uri.parse(WebServiceClient.HTTP_STAGING + videoUrl))
                        .build();

                shareDialog.show(linkContent);*/
            } else {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle(textMsg)
                        .setContentUrl(Uri.parse(WebServiceClient.HTTP_STAGING))
                        .build();


                shareDialog.show(linkContent);
            }

        }

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (authClient != null) {
            if (authClient.getRequestCode() == requestCode) {
                authClient.onActivityResult(requestCode, resultCode, data);
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void twitterLogin() {
        authClient = new TwitterAuthClient();
        authClient.authorize(NewsFeedActivity.this, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.e(TAG, result.toString());
                TwitterAuthToken token = result.data.getAuthToken();
                PreferenceHandler.writeString(NewsFeedActivity.this, Constants.PREF_KEY_OAUTH_SECRET, token.secret);
                PreferenceHandler.writeString(NewsFeedActivity.this, Constants.PREF_KEY_OAUTH_TOKEN, token.token);

                new UpdateTwitterStatus().execute(textMsgForSocialSite, imagePathForSocialSite);

            }

            @Override
            public void failure(com.twitter.sdk.android.core.TwitterException exception) {
                Log.e(TAG, exception.getMessage());
            }
        });
    }


    class UpdateTwitterStatus extends AsyncTask<String, String, Boolean> {
        ProgressDialog pDialog = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(NewsFeedActivity.this);
            pDialog.setMessage("Posting to twitter...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Boolean doInBackground(String... args) {
            boolean responseStatus = false;
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(Constants.TWITTER_KEY);
                builder.setOAuthConsumerSecret(Constants.TWITTER_SECRET);

                // Access Token
                String access_token = PreferenceHandler.readString(NewsFeedActivity.this, Constants.PREF_KEY_OAUTH_TOKEN, "");
                // Access Token Secret
                String access_token_secret = PreferenceHandler.readString(NewsFeedActivity.this, Constants.PREF_KEY_OAUTH_SECRET, "");

                AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

                // Update status
                StatusUpdate statusUpdate = new StatusUpdate(args[0]);
                if (args[1] != null && !args[1].isEmpty()) {
                    statusUpdate.setMedia(new File(args[1]));
                }

                twitter4j.Status response = twitter.updateStatus(statusUpdate);

                Log.d("Status", response.getText());
                responseStatus = true;

            } catch (TwitterException e) {
                responseStatus = false;
                Log.d("Failed to post!", e.getMessage());
            }
            return responseStatus;
        }

        @Override
        protected void onPostExecute(Boolean result) {

			/* Dismiss the progress dialog after sharing */
            pDialog.dismiss();
            textMsgForSocialSite = "";
            imagePathForSocialSite = "";
            String msg = "Posted to Twitter!";
            if (!result) {
                msg = "Some Error Occured";
            }
            Toast.makeText(NewsFeedActivity.this, msg, Toast.LENGTH_SHORT).show();

        }

    }

    /*public boolean isTwitterAppInstalled() {

        boolean status = false;
        PackageManager pkManager = this.getPackageManager();
        try {
            PackageInfo pkgInfo = pkManager.getPackageInfo("com.twitter.android", 0);
            String getPkgInfo = pkgInfo.toString();

            if (getPkgInfo.equals("com.twitter.android")) {
                // APP INSTALLED
                status = true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            // APP NOT INSTALLED
            status = false;

        }
        return status;
    }*/
}



