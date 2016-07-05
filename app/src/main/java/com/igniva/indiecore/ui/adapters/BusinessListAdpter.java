package com.igniva.indiecore.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.OnCardClickListner;
import com.igniva.indiecore.model.BadgesPojo;
import com.igniva.indiecore.model.BusinessPojo;
import com.igniva.indiecore.ui.activities.BadgeDetailActivity;
import com.igniva.indiecore.ui.activities.MyBadgesActivity;
import com.igniva.indiecore.utils.Constants;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class BusinessListAdpter extends RecyclerView.Adapter<BusinessListAdpter.RecyclerViewHolders> {

    private Context mContext;
    String LOG_TAG = "BusinessListAdapter";
    private ArrayList<BusinessPojo> mBusinessList;
    private final OnCardClickListner listener;

    public BusinessListAdpter(Context context, ArrayList<BusinessPojo> mBusinessList, OnCardClickListner onCardClickListner) {

        this.mContext = context;
        this.mBusinessList = mBusinessList;
        this.listener = onCardClickListner;
    }



    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_busineses, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        // final ImageLoader imageLoader = new ImageLoader(mContext);
String address="";
        try {
            holder.mTVBusinessName.setText(mBusinessList.get(position).getName());

            String distance= String.valueOf(mBusinessList.get(position).getDistance()).substring(0,4);
            holder.mTVDistance.setText(distance+" miles away");
            holder.mUserCount.setText(mBusinessList.get(position).getUser_count());
            Glide.with(mContext).load(mBusinessList.get(position).getImage_url())
                    .thumbnail(1f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.mIvBusinessIcon);

            int size=mBusinessList.get(position).getLocation().getAddress().size();
            for (int i=0;i<size;i++){
                address=address+","+mBusinessList.get(position).getLocation().getAddress().get(i);
                 address = address.replaceFirst("^,",
                        "");
            }
            holder.mTvAddress.setText(address);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if(mBusinessList.get(position).getBadge_status()==1){

            holder.mIvOnOffBadgeStatus.setImageResource(R.drawable.badge_on);
        } else {
            holder.mIvOnOffBadgeStatus.setImageResource(R.drawable.badge_off);
        }


        holder.mIvOnOffBadgeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCardClicked(holder.mIvOnOffBadgeStatus,position);
            }
        });



    }


    @Override
    public int getItemCount() {
        return this.mBusinessList.size();
    }
    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTVBusinessName;
        public ImageView mIvBusinessIcon;
        public ImageView mIvOnOffBadgeStatus;
        public TextView mTvAddress;
        public TextView mTVDistance;
        public TextView mUserCount;

        public RecyclerViewHolders(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTVBusinessName = (TextView) itemView.findViewById(R.id.tv_business_name);
            mIvBusinessIcon = (ImageView) itemView.findViewById(R.id.iv_business_icon);
            mIvOnOffBadgeStatus = (ImageView) itemView.findViewById(R.id.iv_badge_on_off_for_business);
            mTvAddress = (TextView) itemView.findViewById(R.id.tv_business_address);
            mTVDistance = (TextView) itemView.findViewById(R.id.tv_distance);
            mUserCount= (TextView) itemView.findViewById(R.id.tv_user_count);

        }

        @Override
        public void onClick(View view) {


        }
    }



}
