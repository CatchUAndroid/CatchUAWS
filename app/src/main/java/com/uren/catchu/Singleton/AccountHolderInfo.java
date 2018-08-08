package com.uren.catchu.Singleton;

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

import static com.facebook.FacebookSdk.getApplicationContext;

public class AccountHolderInfo {

    private static AccountHolderInfo accountHolderInfoInstance;
    private static UserProfile userProfile;
    //private static String userid;

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

    public static void setInstance(AccountHolderInfo instance) {
        AccountHolderInfo.accountHolderInfoInstance = instance;
    }

    public UserProfile getUser() {
        return this.userProfile;
    }

    public static String getUserID() {

        if(accountHolderInfoInstance == null){
            accountHolderInfoInstance = new AccountHolderInfo();
            return accountHolderInfoInstance.userProfile.getResultArray().get(0).getUserid();
        }

        // TODO: 8.08.2018 --> Burada duzenleme gerekecek. Array olmasina gerek yok dedik...
        if(!accountHolderInfoInstance.userProfile.getResultArray().get(0).getUserid().isEmpty())
            return accountHolderInfoInstance.userProfile.getResultArray().get(0).getUserid();
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
                getApplicationContext(), // Context
                poolId, // Identity Pool ID
                Regions.US_EAST_1 // Region
        );


        // TODO: 8.08.2018 --> Simdilik erkutun userid ile gidelim...
        return "us-east-1:4af861e4-1cb6-4218-87e7-523c84bbfa96";

        //String identityId = credentialsProvider.getIdentityId();
        //return identityId;
    }

    private void getProfileDetail(String userid) {

        UserDetail loadUserDetail = new UserDetail(getApplicationContext(), new OnEventListener<UserProfile>() {

            @Override
            public void onSuccess(UserProfile u) {
                userProfile = u;
            }

            @Override
            public void onFailure(Exception e) {

            }

            @Override
            public void onTaskContinue() {

            }
        }, userid);
        loadUserDetail.execute();
    }

}




