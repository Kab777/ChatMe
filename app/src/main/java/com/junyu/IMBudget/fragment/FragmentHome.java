package com.junyu.IMBudget.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.junyu.IMBudget.R;

import timber.log.Timber;

public class FragmentHome extends Fragment {


    public FragmentHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Timber.v("FragmentHome Called");
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

}
