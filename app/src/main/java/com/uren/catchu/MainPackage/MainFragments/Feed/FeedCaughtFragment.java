package com.uren.catchu.MainPackage.MainFragments.Feed;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.Adapters.LocationTrackerAdapter;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.PostListResponseProcess;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.InfoActivity;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.InfoDialogBoxCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.FeedAdapter;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.FeedRefreshCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.FeedContextMenuManager;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.FeedItemAnimator;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.LocationCallback;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.GeneralUtils.TransitionHelper;
import com.uren.catchu._Libraries.LayoutManager.CustomLinearLayoutManager;
import com.uren.catchu._Libraries.VideoPlay.CustomRecyclerView;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.Media;
import catchu.model.Post;
import catchu.model.PostListResponse;

import static com.uren.catchu.Constants.NumericConstants.DEFAULT_FEED_PAGE_COUNT;
import static com.uren.catchu.Constants.NumericConstants.DEFAULT_FEED_PERPAGE_COUNT;
import static com.uren.catchu.Constants.NumericConstants.FILTERED_FEED_RADIUS;
import static com.uren.catchu.Constants.NumericConstants.VIEW_LOCATION_PERMISSION;
import static com.uren.catchu.Constants.NumericConstants.VIEW_LOCATION_SERVICE_ERROR;
import static com.uren.catchu.Constants.NumericConstants.VIEW_NO_POST_FOUND;
import static com.uren.catchu.Constants.NumericConstants.VIEW_RETRY;
import static com.uren.catchu.Constants.NumericConstants.VIEW_SERVER_ERROR;
import static com.uren.catchu.Constants.StringConstants.AWS_EMPTY;
import static com.uren.catchu.Constants.StringConstants.FEED_TYPE_CATCH;


public class FeedCaughtFragment extends BaseFragment implements View.OnClickListener {

    View mView;
    FeedAdapter feedAdapter;
    CustomLinearLayoutManager mLayoutManager;

    @BindView(R.id.rv_feed)
    CustomRecyclerView recyclerView;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refresh_layout;

    @BindView(R.id.loadingView)
    AVLoadingIndicatorView loadingView;

    @BindView(R.id.mainExceptionLayout)
    RelativeLayout mainExceptionLayout;
    @BindView(R.id.noPostFoundLayout)
    LinearLayout noPostFoundLayout;
    @BindView(R.id.retryLayout)
    LinearLayout retryLayout;
    @BindView(R.id.locationServiceError)
    LinearLayout locationServiceError;
    @BindView(R.id.needLocationPermission)
    LinearLayout needLocationPermission;
    @BindView(R.id.serverError)
    LinearLayout serverError;

    @BindView(R.id.imgRetry)
    ClickableImageView imgRetry;

    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private int perPageCnt;
    private int pageCnt;
    private List<Post> postList = new ArrayList<Post>();
    private static final int RECYCLER_VIEW_CACHE_COUNT = 10;
    private boolean pulledToRefresh = false;
    private boolean isFirstFetch = false;

    //Location
    private LocationTrackerAdapter locationTrackObj;
    PermissionModule permissionModule;
    String longitude;
    String latitude;
    String radius;

