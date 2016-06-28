package com.igniva.indiecore.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.igniva.indiecore.R;
import com.igniva.indiecore.model.BadgesPojo;
import com.igniva.indiecore.ui.activities.MyBadgesActivity;
import com.igniva.indiecore.utils.imageloader.ImageLoader;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class BadgesMarketAdapter extends RecyclerView.Adapter<BadgesMarketAdapter.RecyclerViewHolders> {

    private Context context;
    private int mTotalBadgeCount;
    public static final int REQUEST_CODE = 500;
    String LOG_TAG = "BadgesAdapter";
    private ArrayList<BadgesPojo> mBadgeMarketList;


    public BadgesMarketAdapter(Context context, ArrayList<BadgesPojo> mSelectedBadgeIds) {

        this.context = context;
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
        final ImageLoader imageLoader = new ImageLoader(context);
        holder.mTvMyBadgeName.setText(mBadgeMarketList.get(position).getName());
        imageLoader.DisplayImage(mBadgeMarketList.get(position).getIcon(), holder.mIvMyBadgeIcon);

        if (mBadgeMarketList.get(position).getiSActive() == 0) {
            holder.mIvGetThisBadge.setImageResource(R.drawable.badge_off);
        } else {

            holder.mIvGetThisBadge.setImageResource(R.drawable.badge_on);
        }

        holder.mIvGetThisBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBadgeMarketList.get(position).getiSActive() == 0) {
                    holder.mIvGetThisBadge.setImageResource(R.drawable.badge_on);
                    mBadgeMarketList.get(position).setiSActive(1);
                    MyBadgesActivity.active=1;
                    MyBadgesActivity.selectedBadgeId= mBadgeMarketList.get(position).getBadgeId();
                } else {

                    holder.mIvGetThisBadge.setImageResource(R.drawable.badge_off);
                    mBadgeMarketList.get(position).setiSActive(0);
                    MyBadgesActivity.active=0;
                    MyBadgesActivity.selectedBadgeId="";
                }

            }
        });
//        //holder.mIvBadgeIcon.setImageResource(itemList.get(position).getIcon());
//
//
//        holder.mIvBadgeIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(context,"position is "+(itemList.get(position).getName()),Toast.LENGTH_SHORT).show();
//
//                //BadgesPojo obj= new BadgesPojo(itemList.get(position).getName(),itemList.get(position).getIcon(),itemList.get(position).getDescription());
//
//                Bundle bundle=new Bundle();
//                bundle.putInt(Constants.POSITION,position);
//                bundle.putSerializable("badgePojo",itemList.get(position));
//                // Creating an intent to open the activity StudentViewActivity
//                Intent intent = new Intent(context, BadgeDetailActivity.class);
//
//                // Passing data as a parecelable object to StudentViewActivity
//               // intent.putExtra("BadgeData",itemList.get(position));
//                intent.putExtras(bundle);
//                // Opening the activity
//                ((Activity) context).startActivityForResult(intent,REQUEST_CODE);
//                Toast.makeText(context, "Recycle Click" +(position+((BadgesActivity.pageNumber-1)*BadgesActivity.badgeCount)), Toast.LENGTH_SHORT).show();
//            }
//        });


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
            mTvMyBadgeName = (TextView) itemView.findViewById(R.id.tv_badge_name_badge_market);
            mIvMyBadgeIcon = (ImageView) itemView.findViewById(R.id.iv_badge_icon_badge_market);
            mIvGetThisBadge = (ImageView) itemView.findViewById(R.id.iv_get_this_badge_badge_market);


        }

        @Override
        public void onClick(View view) {


        }
    }


}
