package com.igniva.indiecore.ui.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.OnCommentListItemClickListnerTest2;
import com.igniva.indiecore.controller.OnListItemClickListner;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.PostPojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class WallPostAdapter extends RecyclerView.Adapter<WallPostAdapter.RecyclerViewHolders> {

    private ArrayList<PostPojo> wallItemsList;
    private Context mContext;
    ImageView mDelete;
    WallPostAdapter mAdapter;
    private boolean deletePostVisible = false;
    private String ACTION = "";
    private int POSTION=-1;
    String LOG_TAG = "PhonebookAdapter";
//    OnListItemClickListner mOnListItemClickListner;
PostPojo postPojo;
    OnCommentListItemClickListnerTest2 onCommentListItemClickListnerTest2l;


    public WallPostAdapter(Context context, ArrayList<PostPojo> wallItemsList, OnCommentListItemClickListnerTest2 onListItemClickListner) {

        this.wallItemsList = wallItemsList;
        this.mContext = context;
        this.onCommentListItemClickListnerTest2l = onListItemClickListner;

    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {


        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wall_post_items,null);
        return new RecyclerViewHolders(layoutView);
//        return rcv;

//        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wall_post_items,null);
//        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
//        return rcv;
    }


    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        try {

             postPojo=wallItemsList.get(position);
            if (!postPojo.getFirstName().isEmpty()) {
                String Name = (postPojo.getFirstName() + " " + (postPojo.getLastName()).charAt(0) + ".");
                holder.mUserName.setText(Name);
            }
            if (postPojo.getProfile_pic() != null) {
                Glide.with(mContext).load(WebServiceClient.HTTP_STAGING + postPojo.getProfile_pic())
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.mUserIcon);
            }else {
                holder.mUserIcon.setImageResource(R.drawable.default_user);
            }
            String time = postPojo.getDate_created();
            holder.mPostTime.setText(time.substring(0,10));

            if (postPojo.getMediaUrl() != null) {
                holder.mMediaPost.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(WebServiceClient.HTTP_STAGING + postPojo.getMediaUrl())
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.mMediaPost);
            } else {
                holder.mMediaPost.setVisibility(View.GONE);
            }
//
//
            holder.mTextPost.setText(postPojo.getText());
            holder.like.setText(postPojo.getLike());
            holder.dislike.setText(postPojo.getDislike());
            holder.neutral.setText(postPojo.getNeutral());
            holder.comment.setText(postPojo.getComment());

            // Post has been liked/disliked or set neutral by user
            if (postPojo.getAction() != null) {

                if (postPojo.getAction().equalsIgnoreCase(Constants.LIKE)) {
                    holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_blue_icon_circle, 0, 0, 0);
                    holder.dislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_grey_icon_circle,0,0,0);
                    holder.neutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_grey_icon_circle,0,0,0);
                    holder.like.setEnabled(true);
                    holder.dislike.setEnabled(false);
                    holder.neutral.setEnabled(false);

                } else if (postPojo.getAction().equalsIgnoreCase(Constants.DISLIKE)) {
                    holder.dislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_blue_icon_circle, 0, 0, 0);
                    holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon_circle,0,0,0);
                    holder.neutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_grey_icon_circle,0,0,0);
                    holder.like.setEnabled(false);
                    holder.dislike.setEnabled(true);
                    holder.neutral.setEnabled(false);
//
                } else if (postPojo.getAction().equalsIgnoreCase(Constants.NEUTRAL)) {

                    holder.neutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_icon_blue_circle, 0, 0, 0);
                    holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon_circle, 0,0,0);
                    holder.dislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_grey_icon_circle,0,0,0);
                    holder.like.setEnabled(false);
                    holder.dislike.setEnabled(false);
                    holder.neutral.setEnabled(true);

                } else {

                    holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon_circle,0,0,0);
                    holder.dislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_grey_icon_circle,0,0,0);
                    holder.neutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_grey_icon_circle,0, 0,0);
                    holder.like.setEnabled(true);
                    holder.dislike.setEnabled(true);
                    holder.neutral.setEnabled(true);

                }
            }
            // Post has not been  like/dislike or neutral
            else {

                holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon_circle,0,0,0);
                holder.dislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_grey_icon_circle,0,0,0);
                holder.neutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_grey_icon_circle,0, 0,0);
                holder.like.setEnabled(true);
                holder.dislike.setEnabled(true);
                holder.neutral.setEnabled(true);


            }


            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Utility.showToastMessageLong(((Activity) mContext)," position is "+position);
                    onCommentListItemClickListnerTest2l.onCommentListItemClicked(holder, position, postPojo.getPostId());
//                    mOnListItemClickListner.onListItemClicked(holder, position, postPojo.getPostId());
                }
            });
            holder.dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Utility.showToastMessageLong(((Activity) mContext)," position is "+position);
                    onCommentListItemClickListnerTest2l.onCommentListItemClicked(holder, position, postPojo.getPostId());
                }
            });
            holder.neutral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Utility.showToastMessageLong(((Activity) mContext)," position is "+position);
                    onCommentListItemClickListnerTest2l.onCommentListItemClicked(holder, position, postPojo.getPostId());
                }
            });

            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCommentListItemClickListnerTest2l.onCommentListItemClicked(holder, position, postPojo.getPostId());

                }
            });


            holder.mDeletePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCommentListItemClickListnerTest2l.onCommentListItemClicked(holder, position, postPojo.getPostId());
