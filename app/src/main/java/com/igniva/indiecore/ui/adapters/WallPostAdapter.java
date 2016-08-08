package com.igniva.indiecore.ui.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.OnCommentListItemClickListnerTest2;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.PostPojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.ui.activities.UserProfileActivity;
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
    public ImageView mDeletePostOld;
    private boolean deletePostVisible = false;
    private String ACTION = "";
    private int POSTION = -1;
    private String ACTIVITYNAME = "";
    String LOG_TAG = "WallPostAdapter";
    //    OnListItemClickListner mOnListItemClickListner;
    PostPojo postPojo;
    OnCommentListItemClickListnerTest2 onCommentListItemClickListnerTest2l;


    public WallPostAdapter(Context context, ArrayList<PostPojo> wallItemsList, OnCommentListItemClickListnerTest2 onListItemClickListner, String contextName) {

        this.wallItemsList = wallItemsList;
        this.mContext = context;
        this.onCommentListItemClickListnerTest2l = onListItemClickListner;
        this.ACTIVITYNAME = contextName;

    }


    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {


        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wall_post_items, parent, false);
        return new RecyclerViewHolders(layoutView);
    }


    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        try {
            postPojo = wallItemsList.get(position);
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
            } else {
                holder.mUserIcon.setImageResource(R.drawable.default_user);
            }
            String time = postPojo.getDate_created();
            holder.mPostTime.setText(time.substring(0, 10));

            try {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
//
//
            holder.mTextPost.setText(postPojo.getText());
            holder.like.setText(postPojo.getLike() + "");
            holder.dislike.setText(postPojo.getDislike() + "");
            holder.neutral.setText(postPojo.getNeutral() + "");
            holder.comment.setText(postPojo.getComment() + "");

            // Post has been liked/disliked or set neutral by user
            if (postPojo.getAction() != null) {

                if (postPojo.getAction().equalsIgnoreCase(Constants.LIKE)) {
                    holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_blue_icon_circle, 0, 0, 0);
                    holder.dislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_grey_icon_circle, 0, 0, 0);
                    holder.neutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_grey_icon_circle, 0, 0, 0);
                    holder.like.setEnabled(true);
                    holder.dislike.setEnabled(false);
                    holder.neutral.setEnabled(false);

                } else if (postPojo.getAction().equalsIgnoreCase(Constants.DISLIKE)) {
                    holder.dislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_blue_icon_circle, 0, 0, 0);
                    holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon_circle, 0, 0, 0);
                    holder.neutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_grey_icon_circle, 0, 0, 0);
                    holder.like.setEnabled(false);
                    holder.dislike.setEnabled(true);
                    holder.neutral.setEnabled(false);
//
                } else if (postPojo.getAction().equalsIgnoreCase(Constants.NEUTRAL)) {

                    holder.neutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_icon_blue_circle, 0, 0, 0);
                    holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon_circle, 0, 0, 0);
                    holder.dislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_grey_icon_circle, 0, 0, 0);
                    holder.like.setEnabled(false);
                    holder.dislike.setEnabled(false);
                    holder.neutral.setEnabled(true);

                } else {

                    holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon_circle, 0, 0, 0);
                    holder.dislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_grey_icon_circle, 0, 0, 0);
                    holder.neutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_grey_icon_circle, 0, 0, 0);
                    holder.like.setEnabled(true);
                    holder.dislike.setEnabled(true);
                    holder.neutral.setEnabled(true);

                }
            }
            // Post has not been  like/dislike or neutral
            else {

                holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon_circle, 0, 0, 0);
                holder.dislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_grey_icon_circle, 0, 0, 0);
                holder.neutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_grey_icon_circle, 0, 0, 0);
                holder.like.setEnabled(true);
                holder.dislike.setEnabled(true);
                holder.neutral.setEnabled(true);

            }

            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCommentListItemClickListnerTest2l.onCommentListItemClicked(holder, position, postPojo.getPostId(), Constants.LIKE);
                }
            });
            holder.dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCommentListItemClickListnerTest2l.onCommentListItemClicked(holder, position, postPojo.getPostId(), Constants.DISLIKE);
                }
            });
            holder.neutral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCommentListItemClickListnerTest2l.onCommentListItemClicked(holder, position, postPojo.getPostId(), Constants.NEUTRAL);
                }
            });

            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCommentListItemClickListnerTest2l.onCommentListItemClicked(holder, position, postPojo.getPostId(), Constants.COMMENT);

                }
            });

            holder.mMediaPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCommentListItemClickListnerTest2l.onCommentListItemClicked(holder, position, postPojo.getPostId(), Constants.MEDIA);

                }
            });


            holder.mDeletePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCommentListItemClickListnerTest2l.onCommentListItemClicked(holder, position, postPojo.getPostId(), ACTION);

                }
            });


            if(ACTIVITYNAME.equals(Constants.USERPROFILEACTIVITY)){
                holder.mUserIcon.setEnabled(false);
            }

            holder.mUserIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                            Bundle bundle = new Bundle();
                            Intent intent = new Intent(mContext, UserProfileActivity.class);
                            bundle.putString(Constants.USERID, postPojo.getUserId());
                            bundle.putString(Constants.BUSINESS_ID, postPojo.getRoomId());
                            bundle.putInt(Constants.INDEX, 11);
                            intent.putExtras(bundle);
                            mContext.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });


            holder.dropDownOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postPojo = wallItemsList.get(position);
                    try {
                        if (deletePostVisible == false) {
                            // Remove previously opened view
                            try {
                                mDeletePostOld.setVisibility(View.GONE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //
                            holder.mDeletePost.setVisibility(View.VISIBLE);

                                String userID=PreferenceHandler.readString(mContext,PreferenceHandler.PREF_KEY_USER_ID,"");
                                if (postPojo.getUserId().equalsIgnoreCase(userID)) {
                                    holder.mDeletePost.setImageResource(R.drawable.delete_icon);
                                    ACTION = "DELETE";
                                } else {
                                    holder.mDeletePost.setImageResource(R.drawable.report_abuse);
                                    ACTION = "REPORT";

                            }
//                            else {
//                                holder.mDeletePost.setImageResource(R.drawable.delete_icon);
//
//                            }
                            deletePostVisible = true;
                            mDeletePostOld = holder.mDeletePost;
                        } else if (deletePostVisible == true) {
                            holder.mDeletePost.setVisibility(View.GONE);
                            deletePostVisible = false;

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

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

        }

    }

}
