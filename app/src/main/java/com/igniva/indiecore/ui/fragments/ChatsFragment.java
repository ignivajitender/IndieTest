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
//        try {
//            mBusinessId = DashBoardActivity.businessId;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        mRvWallPosts = (RecyclerView) rootView.findViewById(R.id.rv_users_posts);
//
//        mChat = (TextView) rootView.findViewById(R.id.tv_chat);
//        mChat.setOnClickListener(onclickListner);
//        mBoard = (TextView) rootView.findViewById(R.id.tv_board);
//        mBoard.setOnClickListener(onclickListner);
//        mPeople = (TextView) rootView.findViewById(R.id.tv_people);
//        mPeople.setOnClickListener(onclickListner);
//
//        mCreatePost = (TextView) rootView.findViewById(R.id.tv_create_post);
//        mCreatePost.setOnClickListener(onclickListner);
//
//        mUserName = (TextView) rootView.findViewById(R.id.tv_user_name_chat_fragment);
//        mUserImage = (ImageView) rootView.findViewById(R.id.iv_user_img_chat_fragment);
//
//        mLlBoard =(LinearLayout) rootView.findViewById(R.id.ll_board);


//        mRvWallPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                try {
//                    //check for scroll down
//                    if (dy > 0)
//                    {
//                        visibleItemCount = mLlManager.getChildCount();
//                        totalItemCount = mLlManager.getItemCount();
//                        pastVisibleItems = mLlManager.findFirstVisibleItemPosition();
//
//
//
//                        if (!isLoading) {
//
//                            Log.d(LOG_TAG, "lis size is "+mWallPostList.size()+ " ======++++++ visibleItemCount " + visibleItemCount + " pastVisibleItems " + pastVisibleItems + " totalItemCount " + totalItemCount);
//                            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
//                                isLoading = true;
//                                //Do pagination.. i.e. fetch new data
//                                if (mWallPostList.size() < 1) {
//                                   PAGE=1;
//                                   viewAllPost();
//                                }
//                                if (mWallPostList.size() < totalPostCount) {
//                                    PAGE+=1;
//                                    viewAllPost();
//                                }
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
////                    }
//            }
//        });
        setDataInViewObjects();

    }

    @Override
    protected void setDataInViewObjects() {
        getChatRooms();

//        try {
//            if (!PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_FIRST_NAME, "").isEmpty()) {
//                String Name = (PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_FIRST_NAME, "") + " " + (PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_LAST_NAME, "")).charAt(0) + ".");
//                mUserName.setText(Name);
//            }
//            if (!PreferenceHandler.readString(getActivity(), PreferenceHandler.PROFILE_PIC_URL, "").isEmpty()) {
//                Glide.with(this).load(WebServiceClient.HTTP_STAGING + PreferenceHandler.readString(getActivity(), PreferenceHandler.PROFILE_PIC_URL, ""))
//                        .thumbnail(1f)
//                        .crossFade()
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .into(mUserImage);
//            }

//            updateChatUi();
//            updateBoardUi();

//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public void onResume() {
        super.onResume();
//        TODO
//        if(mWallPostList.size()==0) {
//            viewAllPost();
//        }else {
//            mWallPostAdapter = null;
//            mWallPostAdapter = new WallPostAdapter(getActivity(), mWallPostList, onCommentListItemClickListnerTest2,Constants.CHATFRAGMENT);
//            mRvWallPosts.setAdapter(mWallPostAdapter);
//        }
    }


//    public View.OnClickListener onclickListner = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.tv_chat:
//                    updateChatUi();
//                    break;
//                case R.id.tv_board:
//                    updateBoardUi();
//                    break;
//                case R.id.tv_people:
//                    updatePeopleUi();
//                    break;
//                case R.id.tv_create_post:
//                    Intent intent = new Intent(getActivity(), CreatePostActivity.class);
//                    intent.putExtra(Constants.BUSINESS_ID, mBusinessId);
//                    startActivity(intent);
//                    break;
//
//                default:
//                    break;
//            }
//        }
//    };


