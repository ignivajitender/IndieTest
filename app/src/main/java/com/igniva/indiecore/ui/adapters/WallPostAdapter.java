package com.igniva.indiecore.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.PostPojo;
import com.igniva.indiecore.ui.activities.UserProfileActivity;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.PreferenceHandler;

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
    private String ACTIVITY_NAME = "";
    String LOG_TAG = "WallPostAdapter";
    OnLikeClickListner mOnLikeClickListner;
    OnDisLikeClickListner mOnDisLikeClickListner;
    OnNeutralClickListner mOnNeutralClickListner;
    OnCommentClickListner mOnCommentClickListner;
    OnMediaPostClickListner mOnMediaPostClickListner;
    OnDeletePostClickListner mOnDeletePostClickListner;


    public WallPostAdapter(Context context, ArrayList<PostPojo> wallItemsList, String contextName,OnLikeClickListner onLikeClickListner,OnDisLikeClickListner OnDisLikeAdapter,OnNeutralClickListner onNeutralClickListner,OnCommentClickListner onCommentClickListner,OnMediaPostClickListner onMediaPostClickListner,OnDeletePostClickListner onDeletePostClickListner) {

        this.wallItemsList = wallItemsList;
        this.mContext = context;
        this.ACTIVITY_NAME = contextName;
        this.mOnLikeClickListner=onLikeClickListner;
        this.mOnDisLikeClickListner =OnDisLikeAdapter;
        this.mOnNeutralClickListner=onNeutralClickListner;
        this.mOnCommentClickListner=onCommentClickListner;
        this.mOnMediaPostClickListner=onMediaPostClickListner;
        this.mOnDeletePostClickListner=onDeletePostClickListner;
    }


    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {


        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wall_post_items, parent, false);
        return new RecyclerViewHolders(layoutView);
    }


    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        try {
//            wallItemsList.get(position) = wallItemsList.get(position);
            if (!wallItemsList.get(position).getFirstName().isEmpty()) {
                String Name = (wallItemsList.get(position).getFirstName() + " " + (wallItemsList.get(position).getLastName()).charAt(0) + ".");
                holder.mUserName.setText(Name);
            }
            if (wallItemsList.get(position).getProfile_pic() != null) {
                Glide.with(mContext).load(WebServiceClient.HTTP_STAGING + wallItemsList.get(position).getProfile_pic())
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.mUserIcon);
            } else {
                holder.mUserIcon.setImageResource(R.drawable.default_user);
            }
            String time = wallItemsList.get(position).getDate_created();
            holder.mPostTime.setText(time.substring(0, 10));

            try {
                if (wallItemsList.get(position).getMediaUrl() != null) {
                    holder.mMediaPost.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(WebServiceClient.HTTP_STAGING + wallItemsList.get(position).getMediaUrl())
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

            holder.mTextPost.setText(wallItemsList.get(position).getText());
            holder.like.setText(wallItemsList.get(position).getLike() + "");
            holder.dislike.setText(wallItemsList.get(position).getDislike() + "");
            holder.neutral.setText(wallItemsList.get(position).getNeutral() + "");
            holder.comment.setText(wallItemsList.get(position).getComment() + "");

            // Post has been liked/disliked or set neutral by user
            if (wallItemsList.get(position).getAction() != null) {

                if (wallItemsList.get(position).getAction().equalsIgnoreCase(Constants.LIKE)) {
                    holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_blue_icon_circle, 0, 0, 0);
                    holder.dislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_grey_icon_circle, 0, 0, 0);
                    holder.neutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_grey_icon_circle, 0, 0, 0);
                    holder.like.setEnabled(true);
                    holder.dislike.setEnabled(false);
                    holder.neutral.setEnabled(false);

                } else if (wallItemsList.get(position).getAction().equalsIgnoreCase(Constants.DISLIKE)) {
                    holder.dislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_blue_icon_circle, 0, 0, 0);
                    holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon_circle, 0, 0, 0);
                    holder.neutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_grey_icon_circle, 0, 0, 0);
                    holder.like.setEnabled(false);
                    holder.dislike.setEnabled(true);
                    holder.neutral.setEnabled(false);
                } else if (wallItemsList.get(position).getAction().equalsIgnoreCase(Constants.NEUTRAL)) {

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

                    mOnLikeClickListner.onLikeClicked(holder.like,position, wallItemsList.get(position).getPostId(), Constants.LIKE);
                }
            });


            holder.dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mOnDisLikeClickListner.onDisLikeClicked(holder.dislike,position, wallItemsList.get(position).getPostId(), Constants.DISLIKE);
                }
            });


            holder.neutral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mOnNeutralClickListner.onNeutralClicked(holder.dislike,position, wallItemsList.get(position).getPostId(), Constants.DISLIKE);
                }
            });

            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnCommentClickListner.onCommentClicked(holder.comment,position, wallItemsList.get(position).getPostId(), Constants.COMMENT);
                }
            });


            holder.mMediaPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnMediaPostClickListner.onMediaPostClicked(holder.mMediaPost,position, wallItemsList.get(position).getPostId(), Constants.MEDIA);

                }
            });

            holder.mDeletePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnDeletePostClickListner.ondeletePostClicked(holder.mDeletePost,position, wallItemsList.get(position).getPostId(),ACTION);

                }
            });


            if(ACTIVITY_NAME.equals(Constants.USERPROFILEACTIVITY)){
                holder.mUserIcon.setEnabled(false);
            }
            if(ACTIVITY_NAME.equalsIgnoreCase(Constants.MYPROFILEACTIVITY)){
                holder.mRlMutualBadgeTxtBg.setVisibility(View.GONE);
                holder.mUserIcon.setEnabled(false);

            }

            holder.mUserIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                            Bundle bundle = new Bundle();
                            Intent intent = new Intent(mContext, UserProfileActivity.class);
                            bundle.putString(Constants.USERID, wallItemsList.get(position).getUserId());
                            bundle.putString(Constants.BUSINESS_ID, wallItemsList.get(position).getRoomId());
                            bundle.putInt(Constants.INDEX, 11);
                            intent.putExtras(bundle);
                            mContext.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            try {
                if(wallItemsList.get(position).getBadges().equalsIgnoreCase("OFF")){
                    holder.mRlMutualBadgeTxtBg.setBackgroundResource(R.drawable.badge_off);
                    holder.mMutualBadgeCount.setText("");
                }else {
                    String in=wallItemsList.get(position).getBadges();
                    holder.mRlMutualBadgeTxtBg.setBackgroundResource(R.drawable.blank_badge);
                    holder.mMutualBadgeCount.setText(Integer.parseInt(in)+"");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }


            holder.dropDownOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                                if (wallItemsList.get(position).getUserId().equalsIgnoreCase(userID)) {
                                    holder.mDeletePost.setImageResource(R.drawable.delete_button);
                                    ACTION = "DELETE";
                                } else {
                                    holder.mDeletePost.setImageResource(R.drawable.report_abuse);
                                    ACTION = "REPORT";

                            }
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
        public RelativeLayout mRlMutualBadgeTxtBg;
        public TextView mMutualBadgeCount;

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

            mRlMutualBadgeTxtBg=(RelativeLayout) itemView.findViewById(R.id.rl_mutual_badge_bg);
            mMutualBadgeCount=(TextView) itemView.findViewById(R.id.tv_mutual_badge_count);


            dropDownOptions = (ImageView) itemView.findViewById(R.id.iv_drop_down_options);
            mDeletePost = (ImageView) itemView.findViewById(R.id.iv_delete_post);

        }

    }

}
