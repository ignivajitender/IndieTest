package com.igniva.indiecore.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.OnCommentDeleteClickListner;
import com.igniva.indiecore.controller.OnCommentDislikeClickListner;
import com.igniva.indiecore.controller.OnCommentLikeClickListner;
import com.igniva.indiecore.controller.OnCommentNeutralClickListner;
import com.igniva.indiecore.controller.OnCommentReplyClickListner;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.CommentPojo;
import com.igniva.indiecore.model.PostPojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.ui.adapters.PostCommentAdapter;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-05 on 22/7/16.
 */
public class CommentActivity extends BaseActivity implements View.OnClickListener{

    private Toolbar mToolbar;
    private RecyclerView mRvComment;
    private ImageView mIvPostComment, mUserImage, mPostMedia, mDropDownOptions, mReportPost;
    private TextView mUserName, mPostTime, mPostText, mPostLike, mPostDislike, mPostNeutral, mPostComments, mPostShare;
    private EditText mEtCommentText;
    private LinearLayoutManager mLlmanager;
    private ArrayList<CommentPojo> mCommentList;
    private PostCommentAdapter mCommentAdapter;
    private String ACTION = "";
    private int INDEX = 0;
    private String mediaurl;
    private boolean isDeleteBtnVisible = false;
    public static final String mActionTypeLike = "like";
    public static final String mActionTypeDislike = "dislike";
    public static final String mActionTypeNeutral = "neutral";
    PostCommentAdapter.RecyclerViewHolders mHolder;
    private int POSITION = 0;
    String postId = "";
    int action = 0;
    PostPojo selected_post_data = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initToolbar();
        setUpLayout();
    }

    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar_comment_activity);
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
            TextView next = (TextView) mToolbar.findViewById(R.id.toolbar_next);
            next.setVisibility(View.GONE);
            TextView title = (TextView) mToolbar.findViewById(R.id.toolbar_title);
            title.setText("Comment");
        } catch (Exception e) {
            e.printStackTrace();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
    }

    @Override
    protected void setUpLayout() {
        try {
            mRvComment = (RecyclerView) findViewById(R.id.rv_comment_activity);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRvComment.setLayoutManager(layoutManager);
            mIvPostComment = (ImageView) findViewById(R.id.iv_post_comment);
            mIvPostComment.setOnClickListener(this);
            mEtCommentText = (EditText) findViewById(R.id.et_comment_text);
            mUserName = (TextView) findViewById(R.id.tv_user_name_comments);
            mUserImage = (ImageView) findViewById(R.id.iv_user_img_comment_activity);
            mPostText = (TextView) findViewById(R.id.tv_post_text_comment_activity);
            mPostLike = (TextView) findViewById(R.id.tv_like_comment_activity);
            mPostLike.setOnClickListener(this);
            mPostDislike = (TextView) findViewById(R.id.tv_dislike_comment_activity);
            mPostDislike.setOnClickListener(this);
            mPostNeutral = (TextView) findViewById(R.id.tv_neutral_comment_activity);
            mPostNeutral.setOnClickListener(this);
            mPostComments = (TextView) findViewById(R.id.tv_comment_comment_activity);
            mPostTime = (TextView) findViewById(R.id.tv_post_time_comment_activity);
            mPostMedia = (ImageView) findViewById(R.id.iv_media_post_comment_activity);

            mDropDownOptions = (ImageView) findViewById(R.id.iv_drop_down_menu);
            mDropDownOptions.setOnClickListener(this);
            mReportPost = (ImageView) findViewById(R.id.iv_delete_report_post);
            mReportPost.setOnClickListener(this);

            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();
            selected_post_data = (PostPojo) bundle.getSerializable("POST");
            postId = selected_post_data.getPostId();

             mediaurl=selected_post_data.getMediaUrl();


            setDataInViewObjects();

        } catch (Exception e) {
       e.printStackTrace();
        }

    }

    @Override
    protected void setDataInViewObjects() {

        try {
            String Name = ((selected_post_data.getFirstName()) + " " + (selected_post_data.getLastName()).charAt(0) + ".");
            mUserName.setText(Name);
            try {
                if (!selected_post_data.getProfile_pic().isEmpty() && selected_post_data.getProfile_pic()!=null) {
                    Glide.with(this).load(WebServiceClient.HTTP_STAGING + selected_post_data.getProfile_pic())
                            .thumbnail(1f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(mUserImage);
                } else {
                    mUserImage.setImageResource(R.drawable.default_user);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (!selected_post_data.getMediaUrl().isEmpty()) {
                    mPostMedia.setVisibility(View.VISIBLE);
                    Glide.with(this).load(WebServiceClient.HTTP_STAGING + selected_post_data.getMediaUrl())
                            .thumbnail(1f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(mPostMedia);
                }
            }catch (Exception e){
                e.printStackTrace();
            }



            mPostTime.setText((selected_post_data.getDate_created()).substring(0,10));
            mPostText.setText(selected_post_data.getText());
            mPostLike.setText(selected_post_data.getLike()+"");
            mPostDislike.setText(selected_post_data.getDislike()+"");
            mPostNeutral.setText(selected_post_data.getNeutral()+"");
            mPostComments.setText(selected_post_data.getComment()+"");

            if (selected_post_data.getAction() != null) {
                if (selected_post_data.getAction().equalsIgnoreCase(Constants.LIKE)) {
                    mPostLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_blue_icon_circle, 0, 0, 0);
                    mPostDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_grey_icon_circle, 0, 0, 0);
                    mPostNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_grey_icon_circle, 0, 0, 0);
                    mPostLike.setEnabled(true);
                    mPostDislike.setEnabled(false);
                    mPostNeutral.setEnabled(false);
                } else if (selected_post_data.getAction().equalsIgnoreCase(Constants.DISLIKE)) {
                    mPostDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_blue_icon_circle, 0, 0, 0);
                    mPostLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon_circle, 0, 0, 0);
                    mPostNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_grey_icon_circle, 0, 0, 0);
                    mPostLike.setEnabled(false);
                    mPostDislike.setEnabled(true);
                    mPostNeutral.setEnabled(false);
                } else if (selected_post_data.getAction().equalsIgnoreCase(Constants.NEUTRAL)) {
                    mPostNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_icon_blue_circle, 0, 0, 0);
                    mPostLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon_circle, 0, 0, 0);
                    mPostDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_grey_icon_circle, 0, 0, 0);
                    mPostLike.setEnabled(false);
                    mPostDislike.setEnabled(false);
                    mPostNeutral.setEnabled(true);
                }else {
                    mPostLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon_circle, 0, 0, 0);
                    mPostDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_grey_icon_circle, 0, 0, 0);
                    mPostNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_grey_icon_circle, 0, 0, 0);
                    mPostLike.setEnabled(true);
                    mPostDislike.setEnabled(true);
                    mPostNeutral.setEnabled(true);
                }
            } else {
                mPostLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon_circle, 0, 0, 0);
                mPostDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_grey_icon_circle, 0, 0, 0);
                mPostNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_grey_icon_circle, 0, 0, 0);
                mPostLike.setEnabled(true);
                mPostDislike.setEnabled(true);
                mPostNeutral.setEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewAllComments(postId);

    }

    @Override
    public void onClick(View v) {

        postId = selected_post_data.getPostId();

        switch (v.getId()) {

            case R.id.iv_post_comment:
                try {
                    String comment_text = mEtCommentText.getText().toString().trim();
                    if (comment_text.isEmpty()) {
                        Utility.showAlertDialog(getResources().getString(R.string.comment_text_validation_message), this,null);
                        return;
                    } else {
                        postComment(selected_post_data.getPostId(), comment_text);


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.tv_like_comment_activity:
                action = 1;
                likeUnlikePost(mActionTypeLike, postId);

                break;

            case R.id.tv_dislike_comment_activity:
                action = 2;
                likeUnlikePost(mActionTypeDislike, postId);
                break;

            case R.id.tv_neutral_comment_activity:
                action = 3;
                likeUnlikePost(mActionTypeNeutral, postId);

                break;

            case R.id.iv_drop_down_menu:
                try {
                    if (isDeleteBtnVisible == false) {

                        if (selected_post_data.getRelation().equalsIgnoreCase("self")) {
                            mReportPost.setVisibility(View.VISIBLE);
                            mReportPost.setImageResource(R.drawable.delete_button);
                            ACTION = "DELETE";
                            isDeleteBtnVisible = true;

                        } else {
                            mReportPost.setVisibility(View.VISIBLE);
                            mReportPost.setImageResource(R.drawable.report_abuse);
                            ACTION = "REPORT";
                            isDeleteBtnVisible = true;

                        }
                    } else {
                        mReportPost.setVisibility(View.GONE);
                        isDeleteBtnVisible = false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.iv_delete_report_post:
                try {
                    if (ACTION.equalsIgnoreCase("DELETE")) {
                        showRemovePostAlertDialog(getResources().getString(R.string.delete_post), CommentActivity.this, postId);
                    } else {

                        flagPost(postId);
                    }
                } finally {

                }

                break;


            default:
                break;
        }

    }
/**
    // create payload to like unlike a post
 //        token, userId, type(like/dislike/neutral), post_id
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


    /**
  * like/unlike/neutral action to a post
  * @parms post_id
  *
  * */
    public void likeUnlikePost(String type, String postId) {
        //create payload with parameters is to be call here

        String payload = createPayload(type, postId);

        if (!payload.isEmpty()) {

            WebNotificationManager.registerResponseListener(responseHandl);
            WebServiceClient.like_unlike_post(this, payload, responseHandl);
        }
    }


    /**
 * like unlike response to  a post
 * */
    ResponseHandlerListener responseHandl = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(responseHandl);
            try {

                if (error == null) {

                    if (result.getSuccess().equalsIgnoreCase("true")) {
                        if (action == 1) {
//action like
                            String num_like = mPostLike.getText().toString();
                            int a = Integer.parseInt(num_like);

                            if (result.getLike() == 1) {
                                mPostLike.setText(a + 1 + "");
                                mPostLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_blue_icon_circle, 0, 0, 0);
                                mPostDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_grey_icon_circle, 0, 0, 0);
                                mPostNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_grey_icon_circle, 0, 0, 0);
                                mPostLike.setEnabled(true);
                                mPostDislike.setEnabled(false);
                                mPostNeutral.setEnabled(false);

                            } else {
                                if (a > 0) {
                                    mPostLike.setText(a - 1 + "");
                                }
                                mPostLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon_circle, 0, 0, 0);
                                mPostDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_grey_icon_circle, 0, 0, 0);
                                mPostNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_grey_icon_circle, 0, 0, 0);
                                mPostLike.setEnabled(true);
                                mPostDislike.setEnabled(true);
                                mPostNeutral.setEnabled(true);

                            }
                        } else if (action == 2) {
//    action dislike

                            String num_dislike = mPostDislike.getText().toString();
                            int b = Integer.parseInt(num_dislike.trim());


                            if (result.getDislike() == 1) {
                                mPostDislike.setText(b + 1 + "");
                                mPostDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_blue_icon_circle, 0, 0, 0);
                                mPostLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon_circle, 0, 0, 0);
                                mPostNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_grey_icon_circle, 0, 0, 0);

                                mPostLike.setEnabled(false);
                                mPostDislike.setEnabled(true);
                                mPostNeutral.setEnabled(false);


                            } else {

                                if (b > 0) {
                                    mPostDislike.setText(b - 1 + "");
                                }
                                mPostLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon_circle, 0, 0, 0);
                                mPostDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_grey_icon_circle, 0, 0, 0);
                                mPostNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_grey_icon_circle, 0, 0, 0);
                                mPostLike.setEnabled(true);
                                mPostDislike.setEnabled(true);
                                mPostNeutral.setEnabled(true);


                            }


                        } else if (action == 3) {
//    action neutral

                            String num_neutral = mPostNeutral.getText().toString();
                            int c = Integer.parseInt(num_neutral.trim());


                            if (result.getNeutral() == 1) {
                                mPostNeutral.setText(c + 1 + "");

                                mPostNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_icon_blue_circle, 0, 0, 0);
                                mPostLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon_circle, 0, 0, 0);
                                mPostDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_grey_icon_circle, 0, 0, 0);

                                mPostLike.setEnabled(false);
                                mPostDislike.setEnabled(false);
                                mPostNeutral.setEnabled(true);


                            } else {
                                if (c > 0) {
                                    mPostNeutral.setText(c - 1 + "");
                                }

                                mPostNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_grey_icon_circle, 0, 0, 0);
                                mPostLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon_circle, 0, 0, 0);
                                mPostDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_grey_icon_circle, 0, 0, 0);
                                mPostLike.setEnabled(true);
                                mPostDislike.setEnabled(true);
                                mPostNeutral.setEnabled(true);

                            }

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


    /**
 * payload to write  a comment to a post
 *
 *
 * */
    public String genratePayload(String postId, String text) {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.POSTID, postId);
            payload.put(Constants.TEXT, text);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return payload.toString();
    }

    /**
    * to post a comment for a businesspost
    *
    * */
    public void postComment(String postId, String text) {

        String payload = genratePayload(postId, text);
        if (payload != null) {

            WebNotificationManager.registerResponseListener(responseHandlerComment);
            WebServiceClient.make_a_comment(this, payload, responseHandlerComment);
        }

    }

    /**
    *
    * response of  a comment post
    * */
    ResponseHandlerListener responseHandlerComment = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            WebNotificationManager.unRegisterResponseListener(responseHandlerComment);

            if (error == null) {

                if (result.getSuccess().equalsIgnoreCase("true")) {

                                        InputMethodManager imm = (InputMethodManager) getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);


                    Utility.showToastMessageLong(CommentActivity.this, "comment posted");
                    String comment_count=mPostComments.getText().toString();
                    int count=Integer.parseInt(comment_count.trim());
                    mPostComments.setText(count+1+"");
                    mEtCommentText.setText("");
                    viewAllComments(postId);

                } else {

                }
            } else {

            }

            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    };


    /**
    *
    * payload to get comments of a post
    *
    * */
    public String createPayload(String postId) {
//        PARAMETER: token, userId, postId, page, limit
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.POSTID, postId);
            payload.put(Constants.PAGE, "1");
            payload.put(Constants.LIMIT, "10");


        } catch (Exception e) {
            e.printStackTrace();
        }
        return payload.toString();
    }


    /**
    *
    * to get all comments of a post
    *
    * */
    public void viewAllComments(String postId) {

        try {
            String payload = createPayload(postId);
            if (payload != null) {

                WebNotificationManager.registerResponseListener(responseHandle);
                WebServiceClient.view_all_comments(this, payload, responseHandle);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
    *
    * response all get comments
    * */
    ResponseHandlerListener responseHandle = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            try {
                WebNotificationManager.unRegisterResponseListener(responseHandle);
                if (error == null) {
                    if (result.getSuccess().equalsIgnoreCase("true")) {

                        mPostComments.setText(result.getTotalComments()+"");
                        mCommentList = new ArrayList<>();
                        mCommentList.addAll(result.getCommentList());

                        mCommentAdapter = null;
                        mCommentAdapter = new PostCommentAdapter(CommentActivity.this, mCommentList, onCommentLikeClickListner,onCommentDislikeClickListner,onCommentNeutralClickListner,onCommentReplyClickListner,onCommentDeleteClickListner);
                        mRvComment.setAdapter(mCommentAdapter);
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
   * token, userId, type(like/dislike/neutral), commentId
   * generate payload to like unlike a comment
   *
   *
   * */

    public String createPayloadLikeUNLikeComment(String type, String commentId) {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.TYPE, type);
            payload.put(Constants.COMMENTID, commentId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return payload.toString();
    }

    /**
    *
    * call request to like unlike a comment
    *
    *
    * */


    public void commentAction(String type, String commentId) {

        String payload = createPayloadLikeUNLikeComment(type, commentId);
        if (payload != null) {

            WebNotificationManager.registerResponseListener(responseCommentAction);
            WebServiceClient.like_unlike_a_comment(this, payload, responseCommentAction);
        }

    }

    /**
    * response LIke unlike a comment
    *
    *
    * */
    ResponseHandlerListener responseCommentAction = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            WebNotificationManager.unRegisterResponseListener(responseCommentAction);


            try {
                if (error == null) {

                    if (result.getSuccess().equalsIgnoreCase("true")) {

                        if (INDEX == 1) {
                            //                        like
                            int likes_count = mCommentList.get(POSITION).getLike();
                            int a = 0;
                            a =likes_count;
                            if (result.getLike() == 1) {

                                mCommentList.get(POSITION).setAction(Constants.LIKE);
                                mCommentList.get(POSITION).setLike(a+1);

                            } else if(result.getLike()==0){
                                mCommentList.get(POSITION).setAction(null);
                                if (a > 0) {
                                    mCommentList.get(POSITION).setLike(a-1);
                                }
                            }

                        } else if (INDEX == 2) {
                            //                        dislike
                            int dislikes_count = mCommentList.get(POSITION).getDislike();
                            int b = 0;
                            b = dislikes_count;
                            if (result.getDislike() == 1) {
                                mCommentList.get(POSITION).setAction(Constants.DISLIKE);
                                mCommentList.get(POSITION).setDislike(b+1);

                            } else if(result.getDislike()==0) {
                                mCommentList.get(POSITION).setAction(null);
                                if (b > 0) {
                                    mCommentList.get(POSITION).setDislike(b-1);
                                }
                            }

                        } else  if(INDEX==3) {
                            //                        neutral
                            int neutral_count =mCommentList.get(POSITION).getNeutral();
                            int c = 0;
                            c = neutral_count;
                            if (result.getNeutral() == 1) {

                                mCommentList.get(POSITION).setAction(Constants.NEUTRAL);
                                mCommentList.get(POSITION).setNeutral(c+1);

                            } else if(result.getNeutral()==0) {
                                mCommentList.get(POSITION).setAction(null);
                                if (c > 0) {
                                    mCommentList.get(POSITION).setNeutral(c-1);
                                }
                            }
                        }

                        mCommentAdapter.notifyDataSetChanged();
                    }
                    mCommentAdapter.notifyDataSetChanged();

                    //                Utility.showToastMessageLong(CommentActivity.this,"Comment like/unlike");


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
    *
    * Create payload to remove a comment
    *   token, userId, commentId
    * */

    public String createPayload_Remove_Comment(String commentId) {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.COMMENTID, commentId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return payload.toString();
    }


    /**
    *
    * call to remove a comment
    * */


    private void removeComment(String commentId) {

        String payload = createPayload_Remove_Comment(commentId);
        if (payload != null) {

            WebNotificationManager.registerResponseListener(responseCommentRemove);
            WebServiceClient.remove_a_comment(this, payload, responseCommentRemove);
        }

    }


    /**
   * response remove a comment
   *
   *
   * */
    ResponseHandlerListener responseCommentRemove = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            WebNotificationManager.unRegisterResponseListener(responseCommentRemove);
            try {

                if(error==null){
                    if(result.getSuccess().equalsIgnoreCase("true")){

                        Utility.showToastMessageLong(CommentActivity.this,"comment deleted");

                        String comment_count=mPostComments.getText().toString();
                        int count=Integer.parseInt(comment_count.trim());
                        mPostComments.setText(count-1+"");
                        mCommentList.remove(POSITION);
                        mCommentAdapter.notifyDataSetChanged();

                    }else {



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
    * create payload to flag/remove a post
    *
    *
    * */


    private String genratePayload(String postId) {
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
    *
    * */
    private void removePost(String postId) {
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

//remove this post from list
//                        mWallPostList.remove(POSITION);
                        mReportPost.setVisibility(View.GONE);
                        Utility.showToastMessageLong(CommentActivity.this, "post removed");
                        finish();

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
    *
    * flag post call
    *
    *
    * */
    private void flagPost(String postId) {

        String payload = genratePayload(postId);
        if (payload != null) {

            WebNotificationManager.registerResponseListener(responseFlagPost);
            WebServiceClient.flag_a_post(this, payload, responseFlagPost);

        }

    }


    /**
    * response flag post
    * */
    ResponseHandlerListener responseFlagPost = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(responseFlagPost);
            try {

                if (error == null) {
                    if (result.getSuccess().equalsIgnoreCase("true")) {
                        mReportPost.setVisibility(View.GONE);
                        Utility.showToastMessageLong(CommentActivity.this,getResources().getString(R.string.post_reported));


                    }else {
                        Utility.showToastMessageShort(CommentActivity.this,getResources().getString(R.string.some_unknown_error));
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




    OnCommentLikeClickListner onCommentLikeClickListner =new OnCommentLikeClickListner() {
        @Override
        public void onCommentLikeClicked(TextView like, int position, String commentId, String type) {
            try {
                INDEX = 1;
                POSITION = position;
                commentAction(mActionTypeLike, commentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    OnCommentDislikeClickListner onCommentDislikeClickListner=new OnCommentDislikeClickListner() {
        @Override
        public void onCommentDislikeClicked(TextView dislike, int position, String commentId, String type) {
            INDEX = 2;
            POSITION = position;
            commentAction(mActionTypeDislike, commentId);

        }
    };


    OnCommentNeutralClickListner onCommentNeutralClickListner=new OnCommentNeutralClickListner() {
        @Override
        public void onCommentNeutralClicked(TextView neutral, int position, String commentId, String type) {
            INDEX = 3;
            POSITION = position;
            commentAction(mActionTypeNeutral, commentId);

        }
    };

    OnCommentDeleteClickListner onCommentDeleteClickListner=new OnCommentDeleteClickListner() {
        @Override
        public void onCommentDeleteClicked(ImageView delete, int position, String commentId, String type) {
            POSITION = position;
            removeComment(commentId);
        }
    };


    OnCommentReplyClickListner onCommentReplyClickListner=new OnCommentReplyClickListner() {
        @Override
        public void onCommentreplyClicked(TextView reply, int position, String commentId, String type) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("COMMENT", mCommentList.get(position));

            Intent intent = new Intent(CommentActivity.this, CommentsReplyActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);

        }
    };

    private  void showRemovePostAlertDialog(String message, final Context context, final String postId){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage( message);
        builder1.setCancelable(true);
        builder1.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removePost(postId);
            }
        });
        builder1.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


}
