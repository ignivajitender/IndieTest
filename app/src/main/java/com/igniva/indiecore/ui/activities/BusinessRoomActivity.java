package com.igniva.indiecore.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.igniva.indiecore.R;

/**
 * Created by igniva-andriod-05 on 15/7/16.
 */
public class BusinessRoomActivity extends BaseActivity {


    private TextView mChat, mBoard, mPeople;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_room);
        initToolbar();
        setUpLayout();

    }


    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar_business_room);
            mToolbar.setNavigationIcon(R.drawable.backarrow_icon);

            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            ImageView mToolBArImageBtn=(ImageView)mToolbar.findViewById(R.id.toolbar_img);
            mToolBArImageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent= new Intent(BusinessRoomActivity.this,MyBadgesActivity.class);
//                    startActivity(intent);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
    }

    @Override
    protected void setUpLayout() {

        mChat = (TextView) findViewById(R.id.tv_chat);
        mBoard = (TextView) findViewById(R.id.tv_board);
        mPeople = (TextView) findViewById(R.id.tv_people);
        setDataInViewObjects();

    }

    @Override
    protected void setDataInViewObjects() {

    }

    @Override
    protected void onClick(View v) {
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
            default:
                break;
        }

    }


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

            mChat.setTextColor(Color.parseColor("#1C6DCE"));
            mChat.setBackgroundResource(R.drawable.simple_border_line_style);

            mBoard.setTextColor(Color.parseColor("#FFFFFF"));
            mBoard.setBackgroundColor(Color.parseColor("#1C6DCE"));

            mPeople.setTextColor(Color.parseColor("#1C6DCE"));
            mPeople.setBackgroundResource(R.drawable.simple_border_line_style);


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
}
