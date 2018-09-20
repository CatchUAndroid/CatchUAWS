package com.uren.catchu.Singleton;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.regions.Regions;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import catchu.model.UserProfile;

public class AccountHolderInfo {

    private static AccountHolderInfo accountHolderInfoInstance;
    private static UserProfile userProfile;
    private static String awsUserId;
    private static OnEventListener<AccountHolderInfo> mCallBack;
    private static Context context;

    public static AccountHolderInfo getInstance() {

        if (accountHolderInfoInstance == null) {
            accountHolderInfoInstance = new AccountHolderInfo();
        }
        return accountHolderInfoInstance;
    }

    public AccountHolderInfo() {
        userProfile = new UserProfile();
        getProfileDetail(getUserIdFromCognito());
    }

    public static void setInstance(AccountHolderInfo instance, Context context) {
        AccountHolderInfo.accountHolderInfoInstance = instance;
        AccountHolderInfo.context = context;
    }

    public UserProfile getUser() {
        return this.userProfile;
    }

    public static String getUserID() {

        if(awsUserId != null) {
            if (!awsUserId.isEmpty())
                return awsUserId;
        }

        if(accountHolderInfoInstance == null){
            accountHolderInfoInstance = new AccountHolderInfo();
            return accountHolderInfoInstance.userProfile.getUserInfo().getUserid();
        }

        if(!accountHolderInfoInstance.userProfile.getUserInfo().getUserid().isEmpty())
            return accountHolderInfoInstance.userProfile.getUserInfo().getUserid();
        else
            return getUserIdFromCognito();
    }

    public static String getUserIdFromCognito() {

        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();

        JSONObject CognitoIdentity = configuration
                .optJsonObject("CredentialsProvider")
                .optJSONObject("CognitoIdentity")
                .optJSONObject("Default");

        String poolId = CognitoIdentity.opt("PoolId").toString();
        String region = CognitoIdentity.opt("Region").toString();

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context, // Context
                poolId, // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        Log.i("userId ", credentialsProvider.getIdentityId());

        //20.9.2018 - userid ':' karakteri '-' ile replace edilecek dedik.
        String editedUserId = credentialsProvider.getIdentityId().replaceFirst(":", "-");
        Log.i("editedUserId ", editedUserId);

        awsUserId = editedUserId;
        return awsUserId;

    }

    private void getProfileDetail(String userid) {

        UserDetail loadUserDetail = new UserDetail(context, new OnEventListener<UserProfile>() {

            @Override
            public void onSuccess(UserProfile u) {
                userProfile = u;
                Log.i("first UserDetailProcess", "succesful");
            }

            @Override
            public void onFailure(Exception e) {
                Log.i("first UserDetailProcess", "fail");
            }

            @Override
            public void onTaskContinue() {

            }
        }, userid);

        loadUserDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}



