package com.uren.catchu.MainPackage.MainFragments.Feed;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.PostListResponseProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.FeedAdapter;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.VideoPlay.CustomRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.BaseRequest;
import catchu.model.Media;
import catchu.model.Post;
import catchu.model.PostListResponse;
import catchu.model.User;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.IMAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.VIDEO_TYPE;


public class FeedFragment extends BaseFragment {

    View mView;
    private LinearLayoutManager layoutManager;
    FeedAdapter feedAdapter;
    UserProfileProperties myProfile;

    @BindView(R.id.rv_feed)
    CustomRecyclerView recyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.txtNoFeed)
    TextView txtNoFeed;

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

            CommonUtils.LOG_NEREDEYIZ("FeedFragment");

            init();
            getPosts();

        }

        return mView;
    }

    private void init() {

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

    }

    private void getPosts() {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetPosts(token);
            }
        });

    }

    private void startGetPosts(String token) {

        BaseRequest baseRequest = getBaseRequest();
        setLocationInfo();

        PostListResponseProcess postListResponseProcess = new PostListResponseProcess(getContext(), new OnEventListener<PostListResponse>() {
            @Override
            public void onSuccess(PostListResponse postListResponse) {

                if(postListResponse == null){
                    Log.i("**PostListResponseProce", "SERVER:OK BUT DATA:NULL");
                    setTextNoFeedVisible(true);
                }else{
                    Log.i("**PostListResponseProce", "OK");
                    setTextNoFeedVisible(false);
                    setUpRecyclerView(postListResponse);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                Log.i("**PostListResponseProce", "FAIL - " + e.toString());
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onTaskContinue() {
                progressBar.setVisibility(View.VISIBLE);
            }
        }, baseRequest, longitude, latitude, radius, token);

        postListResponseProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void setTextNoFeedVisible(boolean setVisible) {
        if(setVisible){
            txtNoFeed.setVisibility(View.VISIBLE);
        }else{
            txtNoFeed.setVisibility(View.GONE);
        }
    }

    private void setUpRecyclerView(PostListResponse postListResponse) {

        //Log.i("postCount ", String.valueOf(postListResponse.getItems().size()));

        ArrayList<Post> postList = setJunkData();

        feedAdapter = new FeedAdapter(getActivity(), getContext(), postList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        setRecyclerViewProperties(postList);

    }

    private void setRecyclerViewProperties(ArrayList<Post> postList) {
        //todo before setAdapter
        recyclerView.setActivity(getActivity());

        //optional - to play only first visible video
        recyclerView.setPlayOnlyFirstVideo(true); // false by default

        //optional - by default we check if url ends with ".mp4". If your urls do not end with mp4, you can set this param to false and implement your own check to see if video points to url
        recyclerView.setCheckForMp4(false); //true by default

        //optional - download videos to local storage (requires "android.permission.WRITE_EXTERNAL_STORAGE" in manifest or ask in runtime)
        recyclerView.setDownloadPath(Environment.getExternalStorageDirectory() + "/MyVideo"); // (Environment.getExternalStorageDirectory() + "/NT_Video") by default

        recyclerView.setDownloadVideos(true); // false by default

        recyclerView.setVisiblePercent(50); // percentage of View that needs to be visible to start playing

        //extra - start downloading all videos in background before loading RecyclerView
        List<String> urls = new ArrayList<>();
        for (int i = 0; i < postList.size(); i++) {
            for(int j = 0; j< postList.get(i).getAttachments().size(); j++){
                Media media = postList.get(i).getAttachments().get(j);
                urls.add(media.getUrl());
            }
        }

        recyclerView.preDownload(urls);

        recyclerView.setAdapter(feedAdapter);
        //call this functions when u want to start autoplay on loading async lists (eg firebase)
        recyclerView.smoothScrollBy(0, 1);
        recyclerView.smoothScrollBy(0, -1);
        //recyclerView.setItemViewCacheSize(mediaList.size());

    }

    private ArrayList<Post> setJunkData() {

        //Video
        Media media1 = new Media();
        media1.setUrl("http://res.cloudinary.com/krupen/video/upload/w_300,h_150,c_crop,q_70/v1491561340/hello_cuwgcb.mp4");
        media1.setType(VIDEO_TYPE);

        //Image
        Media media2 = new Media();
        media2.setUrl("https://i.hizliresim.com/mo94Vy.png");
        media2.setType(IMAGE_TYPE);

        List<Media> mediaList = new ArrayList<Media>();
        mediaList.add(media1);
        mediaList.add(media2);

        Post post = new Post();
        post.setAttachments(mediaList);

        ArrayList<Post> postList = new ArrayList<Post>();

        for (int i = 0; i < 100; i++) {
            postList.add(post);
        }

        return postList;

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
