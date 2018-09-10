package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.uren.catchu.ApiGatewayFunctions.FollowInfoProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;

import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters.FollowAdapter;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.FollowInfo;
import catchu.model.FollowInfoResultArrayItem;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.uren.catchu.Constants.StringConstants.AnimateLeftToRight;
import static com.uren.catchu.Constants.StringConstants.GET_USER_FOLLOWINGS;


public class FollowingFragment extends Fragment
        implements View.OnClickListener {

    View mView;
    FollowInfo followInfo;
    private LinearLayoutManager layoutManager;

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
        mView = inflater.inflate(R.layout.profile_subfragment_following, container, false);
        ButterKnife.bind(this, mView);

        init();

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getFollowingList();

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

            ((NextActivity) getActivity()).ANIMATION_TAG = AnimateLeftToRight;
            getActivity().onBackPressed();

        }

        if (v == imgAddFollowing) {
            //following..
        }

    }

    private void getFollowingList() {

        followInfo.setRequestType(GET_USER_FOLLOWINGS);
        followInfo.setUserId(AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid());

        FollowInfoProcess followInfoProcess = new FollowInfoProcess(getActivity(), new OnEventListener<FollowInfo>() {
            @Override
            public void onSuccess(FollowInfo resp) {

                Log.i("count ", String.valueOf(resp.getResultArray().size()));


                for (int i = 0; i < resp.getResultArray().size(); i++) {
                    int a = i + 1;
                    //Log.i("following-" + a + " :", resp.getResultArray().get(i));

                }

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
        }, followInfo);

        followInfoProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }

    private void setUpRecyclerView(FollowInfo followInfo) {

        //Takip edilenlerin isFollow degerleri set edilir.
        for(FollowInfoResultArrayItem item:followInfo.getResultArray()){
            item.setIsFollow(true);
        }


        FollowAdapter followAdapter = new FollowAdapter(getActivity(), followInfo.getResultArray());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        following_recyclerView.setLayoutManager(mLayoutManager);
        following_recyclerView.setAdapter(followAdapter);




    }


}
