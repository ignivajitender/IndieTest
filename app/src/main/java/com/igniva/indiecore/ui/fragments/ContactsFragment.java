package com.igniva.indiecore.ui.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.OnContactCardClickListner;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.db.BadgesDb;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.model.UsersPojo;
import com.igniva.indiecore.ui.activities.UserProfileActivity;
import com.igniva.indiecore.ui.adapters.PhonebookAdapter;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 4/7/16.
 */
public class ContactsFragment extends BaseFragment {

    private ArrayList<UsersPojo> mSavedUsersList = new ArrayList<UsersPojo>();
    private ArrayList<UsersPojo> mFavouriteList= new ArrayList<UsersPojo>();
    private LinearLayout mLlNoContactText;
    private RecyclerView mRvUsers;
    private LinearLayoutManager mLlManager;
    private TextView mTvPhoneBook, mTvFavourite, mComingSoon;
    private PhonebookAdapter mPhoneBookAdapter;
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
            mTvPhoneBook = (TextView) rootView.findViewById(R.id.tv_phonebook);
            mTvPhoneBook.setOnClickListener(onClickListener);
            mTvFavourite = (TextView) rootView.findViewById(R.id.tv_favourite);
            mTvFavourite.setOnClickListener(onClickListener);
            mComingSoon = (TextView) rootView.findViewById(R.id.tv_coming_soon);
            mLlNoContactText =(LinearLayout) rootView.findViewById(R.id.ll_no_contact_txt);
            setDataInViewObjects();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setDataInViewObjects() {
        getSavedUsers();
        updatePhoneBookUi();

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

            if (mSavedUsersList.size() > 0) {
                mPhoneBookAdapter = null;
                mPhoneBookAdapter = new PhonebookAdapter(getActivity(), mSavedUsersList, onContactCardClickListner);
                mRvUsers.setAdapter(mPhoneBookAdapter);
                mLlNoContactText.setVisibility(View.GONE);
            } else {
                mRvUsers.setVisibility(View.GONE);
                mLlNoContactText.setVisibility(View.VISIBLE);
                mComingSoon.setText(R.string.no_contacts_one);
                ((TextView) rootView.findViewById(R.id.tv_coming_soon_two)).setText(R.string.no_contacts_two);

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
                mPhoneBookAdapter = null;
                mPhoneBookAdapter = new PhonebookAdapter(getActivity(), mSavedUsersList, onContactCardClickListner);
                mRvUsers.setAdapter(mPhoneBookAdapter);
                mLlNoContactText.setVisibility(View.GONE);
                } else {
                mRvUsers.setVisibility(View.GONE);
                mLlNoContactText.setVisibility(View.VISIBLE);
                mComingSoon.setText(R.string.no_contacts_one);
                ((TextView) rootView.findViewById(R.id.tv_coming_soon_two)).setText(R.string.no_contacts_two);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateFavouriteUi() {
        try {

            mLlManager = new LinearLayoutManager(getActivity());
            mRvUsers.setLayoutManager(mLlManager);
            mTvPhoneBook.setTextColor(Color.parseColor("#1C6DCE"));
            mTvPhoneBook.setBackgroundResource(R.drawable.simple_border_line_style);
            mTvFavourite.setTextColor(Color.parseColor("#FFFFFF"));
            mTvFavourite.setBackgroundColor(Color.parseColor("#1C6DCE"));

            if(mFavouriteList.size()>0){
                mRvUsers.setVisibility(View.VISIBLE);
               mLlNoContactText.setVisibility(View.GONE);
                mPhoneBookAdapter = null;
                mPhoneBookAdapter = new PhonebookAdapter(getActivity(), mFavouriteList, onContactCardClickListner);
                mRvUsers.setAdapter(mPhoneBookAdapter);

            }else {
                fetchFavourite();

            }




//
//            mComingSoon.setVisibility(View.VISIBLE);
//            rootView.findViewById(R.id.tv_coming_soon_two).setVisibility(View.GONE);
//            mComingSoon.setText(R.string.coming_soon);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    OnContactCardClickListner onContactCardClickListner = new OnContactCardClickListner() {
        @Override
        public void onContactCardClicked(ImageView UserImage, int position, final String userId) {

            UserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                        bundle.putString(Constants.USERID, userId);
                        bundle.putString(Constants.BUSINESS_ID, "");
                        bundle.putInt(Constants.INDEX, 12);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    };

    /*
    *    create payload to fetch favourite list
    *    token,userid
    *
    * */
    public String generatePayload() {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_ID, ""));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return payload.toString();
    }

    public void fetchFavourite() {


        String payload = generatePayload();
        if (payload != null) {

            WebNotificationManager.registerResponseListener(FavouriteUsersResponseHandler);
            WebServiceClient.get_favourite_list(getActivity(), payload, FavouriteUsersResponseHandler);

        }
    }

    ResponseHandlerListener FavouriteUsersResponseHandler = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(FavouriteUsersResponseHandler);

            try {
                if (error == null) {

                    if (result.getSuccess().equalsIgnoreCase("true")) {
                        mFavouriteList.clear();
                        mFavouriteList.addAll(result.getUsers());

                        if(mFavouriteList.size()>0) {
                            mRvUsers.setVisibility(View.VISIBLE);
                            mLlNoContactText.setVisibility(View.GONE);
                            mPhoneBookAdapter = null;
                            mPhoneBookAdapter = new PhonebookAdapter(getActivity(), mFavouriteList, onContactCardClickListner);
                            mRvUsers.setAdapter(mPhoneBookAdapter);
                        }else {
                            Utility.showToastMessageShort(getActivity(),"No user set a favourite");
                            mLlNoContactText.setVisibility(View.VISIBLE);
                            mRvUsers.setVisibility(View.GONE);
                            mComingSoon.setText(R.string.no_favourite_one);
                            ((TextView) rootView.findViewById(R.id.tv_coming_soon_two)).setText(R.string.no_favourite_two);
                        }

                    }
                }
//                always dismiss this progress dialoge
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    };

}
