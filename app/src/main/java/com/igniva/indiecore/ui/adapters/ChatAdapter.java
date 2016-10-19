package com.igniva.indiecore.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.ChatPojo;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-11 on 10/6/16.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.RecyclerViewHolders> {

    String LOG_TAG = "CHAT_LIST_ADAPTER";
    private ArrayList<ChatPojo> mChatList;
    private Context context;
    private String mContextName;
    private String mMessageId;


    public ChatAdapter(Context context, ArrayList<ChatPojo> chatList, String contextName,String messageID) {
        this.context = context;
        this.mChatList = chatList;
        this.mContextName = contextName;
        this.mMessageId=messageID;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_items_layout, parent, false);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        try {
            String time;
                if (mChatList.get(position).getRelation().equalsIgnoreCase("self")) {
                    holder.mRlThis.setVisibility(View.VISIBLE);
                    holder.mRlOther.setVisibility(View.GONE);
                    holder.mTvThisUserName.setVisibility(View.GONE);
                    if (mChatList.get(position).getIcon() != null && !mChatList.get(position).getIcon().isEmpty()) {
                        Glide.with(context).load(WebServiceClient.HTTP_STAGING + mChatList.get(position).getIcon())
                                .thumbnail(1f)
                                .crossFade()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(holder.mIvThisUserImage);
                    } else {
                        holder.mIvThisUserImage.setImageResource(R.drawable.default_user);
                    }
                    holder.mTv_LastMessage_ThisUser.setText(mChatList.get(position).getText());
                    time = mChatList.get(position).getDate_updated();
                    if (time != null && time.length() > 0 && time.contains("T")) {
                        holder.mThisUserLastTextTime.setText(time.substring(time.indexOf("T") + 1, time.indexOf(".") - 3));
                    } else {
                        holder.mThisUserLastTextTime.setText(time);
                    }

                    if(mChatList.get(position).getStatus()==2){
                        holder.mIvMessageStatus.setImageResource(R.drawable.ic_double_seen_tick);
                    }else if(mChatList.get(position).getStatus()==1){
                        holder.mIvMessageStatus.setImageResource(R.drawable.ic_double_tick);

                    }else {
                        holder.mIvMessageStatus.setImageResource(R.drawable.ic_sent_tick);

                    }

//                    if(!mMessageId.isEmpty()&&mChatList.get(position).getMessageId().equalsIgnoreCase(mMessageId)){
//                        holder.mIvMessageStatus.setImageResource(R.drawable.ic_double_tick);
//                    }
                } else {
                    holder.mRlThis.setVisibility(View.GONE);
                    holder.mRlOther.setVisibility(View.VISIBLE);

                    holder.mTvOtherUserName.setText(mChatList.get(position).getName());
                    if (mChatList.get(position).getIcon() != null && !mChatList.get(position).getIcon().isEmpty()) {
                        Glide.with(context).load(WebServiceClient.HTTP_STAGING + mChatList.get(position).getIcon())
                                .thumbnail(1f)
                                .crossFade()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(holder.mIvOtherUserImage);
                    } else {
                        holder.mIvOtherUserImage.setImageResource(R.drawable.default_user);
                    }
                    holder.mTv_LastMessage_OtherUser.setText(mChatList.get(position).getText());
                    time = mChatList.get(position).getDate_updated();
                    if (time != null && time.length() > 0 && time.contains("T")) {
                        holder.mOtherUserLastTextTime.setText(time.substring(time.indexOf("T") + 1, time.indexOf(".") - 3));
                    } else {
                        holder.mOtherUserLastTextTime.setText(time);
                    }
                }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return this.mChatList.size();
    }


    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTv_LastMessage_OtherUser;
        private TextView mTvOtherUserName;
        private ImageView mIvOtherUserImage;
        private TextView mOtherUserLastTextTime;
        private TextView mTv_LastMessage_ThisUser;
        private TextView mTvThisUserName;
        private ImageView mIvThisUserImage;
        private TextView mThisUserLastTextTime;
        private RelativeLayout mRlThis;
        private RelativeLayout mRlOther;
        private ImageView mIvMessageStatus;

        public RecyclerViewHolders(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTv_LastMessage_OtherUser = (TextView) itemView.findViewById(R.id.tv_otheruser_lasttext);
            mTvOtherUserName = (TextView) itemView.findViewById(R.id.tv_user_name_other);
            mIvOtherUserImage = (ImageView) itemView.findViewById(R.id.iv_otheruser_chat);
            mOtherUserLastTextTime = (TextView) itemView.findViewById(R.id.tv_otheruser_texttime);

            mTv_LastMessage_ThisUser = (TextView) itemView.findViewById(R.id.tv_lasttext_this);
            mTvThisUserName = (TextView) itemView.findViewById(R.id.tv_this_user_name);
            mIvThisUserImage = (ImageView) itemView.findViewById(R.id.iv_this_user_chat);
            mThisUserLastTextTime = (TextView) itemView.findViewById(R.id.tv_lasttext_time);

            mRlThis = (RelativeLayout) itemView.findViewById(R.id.ll_chat_this_user);
            mRlOther = (RelativeLayout) itemView.findViewById(R.id.ll_chat_other);
            mIvMessageStatus=(ImageView) itemView.findViewById(R.id.iv_message_status);
        }

        @Override
        public void onClick(View view) {


        }
    }


}
