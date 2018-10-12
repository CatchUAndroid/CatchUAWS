package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uren.catchu.Adapters.UserDetailAdapter;
import com.uren.catchu.ApiGatewayFunctions.FriendRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.YesNoDialogBoxCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.FollowInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters.FollowAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters.NewsPagerAdapter;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.FollowInfoResultArrayItem;
import catchu.model.FriendRequestList;
import catchu.model.UserProfile;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.FRIEND_CREATE_FOLLOW_DIRECTLY;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_FOLLOW;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_PENDING_FOLLOW_REQUEST;
import static com.uren.catchu.Constants.StringConstants.FRIEND_FOLLOW_REQUEST;

public class OtherProfileFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    UserProfile otherProfile;
    FollowInfoListItem followInfoListItem;
    FollowInfoResultArrayItem selectedProfile;
    private int followingCount, followerCount;

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

    @BindView(R.id.imgBackBtn)
    ClickableImageView imgBackBtn;

    @BindView(R.id.btnFollowStatus)
    Button btnFollowStatus;

    /**
     * @param rowItem içerisinde userId, isFollow degerleri zorunlu olarak beslenmelidir.
     */
    public static OtherProfileFragment newInstance(FollowInfoListItem rowItem) {
        Bundle args = new Bundle();
        args.putSerializable(ARGS_INSTANCE, rowItem);
        OtherProfileFragment fragment = new OtherProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
                followInfoListItem = (FollowInfoListItem) args.getSerializable(ARGS_INSTANCE);
                selectedProfile = followInfoListItem.getResultArrayItem();
            }

            setCollapsingToolbar();
            setUpPager();
        }


        return mView;
    }

    private void setCollapsingToolbar() {

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) mView.findViewById(R.id.htab_collapse_toolbar);

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


    private void setUpPager() {

        NewsPagerAdapter adp = new NewsPagerAdapter(getFragmentManager());
        NewsList n1 = new NewsList();
        NewsList n2 = new NewsList();
        NewsList n3 = new NewsList();
        NewsList n4 = new NewsList();
        NewsList n5 = new NewsList();

        adp.addFrag(n1, "World");
        adp.addFrag(n2, "Special");
        adp.addFrag(n3, "International");
        adp.addFrag(n4, "Technology");
        adp.addFrag(n5, "Finance");

        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        vpNews.setAdapter(adp);
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
        imgBackBtn.setOnClickListener(this);
        btnFollowStatus.setOnClickListener(this);
    }

    private void updateUI() {

        txtFollowerCnt.setClickable(false);
        txtFollowingCnt.setClickable(false);
        UserDataUtil.updateFollowButton(getActivity(), selectedProfile.getIsFollow(), selectedProfile.getIsPendingRequest(), btnFollowStatus, true);
        getProfileDetail(selectedProfile.getUserid());
    }

    private void setProfileDetail(UserProfile user) {
        toolbarTitle.setText(user.getUserInfo().getName());
        txtUserName.setText(user.getUserInfo().getUsername());
        txtFollowerCnt.setText(user.getRelationCountInfo().getFollowerCount() + "\n" + "follower");
        txtFollowingCnt.setText(user.getRelationCountInfo().getFollowingCount() + "\n" + "following");

        UserDataUtil.setProfilePicture(getActivity(), user.getUserInfo().getProfilePhotoUrl(), user.getUserInfo().getName(),
                txtUserName, imgProfile);
    }

    private void getProfileDetail(final String userID) {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetProfileDetail(userID, token);
            }
        });
    }

    private void startGetProfileDetail(final String userID, String token) {

        UserDetail loadUserDetail = new UserDetail(getApplicationContext(), new OnEventListener<UserProfile>() {

            @Override
            public void onSuccess(UserProfile up) {
                progressBar.setVisibility(View.GONE);
                otherProfile = up;
                setProfileDetail(up);
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onTaskContinue() {
                progressBar.setVisibility(View.VISIBLE);
            }
        }, userID, token);

        loadUserDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onClick(View v) {

        if (v == imgBackBtn) {

            ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;

            if (followInfoListItem.getAdapter() instanceof FollowAdapter) {
                ((FollowAdapter) followInfoListItem.getAdapter()).updateAdapterWithPosition(followInfoListItem.getClickedPosition());
            } else if (followInfoListItem.getAdapter() instanceof UserDetailAdapter) {
                ((UserDetailAdapter) followInfoListItem.getAdapter()).updateAdapterWithPosition(followInfoListItem.getClickedPosition());
            }

            getActivity().onBackPressed();
        }

        if (v == btnFollowStatus) {
            btnFollowStatusClicked();
        }
    }

    private void btnFollowStatusClicked() {

        if (selectedProfile.getIsFollow() != null && selectedProfile.getIsFollow()) {
            /*takip ediyorsak takibi bırak*/
            if (selectedProfile.getIsPrivateAccount() != null && selectedProfile.getIsPrivateAccount()) {
                openDialogBox();
            } else {
                updateFollowStatus(FRIEND_DELETE_FOLLOW);
            }

        } else if (selectedProfile.getIsPendingRequest() != null && selectedProfile.getIsPendingRequest()) {
            /*İstek gönderdiysek isteği iptal et*/
            updateFollowStatus(FRIEND_DELETE_PENDING_FOLLOW_REQUEST);
        } else {
            /*Takip etmiyorsak istek gönder*/
            if (selectedProfile.getIsPrivateAccount() != null && selectedProfile.getIsPrivateAccount()) {
                updateFollowStatus(FRIEND_FOLLOW_REQUEST);
            } else {
                updateFollowStatus(FRIEND_CREATE_FOLLOW_DIRECTLY);
            }
        }
    }

    private void updateFollowStatus(final String requestType) {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startUpdateFollowStatus(requestType, token);
            }
        });
    }

    private void startUpdateFollowStatus(final String requestType, String token) {

        FriendRequestProcess friendRequestProcess = new FriendRequestProcess(new OnEventListener<FriendRequestList>() {
            @Override
            public void onSuccess(FriendRequestList object) {
                updateFollowUI(requestType);
            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.showToastLong(getContext(), getContext().getResources().getString(R.string.error) + e.toString());
            }

            @Override
            public void onTaskContinue() {

            }
        }, requestType
                , AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid()
                , selectedProfile.getUserid()
                , token);

        friendRequestProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void updateFollowUI(String requestType) {

        UserProfile user = AccountHolderInfo.getInstance().getUser();

        switch (requestType) {
            case FRIEND_DELETE_FOLLOW:
                selectedProfile.setIsFollow(false);
                selectedProfile.setIsPendingRequest(false);
                followingCount = Integer.parseInt(user.getRelationCountInfo().getFollowingCount());
                user.getRelationCountInfo().setFollowingCount(String.valueOf(followingCount - 1));
                followerCount = Integer.parseInt(otherProfile.getRelationCountInfo().getFollowerCount());
                otherProfile.getRelationCountInfo().setFollowerCount(String.valueOf(followerCount - 1));
                txtFollowerCnt.setText(otherProfile.getRelationCountInfo().getFollowerCount() + "\n" + "follower");
                break;

            case FRIEND_DELETE_PENDING_FOLLOW_REQUEST:
                selectedProfile.setIsFollow(false);
                selectedProfile.setIsPendingRequest(false);
                break;

            case FRIEND_FOLLOW_REQUEST:
                selectedProfile.setIsPendingRequest(true);
                break;

            case FRIEND_CREATE_FOLLOW_DIRECTLY:
                selectedProfile.setIsFollow(true);
                followingCount = Integer.parseInt(user.getRelationCountInfo().getFollowingCount());
                user.getRelationCountInfo().setFollowingCount(String.valueOf(followingCount + 1));
                followerCount = Integer.parseInt(otherProfile.getRelationCountInfo().getFollowerCount());
                otherProfile.getRelationCountInfo().setFollowerCount(String.valueOf(followerCount + 1));
                txtFollowerCnt.setText(otherProfile.getRelationCountInfo().getFollowerCount() + "\n" + "follower");
                break;

            default:
                break;
        }

        UserDataUtil.updateFollowButton(getActivity(), selectedProfile.getIsFollow(), selectedProfile.getIsPendingRequest(), btnFollowStatus, true);
    }

    private void openDialogBox() {

        YesNoDialogBoxCallback yesNoDialogBoxCallback = new YesNoDialogBoxCallback() {
            @Override
            public void yesClick() {
                updateFollowStatus(FRIEND_DELETE_FOLLOW);
            }

            @Override
            public void noClick() {

            }
        };

        DialogBoxUtil.showYesNoDialog(getContext(), "", getContext().getString(R.string.cancel_following), yesNoDialogBoxCallback);

    }


}
