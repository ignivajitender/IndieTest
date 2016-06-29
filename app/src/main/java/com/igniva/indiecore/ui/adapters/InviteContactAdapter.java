package com.igniva.indiecore.ui.adapters;

import android.app.Activity;
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
import android.widget.Toast;

import com.igniva.indiecore.R;
import com.igniva.indiecore.model.BadgesPojo;
import com.igniva.indiecore.model.ContactPojo;
import com.igniva.indiecore.ui.activities.InviteContactActivity;
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.Utility;
import com.igniva.indiecore.utils.imageloader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class InviteContactAdapter extends RecyclerView.Adapter<InviteContactAdapter.RecyclerViewHolders> implements Filterable{

    private ArrayList<ContactPojo> itemList;
    private Context context;
    String LOG_TAG = "InviteContactAdapter";
    private ItemFilter mFilter = new ItemFilter();
    private List<ContactPojo>originalData = null;
    private List<ContactPojo>filteredData = null;


    public InviteContactAdapter(Context context, ArrayList<ContactPojo> itemList) {

        this.itemList = itemList;
        this.context = context;
        this.originalData=itemList;
        filteredData=itemList;

    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {


        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_contact_list_items, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        try {

            holder.mContactName.setText(filteredData.get(position).getContactName());
//

            if (filteredData.get(position).getContactIcon() != null) {

                holder.mContactImage.setImageURI(Uri.EMPTY.parse(filteredData.get(position).getContactIcon()));
            } else {
                holder.mContactImage.setImageResource(R.drawable.default_user);
            }


            if (filteredData.get(position).isSelected() == false) {

                holder.mTicked.setChecked(false);
            } else {

                holder.mTicked.setChecked(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {


                    Utility.showToastMessageShort((Activity) context, "Clicked position is " + position);

                    if (holder.mTicked.isChecked()) {
                        holder.mTicked.setChecked(false);
                        filteredData.get(position).setSelected(false);
                        if (InviteContactActivity.mSelectedContacts.contains(filteredData.get(position).getContactNumber())) {

                            InviteContactActivity.mSelectedContacts.remove((filteredData).get(position).getContactNumber().trim());
                            InviteContactActivity.mSelectedContactName.remove(filteredData.get(position).getContactName());
                            Log.e("selected Contacts after remove", "" + InviteContactActivity.mSelectedContacts + InviteContactActivity.mSelectedContacts.size());
                            Log.e("selected Contacts name after remove", "" + InviteContactActivity.mSelectedContactName);

                        }

                    } else if (InviteContactActivity.mSelectedContacts.size() < 10) {
                        filteredData.get(position).setSelected(true);
                        InviteContactActivity.mSelectedContactName.add(filteredData.get(position).getContactName());
                        InviteContactActivity.mSelectedContacts.add(filteredData.get(position).getContactNumber().trim());
                        Log.e("selected Contacts", "" + InviteContactActivity.mSelectedContacts + InviteContactActivity.mSelectedContacts.size());
                        Log.e("selected Contacts name", "" + InviteContactActivity.mSelectedContactName);

                        holder.mTicked.setChecked(true);

                    } else {

                        Utility.showAlertDialog("You canot send sms to more than 10 peoples at the same time ", context);
                        return;
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return this.filteredData.size();
    }

    @Override
    public Filter getFilter() {



        return mFilter;
    }


    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mContactName;
        public ImageView mContactImage;
        public TextView mContactNumber;
        public CheckBox mTicked;
        public CardView mCardView;

        public RecyclerViewHolders(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mContactName = (TextView) itemView.findViewById(R.id.tv_contact_name);
            mContactImage = (ImageView) itemView.findViewById(R.id.iv_contact_icon);
            mTicked = (CheckBox) itemView.findViewById(R.id.iv_tick);
            mCardView = (CardView) itemView.findViewById(R.id.card_view);


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
            Log.e("", "onClick " + getPosition() + " ");


        }
    }




    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<ContactPojo> list = itemList;

            int count = list.size();
            final ArrayList<ContactPojo> nlist = new ArrayList<ContactPojo>(count);

            String filterableString="" ;

            for (int i = 0; i < count; i++) {
                filterableString = itemList.get(i).getContactName();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(itemList.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<ContactPojo>) results.values;
            notifyDataSetChanged();
        }

    }


}
