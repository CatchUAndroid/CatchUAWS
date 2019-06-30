package com.uren.catchu.MainPackage.MainFragments.Profile.OtherProfile.SubFragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.uren.catchu.Adapters.LocationTrackerAdapter;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.UserSharedPostListProcess;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.TransitionHelper;
import com.uren.catchu.InfoActivity;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.FeedAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.OtherProfile.JavaClasses.OtherProfilePostList;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.JavaClasses.UserPostItemAnimator;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.LocationCallback;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu._Libraries.LayoutManager.CustomLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.Post;
import catchu.model.PostListResponse;

import static com.uren.catchu.Constants.NumericConstants.DEFAULT_PROFILE_GRIDVIEW_PAGE_COUNT;
import static com.uren.catchu.Constants.NumericConstants.DEFAULT_PROFILE_GRIDVIEW_PERPAGE_COUNT;
import static com.uren.catchu.Constants.NumericConstants.FILTERED_FEED_RADIUS;
import static com.uren.catchu.Constants.NumericConstants.VIEW_LOCATION_PERMISSION;
import static com.uren.catchu.Constants.NumericConstants.VIEW_LOCATION_SERVICE_ERROR;
import static com.uren.catchu.Constants.NumericConstants.VIEW_NO_POST_FOUND;
import static com.uren.catchu.Constants.NumericConstants.VIEW_RETRY;
import static com.uren.catchu.Constants.NumericConstants.VIEW_SERVER_ERROR;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.OTHER_PROFILE_POST_TYPE_SHARED;

