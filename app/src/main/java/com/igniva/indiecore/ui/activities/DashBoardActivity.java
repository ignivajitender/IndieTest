package com.igniva.indiecore.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.igniva.indiecore.R;
import com.igniva.indiecore.ui.fragments.ChatsFragment;
import com.igniva.indiecore.ui.fragments.CheckInFragment;
import com.igniva.indiecore.ui.fragments.ContactsFragment;
import com.igniva.indiecore.ui.fragments.MessagesFragment;
import com.igniva.indiecore.ui.fragments.SettingsFragment;

/**
 * Created by igniva-andriod-05 on 29/6/16.
 */
public class DashBoardActivity extends BaseActivity {

    public AHBottomNavigation bottomNavigation;
    Fragment mFragment;
    Toolbar mToolbar;
    TextView mTvTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        setUpLayout();
        initToolbar();
        setDataInViewObjects();
    }

    @Override
    protected void setUpLayout() {
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
    }

    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar_with_icon);
            mTvTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title_img);
            mTvTitle.setText(getResources().getString(R.string.my_badges));
            //
            ImageView mTvNext = (ImageView) mToolbar.findViewById(R.id.toolbar_img);
            mTvNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(DashBoardActivity.this, MyBadgesActivity.class));
                }
            });
            //
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
    }

    @Override
    protected void setDataInViewObjects() {
        final AHBottomNavigationItem item1 = new AHBottomNavigationItem("Check In", R.drawable.check_in, R.color.blue);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Contacts", R.drawable.contact, R.color.blue);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Chats", R.drawable.chats, R.color.blue);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("Messages", R.drawable.messages, R.color.blue);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem("Settings", R.drawable.settings, R.color.blue);

        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);
        bottomNavigation.addItem(item5);


        // Set background color
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FFFFFF"));

        // Disable the translation inside the CoordinatorLayout
        bottomNavigation.setBehaviorTranslationEnabled(false);

        // Change colors
        bottomNavigation.setAccentColor(Color.parseColor("#3d92ff"));
        //   bottomNavigation.setInactiveColor(Color.parseColor("#FFFFFF"));

        // Force to tint the drawable (useful for font with icon for example)
        //        bottomNavigation.setForceTint(true);

        // Force the titles to be displayed (against Material Design guidelines!)
        bottomNavigation.setForceTitlesDisplay(true);

        // Use colored navigation with circle reveal effect
        //        bottomNavigation.setColored(true);

        // Set current item programmatically
        bottomNavigation.setCurrentItem(0);

        // Customize notification (title, background, typeface)
        //        bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));

        // Add or remove notification for each item
        bottomNavigation.setNotification("4", 1);
        bottomNavigation.setNotification("", 1);

        // Set listener
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                Fragment fragment = null;
                switch (position) {
                    case 0:
                        fragment = new CheckInFragment();
                        mTvTitle.setText("Check In");
                        break;
                    case 1:
                        fragment = new ContactsFragment();
                        mTvTitle.setText("Contacts");
                        break;
                    case 2:
                        fragment = new ChatsFragment();
                        mTvTitle.setText("Chats");
                        break;
                    case 3:
                        fragment = new MessagesFragment();
                        mTvTitle.setText("Messages");
                        break;
                    case 4:
                        fragment = new SettingsFragment();
                        mTvTitle.setText("Settings");
                        break;
                    default:
                        break;
                }

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fl_fragment_container, fragment);
                transaction.commit();
                return true;
            }
        });

        bottomNavigation.setCurrentItem(0);
    }

    @Override
    protected void onClick(View v) {

    }
}
