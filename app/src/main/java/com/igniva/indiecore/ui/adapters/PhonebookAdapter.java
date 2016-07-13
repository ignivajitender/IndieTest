package com.igniva.indiecore.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.ContactPojo;
import com.igniva.indiecore.model.ProfilePojo;
import com.igniva.indiecore.ui.activities.InviteContactActivity;
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class PhonebookAdapter extends RecyclerView.Adapter<PhonebookAdapter.RecyclerViewHolders> {

    private ArrayList<ProfilePojo> itemList;
    private Context context;
    String LOG_TAG = "PhonebookAdapter";


    public PhonebookAdapter(Context context, ArrayList<ProfilePojo> itemList) {

        this.itemList = itemList;
        this.context = context;

    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {


        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_items, parent,false);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        try {
            holder.mContactName.setText(itemList.get(position).getFirstName());
if(itemList.get(position).getProfilePic()!=null) {
    Glide.with(context).load(WebServiceClient.HTTP_STAGING + itemList.get(position).getProfilePic())
            .thumbnail(1f)
            .crossFade()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.mContactImage);
}else {

    holder.mContactImage.setImageResource(R.drawable.default_user);
}
            holder.mText.setText(itemList.get(position).getDesc());
        }catch (Exception e){
            e.printStackTrace();
        }

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                try {
//
//
////                    Utility.showToastMessageShort((Activity) context, "Clicked position is " + position);
//
//                    if (holder.mTicked.isChecked()) {
//                        holder.mTicked.setChecked(false);
//                        filteredData.get(position).setSelected(false);
//                        if (InviteContactActivity.mSelectedContacts.contains(filteredData.get(position).getContactNumber())) {
//
//                            InviteContactActivity.mSelectedContacts.remove((filteredData).get(position).getContactNumber().trim());
//                            InviteContactActivity.mSelectedContactName.remove(filteredData.get(position).getContactName());
//                            Log.e("selected Contacts after remove", "" + InviteContactActivity.mSelectedContacts + InviteContactActivity.mSelectedContacts.size());
//                            Log.e("selected Contacts name after remove", "" + InviteContactActivity.mSelectedContactName);
//
//                        }
//
//                    } else if (InviteContactActivity.mSelectedContacts.size() < 10) {
//                        filteredData.get(position).setSelected(true);
//                        InviteContactActivity.mSelectedContactName.add(filteredData.get(position).getContactName());
//                        InviteContactActivity.mSelectedContacts.add(filteredData.get(position).getContactNumber().trim());
//                        Log.e("selected Contacts", "" + InviteContactActivity.mSelectedContacts + InviteContactActivity.mSelectedContacts.size());
//                        Log.e("selected Contacts name", "" + InviteContactActivity.mSelectedContactName);
//
//                        holder.mTicked.setChecked(true);
//
//                    } else {
//
//                        Utility.showAlertDialog("You canot send sms to more than 10 peoples at the same time ", context);
//                        return;
//                    }
//
//
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return this.itemList.size();
    }



    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mContactName;
        public ImageView mContactImage;
        public TextView mText;
        public CardView mCardView;

        public RecyclerViewHolders(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mContactName = (TextView) itemView.findViewById(R.id.tv_phonebook_name);
            mContactImage = (ImageView) itemView.findViewById(R.id.iv_contact_image);
            mText=(TextView) itemView.findViewById(R.id.tv_text);
            mCardView = (CardView) itemView.findViewById(R.id.cv_phonebook);


        }

        @Override
        public void onClick(View view) {
            Log.e("", "onClick " + getPosition() + " ");


        }
    }


}
