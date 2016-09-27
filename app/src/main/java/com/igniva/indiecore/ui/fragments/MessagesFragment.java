package com.igniva.indiecore.ui.fragments;

import android.app.ProgressDialog;
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
public class MessagesFragment extends BaseFragment {

    View rootView;
    private RecyclerView mRvChatRoom;
    private String MESSAGE_FRAGMENT="MESSAGE_FRAGMENT";
    private LinearLayoutManager mLlManagerChatRoom;
    private TextView mNoConversations;
    private ArrayList<ChatListPojo> chatRoomList = new ArrayList<>();
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
            mRvChatRoom = (RecyclerView) rootView.findViewById(R.id.rv_chat_rooms_messagestab);
            mLlManagerChatRoom = new LinearLayoutManager(getActivity());
            mRvChatRoom.setLayoutManager(mLlManagerChatRoom);
            setDataInViewObjects();
        } catch (Exception e) {
            e.printStackTrace();
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
                        chatRoomList.addAll(result.getChatList());
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
}
