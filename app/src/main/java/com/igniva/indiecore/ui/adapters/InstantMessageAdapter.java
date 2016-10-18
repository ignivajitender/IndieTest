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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.OnContactCardClickListner;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.UsersPojo;
import com.igniva.indiecore.ui.activities.ChatActivity;
import com.igniva.indiecore.ui.activities.DashBoardActivity;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.Log;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class InstantMessageAdapter extends RecyclerView.Adapter<InstantMessageAdapter.RecyclerViewHolders> {

    String LOG_TAG = "PhonebookAdapter";
    OnContactCardClickListner onContactCardClickListner;
    private ArrayList<UsersPojo> indieCoreUsersLIst;
    private Context context;


    public InstantMessageAdapter(Context context) {
        this.context = context;

    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.instant_message_layout, parent, false);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        try {
//            holder.mContactName.setText(indieCoreUsersLIst.get(position).getProfile().getFirstName());
//            if (indieCoreUsersLIst.get(position).getProfile().getProfilePic() != null) {
//                Glide.with(context).load(WebServiceClient.HTTP_STAGING + indieCoreUsersLIst.get(position).getProfile().getProfilePic())
//                        .thumbnail(1f)
//                        .crossFade()
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .into(holder.mContactImage);
//            } else {
//
//                holder.mContactImage.setImageResource(R.drawable.default_user);
//            }
//            holder.mText.setText(indieCoreUsersLIst.get(position).getProfile().getDesc());
//
//            Log.e("Adapter", "-----" + indieCoreUsersLIst.get(position).getMobileNo());
        } catch (Exception e) {
            e.printStackTrace();
        }

//
//        holder.mContactImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mOnsetFavouriteListner.onContactCardClicked(holder.mContactImage, position, indieCoreUsersLIst.get(position).getUserId());
//            }
//        });


    }


    @Override
    public int getItemCount() {
        return this.indieCoreUsersLIst.size();
    }


    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LinearLayout mLlOtherUser;
        private LinearLayout mLlThisUser;
        private TextView mTvOtherUserText;
        private TextView mTvThisUserText;
        private ImageView mIvOtherUser;
        private ImageView mOtherUser;

        public RecyclerViewHolders(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mLlOtherUser = (LinearLayout) itemView.findViewById(R.id.ll_other_user);
            mLlThisUser = (LinearLayout) itemView.findViewById(R.id.ll_this_user);

            mTvOtherUserText = (TextView) itemView.findViewById(R.id.tv_other_user);
            mTvThisUserText = (TextView) itemView.findViewById(R.id.tv_this_user);

            mIvOtherUser = (ImageView) itemView.findViewById(R.id.iv_other_user);
            mOtherUser = (ImageView) itemView.findViewById(R.id.iv_this_user);


        }

        @Override
        public void onClick(View view) {


        }
    }


}
