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
import com.uren.catchu.MainPackage.MainFragments.Profile.ProfileFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters.FollowAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.FollowInfoRowItem;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.FollowInfo;
import catchu.model.FollowInfoResultArrayItem;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.GET_USER_FOLLOWINGS;


public class FollowingFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    FollowInfo followInfo;
    private LinearLayoutManager layoutManager;
    FollowAdapter followAdapter;

    @BindView(R.id.following_recyclerView)
    RecyclerView following_recyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.imgBack)
    ClickableImageView imgBack;
    @BindView(R.id.imgAddFollowing)
    ClickableImageView imgAddFollowing;

    public FollowingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null){
            mView = inflater.inflate(R.layout.profile_subfragment_following, container, false);
            ButterKnife.bind(this, mView);
            init();
            getFollowingList();
        }

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart(){
        if(followAdapter != null)
            getFollowingList();

        super.onStart();
    }

    private void init() {

        imgBack.setOnClickListener(this);
        imgAddFollowing.setOnClickListener(this);
        followInfo = new FollowInfo();
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }


    @Override
    public void onClick(View v) {

        if (v == imgBack) {

            ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
            getActivity().onBackPressed();

        }

        if (v == imgAddFollowing) {
            //following..
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

        followInfo.setRequestType(GET_USER_FOLLOWINGS);
        followInfo.setUserId(AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid());

        FollowInfoProcess followInfoProcess = new FollowInfoProcess(new OnEventListener<FollowInfo>() {
            @Override
            public void onSuccess(FollowInfo resp) {

                Log.i("count ", String.valueOf(resp.getResultArray().size()));

                setUpRecyclerView(resp);

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {

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

        //Takip edilenlerin isFollow degerleri set edilir.
        for(FollowInfoResultArrayItem item:followInfo.getResultArray()){
            item.setIsFollow(true);
        }

        RowItemClickListener rowItemClickListener = new RowItemClickListener() {
            @Override
            public void onClick(View view, FollowInfoResultArrayItem rowItem, int clickedPosition) {
                CommonUtils.showToast(getContext(), "Clicked : " + rowItem.getName());
                startFollowingInfoProcess(rowItem, clickedPosition);
            }

        };


        followAdapter = new FollowAdapter(getActivity(), followInfo.getResultArray(), rowItemClickListener);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        following_recyclerView.setLayoutManager(mLayoutManager);
        following_recyclerView.setAdapter(followAdapter);
    }

    private void startFollowingInfoProcess(FollowInfoResultArrayItem rowItem, int clickedPosition) {

        if (mFragmentNavigation != null) {
            FollowInfoRowItem followInfoRowItem = new FollowInfoRowItem(rowItem);
            followInfoRowItem.setAdapter(followAdapter);
            followInfoRowItem.setClickedPosition(clickedPosition);
            mFragmentNavigation.pushFragment(OtherProfileFragment.newInstance(followInfoRowItem), ANIMATE_RIGHT_TO_LEFT);
        }
    }

}
