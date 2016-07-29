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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.CommentPojo;
import com.igniva.indiecore.model.RepliesPojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.ui.adapters.CommentReplyAdapter;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-05 on 29/7/16.
 */
public class CommentsReplyActivity extends BaseActivity implements View.OnClickListener {


    private Toolbar mToolbar;
    private EditText mEtReplyText;
    private TextView mTvCommentLike,mTvCommentDislike,mTvCommentNeutral,mTvCommentReply;
    private ImageView mIvPostReply;
    private RecyclerView mRvReplies;
    private CommentPojo selected_comment_data = null;
    private String commentId = "";
    private TextView mUserName, mUserComment;
    private ImageView mUserImg;
    public static final String mActionTypeLike = "like";
    public static final String mActionTypeDislike = "dislike";
    public static final String mActionTypeNeutral = "neutral";
    private int action=0;
    private String page="1";
    private String limit="10";
    private ArrayList<RepliesPojo> mRelpiesLIst;
    private CommentReplyAdapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_reply);
        initToolbar();
        setUpLayout();
        setDataInViewObjects();
    }


    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar_comment_reply_activity);
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
            title.setText("Reply");
        } catch (Exception e) {
            e.printStackTrace();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
    }


    @Override
    protected void setUpLayout() {
        try {

            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();
            selected_comment_data = (CommentPojo) bundle.getSerializable("COMMENT");
            commentId = selected_comment_data.getCommentId();

            mUserName=(TextView) findViewById(R.id.tv_user_name_comment_reply_activity);
            mUserComment=(TextView) findViewById(R.id.tv_comment_text_comment_reply_activity);
            mUserImg=(ImageView) findViewById(R.id.iv_user_img_comment_reply_activity);
              mIvPostReply =(ImageView) findViewById(R.id.iv_post_reply);
            mIvPostReply.setOnClickListener(this);
            mEtReplyText=(EditText) findViewById(R.id.et_reply_txt);
            mRvReplies=(RecyclerView) findViewById(R.id.rv_replies);
            
            
            mTvCommentLike=(TextView) findViewById(R.id.tv_like_comment_reply);
            mTvCommentLike.setOnClickListener(this);
            mTvCommentDislike=(TextView) findViewById(R.id.tv_dislike_comment_reply);
            mTvCommentDislike.setOnClickListener(this);
            mTvCommentNeutral=(TextView) findViewById(R.id.tv_neutral_comment_reply);
            mTvCommentNeutral.setOnClickListener(this);
            mTvCommentReply=(TextView) findViewById(R.id.tv_reply_comment_reply);
            mTvCommentReply.setOnClickListener(this);

            final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRvReplies.setLayoutManager(layoutManager);
             viewAllReplies(commentId,page,limit);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void setDataInViewObjects() {
try {

    String Name = ((selected_comment_data.getFirstName()) + " " + (selected_comment_data.getLastName()).charAt(0) + ".");
    mUserName.setText(Name);

    mUserComment.setText(selected_comment_data.getText());

    if (!selected_comment_data.getProfile_pic().isEmpty()) {
        Glide.with(this).load(WebServiceClient.HTTP_STAGING + selected_comment_data.getProfile_pic())
                .thumbnail(1f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mUserImg);
    } else {
        mUserImg.setImageResource(R.drawable.default_user);
    }

    if (selected_comment_data.getAction() != null) {
        if (selected_comment_data.getAction().equalsIgnoreCase(Constants.LIKE)) {
            mTvCommentLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_icon, 0, 0, 0);
            mTvCommentLike.setEnabled(true);
            mTvCommentDislike.setEnabled(false);
            mTvCommentNeutral.setEnabled(false);
        } else if (selected_comment_data.getAction().equalsIgnoreCase(Constants.DISLIKE)) {
            mTvCommentDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_icon, 0, 0, 0);
            mTvCommentLike.setEnabled(false);
            mTvCommentDislike.setEnabled(true);
            mTvCommentNeutral.setEnabled(false);


        } else if (selected_comment_data.getAction().equalsIgnoreCase(Constants.NEUTRAL)) {
            mTvCommentNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_icon, 0, 0, 0);
            mTvCommentLike.setEnabled(false);
            mTvCommentDislike.setEnabled(false);
            mTvCommentNeutral.setEnabled(true);

        }
    } else {
        mTvCommentLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon, 0, 0, 0);

        mTvCommentDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_icon_grey, 0, 0, 0);

        mTvCommentNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_icon_grey, 0, 0, 0);

        mTvCommentLike.setEnabled(true);
        mTvCommentDislike.setEnabled(true);
        mTvCommentNeutral.setEnabled(true);
    }


}catch (Exception e){
    e.printStackTrace();
}

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.iv_post_reply:

                String reply_txt=mEtReplyText.getText().toString();

                    if(reply_txt.isEmpty()) {
                        Utility.showToastMessageLong(CommentsReplyActivity.this, "Please write a reply");
                    }else {
                        replyToComment(commentId,reply_txt);

                    }
                        break;


            case R.id.tv_like_comment_reply:
                action = 1;
                commentAction(mActionTypeLike, commentId);

                break;

            case R.id.tv_dislike_comment_reply:
                action = 2;
                commentAction(mActionTypeDislike, commentId);

                break;


            case R.id.tv_neutral_comment_reply:
                action = 3;
                commentAction(mActionTypeNeutral, commentId);

                break;
            default:
                break;
        }

    }

    /*
    * payload to reply to a comment
    * */
    public String createPayload(String commentId,String text) {
        JSONObject payload = null;
//        token, userId, commentId, text

            try {
                    payload = new JSONObject();
                    payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
                    payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
                    payload.put(Constants.COMMENTID, commentId);
                    payload.put(Constants.TEXT, text);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return payload.toString();

    }


    /*
    * reply to a comment post request
    *
    * */
    public void replyToComment(String commentId,String text) {

        String payload = createPayload(commentId,text);
        if (payload != null) {

            try {

                WebNotificationManager.registerResponseListener(responseReplyToComment);
                WebServiceClient.reply_to_comment(this, payload, responseReplyToComment);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    ResponseHandlerListener responseReplyToComment = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            try {
                WebNotificationManager.unRegisterResponseListener(responseReplyToComment);

                if (error == null) {
                    if(result.getSuccess().equalsIgnoreCase("true")){
                        viewAllReplies(commentId,page,limit);

                    }else {


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
* payload to view all replies
* */
    public String createPayload(String commentId, String page, String limit) {
        JSONObject payload = null;
//       token, userId, commentId, page, limit
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(this, PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.COMMENTID, commentId);
            payload.put(Constants.PAGE, page);
            payload.put(Constants.LIMIT, limit);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return payload.toString();
    }

    public void viewAllReplies(String commentId, String page, String limit) {
        try {

            String payload = createPayload(commentId, page, limit);
            if (payload != null) {

                WebNotificationManager.registerResponseListener(responseViewAllReply);
                WebServiceClient.view_all_replies(this, payload, responseViewAllReply);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    ResponseHandlerListener responseViewAllReply = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(responseViewAllReply);

            try {
                mRelpiesLIst= new ArrayList<RepliesPojo>();
                mRelpiesLIst.addAll(result.getRepliesList());

                mAdapter=null;
                mAdapter=new CommentReplyAdapter(CommentsReplyActivity.this,mRelpiesLIst);
                mRvReplies.setAdapter(mAdapter);


            } catch (Exception e) {

            }

//            finish the dialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
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

            WebNotificationManager.registerResponseListener(responseCommentActionReply);
            WebServiceClient.like_unlike_a_comment(this, payload, responseCommentActionReply);
        }

    }

    /*
    * response LIke unlike a comment
    *
    *
    * */
    ResponseHandlerListener responseCommentActionReply = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            WebNotificationManager.unRegisterResponseListener(responseCommentActionReply);


            try {
                if (error == null) {

                    if (result.getSuccess().equalsIgnoreCase("true")) {

                        if (action == 1) {
                            //                        like
                            String likes_count = mTvCommentLike.getText().toString();
                            int a = 0;
                            a=Integer.parseInt(likes_count.trim());
                            if (result.getLike() == 1) {
                                mTvCommentLike.setText(a + 1 + "");
                                mTvCommentLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_icon, 0, 0, 0);
                                mTvCommentLike.setEnabled(true);
                                mTvCommentDislike.setEnabled(false);
                                mTvCommentNeutral.setEnabled(false);

                            } else {
                                if (a > 0) {
                                    mTvCommentLike.setText(a - 1 + "");
                                }
                                mTvCommentLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon, 0, 0, 0);
                                mTvCommentLike.setEnabled(true);
                                mTvCommentDislike.setEnabled(true);
                                mTvCommentNeutral.setEnabled(true);


                            }

                        } else if (action == 2) {
                            //                        dislike


                            String dislikes_count = mTvCommentDislike.getText().toString();
                            int b = 0;
                            b=Integer.parseInt(dislikes_count.trim());
                            if (result.getDislike() == 1) {
                                mTvCommentDislike.setText(b + 1 + "");
                                mTvCommentDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_icon_without_circle, 0, 0, 0);
                                mTvCommentLike.setEnabled(false);
                                mTvCommentDislike.setEnabled(true);
                                mTvCommentNeutral.setEnabled(false);

                            } else {
                                if (b > 0) {
                                    mTvCommentDislike.setText(b - 1 + "");
                                }
                                mTvCommentDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_icon_grey, 0, 0, 0);
                                mTvCommentLike.setEnabled(true);
                                mTvCommentDislike.setEnabled(true);
                                mTvCommentNeutral.setEnabled(true);

                            }

                        } else {

                            //                        neutral

                            String neutral_count = mTvCommentNeutral.getText().toString();
                            int c=0;
                            c=Integer.parseInt(neutral_count.trim());
                            if (result.getNeutral() == 1) {
                                mTvCommentNeutral.setText(c + 1 + "");
                                mTvCommentNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_icon, 0, 0, 0);
                                mTvCommentLike.setEnabled(false);
                                mTvCommentDislike.setEnabled(false);
                                mTvCommentNeutral.setEnabled(true);

                            } else {
                                if (c > 0) {
                                    mTvCommentNeutral.setText(c - 1 + "");

                                }
                                mTvCommentNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_icon_grey, 0, 0, 0);
                                mTvCommentLike.setEnabled(true);
                                mTvCommentDislike.setEnabled(true);
                                mTvCommentNeutral.setEnabled(true);



                            }
                        }


                    }

                    //                Utility.showToastMessageLong(CommentActivity.this,"Comment like/unlike");


                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }


            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    };




}
