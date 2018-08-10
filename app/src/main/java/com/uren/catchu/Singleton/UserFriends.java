package com.uren.catchu.Singleton;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.uren.catchu.ApiGatewayFunctions.FriendListRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.FriendRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.R;

import catchu.model.FriendList;
import catchu.model.FriendRequestList;

public class UserFriends {

    private static UserFriends userFriendsInstance = null;
    private String userid;
    private FriendList friendList;

    public static UserFriends getInstance(String userId){

        if(userFriendsInstance == null)
            userFriendsInstance = new UserFriends(userId);

        return userFriendsInstance;
    }

    public UserFriends(String userid){
        this.userid = userid;
        getFriends();
    }

    public FriendList getFriendList(){
        return friendList;
    }

    public int getSize(){
        return friendList.getResultArray().size();
    }

    public static void setInstance(UserFriends instance) {
        userFriendsInstance = instance;
    }

    private void getFriends() {

        FriendListRequestProcess friendListRequestProcess = new FriendListRequestProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                friendList = (FriendList) object;
                Log.i("Info", "dd");
            }

            @Override
            public void onFailure(Exception e) {

            }

            @Override
            public void onTaskContinue() {

            }
        }, userid);

        friendListRequestProcess.execute();
    }
}
