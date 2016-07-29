package com.igniva.indiecore.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.OnCommentListItemClickListnerTest;
import com.igniva.indiecore.controller.OnListItemClickListner;
import com.igniva.indiecore.model.CommentPojo;
import com.igniva.indiecore.model.PostPojo;
import com.igniva.indiecore.ui.adapters.PostCommentAdapter;
import com.igniva.indiecore.ui.adapters.TestAdapter;
import com.igniva.indiecore.ui.adapters.WallPostAdapter;
import com.igniva.indiecore.utils.Utility;

/**
 * Created by igniva-andriod-11 on 29/4/16.
 */
public class TestActivity extends BaseActivity {


   
    private TextView mChat, mBoard, mPeople, mCreatePost, mUserName;
    ImageView mUserImage;
    public DashBoardActivity mDashBoard;
    private TestAdapter mAdapter;
    private ArrayList<PostPojo> mWallPostList;
    private ArrayList<CommentPojo> mCommentList;
    private LinearLayoutManager mLlManager;
    private LinearLayoutManager mLlmanager;
    private TestAdapter mTestAdapter;
    private PostCommentAdapter mCommentAdapter;
    private RecyclerView mRvWallPosts;
    private RecyclerView mRvComment;
    public final static String BUSINESS = "business";
    TestAdapter.RecyclerViewHolders mHolder;
    String PAGE = "1";
    String LIMIT = "10";
    String mBusinessId = "";
    int action = 0;

    //    (like/dislike/neutral), post_id
    public static final String mActionTypeLike = "like";
    public static final String mActionTypeDislike = "dislike";
    public static final String mActionTypeNeutral = "neutral";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        //
        setUpLayout();
        setDataInViewObjects();
        // this is sidharth
        // this is sidharth 2
        // this is sidharth 4
    }


    @Override
    protected void setUpLayout() {

        mWallPostList = new ArrayList<PostPojo>();
        try {

            mBusinessId = DashBoardActivity.businessId;

        } catch (Exception e) {
            e.printStackTrace();
        }

        mRvWallPosts = (RecyclerView) findViewById(R.id.rv_users_posts);
        mRvWallPosts.setLayoutManager(mLlManager);
        mChat = (TextView) findViewById(R.id.tv_chat);
        mBoard = (TextView) findViewById(R.id.tv_board);
        mPeople = (TextView) findViewById(R.id.tv_people);


        mCreatePost = (TextView) findViewById(R.id.tv_create_post);


        mUserName = (TextView) findViewById(R.id.tv_user_name_chat_fragment);
        mUserImage = (ImageView) findViewById(R.id.iv_user_img_chat_fragment);


    }

    @Override
    protected void setDataInViewObjects() {
        mAdapter = null;

        for (int i=0;i<10;i++){
            mWallPostList.add(new PostPojo());
        }

        mAdapter = new TestAdapter(this, mWallPostList, onListItemClickListner);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRvWallPosts.setLayoutManager(mLayoutManager);
        mRvWallPosts.setAdapter(mAdapter);

    }


    protected void onClick(View v) {

    }

    OnCommentListItemClickListnerTest onListItemClickListner = new OnCommentListItemClickListnerTest() {


        @Override
        public void onCommentListItemClicked(TestAdapter.RecyclerViewHolders holder, final int position, String postId) {

            {

                mHolder = holder;

                holder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Utility.showToastMessageShort(TestActivity.this," position is "+position);

                    }
                });

                holder.dislike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });


                holder.neutral.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        action = 3;


                    }
                });

                holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                    }
                });


            }

        }

        };

}
