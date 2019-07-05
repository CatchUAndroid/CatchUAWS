package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.InfoDialogBoxCallback;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.OtherProfile.OtherProfileFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters.PendingRequestAdapter;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.FriendRequestList;
import catchu.model.User;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;

public class PendingRequestsFragment extends BaseFragment {

    View mView;
    PendingRequestAdapter pendingRequestAdapter;
    LinearLayoutManager linearLayoutManager;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.warningMsgTv)
    TextView warningMsgTv;
    @BindView(R.id.commonToolbarbackImgv)
    ClickableImageView commonToolbarbackImgv;
    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;
    @BindView(R.id.following_recyclerView)
    RecyclerView following_recyclerView;

    public PendingRequestsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((NextActivity) getContext()).ANIMATION_TAG = ANIMATE_RIGHT_TO_LEFT;
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_penging_requests, container, false);
            ButterKnife.bind(this, mView);
            toolbarTitleTv.setText(getContext().getResources().getString(R.string.PENDING_REQUESTS));
            warningMsgTv.setText(getContext().getResources().getString(R.string.THERE_IS_NO_PENDING_REQUEST));
            addListeners();
            getData();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void addListeners() {
        commonToolbarbackImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void getData() {
        progressBar.setVisibility(View.VISIBLE);
        AccountHolderFollowProcess.getPendingList(new CompleteCallback() {
            @Override
            public void onComplete(Object object) {
                FriendRequestList friendRequestList = (FriendRequestList) object;

                if (getContext() != null) {
                    setMessageWarning(friendRequestList);

                    pendingRequestAdapter = new PendingRequestAdapter(getContext(), friendRequestList, new ListItemClickListener() {
                        @Override
                        public void onClick(View view, User user, int clickedPosition) {
                            startFollowingInfoProcess(user, clickedPosition);
                        }
                    }, new ReturnCallback() {
                        @Override
                        public void onReturn(Object object1) {
                            AccountHolderFollowProcess.getPendingList(new CompleteCallback() {
                                @Override
                                public void onComplete(Object object) {
                                    FriendRequestList friendRequestList1 = (FriendRequestList) object;
                                    setMessageWarning(friendRequestList1);
                                }

                                @Override
                                public void onFailed(Exception e) {
                                }
                            });
                        }
                    });
                    following_recyclerView.setAdapter(pendingRequestAdapter);
                    linearLayoutManager = new LinearLayoutManager(getContext());
                    linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                    following_recyclerView.setLayoutManager(linearLayoutManager);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailed(Exception e) {
                progressBar.setVisibility(View.GONE);
                DialogBoxUtil.showErrorDialog(getContext(), getActivity().getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                    @Override
                    public void okClick() {

                    }
                });
            }
        });
    }

    private void setMessageWarning(FriendRequestList friendRequestList) {
        if (friendRequestList != null && friendRequestList.getResultArray() != null &&
                friendRequestList.getResultArray().size() > 0)
            warningMsgTv.setVisibility(View.GONE);
        else
            warningMsgTv.setVisibility(View.VISIBLE);
    }

    private void startFollowingInfoProcess(User user, int clickedPosition) {
        if (mFragmentNavigation != null && user != null) {
            UserInfoListItem userInfoListItem = new UserInfoListItem(user);
            userInfoListItem.setAdapter(pendingRequestAdapter);
            userInfoListItem.setClickedPosition(clickedPosition);
            mFragmentNavigation.pushFragment(new OtherProfileFragment(userInfoListItem), ANIMATE_RIGHT_TO_LEFT);
        }
    }
}
