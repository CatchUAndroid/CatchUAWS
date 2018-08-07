package com.uren.catchu.MainPackage.MainFragments;

import android.annotation.SuppressLint;
import android.media.session.PlaybackState;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.regions.Regions;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.CircleTransform;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
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
    public ProgressBar progressBar;

    //@BindView(R.id.imgProfile)
    ImageView imgProfile;

    TabLayout tabs;
    ViewPager vpNews;

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
            initUI();

            tabs = (TabLayout) mView.findViewById(R.id.htab_tabs);
            vpNews = (ViewPager) mView.findViewById(R.id.htab_viewpager);
            setUpPager();
        }




        return mView;
    }

    private void setUpPager() {

        NewsPagerAdapter adp = new NewsPagerAdapter(getFragmentManager());

        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        vpNews.setAdapter(adp);
        vpNews.setOffscreenPageLimit(12);
        tabs.setupWithViewPager(vpNews);


    }

    private class NewsPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();

        public NewsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFrag(Fragment f, String title) {
            fragList.add(f);
            titleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragList.get(position);
        }

        @Override
        public int getCount() {
            return fragList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getCurrentUserInfo();
        getProfileDetail();

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

        ButterKnife.bind(this, mView);
        ((NextActivity) getActivity()).updateToolbarTitle("Profile");

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
                    printUserDetail();
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
