package com.igniva.indiecore.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.igniva.indiecore.R;
import com.igniva.indiecore.model.BadgesPojo;
import com.igniva.indiecore.ui.activities.BadgeDetailActivity;
import com.igniva.indiecore.ui.activities.BadgesActivity;
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

    public BadgesAdapter(Context context, ArrayList<BadgesPojo> itemList, int pageNo,int badgePerPage,  int badgeCount ) {

        this.itemList = getBadges(itemList,pageNo,badgePerPage,badgeCount);
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
    public void onBindViewHolder(RecyclerViewHolders holder, final int position) {
        ImageLoader imageLoader=new ImageLoader(context);
        holder.mTvBadgeName.setText(itemList.get(position).getName());
        imageLoader.DisplayImage(itemList.get(position).getIcon(),holder.mIvBadgeIcon);

        holder.mIvActivateBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context,"position is "+(position)+"this is icon click",Toast.LENGTH_SHORT).show();


            }
        });
        //holder.mIvBadgeIcon.setImageResource(itemList.get(position).getIcon());


        holder.mIvBadgeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"position is "+(itemList.get(position).getName()),Toast.LENGTH_SHORT).show();

                BadgesPojo obj= new BadgesPojo(itemList.get(position).getName(),itemList.get(position).getIcon(),itemList.get(position).getDescription());


                // Creating an intent to open the activity StudentViewActivity
                Intent intent = new Intent(context, BadgeDetailActivity.class);

                // Passing data as a parecelable object to StudentViewActivity
                intent.putExtra("BadgeData",obj);

                // Opening the activity
                context.startActivity(intent);
                Toast.makeText(context, "Recycle Click" +(position+((BadgesActivity.pageNumber-1)*BadgesActivity.badgeCount)), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView mTvBadgeName;
        public ImageView mIvBadgeIcon;
        public ImageView mIvActivateBadge;

        public RecyclerViewHolders(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTvBadgeName = (TextView)itemView.findViewById(R.id.tv_badge_name);
            mIvBadgeIcon = (ImageView)itemView.findViewById(R.id.iv_badge);
            mIvActivateBadge=(ImageView) itemView.findViewById(R.id.iv_badge_selected);



//            mIvBadgeIcon.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    BadgesPojo badgesPojo= new BadgesPojo()
//                    Intent intent= new Intent(context, BadgeDetailActivity.class);
//
//                    context.startActivity(intent);
//                }
//            });
        }

        @Override
        public void onClick(View view) {



//            Toast.makeText(view.getContext(), R.string.coming_soon, Toast.LENGTH_SHORT).show();
        }
    }


    public ArrayList<BadgesPojo> getBadges(ArrayList<BadgesPojo> originalList,int pageNumber,int badgePerPage, int totalBadgeCount) {
        ArrayList<BadgesPojo> badgesList = null;

        // for first time
//        if (originalList.size()<=badgePerPage){
//            return originalList;
//        }
//
        if (true) {
            badgesList = new ArrayList<BadgesPojo>();
            Log.d(LOG_TAG, " total badge " + totalBadgeCount + " size " + pageNumber);
            // if list has still more items and equals to 20
            if (totalBadgeCount > (pageNumber * badgePerPage)) {


                int sizeOfList = originalList.size() - 1;
                for (int i = 0; i < badgePerPage; i++) {
                    badgesList.add(i, originalList.get(badgePerPage * (pageNumber - 1) + i));
                }
//                // case 1 , size is more than 20
//                if (totalBadgeCount-(pageNumber*badgePerPage)>=badgePerPage){
//                    int sizeOfList = originalList.size() - 1;
//                    for (int i = 0; i < badgePerPage; i++) {
//                        badgesList.add(i, originalList.get(badgePerPage + i));
//                    }
//                }
//                // case 2 , size is lesser than 20
//                else{
//                    int sizeOfList = originalList.size() - 1;
//                    for (int i = 0; i <= sizeOfList+1; i++) {
//                        badgesList.add(i, originalList.get(sizeOfList - i));
//                    }
            } else {
                int sizeOfList = (originalList.size()%((pageNumber-1)*badgePerPage))-1;

                for (int i = 0; i <= sizeOfList; i++) {
                    badgesList.add(i, originalList.get((originalList.size()-1)-i));
                }
            }
        }


            //  }


            return badgesList;

    }
}
