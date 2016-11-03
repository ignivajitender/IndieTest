package com.igniva.indiecore.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.OnImageDownloadClick;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.ChatPojo;
import com.igniva.indiecore.ui.activities.ViewMediaActivity;
import com.igniva.indiecore.utils.Constants;

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
    private String STATUS = "status";
    private String MESSAGE="Message";
    private String mStatus;
    private boolean Is_downloaded =false;
    private boolean Is_Clicked =false;
    OnImageDownloadClick mOnImageDownload;
    private int NOT_SENT=0;
    private int SENT=1;
    private int DELIVERED=2;
    private int READ=3;
    private int mStatusValue;



    public ChatAdapter(Context context, ArrayList<ChatPojo> chatList, String contextName,String messageID,OnImageDownloadClick onImageDownloadClick, String status,int statusValue) {
        this.context = context;
        this.mChatList = chatList;
        this.mContextName = contextName;
        this.mMessageId=messageID;
        this.mOnImageDownload=onImageDownloadClick;
        this.mStatus = status;
        this.mStatusValue=statusValue;

    }

//    public ChatAdapter(Context context, String status,String messageID,int statusValue) {
//        this.context = context;
//
//        this.mMessageId=messageID;
//    }

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
                    if (!mChatList.get(position).getText().isEmpty()) {
                        holder.mTv_LastMessage_ThisUser.setVisibility(View.VISIBLE);
                        holder.mMediaThis.setVisibility(View.GONE);
                        holder.mLlThis.setBackgroundResource(R.drawable.chat_bubble_rt);
                        holder.mTv_LastMessage_ThisUser.setText(mChatList.get(position).getText());
                    } else {
                        holder.mTv_LastMessage_ThisUser.setVisibility(View.GONE);
                        holder.mMediaThis.setVisibility(View.VISIBLE);
                        if (!mChatList.get(position).getThumb().isEmpty()) {
                            holder.mLlThis.setBackgroundResource(0);
                            holder.mLlThis.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            holder.mMediaThis.setImageBitmap(decodeBase64(mChatList.get(position).getThumb()));
                        } else {
                            holder.mLlThis.setBackgroundResource(R.drawable.chat_bubble_rt);
                        }
                    }
                    time = mChatList.get(position).getDate_updated();
                    if (time != null && time.length() > 0 && time.contains("T")) {
                        holder.mThisUserLastTextTime.setText(time.substring(time.indexOf("T") + 1, time.indexOf(".") - 3));
                    } else {
                        holder.mThisUserLastTextTime.setText(time);
                    }

                    if (mChatList.get(position).getStatus() == READ) {
                        holder.mIvMessageStatus.setImageResource(R.drawable.ic_double_seen_tick);
                    } else if (mChatList.get(position).getStatus() == DELIVERED) {
                        holder.mIvMessageStatus.setImageResource(R.drawable.ic_double_tick);

                    } else if (mChatList.get(position).getStatus() == SENT) {
                        holder.mIvMessageStatus.setImageResource(R.drawable.ic_sent_tick);
                    } else {
                        holder.mIvMessageStatus.setVisibility(View.GONE);
                    }

                }
                else
               {
                    holder.mRlThis.setVisibility(View.GONE);
                    holder.mRlOther.setVisibility(View.VISIBLE);

                    if (mChatList.get(position).getType().equalsIgnoreCase("Photo")) {
                        holder.mIvDownloadOther.setVisibility(View.VISIBLE);
                    }
                    if (mChatList.get(position).getType().equalsIgnoreCase("TEXT")) {
                        holder.mIvDownloadOther.setVisibility(View.GONE);
                    }

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


                    if (!mChatList.get(position).getText().isEmpty() && mChatList.get(position).getType().equalsIgnoreCase(Constants.TEXT)) {
                        holder.mTv_LastMessage_OtherUser.setVisibility(View.VISIBLE);
                        holder.mMediaOther.setVisibility(View.GONE);
                        holder.mLlOther.setBackgroundResource(R.drawable.chat_bubble_lt);
                        holder.mTv_LastMessage_OtherUser.setText(mChatList.get(position).getText());
                    } else {
                        holder.mTv_LastMessage_OtherUser.setVisibility(View.GONE);
                        holder.mMediaOther.setVisibility(View.VISIBLE);

                        if (!mChatList.get(position).getThumb().isEmpty()) {
                            holder.mLlOther.setBackgroundResource(0);
                            holder.mLlOther.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            holder.mMediaOther.setImageBitmap(decodeBase64(mChatList.get(position).getThumb()));
                        } else {
                            holder.mLlOther.setBackgroundResource(R.drawable.chat_bubble_lt);
                        }
                    }

                    time = mChatList.get(position).getDate_updated();
                    if (time != null && time.length() > 0 && time.contains("T")) {
                        holder.mOtherUserLastTextTime.setText(time.substring(time.indexOf("T") + 1, time.indexOf(".") - 3));
                    } else {
                        holder.mOtherUserLastTextTime.setText(time);
                    }

                    if (mChatList.get(position).getImagePath() != null) {
                        if (mChatList.get(position).getImagePath().isEmpty()) {
                            holder.mIvDownloadOther.setVisibility(View.VISIBLE);
                            holder.mMediaOther.setEnabled(true);
                            Is_downloaded = false;
                        } else {
                            holder.mIvDownloadOther.setVisibility(View.GONE);
//                            holder.mMediaOther.setEnabled(false);
                            Is_downloaded = true;
                        }
                    }
//                   if(Is_Clicked=true){
//                       holder.mIvDownloadOther.setVisibility(View.GONE);
//                   }

                }


            holder.mMediaThis.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, ViewMediaActivity.class);
                    intent.putExtra(Constants.MEDIA_PATH,mChatList.get(position).getImagePath());
                    context.startActivity(intent);
                }
            });
            holder.mMediaOther.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mChatList.get(position).getImagePath() != null && !mChatList.get(position).getImagePath().isEmpty()) {
                        Intent intent = new Intent(context, ViewMediaActivity.class);
                        intent.putExtra(Constants.MEDIA_PATH, mChatList.get(position).getImagePath());
                        context.startActivity(intent);
                    } else {
                        Is_Clicked = true;
                        holder.mIvDownloadOther.setVisibility(View.GONE);
                        mOnImageDownload.onDownloadClick(holder.mProgressBar, position, mChatList.get(position).getMedia(), mChatList.get(position).getMessageId(), Is_downloaded);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return this.mChatList.size();
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);

    }

    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTv_LastMessage_OtherUser;
        private TextView mTvOtherUserName;
        private ImageView mIvOtherUserImage;
        private ImageView mMediaOther;
        private ImageView mMediaThis;
        private ImageView mIvDownloadOther;
        private TextView mOtherUserLastTextTime;
        private TextView mTv_LastMessage_ThisUser;
        private TextView mTvThisUserName;
        private ImageView mIvThisUserImage;
        private TextView mThisUserLastTextTime;
        private RelativeLayout mRlThis;
        private RelativeLayout mRlOther;
        private LinearLayout mLlOther;
        private LinearLayout mLlThis;
        private ImageView mIvMessageStatus;
        ProgressBar mProgressBar;

        public RecyclerViewHolders(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTv_LastMessage_OtherUser = (TextView) itemView.findViewById(R.id.tv_otheruser_lasttext);
            mTvOtherUserName = (TextView) itemView.findViewById(R.id.tv_user_name_other);
            mIvOtherUserImage = (ImageView) itemView.findViewById(R.id.iv_otheruser_chat);
            mOtherUserLastTextTime = (TextView) itemView.findViewById(R.id.tv_otheruser_texttime);
            mMediaOther=(ImageView) itemView.findViewById(R.id.iv_media_other);
            mLlOther=(LinearLayout) itemView.findViewById(R.id.ll_other);
            mIvDownloadOther=(ImageView) itemView.findViewById(R.id.iv_download);

            mTv_LastMessage_ThisUser = (TextView) itemView.findViewById(R.id.tv_lasttext_this);
            mTvThisUserName = (TextView) itemView.findViewById(R.id.tv_this_user_name);
            mIvThisUserImage = (ImageView) itemView.findViewById(R.id.iv_this_user_chat);
            mThisUserLastTextTime = (TextView) itemView.findViewById(R.id.tv_lasttext_time);
            mMediaThis=(ImageView) itemView.findViewById(R.id.iv_media_this);
            mLlThis=(LinearLayout) itemView.findViewById(R.id.ll_this);


            mProgressBar = (ProgressBar) itemView.findViewById(R.id.circular_progress_bar);
            mRlThis = (RelativeLayout) itemView.findViewById(R.id.ll_chat_this_user);
            mRlOther = (RelativeLayout) itemView.findViewById(R.id.ll_chat_other);
            mIvMessageStatus=(ImageView) itemView.findViewById(R.id.iv_message_status);
        }

        @Override
        public void onClick(View view) {


        }
    }


}
