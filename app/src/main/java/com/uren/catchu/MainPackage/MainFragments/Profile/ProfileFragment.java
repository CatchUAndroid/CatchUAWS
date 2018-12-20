package com.uren.catchu.MainPackage.MainFragments.Profile;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.uren.catchu.Adapters.LocationTrackerAdapter;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.ApiGatewayFunctions.UserSharedPostListProcess;
import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.ApiModelsProcess.UserGroupsProcess;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Adapters.ProfileAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.GroupManagementFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.OtherProfile.Adapters.OtherProfileAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.Adapters.GroupsListAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.UserPostFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SettingsManagement.NotifyProblemFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SettingsManagement.SettingsFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.ExplorePeopleFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.FollowerFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.FollowingFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.PendingRequestsFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.UserEditFragment;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.LocationCallback;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.GroupListHolder;
import com.uren.catchu.Singleton.Interfaces.AccountHolderInfoCallback;
import com.uren.catchu.Singleton.Interfaces.GroupListHolderCallback;
import com.uren.catchu._Libraries.LayoutManager.CustomLinearLayoutManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.FriendRequestList;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.PostListResponse;
import catchu.model.UserProfile;

import static com.uren.catchu.Constants.NumericConstants.DEFAULT_PROFILE_GRIDVIEW_PAGE_COUNT;
import static com.uren.catchu.Constants.NumericConstants.DEFAULT_PROFILE_GRIDVIEW_PERPAGE_COUNT;
import static com.uren.catchu.Constants.NumericConstants.FILTERED_FEED_RADIUS;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.CHAR_AMPERSAND;
import static com.uren.catchu.Constants.StringConstants.GROUP_OP_VIEW_TYPE;
import static com.uren.catchu.Constants.StringConstants.PROFILE_POST_TYPE_CAUGHT;
import static com.uren.catchu.Constants.StringConstants.PROFILE_POST_TYPE_SHARED;

