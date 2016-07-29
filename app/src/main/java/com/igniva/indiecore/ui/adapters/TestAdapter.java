package com.igniva.indiecore.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.OnCommentListItemClickListnerTest;
import com.igniva.indiecore.controller.OnListItemClickListner;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.PostPojo;
import com.igniva.indiecore.utils.Constants;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class TestAdapter extends RecyclerView.Adapter<TestAdapter.RecyclerViewHolders> {

    private ArrayList<PostPojo> wallItemsList;
    private Context context;
    String LOG_TAG = "PhonebookAdapter";
    OnCommentListItemClickListnerTest mOnListItemClickListner;


    public TestAdapter(Context context, ArrayList<PostPojo> wallItemsList, OnCommentListItemClickListnerTest onListItemClickListner) {

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


            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnListItemClickListner.onCommentListItemClicked(holder, position, "test");
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
//        public TextView  share;
//        public LinearLayout mCommentSection;
//        public EditText mEtComment;
//        public  ImageView mIvPostComment;
//        public RecyclerView mRvComments;


        public RecyclerViewHolders(View itemView) {
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
//            share=(TextView) itemView.findViewById(R.id.tv_share);
//            mCommentSection=(LinearLayout) itemView.findViewById(R.id.ll_comment_part);

//            mEtComment=(EditText) itemView.findViewById(R.id.et_comment_text);
//            mIvPostComment=(ImageView) itemView.findViewById(R.id.iv_post_comment);
//            mRvComments=(RecyclerView) itemView.findViewById(R.id.rv_comments);

        }


    }


}
