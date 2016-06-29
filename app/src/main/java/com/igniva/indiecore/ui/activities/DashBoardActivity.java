package com.igniva.indiecore.ui.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.igniva.indiecore.R;

/**
 * Created by igniva-andriod-05 on 29/6/16.
 */
public class DashBoardActivity extends BaseActivity {

    AHBottomNavigation bottomNavigation;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        setUpLayout();
        setDataInViewObjects();
    }

    @Override
    protected void setUpLayout() {
         bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
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
        bottomNavigation.setCurrentItem(1);

// Customize notification (title, background, typeface)
//        bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));

// Add or remove notification for each item
        bottomNavigation.setNotification("4", 1);
        bottomNavigation.setNotification("", 1);

// Set listener
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                // Do something cool here...
                switch (position){
                    case 0:
                        item1.setDrawable(R.drawable.check_in_selected);
                        break;
                    case 1:

                        break;

                }
                return true;
            }
        });
    }

    @Override
    protected void onClick(View v) {

    }
}
