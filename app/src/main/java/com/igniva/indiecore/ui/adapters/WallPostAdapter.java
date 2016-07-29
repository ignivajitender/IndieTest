package com.igniva.indiecore.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.OnListItemClickListner;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.PostPojo;
import com.igniva.indiecore.utils.Constants;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class WallPostAdapter extends RecyclerView.Adapter<WallPostAdapter.RecyclerViewHolders> {

    private ArrayList<PostPojo> wallItemsList;
    private Context context;
    String LOG_TAG = "PhonebookAdapter";
    OnListItemClickListner mOnListItemClickListner;


    public WallPostAdapter(Context context, ArrayList<PostPojo> wallItemsList, OnListItemClickListner onListItemClickListner) {

        this.wallItemsList = wallItemsList;
        this.context = context;
        this.mOnListItemClickListner = onListItemClickListner;

    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {


        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wall_post_items, null);
        return new RecyclerViewHolders(layoutView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        try {
            if (!wallItemsList.get(position).getFirstName().isEmpty()) {
                String Name = (wallItemsList.get(position).getFirstName() + " " + (wallItemsList.get(position).getLastName()).charAt(0) + ".");
                holder.mUserName.setText(Name);
            }
            if (wallItemsList.get(position).getProfile_pic() != null) {
                Glide.with(context).load(WebServiceClient.HTTP_STAGING + wallItemsList.get(position).getProfile_pic())
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.mUserIcon);
            }
            String time = wallItemsList.get(position).getDate_created().replace("T", " @ ");
            holder.mPostTime.setText(time);

            if (wallItemsList.get(position).getMediaUrl() != null) {
                holder.mMediaPost.setVisibility(View.VISIBLE);
                Glide.with(context).load(WebServiceClient.HTTP_STAGING + wallItemsList.get(position).getMediaUrl())
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.mMediaPost);
            } else {
                holder.mMediaPost.setVisibility(View.GONE);
            }

            holder.mTextPost.setText(wallItemsList.get(position).getText());
            holder.like.setText(wallItemsList.get(position).getLike());
            holder.dislike.setText(wallItemsList.get(position).getDislike());
            holder.neutral.setText(wallItemsList.get(position).getNeutral());
            holder.comment.setText(wallItemsList.get(position).getComment());


//            if (wallItemsList.get(position).getAction() != null) {
//                if (wallItemsList.get(position).getAction().equalsIgnoreCase(Constants.LIKE)) {
//                    Drawable img_like_blu = context.getResources().getDrawable(R.drawable.like_blue_icon_circle);
//                    holder.like.setCompoundDrawablesWithIntrinsicBounds(img_like_blu, null, null, null);
//                    holder.dislike.setEnabled(false);
//                    holder.neutral.setEnabled(false);
//
//                } else if (wallItemsList.get(position).getAction().equalsIgnoreCase(Constants.DISLIKE)) {
//
//                    Drawable img_dislike_blu = context.getResources().getDrawable(R.drawable.dislike_blue_icon_circle);
//                    holder.dislike.setCompoundDrawablesWithIntrinsicBounds(img_dislike_blu, null, null, null);
//                    holder.like.setEnabled(false);
//                    holder.neutral.setEnabled(false);
//
//
//                } else if (wallItemsList.get(position).getAction().equalsIgnoreCase(Constants.NEUTRAL)) {
//
//                    Drawable img_neutral_blu = context.getResources().getDrawable(R.drawable.hand_icon_blue_circle);
//                    holder.neutral.setCompoundDrawablesWithIntrinsicBounds(img_neutral_blu, null, null, null);
//                    holder.dislike.setEnabled(false);
//                    holder.like.setEnabled(false);
//
//                }
//            } else {
//
//                holder.like.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.like_grey_icon_circle), null, null, null);
//                holder.dislike.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.dislike_grey_icon_circle), null, null, null);
//                holder.neutral.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.hand_grey_icon_circle), null, null, null);
//                holder.like.setEnabled(true);
//                holder.dislike.setEnabled(true);
//                holder.neutral.setEnabled(true);
//
//
//            }

            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnListItemClickListner.onListItemClicked(holder, position, "test");
                    // mOnListItemClickListner.onListItemClicked(holder, position, wallItemsList.get(position).getPostId());
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

        }

    }


}
