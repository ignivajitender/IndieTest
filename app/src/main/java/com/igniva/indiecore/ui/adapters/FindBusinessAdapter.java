package com.igniva.indiecore.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.OnCardClickListner;
import com.igniva.indiecore.model.BusinessPojo;
import com.igniva.indiecore.model.ContactPojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class FindBusinessAdapter extends RecyclerView.Adapter<FindBusinessAdapter.RecyclerViewHolders> implements Filterable {

    private Context mContext;
    String LOG_TAG = "BusinessListAdapter";
    private ArrayList<BusinessPojo> mBusinessList;
    private final OnCardClickListner listener;
    private ItemFilter mFilter = new ItemFilter();
    private  List<BusinessPojo> orignalList=null;
    private  List<BusinessPojo> filteredList=null;

    public FindBusinessAdapter(Context context, ArrayList<BusinessPojo> mBusinessList, OnCardClickListner onCardClickListner) {

        this.mContext = context;
        this.mBusinessList = mBusinessList;
        this.listener = onCardClickListner;
        filteredList=mBusinessList;
    }


    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_find_busineses, parent, false);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        // final ImageLoader imageLoader = new ImageLoader(mContext);
        String address = "";
        try {
            holder.mTVBusinessName.setText(filteredList.get(position).getName());

            String distance = String.valueOf(filteredList.get(position).getDistance()).substring(0, 4);
            holder.mTVDistance.setText(distance + " miles away");
            holder.mUserCount.setText(filteredList.get(position).getUser_count());
            Glide.with(mContext).load(filteredList.get(position).getImage_url())
                    .thumbnail(1f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.mIvBusinessIcon);

            int size = filteredList.get(position).getLocation().getAddress().size();
            for (int i = 0; i < size; i++) {
                address = address + "," + filteredList.get(position).getLocation().getAddress().get(i);
                address = address.replaceFirst("^,",
                        "");
            }
            holder.mTvAddress.setText(address);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (filteredList.get(position).getBadge_status() == 1) {

            holder.mIvOnOffBadgeStatus.setImageResource(R.drawable.badge_on);
        } else {
            holder.mIvOnOffBadgeStatus.setImageResource(R.drawable.badge_off);
        }


        holder.mIvOnOffBadgeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCardClicked(holder.mIvOnOffBadgeStatus, position);
            }
        });


    }


    @Override
    public int getItemCount() {
        return this.filteredList.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTVBusinessName;
        public ImageView mIvBusinessIcon;
        public ImageView mIvOnOffBadgeStatus;
        public TextView mTvAddress;
        public TextView mTVDistance;
        public TextView mUserCount;

        public RecyclerViewHolders(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTVBusinessName = (TextView) itemView.findViewById(R.id.tv_find_business_name);
            mIvBusinessIcon = (ImageView) itemView.findViewById(R.id.iv_find_business_icon);
            mIvOnOffBadgeStatus = (ImageView) itemView.findViewById(R.id.iv_badge_on_off_find_business);
            mTvAddress = (TextView) itemView.findViewById(R.id.tv_find_business_address);
            mTVDistance = (TextView) itemView.findViewById(R.id.tv_findbusiness_distance);
            mUserCount = (TextView) itemView.findViewById(R.id.tv_find_user_count);

        }

        @Override
        public void onClick(View view) {


        }
    }


    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<BusinessPojo> list = mBusinessList;

            int count = list.size();
            final ArrayList<BusinessPojo> nlist = new ArrayList<BusinessPojo>(count);

            String filterableString = "";

            for (int i = 0; i < count; i++) {
                filterableString = mBusinessList.get(i).getName();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(mBusinessList.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList<BusinessPojo>) results.values;
            notifyDataSetChanged();
        }


    }
}
