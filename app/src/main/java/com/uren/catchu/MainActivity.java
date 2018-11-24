package com.uren.catchu;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.LoginProcess;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.AnimationUtil;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.LoginPackage.AppIntroductionActivity;
import com.uren.catchu.LoginPackage.LoginActivity;
import com.uren.catchu.LoginPackage.Models.LoginUser;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Singleton.AccountHolderInfo;

import catchu.model.BaseRequest;
import catchu.model.BaseResponse;
import catchu.model.Provider;
import catchu.model.User;
import catchu.model.UserProfile;

public class MainActivity extends AppCompatActivity {

    ImageView appIconImgv;

    private FirebaseAuth firebaseAuth;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVariables();

        CommonUtils.LOG_NEREDEYIZ("MainActivity");

        initFacebookLogin();
        initTwitterLogin();

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else
            checkUser();
    }

    private void initVariables() {
        appIconImgv = findViewById(R.id.appIconImgv);
        AnimationUtil.blink(MainActivity.this, appIconImgv);
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
        fillUserInfo();
        displayUserInfo(user);
        startLoginProcess();
    }

    public void fillUserInfo(){
        user = new User();
        Bundle extras = getIntent().getExtras();
        Provider provider = new Provider();
        LoginUser loginUser = (LoginUser) getIntent().getSerializableExtra("LoginUser");

        if (extras != null && loginUser != null) {

            /**
             * New user sign in
             */
            user.setUserid(loginUser.getUserId());
            user.setUsername(loginUser.getUsername());
            user.setEmail(loginUser.getEmail());

            if (loginUser.getName() != null && !loginUser.getName().isEmpty()) {
                user.setName(loginUser.getName());
            }
            if (loginUser.getProfilePhotoUrl() != null && !loginUser.getProfilePhotoUrl().isEmpty()) {
                user.setProfilePhotoUrl(loginUser.getProfilePhotoUrl());
            }

            //Provider
            provider.setProviderid(loginUser.getProviderId());
            provider.setProviderType(loginUser.getProviderType());
            user.setProvider(provider);

        } else {

            /**
             * Already signed-in
             */
            user.setUserid(firebaseAuth.getCurrentUser().getUid());
            user.setEmail(firebaseAuth.getCurrentUser().getEmail());
            user.setUsername("default");
            provider.setProviderid("");
            provider.setProviderType("");
            user.setProvider(provider);
        }
    }

    public void startLoginProcess(){
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {

                final BaseRequest baseRequest = new BaseRequest();
                baseRequest.setUser(user);

                LoginProcess loginProcess = new LoginProcess(MainActivity.this, new OnEventListener<BaseResponse>() {

                    @Override
                    public void onSuccess(BaseResponse baseResponse) {

                        if (baseResponse == null) {
                            CommonUtils.LOG_OK_BUT_NULL("LoginProcess");
                        } else {
                            CommonUtils.LOG_OK("LoginProcess");
                            startActivity(new Intent(MainActivity.this, NextActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        CommonUtils.LOG_FAIL("LoginProcess", e.toString());
                    }

                    @Override
                    public void onTaskContinue() {

                    }
                }, user.getUserid(), baseRequest, token);

                loginProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

    private void displayUserInfo(User user) {

        if (user != null) {
            Log.i("*******", "currentUser *******");
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


}
