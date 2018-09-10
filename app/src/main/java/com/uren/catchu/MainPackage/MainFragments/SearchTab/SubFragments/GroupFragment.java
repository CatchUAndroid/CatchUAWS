package com.uren.catchu.MainPackage.MainFragments.SearchTab.SubFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.uren.catchu.Adapters.UserGroupsListAdapter;
import com.uren.catchu.ApiGatewayFunctions.GroupResultProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.UserGroups;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.GroupRequest;
import catchu.model.GroupRequestResult;

@SuppressLint("ValidFragment")
public class GroupFragment extends Fragment {

    RecyclerView groupRecyclerView;

    private View mView;
    String userid;
    ViewGroup mContainer;
    LayoutInflater mLayoutInflater;

    GroupRequestResult groupRequestResult;

    LinearLayoutManager linearLayoutManager;
    RelativeLayout specialSelectRelLayout;
    GroupRequest groupRequest;

    private Context context;

    @BindView(R.id.progressBar)
    public ProgressBar progressBar;

    @SuppressLint("ValidFragment")
    public GroupFragment(Context context, String userid) {
        this.userid = userid;
        this.context = context;
    }

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
        specialSelectRelLayout = (RelativeLayout) mView.findViewById(R.id.specialSelectRelLayout);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        groupRecyclerView = (RecyclerView) mView.findViewById(R.id.specialRecyclerView);
        getData();
    }

    public void getData(){

        UserGroups userGroups = UserGroups.getInstance(AccountHolderInfo.getUserID());
        UserGroupsListAdapter userGroupsListAdapter = new UserGroupsListAdapter(context, userGroups.getGroupRequestResult());
        groupRecyclerView.setAdapter(userGroupsListAdapter);
        linearLayoutManager  = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        groupRecyclerView.setLayoutManager(linearLayoutManager);
    }
}
