package com.igniva.indiecore.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.ProfilePojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.model.UsersWallPostPojo;
import com.igniva.indiecore.ui.activities.CreatePostActivity;
import com.igniva.indiecore.ui.activities.DashBoardActivity;
import com.igniva.indiecore.ui.adapters.FindBusinessAdapter;
import com.igniva.indiecore.ui.adapters.WallPostAdapter;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.PreferenceHandler;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 4/7/16.
 */
public class ChatsFragment extends BaseFragment {
    View rootView;
    private TextView mChat, mBoard, mPeople, mCreatePost, mUserName;
    ImageView mUserImage;
    public DashBoardActivity mDashBoard;
    private WallPostAdapter mAdapter;
    private ArrayList<UsersWallPostPojo> mWallPostList;
    private LinearLayoutManager mLlManager;
    private WallPostAdapter mWallPostAdapter;
    private RecyclerView mRvWallPosts;
    public final static String BUSINESS = "business";
    String PAGE = "1";
    String LIMIT = "10";
    String mBusinessId = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_chats, container, false);

//        mDashBoard=(DashBoardActivity) getActivity();
//        mDashBoard.bottomNavigation.setCurrentItem(2);
        setUpLayout();
        return rootView;
    }

    @Override
    protected void setUpLayout() {

        mWallPostList= new ArrayList<UsersWallPostPojo>();
        try {

            mBusinessId = DashBoardActivity.businessId;

        } catch (Exception e) {
            e.printStackTrace();
        }

        mRvWallPosts = (RecyclerView) rootView.findViewById(R.id.rv_users_posts);
        mRvWallPosts.setLayoutManager(mLlManager);
        mChat = (TextView) rootView.findViewById(R.id.tv_chat);
        mChat.setOnClickListener(onclickListner);
        mBoard = (TextView) rootView.findViewById(R.id.tv_board);
        mBoard.setOnClickListener(onclickListner);
        mPeople = (TextView) rootView.findViewById(R.id.tv_people);
        mPeople.setOnClickListener(onclickListner);

        mCreatePost = (TextView) rootView.findViewById(R.id.tv_create_post);
        mCreatePost.setOnClickListener(onclickListner);

        mUserName = (TextView) rootView.findViewById(R.id.tv_user_name_chat_fragment);
        mUserImage = (ImageView) rootView.findViewById(R.id.iv_user_img_chat_fragment);


//        viewAllPost();
        setDataInViewObjects();

    }

    @Override
    protected void setDataInViewObjects() {


        try {
            if (!PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_FIRST_NAME, "").isEmpty()) {
                String Name = (PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_FIRST_NAME, "") + " " + (PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_LAST_NAME, "")).charAt(0) + ".");
                mUserName.setText(Name);
            }
            if (!PreferenceHandler.readString(getActivity(), PreferenceHandler.PROFILE_PIC_URL, "").isEmpty()) {
                Glide.with(this).load(WebServiceClient.HTTP_STAGING + PreferenceHandler.readString(getActivity(), PreferenceHandler.PROFILE_PIC_URL, ""))
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mUserImage);
            }

            updateBoardUi();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        viewAllPost();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    protected void onClick(View v) {

    }


    public View.OnClickListener onclickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_chat:

                    updateChatUi();
                    break;
                case R.id.tv_board:
                    updateBoardUi();
                    break;
                case R.id.tv_people:
                    updatePeopleUi();
                    break;
                case R.id.tv_create_post:
                    Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                    intent.putExtra(Constants.BUSINESS_ID, mBusinessId);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };


    public void updateChatUi() {

        try {

            mChat.setTextColor(Color.parseColor("#FFFFFF"));
            mChat.setBackgroundColor(Color.parseColor("#1C6DCE"));

            mBoard.setTextColor(Color.parseColor("#1C6DCE"));
            mBoard.setBackgroundResource(R.drawable.simple_border_line_style);

            mPeople.setTextColor(Color.parseColor("#1C6DCE"));
            mPeople.setBackgroundResource(R.drawable.simple_border_line_style);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateBoardUi() {

        try {
            mLlManager = new LinearLayoutManager(getActivity());
            mRvWallPosts.setLayoutManager(mLlManager);
            mChat.setTextColor(Color.parseColor("#1C6DCE"));
            mChat.setBackgroundResource(R.drawable.simple_border_line_style);

            mBoard.setTextColor(Color.parseColor("#FFFFFF"));
            mBoard.setBackgroundColor(Color.parseColor("#1C6DCE"));

            mPeople.setTextColor(Color.parseColor("#1C6DCE"));
            mPeople.setBackgroundResource(R.drawable.simple_border_line_style);

            mAdapter = null;
            mAdapter = new WallPostAdapter(getActivity(), mWallPostList);
            mRvWallPosts.setAdapter(mAdapter);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePeopleUi() {

        try {
            mChat.setTextColor(Color.parseColor("#1C6DCE"));
            mChat.setBackgroundResource(R.drawable.simple_border_line_style);

            mBoard.setTextColor(Color.parseColor("#1C6DCE"));
            mBoard.setBackgroundResource(R.drawable.simple_border_line_style);

            mPeople.setTextColor(Color.parseColor("#FFFFFF"));
            mPeople.setBackgroundColor(Color.parseColor("#1C6DCE"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String createPayload() {
        JSONObject payload = null;
//    token, userId, roomId, postType, page, limit
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.ROOM_ID, mBusinessId);
            payload.put(Constants.POST_TYPE, BUSINESS);
            payload.put(Constants.PAGE, PAGE);
            payload.put(Constants.LIMIT, LIMIT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return payload.toString();
    }

    public void viewAllPost() {
        String payload = createPayload();
        if (!payload.isEmpty()) {

            WebNotificationManager.registerResponseListener(responseHandler);
            WebServiceClient.view_all_posts(getActivity(), payload, responseHandler);
        }

    }


    ResponseHandlerListener responseHandler = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(responseHandler);
            try {

                if (error == null) {

                    if (result.getSuccess().equalsIgnoreCase("true")) {
                        mWallPostList.clear();
                        mWallPostList.addAll(result.getPostList());

                        if(mWallPostList.size()>0) {
                            try {
                                mWallPostAdapter = null;
                                mWallPostAdapter = new WallPostAdapter(getActivity(), mWallPostList);
                                mWallPostAdapter.notifyDataSetChanged();
                                mRvWallPosts.setAdapter(mWallPostAdapter);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    } else {


                    }
                } else {

                }


                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    public String createPayload(String postId) {

        JSONObject payload = null;
//        token, userId, type(like/dislike/neutral), post_id
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.TYPE, "");
            payload.put(Constants.POST_ID, "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return payload.toString();
    }



    /*
    * like/unlike/neutral action to a post
    * @parms post_id
    * */
    public void likeUnlikePost() {
//cretepayload with parametrs i sto be acll here
        String payload = createPayload();

        if (!payload.isEmpty()) {

            WebNotificationManager.registerResponseListener(responseHandl);
            WebServiceClient.like_unlike_post(getActivity(), payload, responseHandl);
        }
    }

    ResponseHandlerListener responseHandl = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(responseHandl);

        }
    };
}
