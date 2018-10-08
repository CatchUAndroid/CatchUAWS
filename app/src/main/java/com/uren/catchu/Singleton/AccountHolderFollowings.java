package com.uren.catchu.Singleton;

import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.FollowInfoProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.Interfaces.CompleteCallback;

import catchu.model.FollowInfo;
import catchu.model.FollowInfoResultArrayItem;

import static com.uren.catchu.Constants.StringConstants.FRIEND_CREATE_FOLLOW_DIRECTLY;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_FOLLOW;
import static com.uren.catchu.Constants.StringConstants.GET_USER_FOLLOWINGS;

public class AccountHolderFollowings {

    private static AccountHolderFollowings accountHolderFollowings = null;
    private static FollowInfo followInfo;
    private static CompleteCallback mCompleteCallback;

    public static void getInstance(CompleteCallback completeCallback) {

        mCompleteCallback = completeCallback;

        if (accountHolderFollowings == null) {
            followInfo = new FollowInfo();
            accountHolderFollowings = new AccountHolderFollowings();
        }else
            mCompleteCallback.onComplete(followInfo);
    }

    public AccountHolderFollowings() {
        getFriends();
    }

    public static FollowInfo getFollowingList() {
        return followInfo;
    }

    public int getSize() {
        return followInfo.getResultArray().size();
    }

    public static void setInstance(AccountHolderFollowings instance) {
        accountHolderFollowings = instance;
    }

    private void getFriends() {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetFollowings(token);
            }
        });
    }

    public static void addFollowing(FollowInfoResultArrayItem followInfoResultArrayItem) {
        followInfo.getResultArray().add(followInfoResultArrayItem);
    }

    public static void removeFollowing(String userid) {
        if (userid != null && !userid.trim().isEmpty()) {
            int index = 0;
            for (FollowInfoResultArrayItem followInfoResultArrayItem : followInfo.getResultArray()) {
                if (followInfoResultArrayItem.getUserid().equals(userid)) {
                    followInfo.getResultArray().remove(index);
                    break;
                }
                index = index + 1;
            }
        }
    }

    private void startGetFollowings(String token) {
        FollowInfo followInfoTemp = new FollowInfo();
        followInfoTemp.setRequestType(GET_USER_FOLLOWINGS);
        followInfoTemp.setUserId(AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid());

        FollowInfoProcess followInfoProcess = new FollowInfoProcess(new OnEventListener<FollowInfo>() {
            @Override
            public void onSuccess(FollowInfo resp) {
                if(resp != null) {
                    followInfo = (FollowInfo) resp;
                    mCompleteCallback.onComplete(followInfo);
                }
            }

            @Override
            public void onFailure(Exception e) {
                mCompleteCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {
            }
        }, followInfoTemp, token);

        followInfoProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void updateFriendListByFollowType(String requestType, FollowInfoResultArrayItem followInfoResultArrayItem) {
        if (requestType.equals(FRIEND_CREATE_FOLLOW_DIRECTLY))
            accountHolderFollowings.addFollowing(followInfoResultArrayItem);
        else if (requestType.equals(FRIEND_DELETE_FOLLOW)) {
            accountHolderFollowings.removeFollowing(followInfoResultArrayItem.getUserid());

        }
    }
}
