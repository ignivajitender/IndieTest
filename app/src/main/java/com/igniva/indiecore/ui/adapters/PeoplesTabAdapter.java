package com.igniva.indiecore.ui.adapters;

import android.content.Context;
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
import com.igniva.indiecore.controller.OnContactCardClickListner;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.PeoplesPojo;
import com.igniva.indiecore.utils.PreferenceHandler;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class PeoplesTabAdapter extends RecyclerView.Adapter<PeoplesTabAdapter.RecyclerViewHolders> {

    private ArrayList<PeoplesPojo> mPeoplesList;
    private Context context;
    private boolean deletePostVisible = false;
    public ImageView mDeletePostOld;
    String LOG_TAG = "PeoplesTabAdapter";
    OnContactCardClickListner onContactCardClickListner;


    public PeoplesTabAdapter(Context context, ArrayList<PeoplesPojo> businessPeoples, OnContactCardClickListner onContactCardClickListner) {

        this.mPeoplesList = businessPeoples;
        this.context = context;
        this.onContactCardClickListner = onContactCardClickListner;

    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.peoples_tab_items, parent, false);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        try {
            holder.mUserName.setText(mPeoplesList.get(position).getProfile().getFirstName() + " " + (mPeoplesList.get(position).getProfile().getLastName()).charAt(0) + ".");
            if (mPeoplesList.get(position).getProfile().getProfilePic() != null) {
                Glide.with(context).load(WebServiceClient.HTTP_STAGING + mPeoplesList.get(position).getProfile().getProfilePic())
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.mUserImage);
            } else {
                holder.mUserImage.setImageResource(R.drawable.default_user);
            }
            holder.mUserDescText.setText(mPeoplesList.get(position).getProfile().getDesc());

            if (mPeoplesList.get(position).getBadges().equalsIgnoreCase("off")) {
                holder.mRlMutualbadgesCount.setBackgroundResource(R.drawable.badge_off);
            } else {
                holder.mRlMutualbadgesCount.setBackgroundResource(R.drawable.blank_badge);
                holder.mTvBadgeCount.setText(mPeoplesList.get(position).getBadges());
            }

            if (mPeoplesList.get(position).getRelation().equalsIgnoreCase("favourite")) {
                holder.mStar.setImageResource(R.drawable.rating_star);
            } else {
                holder.mStar.setImageResource(R.drawable.grey_star);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.mStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onContactCardClickListner.onContactCardClicked(holder.mStar, position, mPeoplesList.get(position).getUserId() );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        holder.mDownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (deletePostVisible == false) {
                        // Remove previously opened view
                        try {
                            mDeletePostOld.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        holder.mDownArrow.setImageResource(R.drawable.dropdown_icon);
                        holder.mIvReportAbuse.setVisibility(View.VISIBLE);
                        deletePostVisible = true;
                        mDeletePostOld = holder.mIvReportAbuse;
                    } else if (deletePostVisible == true) {
                        holder.mIvReportAbuse.setVisibility(View.GONE);
                        holder.mDownArrow.setImageResource(R.drawable.next_arrow_icon);

                        deletePostVisible = false;

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

//        holder.mCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
////                    context.startActivity(new Intent(context, ChatActivity.class));
////                    Intent intent=new Intent(context,ChatActivity.class);
////                    intent.putExtra(Constants.PERSON_ID,indieCoreUsersLIst.get(position).getUserId());
////                    intent.putExtra(Constants.NAME,indieCoreUsersLIst.get(position).getProfile().getFirstName()+" "+indieCoreUsersLIst.get(position).getProfile().getLastName());
////                    intent.putExtra(Constants.INDEX,44);
////                    context.startActivity(intent);
////                    DashBoardActivity.bottomNavigation.setCurrentItem(3);
//
//
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        });

    }


    @Override
    public int getItemCount() {
        return this.mPeoplesList.size();
    }


    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mUserName;
        public TextView mTvBadgeCount;
        public TextView mUserDescText;
        public ImageView mUserImage;
        public ImageView mStar;
        public ImageView mIvReportAbuse;
        public ImageView mDownArrow;
        public CardView mCardView;
        public RelativeLayout mRlMutualbadgesCount;

        public RecyclerViewHolders(final View itemView) {
            super(itemView);
//            itemView.setOnClickListener(this);
            mRlMutualbadgesCount = (RelativeLayout) itemView.findViewById(R.id.rl_mutual_badge_bg_peoples_tab);
            mTvBadgeCount = (TextView) itemView.findViewById(R.id.tv_mutual_badge_count_peoples_tab);
            mUserName = (TextView) itemView.findViewById(R.id.tv_name_peoples_tab);
            mUserImage = (ImageView) itemView.findViewById(R.id.iv_user_peoples_tab);
            mIvReportAbuse=(ImageView) itemView.findViewById(R.id.iv_report_abuse);
            mUserDescText = (TextView) itemView.findViewById(R.id.tv_desc_peoples_tab);
            mCardView = (CardView) itemView.findViewById(R.id.cv_phonebook);
            mStar = (ImageView) itemView.findViewById(R.id.iv_star);
            mDownArrow = (ImageView) itemView.findViewById(R.id.iv_right_arrow);

        }

        @Override
        public void onClick(View view) {

        }
    }


}
