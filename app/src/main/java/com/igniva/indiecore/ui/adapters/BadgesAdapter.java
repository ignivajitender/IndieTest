package com.igniva.indiecore.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.igniva.indiecore.R;
import com.igniva.indiecore.model.BadgesPojo;
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.imageloader.ImageLoader;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class BadgesAdapter extends RecyclerView.Adapter<BadgesAdapter.RecyclerViewHolders> {

    private ArrayList<BadgesPojo> itemList;
    private Context context;
    private int mTotalBadgeCount;
    String LOG_TAG="BadgesAdapter";

    public BadgesAdapter(Context context, ArrayList<BadgesPojo> itemList, int pageNo, int badgeCount) {

        this.itemList = getBadges(itemList,pageNo,badgeCount);
        this.context = context;
        mTotalBadgeCount=badgeCount;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_badges, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {
        ImageLoader imageLoader=new ImageLoader(context);
        holder.mTvBadgeName.setText(itemList.get(position).getName());
        imageLoader.DisplayImage(itemList.get(position).getIcon(),holder.mIvBadgeIcon);
        //holder.mIvBadgeIcon.setImageResource(itemList.get(position).getIcon());
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView mTvBadgeName;
        public ImageView mIvBadgeIcon;

        public RecyclerViewHolders(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTvBadgeName = (TextView)itemView.findViewById(R.id.tv_badge_name);
            mIvBadgeIcon = (ImageView)itemView.findViewById(R.id.iv_badge);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "Clicked badge Position = " + getPosition(), Toast.LENGTH_SHORT).show();
        }
    }


    public ArrayList<BadgesPojo> getBadges(ArrayList<BadgesPojo> originalList,int size, int totalBadgeCount){
        ArrayList<BadgesPojo> badgesList=null;
        // for first time
        if (originalList.size()<21){
            return originalList;
        }else{
            badgesList = new ArrayList<BadgesPojo>();
            // if list has still more items and equals to 20
            Log.d(LOG_TAG," total badge "+totalBadgeCount +" size "+size);
            if((size+20)<totalBadgeCount) {

                int sizeOfList = originalList.size() - 1;
                for (int i = 0; i < 20; i++) {
                    badgesList.add(i, originalList.get(sizeOfList - i));
                }
            }
            // if list has items but less than 20
             else{
                int sizeOfList=totalBadgeCount%20;
                for (int i = 0; i < sizeOfList; i++) {
                    badgesList.add(i, originalList.get(sizeOfList - i));
                }
            }
        }


        return  badgesList;
    }
}
