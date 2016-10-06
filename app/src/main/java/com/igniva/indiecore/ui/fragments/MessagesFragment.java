package com.igniva.indiecore.ui.fragments;

import android.app.ProgressDialog;
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
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.ChatListPojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.ui.adapters.ChatListAdapter;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.PreferenceHandler;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 4/7/16.
 */
public class MessagesFragment extends BaseFragment implements View.OnClickListener {

    View rootView;
    private RecyclerView mRvChatRoom;
    private String MESSAGE_FRAGMENT="MESSAGE_FRAGMENT";
    private LinearLayoutManager mLlManagerChatRoom;
    private TextView mNoConversations,mFriends,mOthers;
    private ArrayList<ChatListPojo> chatRoomList = new ArrayList<>();
    ArrayList<ChatListPojo> othersList= new ArrayList<>();
    private ChatListAdapter mChatListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_messages, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpLayout();
    }

    @Override
    protected void setUpLayout() {
        try {
            mNoConversations = (TextView) rootView.findViewById(R.id.tv_no_conversations);
            mFriends=(TextView) rootView.findViewById(R.id.tv_friends);
            mFriends.setOnClickListener(this);
            mOthers=(TextView) rootView.findViewById(R.id.tv_others);
            mOthers.setOnClickListener(this);
            mRvChatRoom = (RecyclerView) rootView.findViewById(R.id.rv_chat_rooms_messagestab);
            mLlManagerChatRoom = new LinearLayoutManager(getActivity());
            mRvChatRoom.setLayoutManager(mLlManagerChatRoom);
            setFriendUi();
            setDataInViewObjects();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void setFriendUi(){
        try {
            mRvChatRoom.setVisibility(View.VISIBLE);
            mNoConversations.setVisibility(View.GONE);
            mFriends.setTextColor(Color.parseColor("#FFFFFF"));
            mFriends.setBackgroundColor(Color.parseColor("#1C6DCE"));
            mOthers.setTextColor(Color.parseColor("#1C6DCE"));
            mOthers.setBackgroundResource(R.drawable.simple_border_line_style);
            updateFriendsList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setOthersUi(){
        try {
            mRvChatRoom.setVisibility(View.VISIBLE);
            mNoConversations.setVisibility(View.GONE);
            mNoConversations.setText("Coming soon");
            mFriends.setTextColor(Color.parseColor("#1C6DCE"));
            mFriends.setBackgroundResource(R.drawable.simple_border_line_style);
            mOthers.setTextColor(Color.parseColor("#FFFFFF"));
            mOthers.setBackgroundColor(Color.parseColor("#1C6DCE"));
            updateOthersList();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.tv_friends:
                setFriendUi();
                break;
            case R.id.tv_others:
                setOthersUi();
                break;
            default:
                break;


        }

    }

    @Override
    protected void setDataInViewObjects() {
        getChatRooms();
    }

    public String genratePayload() {
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

    public void getChatRooms() {
        String payload = genratePayload();

        try {
            WebNotificationManager.registerResponseListener(responseHandlerChatRoom);
            WebServiceClient.get_recent_chats(getActivity(), payload, responseHandlerChatRoom);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ResponseHandlerListener responseHandlerChatRoom = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            try {
                WebNotificationManager.unRegisterResponseListener(responseHandlerChatRoom);
                android.util.Log.e("Recent chat Rooms", "+++++++++++++" + result);
                if (error == null) {
                    if (result.getSuccess().equalsIgnoreCase("true") && !result.getTotalChats().equals(0)) {
                        chatRoomList.clear();
                        for(int i=0;i<result.getChatList().size();i++) {
                            if (result.getChatList().get(i).getType() == 0) {
                                if (!result.getChatList().get(i).getRelation().equalsIgnoreCase("favourite")) {
                                    chatRoomList.add(result.getChatList().get(i));
                                } else {
                                    othersList.add(result.getChatList().get(i));
                                }
                            }
                        }
                        mChatListAdapter = null;
                        mChatListAdapter = new ChatListAdapter(getActivity(), chatRoomList,MESSAGE_FRAGMENT);
                        mRvChatRoom.setAdapter(mChatListAdapter);

                    } else {
                        mNoConversations.setVisibility(View.VISIBLE);
                        mRvChatRoom.setVisibility(View.GONE);
                        mNoConversations.setText(getResources().getString(R.string.no_conversations_found));
                    }
                }
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

public void updateOthersList(){
    if(othersList.size()>0) {
        mChatListAdapter = null;
        mChatListAdapter = new ChatListAdapter(getActivity(), othersList, MESSAGE_FRAGMENT);
        mRvChatRoom.setAdapter(mChatListAdapter);
    }else {
        mRvChatRoom.setVisibility(View.GONE);
        mNoConversations.setVisibility(View.VISIBLE);
        mNoConversations.setText("No user is set a favourite");
    }
}

    public void updateFriendsList(){
        if(chatRoomList.size()>0) {
            mChatListAdapter = null;
            mChatListAdapter = new ChatListAdapter(getActivity(), chatRoomList, MESSAGE_FRAGMENT);
            mRvChatRoom.setAdapter(mChatListAdapter);
        }
    }

}
