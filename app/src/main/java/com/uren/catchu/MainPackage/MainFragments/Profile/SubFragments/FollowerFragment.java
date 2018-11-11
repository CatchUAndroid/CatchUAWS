package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uren.catchu.ApiGatewayFunctions.FollowInfoProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters.FollowAdapter;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.FollowInfoListResponse;
import catchu.model.User;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.GET_USER_FOLLOWERS;


public class FollowerFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    FollowAdapter followAdapter;

    @BindView(R.id.follower_recyclerView)
    RecyclerView follower_recyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.commonToolbarbackImgv)
    ImageView commonToolbarbackImgv;

    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;

    public FollowerFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(mView == null){
            mView = inflater.inflate(R.layout.profile_subfragment_followers, container, false);
            ButterKnife.bind(this, mView);

            init();
            getFollowerList();
        }

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void init() {
        commonToolbarbackImgv.setOnClickListener(this);
        toolbarTitleTv.setText(getContext().getResources().getString(R.string.followers));
    }

    @Override
    public void onClick(View v) {

        if (v == commonToolbarbackImgv) {
            ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
            getActivity().onBackPressed();
        }
    }

    private void getFollowerList() {

        AccountHolderInfo.getToken(new TokenCallback() {

            @Override
            public void onTokenTaken(String token) {
                startFollowInfoProcess(token);
            }

        });
    }

    private void startFollowInfoProcess(String token) {

        String userId = AccountHolderInfo.getUserID();
        String requestType = GET_USER_FOLLOWERS;

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
        }, userId, requestType, token);

        followInfoProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void setUpRecyclerView(FollowInfoListResponse followInfoListResponse) {

        ListItemClickListener listItemClickListener = new ListItemClickListener() {
            @Override
            public void onClick(View view, User user, int clickedPosition) {
                CommonUtils.showToast(getContext(), "Clicked : " + user.getName());
                startFollowerInfoProcess(user, clickedPosition);
            }
        };

        if(getActivity() != null) {
            followAdapter = new FollowAdapter(getContext(), followInfoListResponse, listItemClickListener);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            follower_recyclerView.setLayoutManager(mLayoutManager);
            follower_recyclerView.setAdapter(followAdapter);
        }
    }

    private void startFollowerInfoProcess(User user, int clickedPosition) {

        if (mFragmentNavigation != null) {

            UserInfoListItem userInfoListItem = new UserInfoListItem(user);
            userInfoListItem.setAdapter(followAdapter);
            userInfoListItem.setClickedPosition(clickedPosition);
            mFragmentNavigation.pushFragment(OtherProfileFragment.newInstance(userInfoListItem), ANIMATE_RIGHT_TO_LEFT);
        }
    }
}
