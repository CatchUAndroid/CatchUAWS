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
import com.squareup.picasso.Picasso;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.CircleTransform;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters.NewsPagerAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.NewsList;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.UserProfile;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ProfileFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    UserProfile userProfile;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    String userid = "us-east-1:4af861e4-1cb6-4218-87e7-523c84bbfa96";

    @BindView(R.id.htab_tabs)
    TabLayout tabs;
    @BindView(R.id.htab_viewpager)
    ViewPager vpNews;

    @BindView(R.id.htab_toolbar)
    Toolbar toolbar;
    @BindView(R.id.imgProfile)
    ImageView imgProfile;
    @BindView(R.id.txtUserName)
    TextView txtUserName;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.imgUserEdit)
    ClickableImageView imgUserEdit;
    @BindView(R.id.imgSettings)
    ClickableImageView imgSettings;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
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
    }

    private void updateUI() {
        Log.i("durum ", "1");
        if (AccountHolderInfo.getInstance().getUser().getUserInfo() != null) {
            Log.i("durum ", "2");
            userProfile = AccountHolderInfo.getInstance().getUser();
            setProfileDetail();
        } else {
            Log.i("durum ", "3");
            getProfileDetail();
        }

    }

    private void setProfileDetail() {
        Log.i("durum ", "4");

        toolbarTitle.setText(userProfile.getUserInfo().getName());

        CommonUtils.showToast(getActivity(), "Hoş geldin " + userProfile.getUserInfo().getName() + "!!");

        Log.i("name ", userProfile.getUserInfo().getName());
        Log.i("username ", userProfile.getUserInfo().getUsername());
        Log.i("userId ", userProfile.getUserInfo().getUserid());
        Log.i("isPrivateAcc ", userProfile.getUserInfo().getIsPrivateAccount().toString());
        Log.i("profilePicUrl ", userProfile.getUserInfo().getProfilePhotoUrl());

        Picasso.with(getActivity())
                //.load(userProfile.getResultArray().get(0).getProfilePhotoUrl())
                .load("https://s3.amazonaws.com/catchumobilebucket/UserProfile/4.jpg")
                .transform(new CircleTransform())
                .into(imgProfile);

        txtUserName.setText(userProfile.getUserInfo().getUsername());
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

    private void getProfileDetail() {

        Log.i("durum ", "5");

        if (userProfile == null) {

            //Asenkron Task başlatır.
            UserDetail loadUserDetail = new UserDetail(getApplicationContext(), new OnEventListener<UserProfile>() {

                @Override
                public void onSuccess(UserProfile u) {
                    Log.i("durum ", "6");
                    Log.i("userDetail", "successful");
                    progressBar.setVisibility(View.GONE);
                    userProfile = u;
                    setProfileDetail();
                }

                @Override
                public void onFailure(Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Log.i("durum ", "7");
                }

                @Override
                public void onTaskContinue() {
                    Log.i("durum ", "8");
                    progressBar.setVisibility(View.VISIBLE);
                }
            }, userid);
            loadUserDetail.execute();
        } else {
            setProfileDetail();
        }

    }

    @Override
    public void onClick(View v) {

        if (v == imgUserEdit){
            CommonUtils.showToast(getActivity()," userEdit clicked");
            userEditClicked();
        }

        if(v == imgSettings){
            CommonUtils.showToast(getActivity()," settings clicked");
            settingsClicked();
        }

    }

    private void settingsClicked() {
    }

    private void userEditClicked() {

        NewsList nextFrag = new NewsList();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(((ViewGroup)getView().getParent()).getId(), nextFrag, "profileDetailFragment")
                .addToBackStack("profileDetailFragment")
                .commit();


    }
}
