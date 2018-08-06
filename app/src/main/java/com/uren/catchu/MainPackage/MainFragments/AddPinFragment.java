package com.uren.catchu.MainPackage.MainFragments;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;

import butterknife.ButterKnife;


public class AddPinFragment extends BaseFragment
 {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_pin, container, false);

        ButterKnife.bind(this, view);

        //((NextActivity) getActivity()).getSupportActionBar().show();
        ( (NextActivity)getActivity()).updateToolbarTitle("Pin fragment");


        return view;
    }



}
