package com.igniva.indiecore.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.model.BadgesPojo;
import com.igniva.indiecore.ui.activities.BadgeDetailActivity;
import com.igniva.indiecore.ui.activities.BadgesActivity;
import com.igniva.indiecore.ui.activities.MyBadgesActivity;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.Utility;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class BadgesMarketAdapter extends RecyclerView.Adapter<BadgesMarketAdapter.RecyclerViewHolders> {

    private Context mContext;
    private int mTotalBadgeCount;
    public static final int REQUEST_CODE = 500;
    String LOG_TAG = "BadgesAdapter";
    private ArrayList<BadgesPojo> mBadgeMarketList;


    public BadgesMarketAdapter(Context mContext, ArrayList<BadgesPojo> mSelectedBadgeIds) {

        this.mContext = mContext;
        this.mBadgeMarketList = mSelectedBadgeIds;

    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_badge_market, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {

        try {
            holder.mTvMyBadgeName.setText(mBadgeMarketList.get(position).getName());
            Glide.with(mContext).load(mBadgeMarketList.get(position).getIcon())
                    .thumbnail(1f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.mIvMyBadgeIcon);
        }catch (Exception e){
            e.printStackTrace();
        }


                 holder.mIvGetThisBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mBadgeMarketList.get(position).isSelected()) {
                        holder.mIvGetThisBadge.setImageResource(R.drawable.get_badge);
//                     MyBadgesActivity.selectedBadgeId= mBadgeMarketList.get(position).getBadgeId();
                        mBadgeMarketList.get(position).setSelected(false);
                    } else {
                        holder.mIvGetThisBadge.setImageResource(R.drawable.tick_badge);
//                    mBadgeMarketList.get(position).setiSActive(0);
                        mBadgeMarketList.get(position).setSelected(true);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
//        //holder.mIvBadgeIcon.setImageResource(itemList.get(position).getIcon());
//

        holder.mIvMyBadgeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Toast.makeText(mContext,"Coming Soon",Toast.LENGTH_SHORT).show();
//
//                //BadgesPojo obj= new BadgesPojo(itemList.get(position).getName(),itemList.get(position).getIcon(),itemList.get(position).getDescription());
//
//                Bundle bundle=new Bundle();
//                bundle.putInt(Constants.POSITION,position);
//                bundle.putSerializable("badgePojo",mBadgeMarketList.get(position));
//                // Creating an intent to open the activity StudentViewActivity
//                Intent intent = new Intent(mContext, BadgeDetailActivity.class);
//
//                // Passing data as a parecelable object to StudentViewActivity
//               // intent.putExtra("BadgeData",itemList.get(position));
//                intent.putExtras(bundle);
//                // Opening the activity
//                ((Activity) mContext).startActivityForResult(intent,REQUEST_CODE);
            }
        });


    }


    @Override
    public int getItemCount() {
        return this.mBadgeMarketList.size();
    }

    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTvMyBadgeName;
        private ImageView mIvMyBadgeIcon;
        private ImageView mIvGetThisBadge;

        public RecyclerViewHolders(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            try {

                mTvMyBadgeName = (TextView) itemView.findViewById(R.id.tv_badge_name_badge_market);
                mIvMyBadgeIcon = (ImageView) itemView.findViewById(R.id.iv_badge_icon_badge_market);
                mIvGetThisBadge = (ImageView) itemView.findViewById(R.id.iv_get_this_badge_badge_market);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public void onClick(View view) {


        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MyAdapter", "onActivityResult");
        // check if the request code is same as what is passed  here it is 2

        try {




        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