public class ProfileFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    UserProfile myProfile;
    boolean mDrawerState;

    private ProfileAdapter profileAdapter;
    private CustomLinearLayoutManager customLinearLayoutManager;
    private List<Object> objectList = new ArrayList<Object>();

    //Refresh layout
    @BindView(R.id.refresh_layout)
    RecyclerRefreshLayout refresh_layout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;


    TextView navViewNameTv;
    TextView navViewEmailTv;
    ImageView navImgProfile;
    TextView navViewShortenTextView;
    RelativeLayout profileNavViewLayout;
    TextView navPendReqCntTv;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.htab_toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.pendReqCntTv)
    TextView pendReqCntTv;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navViewLayout)
    NavigationView navViewLayout;

    @BindView(R.id.imgUserEdit)
    ClickableImageView imgUserEdit;
    @BindView(R.id.menuImgv)
    ClickableImageView menuImgv;
    @BindView(R.id.imgBackBtn)
    ClickableImageView imgBackBtn;
    @BindView(R.id.menuLayout)
    RelativeLayout menuLayout;
    @BindView(R.id.backLayout)
    RelativeLayout backLayout;

    private static final int ADAPTER_ITEMS_END_POSITION = 2;
    private boolean pulledToRefreshHeader = false;
    private boolean pulledToRefreshPost = false;
    private boolean loading = true;
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


    public static ProfileFragment newInstance(Boolean comingFromTab) {
        Bundle args = new Bundle();
        args.putBoolean("comingFromTab", comingFromTab);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {

    }

    @Override
    public void onStart() {
        NextActivity.bottomTabLayout.setVisibility(View.VISIBLE);
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_profile, container, false);
            ButterKnife.bind(this, mView);
            checkBundle();

            //Menu Layout
            setNavViewItems();
            setDrawerListeners();

            initListeners();
            //updateUI();



            initRecyclerView();
            getData();

        }

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initRecyclerView() {

        setPaginationValues();
        setLayoutManager();
        setAdapter();
        setPullToRefresh();
        setRecyclerViewScroll();

    }

    private void setLayoutManager() {
        customLinearLayoutManager = new CustomLinearLayoutManager(getContext());
        recyclerView.setLayoutManager(customLinearLayoutManager);
    }

    private void setAdapter() {
        profileAdapter = new ProfileAdapter(getActivity(), getContext(), mFragmentNavigation, innerRecyclerPageCnt);
        recyclerView.setAdapter(profileAdapter);
        recyclerView.setItemViewCacheSize(RECYCLER_VIEW_CACHE_COUNT);

        addHeader();

    }

    private void addHeader() {

        profileAdapter.addHeader(userInfoListItem);


        /*
        AccountHolderInfo instance = AccountHolderInfo.getInstance();

        if (instance != null) {
            myProfile = instance.getUser();
            if(myProfile.getUserInfo().getUsername() == null){
                AccountHolderInfo.setAccountHolderInfoCallback(new AccountHolderInfoCallback() {
                    @Override
                    public void onAccountHolderIfoTaken(UserProfile userProfile) {
                        setProfileDetail(userProfile);
                    }
                });
            }else{
                setProfileDetail(myProfile);
            }
        } else {
            getProfileDetail(AccountHolderInfo.getUserID());
        }

        getGroupsFromSingleton();
        */

    }

    private void setPullToRefresh() {

        refresh_layout.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pulledToRefreshHeader = true;
                pulledToRefreshPost = true;
                setPaginationValues();
                profileAdapter.innerRecyclerPageCntChanged(innerRecyclerPageCnt);

                getData();
            }
        });

    }

    private void setPaginationValues() {
        perPageCnt = DEFAULT_PROFILE_GRIDVIEW_PERPAGE_COUNT;
        pageCnt = DEFAULT_PROFILE_GRIDVIEW_PAGE_COUNT;
        innerRecyclerPageCnt = DEFAULT_PROFILE_GRIDVIEW_PAGE_COUNT;
        isMoreItemAvailable = true;
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
                    lastCompletelyVisibleItemPosition = customLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                    if (loading) {

                        if (lastCompletelyVisibleItemPosition == ADAPTER_ITEMS_END_POSITION && isMoreItemAvailable) {
                            loading = false;
                            innerRecyclerPageCnt++;
                            profileAdapter.innerRecyclerPageCntChanged(innerRecyclerPageCnt);
                            profileAdapter.addProgressLoading();
                            recyclerView.scrollToPosition(profileAdapter.getItemCount()-1);

                            getPosts();
                        }

                    }

                }

            }

        });

    }

    private void initListeners() {
        imgUserEdit.setOnClickListener(this);

    }

    private void updateUI() {

        AccountHolderInfo instance = AccountHolderInfo.getInstance();

        if (instance != null) {
            myProfile = instance.getUser();
            if(myProfile.getUserInfo().getUsername() == null){
                AccountHolderInfo.setAccountHolderInfoCallback(new AccountHolderInfoCallback() {
                    @Override
                    public void onAccountHolderIfoTaken(UserProfile userProfile) {
                        setProfileDetail(userProfile);
                    }
                });
            }else{
                setProfileDetail(myProfile);
            }
        }

    }


    private void checkBundle() {
        Bundle args = getArguments();
        if (args != null) {
            Boolean comingFromTab = (Boolean) args.getBoolean("comingFromTab");
            if (!comingFromTab) {
                //if not coming from Tab, edits disabled..
                imgUserEdit.setVisibility(View.GONE);
                menuLayout.setVisibility(View.GONE);
                backLayout.setVisibility(View.VISIBLE);
                imgBackBtn.setOnClickListener(this);
            }
        }
    }

    private void getData() {
        getUserInfo();
        checkLocationAndRetrievePosts();
    }


    private void setNavViewItems() {
        View v = navViewLayout.getHeaderView(0);
        navViewNameTv = v.findViewById(R.id.navViewNameTv);
        navViewEmailTv = v.findViewById(R.id.navViewEmailTv);
        navImgProfile = v.findViewById(R.id.navImgProfile);
        navViewShortenTextView = v.findViewById(R.id.navViewShortenTextView);
        profileNavViewLayout = v.findViewById(R.id.profileNavViewLayout);
        profileNavViewLayout.setBackground(ShapeUtil.getGradientBackground(getResources().getColor(R.color.Chocolate, null),
                getResources().getColor(R.color.DarkBlue, null)));
    }

    public void setDrawerListeners() {
        drawerLayout.addDrawerListener(new ActionBarDrawerToggle(getActivity(),
                drawerLayout,
                null,
                0,
                0) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mDrawerState = false;
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mDrawerState = true;
            }
        });

        menuImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuImgv.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                if (pendReqCntTv != null)
                    pendReqCntTv.setVisibility(View.GONE);

                if (mDrawerState) {
                    drawerLayout.closeDrawer(Gravity.START);
                } else {
                    drawerLayout.openDrawer(Gravity.START);
                }
            }
        });

        navViewLayout.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.searchItem:
                        drawerLayout.closeDrawer(Gravity.START);
                        startExplorePeopleFragment();
                        break;

                    case R.id.viewItem:
                        if (navPendReqCntTv != null)
                            navPendReqCntTv.setVisibility(View.GONE);

                        drawerLayout.closeDrawer(Gravity.START);
                        startPendingRequestFragment();
                        break;

                    case R.id.manageGroupsItem:
                        drawerLayout.closeDrawer(Gravity.START);
                        startGroupSettingFragment();
                        break;

                    case R.id.settingsItem:
                        drawerLayout.closeDrawer(Gravity.START);
                        startSettingsFragment();
                        break;

                    case R.id.reportProblemItem:
                        drawerLayout.closeDrawer(Gravity.START);
                        startNotifyProblemFragment();
                        break;

                    default:
                        break;
                }

                return false;
            }
        });

    }

    private void setProfileDetail(UserProfile user) {

        if (user != null && user.getUserInfo() != null) {

            Log.i("->UserInfo", user.getUserInfo().toString());

            //Name
            if (user.getUserInfo().getName() != null && !user.getUserInfo().getName().trim().isEmpty()) {
                navViewNameTv.setText(user.getUserInfo().getName());
            } else if (user.getUserInfo().getUsername() != null && !user.getUserInfo().getUsername().trim().isEmpty()) {
                navViewNameTv.setText(user.getUserInfo().getUsername());
            }
            //Username
            if (user.getUserInfo().getUsername() != null && !user.getUserInfo().getUsername().trim().isEmpty()) {
                toolbarTitle.setText(user.getUserInfo().getUsername());
                navViewEmailTv.setText(user.getUserInfo().getEmail().trim());
            }

            //navigation profile picture
            UserDataUtil.setProfilePicture(getContext(), user.getUserInfo().getProfilePhotoUrl(),
                    user.getUserInfo().getName(), user.getUserInfo().getUsername(), navViewShortenTextView, navImgProfile);

            if (user.getUserInfo().getIsPrivateAccount() != null) {
                getPendingFriendList();
            }
        }

        refresh_layout.setRefreshing(false);
    }

    private void getPendingFriendList() {

        AccountHolderFollowProcess.getPendingList(new CompleteCallback() {
            @Override
            public void onComplete(Object object) {
                if (object != null) {
                    FriendRequestList friendRequestList = (FriendRequestList) object;

                    if (friendRequestList.getResultArray() != null && friendRequestList.getResultArray().size() > 0) {
                        pendReqCntTv.setVisibility(View.VISIBLE);
                        pendReqCntTv.setText(Integer.toString(friendRequestList.getResultArray().size()));
                    } else
                        pendReqCntTv.setVisibility(View.GONE);


                    Menu menu = navViewLayout.getMenu();
                    for (int index = 0; index < menu.size(); index++) {
                        MenuItem menuItem = menu.getItem(index);
                        if (menuItem.getItemId() == R.id.viewItem) {
                            RelativeLayout rootView = (RelativeLayout) menuItem.getActionView();
                            navPendReqCntTv = rootView.findViewById(R.id.pendReqCntTv);

                            if (friendRequestList.getResultArray() != null && friendRequestList.getResultArray().size() > 0) {
                                navPendReqCntTv.setVisibility(View.VISIBLE);
                                navPendReqCntTv.setText(Integer.toString(friendRequestList.getResultArray().size()));
                            } else
                                navPendReqCntTv.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
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
                    myProfile = userProfile;
                    setHeaderInRecyclerView(myProfile);
                }

            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.LOG_FAIL("UserDetail", e.toString());
            }

            @Override
            public void onTaskContinue() {
            }
        }, AccountHolderInfo.getUserID(), AccountHolderInfo.getUserID(), token);

        loadUserDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setHeaderInRecyclerView(UserProfile userProfile) {
        profileAdapter.updateHeader(userProfile);
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
        String sUid = AccountHolderInfo.getUserID();
        String sLongitude = longitude;
        String sPerpage = String.valueOf(9);
        String sLatitude = latitude;
        String sRadius = radius;
        String sPage = String.valueOf(innerRecyclerPageCnt);
        String sPrivacyType = "";

        UserSharedPostListProcess userSharedPostListProcess = new UserSharedPostListProcess(getContext(), new OnEventListener<PostListResponse>() {
            @Override
            public void onSuccess(final PostListResponse postListResponse) {

                if (profileAdapter.isShowingProgressLoading()) {
                    profileAdapter.removeProgressLoading();
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

                if (profileAdapter.isShowingProgressLoading()) {
                    profileAdapter.removeProgressLoading();
                }

                refresh_layout.setRefreshing(false);

            }

            @Override
            public void onTaskContinue() {

                if (innerRecyclerPageCnt == 1 && !pulledToRefreshPost) {
                    profileAdapter.addProgressLoading();
                }

            }
        }, sUserId, sUid, sLongitude, sPerpage, sLatitude, sRadius, sPage, sPrivacyType, token);

        userSharedPostListProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }

    private void setPostsInRecyclerView(PostListResponse postListResponse) {

        objectList.addAll(postListResponse.getItems());
        loading = true;

        if (innerRecyclerPageCnt != 1 && profileAdapter.isShowingProgressLoading()) {
            profileAdapter.removeProgressLoading();
        }

        if (pulledToRefreshPost) {
            profileAdapter.updatePosts(postListResponse.getItems());
            pulledToRefreshPost = false;
        } else {
            if (innerRecyclerPageCnt == 1) {
                profileAdapter.addPosts(postListResponse.getItems());
            } else {
                profileAdapter.loadMorePost(postListResponse.getItems());
            }
        }

    }

    private void setLocationInfo() {
        longitude = String.valueOf(locationTrackObj.getLocation().getLongitude());
        latitude = String.valueOf(locationTrackObj.getLocation().getLatitude());
    }



    @Override
    public void onClick(View v) {

        if (v == imgUserEdit) {
            imgUserEdit.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
            userEditClicked();
        }

        if (v == imgBackBtn) {
            ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
            getActivity().onBackPressed();
        }


    }

    private void userEditClicked() {

        if (mFragmentNavigation != null) {
            //mFragmentNavigation.pushFragment(new UserEditFragment());
            mFragmentNavigation.pushFragment(new UserEditFragment(), ANIMATE_LEFT_TO_RIGHT);
        }

    }

    private void startSettingsFragment() {
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new SettingsFragment(), ANIMATE_RIGHT_TO_LEFT);
        }
    }

    private void startPendingRequestFragment() {
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new PendingRequestsFragment(), ANIMATE_LEFT_TO_RIGHT);
        }
    }

    private void startExplorePeopleFragment() {
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new ExplorePeopleFragment(), ANIMATE_LEFT_TO_RIGHT);
        }
    }

    private void startGroupSettingFragment() {
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new GroupManagementFragment(GROUP_OP_VIEW_TYPE,
                    new ReturnCallback() {
                        @Override
                        public void onReturn(Object object) {

                        }
                    }), ANIMATE_RIGHT_TO_LEFT);
        }
    }

    public void startNotifyProblemFragment() {
        if (mFragmentNavigation != null) {
            NextActivity.screenShotMainLayout.setVisibility(View.GONE);
            NextActivity.notifyProblemFragment = null;
            mFragmentNavigation.pushFragment(new NotifyProblemFragment(), ANIMATE_LEFT_TO_RIGHT);
        }
    }

}


