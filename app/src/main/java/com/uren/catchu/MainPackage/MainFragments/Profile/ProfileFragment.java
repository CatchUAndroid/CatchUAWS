package com.uren.catchu.MainPackage.MainFragments.Profile;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.regions.Regions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.FollowInfoRowItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters.NewsPagerAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.FollowerFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.FollowingFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.NewsList;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.SettingsFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.UserEditFragment;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.UserProfile;
import catchu.model.UserProfileProperties;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.uren.catchu.Constants.StringConstants.AnimateLeftToRight;
import static com.uren.catchu.Constants.StringConstants.AnimateRightToLeft;

public class ProfileFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    UserProfile myProfile;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    // TODO: 11.9.2018 NT:constant kaldırılacak
    String userid = "us-east-1:4af861e4-1cb6-4218-87e7-523c84bbfa96";

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

    @BindView(R.id.imgUserEdit)
    ClickableImageView imgUserEdit;
    @BindView(R.id.imgSettings)
    ClickableImageView imgSettings;

    public static ProfileFragment newInstance(FollowInfoRowItem rowItem) {
        Bundle args = new Bundle();
        args.putSerializable(ARGS_INSTANCE, rowItem);
        ProfileFragment fragment = new ProfileFragment();
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
            mView = inflater.inflate(R.layout.fragment_profile, container, false);
            ButterKnife.bind(this, mView);

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

        imgUserEdit.setOnClickListener(this);
        imgSettings.setOnClickListener(this);
        txtFollowerCnt.setOnClickListener(this);
        txtFollowingCnt.setOnClickListener(this);

    }

    private void updateUI() {

        if (AccountHolderInfo.getInstance().getUser().getUserInfo() != null) {
            myProfile = AccountHolderInfo.getInstance().getUser();
            setProfileDetail(myProfile);
        } else {
            getProfileDetail(userid);
        }

    }

    private void setProfileDetail(UserProfile user) {

        toolbarTitle.setText(user.getUserInfo().getName());

        CommonUtils.showToast(getActivity(), "Hoş geldin " + user.getUserInfo().getName() + "!!");

        Log.i("name ", user.getUserInfo().getName());
        Log.i("username ", user.getUserInfo().getUsername());
        Log.i("userId ", user.getUserInfo().getUserid());
        Log.i("isPrivateAcc ", user.getUserInfo().getIsPrivateAccount().toString());
        Log.i("profilePicUrl ", user.getUserInfo().getProfilePhotoUrl());


        Glide.with(getActivity())
                .load(user.getUserInfo().getProfilePhotoUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(imgProfile);

        txtUserName.setText(user.getUserInfo().getUsername());
        txtFollowerCnt.setText(user.getRelationCountInfo().getFollowerCount() + "\n" + "follower");
        txtFollowingCnt.setText(user.getRelationCountInfo().getFollowingCount() + "\n" + "following");

    }


    private void getCurrentUserInfo() {

        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();

        JSONObject CognitoIdentity = configuration
                .optJsonObject("CredentialsProvider")
                .optJSONObject("CognitoIdentity")
                .optJSONObject("Default");

        String poolId = CognitoIdentity.opt("PoolId").toString();
        String region = CognitoIdentity.opt("Region").toString();

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(), // Context
                poolId, // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        String identityId = credentialsProvider.getIdentityId();
        Log.i("Identity Id ", identityId);
        Log.i("Pool Id     ", poolId);
        Log.i("Region      ", region);


    }

    private void getProfileDetail(String userID) {

        Log.i("gidilen UserId", userID);

        if (myProfile == null) {

            //Asenkron Task başlatır.
            UserDetail loadUserDetail = new UserDetail(getApplicationContext(), new OnEventListener<UserProfile>() {

                @Override
                public void onSuccess(UserProfile up) {
                    Log.i("userDetail", "successful");
                    progressBar.setVisibility(View.GONE);
                    myProfile = up;
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
            }, userID);

            loadUserDetail.execute();

        } else {
            setProfileDetail(myProfile);
        }

    }

    @Override
    public void onClick(View v) {

        if (v == imgUserEdit) {
            userEditClicked();
        }

        if (v == imgSettings) {
            settingsClicked();
        }

        if (v == txtFollowerCnt) {
            followerClicked();
        }

        if (v == txtFollowingCnt) {
            followingClicked();
        }

    }


    private void settingsClicked() {

        if (mFragmentNavigation != null) {

            mFragmentNavigation.pushFragment(new SettingsFragment(), AnimateLeftToRight);

            //mFragmentNavigation.pushFragment(new UserEditFragment());

        }

    }

    private void userEditClicked() {

        if (mFragmentNavigation != null) {

            mFragmentNavigation.pushFragment(new UserEditFragment(), AnimateRightToLeft);

            //mFragmentNavigation.pushFragment(new UserEditFragment());

        }

    }

    private void followerClicked() {

        if (mFragmentNavigation != null) {

            mFragmentNavigation.pushFragment(new FollowerFragment(), AnimateRightToLeft);

            //mFragmentNavigation.pushFragment(new UserEditFragment());

        }

    }

    private void followingClicked() {

        if (mFragmentNavigation != null) {

            mFragmentNavigation.pushFragment(new FollowingFragment(), AnimateRightToLeft);

            //mFragmentNavigation.pushFragment(new UserEditFragment());

        }

    }


}
