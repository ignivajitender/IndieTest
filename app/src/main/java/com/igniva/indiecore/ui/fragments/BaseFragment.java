package com.igniva.indiecore.ui.fragments;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by igniva-andriod-11 on 29/4/16.
 */
public abstract class BaseFragment extends Fragment{

    protected abstract void setUpLayout();
    protected abstract void setDataInViewObjects();
//    protected abstract void onClick(View v);

}
