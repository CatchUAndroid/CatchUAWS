package com.uren.catchu.Singleton;

import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.FollowInfoProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.Interfaces.CompleteCallback;

import java.util.ArrayList;
import java.util.List;

import catchu.model.FollowInfo;
import catchu.model.FollowInfoResultArrayItem;

import static com.uren.catchu.Constants.StringConstants.FRIEND_CREATE_FOLLOW_DIRECTLY;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_FOLLOW;
import static com.uren.catchu.Constants.StringConstants.GET_USER_FOLLOWINGS;

public class AccountHolderFacebookFriends {


    private static AccountHolderFacebookFriends accountHolderFacebookFriends = null;
    private static FollowInfo followInfo;
    private static CompleteCallback mCompleteCallback;

    public static void getInstance(CompleteCallback completeCallback) {

        mCompleteCallback = completeCallback;

        if (accountHolderFacebookFriends == null) {
            followInfo = new FollowInfo();
            List<FollowInfoResultArrayItem> followInfoResultArrayItems = new ArrayList<>();
            followInfo.setResultArray(followInfoResultArrayItems);
            accountHolderFacebookFriends = new AccountHolderFacebookFriends();
        }else
            mCompleteCallback.onComplete(followInfo);
    }

    public AccountHolderFacebookFriends() {
        getFriends();
    }

    public static FollowInfo getFacebookFriendsList() {
        return followInfo;
    }

    public int getSize() {
        return followInfo.getResultArray().size();
    }

    public static void setInstance(AccountHolderFacebookFriends instance) {
        accountHolderFacebookFriends = instance;
    }

    private void getFriends() {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetFacebookFriends(token);
            }
        });
    }

    public static boolean isFacebookFriend(String userid){
        boolean isFoolowing = false;
        if (userid != null && !userid.trim().isEmpty()) {
            int index = 0;
            for (FollowInfoResultArrayItem followInfoResultArrayItem : followInfo.getResultArray()) {
                if (followInfoResultArrayItem.getUserid().equals(userid)) {
                    isFoolowing = true;
                    break;
                }
                index = index + 1;
            }
        }
        return isFoolowing;
    }

    private void startGetFacebookFriends(String token) {
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

    public static void updateFriendListByFollowType(String requestType, final FollowInfoResultArrayItem followInfoResultArrayItem) {
        if (requestType.equals(FRIEND_CREATE_FOLLOW_DIRECTLY))
            AccountHolderFollowings.getInstance(new CompleteCallback() {
                @Override
                public void onComplete(Object object) {
                    AccountHolderFollowings.addFollowing(followInfoResultArrayItem);
                }

                @Override
                public void onFailed(Exception e) {

                }
            });
        else if (requestType.equals(FRIEND_DELETE_FOLLOW)) {
            AccountHolderFollowings.getInstance(new CompleteCallback() {
                @Override
                public void onComplete(Object object) {
                    AccountHolderFollowings.removeFollowing(followInfoResultArrayItem.getUserid());
                }

                @Override
                public void onFailed(Exception e) {

                }
            });

        }
    }
}
