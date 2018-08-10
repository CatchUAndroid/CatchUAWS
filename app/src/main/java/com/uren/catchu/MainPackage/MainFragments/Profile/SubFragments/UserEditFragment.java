package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.uren.catchu.FragmentControllers.FragNavTransactionOptions;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;

import static com.uren.catchu.Constants.StringConstants.AnimateLeftToRight;
import static com.uren.catchu.Constants.StringConstants.AnimateRightToLeft;

public class UserEditFragment extends BaseFragment {

    LinearLayout rvNewsList;

    public UserEditFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rvNewsList = (LinearLayout) inflater.inflate(R.layout.fragment_user_edit, container, false);
        return rvNewsList;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //setUpRecyclerView();


        Button btn = (Button) view.findViewById(R.id.btnBack);
        Button btnbtnNextFrag = (Button) view.findViewById(R.id.btnNextFrag);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NextActivity)getActivity()).ANIMATION_TAG = AnimateLeftToRight;
                getActivity().onBackPressed();
            }
        });

        btnbtnNextFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFragmentNavigation != null) {

                    mFragmentNavigation.pushFragment(new UserEditFragment(), AnimateRightToLeft);

                    //mFragmentNavigation.pushFragment(new UserEditFragment());

                }
            }
        });

    }

}