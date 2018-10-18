package com.uren.catchu.MainPackage.MainFragments.SearchTab.SubFragments;

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

import com.uren.catchu.GroupPackage.DisplayGroupDetailFragment;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.FollowInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.OtherProfileFragment;
import com.uren.catchu.MainPackage.MainFragments.SearchTab.Adapters.UserGroupsListAdapter;
import com.uren.catchu.GeneralUtils.DataModelUtil.MessageDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.UserGroups;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;

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

    public static GroupFragment newInstance() {
        Bundle args = new Bundle();
        GroupFragment fragment = new GroupFragment();
        fragment.setArguments(args);
        return fragment;
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
        getGroups();
    }

    public void getGroups() {

        UserGroups.getInstance(new CompleteCallback() {
            @Override
            public void onComplete(Object object) {
                GroupRequestResult groupRequestResult = (GroupRequestResult) object;

                MessageDataUtil.setWarningMessageVisibility(groupRequestResult, warningMsgTv,
                        getActivity().getResources().getString(R.string.THERE_IS_NO_GROUP_CREATE_OR_INCLUDE));

                userGroupsListAdapter = new UserGroupsListAdapter(getActivity(), groupRequestResult, new ReturnCallback() {
                    @Override
                    public void onReturn(Object object) {
                        GroupRequestResult groupRequestResult1 = (GroupRequestResult) object;

                        MessageDataUtil.setWarningMessageVisibility(groupRequestResult1, warningMsgTv,
                                getActivity().getResources().getString(R.string.THERE_IS_NO_GROUP_CREATE_OR_INCLUDE));
                    }
                }, new ItemClickListener() {
                    @Override
                    public void onClick(Object object, int clickedItem) {
                        GroupRequestResultResultArrayItem groupRequestResultResultArrayItem = (GroupRequestResultResultArrayItem) object;
                        String groupId = groupRequestResultResultArrayItem.getGroupid();

                        if (mFragmentNavigation != null)
                            mFragmentNavigation.pushFragment(DisplayGroupDetailFragment.newInstance(groupId), ANIMATE_RIGHT_TO_LEFT);
                    }
                });

                groupRecyclerView.setAdapter(userGroupsListAdapter);
                linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                groupRecyclerView.setLayoutManager(linearLayoutManager);
            }

            @Override
            public void onFailed(Exception e) {
                DialogBoxUtil.showErrorDialog(getActivity(), getActivity().getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                    @Override
                    public void okClick() {
                    }
                });
            }
        });
    }
}
