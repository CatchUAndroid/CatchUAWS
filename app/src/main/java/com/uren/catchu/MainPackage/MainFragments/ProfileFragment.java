package com.uren.catchu.MainPackage.MainFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;

import com.amazonaws.regions.Regions;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
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
                    printUserDetail();
                }

                @Override
                public void onFailure(Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Log.i("nerdeyiz", "c");
                    //
                }

                @Override
                public void onTaskContinue() {
                    Log.i("nerdeyiz", "a");
                    progressBar.setVisibility(View.VISIBLE);
                }
            });
            loadUserDetail.execute();
        }else{
            printUserDetail();
        }

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
