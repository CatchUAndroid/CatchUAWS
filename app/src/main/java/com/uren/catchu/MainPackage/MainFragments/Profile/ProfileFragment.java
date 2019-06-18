package com.uren.catchu.MainPackage.MainFragments.Profile;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.ApiModelsProcess.UserGroupsProcess;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.GroupManagementFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ProfileRefreshCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Activities.MessageListActivity;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.UnreadMessageCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses.MessageGetProcess;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.ShowSelectedPhotoFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.Adapters.GroupsListAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.UserPostFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SettingsManagement.NotifyProblemFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SettingsManagement.SettingsFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.ExplorePeopleFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.FollowerFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.FollowingFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.PendingRequestsFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.UserEditFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Utils.ProfileHelper;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.GroupListHolder;
import com.uren.catchu.Singleton.Interfaces.AccountHolderInfoCallback;
import com.uren.catchu.Singleton.Interfaces.GroupListHolderCallback;

import java.util.Collections;
import java.util.Comparator;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.FriendRequestList;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.UserProfile;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.NumericConstants.ORIENTATION_BOTTOM_TOP;
import static com.uren.catchu.Constants.NumericConstants.ORIENTATION_LEFT_RIGHT;
import static com.uren.catchu.Constants.NumericConstants.ORIENTATION_TOP_BOTTOM;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.CHAR_AMPERSAND;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_RECEIPT_USERID;
import static com.uren.catchu.Constants.StringConstants.GROUP_OP_VIEW_TYPE;
import static com.uren.catchu.Constants.StringConstants.PROFILE_POST_TYPE_CAUGHT;
import static com.uren.catchu.Constants.StringConstants.PROFILE_POST_TYPE_SHARED;

