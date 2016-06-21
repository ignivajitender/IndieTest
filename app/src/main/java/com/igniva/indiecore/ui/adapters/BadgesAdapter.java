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

import com.igniva.indiecore.R;
import com.igniva.indiecore.model.BadgesPojo;
import com.igniva.indiecore.ui.activities.BadgeDetailActivity;
import com.igniva.indiecore.ui.activities.BadgesActivity;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.Utility;
import com.igniva.indiecore.utils.imageloader.ImageLoader;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class BadgesAdapter extends RecyclerView.Adapter<BadgesAdapter.RecyclerViewHolders> {

    private ArrayList<BadgesPojo> itemList;
    private Context context;
    private int mTotalBadgeCount;
    public static  final int REQUEST_CODE=500;
    String LOG_TAG="BadgesAdapter";
    private ArrayList<String> mSelectedBadgeIds= new ArrayList<String>();


    public BadgesAdapter(Context context, ArrayList<BadgesPojo> itemList, int pageNo,int badgePerPage,  int badgeCount,ArrayList<String> mSelectedBadgeIds ) {

        this.itemList = getBadges(itemList,pageNo,badgePerPage,badgeCount);
        this.context = context;
        mTotalBadgeCount=badgeCount;
        this.mSelectedBadgeIds=mSelectedBadgeIds;

    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_badges, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        final ImageLoader imageLoader=new ImageLoader(context);
        holder.mTvBadgeName.setText(itemList.get(position).getName());
        imageLoader.DisplayImage(itemList.get(position).getIcon(),holder.mIvBadgeIcon);

        //
        if(itemList.get(position).isSelected()==true){

          Log.d("","");
            Log.d("","");

            holder.mIvActivateBadge.setImageResource(R.drawable.tick_badge);
        }

        holder.mIvActivateBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(itemList.get(position).isSelected()){
                    return;
                }

           if(mSelectedBadgeIds.size()<10) {
               holder.mIvActivateBadge.setImageResource(R.drawable.tick_badge);
               itemList.get(position).setSelected(true);
               addSelectedBadgeIds(position);
           } else {

               Utility.showAlertDialogInviteAndBuy("You can Select maximum 10 badges free,to get more either invite friends or purchase",context);
               return;


           }
//                mSelectedBadgeIds.add(itemList.get(position).getBadgeId());


            }
        });
        //holder.mIvBadgeIcon.setImageResource(itemList.get(position).getIcon());


        holder.mIvBadgeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"position is "+(itemList.get(position).getName()),Toast.LENGTH_SHORT).show();

                //BadgesPojo obj= new BadgesPojo(itemList.get(position).getName(),itemList.get(position).getIcon(),itemList.get(position).getDescription());

                Bundle bundle=new Bundle();
                bundle.putInt(Constants.POSITION,position);
                bundle.putSerializable("badgePojo",itemList.get(position));
                // Creating an intent to open the activity StudentViewActivity
                Intent intent = new Intent(context, BadgeDetailActivity.class);

                // Passing data as a parecelable object to StudentViewActivity
               // intent.putExtra("BadgeData",itemList.get(position));
                intent.putExtras(bundle);
                // Opening the activity
                ((Activity) context).startActivityForResult(intent,REQUEST_CODE);
                Toast.makeText(context, "Recycle Click" +(position+((BadgesActivity.pageNumber-1)*BadgesActivity.badgeCount)), Toast.LENGTH_SHORT).show();
            }
        });





    }

    public  void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MyAdapter", "onActivityResult");
        // check if the request code is same as what is passed  here it is 2
        if(resultCode==2) {
            String badgeId = data.getStringExtra(Constants.BADGEIDS);
            int pos = data.getIntExtra(Constants.POSITION, 0);

            if(itemList.get(pos).isSelected()){
                return;
            }
            if (mSelectedBadgeIds.size()<10) {

                itemList.get(pos).setSelected(true);
                addSelectedBadgeIds(pos);

//            mSelectedBadgeIds.add(itemList.get(pos).getBadgeId());
                this.notifyDataSetChanged();
                //itemList.get()
            }else {

                Utility.showAlertDialogInviteAndBuy("You can Select maximum 10 badges free,to get more either invite friends or purchase",context);
                 return;
            }
        }


    }


    public void addSelectedBadgeIds(int postion){

        if(!mSelectedBadgeIds.contains(itemList.get(postion).getBadgeId())){

            mSelectedBadgeIds.add(itemList.get(postion).getBadgeId());
        }
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
