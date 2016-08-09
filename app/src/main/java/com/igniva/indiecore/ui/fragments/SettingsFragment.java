package com.igniva.indiecore.ui.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.igniva.indiecore.R;
import com.igniva.indiecore.ui.activities.CreateProfileActivity;
import com.igniva.indiecore.ui.activities.InviteContactActivity;
import com.igniva.indiecore.ui.activities.RecommendBadgeActivity;
import com.igniva.indiecore.ui.activities.SyncContactsActivity;
import com.igniva.indiecore.ui.adapters.ExpandableListAdapter;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by igniva-andriod-11 on 4/7/16.
 */
public class SettingsFragment extends BaseFragment {

    View rootView;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    ImageView mIvcollapse;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        setUpLayout();
        return rootView;
    }

    @Override
    protected void setUpLayout() {
        try {

            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int width = metrics.widthPixels;

            expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);


            // preparing list data
            prepareListData();

            expListView.setIndicatorBounds(width - GetPixelFromDips(50), width - GetPixelFromDips(10));
            listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);

            // setting list adapter
            expListView.setAdapter(listAdapter);

            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    switch (groupPosition) {

                        case 0:
//                            profile
                            try {
                                if (childPosition == 0) {
                                    Intent in = new Intent(getActivity(), CreateProfileActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(Constants.INDEX, 2);
                                    in.putExtras(bundle);
                                    startActivity(in);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case 1:
//                            privacy
                            break;
                        case 2:
//                         about

                            break;
                        case 3:
//                            invite friend
                            try {
                                if (childPosition == 0) {
                                    Intent intent= new Intent(getActivity(),InviteContactActivity.class);
                                    intent.putExtra(Constants.INDEX,4);
                                    getActivity().startActivity(intent);

                                } else if(childPosition==1){
                                    onClickWhatsApp();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            break;
                        case 4:
                            break;
                        case 5:


                            break;
                        case 6:
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });


            expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    Log.d("onGroupClick:", "worked");
                    try {

                        mIvcollapse = (ImageView) v.findViewById(R.id.iv_collapse);
                        mIvcollapse.setImageResource(R.drawable.dropdown_icon);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });


            // Listview Group expanded listener
            expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                @Override
                public void onGroupExpand(int groupPosition) {
                    int m = groupPosition;
                    switch (m) {
                        case 0:
                            // Utility.showToastMessageShort(getActivity(),"Profile Expanded");
                            break;
                        case 1:
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:


                            break;
                        case 5:
                            Intent intent = new Intent(getActivity(),SyncContactsActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt(Constants.INDEX,20);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            break;
                        case 6:
                            Intent in = new Intent(getActivity(), RecommendBadgeActivity.class);
                            getActivity().startActivity(in);
                            break;
                        default:
                            break;
                    }

                    mIvcollapse.setImageResource(R.drawable.next_arrow_icon);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    protected void setDataInViewObjects() {

    }
//
//    @Override
//    protected void onClick(View v) {
//
//    }


    public void onClickWhatsApp() {

        PackageManager pm = getActivity().getPackageManager();
//        try {
//
//            Intent waIntent = new Intent(Intent.ACTION_SEND);
//            waIntent.setType("text/plain");
//            String text =getResources().getString(R.string.invite_message);
//
//            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
//            //Check if package exists or not. If not then code
//            //in catch block will be called
//            waIntent.setPackage("com.whatsapp");
//
//            waIntent.putExtra(Intent.EXTRA_TEXT, text);
//            startActivity(Intent.createChooser(waIntent, "Share with"));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
//                    .show();
//        }

        try {


            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.invite_message));
            try {
                startActivity(whatsappIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Utility.showToastMessageLong(getActivity(),"WhatsApp not Installed");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add(getResources().getString(R.string.profile));
        listDataHeader.add(getResources().getString(R.string.privacy));
        listDataHeader.add(getResources().getString(R.string.about));
        listDataHeader.add(getResources().getString(R.string.invite_friends));
        listDataHeader.add(getResources().getString(R.string.notifiation));
        listDataHeader.add(getResources().getString(R.string.contacts_sync));
        listDataHeader.add(getResources().getString(R.string.reccomend_badge));
        listDataHeader.add(getResources().getString(R.string.clear_chats));


        // Adding child data
        List<String> firstGroup = new ArrayList<String>();
        firstGroup.add(getResources().getString(R.string.edit_profile));
        firstGroup.add(getResources().getString(R.string.cover_pic));
        firstGroup.add(getResources().getString(R.string.name));
        firstGroup.add(getResources().getString(R.string.description));


        List<String> secondGroup = new ArrayList<String>();


        List<String> thirdGroup = new ArrayList<String>();


        List<String> forthGroup = new ArrayList<String>();
        forthGroup.add(getResources().getString(R.string.by_sms));
        forthGroup.add(getResources().getString(R.string.by_whats_app));


        List<String> fifthGroup = new ArrayList<String>();
        List<String> sixthGroup = new ArrayList<String>();
        List<String> seventhGroup = new ArrayList<String>();
        List<String> eightthGroup = new ArrayList<String>();


        listDataChild.put(listDataHeader.get(0), firstGroup); // Header, Child data
        listDataChild.put(listDataHeader.get(1), secondGroup);
        listDataChild.put(listDataHeader.get(2), thirdGroup);
        listDataChild.put(listDataHeader.get(3), forthGroup);
        listDataChild.put(listDataHeader.get(4), fifthGroup);
        listDataChild.put(listDataHeader.get(5), sixthGroup);
        listDataChild.put(listDataHeader.get(6), seventhGroup);
        listDataChild.put(listDataHeader.get(7), eightthGroup);
    }
}
