package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.uren.catchu.Adapters.LocationTrackerAdapter;
import com.uren.catchu.ApiGatewayFunctions.UserSharedPostListProcess;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters.DenemeAdapter;
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

import catchu.model.PostListResponse;
import catchu.model.User;
import catchu.model.UserProfile;

import static com.uren.catchu.Constants.NumericConstants.DEFAULT_PROFILE_GRIDVIEW_PAGE_COUNT;
import static com.uren.catchu.Constants.NumericConstants.DEFAULT_PROFILE_GRIDVIEW_PERPAGE_COUNT;
import static com.uren.catchu.Constants.NumericConstants.FILTERED_FEED_RADIUS;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;

public class OtherProfileFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    UserInfoListItem userInfoListItem;
    User selectedUser;
    UserProfile fetchedUser;

    private DenemeAdapter denemeAdapter;
    private CustomLinearLayoutManager customLinearLayoutManager;
    private List<Object> objectList = new ArrayList<Object>();

    //Refresh layout
    @BindView(R.id.refresh_layout)
    RecyclerRefreshLayout refresh_layout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    //toolbar items
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;
    @BindView(R.id.commonToolbarbackImgv)
    ClickableImageView commonToolbarbackImgv;

    private static final int MARGING_GRID = 2;
    private static final int SPAN_COUNT = 3;

    private boolean loading = true;
    private boolean pulledToRefreshHeader = false;
    private boolean pulledToRefreshPost = false;
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

    /**
     * @param user i) userId -> ZORUNLU,
     *             ii) profilePicUrl ve username -> nice to have.
     *             iii) eger adaptor beslenecek ise updateAdapters fonksiyonu icerisinde ilgili
     *             adaptor icin kosul eklenmeli..
     */
    public static OtherProfileFragment newInstance(UserInfoListItem user) {
        Bundle args = new Bundle();
        args.putSerializable(ARGS_INSTANCE, user);
        OtherProfileFragment fragment = new OtherProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        NextActivity.bottomTabLayout.setVisibility(View.VISIBLE);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.profile_subfragment_other_profile, container, false);
            ButterKnife.bind(this, mView);

            Bundle args = getArguments();
            if (args != null) {
                userInfoListItem = (UserInfoListItem) args.getSerializable(ARGS_INSTANCE);
                selectedUser = userInfoListItem.getUser();
            }

            setInitialValues();
            initRecyclerView();
            getData();
        }

        return mView;
    }

    private void setInitialValues() {

        commonToolbarbackImgv.setOnClickListener(this);
        toolbarTitleTv.setText(getContext().getResources().getString(R.string.profile));

        //toolbar Title
        if (selectedUser != null) {
            if (isValid(selectedUser.getUsername())) {
                toolbarTitleTv.setText(selectedUser.getUsername());
            }
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
        recyclerView.setLayoutManager(customLinearLayoutManager);
        //gridRecyclerView.setItemAnimator(new UserPostItemAnimator());
    }

    private void setAdapter() {
        denemeAdapter = new DenemeAdapter(getActivity(), getContext(), mFragmentNavigation);
        recyclerView.setAdapter(denemeAdapter);
        recyclerView.setItemViewCacheSize(RECYCLER_VIEW_CACHE_COUNT);

        denemeAdapter.addHeader(userInfoListItem);
    }

    private void setPullToRefresh() {

        refresh_layout.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pulledToRefreshHeader = true;
                pulledToRefreshPost = true;
                setPaginationValues();

                getData();
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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = customLinearLayoutManager.getChildCount();
                    totalItemCount = customLinearLayoutManager.getItemCount();
                    pastVisibleItems = customLinearLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {

                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
                            Log.v("...", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data
                            pageCnt++;
                            //denemeAdapter.addProgressLoading();
                            //getPosts();

                        }
                    }
                }
            }

        });

    }


    private void getData() {
        getUserInfo();
        checkLocationAndRetrievePosts();
    }

    private void getUserInfo() {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetUserInfo(token);
            }
        });
    }

    private void startGetUserInfo(String token) {

        UserDetail loadUserDetail = new UserDetail(new OnEventListener<UserProfile>() {

            @Override
            public void onSuccess(UserProfile userProfile) {

                if (userProfile == null) {
                    CommonUtils.LOG_OK_BUT_NULL("UserDetail");

                } else {
                    CommonUtils.LOG_OK("UserDetail");
                    fetchedUser = userProfile;
                    setHeaderInRecyclerView(fetchedUser);
                }
            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.LOG_FAIL("UserDetail", e.toString());
            }

            @Override
            public void onTaskContinue() {
            }
        }, AccountHolderInfo.getUserID(), selectedUser.getUserid(), token);

        loadUserDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void setHeaderInRecyclerView(UserProfile userProfile) {
        denemeAdapter.updateHeader(userProfile);
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

                    // permission was granted, yay!
                    getPosts();

                } else {

                    // permission denied, boo!
                    //showNoFeedLayout(true, R.string.needLocationPermission);
                    refresh_layout.setRefreshing(false);

                }

            }

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
                    //showNoFeedLayout(true, R.string.locationError);
                    refresh_layout.setRefreshing(false);
                }
            }
        });
    }

    private void startGetPosts(String token) {

        setLocationInfo();

        String sUserId = AccountHolderInfo.getUserID();
        String sUid = selectedUser.getUserid();
        String sLongitude = longitude;
        String sPerpage = String.valueOf(perPageCnt);
        String sLatitude = latitude;
        String sRadius = radius;
        String sPage = String.valueOf(pageCnt);
        String sPrivacyType = "";

        UserSharedPostListProcess userSharedPostListProcess = new UserSharedPostListProcess(getContext(), new OnEventListener<PostListResponse>() {
            @Override
            public void onSuccess(final PostListResponse postListResponse) {

                if(denemeAdapter.isShowingProgressLoading()){
                    denemeAdapter.removeProgressLoading();
                }

                if (postListResponse == null) {
                    CommonUtils.LOG_OK_BUT_NULL("UserSharedPostListProcess");
                } else {
                    CommonUtils.LOG_OK("UserSharedPostListProcess");
                 /*   if (postListResponse.getItems().size() == 0 && pageCnt == 1) {
                        //showNoFeedLayout(true, R.string.emptyFeed);
                    } else {
                        //showNoFeedLayout(false, 0);
                    }*/
                    setPostsInRecyclerView(postListResponse);
                }

                refresh_layout.setRefreshing(false);

            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.LOG_FAIL("UserSharedPostListProcess", e.toString());

                if(denemeAdapter.isShowingProgressLoading()){
                    denemeAdapter.removeProgressLoading();
                }

                refresh_layout.setRefreshing(false);
/*
                if (postList.size() > 0) {
                    DialogBoxUtil.showErrorDialog(getContext(), getContext().getResources().getString(R.string.serverError), new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {
                        }
                    });
                    showNoFeedLayout(false, 0);
                    if (userPostGridViewAdapter.isShowingProgressLoading()) {
                        userPostGridViewAdapter.removeProgressLoading();
                    }
                } else {
                    //showNoFeedLayout(true, R.string.serverError);
                }
                */
            }

            @Override
            public void onTaskContinue() {

                if (pageCnt == 1 && !pulledToRefreshPost) {
                    denemeAdapter.addProgressLoading();
                }

            }
        }, sUserId, sUid, sLongitude, sPerpage, sLatitude, sRadius, sPage, sPrivacyType, token);

        userSharedPostListProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }

    private void setPostsInRecyclerView(PostListResponse postListResponse) {

        loading = true;
        objectList.addAll(postListResponse.getItems());

        if (pageCnt != 1 && denemeAdapter.isShowingProgressLoading()) {
            denemeAdapter.removeProgressLoading();
        }

        if (pulledToRefreshPost) {
            denemeAdapter.updatePosts(postListResponse.getItems());
            pulledToRefreshPost = false;
        } else {
            denemeAdapter.addPosts(postListResponse.getItems());
        }

    }

    private void setLocationInfo() {
        longitude = String.valueOf(locationTrackObj.getLocation().getLongitude());
        latitude = String.valueOf(locationTrackObj.getLocation().getLatitude());
    }


    @Override
    public void onClick(View v) {

        if (v == commonToolbarbackImgv) {

            if (getActivity() instanceof NextActivity)
                ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;

            getActivity().onBackPressed();
        }

    }

    /*****************************************************************************/

    private boolean isValid(String name) {
        if (name != null && !name.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    private RecyclerView.ItemDecoration addItemDecoration() {

        RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {


                int position = parent.getChildLayoutPosition(view);

                if (position != 0) {
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
            }
        };

        return itemDecoration;
    }


}