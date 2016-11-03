package com.igniva.indiecore.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
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
import com.igniva.indiecore.controller.OnContactCardClickListner;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.ChatListPojo;
import com.igniva.indiecore.ui.activities.BoardActivity;
import com.igniva.indiecore.ui.activities.ChatActivity;
import com.igniva.indiecore.ui.activities.DashBoardActivity;
import com.igniva.indiecore.utils.Constants;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.RecyclerViewHolders> {

    String LOG_TAG = "CHATLISTADAPTER";
    private ArrayList<ChatListPojo> mChatRoomList;
    private Context context;
    private String mContextName;


    public ChatListAdapter(Context context, ArrayList<ChatListPojo> chatRoomList, String contextName) {
        this.context = context;
        this.mChatRoomList = chatRoomList;
        this.mContextName = contextName;

    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_items, parent, false);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        try {
            holder.mTvUserName.setText(mChatRoomList.get(position).getName());
            if (mChatRoomList.get(position).getIcon() != null && mChatRoomList.get(position).getIcon().contains("https")) {
                Glide.with(context).load(mChatRoomList.get(position).getIcon())
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.mIvUserImage);
            } else if (mChatRoomList.get(position).getIcon() != null) {
                Glide.with(context).load(WebServiceClient.HTTP_STAGING + mChatRoomList.get(position).getIcon())
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.mIvUserImage);
            } else {
                holder.mIvUserImage.setImageResource(R.drawable.default_user);
            }

            if (mChatRoomList.get(position).getLast_message_type() != null) {
                if (mChatRoomList.get(position).getLast_message_type().equalsIgnoreCase("text")) {
                    holder.mTv_lastMessage.setText(mChatRoomList.get(position).getLast_message());
                } else {
                    holder.mTv_lastMessage.setText("IMAGE");
                }
            }
            if (mChatRoomList.get(position).getMembers() != null) {
                holder.mLlMembers.setVisibility(View.VISIBLE);
                holder.mMemberCount.setText(mChatRoomList.get(position).getMembers());
            }else {
                holder.mLlMembers.setVisibility(View.GONE);

            }
            holder.mChatRoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContextName.equalsIgnoreCase("MESSAGE_FRAGMENT")) {
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra(Constants.ROOM_ID, mChatRoomList.get(position).getRoomId());
                        intent.putExtra(Constants.NAME, mChatRoomList.get(position).getName());
                        ((DashBoardActivity) context).startActivityForResult(intent, Constants.STARTACTIVITYFORRESULTFORCHAT);
                    } else {
                        Intent intent = new Intent(context, BoardActivity.class);
                        intent.putExtra(Constants.BUSINESS_ID, mChatRoomList.get(position).getBusinessId());
                        intent.putExtra(Constants.BUSINESS_NAME, mChatRoomList.get(position).getName());
                        context.startActivity(intent);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return this.mChatRoomList.size();
    }


    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTv_lastMessage;
        private TextView mTvUserName;
        private ImageView mIvUserImage;
        private CardView mChatRoom;
        private TextView mMemberCount;
        private LinearLayout mLlMembers;

        public RecyclerViewHolders(final View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            mTv_lastMessage = (TextView) itemView.findViewById(R.id.tv_lasttext);
            mTvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
            mIvUserImage = (ImageView) itemView.findViewById(R.id.iv_user_chatroom);
            mChatRoom = (CardView) itemView.findViewById(R.id.card_view_chat_rooms);
            mMemberCount = (TextView) itemView.findViewById(R.id.tv_member_count);
            mLlMembers=(LinearLayout) itemView.findViewById(R.id.ll_members);
        }

        @Override
        public void onClick(View view) {


        }
    }


}