@SuppressLint("ValidFragment")
public class ProfileFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    UserProfile myProfile;
    boolean mDrawerState;

    TextView navViewNameTv;
    TextView navViewEmailTv;
    ImageView navImgProfile;
    TextView navViewShortenTextView;
    RelativeLayout profileNavViewLayout;
    TextView navPendReqCntTv;
    TextView navMessageCntTv;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.htab_toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.imgProfile)
    ImageView imgProfile;
    @BindView(R.id.txtProfile)
    TextView txtProfile;

    @BindView(R.id.txtName)
    TextView txtName;
    /*@BindView(R.id.txtBio)
    TextView txtBio;*/

    @BindView(R.id.txtFollowerCnt)
    TextView txtFollowerCnt;
    @BindView(R.id.txtFollowingCnt)
    TextView txtFollowingCnt;

    @BindView(R.id.requestWaitingCntTv)
    TextView requestWaitingCntTv;
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

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refresh_layout;


    @BindView(R.id.followersLayout)
    LinearLayout followersLayout;
    @BindView(R.id.followingsLayout)
    LinearLayout followingsLayout;
    @BindView(R.id.sharedPostCount)
    TextView sharedPostCount;
    @BindView(R.id.caughtPostCount)
    TextView caughtPostCount;

    //Groups
    @BindView(R.id.groupRecyclerView)
    RecyclerView groupRecyclerView;

    @BindView(R.id.llProfile)
    LinearLayout llProfile;
    @BindView(R.id.llSharedPosts)
    LinearLayout llSharedPosts;
    @BindView(R.id.llCatchedPosts)
    LinearLayout llCatchedPosts;


    @BindView(R.id.imgSharedPosts)
    ImageView imgSharedPosts;
    @BindView(R.id.imgCatchPosts)
    ImageView imgCatchPosts;
    @BindView(R.id.imgGroupPosts)
    ImageView imgGroupPosts;
    @BindView(R.id.imgForward1)
    ImageView imgForward1;
    @BindView(R.id.imgForward2)
    ImageView imgForward2;


    @BindView(R.id.llGroupsInfo)
    LinearLayout llGroupsInfo;
    @BindView(R.id.llGroupsRecycler)
    LinearLayout llGroupsRecycler;
    @BindView(R.id.txtGroupDetail)
    TextView txtGroupDetail;
    @BindView(R.id.txtEditGroup)
    TextView txtEditGroup;


    int unreadMessageCount = 0;
    int pendingRequestCount = 0;
    int waitingRequestCount = 0;

    private boolean comingFromTab;

    public ProfileFragment(boolean comingFromTab) {
        this.comingFromTab = comingFromTab;
    }

    @Override
    public void onStart() {
        getActivity().findViewById(R.id.tabMainLayout).setVisibility(View.VISIBLE);
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
            checkComingFrom();

            //Menu Layout
            setNavViewItems();
            setDrawerListeners();

            initListeners();
            updateUI();
            setPullToRefresh();
            setPostRefreshListener();

        }

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
    }

    private void setPostRefreshListener() {
        ProfileHelper.ProfileRefresh.getInstance().setProfileRefreshCallback(new ProfileRefreshCallback() {
            @Override
            public void onProfileRefresh() {
                CommonUtils.showToastShort(getContext(), "Profile..");
                refreshProfile();
            }
        });
    }

    private void initListeners() {

        imgUserEdit.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        followersLayout.setOnClickListener(this);
        followingsLayout.setOnClickListener(this);

        llSharedPosts.setOnClickListener(this);
        llCatchedPosts.setOnClickListener(this);
        txtEditGroup.setOnClickListener(this);

        //Gradients
        //llProfile.setBackground(ShapeUtil.getGradientBackgroundFromLeft(getResources().getColor(R.color.colorPrimary, null),
        //        getResources().getColor(R.color.profile_open_color, null), ORIENTATION_TOP_BOTTOM, 0));


        imgSharedPosts.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
        imgSharedPosts.setBackground(ShapeUtil.getShape(0,
                getContext().getResources().getColor(R.color.colorPrimary, null), GradientDrawable.OVAL, 15, 2));

        imgCatchPosts.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
        imgCatchPosts.setBackground(ShapeUtil.getShape(0,
                getContext().getResources().getColor(R.color.colorPrimary, null), GradientDrawable.OVAL, 15, 2));

        imgGroupPosts.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
        imgGroupPosts.setBackground(ShapeUtil.getShape(0,
                getContext().getResources().getColor(R.color.colorPrimary, null), GradientDrawable.OVAL, 15, 2));

        imgForward1.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
        imgForward2.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);


        //group layout
        llGroupsRecycler.setVisibility(View.GONE);
        llGroupsInfo.setVisibility(View.VISIBLE);
    }

    private void updateUI() {

        AccountHolderInfo instance = AccountHolderInfo.getInstance();

        if (instance != null) {
            myProfile = instance.getUser();
            if (myProfile.getUserInfo().getUsername() == null) {
                AccountHolderInfo.setAccountHolderInfoCallback(new AccountHolderInfoCallback() {
                    @Override
                    public void onAccountHolderIfoTaken(UserProfile userProfile) {
                        setProfileDetail(userProfile);
                    }
                });
            } else {
                setProfileDetail(myProfile);
            }
        } else {
            getProfileDetail(AccountHolderInfo.getUserID());
        }

        getGroupsFromSingleton();

    }


    private void setPullToRefresh() {

        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshProfile();
            }
        });
    }

    private void refreshProfile() {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetProfileDetail(AccountHolderInfo.getUserID(), token);
            }

            @Override
            public void onTokenFail(String message) {
            }
        });
        getGroupsHere();
    }

    private void checkComingFrom() {
        if (!comingFromTab) {
            //if not coming from Tab, edits disabled..
            imgUserEdit.setVisibility(View.GONE);
            menuLayout.setVisibility(View.GONE);
            txtEditGroup.setVisibility(View.GONE);
            backLayout.setVisibility(View.VISIBLE);
            imgBackBtn.setOnClickListener(this);
        }
    }

    private void setNavViewItems() {
        View v = navViewLayout.getHeaderView(0);
        navViewNameTv = v.findViewById(R.id.navViewNameTv);
        navViewEmailTv = v.findViewById(R.id.navViewEmailTv);
        navImgProfile = v.findViewById(R.id.navImgProfile);
        navViewShortenTextView = v.findViewById(R.id.navViewShortenTextView);
        //profileNavViewLayout = v.findViewById(R.id.profileNavViewLayout);
        //profileNavViewLayout.setBackground(ShapeUtil.getGradientBackground(getResources().getColor(R.color.Chocolate, null),
        //        getResources().getColor(R.color.DarkBlue, null)));
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
                if (requestWaitingCntTv != null)
                    requestWaitingCntTv.setVisibility(View.GONE);

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

                    case R.id.messagesItem:
                        if (navMessageCntTv != null)
                            navMessageCntTv.setVisibility(View.GONE);

                        drawerLayout.closeDrawer(Gravity.START);
                        startMessageListActivity();
                        break;

                    case R.id.settingsItem:
                        drawerLayout.closeDrawer(Gravity.START);
                        startSettingsFragment();
                        break;

                    case R.id.reportProblemItem:
                        drawerLayout.closeDrawer(Gravity.START);
                        startNotifyProblemFragment();
                        break;

                    case R.id.rateUs:
                        drawerLayout.closeDrawer(Gravity.START);
                        CommonUtils.commentApp(getContext());
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
                txtName.setText(user.getUserInfo().getName());
            } else if (user.getUserInfo().getUsername() != null && !user.getUserInfo().getUsername().trim().isEmpty()) {
                navViewNameTv.setText(user.getUserInfo().getUsername());
                txtName.setText(user.getUserInfo().getUsername());
            }
            //Username
            if (user.getUserInfo().getUsername() != null && !user.getUserInfo().getUsername().trim().isEmpty()) {
                toolbarTitle.setText(CHAR_AMPERSAND + user.getUserInfo().getUsername());
                navViewEmailTv.setText(user.getUserInfo().getEmail().trim());
            }
            //profile picture
            UserDataUtil.setProfilePicture(getContext(), user.getUserInfo().getProfilePhotoUrl(),
                    user.getUserInfo().getName(), user.getUserInfo().getUsername(), txtProfile, imgProfile);
            imgProfile.setPadding(3, 3, 3, 3);
            //navigation profile picture
            UserDataUtil.setProfilePicture(getContext(), user.getUserInfo().getProfilePhotoUrl(),
                    user.getUserInfo().getName(), user.getUserInfo().getUsername(), navViewShortenTextView, navImgProfile);

            // Animations
            Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
            Animation moveUpAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.move_up);
            imgProfile.startAnimation(moveUpAnimation); //Set animation to your ImageView
            //llProfile.startAnimation(fadeInAnimation);
            imgProfile.setPadding(3, 3, 3, 3);

            setWaitingRequestsCount(user);
        }

        setUserFollowerAndFollowingCnt(user);
        setPostCounts(user);
        refresh_layout.setRefreshing(false);
    }

    public void setWaitingRequestsCount(UserProfile user) {
        unreadMessageCount = 0;
        setNavViewMenuItems();

        if (user.getUserInfo().getIsPrivateAccount() != null)
            getPendingFriendList();
        else
            getUserUnreadMsgCount();
    }

    private void setNavViewMenuItems() {
        Menu menu = navViewLayout.getMenu();
        for (int index = 0; index < menu.size(); index++) {
            MenuItem menuItem = menu.getItem(index);
            if (menuItem.getItemId() == R.id.viewItem) {
                RelativeLayout rootView = (RelativeLayout) menuItem.getActionView();
                navPendReqCntTv = rootView.findViewById(R.id.pendReqCntTv);
            } else if (menuItem.getItemId() == R.id.messagesItem) {
                RelativeLayout rootView = (RelativeLayout) menuItem.getActionView();
                navMessageCntTv = rootView.findViewById(R.id.messageCntTv);
            }
        }
    }

    public void getUserUnreadMsgCount() {

        MessageGetProcess.getUnreadMessageCount(new UnreadMessageCallback() {
            @Override
            public void onReturn(int listSize) {
                unreadMessageCount = listSize;
                waitingRequestCount = unreadMessageCount + pendingRequestCount;

                if (requestWaitingCntTv.getVisibility() == View.GONE && waitingRequestCount > 0)
                    requestWaitingCntTv.setVisibility(View.VISIBLE);

                requestWaitingCntTv.setText(Integer.toString(waitingRequestCount));

                if (navMessageCntTv != null) {
                    if (unreadMessageCount > 0) {
                        if (navMessageCntTv.getVisibility() == View.GONE)
                            navMessageCntTv.setVisibility(View.VISIBLE);
                        navMessageCntTv.setText(Integer.toString(unreadMessageCount));
                    } else
                        navMessageCntTv.setVisibility(View.GONE);
                }
            }
        });

    }

    private void setPostCounts(UserProfile user) {

        if (user != null && user.getRelationInfo() != null) {

            if (user.getRelationInfo().getPostCount() != null && !user.getRelationInfo().getPostCount().toString().trim().isEmpty())
                sharedPostCount.setText(String.valueOf(user.getRelationInfo().getPostCount()));

            if (user.getRelationInfo().getCatchCount() != null && !user.getRelationInfo().getCatchCount().toString().trim().isEmpty())
                caughtPostCount.setText(String.valueOf(user.getRelationInfo().getCatchCount()));
        }

    }

    private void setUserFollowerAndFollowingCnt(UserProfile user) {

        if (user != null && user.getRelationInfo() != null) {
            Log.i("->UserRelationCountInfo", user.getRelationInfo().toString());

            if (user.getRelationInfo().getFollowerCount() != null && !user.getRelationInfo().getFollowerCount().trim().isEmpty())
                txtFollowerCnt.setText(user.getRelationInfo().getFollowerCount());

            if (user.getRelationInfo().getFollowingCount() != null && !user.getRelationInfo().getFollowingCount().trim().isEmpty())
                txtFollowingCnt.setText(user.getRelationInfo().getFollowingCount());

        }
    }

    private void getPendingFriendList() {

        AccountHolderFollowProcess.getPendingList(new CompleteCallback() {
            @Override
            public void onComplete(Object object) {
                if (object != null) {
                    FriendRequestList friendRequestList = (FriendRequestList) object;

                    if (friendRequestList.getResultArray() != null && friendRequestList.getResultArray().size() > 0) {
                        pendingRequestCount = friendRequestList.getResultArray().size();
                        requestWaitingCntTv.setVisibility(View.VISIBLE);
                        requestWaitingCntTv.setText(Integer.toString(pendingRequestCount));
                    } else
                        pendingRequestCount = 0;

                    if (navPendReqCntTv != null) {
                        if (friendRequestList.getResultArray() != null && friendRequestList.getResultArray().size() > 0) {
                            navPendReqCntTv.setVisibility(View.VISIBLE);
                            navPendReqCntTv.setText(Integer.toString(friendRequestList.getResultArray().size()));
                        } else
                            navPendReqCntTv.setVisibility(View.GONE);
                    }
                }

                getUserUnreadMsgCount();
            }

            @Override
            public void onFailed(Exception e) {
                getUserUnreadMsgCount();
            }
        });
    }

    private void getProfileDetail(final String userID) {

        if (myProfile == null) {

            AccountHolderInfo.getToken(new TokenCallback() {
                @Override
                public void onTokenTaken(String token) {
                    startGetProfileDetail(userID, token);
                }

                @Override
                public void onTokenFail(String message) {
                    refresh_layout.setRefreshing(false);
                }
            });

        } else {
            setProfileDetail(myProfile);
        }

    }

    private void startGetProfileDetail(final String userID, String token) {

        UserDetail loadUserDetail = new UserDetail(new OnEventListener<UserProfile>() {

            @Override
            public void onSuccess(UserProfile up) {

                if (up == null) {
                    CommonUtils.LOG_OK_BUT_NULL("UserDetail");
                } else {
                    CommonUtils.LOG_OK("UserDetail");
                    myProfile = up;
                    setProfileDetail(up);
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.LOG_FAIL("UserDetail", e.toString());
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onTaskContinue() {
                progressBar.setVisibility(View.VISIBLE);
            }
        }, AccountHolderInfo.getUserID(), AccountHolderInfo.getUserID(), "false", token);

        loadUserDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getGroupsFromSingleton() {

        GroupListHolder groupListHolderInstance = GroupListHolder.getInstance();

        if (groupListHolderInstance != null && groupListHolderInstance.getGroupList() != null) {
            GroupRequestResult groupRequestResult = groupListHolderInstance.getGroupList();
            if (groupRequestResult != null && groupRequestResult.getResultArray() != null) {
                //CommonUtils.showToastShort(getContext(), "grupları singletondan hemen aldım");
                setGroupRecyclerView(groupRequestResult);
            } else {
                GroupListHolder.setGroupListHolderCallback(new GroupListHolderCallback() {
                    @Override
                    public void onGroupListInfoTaken(GroupRequestResult groupRequestResult) {
                        //CommonUtils.showToastShort(getContext(), "grupları singletondan Callback ile aldım");
                        setGroupRecyclerView(GroupListHolder.getInstance().getGroupList());
                    }
                });
            }
        } else {
            getGroupsHere();
        }

    }

    private void getGroupsHere() {

        UserGroupsProcess.getGroups(AccountHolderInfo.getUserID(),
                new CompleteCallback() {
                    @Override
                    public void onComplete(Object object) {
                        //CommonUtils.showToastShort(getContext(), "grupları bu sayfadan aldım- Not singleton");
                        GroupRequestResult groupRequestResult = (GroupRequestResult) object;
                        setGroupRecyclerView(groupRequestResult);
                    }

                    @Override
                    public void onFailed(Exception e) {

                        if (getContext() != null) {
                            DialogBoxUtil.showErrorDialog(getContext(), getActivity().getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                                @Override
                                public void okClick() {
                                }
                            });
                        }
                    }
                });
    }

    private void setGroupRecyclerView(GroupRequestResult groupRequestResult) {

        orderGroupByName(groupRequestResult);

        //layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        groupRecyclerView.setLayoutManager(layoutManager);
        groupRecyclerView.setItemViewCacheSize(25);

        //adapter
        GroupsListAdapter groupsListAdapter = new GroupsListAdapter(getContext(), mFragmentNavigation, groupRequestResult);
        groupRecyclerView.setAdapter(groupsListAdapter);

        //group layout
        if (groupRequestResult.getResultArray().size() > 0) {
            llGroupsInfo.setVisibility(View.GONE);
            llGroupsRecycler.setVisibility(View.VISIBLE);
        } else {
            llGroupsInfo.setVisibility(View.VISIBLE);
            llGroupsRecycler.setVisibility(View.GONE);
            txtGroupDetail.setText("Henüz grubunuz bulunmamaktadır.");
        }

    }

    private void orderGroupByName(GroupRequestResult groupRequestResult) {
        //order
        Collections.sort(groupRequestResult.getResultArray(), new Comparator<GroupRequestResultResultArrayItem>() {
            @Override
            public int compare(GroupRequestResultResultArrayItem o1, GroupRequestResultResultArrayItem o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }

        });
    }

    @Override
    public void onClick(View v) {

        if (v == imgUserEdit) {
            imgUserEdit.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
            userEditClicked();
        }

        if (v == imgBackBtn) {
            getActivity().onBackPressed();
        }

        if (v == llSharedPosts) {
            String targetUid = AccountHolderInfo.getUserID();
            String toolbarTitle = getContext().getResources().getString(R.string.sharedPosts);
            mFragmentNavigation.pushFragment(UserPostFragment.newInstance(PROFILE_POST_TYPE_SHARED, targetUid, toolbarTitle), ANIMATE_RIGHT_TO_LEFT);
        }

        if (v == llCatchedPosts) {
            String targetUid = AccountHolderInfo.getUserID();
            String toolbarTitle = getContext().getResources().getString(R.string.caughtPosts);
            mFragmentNavigation.pushFragment(UserPostFragment.newInstance(PROFILE_POST_TYPE_CAUGHT, targetUid, toolbarTitle), ANIMATE_RIGHT_TO_LEFT);
        }

        if (v == followingsLayout) {
            followingsLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
            followingClicked();
        }

        if (v == followersLayout) {
            followersLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
            followerClicked();
        }

        if (v == imgProfile) {
            if (myProfile != null && myProfile.getUserInfo() != null && myProfile.getUserInfo().getProfilePhotoUrl() != null &&
                    !myProfile.getUserInfo().getProfilePhotoUrl().isEmpty()) {
                mFragmentNavigation.pushFragment(new ShowSelectedPhotoFragment(myProfile.getUserInfo().getProfilePhotoUrl()));
            }
        }

        if (v == txtEditGroup) {
            startGroupSettingFragment();
        }
    }

    private void userEditClicked() {
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new UserEditFragment(), ANIMATE_LEFT_TO_RIGHT);
        }
    }

    private void followerClicked() {
        if (mFragmentNavigation != null) {
            String requestedUserId = AccountHolderInfo.getUserID();
            UserProfileProperties user = AccountHolderInfo.getInstance().getUser().getUserInfo();
            mFragmentNavigation.pushFragment(new FollowerFragment(requestedUserId,
                    UserDataUtil.getNameOrUsername(user.getName(), user.getUsername())), ANIMATE_RIGHT_TO_LEFT);
        }
    }

    private void followingClicked() {
        if (mFragmentNavigation != null) {
            String requestedUserId = AccountHolderInfo.getUserID();
            UserProfileProperties user = AccountHolderInfo.getInstance().getUser().getUserInfo();
            mFragmentNavigation.pushFragment(new FollowingFragment(requestedUserId,
                    UserDataUtil.getNameOrUsername(user.getName(), user.getUsername())), ANIMATE_RIGHT_TO_LEFT);
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

    public void startMessageListActivity() {
        if (MessageListActivity.thisActivity != null) {
            MessageListActivity.thisActivity.finish();
        }
        Intent intent = new Intent(getContext(), MessageListActivity.class);
        intent.putExtra(FCM_CODE_RECEIPT_USERID, AccountHolderInfo.getUserID());
        startActivity(intent);
    }

    public void startNotifyProblemFragment() {
        if (mFragmentNavigation != null) {
            getActivity().findViewById(R.id.screenShotMainLayout).setVisibility(View.GONE);
            NextActivity.notifyProblemFragment = null;
            mFragmentNavigation.pushFragment(new NotifyProblemFragment(), ANIMATE_LEFT_TO_RIGHT);
        }
    }

}