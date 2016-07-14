package com.igniva.indiecore.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.igniva.indiecore.R;
import com.igniva.indiecore.db.BadgesDb;
import com.igniva.indiecore.model.BadgesPojo;
import com.igniva.indiecore.model.ProfilePojo;
import com.igniva.indiecore.ui.adapters.PhonebookAdapter;
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.Utility;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 4/7/16.
 */
public class ContactsFragment extends BaseFragment {

    private ArrayList<ProfilePojo> mSavedUsersList = null;
    private RecyclerView mRvUsers;
    private LinearLayoutManager mLlManager;
    private TextView mTvPhoneBook, mTvFavourite,mComingSoon;
    PhonebookAdapter mPhoneBookAdter;
    BadgesDb usersDb;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_contacts, container, false);

        setUpLayout();

        return rootView;

    }

    @Override
    protected void setUpLayout() {
        try {
        mRvUsers = (RecyclerView) rootView.findViewById(R.id.rv_users);
        mSavedUsersList = new ArrayList<ProfilePojo>();
        mTvPhoneBook = (TextView) rootView.findViewById(R.id.tv_phonebook);
        mTvPhoneBook.setOnClickListener(onClickListener);
        mTvFavourite = (TextView) rootView.findViewById(R.id.tv_favourite);
        mTvFavourite.setOnClickListener(onClickListener);

        mComingSoon=(TextView) rootView.findViewById(R.id.tv_coming_soon);

        setDataInViewObjects();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void setDataInViewObjects() {
        getSavedUsers();
        updatePhoneBookUi();

    }

    @Override
    protected void onClick(View v) {

    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_phonebook:
                    updatePhoneBookUi();
                    break;
                case R.id.tv_favourite:
                    updateFavouriteUi();
                    break;

                default:
                    break;

            }

        }
    };

    public void getSavedUsers() {
        try {
            mLlManager = new LinearLayoutManager(getActivity());
            mRvUsers.setLayoutManager(mLlManager);
            usersDb = new BadgesDb(getActivity());
            mSavedUsersList = usersDb.retrieveSavedUsersList();

            if(mSavedUsersList.size()>0) {
                mPhoneBookAdter = null;
                mPhoneBookAdter = new PhonebookAdapter(getActivity(), mSavedUsersList);
                mRvUsers.setAdapter(mPhoneBookAdter);
                mComingSoon.setVisibility(View.GONE);
                rootView.findViewById(R.id.tv_coming_soon_two).setVisibility(View.GONE);

            }else {
                mRvUsers.setVisibility(View.GONE);
                mComingSoon.setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.tv_coming_soon_two).setVisibility(View.VISIBLE);
                mComingSoon.setText(R.string.no_contacts_one);
                ((TextView)rootView.findViewById(R.id.tv_coming_soon_two)).setText(R.string.no_contacts_two);

            }
        } catch (Exception e) {
            e.printStackTrace();
            mRvUsers.setVisibility(View.GONE);
            mComingSoon.setVisibility(View.VISIBLE);
            mComingSoon.setText("No Contacts on Indiecore.Try Syncing contact with indiecore");
        }
    }

    public void updatePhoneBookUi() {
try {
    mTvPhoneBook.setTextColor(Color.parseColor("#FFFFFF"));
    mTvPhoneBook.setBackgroundColor(Color.parseColor("#1C6DCE"));
    mTvFavourite.setTextColor(Color.parseColor("#1C6DCE"));
    mTvFavourite.setBackgroundResource(R.drawable.simple_border_line_style);

    if (mSavedUsersList.size() > 0) {
        mRvUsers.setVisibility(View.VISIBLE);
        mComingSoon.setVisibility(View.GONE);
    } else {
        mRvUsers.setVisibility(View.GONE);
        mComingSoon.setVisibility(View.VISIBLE);

        rootView.findViewById(R.id.tv_coming_soon_two).setVisibility(View.VISIBLE);
        mComingSoon.setText(R.string.no_contacts_one);
        ((TextView)rootView.findViewById(R.id.tv_coming_soon_two)).setText(R.string.no_contacts_two);
    }
}catch (Exception e){
    e.printStackTrace();
}

    }

    public void updateFavouriteUi() {
        try {

            mTvPhoneBook.setTextColor(Color.parseColor("#1C6DCE"));
            mTvPhoneBook.setBackgroundResource(R.drawable.simple_border_line_style);
            mTvFavourite.setTextColor(Color.parseColor("#FFFFFF"));
            mTvFavourite.setBackgroundColor(Color.parseColor("#1C6DCE"));
            mRvUsers.setVisibility(View.GONE);
            mComingSoon.setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.tv_coming_soon_two).setVisibility(View.GONE);
            mComingSoon.setText(R.string.coming_soon);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
