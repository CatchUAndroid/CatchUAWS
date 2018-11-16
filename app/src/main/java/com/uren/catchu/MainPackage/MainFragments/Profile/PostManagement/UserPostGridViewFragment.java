package com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
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
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.Adapters.UserPostGridViewAdapter;
import com.uren.catchu.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserPostGridViewFragment extends BaseFragment{

    View mView;
    String catchType;

    UserPostGridViewAdapter userPostGridViewAdapter;
    GridLayoutManager mGridLayoutManager;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.gridRecyclerView)
    RecyclerView gridRecyclerView;


    public static UserPostGridViewFragment newInstance(String catchType) {
        Bundle args = new Bundle();
        args.putString("catchType", catchType);
        UserPostGridViewFragment fragment = new UserPostGridViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public UserPostGridViewFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(mView == null) {
            mView = inflater.inflate(R.layout.fragment_user_post_gridview_layout, container, false);
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

