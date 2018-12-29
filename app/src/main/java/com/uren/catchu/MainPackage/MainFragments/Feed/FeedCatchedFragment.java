package com.uren.catchu.MainPackage.MainFragments.Feed;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.dinuscxj.refresh.RecyclerRefreshLayout.OnRefreshListener;
import com.uren.catchu.Adapters.LocationTrackerAdapter;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.PostListResponseProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
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
import com.uren.catchu.Singleton.Interfaces.AccountHolderInfoCallback;
import com.uren.catchu._Libraries.LayoutManager.CustomLinearLayoutManager;
import com.uren.catchu._Libraries.PulseView.PulsatorLayout;
import com.uren.catchu._Libraries.VideoPlay.CustomRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.Media;
import catchu.model.Post;
import catchu.model.PostListResponse;
import catchu.model.User;
import catchu.model.UserProfile;

import static com.uren.catchu.Constants.NumericConstants.DEFAULT_FEED_PAGE_COUNT;
import static com.uren.catchu.Constants.NumericConstants.DEFAULT_FEED_PERPAGE_COUNT;
import static com.uren.catchu.Constants.NumericConstants.FILTERED_FEED_RADIUS;
import static com.uren.catchu.Constants.StringConstants.AWS_EMPTY;
import static com.uren.catchu.Constants.StringConstants.FEED_TYPE_CATCH;
import static com.uren.catchu.Constants.StringConstants.IMAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.VIDEO_TYPE;


public class FeedCatchedFragment extends BaseFragment implements View.OnClickListener {

    View mView;
    FeedAdapter feedAdapter;
    CustomLinearLayoutManager mLayoutManager;

    @BindView(R.id.rv_feed)
    CustomRecyclerView recyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.rl_no_feed)
    RelativeLayout rl_no_feed;

    @BindView(R.id.txtNoFeedExplanation)
    TextView txtNoFeedExplanation;

    @BindView(R.id.rl_pulsator)
    RelativeLayout rl_pulsator;

    @BindView(R.id.pulsator)
    PulsatorLayout mPulsator;

    @BindView(R.id.refresh_layout)
    RecyclerRefreshLayout refresh_layout;

    @BindView(R.id.imgProfile)
    ImageView imgProfile;
    @BindView(R.id.txtProfile)
    TextView txtProfile;

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
            mView = inflater.inflate(R.layout.fragment_feed_catched, container, false);
            ButterKnife.bind(this, mView);
        }

        if (!mPulsator.isStarted()) {
            mPulsator.reset();
            mPulsator.start();
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

        showPulsatorLayout(true);

        isFirstFetch = true;
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
                pulledToRefresh = true;
                Log.i("--> FilteredRa", String.valueOf(FILTERED_FEED_RADIUS));
                setPaginationValues();
                checkLocationAndRetrievePosts();
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
        refresh_layout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (rl_pulsator.getVisibility() != View.VISIBLE) {
                    pulledToRefresh = true;
                    setPaginationValues();
                    checkLocationAndRetrievePosts();
                }

            }
        });
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
            //todo NT - gps kapatıldığında case'i handle et
            DialogBoxUtil.showSettingsAlert(getActivity());
        else {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionModule.PERMISSION_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    getPosts();

                } else {

                    // permission denied, boo! Disable the
                    showPulsatorLayout(false);
                    showNoFeedLayout(true, R.string.needLocationPermission);
                    refresh_layout.setRefreshing(false);
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
                    showPulsatorLayout(false);
                    showNoFeedLayout(true, R.string.locationError);
                    refresh_layout.setRefreshing(false);
                }
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

                if (isFirstFetch) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            isFirstFetch = false;
                            setFetchData(postListResponse);
                        }
                    }, 2500);
                } else {
                    setFetchData(postListResponse);
                }

            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.LOG_FAIL("PostListResponseProcess", e.toString());
                //progressBar.setVisibility(View.GONE);
                refresh_layout.setRefreshing(false);
                showPulsatorLayout(false);
                if (postList.size() > 0) {
                    DialogBoxUtil.showErrorDialog(getContext(), getContext().getResources().getString(R.string.serverError), new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {

                        }
                    });
                    showNoFeedLayout(false, 0);
                    if (feedAdapter.isShowingProgressLoading()) {
                        feedAdapter.removeProgressLoading();
                    }

                } else {
                    showNoFeedLayout(true, R.string.serverError);
                }
            }

            @Override
            public void onTaskContinue() {

                if (pageCnt == 1 && !pulledToRefresh) {
                    //progressBar.setVisibility(View.VISIBLE);
                }
            }
        }, sUserId, sPostId, sCatchType, sLongitude, sLatitude, sRadius, sPerpage, sPage, token);

        postListResponseProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void setFetchData(PostListResponse postListResponse) {

        if (postListResponse == null) {
            CommonUtils.LOG_OK_BUT_NULL("PostListResponseProcess");
        } else {
            CommonUtils.LOG_OK("PostListResponseProcess");
            if (postListResponse.getItems().size() == 0 && pageCnt == 1) {
                showNoFeedLayout(true, R.string.emptyFeed);
            } else {
                showNoFeedLayout(false, 0);
            }
            setUpRecyclerView(postListResponse);
        }

        //progressBar.setVisibility(View.GONE);
        refresh_layout.setRefreshing(false);
        showPulsatorLayout(false);

    }

    private void showPulsatorLayout(boolean isShowPulsator) {
        if (isShowPulsator) {
            UserProfile user = AccountHolderInfo.getInstance().getUser();
            UserDataUtil.setProfilePicture(getActivity(), user.getUserInfo().getProfilePhotoUrl(),
                    user.getUserInfo().getName(), user.getUserInfo().getUsername(), txtProfile, imgProfile);
            AccountHolderInfo.setAccountHolderInfoCallback(new AccountHolderInfoCallback() {
                @Override
                public void onAccountHolderIfoTaken(UserProfile userProfile) {
                    UserDataUtil.setProfilePicture(getActivity(), userProfile.getUserInfo().getProfilePhotoUrl(),
                            userProfile.getUserInfo().getName(), userProfile.getUserInfo().getUsername(), txtProfile, imgProfile);
                }
            });

            rl_pulsator.setVisibility(View.VISIBLE);
            mPulsator.start();
        } else {
            mPulsator.stop();
            rl_pulsator.setVisibility(View.GONE);
        }
    }

    private void showNoFeedLayout(boolean setVisible, int textDetail) {
        if (setVisible) {
            rl_no_feed.setVisibility(View.VISIBLE);
            txtNoFeedExplanation.setText(textDetail);
        } else {
            rl_no_feed.setVisibility(View.GONE);
            txtNoFeedExplanation.setText("");
        }
    }

    private void setUpRecyclerView(PostListResponse postListResponse) {

        loading = true;
        preDownloadUrls(postListResponse.getItems());

        if (pageCnt != 1) {
            feedAdapter.removeProgressLoading();
        }

        postList.addAll(postListResponse.getItems());

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


}