package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.DataModelUtil.MessageDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.FollowInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters.PendingRequestAdapter;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.FollowInfoResultArrayItem;
import catchu.model.FriendRequestList;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;

public class PendingRequestsFragment extends BaseFragment {

    RecyclerView following_recyclerView;

    View mView;
    PendingRequestAdapter pendingRequestAdapter;
    LinearLayoutManager linearLayoutManager;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.warningMsgTv)
    TextView warningMsgTv;

    @BindView(R.id.backImgv)
    ImageView backImgv;

    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;

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
        toolbarTitleTv.setText(getActivity().getResources().getString(R.string.PENDING_REQUESTS));
        addListeners();
        getData();
    }

    private void addListeners() {
        backImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NextActivity) getContext()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
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

                MessageDataUtil.setWarningMessageVisibility(friendRequestList, warningMsgTv,
                        getActivity().getResources().getString(R.string.THERE_IS_NO_PENDING_REQUEST));

                if(getContext() != null) {
                    pendingRequestAdapter = new PendingRequestAdapter(getContext(), friendRequestList, new ListItemClickListener() {
                        @Override
                        public void onClick(View view, FollowInfoResultArrayItem rowItem, int clickedPosition) {
                            startFollowingInfoProcess(rowItem, clickedPosition);
                        }
                    }, new ReturnCallback() {
                        @Override
                        public void onReturn(Object object1) {
                            AccountHolderFollowProcess.getPendingList(new CompleteCallback() {
                                @Override
                                public void onComplete(Object object) {
                                    FriendRequestList friendRequestList = (FriendRequestList) object;
                                    MessageDataUtil.setWarningMessageVisibility(friendRequestList, warningMsgTv,
                                            getActivity().getResources().getString(R.string.THERE_IS_NO_PENDING_REQUEST));
                                }

                                @Override
                                public void onFailed(Exception e) {

                                }
                            });
                        }
                    });
                    following_recyclerView.setAdapter(pendingRequestAdapter);
                    linearLayoutManager = new LinearLayoutManager(getContext());
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
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

    private void startFollowingInfoProcess(FollowInfoResultArrayItem rowItem, int clickedPosition) {

        if (mFragmentNavigation != null) {
            FollowInfoListItem followInfoListItem = new FollowInfoListItem(rowItem);
            followInfoListItem.setAdapter(pendingRequestAdapter);
            followInfoListItem.setClickedPosition(clickedPosition);
            mFragmentNavigation.pushFragment(OtherProfileFragment.newInstance(followInfoListItem), ANIMATE_RIGHT_TO_LEFT);
        }
    }
}
