package com.uren.catchu.MainPackage.MainFragments.Profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.regions.Regions;
import com.squareup.picasso.Picasso;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.CircleTransform;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters.NewsPagerAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.NewsList;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.UserProfile;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ProfileFragment extends BaseFragment {

    View mView;
    UserProfile userProfile;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.htab_tabs)
    TabLayout tabs;
    @BindView(R.id.htab_viewpager)
    ViewPager vpNews;


    @BindView(R.id.imgProfile)
    ImageView imgProfile;


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
            //((NextActivity) getActivity()).updateToolbarTitle("Profile");

            setUpPager();
            setToolbarColor();
        }




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



        return mView;
    }

    private void setToolbarColor() {

        //Toolbar toolbar = (Toolbar) mView.findViewById(R.id.htab_toolbar);



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

        getCurrentUserInfo();
        getProfileDetail();
        updateUI();

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

    private void initUI() {


    }


    private void getProfileDetail() {

        if (userProfile == null) {

            //Asenkron Task başlatır.
            UserDetail loadUserDetail = new UserDetail(getApplicationContext(), new OnEventListener<UserProfile>() {

                @Override
                public void onSuccess(UserProfile u) {

                    Log.i("userDetail", "successful");
                    progressBar.setVisibility(View.GONE);
                    userProfile = u;
                    //updateUI();
                    //printUserDetail();
                }

                @Override
                public void onFailure(Exception e) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onTaskContinue() {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });
            loadUserDetail.execute();
        } else {
            printUserDetail();
        }

    }

    private void updateUI() {


        final String[] photoUrl = {
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/1.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/2.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/3.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/4.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/5.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/6.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/7.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/8.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/9.jpg"
        };


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CommonUtils.showToast(getActivity(), "yeni foto1..");
                //feed porfile picture
                Picasso.with(getActivity())
                        //.load(userProfile.getResultArray().get(0).getProfilePhotoUrl())
                        .load("https://s3.amazonaws.com/catchumobilebucket/UserProfile/1.jpg")
                        .transform(new CircleTransform())
                        .into(imgProfile);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.showToast(getActivity(), "yeni foto2..");
                        //feed porfile picture
                        Picasso.with(getActivity())
                                //.load(userProfile.getResultArray().get(0).getProfilePhotoUrl())
                                .load("https://s3.amazonaws.com/catchumobilebucket/UserProfile/2.jpg")
                                .transform(new CircleTransform())
                                .into(imgProfile);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.showToast(getActivity(), "yeni foto3..");
                                //feed porfile picture
                                Picasso.with(getActivity())
                                        //.load(userProfile.getResultArray().get(0).getProfilePhotoUrl())
                                        .load("https://s3.amazonaws.com/catchumobilebucket/UserProfile/3.jpg")
                                        .transform(new CircleTransform())
                                        .into(imgProfile);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        CommonUtils.showToast(getActivity(), "yeni foto4..");
                                        //feed porfile picture
                                        Picasso.with(getActivity())
                                                //.load(userProfile.getResultArray().get(0).getProfilePhotoUrl())
                                                .load("https://s3.amazonaws.com/catchumobilebucket/UserProfile/4.jpg")
                                                .transform(new CircleTransform())
                                                .into(imgProfile);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                CommonUtils.showToast(getActivity(), "yeni fot5o..");
                                                //feed porfile picture
                                                Picasso.with(getActivity())
                                                        //.load(userProfile.getResultArray().get(0).getProfilePhotoUrl())
                                                        .load("https://s3.amazonaws.com/catchumobilebucket/UserProfile/5.jpg")
                                                        .transform(new CircleTransform())
                                                        .into(imgProfile);

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        CommonUtils.showToast(getActivity(), "yeni foto6..");
                                                        //feed porfile picture
                                                        Picasso.with(getActivity())
                                                                //.load(userProfile.getResultArray().get(0).getProfilePhotoUrl())
                                                                .load("https://s3.amazonaws.com/catchumobilebucket/UserProfile/6.jpg")
                                                                .transform(new CircleTransform())
                                                                .into(imgProfile);

                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                CommonUtils.showToast(getActivity(), "yeni foto7..");
                                                                //feed porfile picture
                                                                Picasso.with(getActivity())
                                                                        //.load(userProfile.getResultArray().get(0).getProfilePhotoUrl())
                                                                        .load("https://s3.amazonaws.com/catchumobilebucket/UserProfile/7.jpg")
                                                                        .transform(new CircleTransform())
                                                                        .into(imgProfile);

                                                            }
                                                        }, 5000);

                                                    }
                                                }, 5000);

                                            }
                                        }, 5000);

                                    }
                                }, 5000);


                            }
                        }, 5000);

                    }
                }, 5000);


            }
        }, 5000);


    }

    private void printUserDetail() {

        CommonUtils.showToast(getActivity(), "Hoş geldin " + userProfile.getResultArray().get(0).getName() + "!!");
        Log.i("name ", userProfile.getResultArray().get(0).getName());
        Log.i("username ", userProfile.getResultArray().get(0).getUsername());
        Log.i("userId ", userProfile.getResultArray().get(0).getUserid());
        Log.i("isPrivateAcc ", userProfile.getResultArray().get(0).getIsPrivateAccount().toString());
        Log.i("profilePicUrl ", userProfile.getResultArray().get(0).getProfilePhotoUrl());

    }

}
