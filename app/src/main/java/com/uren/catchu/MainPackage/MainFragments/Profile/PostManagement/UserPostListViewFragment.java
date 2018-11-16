package com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.Adapters.UserPostListViewAdapter;
import com.uren.catchu.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserPostListViewFragment extends BaseFragment{

    View mView;
    String catchType;

    UserPostListViewAdapter userPostListViewAdapter;
    LinearLayoutManager mLinearLayoutManager;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.listRecyclerView)
    RecyclerView listRecyclerView;


    public static UserPostListViewFragment newInstance(String catchType) {
        Bundle args = new Bundle();
        args.putString("catchType", catchType);
        UserPostListViewFragment fragment = new UserPostListViewFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public UserPostListViewFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(mView == null) {
            mView = inflater.inflate(R.layout.fragment_user_post_listview_layout, container, false);
            ButterKnife.bind(this, mView);
            getItemsFromBundle();

        }
        return mView;
    }

    private void getItemsFromBundle() {
        Bundle args = getArguments();
        if (args != null) {
            catchType = (String) args.getString("catchType");
        }
    }



}

