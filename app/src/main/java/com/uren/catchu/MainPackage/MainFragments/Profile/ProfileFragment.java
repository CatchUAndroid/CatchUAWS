package com.uren.catchu.MainPackage.MainFragments.Profile;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.graphics.Palette;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.uren.catchu.Adapters.SpecialSelectTabAdapter;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.GroupManagementFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.ExplorePeopleFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.FollowerFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.FollowingFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.NewsList;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.PendingRequestsFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.SettingsFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.UserEditFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.UserShareManagement.UserGroupsPostFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.UserShareManagement.UserSharedPostFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.FriendRequestList;
import catchu.model.UserProfile;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.CHAR_AMPERSAND;
import static com.uren.catchu.Constants.StringConstants.GROUP_OP_VIEW_TYPE;

public class ProfileFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    UserProfile myProfile;
    boolean mDrawerState;

    TextView navViewNameTv;
    TextView navViewUsernameTv;
    ImageView navImgProfile;
    TextView navViewShortenTextView;
    RelativeLayout profileNavViewLayout;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.htab_tabs)
    TabLayout tabs;
    @BindView(R.id.htab_viewpager)
    ViewPager vpNews;

    @BindView(R.id.htab_toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.imgProfile)
    ImageView imgProfile;
    @BindView(R.id.txtUserName)
    TextView txtUserName;
    @BindView(R.id.txtFollowerCnt)
    TextView txtFollowerCnt;
    @BindView(R.id.txtFollowingCnt)
    TextView txtFollowingCnt;
    @BindView(R.id.txtProfile)
    TextView txtProfile;
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
    @BindView(R.id.refresh_layout)
    RecyclerRefreshLayout refresh_layout;
    @BindView(R.id.htab_collapse_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.htab_appbar)
    AppBarLayout appBarLayout;

    TextView navPendReqCntTv;
    SpecialSelectTabAdapter adapter;

    boolean appBarLayoutCollapsed = false;
    boolean collapsedOneTime = false;

    UserSharedPostFragment userSharedPostFragment;
    UserGroupsPostFragment userGroupsPostFragment;

    public static ProfileFragment newInstance(Boolean comingFromTab) {
        Bundle args = new Bundle();
        args.putBoolean(ARGS_INSTANCE, comingFromTab);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {

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
            setCollapsingToolbar();
            setPullToRefreshListener();
            addListeners();
            setUpPager();
            setNavViewItems();


           /* CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
            AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
            behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return false;
                }
            });*/

            //System.out.println("appBarLayout.getTotalScrollRange()+++:" + appBarLayout.getTotalScrollRange());
        }

        return mView;
    }

    private void checkBundle() {
        Bundle args = getArguments();
        if (args != null) {
            Boolean comingFromTab = (Boolean) args.getBoolean(ARGS_INSTANCE);
            if (!comingFromTab) {
                //if not coming from Tab, edits disabled..
                imgUserEdit.setVisibility(View.GONE);
                menuLayout.setVisibility(View.GONE);
                backLayout.setVisibility(View.VISIBLE);
                imgBackBtn.setOnClickListener(this);
            }
        }
    }

    private void setNavViewItems() {
        View v = navViewLayout.getHeaderView(0);
        navViewNameTv = v.findViewById(R.id.navViewNameTv);
        navViewUsernameTv = v.findViewById(R.id.navViewUsernameTv);
        navImgProfile = v.findViewById(R.id.navImgProfile);
        navViewShortenTextView = v.findViewById(R.id.navViewShortenTextView);
        profileNavViewLayout = v.findViewById(R.id.profileNavViewLayout);
        profileNavViewLayout.setBackground(ShapeUtil.getGradientBackground(getResources().getColor(R.color.Chocolate, null),
                getResources().getColor(R.color.DarkBlue, null)));
    }

    private void setPullToRefreshListener() {
        refresh_layout.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AccountHolderInfo.getToken(new TokenCallback() {
                    @Override
                    public void onTokenTaken(String token) {
                        startGetProfileDetail(AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid(), token);
                        userGroupsPostFragment.getUserGroups();
                    }
                });
            }
        });
    }

    private void setCollapsingToolbar() {

        try {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.header);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @SuppressWarnings("ResourceType")
                @Override
                public void onGenerated(Palette palette) {

                    int vibrantColor = palette.getVibrantColor(R.color.primary_500);
                    int vibrantDarkColor = palette.getDarkVibrantColor(R.color.primary_700);
                    collapsingToolbarLayout.setContentScrimColor(vibrantColor);
                    collapsingToolbarLayout.setStatusBarScrimColor(vibrantDarkColor);
                    //
                }
            });

        } catch (Exception e) {
            // if Bitmap fetch fails, fallback to primary colors
            Log.e("TAG", "onCreate: failed to create bitmap from background", e.fillInStackTrace());
            collapsingToolbarLayout.setContentScrimColor(
                    ContextCompat.getColor(getActivity(), R.color.primary_500)
            );
            collapsingToolbarLayout.setStatusBarScrimColor(
                    ContextCompat.getColor(getActivity(), R.color.primary_700)
            );
        }
    }

    public void addListeners() {
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

                    default:
                        break;
                }

                return false;
            }
        });

        /*appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                System.out.println("appBarLayout.verticalOffset     :" + verticalOffset);
                //System.out.println("appBarLayout.getTotalScrollRange:" + appBarLayout.getTotalScrollRange());

                if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                    //  Collapsed
                    appBarLayoutCollapsed = true;
                    collapsedOneTime = true;
                    refresh_layout.setRefreshing(false);
                    refresh_layout.setOnRefreshListener(null);

                } else if (verticalOffset < 0) {
                    refresh_layout.setRefreshing(false);
                    refresh_layout.setOnRefreshListener(null);
                } else if (verticalOffset == 0) {
                    setPullToRefreshListener();
                }
            }
        });*/
    }

    private void setUpPager() {

        adapter = new SpecialSelectTabAdapter(getChildFragmentManager());
        userSharedPostFragment = new UserSharedPostFragment();
        NewsList n2 = new NewsList();
        userGroupsPostFragment = new UserGroupsPostFragment(AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid());

        adapter.addFragment(userSharedPostFragment, "World");
        adapter.addFragment(n2, "Special");
        adapter.addFragment(userGroupsPostFragment, "International");

        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        vpNews.setAdapter(adapter);
        vpNews.setOffscreenPageLimit(12);
        tabs.setupWithViewPager(vpNews);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateUI();
        initListeners();
    }

    private void initListeners() {

        imgUserEdit.setOnClickListener(this);
        txtFollowerCnt.setOnClickListener(this);
        txtFollowingCnt.setOnClickListener(this);
    }

    private void updateUI() {

        if (AccountHolderInfo.getInstance().getUser().getUserInfo() != null) {
            myProfile = AccountHolderInfo.getInstance().getUser();
            setProfileDetail(myProfile);
        } else {
            getProfileDetail(AccountHolderInfo.getUserID());
        }

    }

    private void setProfileDetail(UserProfile user) {

        if (user != null && user.getUserInfo() != null) {

            Log.i("->UserInfo", user.getUserInfo().toString());

            if (user.getUserInfo().getName() != null && !user.getUserInfo().getName().trim().isEmpty()) {
                toolbarTitle.setText(user.getUserInfo().getName());
                navViewNameTv.setText(user.getUserInfo().getName());
            }

            UserDataUtil.setProfilePicture(getContext(), user.getUserInfo().getProfilePhotoUrl(),
                    user.getUserInfo().getName(), txtProfile, imgProfile);

            UserDataUtil.setProfilePicture(getContext(), user.getUserInfo().getProfilePhotoUrl(),
                    user.getUserInfo().getName(), navViewShortenTextView, navImgProfile);

            if (user.getUserInfo().getUsername() != null && !user.getUserInfo().getUsername().trim().isEmpty()) {
                txtUserName.setText(user.getUserInfo().getUsername());
                navViewUsernameTv.setText(CHAR_AMPERSAND + user.getUserInfo().getUsername().trim());
            }

            if (user.getUserInfo().getIsPrivateAccount() != null) {
                getPendingFriendList();
            }
        }
        setUserFollowerAndFollowingCnt(user);
        refresh_layout.setRefreshing(false);
    }

    private void setUserFollowerAndFollowingCnt(UserProfile user) {

        if (user != null && user.getRelationInfo() != null) {
            Log.i("->UserRelationCountInfo", user.getRelationInfo().toString());

            if (user.getRelationInfo().getFollowerCount() != null && !user.getRelationInfo().getFollowerCount().trim().isEmpty())
                txtFollowerCnt.setText(user.getRelationInfo().getFollowerCount() + "\n" + getActivity().getResources().getString(R.string.followers));

            if (user.getRelationInfo().getFollowingCount() != null && !user.getRelationInfo().getFollowingCount().trim().isEmpty())
                txtFollowingCnt.setText(user.getRelationInfo().getFollowingCount() + "\n" + getActivity().getResources().getString(R.string.followings));
        }
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

    private void getProfileDetail(final String userID) {

        if (myProfile == null) {

            AccountHolderInfo.getToken(new TokenCallback() {
                @Override
                public void onTokenTaken(String token) {
                    startGetProfileDetail(userID, token);
                }
            });

        } else {
            setProfileDetail(myProfile);
        }

    }

    private void startGetProfileDetail(final String userID, String token) {

        UserDetail loadUserDetail = new UserDetail(getContext(), new OnEventListener<UserProfile>() {

            @Override
            public void onSuccess(UserProfile up) {
                Log.i("userDetail", "successful");
                progressBar.setVisibility(View.GONE);
                myProfile = up;
                setProfileDetail(up);
                refresh_layout.setRefreshing(false);
            }

            @Override
            public void onFailure(Exception e) {
                refresh_layout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onTaskContinue() {
                progressBar.setVisibility(View.VISIBLE);
            }
        }, AccountHolderInfo.getUserID(), AccountHolderInfo.getUserID(), token);

        loadUserDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onClick(View v) {

        if (v == imgUserEdit) {
            imgUserEdit.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
            userEditClicked();
        }

        if (v == txtFollowerCnt) {
            txtFollowerCnt.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
            followerClicked();
        }

        if (v == txtFollowingCnt) {
            txtFollowingCnt.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
            followingClicked();
        }

        if (v == imgBackBtn) {
            ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
            getActivity().onBackPressed();
        }

    }

    private void userEditClicked() {

        if (mFragmentNavigation != null) {
            //mFragmentNavigation.pushFragment(new UserEditFragment());
            mFragmentNavigation.pushFragment(new UserEditFragment(), ANIMATE_RIGHT_TO_LEFT);
        }
    }

    private void followerClicked() {

        if (mFragmentNavigation != null) {
            //mFragmentNavigation.pushFragment(new UserEditFragment());
            mFragmentNavigation.pushFragment(new FollowerFragment(), ANIMATE_RIGHT_TO_LEFT);
        }
    }

    private void followingClicked() {

        if (mFragmentNavigation != null) {
            //mFragmentNavigation.pushFragment(new UserEditFragment());
            mFragmentNavigation.pushFragment(new FollowingFragment(), ANIMATE_RIGHT_TO_LEFT);
        }

    }

    private void startSettingsFragment() {
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new SettingsFragment(), ANIMATE_RIGHT_TO_LEFT);
        }
    }

    private void startPendingRequestFragment() {
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new PendingRequestsFragment(), ANIMATE_RIGHT_TO_LEFT);
        }
    }

    private void startExplorePeopleFragment() {
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new ExplorePeopleFragment(), ANIMATE_RIGHT_TO_LEFT);
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

}
