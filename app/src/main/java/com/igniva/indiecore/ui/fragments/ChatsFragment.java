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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.OnCommentClickListner;
import com.igniva.indiecore.controller.OnDeletePostClickListner;
import com.igniva.indiecore.controller.OnDisLikeClickListner;
import com.igniva.indiecore.controller.OnLikeClickListner;
import com.igniva.indiecore.controller.OnMediaPostClickListner;
import com.igniva.indiecore.controller.OnNeutralClickListner;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.ChatListPojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.model.PostPojo;
import com.igniva.indiecore.ui.activities.ChatActivity;
import com.igniva.indiecore.ui.activities.CommentActivity;
import com.igniva.indiecore.ui.activities.CreatePostActivity;
import com.igniva.indiecore.ui.activities.DashBoardActivity;
import com.igniva.indiecore.ui.adapters.ChatListAdapter;
import com.igniva.indiecore.ui.adapters.WallPostAdapter;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 4/7/16.
 */
public class ChatsFragment extends BaseFragment {
    View rootView;
    public static int POSITION = -1;
    private TextView mComingSoon;
    private RecyclerView mRvChatRoom;
    private  String LOG_TAG = "LOG_TAG";
    private ArrayList<ChatListPojo> chatRoomList= new ArrayList<>();
    private ChatListAdapter mChatListAdapter;
    private LinearLayoutManager mLlManagerChatRoom ;
    private String CHAT_FRAGMENT="CHAT_FRAGMENT";



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_chats, container, false);
        setUpLayout();
        return rootView;
    }

    @Override
    protected void setUpLayout() {
        mComingSoon=(TextView) rootView.findViewById(R.id.tv_cuming_soon);
        mRvChatRoom=(RecyclerView) rootView.findViewById(R.id.rv_chat_rooms);
        mLlManagerChatRoom = new LinearLayoutManager(getActivity());;
        mRvChatRoom.setLayoutManager(mLlManagerChatRoom);
        setDataInViewObjects();

    }

    @Override
    protected void setDataInViewObjects() {
        getChatRooms();

    }

    @Override
    public void onResume() {
        super.onResume();

    }


    public String genratePayload(){
        JSONObject payload =null;
        try {
            payload=new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_ID, ""));

        }catch (Exception e){
            e.printStackTrace();
        }
        return payload.toString();

    }

    public void getChatRooms(){
        String payload=genratePayload();

        try {
            WebNotificationManager.registerResponseListener(responseHandlerChatRoom);
            WebServiceClient.get_recent_chats(getActivity(),payload,responseHandlerChatRoom);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    ResponseHandlerListener responseHandlerChatRoom= new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            try{
                WebNotificationManager.unRegisterResponseListener(responseHandlerChatRoom);
                android.util.Log.e("Recent chat Rooms","+++++++++++++"+result);
                if(error==null){
                    if(result.getSuccess().equalsIgnoreCase("true") && !result.getTotalChats().equals(0)){
                       chatRoomList.clear();
                        for(int i=0;i<result.getChatList().size();i++) {
                            if(result.getChatList().get(i).getType()==1) {
                                chatRoomList.add(result.getChatList().get(i));
                            }
                        }
                        mChatListAdapter=null;
                        mChatListAdapter=new ChatListAdapter(getActivity(),chatRoomList,CHAT_FRAGMENT);
                        mRvChatRoom.setAdapter(mChatListAdapter);
                    }else {
                        mComingSoon.setVisibility(View.VISIBLE);
                        mRvChatRoom.setVisibility(View.GONE);
                        mComingSoon.setText(getResources().getString(R.string.no_conversations_found));
                    }
                }
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };




}
