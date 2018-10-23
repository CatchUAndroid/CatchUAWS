package com.uren.catchu.MainPackage.MainFragments.Feed;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.dinuscxj.refresh.RecyclerRefreshLayout.OnRefreshListener;
import com.uren.catchu.Adapters.LocationTrackerAdapter;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.PostListResponseProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.FeedAdapter;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.GalleryPicker.Interfaces.LocationCallback;
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

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.uren.catchu.Constants.StringConstants.IMAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.VIDEO_TYPE;


public class FeedFragment extends BaseFragment {

    View mView;
    FeedAdapter feedAdapter;
    LinearLayoutManager mLayoutManager;
    UserProfileProperties myProfile;

    @BindView(R.id.rv_feed)
    CustomRecyclerView recyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.txtNoFeed)
    TextView txtNoFeed;

    @BindView(R.id.refresh_layout)
    RecyclerRefreshLayout refresh_layout;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int perPageCnt = 3;
    private int pageCnt = 1;
    List<Post> postList = new ArrayList<Post>();
    private static final int RECYCLER_VIEW_CACHE_COUNT = 10;

    //todo : NT degerler current locationdan alınacak..
    private LocationTrackerAdapter locationTrackObj;
    PermissionModule permissionModule;
    String longitude;
    String latitude;
    String radius;
    // The minimum distance to change Updates in meters
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 1 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000; // 1 sec

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
            checkLocationAndRetrievePosts();
            //getPosts();

        }

        return mView;
    }

    private void init() {

        setLayoutManager();
        setAdapter();
        setRecyclerViewProperties();
        setPullToRefresh();
        setRecyclerViewScroll();

    }

    private void setLayoutManager() {
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
    }
    private void setAdapter() {
        feedAdapter = new FeedAdapter(getActivity(), getContext(), mFragmentNavigation);
        recyclerView.setAdapter(feedAdapter);
    }
    private void setPullToRefresh() {
        refresh_layout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkLocationAndRetrievePosts();
            }
        });
    }


    private void setRecyclerViewScroll() {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    Log.i("visibleItemCount", String.valueOf(visibleItemCount));
                    Log.i("totalItemCount", String.valueOf(totalItemCount));
                    Log.i("pastVisiblesItems", String.valueOf(pastVisiblesItems));
                    Log.i("loading", String.valueOf(loading));

                    if (loading) {

                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            Log.v("...", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data
                            pageCnt++;
                            feedAdapter.addProgressLoading();
                            getPosts();

                        }
                    }
                }
            }
        });

    }

    private void checkLocationAndRetrievePosts() {
        permissionModule = new PermissionModule(getContext());
        initLocationTracker();
        checkCanGetLocation();
    }

    private void initLocationTracker() {
        locationTrackObj = new LocationTrackerAdapter(getContext(), new LocationCallback() {
            @Override
            public void onLocationChanged(Location location) {
            }
        });
    }

    private void checkCanGetLocation() {

        if (!locationTrackObj.canGetLocation())
            //gps ve network provider olup olmadığı kontrol edilir
            DialogBoxUtil.showSettingsAlert(getActivity());
        else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (permissionModule.checkAccessFineLocationPermission()) {
                    getPosts();
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PermissionModule.PERMISSION_ACCESS_FINE_LOCATION);
                }
            } else {
                getPosts();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionModule.PERMISSION_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(getApplicationContext(), " ACCESS_FINE_LOCATION - Permission granted", Toast.LENGTH_SHORT).show();
                    getPosts();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    setTextNoFeedVisible(true, R.string.needLocationPermission);
                }

            }

            // other 'case' lines to check for other
            // permissions this app might request

        }

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
        String perpage = String.valueOf(perPageCnt);
        String page = String.valueOf(pageCnt);

        PostListResponseProcess postListResponseProcess = new PostListResponseProcess(getContext(), new OnEventListener<PostListResponse>() {
            @Override
            public void onSuccess(PostListResponse postListResponse) {

                if (postListResponse == null) {
                    CommonUtils.LOG_OK_BUT_NULL("PostListResponseProcess");

                } else {
                    CommonUtils.LOG_OK("PostListResponseProcess");
                    if (postListResponse.getItems().size() == 0 && pageCnt == 1) {
                        setTextNoFeedVisible(true, R.string.emptyFeed);
                    } else {
                        setTextNoFeedVisible(false, 0);
                    }
                    setUpRecyclerView(postListResponse);
                }

                progressBar.setVisibility(View.GONE);
                refresh_layout.setRefreshing(false);

            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.LOG_FAIL("PostListResponseProcess", e.toString());
                progressBar.setVisibility(View.GONE);
                refresh_layout.setRefreshing(false);
            }

            @Override
            public void onTaskContinue() {

                if (pageCnt == 1) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        }, baseRequest, longitude, latitude, radius, perpage, page, token);

        postListResponseProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void setTextNoFeedVisible(boolean setVisible, int textDetail) {
        if (setVisible) {
            txtNoFeed.setVisibility(View.VISIBLE);
            txtNoFeed.setText(textDetail);
        } else {
            txtNoFeed.setVisibility(View.GONE);
        }
    }

    private void setUpRecyclerView(PostListResponse postListResponse) {

        loading = true;
        preDownloadUrls(postListResponse.getItems());

        if (pageCnt != 1) {
            feedAdapter.removeProgressLoading();
        }

        for (int i = 0; i < postListResponse.getItems().size(); i++) {
            postList.add(postListResponse.getItems().get(i));
        }
        //postList=setJunkData();
        //logPostId(postList); //todo NT - silinecek
        feedAdapter.addAll(postListResponse.getItems());

    }

    private void preDownloadUrls(List<Post> items) {

        //extra - start downloading all videos in background before loading RecyclerView
        List<String> urls = new ArrayList<>();
        int postNum;
        for (int i = 0; i < postList.size(); i++) {
            postNum = i + 1;
            Log.i("=== Post-" + postNum + " :", postList.get(i).getPostid() + " === ATTACHMENT URLS");
            for (int j = 0; j < postList.get(i).getAttachments().size(); j++) {
                Media media = postList.get(i).getAttachments().get(j);
                urls.add(media.getUrl());
                Log.i("url", media.getUrl());
            }
        }

        recyclerView.preDownload(urls);

    }

    //todo NT - silinecek
    private void logPostId(List<Post> postList) {
        int postNumber;
        for (int i = 0; i < postList.size(); i++) {
            postNumber = i + 1;
            if (postList.get(i).getPostid() != null && !postList.get(i).getPostid().isEmpty()) {
                Log.i("post-" + postNumber + " :", postList.get(i).getPostid());
            }
        }
    }


    private void setRecyclerViewProperties() {

        //todo before setAdapter
        recyclerView.setActivity(getActivity());

        //optional - to play only first visible video
        recyclerView.setPlayOnlyFirstVideo(false); // false by default

        //optional - by default we check if url ends with ".mp4". If your urls do not end with mp4, you can set this param to false and implement your own check to see if video points to url
        recyclerView.setCheckForMp4(false); //true by default

        //optional - download videos to local storage (requires "android.permission.WRITE_EXTERNAL_STORAGE" in manifest or ask in runtime)
        recyclerView.setDownloadPath(Environment.getExternalStorageDirectory() + "/MyVideo"); // (Environment.getExternalStorageDirectory() + "/NT_Video") by default

        recyclerView.setDownloadVideos(false); // false by default

        recyclerView.setVisiblePercent(50); // percentage of View that needs to be visible to start playing

        //call this functions when u want to start autoplay on loading async lists (eg firebase)
        recyclerView.smoothScrollBy(0, 1);
        recyclerView.smoothScrollBy(0, -1);
        recyclerView.setItemViewCacheSize(RECYCLER_VIEW_CACHE_COUNT);

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

        User user = new User();
        user.setName("JunkUser");
        user.setUsername("junkUser");

        Post post = new Post();
        post.setAttachments(mediaList);
        post.setUser(user);
        post.setLikeCount(20);
        post.setCommentCount(20);
        post.setIsLiked(true);

        ArrayList<Post> postList = new ArrayList<Post>();

        for (int i = 0; i < 1; i++) {
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

        longitude = String.valueOf(locationTrackObj.getLocation().getLongitude());
        latitude = String.valueOf(locationTrackObj.getLocation().getLatitude());
        radius = "10";

    }


}