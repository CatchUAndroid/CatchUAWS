package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uren.catchu.ApiGatewayFunctions.FollowInfoProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;

import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.OtherProfile.OtherProfileFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters.FollowingAdapter;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.FollowInfoListResponse;
import catchu.model.User;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_FOLLOWING;
import static com.uren.catchu.Constants.StringConstants.GET_USER_FOLLOWINGS;


@SuppressLint("ValidFragment")
public class FollowingFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    private LinearLayoutManager mLayoutManager;
    private FollowingAdapter followingAdapter;
    private String requestedUserId, perPage, page;

    @BindView(R.id.following_recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.commonToolbarbackImgv)
    ClickableImageView commonToolbarbackImgv;

    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;

    public FollowingFragment (String requestedUserId) {
        this.requestedUserId = requestedUserId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.profile_subfragment_following, container, false);
            ButterKnife.bind(this, mView);
            init();
            initRecyclerView();
            getFollowingList();
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
    }

    private void init() {
        commonToolbarbackImgv.setOnClickListener(this);
        toolbarTitleTv.setText(getContext().getResources().getString(R.string.followings));
    }

    private void initRecyclerView() {
        setLayoutManager();
        setAdapter();
    }

    private void setLayoutManager() {
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void setAdapter() {
        followingAdapter = new FollowingAdapter(getContext());
        recyclerView.setAdapter(followingAdapter);

        followingAdapter.setListItemClickListener(new ListItemClickListener() {
            @Override
            public void onClick(View view, User user, int clickedPosition) {
                startFollowingInfoProcess(user, clickedPosition);
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v == commonToolbarbackImgv) {
            getActivity().onBackPressed();
        }
    }

    private void getFollowingList() {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetFollowingList(token);
            }
        });
    }

    private void startGetFollowingList(String token) {
        String userId = AccountHolderInfo.getUserID();
        String requestType = GET_USER_FOLLOWINGS;
        String requestedUserId = this.requestedUserId;
        String perPage = "";
        String page = "";

        FollowInfoProcess followInfoProcess = new FollowInfoProcess(new OnEventListener<FollowInfoListResponse>() {
            @Override
            public void onSuccess(FollowInfoListResponse followInfoListResponse) {

                if (followInfoListResponse == null) {
                    CommonUtils.LOG_OK_BUT_NULL("FollowInfoProcess");
                } else {
                    CommonUtils.LOG_OK("FollowInfoProcess");
                    setUpRecyclerView(followInfoListResponse);
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.LOG_FAIL("FollowInfoProcess", e.toString());
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onTaskContinue() {
                progressBar.setVisibility(View.VISIBLE);
            }
        }, userId, requestedUserId, requestType, perPage, page, token);

        followInfoProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void setUpRecyclerView(FollowInfoListResponse followInfoListResponse) {

        //Takip edilenlerin isFollow degerleri set edilir.
        /*
        for(User item:followInfoListResponse.getItems()){
            item.setFollowStatus(FOLLOW_STATUS_FOLLOWING);
        }
        */

        if (getActivity() != null) {
            followingAdapter.addAll(followInfoListResponse.getItems());
        }
    }

    private void startFollowingInfoProcess(User user, int clickedPosition) {

        if (mFragmentNavigation != null) {
            UserInfoListItem userInfoListItem = new UserInfoListItem(user);
            userInfoListItem.setAdapter(followingAdapter);
            userInfoListItem.setClickedPosition(clickedPosition);
            mFragmentNavigation.pushFragment(new OtherProfileFragment(userInfoListItem), ANIMATE_RIGHT_TO_LEFT);
        }
    }

}
