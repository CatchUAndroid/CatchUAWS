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


public class FollowingFragment extends Fragment {

    View mView;

    public FollowingFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.profile_subfragment_following, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


}
