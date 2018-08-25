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

import java.util.List;

import butterknife.ButterKnife;
import catchu.model.GroupRequestResult;
import catchu.model.UserProfile;

@SuppressLint("ValidFragment")
public class GroupDetailFragment extends Fragment {

    View mView;
    RecyclerView personRecyclerView;

    LinearLayoutManager linearLayoutManager;
    GroupRequestResult groupRequestResult;
    List<UserProfile> groupParticipantList;

    @SuppressLint("ValidFragment")
    public GroupDetailFragment(List<UserProfile> groupParticipantList, GroupRequestResult groupRequestResult) {
        this.groupRequestResult = groupRequestResult;
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
                groupParticipantList, groupRequestResult);

        personRecyclerView.setAdapter(groupDetailListAdapter);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        personRecyclerView.setLayoutManager(linearLayoutManager);
    }
}