public class OtherProfilePostListViewFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    private String catchType, targetUid, userName;
    private int position;
    private FeedAdapter userPostListViewAdapter;
    private CustomLinearLayoutManager customLinearLayoutManager;
    private RecyclerView listRecyclerView;
    private List<Post> postList = new ArrayList<Post>();

    private boolean loading = true;
    private boolean pulledToRefresh = false;
    private boolean isFirstFetch = false;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private int perPageCnt, pageCnt;
    private int comingPageCnt;
    private static final int RECYCLER_VIEW_CACHE_COUNT = 50;

    //Location
    private LocationTrackerAdapter locationTrackObj;
    private PermissionModule permissionModule;
    private String longitude;
    private String latitude;
    private String radius;


    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refresh_layout;

    //toolbar items
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;
    @BindView(R.id.commonToolbarbackImgv)
    ClickableImageView commonToolbarbackImgv;

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


    public static OtherProfilePostListViewFragment newInstance(String catchType, String targetUid, int position, String userName, int comingPageCnt) {
        Bundle args = new Bundle();
        args.putString("catchType", catchType);
        args.putString("targetUid", targetUid);
        args.putInt("position", position);
        args.putString("userName", userName);
        args.putInt("comingPageCnt", comingPageCnt);
        OtherProfilePostListViewFragment fragment = new OtherProfilePostListViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public OtherProfilePostListViewFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_user_post_listview_layout, container, false);
            ButterKnife.bind(this, mView);

            loadData();
            //setUserVisibleHint olduktan sonra fonksiyonlar çalışır..
        }
        return mView;
    }

    private void loadData() {

        getItemsFromBundle();

        initItems();
        initRecyclerView();
        checkLocationAndRetrievePosts();
    }

    private void initItems() {
        listRecyclerView = (RecyclerView) mView.findViewById(R.id.listRecyclerView);
        commonToolbarbackImgv.setOnClickListener(this);
        if (userName != null && !userName.isEmpty()) {
            toolbarTitleTv.setText(userName);
        } else {
            toolbarTitleTv.setText(getContext().getResources().getString(R.string.profile));
        }

    }

    private void getItemsFromBundle() {
        Bundle args = getArguments();
        if (args != null) {
            catchType = (String) args.getString("catchType");
            targetUid = (String) args.getString("targetUid");
            position = (Integer) args.getInt("position");
            userName = (String) args.getString("userName");
            comingPageCnt = (Integer) args.getInt("comingPageCnt");
        }
    }

    private void initRecyclerView() {

        isFirstFetch = true;
        mainExceptionLayout.setVisibility(View.GONE);
        setLayoutManager();
        setAdapter();
        setPullToRefresh();
        setRecyclerViewScroll();
        setPaginationValues();

        pageCnt = comingPageCnt;

    }

    private void setLayoutManager() {
        customLinearLayoutManager = new CustomLinearLayoutManager(getContext());
        listRecyclerView.setLayoutManager(customLinearLayoutManager);
        listRecyclerView.setItemAnimator(new UserPostItemAnimator());
        //listRecyclerView.addItemDecoration(addItemDecoration());
    }

    private void setAdapter() {
        userPostListViewAdapter = new FeedAdapter(getActivity(), getContext(), mFragmentNavigation);
        listRecyclerView.setAdapter(userPostListViewAdapter);
        listRecyclerView.setItemViewCacheSize(RECYCLER_VIEW_CACHE_COUNT);
    }

    private void setPullToRefresh() {

        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pulledToRefresh = true;
                setPaginationValues();
                checkLocationAndRetrievePosts();
            }
        });

    }

    private void setPaginationValues() {
        perPageCnt = DEFAULT_PROFILE_GRIDVIEW_PERPAGE_COUNT;
        pageCnt = DEFAULT_PROFILE_GRIDVIEW_PAGE_COUNT;
        float radiusInKm = (float) ((double) FILTERED_FEED_RADIUS / (double) 1000);
        radius = String.valueOf(radiusInKm);
    }

    private void setRecyclerViewScroll() {

        listRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);
                //setScrollButtonVisibility();

                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = customLinearLayoutManager.getChildCount();
                    totalItemCount = customLinearLayoutManager.getItemCount();
                    pastVisibleItems = customLinearLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
                            pageCnt++;
                            userPostListViewAdapter.addProgressLoading();
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
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

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

        if (OtherProfilePostList.getInstance().getPostList().size() > 0 && !pulledToRefresh && loading) {
            List<Post> singletonPostList = OtherProfilePostList.getInstance().getPostList();
            setUpRecyclerView(singletonPostList);
            listRecyclerView.scrollToPosition(position);
            return;
        }

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(final String token) {
                Location location = locationTrackObj.getLocation();
                if (location != null) {
                    startGetPosts(token);
                } else {
                    showExceptionLayout(true, VIEW_LOCATION_PERMISSION);
                }
            }

            @Override
            public void onTokenFail(String message) {
                refresh_layout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void startGetPosts(String token) {

        if (catchType.equals(OTHER_PROFILE_POST_TYPE_SHARED)) {
            getOtherProfileSharedPosts(token);
        } else {
            //do nothing
        }

    }

    private void getOtherProfileSharedPosts(String token) {

        setLocationInfo();

        String sUserId = AccountHolderInfo.getUserID();
        String sUid = targetUid;
        String sLongitude = longitude;
        String sPerpage = String.valueOf(9);
        String sLatitude = latitude;
        String sRadius = radius;
        String sPage = String.valueOf(pageCnt);
        String sPrivacyType = "";

        UserSharedPostListProcess userSharedPostListProcess = new UserSharedPostListProcess(getContext(), new OnEventListener<PostListResponse>() {
            @Override
            public void onSuccess(final PostListResponse postListResponse) {

                if (postListResponse == null) {
                    CommonUtils.LOG_OK_BUT_NULL("UserSharedPostListProcess");
                    showExceptionLayout(true, VIEW_NO_POST_FOUND);
                } else {
                    CommonUtils.LOG_OK("UserSharedPostListProcess");
                    if (postListResponse.getItems().size() == 0 && pageCnt == 1) {
                        showExceptionLayout(true, VIEW_NO_POST_FOUND);
                    } else {
                        showExceptionLayout(false, -1);
                    }
                    setUpRecyclerView(postListResponse.getItems());
                }

                progressBar.setVisibility(View.GONE);
                refresh_layout.setRefreshing(false);

            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.LOG_FAIL("UserSharedPostListProcess", e.toString());
                progressBar.setVisibility(View.GONE);
                refresh_layout.setRefreshing(false);

                if (postList.size() > 0) {
                    DialogBoxUtil.showErrorDialog(getContext(), getContext().getResources().getString(R.string.serverError), new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {
                        }
                    });
                    showExceptionLayout(false, -1);
                    if (userPostListViewAdapter.isShowingProgressLoading()) {
                        userPostListViewAdapter.removeProgressLoading();
                    }

                } else {
                    showExceptionLayout(true, VIEW_SERVER_ERROR);
                }
            }

            @Override
            public void onTaskContinue() {

                if (pageCnt == 1 && !pulledToRefresh) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        }, sUserId, sUid, sLongitude, sPerpage, sLatitude, sRadius, sPage, sPrivacyType, token);

        userSharedPostListProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void setLocationInfo() {
        longitude = String.valueOf(locationTrackObj.getLocation().getLongitude());
        latitude = String.valueOf(locationTrackObj.getLocation().getLatitude());
    }

    private void setUpRecyclerView(List<Post> addPostList) {

        loading = true;
        postList.addAll(addPostList);
        if (pageCnt != 1) {
            userPostListViewAdapter.removeProgressLoading();
        }

        if (pulledToRefresh) {
            userPostListViewAdapter.updatePostListItems(addPostList);
            pulledToRefresh = false;
        } else {
            userPostListViewAdapter.addAll(addPostList);
        }

    }

    @Override
    public void onClick(View view) {

        if (view == commonToolbarbackImgv) {

            if (getActivity() instanceof NextActivity)
                ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;

            getActivity().onBackPressed();
        }

    }


    /**********************************************/
    private void showExceptionLayout(boolean showException, int viewType) {

        if (showException) {

            refresh_layout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
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
            } else if (viewType == VIEW_LOCATION_PERMISSION) {
                needLocationPermission.setVisibility(View.VISIBLE);
            } else if (viewType == VIEW_SERVER_ERROR) {
                serverError.setVisibility(View.VISIBLE);
            }

        } else {
            mainExceptionLayout.setVisibility(View.GONE);
        }

    }


}

