package com.igniva.indiecore.ui.adapters;

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
import com.igniva.indiecore.controller.OnCommentDeleteClickListner;
import com.igniva.indiecore.controller.OnCommentDislikeClickListner;
import com.igniva.indiecore.controller.OnCommentLikeClickListner;
import com.igniva.indiecore.controller.OnCommentNeutralClickListner;
import com.igniva.indiecore.controller.OnCommentReplyClickListner;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.CommentPojo;
import com.igniva.indiecore.utils.Constants;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class PostCommentAdapter extends RecyclerView.Adapter<PostCommentAdapter.RecyclerViewHolders> {

    private ArrayList<CommentPojo> mCommentList;
    private Context context;
    String LOG_TAG = "PostCommentAdapter";
    OnCommentLikeClickListner mOnCommentLikeClickLitner;
    OnCommentDislikeClickListner mOnCommentDislikeClickListner;
    OnCommentNeutralClickListner mOnCommentNeutarlClickListner;
    OnCommentReplyClickListner mOnCommentReplyClickListner;
    OnCommentDeleteClickListner mOnCommentDeleteClickLisnter;


    public PostCommentAdapter(Context context, ArrayList<CommentPojo> commentList) {
        this.mCommentList = commentList;
        this.context = context;
//        this.mOnCommentListItemClickLitner= mOnCommentClick;
    }

    public PostCommentAdapter(Context context, ArrayList<CommentPojo> commentList, OnCommentLikeClickListner OnCommentLikeClickLitner,OnCommentDislikeClickListner onCommentDislikeClickListner,
                              OnCommentNeutralClickListner onCommentNeutralClickListner,OnCommentReplyClickListner onCommentReplyClickListner,OnCommentDeleteClickListner onCommentDeleteClickListner) {
        this.mCommentList = commentList;
        this.context = context;
        this.mOnCommentLikeClickLitner = OnCommentLikeClickLitner;
        this.mOnCommentDislikeClickListner=onCommentDislikeClickListner;
        this.mOnCommentNeutarlClickListner= onCommentNeutralClickListner;
        this.mOnCommentReplyClickListner=onCommentReplyClickListner;
        this.mOnCommentDeleteClickLisnter=onCommentDeleteClickListner;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {


        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_list_items, null);
        return new RecyclerViewHolders(layoutView);



//        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wall_post_items, null);
//        return new RecyclerViewHolders(layoutView);

//        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_list_items, null);
//        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
//        return rcv;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        try {
            if (!mCommentList.get(position).getFirstName().isEmpty()) {
                String Name = (mCommentList.get(position).getFirstName() + " " + (mCommentList.get(position).getLastName()).charAt(0) + ".");
                holder.mUserName.setText(Name);
            }
            if (mCommentList.get(position).getProfile_pic() != null) {
                Glide.with(context).load(WebServiceClient.HTTP_STAGING+mCommentList.get(position).getProfile_pic())
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.mUserIcon);
            }

            holder.mComment.setText(mCommentList.get(position).getText()+"");
            holder.mCommentLike.setText(mCommentList.get(position).getLike()+"");
            holder.mCommentDislike.setText(mCommentList.get(position).getDislike()+"");
            holder.mCommentNeutral.setText(mCommentList.get(position).getNeutral()+"");
            holder.mReply.setText(mCommentList.get(position).getReplie()+"");

            if (mCommentList.get(position).getRelation().equalsIgnoreCase(Constants.SELF)) {

                holder.mDelete.setVisibility(View.VISIBLE);
            } else {
                holder.mDelete.setVisibility(View.GONE);

            }


            if (mCommentList.get(position).getAction() != null) {

                if (mCommentList.get(position).getAction().equalsIgnoreCase(Constants.LIKE)) {

                    holder.mCommentLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_icon, 0, 0, 0);
                    holder.mCommentDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_icon_grey, 0, 0, 0);
                    holder.mCommentNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_icon_grey, 0, 0, 0);
                    holder.mCommentLike.setEnabled(true);
                    holder.mCommentDislike.setEnabled(false);
                    holder.mCommentNeutral.setEnabled(false);


                } else if (mCommentList.get(position).getAction().equalsIgnoreCase(Constants.DISLIKE)) {
                    holder.mCommentLike.setEnabled(false);
                    holder.mCommentDislike.setEnabled(true);
                    holder.mCommentDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_icon_without_circle, 0, 0, 0);
                    holder.mCommentLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon, 0, 0, 0);
                    holder.mCommentNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_icon_grey, 0, 0, 0);
                    holder.mCommentNeutral.setEnabled(false);

                } else if (mCommentList.get(position).getAction().equalsIgnoreCase(Constants.NEUTRAL)) {
                    holder.mCommentLike.setEnabled(false);
                    holder.mCommentDislike.setEnabled(false);
                    holder.mCommentNeutral.setEnabled(true);
                    holder.mCommentLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon, 0, 0, 0);
                    holder.mCommentDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_icon_grey, 0, 0, 0);
                    holder.mCommentNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_icon, 0, 0, 0);
                } else {

                    holder.mCommentLike.setEnabled(true);
                    holder.mCommentDislike.setEnabled(true);
                    holder.mCommentNeutral.setEnabled(true);
                    holder.mCommentLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon, 0, 0, 0);
                    holder.mCommentDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_icon_grey, 0, 0, 0);
                    holder.mCommentNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_icon_grey, 0, 0, 0);
                }
            } else {

                holder.mCommentLike.setEnabled(true);
                holder.mCommentDislike.setEnabled(true);
                holder.mCommentNeutral.setEnabled(true);
                holder.mCommentLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon, 0, 0, 0);
                holder.mCommentDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_icon_grey, 0, 0, 0);
                holder.mCommentNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_icon_grey, 0, 0, 0);
            }

            holder.mCommentLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mOnCommentLikeClickLitner.onCommentLikeClicked(holder.mCommentLike,position, mCommentList.get(position).getCommentId(), Constants.LIKE);
                }
            });

            holder.mCommentDislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mOnCommentDislikeClickListner.onCommentDislikeClicked(holder.mCommentDislike,position, mCommentList.get(position).getCommentId(), Constants.DISLIKE);
                }
            });

            holder.mCommentNeutral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mOnCommentNeutarlClickListner.onCommentNeutralClicked(holder.mCommentNeutral, position, mCommentList.get(position).getCommentId(),Constants.NEUTRAL);
                }
            });

            holder.mReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mOnCommentReplyClickListner.onCommentreplyClicked(holder.mReply,position,mCommentList.get(position).getCommentId(),Constants.REPLY);
                }
            });

            holder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mOnCommentDeleteClickLisnter.onCommentDeleteClicked(holder.mDelete, position, mCommentList.get(position).getCommentId(),Constants.DELETE);

                }
            });


//
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public int getItemCount() {
        return this.mCommentList.size();
    }


    public class RecyclerViewHolders extends RecyclerView.ViewHolder {

        public TextView mUserName;
        public ImageView mUserIcon;
        public TextView mComment;
        public TextView mCommentLike;
        public TextView mCommentDislike;
        public TextView mCommentNeutral;
        public TextView mReply;
        public ImageView mDelete;

        public RecyclerViewHolders(final View itemView) {
            super(itemView);

            mUserName = (TextView) itemView.findViewById(R.id.tv_user_name_comments);
            mUserIcon = (ImageView) itemView.findViewById(R.id.iv_user_image_comments);
            mComment = (TextView) itemView.findViewById(R.id.tv_user_comment_txt);
            mCommentLike = (TextView) itemView.findViewById(R.id.tv_like_comment);
            mCommentDislike = (TextView) itemView.findViewById(R.id.tv_dislike_comment);
            mCommentNeutral = (TextView) itemView.findViewById(R.id.tv_neutral_comment);
            mReply = (TextView) itemView.findViewById(R.id.tv_comment_reply);
            mDelete = (ImageView) itemView.findViewById(R.id.iv_delete_comment);

        }


    }
}
