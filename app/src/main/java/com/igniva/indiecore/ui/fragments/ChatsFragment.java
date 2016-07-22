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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.OnListItemClickListner;
import com.igniva.indiecore.controller.ResponseHandlerListener;
import com.igniva.indiecore.controller.WebNotificationManager;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.model.CommentPojo;
import com.igniva.indiecore.model.ResponsePojo;
import com.igniva.indiecore.model.PostPojo;
import com.igniva.indiecore.ui.activities.CommentActivity;
import com.igniva.indiecore.ui.activities.CreatePostActivity;
import com.igniva.indiecore.ui.activities.DashBoardActivity;
import com.igniva.indiecore.ui.adapters.PostCommentAdapter;
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
    private TextView mChat, mBoard, mPeople, mCreatePost, mUserName;
    ImageView mUserImage;
    public DashBoardActivity mDashBoard;
    private WallPostAdapter mAdapter;
    private ArrayList<PostPojo> mWallPostList;
    private ArrayList<CommentPojo> mCommentList;
    private LinearLayoutManager mLlManager;
    private LinearLayoutManager mLlmanager;
    private WallPostAdapter mWallPostAdapter;
    private PostCommentAdapter mCommentAdapter;
    private RecyclerView mRvWallPosts;
    private RecyclerView mRvComment;
    public final static String BUSINESS = "business";
    String PAGE = "1";
    String LIMIT = "10";
    String mBusinessId = "";

    //    (like/dislike/neutral), post_id
    public static final String mActionTypeLike = "like";
    public static final String mActionTypeDislike = "dislike";
    public static final String mActionTypeNeutral = "neutral";

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

        mWallPostList = new ArrayList<PostPojo>();
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



/*
*
* TODO add scroll listner
* */

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
//
//                }
//
//        });


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


    OnListItemClickListner onListItemClickListner = new OnListItemClickListner() {
        @Override
        public void onListItemClicked(final WallPostAdapter.RecyclerViewHolders holder, final int position, final String postId) {


            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    likeUnlikePost(mActionTypeLike, postId);
                }
            });

            holder.dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    likeUnlikePost(mActionTypeDislike, postId);
                }
            });


            holder.neutral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    likeUnlikePost(mActionTypeNeutral, postId);

                }
            });

            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    holder.mCommentSection.setVisibility(View.VISIBLE);
//                    mRvComment = holder.mRvComments;
//                    mRvComment.setLayoutManager(mLlmanager);
//                    holder.mCommentSection.setVisibility(View.VISIBLE);
//                    viewAllComments(postId);
                    Bundle bundle= new Bundle();
                    bundle.putSerializable("POST",mWallPostList.get(position));

                    Intent intent= new Intent(getActivity(), CommentActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);


                }
            });