//    OnLikeClickListner onLikeClickListner =new OnLikeClickListner() {
//        @Override
//        public void onLikeClicked(TextView like, int position, String postId, String type) {
//            try {
//                Utility.showToastMessageShort(getActivity(),"Like Clicked");
//                POSITION = position;
//                action = 1;
//                likeUnlikePost(mActionTypeLike, mWallPostList.get(position).getPostId());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };
//
//    OnDisLikeClickListner onDisLikeClickListner =new OnDisLikeClickListner() {
//        @Override
//        public void onDisLikeClicked(TextView dislike, int position, String postId, String type) {
//            try {
//                POSITION = position;
//                action = 2;
//                likeUnlikePost(mActionTypeDislike, postId);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };
//
//    OnNeutralClickListner onNeutralClickListner =new OnNeutralClickListner() {
//        @Override
//        public void onNeutralClicked(TextView neutral, int position, String postId, String type) {
//            try {
//                POSITION = position;
//                action = 3;
//                likeUnlikePost(mActionTypeNeutral, postId);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };
//
//
//    OnCommentClickListner onCommentClickListner= new OnCommentClickListner() {
//        @Override
//        public void onCommentClicked(TextView comment, int position, String postId, String type) {
//            try {
//                POSITION = position;
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("POST", mWallPostList.get(position));
//                Intent intent = new Intent(getActivity(), CommentActivity.class);
//                intent.putExtras(bundle);
//                startActivity(intent);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };
//
//    OnDeletePostClickListner onDeleteClickListner =new OnDeletePostClickListner() {
//        @Override
//        public void ondeletePostClicked(ImageView delete, int position, String postId, String ACTION) {
//            try {
//                POSITION = position;
//                mIvDelete=delete;
//                if (ACTION.equalsIgnoreCase("DELETE")) {
//
//                    removePost(postId);
//
//                } else if (ACTION.equalsIgnoreCase("REPORT")) {
//
//                    flagPost(postId);
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };
//
//    OnMediaPostClickListner onMediaPostClickListner=new OnMediaPostClickListner() {
//        @Override
//        public void onMediaPostClicked(ImageView media, int position, String postId, String type) {
//            try {
//                POSITION = position;
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("POST", mWallPostList.get(position));
//                Intent intent = new Intent(getActivity(), CommentActivity.class);
//                intent.putExtras(bundle);
//                startActivity(intent);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//    };


//    public void updateChatUi() {
//
//        try {
//            mLlManagerChatRoom = new LinearLayoutManager(getActivity());;
//            mRvChatRoom.setLayoutManager(mLlManagerChatRoom);
//            mLlBoard.setVisibility(View.GONE);
//            mRvChatRoom.setVisibility(View.VISIBLE);
//            mChat.setTextColor(Color.parseColor("#FFFFFF"));
//            mChat.setBackgroundColor(Color.parseColor("#1C6DCE"));
//
//            mBoard.setTextColor(Color.parseColor("#1C6DCE"));
//            mBoard.setBackgroundResource(R.drawable.simple_border_line_style);
//
//            mPeople.setTextColor(Color.parseColor("#1C6DCE"));
//            mPeople.setBackgroundResource(R.drawable.simple_border_line_style);
//            mComingSoon.setVisibility(View.GONE);
//            if(chatRoomList.size()==0) {
//                getChatRooms();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void updateBoardUi() {
//
//        try {
//            mLlBoard.setVisibility(View.VISIBLE);
//            mRvChatRoom.setVisibility(View.GONE);
//            mComingSoon.setVisibility(View.GONE);
//            mLlManager = new LinearLayoutManager(getActivity());
//            mRvWallPosts.setLayoutManager(mLlManager);
//            viewAllPost();
//            mChat.setTextColor(Color.parseColor("#1C6DCE"));
//            mChat.setBackgroundResource(R.drawable.simple_border_line_style);
//
//            mBoard.setTextColor(Color.parseColor("#FFFFFF"));
//            mBoard.setBackgroundColor(Color.parseColor("#1C6DCE"));
//
//            mPeople.setTextColor(Color.parseColor("#1C6DCE"));
//            mPeople.setBackgroundResource(R.drawable.simple_border_line_style);
//            mAdapter = null;
//                mAdapter = new WallPostAdapter(getActivity(), mWallPostList,Constants.CHATFRAGMENT,onLikeClickListner,onDisLikeClickListner,onNeutralClickListner,onCommentClickListner,onMediaPostClickListner,onDeleteClickListner);
//                mRvWallPosts.setAdapter(mAdapter);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void updatePeopleUi() {
//
//        try {
//            mLlBoard.setVisibility(View.GONE);
//            mRvChatRoom.setVisibility(View.GONE);
//            mComingSoon.setVisibility(View.VISIBLE);
//            mComingSoon.setText(getResources().getString(R.string.coming_soon));
//            mChat.setTextColor(Color.parseColor("#1C6DCE"));
//            mChat.setBackgroundResource(R.drawable.simple_border_line_style);
//
//            mBoard.setTextColor(Color.parseColor("#1C6DCE"));
//            mBoard.setBackgroundResource(R.drawable.simple_border_line_style);
//
//            mPeople.setTextColor(Color.parseColor("#FFFFFF"));
//            mPeople.setBackgroundColor(Color.parseColor("#1C6DCE"));
//
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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

