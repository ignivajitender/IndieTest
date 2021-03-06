package com.igniva.indiecore.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.OnContactCardClickListner;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.UsersPojo;
import com.igniva.indiecore.ui.activities.ChatActivity;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.Log;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class PhonebookAdapter extends RecyclerView.Adapter<PhonebookAdapter.RecyclerViewHolders> {

    private ArrayList<UsersPojo> indieCoreUsersLIst;
    private Context context;
    private String mTAb = "";
    private String FAVOURITE = "favourite";
    private String LOG_TAG = "PhonebookAdapter";
    OnContactCardClickListner onContactCardClickListner;


    public PhonebookAdapter(Context context, ArrayList<UsersPojo> indieCoreUsersLIst, OnContactCardClickListner onContactCardClickListner, String TAB) {

        this.indieCoreUsersLIst = indieCoreUsersLIst;
        this.context = context;
        this.onContactCardClickListner = onContactCardClickListner;
        this.mTAb = TAB;

    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {


        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_items, parent, false);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        try {
            if (mTAb.equalsIgnoreCase(FAVOURITE)) {
                holder.mBadgeIconConatiner.setVisibility(View.VISIBLE);
                if (indieCoreUsersLIst.get(position).getBadges().equalsIgnoreCase("off")) {
                    holder.mBadgeIconConatiner.setBackgroundResource(R.drawable.badge_off);
                } else {
                    holder.mBadgeIconConatiner.setBackgroundResource(R.drawable.blank_badge);
                    holder.mMutualBadgeCount.setText(indieCoreUsersLIst.get(position).getBadges());
                }

                holder.mStarContainer.setVisibility(View.VISIBLE);
            } else {
                holder.mBadgeIconConatiner.setVisibility(View.GONE);
                holder.mStarContainer.setVisibility(View.GONE);
            }
            holder.mContactName.setText(indieCoreUsersLIst.get(position).getProfile().getFirstName());
            if (indieCoreUsersLIst.get(position).getProfile().getProfilePic() != null) {
                Glide.with(context).load(WebServiceClient.HTTP_STAGING + indieCoreUsersLIst.get(position).getProfile().getProfilePic())
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.mContactImage);
            } else {

                holder.mContactImage.setImageResource(R.drawable.default_user);
            }
            holder.mText.setText(indieCoreUsersLIst.get(position).getProfile().getDesc());

            Log.e("Adapter", "-----" + indieCoreUsersLIst.get(position).getMobileNo());
        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.mContactImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onContactCardClickListner.onContactCardClicked(holder.mContactImage, position, indieCoreUsersLIst.get(position).getUserId());
            }
        });

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra(Constants.PERSON_ID, indieCoreUsersLIst.get(position).getUserId());
                    if (indieCoreUsersLIst.get(position).getProfile().getLastName() != null) {
                        intent.putExtra(Constants.NAME, indieCoreUsersLIst.get(position).getProfile().getFirstName() + " " + indieCoreUsersLIst.get(position).getProfile().getLastName());
                    } else {
                        intent.putExtra(Constants.NAME, indieCoreUsersLIst.get(position).getProfile().getFirstName());
                    }
                    intent.putExtra(Constants.INDEX, 44);
                    context.startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.indieCoreUsersLIst.size();
    }


    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mContactName;
        public TextView mText;
        public TextView mMutualBadgeCount;
        public LinearLayout mStarContainer;
        public RelativeLayout mBadgeIconConatiner;
        public ImageView mContactImage;
        public CardView mCardView;

        public RecyclerViewHolders(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mContactName = (TextView) itemView.findViewById(R.id.tv_phonebook_name);
            mContactImage = (ImageView) itemView.findViewById(R.id.iv_contact_image);
            mText = (TextView) itemView.findViewById(R.id.tv_text);
            mCardView = (CardView) itemView.findViewById(R.id.cv_phonebook);
            mStarContainer = (LinearLayout) itemView.findViewById(R.id.ll_star_container);
            mBadgeIconConatiner = (RelativeLayout) itemView.findViewById(R.id.rl_badges_fav_tab);
            mMutualBadgeCount = (TextView) itemView.findViewById(R.id.tv_mut_badge_count_fav_tab);

        }

        @Override
        public void onClick(View view) {


        }
    }


}
