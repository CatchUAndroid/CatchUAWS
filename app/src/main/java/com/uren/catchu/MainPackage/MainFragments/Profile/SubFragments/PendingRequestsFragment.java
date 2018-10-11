package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.RowItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.FollowInfoRowItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters.PendingRequestAdapter;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderPendings;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.FollowInfoResultArrayItem;
import catchu.model.FriendRequestList;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;

public class PendingRequestsFragment extends BaseFragment {

    RecyclerView following_recyclerView;
    TextView toolbarTitle;
    View mView;
    PendingRequestAdapter pendingRequestAdapter;
    LinearLayoutManager linearLayoutManager;

    @BindView(R.id.progressBar)
    public ProgressBar progressBar;
    @BindView(R.id.imgBack)
    ClickableImageView imgBack;
    @BindView(R.id.imgAddFollowing)
    ClickableImageView imgAddFollowing;
    @BindView(R.id.warningMsgTv)
    TextView warningMsgTv;

    public PendingRequestsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.profile_subfragment_following, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        following_recyclerView = mView.findViewById(R.id.following_recyclerView);
        toolbarTitle = mView.findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(getActivity().getResources().getString(R.string.PENDING_REQUESTS));
        imgAddFollowing.setVisibility(View.GONE);
        warningMsgTv.setText(getActivity().getResources().getString(R.string.THERE_IS_NO_PENDING_REQUEST));
        addListeners();
        getData();
    }

    private void addListeners() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void getData() {
        AccountHolderPendings.getInstance(new CompleteCallback() {
            @Override
            public void onComplete(Object object) {
                FriendRequestList friendRequestList = (FriendRequestList) object;
                setWarningMessageVisibility(friendRequestList);
                pendingRequestAdapter = new PendingRequestAdapter(getActivity(), friendRequestList, new RowItemClickListener() {
                    @Override
                    public void onClick(View view, FollowInfoResultArrayItem rowItem, int clickedPosition) {
                        startFollowingInfoProcess(rowItem, clickedPosition);
                    }
                }, new ReturnCallback() {
                    @Override
                    public void onReturn(Object object1) {
                        FriendRequestList friendRequestList1 = (FriendRequestList) object1;
                        setWarningMessageVisibility(friendRequestList1);
                    }
                });
                following_recyclerView.setAdapter(pendingRequestAdapter);
                linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                following_recyclerView.setLayoutManager(linearLayoutManager);
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

    public void setWarningMessageVisibility(FriendRequestList friendRequestList) {
        if (friendRequestList != null && friendRequestList.getResultArray() != null &&
                friendRequestList.getResultArray().size() == 0) {
            warningMsgTv.setVisibility(View.VISIBLE);
        } else
            warningMsgTv.setVisibility(View.GONE);
    }

    private void startFollowingInfoProcess(FollowInfoResultArrayItem rowItem, int clickedPosition) {

        if (mFragmentNavigation != null) {
            FollowInfoRowItem followInfoRowItem = new FollowInfoRowItem(rowItem);
            followInfoRowItem.setAdapter(pendingRequestAdapter);
            followInfoRowItem.setClickedPosition(clickedPosition);
            mFragmentNavigation.pushFragment(OtherProfileFragment.newInstance(followInfoRowItem), ANIMATE_RIGHT_TO_LEFT);
        }
    }
}
