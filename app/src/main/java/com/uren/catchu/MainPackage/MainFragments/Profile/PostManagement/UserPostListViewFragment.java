package com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.uren.catchu.Adapters.LocationTrackerAdapter;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.UserCaughtPostListProcess;
import com.uren.catchu.ApiGatewayFunctions.UserSharedPostListProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
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
import static com.uren.catchu.Constants.StringConstants.PROFILE_POST_TYPE_CAUGHT;
import static com.uren.catchu.Constants.StringConstants.PROFILE_POST_TYPE_MY_POSTS;

public class UserPostListViewFragment extends BaseFragment {

    View mView;
    String catchType;
    FeedAdapter userPostListViewAdapter;
    static CustomLinearLayoutManager customLinearLayoutManager;
    static RecyclerView listRecyclerView;
    static boolean scrollButtonVisible=false;
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
    RecyclerRefreshLayout refresh_layout;
    @BindView(R.id.rl_no_feed)
    RelativeLayout rl_no_feed;
    @BindView(R.id.txtNoFeedExplanation)
    TextView txtNoFeedExplanation;

    private boolean hasLoadedOnce = false; // your boolean field

    public static UserPostListViewFragment newInstance(String catchType) {
        Bundle args = new Bundle();
        args.putString("catchType", catchType);
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
        }
    }

    private void initRecyclerView() {

        isFirstFetch = true;
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
        listRecyclerView.setItemViewCacheSize(RECYCLER_VIEW_CACHE_COUNT) ;
    }

    private void setPullToRefresh() {

        refresh_layout.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
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
                setScrollButtonVisibility();

                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = customLinearLayoutManager.getChildCount();
                    totalItemCount = customLinearLayoutManager.getItemCount();
                    pastVisibleItems = customLinearLayoutManager.findFirstVisibleItemPosition();

                    Log.i("visibleItemCount", String.valueOf(visibleItemCount));
                    Log.i("totalItemCount", String.valueOf(totalItemCount));
                    Log.i("pastVisibleItems", String.valueOf(pastVisibleItems));
                    Log.i("loading", String.valueOf(loading));

                    if (loading) {

                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
                            Log.v("...", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data
                            pageCnt++;
                            userPostListViewAdapter.addProgressLoading();
                            getPosts();

                        }
                    }

                }
            }
        });

    }

    private void setScrollButtonVisibility() {
        int visibility;
        int firstVisibleItemPosition = customLinearLayoutManager.findFirstVisibleItemPosition();
        if (firstVisibleItemPosition<3) {
            visibility = View.GONE;
            scrollButtonVisible = false;
        } else {
            visibility = View.VISIBLE;
            scrollButtonVisible = true;
        }

        UserPostFragment.fabScrollUp.setVisibility(visibility);
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
                    // contacts-related task you need to do.
                    Toast.makeText(getContext(), " ACCESS_FINE_LOCATION - Permission granted", Toast.LENGTH_SHORT).show();
                    getPosts();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    showNoFeedLayout(true, R.string.needLocationPermission);
                    refresh_layout.setRefreshing(false);

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
                    showNoFeedLayout(true, R.string.locationError);
                    refresh_layout.setRefreshing(false);
                }
            }
        });
    }

    private void startGetPosts(String token) {

        if (catchType.equals(PROFILE_POST_TYPE_MY_POSTS)) {
            getMyPosts(token);
        } else if (catchType.equals(PROFILE_POST_TYPE_CAUGHT)) {
            getCaughtPosts(token);
        } else {
            //do nothing
        }

    }

    private void getMyPosts(String token) {

        setLocationInfo();

        String sUserId = AccountHolderInfo.getUserID();
        String sUid = AccountHolderInfo.getUserID();
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
                    CommonUtils.LOG_OK_BUT_NULL("UserSharedPostListProcess");
                } else {
                    CommonUtils.LOG_OK("UserSharedPostListProcess");
                    if (postListResponse.getItems().size() == 0 && pageCnt == 1) {
                        showNoFeedLayout(true, R.string.emptyFeed);
                    } else {
                        showNoFeedLayout(false, 0);
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
                    showNoFeedLayout(false, 0);
                    if (userPostListViewAdapter.isShowingProgressLoading()) {
                        userPostListViewAdapter.removeProgressLoading();
                    }

                } else {
                    showNoFeedLayout(true, R.string.serverError);
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
        String sUid = AccountHolderInfo.getUserID();
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
                    CommonUtils.LOG_OK_BUT_NULL("UserCaughtPostListProcess");
                } else {
                    CommonUtils.LOG_OK("UserCaughtPostListProcess");
                    if (postListResponse.getItems().size() == 0 && pageCnt == 1) {
                        showNoFeedLayout(true, R.string.emptyFeed);
                    } else {
                        showNoFeedLayout(false, 0);
                    }
                    setUpRecyclerView(postListResponse.getItems());
                }

                progressBar.setVisibility(View.GONE);
                refresh_layout.setRefreshing(false);

            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.LOG_FAIL("UserCaughtPostListProcess", e.toString());
                progressBar.setVisibility(View.GONE);
                refresh_layout.setRefreshing(false);

                if (postList.size() > 0) {
                    DialogBoxUtil.showErrorDialog(getContext(), getContext().getResources().getString(R.string.serverError), new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {
                        }
                    });
                    showNoFeedLayout(false, 0);
                    if (userPostListViewAdapter.isShowingProgressLoading()) {
                        userPostListViewAdapter.removeProgressLoading();
                    }

                } else {
                    showNoFeedLayout(true, R.string.serverError);
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

    private void showNoFeedLayout(boolean setVisible, int textDetail) {
        if (setVisible) {
            rl_no_feed.setVisibility(View.VISIBLE);
            txtNoFeedExplanation.setText(textDetail);
        } else {
            rl_no_feed.setVisibility(View.GONE);
            txtNoFeedExplanation.setText("");
        }
    }
}

