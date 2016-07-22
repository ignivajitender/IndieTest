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
import com.igniva.indiecore.model.PremiumBadgePojo;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class PremiumBadgesAdapter extends RecyclerView.Adapter<PremiumBadgesAdapter.RecyclerViewHolders> {

    private ArrayList<PremiumBadgePojo> mPremiumBadgeList;
    private Context context;
    String LOG_TAG = "PremiumBadgeAdapter";


    public PremiumBadgesAdapter(Context context, ArrayList<PremiumBadgePojo> premiumBadgeList) {

        this.mPremiumBadgeList = premiumBadgeList;
        this.context = context;

    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {


        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.premium_badges_list_items, parent, false);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        try {


            holder.mBadgeIcon.setImageResource(R.drawable.default_user);
            holder.mBadgeName.setText(mPremiumBadgeList.get(position).getBadgeName());
            holder.mBadgePrice.setText(mPremiumBadgeList.get(position).getBadgePrice());



//            mOnCommentListItemClickListner.onCommentListItemClicked(holder,position,wallItemsList.get(position).getPostId());

//
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public int getItemCount() {
        return this.mPremiumBadgeList.size();
    }


    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mBadgeName;
        public ImageView mBadgeIcon;
        public TextView mBadgePrice;




        public RecyclerViewHolders(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mBadgeIcon=(ImageView) itemView.findViewById(R.id.iv_premium_badge_icon);
            mBadgeName = (TextView) itemView.findViewById(R.id.tv_premium_badge_name);
            mBadgePrice = (TextView) itemView.findViewById(R.id.tv_premium_badge_price);
        }

        @Override
        public void onClick(View view) {


        }
    }


}