    private boolean hasLoadedOnce = false; // your boolean field

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_feed_caught, container, false);
            ButterKnife.bind(this, mView);

            loadingView.show();
        }

        return mView;
    }


    @Override
    public void setUserVisibleHint(boolean isFragmentVisible) {
        super.setUserVisibleHint(true);

        if (this.isVisible()) {
            // we check that the fragment is becoming visible
            if (isFragmentVisible && !hasLoadedOnce) {
                loadData();
                hasLoadedOnce = true;
            }
        }
    }

    private void loadData() {
        CommonUtils.LOG_NEREDEYIZ("FeedFragment");
        initListeners();
        initRecyclerView();
        checkLocationAndRetrievePosts();
    }


    private void initListeners() {
    }

    private void initRecyclerView() {

        isFirstFetch = true;
        mainExceptionLayout.setVisibility(View.GONE);
        setLayoutManager();
        setAdapter();
        setRecyclerViewProperties();
        setPullToRefresh();
        setRecyclerViewScroll();
        setPaginationValues();
        setFeedRefreshListener();

    }

    private void setFeedRefreshListener() {
        PostHelper.FeedRefresh.getInstance().setFeedRefreshCallback(new FeedRefreshCallback() {
            @Override
            public void onFeedRefresh() {
                CommonUtils.showToastShort(getContext(), "Feed - Caught refreshing..");
                Log.i("--> FilteredRa", String.valueOf(FILTERED_FEED_RADIUS));
                refreshFeed();
            }
        });
    }

    private void setLayoutManager() {
        mLayoutManager = new CustomLinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new FeedItemAnimator());
    }

    private void setAdapter() {
        feedAdapter = new FeedAdapter(getActivity(), getContext(), mFragmentNavigation);
        recyclerView.setAdapter(feedAdapter);
    }

    private void setPullToRefresh() {
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFeed();
            }
        });
    }

    private void refreshFeed(){
        pulledToRefresh = true;
        setPaginationValues();
        checkLocationAndRetrievePosts();
    }

    private void setPaginationValues() {
        perPageCnt = DEFAULT_FEED_PERPAGE_COUNT;
        pageCnt = DEFAULT_FEED_PAGE_COUNT;
        float radiusInKm = (float) ((double) FILTERED_FEED_RADIUS / (double) 1000);
        Log.i("radiusInKm", String.valueOf(radiusInKm));
        radius = String.valueOf(radiusInKm);
    }


    private void setRecyclerViewScroll() {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);

                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {

                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
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

        if (!locationTrackObj.canGetLocation()) {
            //gps ve network provider olup olmadığı kontrol edilir
            showExceptionLayout(true, VIEW_RETRY);

            final int TYPE_XML = 1;
            Intent i = new Intent(getActivity(), InfoActivity.class);
            i.putExtra("EXTRA_TYPE", TYPE_XML);
            transitionTo(i);

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

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

    @SuppressWarnings("unchecked")
    void transitionTo(Intent i) {
        final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(getActivity(), false);
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), pairs);
        startActivity(i, transitionActivityOptions.toBundle());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionModule.PERMISSION_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    getPosts();

                } else {
                    // permission denied, boo! Disable the
                    showExceptionLayout(true, VIEW_LOCATION_PERMISSION);
                }

            }

            // other 'case' lines to check for other
            // permissions this app might request

        }

    }

    private void getPosts() {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(final String token) {
                Location location = locationTrackObj.getLocation();
                if (location != null) {
                    startGetPosts(token);
                } else {
                    showExceptionLayout(true, VIEW_LOCATION_SERVICE_ERROR);
                }
            }

            @Override
            public void onTokenFail(String message) {
                refresh_layout.setRefreshing(false);
                loadingView.hide();
            }
        });
    }

    private void startGetPosts(String token) {

        setLocationInfo();

        String sUserId = AccountHolderInfo.getUserID();
        String sPostId = AWS_EMPTY;
        String sCatchType = FEED_TYPE_CATCH;
        String sLongitude = longitude;
        String sLatitude = latitude;
        String sRadius = radius;
        String sPerpage = String.valueOf(perPageCnt);
        String sPage = String.valueOf(pageCnt);

        PostListResponseProcess postListResponseProcess = new PostListResponseProcess(getContext(), new OnEventListener<PostListResponse>() {
            @Override
            public void onSuccess(final PostListResponse postListResponse) {
                setFetchData(postListResponse);
            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.LOG_FAIL("PostListResponseProcess", e.toString());
                loadingView.hide();
                refresh_layout.setRefreshing(false);

                if (postList.size() > 0) {
                    DialogBoxUtil.showErrorDialog(getContext(), getContext().getResources().getString(R.string.serverError), new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {

                        }
                    });
                    showExceptionLayout(false,-1);
                    if (feedAdapter.isShowingProgressLoading()) {
                        feedAdapter.removeProgressLoading();
                    }

                } else {
                    showExceptionLayout(true, VIEW_SERVER_ERROR);
                }
            }

            @Override
            public void onTaskContinue() {
            }

        }, sUserId, sPostId, sCatchType, sLongitude, sLatitude, sRadius, sPerpage, sPage, token);

        postListResponseProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void setFetchData(PostListResponse postListResponse) {

        if(isFirstFetch){
            isFirstFetch=false;
            loadingView.smoothToHide();
        }

        if (postListResponse == null) {
            CommonUtils.LOG_OK_BUT_NULL("PostListResponseProcess");
        } else {
            CommonUtils.LOG_OK("PostListResponseProcess");
            if (postListResponse.getItems().size() == 0 && pageCnt == 1) {
                showExceptionLayout(true, VIEW_NO_POST_FOUND);
            } else {
                showExceptionLayout(false, -1);
            }
            setUpRecyclerView(postListResponse);
        }

        refresh_layout.setRefreshing(false);

    }


    private void setUpRecyclerView(PostListResponse postListResponse) {

        loading = true;
        postList.addAll(postListResponse.getItems());
        preDownloadUrls(postListResponse.getItems());

        if (pageCnt != 1) {
            feedAdapter.removeProgressLoading();
        }

        if (pulledToRefresh) {
            feedAdapter.updatePostListItems(postListResponse.getItems());
            pulledToRefresh = false;
        } else {
            feedAdapter.addAll(postListResponse.getItems());
        }

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

    private void setRecyclerViewProperties() {

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

    private void setLocationInfo() {
        longitude = String.valueOf(locationTrackObj.getLocation().getLongitude());
        latitude = String.valueOf(locationTrackObj.getLocation().getLatitude());
    }

    public void scrollRecViewInitPosition() {
        mLayoutManager.smoothScrollToPosition(recyclerView, null, 0);
    }

    @Override
    public void onClick(View view) {
    }

    /**********************************************/
    private void showExceptionLayout(boolean showException, int viewType) {

        if (showException) {

            refresh_layout.setRefreshing(false);
            loadingView.hide();
            mainExceptionLayout.setVisibility(View.VISIBLE);
            retryLayout.setVisibility(View.GONE);
            noPostFoundLayout.setVisibility(View.GONE);
            locationServiceError.setVisibility(View.GONE);
            needLocationPermission.setVisibility(View.GONE);
            serverError.setVisibility(View.GONE);

            if (viewType == VIEW_RETRY) {
                imgRetry.setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), android.graphics.PorterDuff.Mode.SRC_IN);
                retryLayout.setVisibility(View.VISIBLE);
            } else if (viewType == VIEW_NO_POST_FOUND) {
                noPostFoundLayout.setVisibility(View.VISIBLE);
            } else if (viewType == VIEW_LOCATION_SERVICE_ERROR) {
                locationServiceError.setVisibility(View.VISIBLE);
            }else if(viewType == VIEW_LOCATION_PERMISSION){
                needLocationPermission.setVisibility(View.VISIBLE);
            }else if(viewType == VIEW_SERVER_ERROR){
                serverError.setVisibility(View.VISIBLE);
            }

        } else {
            mainExceptionLayout.setVisibility(View.GONE);
        }

    }

}