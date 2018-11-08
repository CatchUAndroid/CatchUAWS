package com.uren.catchu.MainPackage.MainFragments.SearchTab.SubFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Adapters.UserGroupsListAdapter;
import com.uren.catchu.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupFragment extends BaseFragment {

    RecyclerView groupRecyclerView;

    private View mView;
    ViewGroup mContainer;
    LayoutInflater mLayoutInflater;
    TextView warningMsgTv;

    LinearLayoutManager linearLayoutManager;
    RelativeLayout specialSelectRelLayout;
    UserGroupsListAdapter userGroupsListAdapter;

    @BindView(R.id.progressBar)
    public ProgressBar progressBar;

    public GroupFragment() {
    }

 /*   public static GroupFragment newInstance() {
        Bundle args = new Bundle();
        GroupFragment fragment = new GroupFragment();
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.mContainer = container;
        this.mLayoutInflater = inflater;
        mView = inflater.inflate(R.layout.fragment_special_select, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        groupRecyclerView = mView.findViewById(R.id.specialRecyclerView);
        specialSelectRelLayout = mView.findViewById(R.id.specialSelectRelLayout);
        warningMsgTv = mView.findViewById(R.id.warningMsgTv);
        getGroups();
    }

    public void getGroups() {

    }
}
