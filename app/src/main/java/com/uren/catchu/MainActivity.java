package com.uren.catchu;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.LoginProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.LoginPackage.LoginActivity;
import com.uren.catchu.LoginPackage.Models.LoginUser;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Singleton.AccountHolderInfo;

import catchu.model.BaseRequest;
import catchu.model.BaseResponse;
import catchu.model.User;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CommonUtils.LOG_NEREDEYIZ("MainActivity");

        initFacebookLogin();
        initTwitterLogin();

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            checkUser();
            startActivity(new Intent(this, NextActivity.class));
            finish();
        }

    }

    private void initFacebookLogin() {
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    private void initTwitterLogin() {

        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));

        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();

        Twitter.initialize(twitterConfig);

    }

    private void checkUser() {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startCheckUser(token);
            }
        });

    }

    private void startCheckUser(String token) {

        CommonUtils.LOG_NEREDEYIZ("startCheckUser()");

        User user = new User();
        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            /**
             * New user sign in
             */

            LoginUser loginUser = (LoginUser) getIntent().getSerializableExtra("LoginUser");
            user.setUserid(loginUser.getUserId());
            user.setUsername(loginUser.getUsername());
            user.setEmail(loginUser.getEmail());

            if (loginUser.getName() != null && !loginUser.getName().isEmpty()) {
                user.setName(loginUser.getName());
            }

            if (loginUser.getProfilePhotoUrl() != null && !loginUser.getProfilePhotoUrl().isEmpty()) {
                user.setProfilePhotoUrl(loginUser.getProfilePhotoUrl());
            }

        } else {

            /**
             * Already signed-in
             */

            user.setUserid(firebaseAuth.getCurrentUser().getUid());
            user.setEmail(firebaseAuth.getCurrentUser().getEmail());
            user.setUsername("not_important_here");


            Log.i("eeeemail", firebaseAuth.getCurrentUser().getEmail());
            Log.i("info", "burdayımm");
        }

        displayUserInfo(user);

        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setUser(user);

        LoginProcess loginProcess = new LoginProcess(this, new OnEventListener<BaseResponse>() {

            @Override
            public void onSuccess(BaseResponse baseResponse) {

                if (baseResponse == null) {
                    CommonUtils.LOG_OK_BUT_NULL("LoginProcess");
                } else {
                    CommonUtils.LOG_OK("LoginProcess");
                }
            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.LOG_FAIL("LoginProcess", e.toString());
            }

            @Override
            public void onTaskContinue() {

            }
        }, baseRequest, token);

        loginProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void displayUserInfo(User user) {

        Log.i("*******", "Current User *******");
        Log.i("-> userId", user.getUserid());
        Log.i("-> Email", user.getEmail());
        if (user.getName() != null)
            Log.i("-> Name", user.getName());
        if (user.getUsername() != null)
            Log.i("-> UserName", user.getUsername());
        if (user.getProfilePhotoUrl() != null)
            Log.i("-> ProfilePicUrl", user.getProfilePhotoUrl());

    }


}
