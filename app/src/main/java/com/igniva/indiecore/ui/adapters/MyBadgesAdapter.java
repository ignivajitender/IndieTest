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
import com.igniva.indiecore.utils.Utility;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class MyBadgesAdapter extends RecyclerView.Adapter<MyBadgesAdapter.RecyclerViewHolders> {

    private Context mContext;
    private int mTotalBadgeCount;
    public static final int REQUEST_CODE = 500;
    String LOG_TAG = "BadgesAdapter";
    private ArrayList<BadgesPojo> mSelectedBadgeIds;


    public MyBadgesAdapter(Context context, ArrayList<BadgesPojo> mSelectedBadgeIds) {

        this.mContext = context;
        this.mSelectedBadgeIds = mSelectedBadgeIds;

    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_mybadges, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        // final ImageLoader imageLoader = new ImageLoader(mContext);

        try {
            if(mSelectedBadgeIds.get(position).getIsSelectedAsMyBadge()==1){

                holder.mIvOnOffBadge.setVisibility(View.VISIBLE);
                holder.mTvMyBadgeName.setText(mSelectedBadgeIds.get(position).getName());
                // Glide Implementation
                Glide.with(mContext).load(mSelectedBadgeIds.get(position).getIcon())
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.mIvMyBadgeIcon);

            }else {
                holder.mIvOnOffBadge.setVisibility(View.INVISIBLE);
                holder.mIvMyBadgeIcon.setImageResource(R.drawable.add_badge);
            }



            //

            // imageLoader.DisplayImage(mSelectedBadgeIds.get(position).getIcon(), holder.mIvMyBadgeIcon);

//            if (mSelectedBadgeIds.get(position).getiSActive() == 0) {
//                holder.mIvOnOffBadge.setImageResource(R.drawable.badge_off);
//            } else {
//                holder.mIvOnOffBadge.setImageResource(R.drawable.badge_on);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.mIvOnOffBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (mSelectedBadgeIds.get(position).getiSActive() == 0) {
                        holder.mIvOnOffBadge.setImageResource(R.drawable.badge_on);
                        mSelectedBadgeIds.get(position).setiSActive(1);
                        MyBadgesActivity.active = 1;
                        MyBadgesActivity.selectedBadgeId = mSelectedBadgeIds.get(position).getBadgeId();
                    } else {

                        holder.mIvOnOffBadge.setImageResource(R.drawable.badge_off);
                        mSelectedBadgeIds.get(position).setiSActive(0);
                        MyBadgesActivity.active = 0;
                        MyBadgesActivity.selectedBadgeId = "";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //holder.mIvBadgeIcon.setImageResource(itemList.get(position).getIcon());


        holder.mIvMyBadgeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(mContext,"Coming Soon  "  +position,Toast.LENGTH_SHORT).show();
                //BadgesPojo obj= new BadgesPojo(itemList.get(position).getName(),itemList.get(position).getIcon(),itemList.get(position).getDescription());
                if (!(mSelectedBadgeIds.get(position).getName()==null)) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.POSITION, position);
                    bundle.putSerializable("badgePojo", mSelectedBadgeIds.get(position));
                    // Creating an intent to open the activity StudentViewActivity
                    Intent intent = new Intent(mContext, BadgeDetailActivity.class);

                    // Passing data as a parecelable object to StudentViewActivity
                    // intent.putExtra("BadgeData",itemList.get(position));
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
//                     Opening the activity
                //((Activity) mContext).startActivityForResult(intent,REQUEST_CODE);
                }else{
                    //Utility.showToastMessageLong((Activity) mContext,"Coming Soon");
                    ((Activity) mContext).startActivity(new Intent(mContext,MyBadgesActivity.class).putExtra("CLICKED",2));
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return this.mSelectedBadgeIds.size();
    }
    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTvMyBadgeName;
        public ImageView mIvMyBadgeIcon;
        public ImageView mIvOnOffBadge;

        public RecyclerViewHolders(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTvMyBadgeName = (TextView) itemView.findViewById(R.id.tv_mybadge_name);
            mIvMyBadgeIcon = (ImageView) itemView.findViewById(R.id.iv_mybadge);
            mIvOnOffBadge = (ImageView) itemView.findViewById(R.id.iv_badge_on_off);


        }

        @Override
        public void onClick(View view) {


        }
    }


}
