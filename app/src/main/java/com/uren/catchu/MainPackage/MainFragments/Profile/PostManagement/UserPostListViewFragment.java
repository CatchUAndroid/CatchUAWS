package com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
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
import com.uren.catchu.ApiGatewayFunctions.UserCaughtPostListProcess;
import com.uren.catchu.ApiGatewayFunctions.UserGroupCaughtPostListProcess;
import com.uren.catchu.ApiGatewayFunctions.UserSharedPostListProcess;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.TransitionHelper;
import com.uren.catchu.InfoActivity;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.FeedAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.JavaClasses.UserPostItemAnimator;
import com.uren.catchu._Libraries.LayoutManager.CustomLinearLayoutManager;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.JavaClasses.SingletonPostList;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.LocationCallback;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

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
import static com.uren.catchu.Constants.StringConstants.PROFILE_POST_TYPE_CAUGHT;
import static com.uren.catchu.Constants.StringConstants.PROFILE_POST_TYPE_GROUP;
import static com.uren.catchu.Constants.StringConstants.PROFILE_POST_TYPE_SHARED;

public class UserPostListViewFragment extends BaseFragment {

    View mView;
    private String catchType, targetUid;
    private FeedAdapter userPostListViewAdapter;
    private CustomLinearLayoutManager customLinearLayoutManager;
    private RecyclerView listRecyclerView;
    private List<Post> postList = new ArrayList<Post>();

    private static final int MARGING_GRID = 2;
    private static final int SPAN_COUNT = 3;

    private boolean loading = true;
    private boolean pulledToRefresh = false;
    private boolean isFirstFetch = false;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private int perPageCnt, pageCnt;
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

    @BindView(R.id.toolbar)
    Toolbar toolbar;

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


    private boolean hasLoadedOnce = false; // your boolean field

    public static UserPostListViewFragment newInstance(String catchType, String targetUid) {
        Bundle args = new Bundle();
        args.putString("catchType", catchType);
        args.putString("targetUid", targetUid);
        UserPostListViewFragment fragment = new UserPostListViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public UserPostListViewFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_user_post_listview_layout, container, false);
            ButterKnife.bind(this, mView);
            toolbar.setVisibility(View.GONE);
            //setUserVisibleHint olduktan sonra fonksiyonlar çalışır..
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

        getItemsFromBundle();

        initItems();
        initRecyclerView();
        checkLocationAndRetrievePosts();
    }

    private void initItems() {
        listRecyclerView = (RecyclerView) mView.findViewById(R.id.listRecyclerView);
    }

    private void getItemsFromBundle() {
        Bundle args = getArguments();
        if (args != null) {
            catchType = (String) args.getString("catchType");
            targetUid = (String) args.getString("targetUid");
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

        if (SingletonPostList.getInstance().getPostList().size() > 0 && !pulledToRefresh && loading) {
            List<Post> singletonPostList = SingletonPostList.getInstance().getPostList();
            setUpRecyclerView(singletonPostList);
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

        if (catchType.equals(PROFILE_POST_TYPE_SHARED)) {
            getSharedPosts(token);
        } else if (catchType.equals(PROFILE_POST_TYPE_CAUGHT)) {
            getCaughtPosts(token);
        } else if (catchType.equals(PROFILE_POST_TYPE_GROUP)) {
            getGroupCaughtPosts(token);
        } else {
            //do nothing
        }

    }

    private void getSharedPosts(String token) {

        setLocationInfo();

        String sUserId = AccountHolderInfo.getUserID();
        String sUid = targetUid;
        String sLongitude = longitude;
        String sPerpage = String.valueOf(perPageCnt);
        String sLatitude = latitude;
        String sRadius = radius;
        String sPage = String.valueOf(pageCnt);
        String sPrivacyType = "";

        UserSharedPostListProcess userSharedPostListProcess = new UserSharedPostListProcess(getContext(), new OnEventListener<PostListResponse>() {
            @Override
            public void onSuccess(final PostListResponse postListResponse) {

                if (postListResponse == null) {
                    showExceptionLayout(true, VIEW_NO_POST_FOUND);
                } else {
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

    private void getCaughtPosts(String token) {

        setLocationInfo();

        String sUserId = AccountHolderInfo.getUserID();
        String sUid = targetUid;
        String sLongitude = longitude;
        String sPerpage = String.valueOf(perPageCnt);
        String sLatitude = latitude;
        String sRadius = radius;
        String sPage = String.valueOf(pageCnt);
        String sPrivacyType = "";

        UserCaughtPostListProcess userCaughtPostListProcess = new UserCaughtPostListProcess(getContext(), new OnEventListener<PostListResponse>() {
            @Override
            public void onSuccess(final PostListResponse postListResponse) {

                if (postListResponse == null) {
                    showExceptionLayout(true, VIEW_NO_POST_FOUND);
                } else {
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

        userCaughtPostListProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void getGroupCaughtPosts(String token) {

        setLocationInfo();

        String sUserId = AccountHolderInfo.getUserID();
        String sGroupId = targetUid;
        String sLongitude = longitude;
        String sPerpage = String.valueOf(perPageCnt);
        String sLatitude = latitude;
        String sRadius = radius;
        String sPage = String.valueOf(pageCnt);


        UserGroupCaughtPostListProcess userGroupCaughtPostListProcess = new UserGroupCaughtPostListProcess(getContext(), new OnEventListener<PostListResponse>() {
            @Override
            public void onSuccess(final PostListResponse postListResponse) {

                if (postListResponse == null) {
                    showExceptionLayout(true, VIEW_NO_POST_FOUND);
                } else {
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
        }, sUserId, sGroupId, sLongitude, sPerpage, sLatitude, sRadius, sPage, token);

        userGroupCaughtPostListProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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


    /********************************************************************************************/
    private RecyclerView.ItemDecoration addItemDecoration() {

        RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {


                int position = parent.getChildLayoutPosition(view);

                if (position % SPAN_COUNT == 0) {
                    //outRect.left = MARGING_GRID;
                    outRect.right = MARGING_GRID;
                    outRect.bottom = MARGING_GRID;
                    outRect.top = MARGING_GRID;
                }
                if (position % SPAN_COUNT == 1) {
                    outRect.left = MARGING_GRID / 2;
                    outRect.right = MARGING_GRID / 2;
                    outRect.bottom = MARGING_GRID / 2;
                    outRect.top = MARGING_GRID / 2;
                }
                if (position % SPAN_COUNT == 2) {
                    outRect.left = MARGING_GRID;
                    //outRect.right = MARGING_GRID;
                    outRect.bottom = MARGING_GRID;
                    outRect.top = MARGING_GRID;
                }

               /*
                outRect.left = MARGING_GRID;
                outRect.right = MARGING_GRID;
                outRect.bottom = MARGING_GRID;
                if (parent.getChildLayoutPosition(view) >= 0 && parent.getChildLayoutPosition(view) <= SPAN_COUNT) {
                    outRect.top = MARGING_GRID;
                }
                */
            }
        };

        return itemDecoration;
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

