package com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.R;

import butterknife.ButterKnife;

public class VideoFragment extends Fragment {

    View mView;

    public VideoFragment() {
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        CommonUtils.LOG_NEREDEYIZ("VideoFragment");

        mView = inflater.inflate(R.layout.viewpager_video, container, false);
        ButterKnife.bind(this, mView);
        
        setVideo();

        return mView;

    }

    private void setVideo() {
    }
}
