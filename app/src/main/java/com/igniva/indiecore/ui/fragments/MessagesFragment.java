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
import com.igniva.indiecore.db.BadgesDb;
import com.igniva.indiecore.model.ChatListPojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.ui.adapters.ChatListAdapter;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.PreferenceHandler;
import com.igniva.indiecore.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by igniva-andriod-11 on 4/7/16.
 */
public class MessagesFragment extends BaseFragment implements View.OnClickListener {

    public static boolean isMessageFragmenVisible;
    View rootView;
    boolean isFriendTabVisible = true;
    private RecyclerView mRvChatRoom;
    private String MESSAGE_FRAGMENT = "MESSAGE_FRAGMENT";
    private LinearLayoutManager mLlManagerChatRoom;
    private TextView mNoConversations, mFriends, mOthers;
    private ArrayList<ChatListPojo> chatRoomListAll = new ArrayList<>();
    private ArrayList<ChatListPojo> chatRoomList = new ArrayList<>();
    private ArrayList<ChatListPojo> othersList = new ArrayList<>();
    private ChatListAdapter mChatListAdapter;
    private boolean firstTime;
    private BadgesDb badgesDb;
    ResponseHandlerListener responseHandlerChatRoom = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            try {
                WebNotificationManager.unRegisterResponseListener(responseHandlerChatRoom);
                android.util.Log.e("Recent chat Rooms", "+++++++++++++" + result);
                if (error == null) {
                    if (result.getSuccess().equalsIgnoreCase("true") && !result.getTotalChats().equals(0)) {

                        getRecentChatList(result.getChatList(), true); //true means data coming from server

                       /* for (int i = 0; i < result.getChatList().size(); i++) {
                            if (result.getChatList().get(i).getType() == 0) {
                                *//*if (!result.getChatList().get(i).getRelation().equalsIgnoreCase("favourite")) {
                                    chatRoomList.add(result.getChatList().get(i));
                                } else {
                                    othersList.add(result.getChatList().get(i));
                                }*//*
                                getRecentChatList(result.getChatList());
                                badgesDb.insertRecenChatList(result.getChatList().get(i));
                            }
                        }
                        chatRoomListAll.clear();
                        chatRoomListAll.addAll(chatRoomList);
                        mChatListAdapter.notifyDataSetChanged();*/


                       /* mChatListAdapter = null;
                        mChatListAdapter = new ChatListAdapter(getActivity(), chatRoomList, MESSAGE_FRAGMENT);
                        mRvChatRoom.setAdapter(mChatListAdapter);*/

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_messages, container, false);
        isMessageFragmenVisible = true;
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
            mFriends = (TextView) rootView.findViewById(R.id.tv_friends);
            mFriends.setOnClickListener(this);
            mOthers = (TextView) rootView.findViewById(R.id.tv_others);
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

    public void setFriendUi() {
        try {
            mRvChatRoom.setVisibility(View.VISIBLE);
            mNoConversations.setVisibility(View.GONE);
            mFriends.setTextColor(Color.parseColor("#FFFFFF"));
            mFriends.setBackgroundColor(Color.parseColor("#1C6DCE"));
            mOthers.setTextColor(Color.parseColor("#1C6DCE"));
            mOthers.setBackgroundResource(R.drawable.simple_border_line_style);
            isFriendTabVisible = true;
            if (firstTime) {
                updateFriendsList();
            }
            firstTime = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOthersUi() {
        try {
            mRvChatRoom.setVisibility(View.VISIBLE);
            mNoConversations.setVisibility(View.GONE);
            mNoConversations.setText("Coming soon");
            mFriends.setTextColor(Color.parseColor("#1C6DCE"));
            mFriends.setBackgroundResource(R.drawable.simple_border_line_style);
            mOthers.setTextColor(Color.parseColor("#FFFFFF"));
            mOthers.setBackgroundColor(Color.parseColor("#1C6DCE"));
            isFriendTabVisible = false;
            updateOthersList();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

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
        badgesDb = new BadgesDb(getActivity());
        mChatListAdapter = new ChatListAdapter(getActivity(), chatRoomListAll, MESSAGE_FRAGMENT);
        mRvChatRoom.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvChatRoom.setAdapter(mChatListAdapter);
        if (Utility.isInternetConnection(getActivity())) {
            getChatRooms();
        } else {
            // get chatlist or other list from db
            ArrayList<ChatListPojo> recentChatList1 = badgesDb.getAllRecenChatMsg();
            Collections.reverse(recentChatList1);
            getRecentChatList(recentChatList1, false);
        }
    }

    // Rohit , isFromLocalDb variable
    public void getRecentChatList(ArrayList<ChatListPojo> recentChatList, boolean isFromServer) {
        chatRoomList.clear();
        othersList.clear();
        if (isFromServer) {
            badgesDb.deleteAllDataOfRecentChat();
        }
        for (int i = 0; i < recentChatList.size(); i++) {
            if (recentChatList.get(i).getType() == 0) {
                if (!recentChatList.get(i).getRelation().equalsIgnoreCase("favourite")) {
                    chatRoomList.add(recentChatList.get(i));
                } else {
                    othersList.add(recentChatList.get(i));
                }
                if (isFromServer) {
                    badgesDb.insertRecenChatList(recentChatList.get(i));
                }
            }
        }
        chatRoomListAll.clear();
        if (isFriendTabVisible) {
            chatRoomListAll.addAll(chatRoomList);
        } else {
            chatRoomListAll.addAll(othersList);
        }
        mChatListAdapter.notifyDataSetChanged();
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

    public void updateOthersList() {
        if (othersList.size() > 0) {
            chatRoomListAll.clear();
            chatRoomListAll.addAll(othersList);
            mChatListAdapter.notifyDataSetChanged();
           /* mChatListAdapter = null;
            mChatListAdapter = new ChatListAdapter(getActivity(), othersList, MESSAGE_FRAGMENT);
            mRvChatRoom.setAdapter(mChatListAdapter);*/
        } else {
            mRvChatRoom.setVisibility(View.GONE);
            mNoConversations.setVisibility(View.VISIBLE);
            mNoConversations.setText("No user is set a favourite");
        }
    }

    public void updateFriendsList() {
        if (chatRoomList.size() > 0) {
            chatRoomListAll.clear();
            chatRoomListAll.addAll(chatRoomList);
            mChatListAdapter.notifyDataSetChanged();
           /* mChatListAdapter = null;
            mChatListAdapter = new ChatListAdapter(getActivity(), chatRoomList, MESSAGE_FRAGMENT);
            mRvChatRoom.setAdapter(mChatListAdapter);*/
        } else {
            mRvChatRoom.setVisibility(View.GONE);
            mNoConversations.setVisibility(View.VISIBLE);
            mNoConversations.setText("No user is set a favourite");
        }
    }

    /*public void addRecentMsg(HashMap<String, InstantChatPojo> recentChatHashMap) {
        Log.e("NewMsg", "come" + recentChatHashMap.size());
        if (isFriendTabVisible) {
            //Friend tab
            for (int i = 0; i < chatRoomList.size(); i++) {
                for (Map.Entry<String, InstantChatPojo> entry : recentChatHashMap.entrySet()) {
                    //System.out.println(entry.getKey() + "/" + entry.getValue());
                    if (chatRoomList.get(i).getRoomId().equalsIgnoreCase(entry.getValue().getRoomId())) {
                        ChatListPojo chatListPojo2 = chatRoomList.get(i);
                        if (entry.getValue().getType().equalsIgnoreCase("Text")) {
                            chatListPojo2.setLast_message(entry.getValue().getText());
                        } else if (entry.getValue().getType().equalsIgnoreCase("video")) {
                            chatListPojo2.setLast_message("VIDEO");
                        } else {
                            chatListPojo2.setLast_message("IMAGE");
                        }

                        chatListPojo2.setLast_message_Id(entry.getValue().getMessageId());
                        chatListPojo2.setDate_updated(entry.getValue().getDate_updated().get$date());
                        chatRoomListAll.set(i, chatListPojo2);
                        if (mChatListAdapter != null) {
                            mChatListAdapter.notifyDataSetChanged();
                        }
                        break;
                    }
                }
            }
        } else {
            //Other tab
            for (int i = 0; i < othersList.size(); i++) {
                for (Map.Entry<String, InstantChatPojo> entry : recentChatHashMap.entrySet()) {
                    //System.out.println(entry.getKey() + "/" + entry.getValue());
                    if (othersList.get(i).getRoomId().equalsIgnoreCase(entry.getValue().getRoomId())) {
                        ChatListPojo chatListPojo2 = othersList.get(i);
                        if (entry.getValue().getType().equalsIgnoreCase("Text")) {
                            chatListPojo2.setLast_message(entry.getValue().getText());
                        } else if (entry.getValue().getType().equalsIgnoreCase("video")) {
                            chatListPojo2.setLast_message("VIDEO");
                        } else {
                            chatListPojo2.setLast_message("IMAGE");
                        }
                        chatListPojo2.setLast_message_Id(entry.getValue().getMessageId());
                        chatListPojo2.setDate_updated(entry.getValue().getDate_updated().get$date());
                        chatRoomListAll.set(i, chatListPojo2);
                        if (mChatListAdapter != null) {
                            mChatListAdapter.notifyDataSetChanged();
                        }
                        break;
                    }
                }

            }
        }

    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isMessageFragmenVisible = false;
    }

}