//
//            holder.mIvPostComment.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (holder.mEtComment.getText().toString().isEmpty()) {
//
//                        Utility.showAlertDialog("Please write a comment", getActivity());
//                    } else {
//
//                        postComment(postId, holder.mEtComment.getText().toString());
//                    }
//
//                }
//            });

        }
    };



    /*
    * payload to write  a comment to a post
    *
    *
    * */
    public String genratePayload(String postId, String text) {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.POSTID, postId);
            payload.put(Constants.TEXT, text);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return payload.toString();
    }

    /*
    * to post a comment for a businesspost
    *
    * */
    public void postComment(String postId, String text) {

        String payload = genratePayload(postId, text);
        if (payload != null) {

            WebNotificationManager.registerResponseListener(responseHandlerComment);
            WebServiceClient.make_a_comment(getActivity(), payload, responseHandlerComment);
        }

    }

    /*
    *
    * response of  a comment post
    * */
    ResponseHandlerListener responseHandlerComment = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            WebNotificationManager.unRegisterResponseListener(responseHandlerComment);

            if (error == null) {

                if (result.getSuccess().equalsIgnoreCase("true")) {

                    Utility.showToastMessageLong(getActivity(), "comment posted");

                } else {

                }
            } else {

            }

            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    };


    /*
    *
    * payload to get comments of a post
    *
    * */
    public String createPayload(String postId) {
//        PARAMETER: token, userId, postId, page, limit
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.POSTID, postId);
            payload.put(Constants.PAGE, "1");
            payload.put(Constants.LIMIT, "10");


        } catch (Exception e) {
            e.printStackTrace();
        }
        return payload.toString();
    }


    /*
    *
    * to get all comments of a post
    *
    * */
    public void viewAllComments(String postId) {

        try {
            String payload = createPayload(postId);
            if (payload != null) {

                WebNotificationManager.registerResponseListener(responseHandle);
                WebServiceClient.view_all_comments(getActivity(), payload, responseHandle);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    *
    * response all get comments
    * */
    ResponseHandlerListener responseHandle = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {

            try {
                WebNotificationManager.unRegisterResponseListener(responseHandle);
                if (error == null) {

                    if (result.getSuccess().equalsIgnoreCase("true")) {
                        mCommentList = new ArrayList<CommentPojo>();
                        mCommentList.addAll(result.getCommentList());
                        mCommentAdapter = null;
                        mCommentAdapter = new PostCommentAdapter(getActivity(), mCommentList);
                        mRvComment.setAdapter(mCommentAdapter);
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
            mAdapter = new WallPostAdapter(getActivity(), mWallPostList, onListItemClickListner);
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

    /*
    *
    * create payload to get all post of a business
    * */
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

    /*
    *
    *
    * to get all the post of this business wall
    *
    * */
    public void viewAllPost() {
        String payload = createPayload();
        if (!payload.isEmpty()) {

            WebNotificationManager.registerResponseListener(responseHandler);
            WebServiceClient.view_all_posts(getActivity(), payload, responseHandler);
        }
    }

    /*
    *
    * posts response and list inflation
    *
    * */
    ResponseHandlerListener responseHandler = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(responseHandler);
            try {

                if (error == null) {

                    if (result.getSuccess().equalsIgnoreCase("true")) {
                        mWallPostList.clear();
                        mWallPostList.addAll(result.getPostList());

                        if (mWallPostList.size() > 0) {
                            try {
                                mWallPostAdapter = null;
                                mWallPostAdapter = new WallPostAdapter(getActivity(), mWallPostList, onListItemClickListner);
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

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    //    /*
//
// create payload to like unlike a post
//
//
// */
    public String createPayload(String type, String postId) {

        JSONObject payload = null;
//        token, userId, type(like/dislike/neutral), post_id
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.TYPE, type);
            payload.put(Constants.POST_ID, postId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return payload.toString();
    }


    /*
    * like/unlike/neutral action to a post
    * @parms post_id
    *
    * */
    public void likeUnlikePost(String type, String postId) {
        //create payload with parameters is to be call here

        String payload = createPayload(type, postId);

        if (!payload.isEmpty()) {

            WebNotificationManager.registerResponseListener(responseHandl);
            WebServiceClient.like_unlike_post(getActivity(), payload, responseHandl);
        }
    }

    /*
    * like unlike response
    * */
    ResponseHandlerListener responseHandl = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(responseHandl);

            if (error == null) {

                if (result.getSuccess().equalsIgnoreCase("true")) {

                    Utility.showToastMessageLong(getActivity(), "done");
                }
            }
//            finish the dialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

        }
    };



    /*
    * create payload to flag/remove a post
    *
    *
    * */


    public String genratePayload(String postId) {
        JSONObject payload = null;
        try {
            payload = new JSONObject();
            payload.put(Constants.TOKEN, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_TOKEN, ""));
            payload.put(Constants.USERID, PreferenceHandler.readString(getActivity(), PreferenceHandler.PREF_KEY_USER_ID, ""));
            payload.put(Constants.POST_ID, postId);


        } catch (Exception e) {

            e.printStackTrace();
        }

        return payload.toString();
    }

/*
* remove post call
*
* */
    public void removePost(String postId) {

        String payload = genratePayload(postId);
        if (!payload.isEmpty()) {

            WebNotificationManager.registerResponseListener(responseRemovePost);
            WebServiceClient.remove_a_post(getActivity(), payload, responseRemovePost);
        }
    }


    /*
    * response Remove post
    *
    *
    * */
    ResponseHandlerListener responseRemovePost = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(responseRemovePost);

            try {
                if (error == null) {
                    if (result.getSuccess().equalsIgnoreCase("true")) {

                        Utility.showToastMessageLong(getActivity(), "post removed");

                    } else {

                    }

                } else {


                }


            } catch (Exception e) {
                e.printStackTrace();
            }
//            finish the dialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }


        }
    };


    /*
    *
    * flag post call
    *
    *
    * */
    public void FlagPost(String postId) {

        String payload = genratePayload(postId);
        if (payload != null) {

            WebNotificationManager.registerResponseListener(responseFlagPost);
            WebServiceClient.flag_a_post(getActivity(), payload, responseFlagPost);

        }

    }


    /*
    * response flag post
    *
    *
    * */
    ResponseHandlerListener responseFlagPost = new ResponseHandlerListener() {
        @Override
        public void onComplete(ResponsePojo result, WebServiceClient.WebError error, ProgressDialog mProgressDialog) {
            WebNotificationManager.unRegisterResponseListener(responseFlagPost);
            try {

                if (error == null) {
                    if (result.getSuccess().equalsIgnoreCase("true")) {
                        Utility.showToastMessageLong(getActivity(), "post reported");


                    }
                    else
                    {


                    }


                }
                else
                {


                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            //            finish the dialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    };

}
