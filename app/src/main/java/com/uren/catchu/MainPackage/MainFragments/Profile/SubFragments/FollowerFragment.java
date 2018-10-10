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
import android.widget.ProgressBar;

import com.uren.catchu.ApiGatewayFunctions.FollowInfoProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.RowItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters.FollowAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.FollowInfoRowItem;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.FollowInfo;
import catchu.model.FollowInfoResultArrayItem;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.GET_USER_FOLLOWERS;


public class FollowerFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    FollowInfo followInfo;
    FollowAdapter followAdapter;

    @BindView(R.id.follower_recyclerView)
    RecyclerView follower_recyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.imgBack)
    ClickableImageView imgBack;
    @BindView(R.id.imgAddFollower)
    ClickableImageView imgAddFollower;


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

        imgBack.setOnClickListener(this);
        imgAddFollower.setOnClickListener(this);

        followInfo = new FollowInfo();

    }


    @Override
    public void onClick(View v) {

        if (v == imgBack) {

            ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
            getActivity().onBackPressed();

        }

        if (v == imgAddFollower) {
            //follower...
        }

    }

    private void getFollowerList() {

        followInfo.setRequestType(GET_USER_FOLLOWERS);
        followInfo.setUserId(AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid());

        AccountHolderInfo.getToken(new TokenCallback() {

            @Override
            public void onTokenTaken(String token) {
                startFollowInfoProcess(token);
            }

        });

    }

    private void startFollowInfoProcess(String token) {

        FollowInfoProcess followInfoProcess = new FollowInfoProcess(new OnEventListener<FollowInfo>() {
            @Override
            public void onSuccess(FollowInfo resp) {

                if(resp == null){
                    Log.i("-> getFollowerList ", "FAIL");
                }else{
                    Log.i("-> getFollowerList ", "OK");
                    setUpRecyclerView(resp);
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                Log.i("-> getFollowerList ", "FAIL");
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onTaskContinue() {
                progressBar.setVisibility(View.VISIBLE);
            }
        }, followInfo, token);

        followInfoProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void setUpRecyclerView(FollowInfo followInfo) {

        RowItemClickListener rowItemClickListener = new RowItemClickListener() {
            @Override
            public void onClick(View view, FollowInfoResultArrayItem rowItem, int clickedPosition) {
                CommonUtils.showToast(getContext(), "Clicked : " + rowItem.getName());
                startFollowerInfoProcess(rowItem, clickedPosition);
            }
        };

        followAdapter = new FollowAdapter(getActivity(), followInfo.getResultArray(), rowItemClickListener);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        follower_recyclerView.setLayoutManager(mLayoutManager);
        follower_recyclerView.setAdapter(followAdapter);

    }

    private void startFollowerInfoProcess(FollowInfoResultArrayItem rowItem, int clickedPosition) {

        if (mFragmentNavigation != null) {

            FollowInfoRowItem followInfoRowItem = new FollowInfoRowItem(rowItem);
            followInfoRowItem.setAdapter(followAdapter);
            followInfoRowItem.setClickedPosition(clickedPosition);

            mFragmentNavigation.pushFragment(OtherProfileFragment.newInstance(followInfoRowItem), ANIMATE_RIGHT_TO_LEFT);

            //mFragmentNavigation.pushFragment(new UserEditFragment());

        }




    }


}
