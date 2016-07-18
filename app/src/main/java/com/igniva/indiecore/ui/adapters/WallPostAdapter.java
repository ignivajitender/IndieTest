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
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.UsersWallPostPojo;

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
            holder.mUserName.setText("Siddharth A.");
//            if (wallItemsList.get(position).getProfilePic() != null) {
//                Glide.with(context).load(WebServiceClient.HTTP_STAGING + wallItemsList.get(position).getProfilePic())
//                        .thumbnail(1f)
//                        .crossFade()
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .into(holder.mContactImage);
//            }
//
        } catch (Exception e) {
            e.printStackTrace();
        }

//        holder.mCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//
//
//                    Utility.showToastMessageShort((Activity) context, "Coming soon");
//
//
//
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        });

    }


    @Override
    public int getItemCount() {
        return this.wallItemsList.size();
    }


    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mUserName;
        public ImageView mUserIcon;
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

            mMediaPost = (ImageView) itemView.findViewById(R.id.iv_media_post);
            mTextPost = (TextView) itemView.findViewById(R.id.tv_post_text);
            mAvailable = (TextView) itemView.findViewById(R.id.tv_available_);
            like = (TextView) itemView.findViewById(R.id.tv_like);
            dislike = (TextView) itemView.findViewById(R.id.tv_dislike);
            neutral=(TextView) itemView.findViewById(R.id.tv_neutral);
            comment=(TextView) itemView.findViewById(R.id.tv_neutral);
            share=(TextView) itemView.findViewById(R.id.tv_share);
        }

        @Override
        public void onClick(View view) {


        }
    }


}
