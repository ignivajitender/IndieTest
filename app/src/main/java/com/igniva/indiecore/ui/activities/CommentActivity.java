package com.igniva.indiecore.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
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
public class CommentActivity extends BaseActivity {

    private Toolbar mToolbar;
    private RecyclerView mRvComment;
    private ImageView mIvPostComment, mUserImage, mPostMedia;
    private TextView mUserName, mPostTime, mPostText, mPostLike, mPostDislike, mPostNeutral, mPostComments, mPostShare;
    private EditText mEtCommentText;
    private LinearLayoutManager mLlmanager;
    private ArrayList<CommentPojo> mCommentList;
    private PostCommentAdapter mCommentAdapter;
    public static final String mActionTypeLike = "like";
    public static final String mActionTypeDislike = "dislike";
    public static final String mActionTypeNeutral = "neutral";
    String postId = "";
    int action=0;
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
            title.setText("Comments");
        } catch (Exception e) {
            e.printStackTrace();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
    }

    @Override
    protected void setUpLayout() {
        try {
            mRvComment = (RecyclerView) findViewById(R.id.rv_comment_activity);
            //mRvComment.setLayoutManager(mLlmanager);

            final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRvComment.setLayoutManager(layoutManager);


            mIvPostComment = (ImageView) findViewById(R.id.iv_post_comment);
            mEtCommentText = (EditText) findViewById(R.id.et_comment_text);
            mUserName = (TextView) findViewById(R.id.tv_user_name_comments);
            mUserImage = (ImageView) findViewById(R.id.iv_user_img_comment_activity);
            mPostText = (TextView) findViewById(R.id.tv_post_text_comment_activity);
            mPostLike = (TextView) findViewById(R.id.tv_like_comment_activity);
            mPostDislike = (TextView) findViewById(R.id.tv_dislike_comment_activity);
            mPostNeutral = (TextView) findViewById(R.id.tv_neutral_comment_activity);
            mPostComments = (TextView) findViewById(R.id.tv_comment_comment_activity);
            mPostTime = (TextView) findViewById(R.id.tv_post_time_comment_activity);
            mPostMedia = (ImageView) findViewById(R.id.iv_media_post_comment_activity);

            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();
            selected_post_data = (PostPojo) bundle.getSerializable("POST");
            postId = selected_post_data.getPostId();

            viewAllComments(selected_post_data.getPostId());
            setDataInViewObjects();
        } catch (Exception e) {

        }

    }

    @Override
    protected void setDataInViewObjects() {


        String Name = ((selected_post_data.getFirstName()) + " " + (selected_post_data.getLastName()).charAt(0) + ".");
        mUserName.setText(Name);
        if (!selected_post_data.getProfile_pic().isEmpty()) {
            Glide.with(this).load(WebServiceClient.HTTP_STAGING + selected_post_data.getProfile_pic())
                    .thumbnail(1f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mUserImage);
        } else {
            mUserImage.setImageResource(R.drawable.default_user);
        }

        if (!selected_post_data.getMediaUrl().isEmpty()) {
            Glide.with(this).load(WebServiceClient.HTTP_STAGING + selected_post_data.getMediaUrl())
                    .thumbnail(1f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mPostMedia);
            mPostMedia.setVisibility(View.VISIBLE);
        }


        mPostComments.setText(selected_post_data.getComment());
        mPostTime.setText(selected_post_data.getDate_created());
        mPostText.setText(selected_post_data.getText());
        mPostLike.setText(selected_post_data.getLike());
        mPostDislike.setText(selected_post_data.getDislike());
        mPostNeutral.setText(selected_post_data.getNeutral());

        if (selected_post_data.getAction() != null) {
            if (selected_post_data.getAction().equalsIgnoreCase(Constants.LIKE)) {
                mPostLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_blue_icon_circle, 0, 0, 0);
                mPostLike.setEnabled(true);
                mPostDislike.setEnabled(false);
                mPostNeutral.setEnabled(false);
            } else if (selected_post_data.getAction().equalsIgnoreCase(Constants.DISLIKE)) {
                mPostDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_blue_icon_circle, 0, 0, 0);
                mPostLike.setEnabled(false);
                mPostDislike.setEnabled(true);
                mPostNeutral.setEnabled(false);


            } else if (selected_post_data.getAction().equalsIgnoreCase(Constants.NEUTRAL)) {
                mPostNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_icon_blue_circle, 0, 0, 0);
                mPostLike.setEnabled(false);
                mPostDislike.setEnabled(false);
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

    }

    @Override
    protected void onClick(View v) {

        postId = selected_post_data.getPostId();

        switch (v.getId()) {

            case R.id.iv_post_comment:
                try {

                    String comment_text = mEtCommentText.getText().toString().trim();

                    if (comment_text.isEmpty()) {
                        Utility.showAlertDialog(getResources().getString(R.string.comment_text_validation_message), this);
                        return;

                    } else {
                        postComment(selected_post_data.getPostId(), comment_text);


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.tv_like_comment_activity:
                action=1;
                likeUnlikePost(mActionTypeLike, postId);

                break;

            case R.id.tv_dislike_comment_activity:
            action=2;
                likeUnlikePost(mActionTypeDislike, postId);
                break;

            case R.id.tv_neutral_comment_activity:
            action=3;
                likeUnlikePost(mActionTypeNeutral, postId);

                break;


            default:
                break;
        }

    }


    //    /*
//
// create payload to like unlike a post
//
//
// */
    public String createPayload(String type, String postId) {

        JSONObject payload = null;
//        token, userId, type(like/dislike/neutral), post_id
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

            WebNotificationManager.registerResponseListener(responseHandl);
            WebServiceClient.like_unlike_post(this, payload, responseHandl);
        }
    }


    /*
 * like unlike response
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

                            String num_like =mPostLike.getText().toString();
                            int a = Integer.parseInt(num_like);

                            if (result.getLike() == 1) {
                                mPostLike.setText(a + 1 + "");
                                mPostLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_blue_icon_circle, 0, 0, 0);

                                mPostLike.setEnabled(true);
                                mPostDislike.setEnabled(false);
                                mPostNeutral.setEnabled(false);

                            } else {

                                mPostLike.setText(a - 1 + "");

                                mPostLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon_circle, 0, 0, 0);
                                mPostLike.setEnabled(true);
                                mPostDislike.setEnabled(true);
                                mPostNeutral.setEnabled(true);

                            }
                        } else if (action == 2) {
//    action dislike

                            String num_dislike = mPostDislike.getText().toString();
                            int b = Integer.parseInt(num_dislike.trim());


                            if (result.getDislike() == 1) {
                                mPostDislike.setText(b + 1 + " ");
                                mPostDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_blue_icon_circle, 0, 0, 0);
                                mPostLike.setEnabled(false);
                                mPostDislike.setEnabled(true);
                                mPostNeutral.setEnabled(false);


                            } else {

                                mPostDislike.setText(b - 1 + " ");

                                mPostDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_grey_icon_circle, 0, 0, 0);

                                mPostLike.setEnabled(true);
                                mPostDislike.setEnabled(true);
                                mPostNeutral.setEnabled(true);


                            }


                        } else if (action == 3) {
//    action neutral

                            String num_neutral = mPostNeutral.getText().toString();
                            int c = Integer.parseInt(num_neutral.trim());


                            if (result.getNeutral() == 1) {
                                mPostNeutral.setText(c + 1 +" ");

                                mPostNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_icon_blue_circle, 0, 0, 0);
                                mPostLike.setEnabled(false);
                                mPostDislike.setEnabled(false);
                                mPostNeutral.setEnabled(true);


                            } else {

                                mPostNeutral.setText(c - 1 + "");

                                mPostNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_grey_icon_circle, 0, 0, 0);

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


    /*
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

    /*
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

    /*
    *
    * response of  a comment post
    * */
    ResponseHandlerListener responseHandlerComment = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            WebNotificationManager.unRegisterResponseListener(responseHandlerComment);

            if (error == null) {

                if (result.getSuccess().equalsIgnoreCase("true")) {

                    Utility.showToastMessageLong(CommentActivity.this, "comment posted");
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


    /*
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


    /*
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

    /*
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
                        mCommentList = new ArrayList<CommentPojo>();
                        mCommentList.addAll(result.getCommentList());

                        mCommentAdapter = null;
                        mCommentAdapter = new PostCommentAdapter(CommentActivity.this, mCommentList);
                        //   mCommentAdapter.notifyDataSetChanged();

                        LinearLayout.LayoutParams lp =
                                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mCommentList.size() * 300);
                        mRvComment.setLayoutParams(lp);
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


   /*
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

    /*
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

    /*
    * response LIke unlike a comment
    *
    *
    * */
    ResponseHandlerListener responseCommentAction = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            WebNotificationManager.unRegisterResponseListener(responseHandlerComment);


            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    };


    /*
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


    /*
    *
    * call to remove a comment
    * */


    public void removeComment(String commentId) {

        String payload = createPayload_Remove_Comment(commentId);
        if (payload != null) {

            WebNotificationManager.registerResponseListener(responseCommentRemove);
            WebServiceClient.remove_a_comment(this, payload, responseCommentRemove);
        }

    }


    /*
   * response remove a comment
   *
   *
   * */
    ResponseHandlerListener responseCommentRemove = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            WebNotificationManager.unRegisterResponseListener(responseCommentRemove);


            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    };


}
