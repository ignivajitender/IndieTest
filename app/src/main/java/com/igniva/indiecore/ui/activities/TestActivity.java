package com.igniva.indiecore.ui.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import com.igniva.indiecore.R;

/**
 * Created by igniva-andriod-11 on 29/4/16.
 */
public class TestActivity extends BaseActivity {

    View mRowView;
    int[] rowIds = new int[]{R.id.tv_0,R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_4, R.id.tv_5, R.id.tv_6, R.id.tv_7, R.id.tv_8,R.id.tv_9, R.id.tv_10, R.id.tv_11, R.id.tv_12, R.id.tv_13, R.id.tv_14, R.id.tv_15, R.id.tv_16, R.id.tv_17};
    String[] timeArray = new String[]{"Time","5 am", "6 am", "7 am", "8 am", "9 am", "10 am", "11 am", "12 pm", "1 pm", "2 pm", "3 pm", "4 pm", "5 pm", "6 pm", "7 pm", "8 pm", "9 pm"};
    ArrayList<View> mRowViewList = new ArrayList<>();
    String[] dayArray = new String[]{"","Mon","Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        //
        setUpLayout();
        setDataInViewObjects();
    }

    @Override
    protected void setUpLayout() {
        LinearLayout mLlParent = (LinearLayout) findViewById(R.id.ll_parent);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < 8; i++) {
            View custom = inflater.inflate(R.layout.row3, null);
            mLlParent.addView(custom);
            mRowViewList.add(custom);
        }

        // update time column
        mRowView=mRowViewList.get(0);
        for (int i=0;i<18;i++){
            ((TextView)mRowView.findViewById(rowIds[i])).setText(timeArray[i]);
        }

        // update day row
        for (int i=1;i<8;i++){
            ((TextView)mRowViewList.get(i).findViewById(R.id.tv_0)).setText(dayArray[i]);
        }
    }

    @Override
    protected void setDataInViewObjects() {

        updateCoachSchedule("Wednesday", 9, 15);

    }

    @Override
    protected void onClick(View v) {
        switch (v.getId()) {
            default:
                break;

        }

    }

    /**
     * @param day Name of day
     * @param  start starting day
     * @param  end exclusive
     *
     * */
    private void updateCoachSchedule(String day, int start, int end) {

        switch (day) {
            case "Monday":
                mRowView=mRowViewList.get(1);
                break;
            case "Tuesday":
                mRowView=mRowViewList.get(2);
                break;
            case "Wednesday":
                mRowView=mRowViewList.get(3);
                break;
            case "Thursday":
                mRowView=mRowViewList.get(4);
                break;
            case "Friday":
                mRowView=mRowViewList.get(5);
                break;
            case "Saturday":
                mRowView=mRowViewList.get(6);
                break;
            case "Sunday":
                mRowView=mRowViewList.get(7);
                break;

        }

        for (int i = start; start < end; start++) {
            ((TextView)mRowView.findViewById(rowIds[start])).setTextColor(Color.parseColor("#f00000"));
        }


    }
}
