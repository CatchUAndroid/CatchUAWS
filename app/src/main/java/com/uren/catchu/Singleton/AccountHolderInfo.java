package com.uren.catchu.Singleton;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.CommonUtils;

import catchu.model.UserProfile;

public class AccountHolderInfo {

    private static AccountHolderInfo accountHolderInfoInstance;
    private static UserProfile userProfile;
    private static OnEventListener<AccountHolderInfo> mCallBack;
    private static Context context;
    private static String token;

    //Firebase
    private static FirebaseAuth firebaseAuth;
    private static String FBuserId;

    public static AccountHolderInfo getInstance() {

        if (accountHolderInfoInstance == null) {
            accountHolderInfoInstance = new AccountHolderInfo();
        }
        return accountHolderInfoInstance;
    }

    public AccountHolderInfo() {
        firebaseAuth = FirebaseAuth.getInstance();
        userProfile = new UserProfile();
        getProfileDetail(getUserIdFromFirebase());
    }

    public static void setInstance(AccountHolderInfo instance, Context context) {
        AccountHolderInfo.accountHolderInfoInstance = instance;
        AccountHolderInfo.context = context;
    }

    public UserProfile getUser() {
        return this.userProfile;
    }

    public static String getUserID() {

        return getUserIdFromFirebase();
    }

    private void getProfileDetail(final String userid) {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetProfileDetail(userid, token);
            }
        });

    }

    private void startGetProfileDetail(final String userid, String token) {

        UserDetail loadUserDetail = new UserDetail(context, new OnEventListener<UserProfile>() {

            @Override
            public void onSuccess(UserProfile up) {

                if(up.getUserInfo() == null){
                    CommonUtils.LOG_OK_BUT_NULL("UserDetailProcess");
                }else{
                    userProfile = up;
                    CommonUtils.LOG_OK("UserDetailProcess");

                }

            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.LOG_FAIL("UserDetailProcess", e.toString());
            }

            @Override
            public void onTaskContinue() {

            }
        }, userid, token);

        loadUserDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public static String getUserIdFromFirebase() {

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        FBuserId = currentUser.getUid();
        return FBuserId;

    }

    public static FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public static void getToken(final TokenCallback tokenCallback) {

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        Task<GetTokenResult> tokenTask = firebaseAuth.getCurrentUser().getIdToken(false);
        tokenTask.addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    tokenCallback.onTokenTaken(task.getResult().getToken());
                } else {
                    Log.i("info", "Token task operation fail");
                }
            }
        });

    }


}