//                    mDelete=holder.mDeletePost;
//                    try {
//
//                        if (ACTION.equalsIgnoreCase("DELETE")) {
//
//                            removePost(postPojo.getPostId());
//
//                        } else if (ACTION.equalsIgnoreCase("REPORT")) {
//
//                            flagPost(postPojo.getPostId());
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                }
            });


            holder.dropDownOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onCommentListItemClickListnerTest2l.onCommentListItemClicked(holder, position, postPojo.getPostId());
//
//                    try {
//
//                        if (deletePostVisible == false) {
//
//                            if (postPojo.getRelation().equalsIgnoreCase("self")) {
//                                holder.mDeletePost.setVisibility(View.VISIBLE);
//                                holder.mDeletePost.setImageResource(R.drawable.delete_icon);
//                                ACTION = "DELETE";
//                            } else {
//                                holder.mDeletePost.setVisibility(View.VISIBLE);
//                                holder.mDeletePost.setImageResource(R.drawable.report_abuse);
//                                ACTION = "REPORT";
//                            }
//                            deletePostVisible = true;
//                        } else if(deletePostVisible==true) {
//                            holder.mDeletePost.setVisibility(View.GONE);
//                            deletePostVisible = false;
//
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }


                }
            });




//
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public int getItemCount() {
        return this.wallItemsList.size();
    }


    public class RecyclerViewHolders extends RecyclerView.ViewHolder {

        public TextView mUserName;
        public ImageView mUserIcon;
        public TextView mPostTime;
        public ImageView mMediaPost;
        public TextView mTextPost;
        public TextView mAvailable;
        public TextView like;
        public TextView dislike;
        public TextView neutral;
        public TextView comment;
        public ImageView dropDownOptions;
        public ImageView mDeletePost;
//        public TextView  share;
//        public LinearLayout mCommentSection;
//        public EditText mEtComment;
//        public  ImageView mIvPostComment;
//        public RecyclerView mRvComments;


        public RecyclerViewHolders(final View itemView) {
            super(itemView);

            mUserName = (TextView) itemView.findViewById(R.id.tv_user_name_);
            mUserIcon = (ImageView) itemView.findViewById(R.id.iv_user_img_);

            mPostTime = (TextView) itemView.findViewById(R.id.tv_post_time_);

            mMediaPost = (ImageView) itemView.findViewById(R.id.iv_media_post);
            mTextPost = (TextView) itemView.findViewById(R.id.tv_post_text);
            mAvailable = (TextView) itemView.findViewById(R.id.tv_available_);
            like = (TextView) itemView.findViewById(R.id.tv_like);
            dislike = (TextView) itemView.findViewById(R.id.tv_dislike);
            neutral = (TextView) itemView.findViewById(R.id.tv_neutral);
            comment = (TextView) itemView.findViewById(R.id.tv_comment);


            dropDownOptions = (ImageView) itemView.findViewById(R.id.iv_drop_down_options);
            mDeletePost = (ImageView) itemView.findViewById(R.id.iv_delete_post);
//            share=(TextView) itemView.findViewById(R.id.tv_share);
//            mCommentSection=(LinearLayout) itemView.findViewById(R.id.ll_comment_part);

//            mEtComment=(EditText) itemView.findViewById(R.id.et_comment_text);
//            mIvPostComment=(ImageView) itemView.findViewById(R.id.iv_post_comment);
//            mRvComments=(RecyclerView) itemView.findViewById(R.id.rv_comments);

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
            payload.put(Constants.TOKEN, PreferenceHandler.readString(mContext, PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(mContext, PreferenceHandler.PREF_KEY_USER_ID, ""));
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
                WebServiceClient.remove_a_post((Activity)mContext, payload, responseRemovePost);
            }
        }catch (Exception e){
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

                        mDelete.setVisibility(View.GONE);
//                        wallItemsList.remove(POSTION);
//                        mAdapter.notifyDataSetChanged();
//                        mWallPostAdapter = new WallPostAdapter(getActivity(), mWallPostList, onListItemClickListner);
//                        mWallPostAdapter.notifyDataSetChanged();
//                      mRvWallPosts.setAdapter(mWallPostAdapter);



                        Utility.showToastMessageLong((Activity) mContext, "post removed");

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


    /*
    *
    * flag post call
    *
    *
    * */
    public void flagPost(String postId) {

        String payload = genratePayload(postId);
        if (payload != null) {

            WebNotificationManager.registerResponseListener(responseFlagPost);
            WebServiceClient.flag_a_post((Activity)mContext, payload, responseFlagPost);

        }

    }


    /*
    * response flag post
    *
    *
    * */
    ResponseHandlerListener responseFlagPost = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(responseFlagPost);
            try {

                if (error == null) {
                    if (result.getSuccess().equalsIgnoreCase("true")) {

                        mDelete.setVisibility(View.GONE);
                        Utility.showToastMessageLong((Activity) mContext, "post reported");


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
