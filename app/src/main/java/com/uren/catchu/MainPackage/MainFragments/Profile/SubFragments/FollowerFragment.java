package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;

import static com.uren.catchu.Constants.StringConstants.AnimateLeftToRight;


public class FollowerFragment extends Fragment {

    LinearLayout rvNewsList;

    public FollowerFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rvNewsList = (LinearLayout) inflater.inflate(R.layout.profile_subfragment_followers, container, false);
        return rvNewsList;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //setUpRecyclerView();


        Button btn = (Button) view.findViewById(R.id.btnBack);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NextActivity)getActivity()).ANIMATION_TAG = AnimateLeftToRight;
                getActivity().onBackPressed();
            }
        });

    }
}
