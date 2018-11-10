package com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.R;
import butterknife.ButterKnife;

public class MapFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.half_map_view, container, false);
            ButterKnife.bind(this, mView);

            init();
        }


        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    private void init() {


    }


    @Override
    public void onClick(View v) {




    }


}
