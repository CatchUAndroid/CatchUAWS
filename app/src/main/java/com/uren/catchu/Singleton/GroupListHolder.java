package com.uren.catchu.Singleton;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.uren.catchu.ApiGatewayFunctions.GroupResultProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Singleton.Interfaces.GroupListHolderCallback;

import catchu.model.GroupRequest;
import catchu.model.GroupRequestResult;

import static com.uren.catchu.Constants.StringConstants.GET_AUTHENTICATED_USER_GROUP_LIST;

public class GroupListHolder {

    private static GroupListHolder groupListHolderInstance;
    private static GroupRequestResult groupRequestResult;
    private GroupListHolderCallback groupListHolderCallback;

    //Firebase
    private static FirebaseAuth firebaseAuth;
    private static String FBuserId;

    public static GroupListHolder getInstance() {
        if (groupListHolderInstance == null) {
            groupListHolderInstance = new GroupListHolder();
        }
        return groupListHolderInstance;
    }

    public GroupListHolder() {
        firebaseAuth = FirebaseAuth.getInstance();
        getGroupList(getUserIdFromFirebase());
    }


    public static String getUserIdFromFirebase() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        FBuserId = currentUser.getUid();
        return FBuserId;
    }

    public static void setInstance(GroupListHolder instance) {
        GroupListHolder.groupListHolderInstance = instance;
    }

    public GroupRequestResult getGroupList() {
        return groupRequestResult;
    }

    public static String getUserID() {
        return getUserIdFromFirebase();
    }

    private void getGroupList(final String userid) {
        GroupListHolder.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetGroupList(userid, token);
            }

            @Override
            public void onTokenFail(String message) {
            }
        });
    }

    private void startGetGroupList(final String userId, String token) {

        final GroupRequest groupRequest = new GroupRequest();
        groupRequest.setUserid(userId);
        groupRequest.setRequestType(GET_AUTHENTICATED_USER_GROUP_LIST);

        GroupResultProcess groupResultProcess = new GroupResultProcess(new OnEventListener<GroupRequestResult>() {

            @Override
            public void onSuccess(GroupRequestResult groupRequestResult1) {

                if(groupRequestResult1 == null){
                    CommonUtils.LOG_OK_BUT_NULL("GroupResultProcess");
                }else{
                    CommonUtils.LOG_OK("GroupResultProcess");
                    groupRequestResult = groupRequestResult1;
                    if (groupListHolderCallback != null) {
                        groupListHolderCallback.onGroupListInfoTaken(groupRequestResult);
                    }
                }

            }

            @Override
            public void onFailure(Exception e) {
                GroupListHolder.setInstance(null);
                CommonUtils.LOG_FAIL("GroupResultProcess", e.toString());
            }

            @Override
            public void onTaskContinue() {

            }
        }, groupRequest, token);

        groupResultProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }

    public static void getToken(final TokenCallback tokenCallback) {

        if (NextActivity.thisActivity != null) {
            if (!CommonUtils.isNetworkConnected(NextActivity.thisActivity)) {
                CommonUtils.connectionErrSnackbarShow(NextActivity.contentFrame, NextActivity.thisActivity);
                return;
            }
        }

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        Task<GetTokenResult> tokenTask = firebaseAuth.getCurrentUser().getIdToken(false);
        tokenTask.addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    tokenCallback.onTokenTaken(task.getResult().getToken());
                } else {

                }
            }
        });
    }

    public static void  setGroupListHolderCallback  (GroupListHolderCallback groupListHolderCallback) {
        groupListHolderInstance.groupListHolderCallback = groupListHolderCallback;
    }

    public static synchronized void reset() {
        groupListHolderInstance = null;
    }

}



