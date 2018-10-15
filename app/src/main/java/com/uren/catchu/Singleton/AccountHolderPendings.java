package com.uren.catchu.Singleton;

import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.FriendRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.Interfaces.CompleteCallback;

import catchu.model.FriendRequestList;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.FRIEND_GET_REQUESTING_FOLLOW_LIST;

public class AccountHolderPendings {

    private static AccountHolderPendings accountHolderPendings = null;
    private static FriendRequestList friendRequestList;
    private static CompleteCallback mCompleteCallback;

    public static void getInstance(CompleteCallback completeCallback) {

        mCompleteCallback = completeCallback;

        if (accountHolderPendings == null) {
            friendRequestList = new FriendRequestList();
            accountHolderPendings = new AccountHolderPendings();
        }else
            mCompleteCallback.onComplete(friendRequestList);
    }

    public AccountHolderPendings() {
        getPendings();
    }

    public static FriendRequestList getPendingList() {
        return friendRequestList;
    }

    public int getSize() {
        return friendRequestList.getResultArray().size();
    }

    public static void setInstance(AccountHolderPendings instance) {
        accountHolderPendings = instance;
    }

    private void getPendings() {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetPendings(token);
            }
        });
    }

    public static void addPending(UserProfileProperties userProfileProperties) {
        friendRequestList.getResultArray().add(userProfileProperties);
    }

    public static void removePending(String userid) {
        if (userid != null && !userid.trim().isEmpty()) {
            int index = 0;
            for (UserProfileProperties userProfileProperties : friendRequestList.getResultArray()) {
                if (userProfileProperties.getUserid().equals(userid)) {
                    friendRequestList.getResultArray().remove(index);
                    break;
                }
                index = index + 1;
            }
        }
    }

    private void startGetPendings(String token) {
        final FriendRequestProcess friendRequestProcess = new FriendRequestProcess(new OnEventListener<FriendRequestList>() {

            @Override
            public void onSuccess(FriendRequestList object) {
                if (object != null) {
                    friendRequestList = (FriendRequestList) object;
                    mCompleteCallback.onComplete(friendRequestList);
                }
            }

            @Override
            public void onFailure(Exception e) {
                mCompleteCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {

            }
        }, FRIEND_GET_REQUESTING_FOLLOW_LIST, AccountHolderInfo.getUserID(), " ", token);

        friendRequestProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
