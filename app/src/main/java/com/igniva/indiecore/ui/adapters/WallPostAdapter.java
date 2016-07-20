package com.igniva.indiecore.ui.adapters;

import android.app.Activity;
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
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.UsersWallPostPojo;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class WallPostAdapter extends RecyclerView.Adapter<WallPostAdapter.RecyclerViewHolders> {

    private ArrayList<UsersWallPostPojo> wallItemsList;
    private Context context;
    String LOG_TAG = "PhonebookAdapter";


    public WallPostAdapter(Context context, ArrayList<UsersWallPostPojo> wallItemsList) {

        this.wallItemsList = wallItemsList;
        this.context = context;

    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {


        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wall_post_items, parent, false);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
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
            holder.mPostTime.setText(wallItemsList.get(position).getDate_created());

            if (wallItemsList.get(position).getMediaUrl() != null) {
                holder.mMediaPost.setVisibility(View.VISIBLE);
                Glide.with(context).load(WebServiceClient.HTTP_STAGING + wallItemsList.get(position).getMediaUrl())
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.mMediaPost);
            }else {

                holder.mMediaPost.setVisibility(View.GONE);
            }

            holder.mTextPost.setText(wallItemsList.get(position).getText());


            holder.like.setText(wallItemsList.get(position).getLike());

            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Utility.showToastMessageLong((Activity) context,"like");
                }
            });
            holder.dislike.setText(wallItemsList.get(position).getDislike());
            holder.dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.showToastMessageLong((Activity) context,"dislike");

                }
            });
            holder.neutral.setText(wallItemsList.get(position).getNeutral());
            holder.neutral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.showToastMessageLong((Activity) context,"neutral");


                }
            });
            holder.comment.setText(wallItemsList.get(position).getComment());
            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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


    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mUserName;
        public ImageView mUserIcon;
        public TextView mPostTime;
        public ImageView mMediaPost;
        public TextView mTextPost;
        public TextView mAvailable;
        public  TextView like;
        public  TextView dislike;
        public  TextView neutral;
        public  TextView comment;
        public TextView  share;


        public RecyclerViewHolders(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mUserName = (TextView) itemView.findViewById(R.id.tv_user_name_);
            mUserIcon=(ImageView) itemView.findViewById(R.id.iv_user_img_);

            mPostTime=(TextView) itemView.findViewById(R.id.tv_post_time_);

            mMediaPost = (ImageView) itemView.findViewById(R.id.iv_media_post);
            mTextPost = (TextView) itemView.findViewById(R.id.tv_post_text);
            mAvailable = (TextView) itemView.findViewById(R.id.tv_available_);
            like = (TextView) itemView.findViewById(R.id.tv_like);
            dislike = (TextView) itemView.findViewById(R.id.tv_dislike);
            neutral=(TextView) itemView.findViewById(R.id.tv_neutral);
            comment=(TextView) itemView.findViewById(R.id.tv_comment);
            share=(TextView) itemView.findViewById(R.id.tv_share);
        }

        @Override
        public void onClick(View view) {


        }
    }


}
