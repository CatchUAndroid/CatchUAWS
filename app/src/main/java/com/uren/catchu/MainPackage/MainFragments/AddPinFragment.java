package com.uren.catchu.MainPackage.MainFragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.uren.catchu.R;
import com.uren.catchu.SharePackage.MainShareActivity;

import butterknife.ButterKnife;


public class AddPinFragment extends BaseFragment {

    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_pin, container, false);
        ButterKnife.bind(this, view);
        context = getActivity();

        startActivity(new Intent(context, MainShareActivity.class));

        return view;
    }


}
