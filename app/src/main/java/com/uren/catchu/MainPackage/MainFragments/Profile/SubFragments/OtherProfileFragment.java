package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.uren.catchu.Adapters.LocationTrackerAdapter;
import com.uren.catchu.ApiGatewayFunctions.UserSharedPostListProcess;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.YesNoDialogBoxCallback;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.PersonListAdapter;
import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.SearchResultAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.FollowClickCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.RecyclerScrollListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters.FollowAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters.OtherProfileAdapter;
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
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_FOLLOWING;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_NONE;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_PENDING;
import static com.uren.catchu.Constants.StringConstants.FRIEND_CREATE_FOLLOW_DIRECTLY;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_FOLLOW;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_PENDING_FOLLOW_REQUEST;
import static com.uren.catchu.Constants.StringConstants.FRIEND_FOLLOW_REQUEST;

@SuppressLint("ValidFragment")
public class OtherProfileFragment extends BaseFragment
        implements View.OnClickListener,
        FollowClickCallback,
        RecyclerScrollListener {

    View mView;
    UserInfoListItem userInfoListItem;
    User selectedUser;
    UserProfile fetchedUser;

    private OtherProfileAdapter otherProfileAdapter;
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


    private static final int ADAPTER_ITEMS_END_POSITION = 2;
    private boolean pulledToRefreshHeader = false;
    private boolean pulledToRefreshPost = false;
    private boolean isFirstFetch = false;
    private boolean isMoreItemAvailable = true;
    private int lastCompletelyVisibleItemPosition;

    private int perPageCnt, pageCnt, innerRecyclerPageCnt;
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
     *             iii) eger adaptor beslenecek ise onFollowStatusChanged fonksiyonu icerisinde ilgili
     *             adaptor icin kosul eklenmeli..
     */
    public OtherProfileFragment (UserInfoListItem user) {
        this.userInfoListItem = user;
        this.selectedUser = user.getUser();
    }

    @Override
    public void onStart() {
        NextActivity.bottomTabLayout.setVisibility(View.VISIBLE);
        super.onStart();
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

            /*Bundle args = getArguments();
            if (args != null) {
                userInfoListItem = (UserInfoListItem) args.getSerializable(ARGS_INSTANCE);
                selectedUser = userInfoListItem.getUser();
            }*/

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
    }

    private void setAdapter() {
        otherProfileAdapter = new OtherProfileAdapter(getActivity(), getContext(), mFragmentNavigation);
        recyclerView.setAdapter(otherProfileAdapter);
        recyclerView.setItemViewCacheSize(RECYCLER_VIEW_CACHE_COUNT);

        otherProfileAdapter.addHeader(userInfoListItem);
        otherProfileAdapter.setFollowClickCallback(this);
        otherProfileAdapter.setInnerRecyclerScrollListener(this);

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
        innerRecyclerPageCnt = DEFAULT_PROFILE_GRIDVIEW_PAGE_COUNT;
        float radiusInKm = (float) ((double) FILTERED_FEED_RADIUS / (double) 1000);
        radius = String.valueOf(radiusInKm);
    }


    private void setRecyclerViewScroll() {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                lastCompletelyVisibleItemPosition = customLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastCompletelyVisibleItemPosition == ADAPTER_ITEMS_END_POSITION && isMoreItemAvailable) {
                    otherProfileAdapter.addProgressLoading();
                    innerRecyclerPageCnt++;
                    getPosts();
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

        loadUserDetail.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

    }

    private void setHeaderInRecyclerView(UserProfile userProfile) {
        otherProfileAdapter.updateHeader(userProfile);
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
                    DialogBoxUtil.showInfoDialogBox(getContext(), getResources().getString(R.string.needLocationPermission), "", new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {
                        }
                    });

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
                    DialogBoxUtil.showInfoDialogBox(getContext(), getResources().getString(R.string.locationError), "", new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {
                        }
                    });
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
        String sPage = String.valueOf(innerRecyclerPageCnt);
        String sPrivacyType = "";

        UserSharedPostListProcess userSharedPostListProcess = new UserSharedPostListProcess(getContext(), new OnEventListener<PostListResponse>() {
            @Override
            public void onSuccess(final PostListResponse postListResponse) {

                if (otherProfileAdapter.isShowingProgressLoading()) {
                    otherProfileAdapter.removeProgressLoading();
                }

                if (postListResponse == null) {
                    CommonUtils.LOG_OK_BUT_NULL("UserSharedPostListProcess");
                } else {
                    CommonUtils.LOG_OK("UserSharedPostListProcess");

                    if (postListResponse.getItems().size() != 0) {
                        isMoreItemAvailable = true;
                    } else {
                        isMoreItemAvailable = false;
                    }

                    setPostsInRecyclerView(postListResponse);

                }

                refresh_layout.setRefreshing(false);

            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.LOG_FAIL("UserSharedPostListProcess", e.toString());

                if (otherProfileAdapter.isShowingProgressLoading()) {
                    otherProfileAdapter.removeProgressLoading();
                }

                refresh_layout.setRefreshing(false);

            }

            @Override
            public void onTaskContinue() {

                if (innerRecyclerPageCnt == 1 && !pulledToRefreshPost) {
                    otherProfileAdapter.addProgressLoading();
                }

            }
        }, sUserId, sUid, sLongitude, sPerpage, sLatitude, sRadius, sPage, sPrivacyType, token);

        userSharedPostListProcess.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);


    }

    private void setPostsInRecyclerView(PostListResponse postListResponse) {

        objectList.addAll(postListResponse.getItems());

        if (innerRecyclerPageCnt != 1 && otherProfileAdapter.isShowingProgressLoading()) {
            otherProfileAdapter.removeProgressLoading();
        }

        if (pulledToRefreshPost) {
            otherProfileAdapter.updatePosts(postListResponse.getItems());
            pulledToRefreshPost = false;
        } else {
            if (innerRecyclerPageCnt == 1) {
                otherProfileAdapter.addPosts(postListResponse.getItems());
                otherProfileAdapter.addLastItem();
            } else {
                otherProfileAdapter.loadMorePost(postListResponse.getItems());
            }
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

    @Override
    public void onFollowStatusChanged(String followStatus) {

        if (userInfoListItem.getAdapter() != null) {
            if (userInfoListItem.getAdapter() instanceof FollowAdapter) {
                ((FollowAdapter) userInfoListItem.getAdapter()).updateAdapterWithPosition(userInfoListItem.getClickedPosition());
                // else if (followInfoListItem.getAdapter() instanceof UserDetailAdapter) {
                //((UserDetailAdapter) followInfoListItem.getAdapter()).updateAdapterWithPosition(followInfoListItem.getClickedPosition());
            } else if (userInfoListItem.getAdapter() instanceof PersonListAdapter) {
                if (followStatus != null && !followStatus.isEmpty()) {
                    PersonListAdapter adapter = (PersonListAdapter) userInfoListItem.getAdapter();
                    User user = adapter.getPersonList().getItems().get(userInfoListItem.getClickedPosition());
                    user.setFollowStatus(followStatus);
                }
                ((PersonListAdapter) userInfoListItem.getAdapter()).updateAdapterWithPosition(userInfoListItem.getClickedPosition());
            } else if (userInfoListItem.getAdapter() instanceof SearchResultAdapter) {
                if (followStatus != null && !followStatus.isEmpty()) {
                    SearchResultAdapter adapter = (SearchResultAdapter) userInfoListItem.getAdapter();
                    User user = adapter.getPersonList().get(userInfoListItem.getClickedPosition());
                    user.setFollowStatus(followStatus);
                }
                ((SearchResultAdapter) userInfoListItem.getAdapter()).updateAdapterWithPosition(userInfoListItem.getClickedPosition());
            }
        }

    }

    @Override
    public void onLoadMore() {
        this.innerRecyclerPageCnt++;
        getPosts();
    }


    /*****************************************************************************/

    private boolean isValid(String name) {
        if (name != null && !name.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }


}