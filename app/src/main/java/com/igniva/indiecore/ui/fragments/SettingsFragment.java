package com.igniva.indiecore.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.igniva.indiecore.R;
import com.igniva.indiecore.ui.adapters.ExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by igniva-andriod-11 on 4/7/16.
 */
public class SettingsFragment extends BaseFragment{

    View rootView;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        setUpLayout();
        return rootView;
    }

    @Override
    protected void setUpLayout() {

        expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getActivity(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });


        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
             switch (groupPosition){
//
//                 case Integer.parseInt(getResources().getString(R.string.reccomend_badge));
//                     break;
//                 default:
//                     break;
             }
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getActivity(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void setDataInViewObjects() {

    }

    @Override
    protected void onClick(View v) {

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
        listDataChild.put(listDataHeader.get(7),eightthGroup);
    }
}
