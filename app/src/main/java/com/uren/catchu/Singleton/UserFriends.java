package com.uren.catchu.Singleton;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.uren.catchu.ApiGatewayFunctions.FriendListRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.FriendRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.R;

import java.util.Date;
import java.util.concurrent.Executor;

import catchu.model.FriendList;
import catchu.model.FriendRequestList;

public class UserFriends {

    private static UserFriends userFriendsInstance = null;
    private String userid;
    private static FriendList friendList;

    public static UserFriends getInstance(String userId){

        if(userFriendsInstance == null)
            userFriendsInstance = new UserFriends(userId);

        return userFriendsInstance;
    }

    public UserFriends(String userid){
        this.userid = userid;
        getFriends();
    }

    public static FriendList getFriendList(){
        return friendList;
    }

    public int getSize(){
        return friendList.getResultArray().size();
    }

    public static void setInstance(UserFriends instance) {
        userFriendsInstance = instance;
    }

    private void getFriends() {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetFriends(token);
            }
        });

    }

    private void startGetFriends(String token) {

        FriendListRequestProcess friendListRequestProcess = new FriendListRequestProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                friendList = (FriendList) object;
                Log.i("**FriendListRequestProc", "OK");
            }

            @Override
            public void onFailure(Exception e) {
                Log.i("**FriendListRequestProc", "FAIL - "+ e.toString());
            }

            @Override
            public void onTaskContinue() {

            }
        }, userid, token);

        friendListRequestProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }



}
