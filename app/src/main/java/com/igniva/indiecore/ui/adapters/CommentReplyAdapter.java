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
import com.igniva.indiecore.controller.OnReplyDeleteClickListner;
import com.igniva.indiecore.controller.OnReplyDislikeClickListner;
import com.igniva.indiecore.controller.OnReplyLikeClickListner;
import com.igniva.indiecore.controller.OnReplyNeutralClickListner;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.RepliesPojo;
import com.igniva.indiecore.utils.Constants;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class CommentReplyAdapter extends RecyclerView.Adapter<CommentReplyAdapter.RecyclerViewHolders> {

    private ArrayList<RepliesPojo> mRelpiesList;
    private Context context;
    String LOG_TAG = "PostRepliesAdapter";
    OnReplyLikeClickListner  mOnReplyLikeClick;
    OnReplyDislikeClickListner mOnReplyDisLikeClick;
    OnReplyNeutralClickListner mOnReplyNeutralClick;
    OnReplyDeleteClickListner mOnReplyDeleteClick;


    public CommentReplyAdapter(Context context, ArrayList<RepliesPojo> repliesList, OnReplyLikeClickListner onReplyLIkeClickListner, OnReplyDislikeClickListner onReplyDislikeClickListner, OnReplyNeutralClickListner onReplyNeutralClickListner, OnReplyDeleteClickListner onReplyDeleteClickListner) {
        this.mRelpiesList = repliesList;
        this.context = context;
        this.mOnReplyLikeClick = onReplyLIkeClickListner;
        this.mOnReplyDisLikeClick=onReplyDislikeClickListner;
        this.mOnReplyNeutralClick=onReplyNeutralClickListner;
        this.mOnReplyDeleteClick=onReplyDeleteClickListner;

    }


    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.replies_list_items, parent, false);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;

    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        try {
            if (!mRelpiesList.get(position).getFirstName().isEmpty()) {
                String Name = (mRelpiesList.get(position).getFirstName() + " " + (mRelpiesList.get(position).getLastName()).charAt(0) + ".");
                holder.mUserName.setText(Name);
            }
            if (mRelpiesList.get(position).getProfile_pic() != null) {
                Glide.with(context).load(WebServiceClient.HTTP_STAGING + mRelpiesList.get(position).getProfile_pic())
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.mUserIcon);
            }

            holder.mReply.setText(mRelpiesList.get(position).getText()+"");
            holder.mReplyLike.setText(mRelpiesList.get(position).getLike()+"");
            holder.mReplyDislike.setText(mRelpiesList.get(position).getDislike()+"");
            holder.mReplyNeutral.setText(mRelpiesList.get(position).getNeutral()+"");


            if (mRelpiesList.get(position).getRelation().equalsIgnoreCase(Constants.SELF)) {

                holder.mImageDelete.setVisibility(View.VISIBLE);
            } else {
                holder.mImageDelete.setVisibility(View.GONE);

            }


            if (mRelpiesList.get(position).getAction() != null) {

                if (mRelpiesList.get(position).getAction().equalsIgnoreCase(Constants.LIKE)) {

                    holder.mReplyLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_icon, 0, 0, 0);
                    holder.mReplyDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_icon_grey_without_circle,0,0,0);
                    holder.mReplyNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_icon_grey,0,0,0);
                    holder.mReplyLike.setEnabled(true);
                    holder.mReplyDislike.setEnabled(false);
                    holder.mReplyNeutral.setEnabled(false);


                } else if (mRelpiesList.get(position).getAction().equalsIgnoreCase(Constants.DISLIKE)) {
                    holder.mReplyLike.setEnabled(false);
                    holder.mReplyDislike.setEnabled(true);
                    holder.mReplyDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_icon_without_circle, 0, 0, 0);
                    holder.mReplyLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon,0,0,0);
                    holder.mReplyNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_icon_grey,0,0,0);
                    holder.mReplyNeutral.setEnabled(false);

                } else if(mRelpiesList.get(position).getAction().equalsIgnoreCase(Constants.NEUTRAL)){
                    holder.mReplyLike.setEnabled(false);
                    holder.mReplyDislike.setEnabled(false);
                    holder.mReplyNeutral.setEnabled(true);
                    holder.mReplyNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_icon, 0, 0, 0);
                    holder.mReplyLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon,0,0,0);
                    holder.mReplyDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_icon_grey_without_circle,0,0,0);


                } else {

                    holder.mReplyLike.setEnabled(true);
                    holder.mReplyDislike.setEnabled(true);
                    holder.mReplyNeutral.setEnabled(true);
                    holder.mReplyLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon,0,0,0);
                    holder.mReplyDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_icon_grey_without_circle,0,0,0);
                    holder.mReplyNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_icon_grey,0,0,0);
                }
            
        }else{

            holder.mReplyLike.setEnabled(true);
            holder.mReplyDislike.setEnabled(true);
            holder.mReplyNeutral.setEnabled(true);
                holder.mReplyLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_grey_icon,0,0,0);
                holder.mReplyDislike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_icon_grey_without_circle,0,0,0);
                holder.mReplyNeutral.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hand_icon_grey,0,0,0);

        }

            holder.mReplyLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   mOnReplyLikeClick.onReplyLikeClicked(holder.mReplyLike,position,mRelpiesList.get(position).getReplyId(),Constants.LIKE);
                }
            });

            holder.mReplyDislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnReplyDisLikeClick.onReplyDislikeClicked(holder.mReplyDislike,position,mRelpiesList.get(position).getReplyId(),Constants.DISLIKE);
                }
            });

            holder.mReplyNeutral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mOnReplyNeutralClick.onReplyNeutralClicked(holder.mReplyNeutral,position,mRelpiesList.get(position).getReplyId(),Constants.REPLY);
                }
            });

        holder.mImageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnReplyDeleteClick.onReplyDeleteClicked(holder.mImageDelete,position,mRelpiesList.get(position).getReplyId(),Constants.DELETE);
            }
        });
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }


}


    @Override
    public int getItemCount() {
        return this.mRelpiesList.size();
    }


    public class RecyclerViewHolders extends RecyclerView.ViewHolder {

        public TextView mUserName;
        public ImageView mUserIcon;
        public TextView mReplyLike;
        public TextView mReplyDislike;
        public TextView mReplyNeutral;
        public TextView mReply;
        public  ImageView mImageDelete;


        public RecyclerViewHolders(final View itemView) {
            super(itemView);

            mUserName = (TextView) itemView.findViewById(R.id.tv_user_name_reply);
            mUserIcon = (ImageView) itemView.findViewById(R.id.iv_user_image_reply);
            mReply = (TextView) itemView.findViewById(R.id.tv_user_reply_txt);
            mReplyDislike = (TextView) itemView.findViewById(R.id.tv_dislike_reply);
            mReplyLike = (TextView) itemView.findViewById(R.id.tv_like_reply);
            mReplyNeutral = (TextView) itemView.findViewById(R.id.tv_neutral_reply);
            mImageDelete=(ImageView) itemView.findViewById(R.id.iv_delete_reply);

        }


    }
}
