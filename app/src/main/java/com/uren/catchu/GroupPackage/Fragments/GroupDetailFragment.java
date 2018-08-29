package com.uren.catchu.GroupPackage.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.uren.catchu.GroupPackage.Adapters.GroupDetailListAdapter;
import com.uren.catchu.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.UserProfile;
import catchu.model.UserProfileProperties;

@SuppressLint("ValidFragment")
public class GroupDetailFragment extends Fragment {

    View mView;
    RecyclerView personRecyclerView;

    LinearLayoutManager linearLayoutManager;
    List<UserProfileProperties> groupParticipantList;
    GroupRequestResultResultArrayItem groupRequestResultResultArrayItem;

    @SuppressLint("ValidFragment")
    public GroupDetailFragment(List<UserProfileProperties> groupParticipantList, GroupRequestResultResultArrayItem groupRequestResultResultArrayItem) {
        this.groupParticipantList = new ArrayList<UserProfileProperties>();
        this.groupRequestResultResultArrayItem = groupRequestResultResultArrayItem;
        this.groupParticipantList.addAll(groupParticipantList);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_special_select, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        personRecyclerView = (RecyclerView) mView.findViewById(R.id.specialRecyclerView);
        getData();
    }

    public void getData() {

        GroupDetailListAdapter groupDetailListAdapter = new GroupDetailListAdapter(getActivity(),
                groupParticipantList, groupRequestResultResultArrayItem);

        personRecyclerView.setAdapter(groupDetailListAdapter);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        personRecyclerView.setLayoutManager(linearLayoutManager);
    }
}
