package com.uren.catchu.Singleton;

import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.FriendListRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;

import catchu.model.FriendList;
import catchu.model.UserProfileProperties;

public class UserFriends {

    private static UserFriends userFriendsInstance = null;
    private static FriendList friendList;

    public static UserFriends getInstance() {

        if (userFriendsInstance == null) {
            friendList = new FriendList();
            userFriendsInstance = new UserFriends();
        }

        return userFriendsInstance;
    }

    public UserFriends() {
        getFriends();
    }

    public static FriendList getFriendList() {
        return friendList;
    }

    public int getSize() {
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

    public static void addFriend(UserProfileProperties userProfileProperties) {
        friendList.getResultArray().add(userProfileProperties);
    }

    public static void removeFriend(String userid) {
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

    private void startGetFriends(String token) {

        FriendListRequestProcess friendListRequestProcess = new FriendListRequestProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                if (object != null)
                    friendList = (FriendList) object;
                Log.i("**FriendListRequestProc", "OK");
            }

            @Override
            public void onFailure(Exception e) {
                Log.i("**FriendListRequestProc", "FAIL - " + e.toString());
            }

            @Override
            public void onTaskContinue() {

            }
        }, AccountHolderInfo.getUserID(), token);

        friendListRequestProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
