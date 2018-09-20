package com.uren.catchu.MainPackage.MainFragments.Feed;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.PostListResponseProcess;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.FeedAdapter;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.BaseRequest;
import catchu.model.Post;
import catchu.model.PostListResponse;
import catchu.model.User;
import catchu.model.UserProfileProperties;


public class FeedFragment extends BaseFragment {

    View mView;
    private LinearLayoutManager layoutManager;
    FeedAdapter feedAdapter;
    UserProfileProperties myProfile;

    @BindView(R.id.feed_recyclerView)
    RecyclerView feed_recyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    //todo : NT degerler current locationdan alınacak..
    String longitude;
    String latitude;
    String radius;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_feed, container, false);
            ButterKnife.bind(this, mView);

            init();
            getPosts();
        }

        return mView;
    }

    private void init() {

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

    }

    private void getPosts() {

        BaseRequest baseRequest = getBaseRequest();
        setLocationInfo();

        PostListResponseProcess postListResponseProcess = new PostListResponseProcess(getContext(), new OnEventListener<PostListResponse>() {
            @Override
            public void onSuccess(PostListResponse postListResponse) {
                Log.i("-> PostListProcess", "successful");
                progressBar.setVisibility(View.GONE);
                setUpRecyclerView(postListResponse);
            }

            @Override
            public void onFailure(Exception e) {
                Log.i("-> PostListProcess", "fail");
                Log.e("error", e.toString());
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onTaskContinue() {
                progressBar.setVisibility(View.VISIBLE);
            }
        }, baseRequest, longitude, latitude, radius);

        postListResponseProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void setUpRecyclerView(PostListResponse postListResponse) {

        Log.i("postCount ", String.valueOf(postListResponse.getItems().size()));

        ArrayList<Post> postList = new ArrayList<Post>();
        for(int i=0; i< postListResponse.getItems().size(); i++ ){
            postList.add(postListResponse.getItems().get(i));
            postList.add(postListResponse.getItems().get(i));
            postList.add(postListResponse.getItems().get(i));
            postList.add(postListResponse.getItems().get(i));
            postList.add(postListResponse.getItems().get(i));
            postList.add(postListResponse.getItems().get(i));
            postList.add(postListResponse.getItems().get(i));
        }

        feedAdapter = new FeedAdapter(getActivity(), postList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        feed_recyclerView.setLayoutManager(mLayoutManager);
        feed_recyclerView.setAdapter(feedAdapter);


    }

    private BaseRequest getBaseRequest() {

        BaseRequest baseRequest = new BaseRequest();
        User user = new User();
        user.setUserid(AccountHolderInfo.getUserID());
        baseRequest.setUser(user);

        return baseRequest;
    }

    private void setLocationInfo() {

        longitude = "29.0328831";
        latitude = "41.1077839";
        radius = "0.1";

    }

}
