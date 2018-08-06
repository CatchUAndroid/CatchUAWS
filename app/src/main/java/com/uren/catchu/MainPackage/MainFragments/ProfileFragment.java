package com.uren.catchu.MainPackage.MainFragments;

import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.regions.Regions;
import com.squareup.picasso.Picasso;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.CircleTransform;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.UserProfile;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ProfileFragment extends BaseFragment {

    View mView;
    UserProfile userProfile;

    @BindView(R.id.progressBar)
    public ProgressBar progressBar;

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
        }

        initUI();

        return mView;
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

        if(userProfile == null){

            //Asenkron Task başlatır.
            UserDetail loadUserDetail = new UserDetail(getApplicationContext(), new OnEventListener<UserProfile>() {

                @Override
                public void onSuccess(UserProfile u) {

                    Log.i("userDetail", "successful");
                    progressBar.setVisibility(View.GONE);
                    userProfile = u;
                    updateUI();
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
        }else{
            printUserDetail();
        }

    }

    int i= 0;
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

        CommonUtils.showToast(getActivity(), "Hoş geldin " + userProfile.getResultArray().get(0).getName() +"!!");
        Log.i("name ", userProfile.getResultArray().get(0).getName());
        Log.i("username ", userProfile.getResultArray().get(0).getUsername());
        Log.i("userId ", userProfile.getResultArray().get(0).getUserid());
        Log.i("isPrivateAcc ", userProfile.getResultArray().get(0).getIsPrivateAccount().toString());
        Log.i("profilePicUrl ", userProfile.getResultArray().get(0).getProfilePhotoUrl());

    }

}
