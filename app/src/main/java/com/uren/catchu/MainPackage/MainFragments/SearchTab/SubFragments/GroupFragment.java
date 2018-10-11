package com.uren.catchu.MainPackage.MainFragments.SearchTab.SubFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.Adapters.UserGroupsListAdapter;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.UserGroups;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.FriendRequestList;
import catchu.model.GroupRequestResult;

@SuppressLint("ValidFragment")
public class GroupFragment extends Fragment {

    RecyclerView groupRecyclerView;

    private View mView;
    String userid;
    ViewGroup mContainer;
    LayoutInflater mLayoutInflater;
    TextView warningMsgTv;

    LinearLayoutManager linearLayoutManager;
    RelativeLayout specialSelectRelLayout;
    UserGroupsListAdapter userGroupsListAdapter;

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
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        groupRecyclerView = mView.findViewById(R.id.specialRecyclerView);
        specialSelectRelLayout = mView.findViewById(R.id.specialSelectRelLayout);
        warningMsgTv = mView.findViewById(R.id.warningMsgTv);
        warningMsgTv.setText(getActivity().getResources().getString(R.string.THERE_IS_NO_GROUP_CREATE_OR_INCLUDE));
        getGroups();
    }

    public void getGroups() {

        UserGroups.getInstance(new CompleteCallback() {
            @Override
            public void onComplete(Object object) {
                GroupRequestResult groupRequestResult = (GroupRequestResult) object;
                setWarningMessageVisibility(groupRequestResult);
                userGroupsListAdapter = new UserGroupsListAdapter(context, groupRequestResult, new ReturnCallback() {
                    @Override
                    public void onReturn(Object object) {
                        GroupRequestResult groupRequestResult1 = (GroupRequestResult) object;
                        setWarningMessageVisibility(groupRequestResult1);
                    }
                });
                groupRecyclerView.setAdapter(userGroupsListAdapter);
                linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                groupRecyclerView.setLayoutManager(linearLayoutManager);
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

    public void setWarningMessageVisibility(GroupRequestResult groupRequestResult) {
        if (groupRequestResult != null && groupRequestResult.getResultArray() != null &&
                groupRequestResult.getResultArray().size() == 0) {
            warningMsgTv.setVisibility(View.VISIBLE);
        } else
            warningMsgTv.setVisibility(View.GONE);
    }
}
