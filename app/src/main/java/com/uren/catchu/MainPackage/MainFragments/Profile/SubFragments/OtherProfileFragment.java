package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.Interfaces.CompleteCallback;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.YesNoDialogBoxCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.PersonListAdapter;
import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.SearchResultAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.UserPostFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters.FollowAdapter;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

import catchu.model.User;
import catchu.model.UserProfile;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_FOLLOWING;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_NONE;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_PENDING;
import static com.uren.catchu.Constants.StringConstants.FRIEND_CREATE_FOLLOW_DIRECTLY;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_FOLLOW;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_PENDING_FOLLOW_REQUEST;
import static com.uren.catchu.Constants.StringConstants.FRIEND_FOLLOW_REQUEST;
import static com.uren.catchu.Constants.StringConstants.PROFILE_POST_TYPE_SHARED;

public class OtherProfileFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    UserProfile otherProfile;  //serverdan getirilen user bilgilerini içerir
    User selectedUser;       // fragmenta aktarılan user'ın bilgilerini içerir
    UserInfoListItem userInfoListItem;

    String followStatus;
    private int followingCount, followerCount;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    //toolbar items
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;
    @BindView(R.id.commonToolbarbackImgv)
    ClickableImageView commonToolbarbackImgv;

    //profileimage
    @BindView(R.id.imgProfile)
    ImageView imgProfile;
    @BindView(R.id.txtProfile)
    TextView txtProfile;

    //profile detail
    @BindView(R.id.txtName)
    TextView txtName;
    @BindView(R.id.txtBio)
    TextView txtBio;

    //Follow variables
    @BindView(R.id.btnFollowStatus)
    Button btnFollowStatus;
    @BindView(R.id.txtFollowerCnt)
    TextView txtFollowerCnt;
    @BindView(R.id.txtFollowingCnt)
    TextView txtFollowingCnt;

    //posts
    @BindView(R.id.llMyPosts)
    LinearLayout llMyPosts;

    //Refresh layout
    @BindView(R.id.refresh_layout)
    RecyclerRefreshLayout refresh_layout;

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

            initToolbar();
            initListeners();
            updateUI();          //fragmenta aktarılan selectedUser üzerinden datalar set edilir.
            setPullToRefresh();

        }

        return mView;
    }

    private void initToolbar() {
        commonToolbarbackImgv.setOnClickListener(this);
        toolbarTitleTv.setText(getContext().getResources().getString(R.string.profile));
    }

    private void initListeners() {
        btnFollowStatus.setOnClickListener(this);
        llMyPosts.setOnClickListener(this);
    }

    private void setPullToRefresh() {

        refresh_layout.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                updateUI();
            }
        });
    }

    private void updateUI() {

        txtFollowerCnt.setClickable(false);
        txtFollowingCnt.setClickable(false);

        //username
        if (isValid(selectedUser.getUsername())) {
            toolbarTitleTv.setText(selectedUser.getUsername());
        }

        //profil fotografi varsa set edilir.
        UserDataUtil.setProfilePicture(getActivity(), selectedUser.getProfilePhotoUrl(), selectedUser.getName(),
                selectedUser.getUsername(), txtProfile, imgProfile);
        imgProfile.setPadding(3, 3, 3, 3);

        //Name
        if (isValid(selectedUser.getName())) {
            txtName.setText(selectedUser.getName());
        }

        /* eldeki datalar set edildikten sonra serverdan tüm bilgiler çekilir */
        getProfileDetail(); //userid ile user serverdan tekrar çekilir

    }

    private void getProfileDetail() {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetProfileDetail(token);
            }
        });
    }

    private void startGetProfileDetail(String token) {

        UserDetail loadUserDetail = new UserDetail(new OnEventListener<UserProfile>() {

            @Override
            public void onSuccess(UserProfile userProfile) {

                if (userProfile == null) {
                    CommonUtils.LOG_OK_BUT_NULL("UserDetail");

                } else {
                    CommonUtils.LOG_OK("UserDetail");
                    otherProfile = userProfile;
                    setProfileDetail();
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
        }, AccountHolderInfo.getUserID(), selectedUser.getUserid(), token);

        loadUserDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void setProfileDetail() {

        //toolbarTitle
        if (isValid(otherProfile.getUserInfo().getUsername())) {
            toolbarTitleTv.setText(otherProfile.getUserInfo().getUsername());
        } else {
            toolbarTitleTv.setText(R.string.profile);
        }

        //profil fotografi varsa set edilir.
        UserDataUtil.setProfilePicture(getActivity(), selectedUser.getProfilePhotoUrl(), selectedUser.getName(),
                selectedUser.getUsername(), txtProfile, imgProfile);
        imgProfile.setPadding(3, 3, 3, 3);
        //Name
        if (isValid(otherProfile.getUserInfo().getName())) {
            txtName.setText(otherProfile.getUserInfo().getName());
        }else if(isValid(otherProfile.getUserInfo().getUsername())){
            txtName.setText(otherProfile.getUserInfo().getUsername());
        }
        //Biography
        // todo NT - biography usera beslenmiyor.düzenlenecek

        //FollowStatus
        UserDataUtil.updateFollowButton2(getActivity(), otherProfile.getRelationInfo().getFollowStatus(), btnFollowStatus, true);

        setUserFollowerAndFollowingCnt(otherProfile);
        refresh_layout.setRefreshing(false);

    }


    private void setUserFollowerAndFollowingCnt(UserProfile user) {

        if (user != null && user.getRelationInfo() != null) {

            if (user.getRelationInfo().getFollowerCount() != null && !user.getRelationInfo().getFollowerCount().trim().isEmpty())
                txtFollowerCnt.setText(user.getRelationInfo().getFollowerCount());

            if (user.getRelationInfo().getFollowingCount() != null && !user.getRelationInfo().getFollowingCount().trim().isEmpty())
                txtFollowingCnt.setText(user.getRelationInfo().getFollowingCount());

        }
    }

    private void btnFollowStatusClicked() {

        //takip ediliyor ise
        if (otherProfile.getRelationInfo().getFollowStatus().equals(FOLLOW_STATUS_FOLLOWING)) {
            if (otherProfile.getUserInfo().getIsPrivateAccount() != null && otherProfile.getUserInfo().getIsPrivateAccount()) {
                openDialogBox();
            } else {
                updateFollowStatus(FRIEND_DELETE_FOLLOW);
            }
        } else if (otherProfile.getRelationInfo().getFollowStatus().equals(FOLLOW_STATUS_PENDING)) {
            //istek gonderilmis ise
            updateFollowStatus(FRIEND_DELETE_PENDING_FOLLOW_REQUEST);
        } else if (otherProfile.getRelationInfo().getFollowStatus().equals(FOLLOW_STATUS_NONE)) {
            //takip istegi yok ise
            if (otherProfile.getUserInfo().getIsPrivateAccount() != null && otherProfile.getUserInfo().getIsPrivateAccount()) {
                updateFollowStatus(FRIEND_FOLLOW_REQUEST);
            } else {
                updateFollowStatus(FRIEND_CREATE_FOLLOW_DIRECTLY);
            }
        } else {
            //do nothing
        }

    }

    private void updateFollowStatus(final String requestType) {

        AccountHolderFollowProcess.friendFollowRequest(requestType, AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid()
                , selectedUser.getUserid(), new CompleteCallback() {
                    @Override
                    public void onComplete(Object object) {
                        updateFollowUI(requestType);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        DialogBoxUtil.showErrorDialog(getActivity(), getActivity().getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                            @Override
                            public void okClick() {
                            }
                        });
                        btnFollowStatus.setEnabled(true);
                    }
                });
    }

    private void updateFollowUI(String requestType) {


        switch (requestType) {
            case FRIEND_DELETE_FOLLOW:
                followStatus = FOLLOW_STATUS_NONE;
                updateSelectedUserProfile();               //gelinen yerdeki kişinin follow statüsü(UI)
                updateOtherUserProfile(-1);     //other profildeki kişinin follow statüsü(UI)
                updateCurrentUserProfile(-1);  //current userın follow count degerleri (UI)

                break;

            case FRIEND_DELETE_PENDING_FOLLOW_REQUEST:
                followStatus = FOLLOW_STATUS_NONE;
                updateSelectedUserProfile();
                otherProfile.getRelationInfo().setFollowStatus(followStatus);
                break;

            case FRIEND_FOLLOW_REQUEST:
                followStatus = FOLLOW_STATUS_PENDING;
                updateSelectedUserProfile();
                otherProfile.getRelationInfo().setFollowStatus(followStatus);
                break;

            case FRIEND_CREATE_FOLLOW_DIRECTLY:
                followStatus = FOLLOW_STATUS_FOLLOWING;
                updateSelectedUserProfile();
                updateOtherUserProfile(1);
                updateCurrentUserProfile(1);
                break;

            default:
                break;
        }

        updateAdapters();
        UserDataUtil.updateFollowButton2(getActivity(), otherProfile.getRelationInfo().getFollowStatus(), btnFollowStatus, true);
    }

    private void updateSelectedUserProfile() {
        selectedUser.setFollowStatus(followStatus);
    }

    private void updateOtherUserProfile(int updateValue) {
        followerCount = Integer.parseInt(otherProfile.getRelationInfo().getFollowerCount());
        otherProfile.getRelationInfo().setFollowerCount(String.valueOf(followerCount + updateValue));
        otherProfile.getRelationInfo().setFollowStatus(followStatus);
        txtFollowerCnt.setText(otherProfile.getRelationInfo().getFollowerCount());
    }

    private void updateCurrentUserProfile(int updateValue) {
        UserProfile user = AccountHolderInfo.getInstance().getUser();
        followingCount = Integer.parseInt(user.getRelationInfo().getFollowingCount());
        user.getRelationInfo().setFollowingCount(String.valueOf(followingCount + updateValue));
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

    private void updateAdapters() {

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

    private boolean isValid(String name) {
        if (name != null && !name.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {

        if (v == commonToolbarbackImgv) {

            if (getActivity() instanceof NextActivity)
                ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;

            getActivity().onBackPressed();
        }

        if (v == btnFollowStatus) {
            btnFollowStatusClicked();
        }

        if(v == llMyPosts){
            String targetUid = selectedUser.getUserid();
            String toolbarTitle = selectedUser.getUsername();
            mFragmentNavigation.pushFragment(UserPostFragment.newInstance(PROFILE_POST_TYPE_SHARED, targetUid, toolbarTitle), ANIMATE_RIGHT_TO_LEFT);
        }

    }



}