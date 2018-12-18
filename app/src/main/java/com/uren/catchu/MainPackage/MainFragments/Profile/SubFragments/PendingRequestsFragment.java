package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.content.res.Resources;
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

import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.DataModelUtil.MessageDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.Interfaces.ReturnCallback;
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
        try {
            ((NextActivity) getContext()).ANIMATION_TAG = ANIMATE_RIGHT_TO_LEFT;
            mView = inflater.inflate(R.layout.fragment_penging_requests, container, false);
            ButterKnife.bind(this, mView);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),PendingRequestsFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        try {
            toolbarTitleTv.setText(getActivity().getResources().getString(R.string.PENDING_REQUESTS));
            addListeners();
            getData();
        } catch (Resources.NotFoundException e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),PendingRequestsFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    private void addListeners() {
        try {
            commonToolbarbackImgv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),PendingRequestsFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void getData() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            AccountHolderFollowProcess.getPendingList(new CompleteCallback() {
                @Override
                public void onComplete(Object object) {
                    FriendRequestList friendRequestList = (FriendRequestList) object;

                    if(getContext() != null) {
                        MessageDataUtil.setWarningMessageVisibility(friendRequestList, warningMsgTv,
                                getContext().getResources().getString(R.string.THERE_IS_NO_PENDING_REQUEST));

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
                                        FriendRequestList friendRequestList = (FriendRequestList) object;
                                        MessageDataUtil.setWarningMessageVisibility(friendRequestList, warningMsgTv,
                                                getActivity().getResources().getString(R.string.THERE_IS_NO_PENDING_REQUEST));
                                    }

                                    @Override
                                    public void onFailed(Exception e) {
                                        ErrorSaveHelper.writeErrorToDB(getContext(),PendingRequestsFragment.class.getSimpleName(),
                                                new Object() {
                                                }.getClass().getEnclosingMethod().getName(), e.getMessage());
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
                    ErrorSaveHelper.writeErrorToDB(getContext(),PendingRequestsFragment.class.getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), e.getMessage());
                    DialogBoxUtil.showErrorDialog(getContext(), getActivity().getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {

                        }
                    });
                }
            });
        } catch (Resources.NotFoundException e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),PendingRequestsFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    private void startFollowingInfoProcess(User user, int clickedPosition) {

        try {
            if (mFragmentNavigation != null) {
                UserInfoListItem userInfoListItem = new UserInfoListItem(user);
                userInfoListItem.setAdapter(pendingRequestAdapter);
                userInfoListItem.setClickedPosition(clickedPosition);
                mFragmentNavigation.pushFragment(new OtherProfileFragment(userInfoListItem), ANIMATE_RIGHT_TO_LEFT);
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),PendingRequestsFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }
}
