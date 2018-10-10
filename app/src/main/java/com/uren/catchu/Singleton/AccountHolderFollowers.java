package com.uren.catchu.Singleton;

import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.FriendListRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.Interfaces.CompleteCallback;

import catchu.model.FriendList;
import catchu.model.UserProfileProperties;

public class AccountHolderFollowers {

    private static AccountHolderFollowers accountHolderFollowers = null;
    private static FriendList friendList;
    private static CompleteCallback mCompleteCallback;

    public static void getInstance(CompleteCallback completeCallback) {

        mCompleteCallback = completeCallback;

        if (accountHolderFollowers == null) {
            friendList = new FriendList();
            accountHolderFollowers = new AccountHolderFollowers();
        }else
            mCompleteCallback.onComplete(friendList);
    }

    public AccountHolderFollowers() {
        getFriends();
    }

    public static FriendList getFriendList() {
        return friendList;
    }

    public int getSize() {
        return friendList.getResultArray().size();
    }

    public static void setInstance(AccountHolderFollowers instance) {
        accountHolderFollowers = instance;
    }

    private void getFriends() {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetFollowers(token);
            }
        });
    }

    public static void addFollower(UserProfileProperties userProfileProperties) {
        friendList.getResultArray().add(userProfileProperties);
    }

    public static void removeFollower(String userid) {
        if (userid != null && !userid.trim().isEmpty()) {
            int index = 0;
            for (UserProfileProperties userProfileProperties : friendList.getResultArray()) {
                if (userProfileProperties.getUserid().equals(userid)) {
                    friendList.getResultArray().remove(index);
                    break;
                }
                index = index + 1;
            }
        }
    }

    private void startGetFollowers(String token) {

        FriendListRequestProcess friendListRequestProcess = new FriendListRequestProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                if (object != null) {
                    Log.i("**FriendListRequestProc", "OK");
                    friendList = (FriendList) object;
                    mCompleteCallback.onComplete(friendList);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.i("**FriendListRequestProc", "FAIL - " + e.toString());
                mCompleteCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {

            }
        }, AccountHolderInfo.getUserID(), token);

        friendListRequestProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
