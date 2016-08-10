package com.igniva.indiecore.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.OnPremiumBadgeClick;
import com.igniva.indiecore.model.BadgesPojo;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class PremiumBadgesAdapter extends RecyclerView.Adapter<PremiumBadgesAdapter.RecyclerViewHolders> {

    private ArrayList<BadgesPojo> mPremiumBadgeList;
    private Context context;
    private Boolean IsSelected=false;
    PremiumBadgesAdapter premiumBadgesAdapter;
    BadgesPojo PremiumBadge;
    String LOG_TAG = "PremiumBadgeAdapter";
    OnPremiumBadgeClick onPremiumBadgeClick;
    int oldPostion=-1;
    RelativeLayout oldRelativeLayout;


    public PremiumBadgesAdapter(Context context, ArrayList<BadgesPojo> premiumBadgeList ,OnPremiumBadgeClick onPremiumBadgeClick) {

        this.mPremiumBadgeList = premiumBadgeList;
        this.context = context;
        this.onPremiumBadgeClick=onPremiumBadgeClick;

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
            if (mPremiumBadgeList.get(position).getIcon() != null) {
                Glide.with(context).load(mPremiumBadgeList.get(position).getIcon())
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.mBadgeIcon);
            }else {
                holder.mBadgeIcon.setImageResource(R.drawable.albino_icon);
            }
            holder.mBadgeName.setText(mPremiumBadgeList.get(position).getName());
            holder.mBadgePrice.setText("£"+mPremiumBadgeList.get(position).getPrice()+" /");


              holder.mRlPremiumBadge.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      try{
                          if(mPremiumBadgeList.get(oldPostion).isSelected()&&oldPostion!=position){
                              oldRelativeLayout.setBackgroundColor(Color.parseColor("#1C6DCE"));
                          }
                      }catch (Exception e){
                          e.printStackTrace();
                      }
                      try {
                          if(mPremiumBadgeList.get(position).isSelected()==false){
                              holder.mRlPremiumBadge.setBackgroundColor(Color.parseColor("#77000000"));
                              mPremiumBadgeList.get(position).setSelected(true);
                          } else {
                              holder.mRlPremiumBadge.setBackgroundColor(Color.parseColor("#1C6DCE"));
                              mPremiumBadgeList.get(position).setSelected(false);
                          }
                          oldPostion=position;
                          oldRelativeLayout=holder.mRlPremiumBadge;
                          PremiumBadge=mPremiumBadgeList.get(position);
//                          onPremiumBadgeClick.onPremiumBadgeClicked(PremiumBadge);
                      } catch (Exception e) {
                          e.printStackTrace();
                      }

                  }
              });

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
        public RelativeLayout mRlPremiumBadge;




        public RecyclerViewHolders(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mBadgeIcon=(ImageView) itemView.findViewById(R.id.iv_premium_badge_icon);
            mBadgeName = (TextView) itemView.findViewById(R.id.tv_premium_badge_name);
            mBadgePrice = (TextView) itemView.findViewById(R.id.tv_premium_badge_price);
            mRlPremiumBadge =(RelativeLayout) itemView.findViewById(R.id.rl_premiumbadge);
        }

        @Override
        public void onClick(View view) {


        }
    }


}
