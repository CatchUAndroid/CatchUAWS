package com.uren.catchu.GeneralUtils.ApiModelsProcess;

import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.FollowInfoProcess;
import com.uren.catchu.ApiGatewayFunctions.FriendListRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.FriendRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Singleton.AccountHolderInfo;

import catchu.model.FollowInfoListResponse;
import catchu.model.FriendList;
import catchu.model.FriendRequestList;

import static com.uren.catchu.Constants.StringConstants.FRIEND_ACCEPT_REQUEST;
import static com.uren.catchu.Constants.StringConstants.FRIEND_GET_REQUESTING_FOLLOW_LIST;
import static com.uren.catchu.Constants.StringConstants.GET_USER_FOLLOWINGS;

public class AccountHolderFollowProcess {

    public static void getFollowers(final CompleteCallback completeCallback) {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetFollowers(token, completeCallback);
            }
        });
    }

    public static void acceptFriendRequest(final String userId, final CompleteCallback completeCallback) {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startAcceptFriendRequest(userId, token, completeCallback);
            }
        });
    }

    public static void getPendingList(final CompleteCallback completeCallback) {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetPendings(token, completeCallback);
            }
        });
    }

    public static void getFollowings(final CompleteCallback completeCallback) {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetFollowings(token, completeCallback);
            }
        });
    }

    public static void friendFollowRequest(final String requestType, final String requesterUserid, final String requestedUserid, final CompleteCallback completeCallback) {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startFriendFollowRequest(requestType, requesterUserid, requestedUserid, token, completeCallback);
            }
        });
    }

    //Get followers list
    public static void startGetFollowers(String token, final CompleteCallback completeCallback) {

        FriendListRequestProcess friendListRequestProcess = new FriendListRequestProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                if (object != null)
                    completeCallback.onComplete((FriendList) object);
            }

            @Override
            public void onFailure(Exception e) {
                completeCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {

            }
        }, AccountHolderInfo.getUserID(), token);

        friendListRequestProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    //Accept pending request
    public static void startAcceptFriendRequest(String userId, String token, final CompleteCallback completeCallback) {

        FriendRequestProcess friendRequestProcess = new FriendRequestProcess(new OnEventListener<FriendRequestList>() {

            @Override
            public void onSuccess(FriendRequestList object) {
                completeCallback.onComplete(object);
            }

            @Override
            public void onFailure(Exception e) {
                completeCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {

            }
        }, FRIEND_ACCEPT_REQUEST, userId, AccountHolderInfo.getUserID(), token);

        friendRequestProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    //Pending list is returned
    public static void startGetPendings(String token, final CompleteCallback completeCallback) {

        FriendRequestProcess friendRequestProcess = new FriendRequestProcess(new OnEventListener<FriendRequestList>() {

            @Override
            public void onSuccess(FriendRequestList object) {
                FriendRequestList friendRequestList = object;
                completeCallback.onComplete(friendRequestList);
            }

            @Override
            public void onFailure(Exception e) {
                completeCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {

            }
        }, FRIEND_GET_REQUESTING_FOLLOW_LIST, AccountHolderInfo.getUserID(), " ", token);

        friendRequestProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    //Get following list
    public static void startGetFollowings(String token, final CompleteCallback completeCallback) {

        String userId = AccountHolderInfo.getUserID();
        String requestType = GET_USER_FOLLOWINGS;

        FollowInfoProcess followInfoProcess = new FollowInfoProcess(new OnEventListener<FollowInfoListResponse>() {
            @Override
            public void onSuccess(FollowInfoListResponse followInfoListResponse) {
                if(followInfoListResponse != null)
                    completeCallback.onComplete((FollowInfoListResponse) followInfoListResponse);
            }

            @Override
            public void onFailure(Exception e) {
                completeCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {
            }
        }, userId, requestType, token);

        followInfoProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    //Request types for : 1-FRIEND_DELETE_FOLLOW, 2-FRIEND_DELETE_PENDING_FOLLOW_REQUEST, 3-FRIEND_FOLLOW_REQUEST, 4-FRIEND_CREATE_FOLLOW_DIRECTLY
    public static void startFriendFollowRequest(String requestType, String requesterUserid, String requestedUserid, String token, final CompleteCallback completeCallback){
        FriendRequestProcess friendRequestProcess = new FriendRequestProcess(new OnEventListener<FriendRequestList>() {

            @Override
            public void onSuccess(FriendRequestList object) {
                if(object != null)
                    completeCallback.onComplete((FriendRequestList) object);
            }

            @Override
            public void onFailure(Exception e) {
                completeCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {

            }
        }, requestType, requesterUserid, requestedUserid, token);

        friendRequestProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}