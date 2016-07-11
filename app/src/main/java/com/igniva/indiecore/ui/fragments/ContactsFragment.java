package com.igniva.indiecore.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.igniva.indiecore.R;

/**
 * Created by igniva-andriod-11 on 4/7/16.
 */
public class ContactsFragment extends BaseFragment{


    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView  = inflater.inflate(R.layout.fragment_contacts, container, false);

        return rootView;
    }

    @Override
    protected void setUpLayout() {

    }

    @Override
    protected void setDataInViewObjects() {

    }

    @Override
    protected void onClick(View v) {

    }
}