//    /*
//    *
//    * create payload to get all post of a business
//    * @Params:token, userId, roomId, postType, page, limit
//    * */
//    public String createPayload() {
//        JSONObject payload = null;
//        try {
//            payload = new JSONObject();
//            payload.put(Constants.TOKEN, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
//            payload.put(Constants.USERID, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_ID, ""));
//            payload.put(Constants.ROOM_ID, mBusinessId);
//            payload.put(Constants.POST_TYPE, BUSINESS);
//            payload.put(Constants.PAGE, PAGE+"");
//            payload.put(Constants.LIMIT, LIMIT+"");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return payload.toString();
//    }
//    /*
//    *
//    * to get all the post of this business wall
//    *
//    * */
//    public void viewAllPost() {
//        try {
//            String payload = createPayload();
//            if (!payload.isEmpty()) {
//                WebNotificationManager.registerResponseListener(responseHandler);
//                WebServiceClient.view_all_posts(getActivity(), payload, responseHandler);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    /*
//    *
//    * posts response and list inflation
//    *
//    * */
//    ResponseHandlerListener responseHandler = new ResponseHandlerListener() {
//        @Override
//        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
//            WebNotificationManager.unRegisterResponseListener(responseHandler);
//            try {
//
//                if (error == null) {
//                    if (result.getSuccess().equalsIgnoreCase("true")) {
//                        totalPostCount=result.getTotalPosts();
//                        if (mWallPostList != null)
//                            mWallPostList.clear();
//                            mWallPostList.addAll(result.getPostList());
//
//                        if (mWallPostList.size() > 0) {
//                            try {
//                                mWallPostAdapter = null;
//                                mWallPostAdapter = new WallPostAdapter(getActivity(), mWallPostList,Constants.CHATFRAGMENT,onLikeClickListner,onDisLikeClickListner,onNeutralClickListner,onCommentClickListner,onMediaPostClickListner,onDeleteClickListner);
//                                mWallPostAdapter.notifyDataSetChanged();
//                                mRvWallPosts.setAdapter(mWallPostAdapter);
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }else {
//                            mComingSoon.setVisibility(View.VISIBLE);
//                            mComingSoon.setText(getResources().getString(R.string.no_post_found));
//                        }
//                        isLoading = false;
//                    }
//                }
//
//                if (mProgressDialog != null && mProgressDialog.isShowing()) {
//                    mProgressDialog.dismiss();
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };
//
//
//    //    /*
////
//// create payload to like unlike a post
////token, userId, type(like/dislike/neutral), post_id
////
//// */
//    public String createPayload(String type, String postId) {
//
//        JSONObject payload = null;
//        try {
//            payload = new JSONObject();
//            payload.put(Constants.TOKEN, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
//            payload.put(Constants.USERID, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_ID, ""));
//            payload.put(Constants.TYPE, type);
//            payload.put(Constants.POST_ID, postId);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return payload.toString();
//    }
//
//
//    /*
//    * like/unlike/neutral action to a post
//    * @parms post_id
//    *
//    * */
//    public void likeUnlikePost(String type, String postId) {
//        //create payload with parameters is to be call here
//
//        String payload = createPayload(type, postId);
//
//        if (!payload.isEmpty()) {
//            WebNotificationManager.registerResponseListener(responseHandlerLike);
//            WebServiceClient.like_unlike_post(getActivity(), payload, responseHandlerLike);
//        }
//    }
//
//    /*
//    * like unlike response
//    * */
//    ResponseHandlerListener responseHandlerLike = new ResponseHandlerListener() {
//        @Override
//        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
//            WebNotificationManager.unRegisterResponseListener(responseHandlerLike);
//            try {
//
//                if (error == null) {
//
//                    if (result.getSuccess().equalsIgnoreCase("true")) {
//
//                        if (action == 1) {
//                            //action like
//                            int num_like = mWallPostList.get(POSITION).getLike();
//                            int a =num_like;
//                            if (result.getLike() == 1) {
//                                mWallPostList.get(POSITION).setAction(Constants.LIKE);
//                                mWallPostList.get(POSITION).setLike(a + 1);
//                            } else if(result.getLike()==0) {
//                                mWallPostList.get(POSITION).setAction(null);
//                                if (a > 0) {
//                                    mWallPostList.get(POSITION).setLike(a - 1 );
//                                }
//                            }
////                            Log.d(LOG_TAG, " new count of like " + mHolder.like.getText());
//
//
//                        } else if (action == 2) {
//                            //action dislike
//                            int num_dislike = mWallPostList.get(POSITION).getDislike();
//                            int b = num_dislike;
//
//                            if (result.getDislike() == 1) {
//
//
//                                mWallPostList.get(POSITION).setAction(Constants.DISLIKE);
//                                mWallPostList.get(POSITION).setDislike(b + 1);
//
//                            } else if(result.getDislike()==0){
//                                mWallPostList.get(POSITION).setAction(null);
//                                if (b > 0) {
//                                    mWallPostList.get(POSITION).setDislike(b - 1 );
//                                }
//
//                            }
//
//
//                        } else if (action == 3) {
//                            //    action neutral
//
//                            int num_neutral = mWallPostList.get(POSITION).getNeutral();
//                            int c = num_neutral;
//
//                            if (result.getNeutral() == 1) {
//                                mWallPostList.get(POSITION).setAction(Constants.NEUTRAL);
//                                mWallPostList.get(POSITION).setNeutral(c + 1);
//                            } else if(result.getNeutral()==0) {
//                                mWallPostList.get(POSITION).setAction(null);
//                                if (c > 0) {
//                                    mWallPostList.get(POSITION).setNeutral(c - 1 );
//                                }
//                            }
//                        }
//                        mWallPostAdapter.notifyDataSetChanged();
//                    }
//
//
//                    // Refresh adapter
//                    mWallPostAdapter.notifyDataSetChanged();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
////            finish the dialog
//            if (mProgressDialog != null && mProgressDialog.isShowing()) {
//                mProgressDialog.dismiss();
//            }
//
//        }
//    };
//
//
//
//    /*
//    * create payload to flag/remove a post
//    *
//    *
//    * */
//
//
//    public String genratePayload(String postId) {
//        JSONObject payload = null;
//        try {
//            payload = new JSONObject();
//            payload.put(Constants.TOKEN, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
//            payload.put(Constants.USERID, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_ID, ""));
//            payload.put(Constants.POST_ID, postId);
//
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }
//
//        return payload.toString();
//    }
//
//    /*
//    * remove post call
//    *
//    * */
//    public void removePost(String postId) {
//        try {
//
//
//            String payload = genratePayload(postId);
//            if (!payload.isEmpty()) {
//
//                WebNotificationManager.registerResponseListener(responseRemovePost);
//                WebServiceClient.remove_a_post(getActivity(), payload, responseRemovePost);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    /*
//    * response Remove post
//    *
//    *
//    * */
//    ResponseHandlerListener responseRemovePost = new ResponseHandlerListener() {
//        @Override
//        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
//            WebNotificationManager.unRegisterResponseListener(responseRemovePost);
//
//            try {
//                if (error == null) {
//                    if (result.getSuccess().equalsIgnoreCase("true")) {
//
//                        mIvDelete.setVisibility(View.GONE);
//                        mWallPostList.remove(POSITION);
//                        mWallPostAdapter.notifyDataSetChanged();
//                        Utility.showToastMessageLong(getActivity(), "post removed");
//
//                    } else {
//                        Utility.showToastMessageLong(getActivity(),getResources().getString(R.string.some_unknown_error));
//                    }
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
////            finish the dialog
//            if (mProgressDialog != null && mProgressDialog.isShowing()) {
//                mProgressDialog.dismiss();
//            }
//
//
//        }
//    };
//
//
//    /*
//    *
//    * flag post call
//    *
//    *
//    * */
//    public void flagPost(String postId) {
//
//        String payload = genratePayload(postId);
//        if (payload != null) {
//
//            WebNotificationManager.registerResponseListener(responseFlagPost);
//            WebServiceClient.flag_a_post(getActivity(), payload, responseFlagPost);
//
//        }
//
//    }
//
//
//    /*
//    * response flag post
//    *
//    *
//    * */
//    ResponseHandlerListener responseFlagPost = new ResponseHandlerListener() {
//        @Override
//        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
//            WebNotificationManager.unRegisterResponseListener(responseFlagPost);
//            try {
//
//                if (error == null) {
//                    if (result.getSuccess().equalsIgnoreCase("true")) {
//
//                        mIvDelete.setVisibility(View.GONE);
//                        Utility.showToastMessageLong(getActivity(), "post reported");
//
//
//                    } else {
//
//
//                    }
//
//
//                } else {
//
//
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//            //            finish the dialog
//            if (mProgressDialog != null && mProgressDialog.isShowing()) {
//                mProgressDialog.dismiss();
//            }
//        }
//    };




}
