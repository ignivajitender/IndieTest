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
import com.igniva.indiecore.model.CommentPojo;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class PostCommentAdapter extends RecyclerView.Adapter<PostCommentAdapter.RecyclerViewHolders> {

    private ArrayList<CommentPojo> mCommentList;
    private Context context;
    String LOG_TAG = "PostCommentAdapter";


    public PostCommentAdapter(Context context, ArrayList<CommentPojo> commentList) {
        this.mCommentList = commentList;
        this.context = context;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {


//        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_list_items, parent, false);
//        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
//        return rcv;

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_list_items, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        try {
            if (!mCommentList.get(position).getFirstName().isEmpty()) {
                String Name = (mCommentList.get(position).getFirstName() + " " + (mCommentList.get(position).getLastName()).charAt(0) + ".");
                holder.mUserName.setText(Name);
            }
            if (mCommentList.get(position).getProfile_pic() != null) {
                Glide.with(context).load(WebServiceClient.HTTP_STAGING + mCommentList.get(position).getProfile_pic())
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.mUserIcon);
            }

            holder.mComment.setText(mCommentList.get(position).getText());
            holder.mCommentLike.setText(mCommentList.get(position).getLike());
            holder.mCommentDislike.setText(mCommentList.get(position).getDislike());
            holder.mShare.setText(mCommentList.get(position).getReplie());



//            mOnCommentListItemClickListner.onCommentListItemClicked(holder,position,wallItemsList.get(position).getPostId());

//
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public int getItemCount() {
        return this.mCommentList.size();
    }


    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mUserName;
        public ImageView mUserIcon;
        public TextView mComment;
        public TextView  mCommentLike;
        public TextView mCommentDislike;
        public TextView mNeutral;
        public TextView mShare;



        public RecyclerViewHolders(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mUserName = (TextView) itemView.findViewById(R.id.tv_user_name_comments);
            mUserIcon=(ImageView) itemView.findViewById(R.id.iv_user_image_comments);
            mComment = (TextView) itemView.findViewById(R.id.tv_user_comment_txt);
            mCommentLike = (TextView) itemView.findViewById(R.id.tv_like_comment);
            mCommentDislike = (TextView) itemView.findViewById(R.id.tv_dislike_comment);
            mNeutral = (TextView) itemView.findViewById(R.id.tv_neutral_comment);
            mShare = (TextView) itemView.findViewById(R.id.tv_comment_reply);

        }

        @Override
        public void onClick(View view) {


        }
    }


}
