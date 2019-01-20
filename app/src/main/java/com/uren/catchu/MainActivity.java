package com.uren.catchu;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.LoginProcess;
import com.uren.catchu.GeneralUtils.AnimationUtil;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.LoginPackage.LoginActivity;
import com.uren.catchu.LoginPackage.Models.LoginUser;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Activities.MessageWithPersonActivity;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Activities.MessageListActivity;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Singleton.AccountHolderInfo;

import catchu.model.BaseRequest;
import catchu.model.BaseResponse;
import catchu.model.Provider;
import catchu.model.User;
import io.fabric.sdk.android.Fabric;

import static com.uren.catchu.Constants.StringConstants.FCM_CODE_RECEIPT_USERID;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_SENDER_USERID;
import static com.uren.catchu.Constants.StringConstants.FCM_MESSAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.FCM_MESSAGE_TYPE_CLUSTER_TO_PERSON;
import static com.uren.catchu.Constants.StringConstants.FCM_MESSAGE_TYPE_NORMAL_TO_PERSON;

public class MainActivity extends AppCompatActivity {

    RelativeLayout mainActLayout;
    ImageView appIconImgv;
    RecyclerRefreshLayout refresh_layout;
    Button tryAgainButton;
    TextView networkTryDesc;

    private FirebaseAuth firebaseAuth;
    User user;
    String receiptUserId = null;
    String senderUserId = null;
    String messagingType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonUtils.showToastShort(getApplicationContext(), "maindeyiz");
        setContentView(R.layout.activity_main);
        Fabric.with(this, new Crashlytics());
        CommonUtils.hideKeyBoard(this);
        initVariables();

        CommonUtils.LOG_NEREDEYIZ("MainActivity");

        initFacebookLogin();
        initTwitterLogin();

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else
            fillUserInfo();
    }

    private void initVariables() {
        mainActLayout = findViewById(R.id.mainActLayout);
        refresh_layout = findViewById(R.id.refresh_layout);
        appIconImgv = findViewById(R.id.appIconImgv);
        tryAgainButton = findViewById(R.id.tryAgainButton);
        networkTryDesc = findViewById(R.id.networkTryDesc);
        AnimationUtil.blink(MainActivity.this, appIconImgv);
        tryAgainButton.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparentBlack, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 2));
        setPullToRefresh();
        addListeners();
    }

    private void addListeners() {
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryAgainButton.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.image_click));
                loginProcess();
            }
        });
    }

    private void setPullToRefresh() {
        refresh_layout.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loginProcess();
            }
        });
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

    public void fillUserInfo() {
        user = new User();
        Bundle extras = getIntent().getExtras();
        Provider provider = new Provider();
        LoginUser loginUser = (LoginUser) getIntent().getSerializableExtra("LoginUser");
        receiptUserId = (String) getIntent().getSerializableExtra(FCM_CODE_RECEIPT_USERID);
        senderUserId = (String) getIntent().getSerializableExtra(FCM_CODE_SENDER_USERID);
        messagingType = (String) getIntent().getSerializableExtra(FCM_MESSAGE_TYPE);

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
            displayUserInfo(user);
            loginProcess();

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
            displayUserInfo(user);
            loginProcess();
        }
    }

    public void loginProcess() {
        if (!CommonUtils.isNetworkConnected(MainActivity.this)) {
            tryAgainButton.setVisibility(View.VISIBLE);
            networkTryDesc.setVisibility(View.VISIBLE);
            CommonUtils.connectionErrSnackbarShow(mainActLayout, MainActivity.this);
            refresh_layout.setRefreshing(false);
        } else {
            tryAgainButton.setVisibility(View.GONE);
            networkTryDesc.setVisibility(View.GONE);
            startLoginProcess();
        }
    }

    public void startLoginProcess() {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {

                final BaseRequest baseRequest = new BaseRequest();
                baseRequest.setUser(user);

                LoginProcess loginProcess = new LoginProcess(MainActivity.this, new OnEventListener<BaseResponse>() {

                    @Override
                    public void onSuccess(BaseResponse baseResponse) {

                        refresh_layout.setRefreshing(false);
                        if (baseResponse == null) {
                            CommonUtils.LOG_OK_BUT_NULL("LoginProcess");
                        } else {
                            CommonUtils.LOG_OK("LoginProcess");

                            if (messagingType == null) {
                                startActivity(new Intent(MainActivity.this, NextActivity.class));
                                finish();
                            }else {
                                switch (messagingType) {
                                    case FCM_MESSAGE_TYPE_NORMAL_TO_PERSON:
                                        if (!receiptUserId.isEmpty())
                                            startMessagePersonActivity();
                                        break;

                                    case FCM_MESSAGE_TYPE_CLUSTER_TO_PERSON:
                                        if (!receiptUserId.isEmpty())
                                            startMessageListActivity();
                                        break;

                                    default:
                                        startActivity(new Intent(MainActivity.this, NextActivity.class));
                                        finish();
                                        break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        refresh_layout.setRefreshing(false);
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

    private void startMessagePersonActivity() {
        if (MessageWithPersonActivity.thisActivity != null) {
            MessageWithPersonActivity.thisActivity.finish();
        }
        Intent intent = new Intent(this, MessageWithPersonActivity.class);
        intent.putExtra(FCM_CODE_SENDER_USERID, senderUserId);
        intent.putExtra(FCM_CODE_RECEIPT_USERID, receiptUserId);
        startActivity(intent);
        finish();
    }

    private void startMessageListActivity() {
        if (MessageListActivity.thisActivity != null) {
            MessageListActivity.thisActivity.finish();
        }
        Intent intent = new Intent(this, MessageListActivity.class);
        intent.putExtra(FCM_CODE_SENDER_USERID, senderUserId);
        intent.putExtra(FCM_CODE_RECEIPT_USERID, receiptUserId);
        startActivity(intent);
        finish();
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
