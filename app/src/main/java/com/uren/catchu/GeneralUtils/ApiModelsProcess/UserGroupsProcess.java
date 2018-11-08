package com.uren.catchu.GeneralUtils.ApiModelsProcess;

import android.content.Context;
import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.GroupResultProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Utils.SaveGroupProcess;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Utils.UpdateGroupProcess;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Interfaces.UpdateGroupCallback;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.List;

import catchu.model.GroupRequest;
import catchu.model.GroupRequestGroupParticipantArrayItem;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;

import static com.uren.catchu.Constants.StringConstants.ADD_PARTICIPANT_INTO_GROUP;
import static com.uren.catchu.Constants.StringConstants.CHANGE_GROUP_ADMIN;
import static com.uren.catchu.Constants.StringConstants.EXIT_GROUP;
import static com.uren.catchu.Constants.StringConstants.GET_AUTHENTICATED_USER_GROUP_LIST;
import static com.uren.catchu.Constants.StringConstants.GET_GROUP_PARTICIPANT_LIST;

public class UserGroupsProcess {

    public static void getGroups(final String userId, final CompleteCallback completeCallback) {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetGroups(userId, token, completeCallback);
            }
        });
    }

    public static void addParticipantsToGroup(final String groupId, final List<GroupRequestGroupParticipantArrayItem> groupParticipantArray,
                                              final CompleteCallback completeCallback) {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startAddParticipantsToGroup(groupId, groupParticipantArray, token, completeCallback);
            }
        });
    }

    public static void getGroupParticipants(final String groupId, final CompleteCallback completeCallback) {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetGroupParticipants(groupId, token, completeCallback);
            }
        });
    }

    public static void exitFromGroup(final String userId, final String groupId, final CompleteCallback completeCallback) {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startExitFromGroup(userId, groupId, token, completeCallback);
            }
        });
    }

    public static void changeGroupAdmin(final String adminUserid, final String userId, final String groupId, final CompleteCallback completeCallback) {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startChangeGroupAdmin(adminUserid, userId, groupId, token, completeCallback);
            }
        });
    }

    public static void saveGroup(Context context, PhotoSelectUtil photoSelectUtil, final String groupName, final CompleteCallback completeCallback) {

        new SaveGroupProcess(context, photoSelectUtil, groupName, new CompleteCallback() {
            @Override
            public void onComplete(final Object object) {
                if (object != null)
                    completeCallback.onComplete(object);
                else
                    completeCallback.onFailed(new Exception(""));
            }

            @Override
            public void onFailed(Exception e) {
                completeCallback.onFailed(e);
            }
        });
    }

    public static void updateGroup(Context context, PhotoSelectUtil photoSelectUtil, GroupRequestResultResultArrayItem groupRequestResultResultArrayItem,
                                   final UpdateGroupCallback updateGroupCallback){
        new UpdateGroupProcess(context, photoSelectUtil, groupRequestResultResultArrayItem, new UpdateGroupCallback() {
            @Override
            public void onSuccess(GroupRequestResultResultArrayItem groupItem) {
                updateGroupCallback.onSuccess(groupItem);
            }

            @Override
            public void onFailed(Exception e) {
                updateGroupCallback.onFailed(e);
            }
        });
    }

    public static void startGetGroups(String userId, String token, final CompleteCallback completeCallback) {
        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setUserid(userId);
        groupRequest.setRequestType(GET_AUTHENTICATED_USER_GROUP_LIST);
        startAsyncTask(groupRequest, token, completeCallback);
    }

    public static void startAddParticipantsToGroup(String groupId, List<GroupRequestGroupParticipantArrayItem> groupParticipantArray, String token,
                                                   final CompleteCallback completeCallback) {
        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setGroupid(groupId);
        groupRequest.setRequestType(ADD_PARTICIPANT_INTO_GROUP);
        groupRequest.setGroupParticipantArray(groupParticipantArray);
        startAsyncTask(groupRequest, token, completeCallback);
    }

    public static void startGetGroupParticipants(String groupId, String token, final CompleteCallback completeCallback) {
        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setGroupid(groupId);
        groupRequest.setRequestType(GET_GROUP_PARTICIPANT_LIST);
        startAsyncTask(groupRequest, token, completeCallback);
    }

    public static void startExitFromGroup(String userId, String groupId, String token, final CompleteCallback completeCallback) {
        final GroupRequest groupRequest = new GroupRequest();
        groupRequest.setRequestType(EXIT_GROUP);
        groupRequest.setUserid(userId);
        groupRequest.setGroupid(groupId);
        startAsyncTask(groupRequest, token, completeCallback);
    }

    public static void startChangeGroupAdmin(String adminUserid, String userId, String groupId, String token, CompleteCallback completeCallback) {
        GroupRequest groupRequest = new GroupRequest();

        List<GroupRequestGroupParticipantArrayItem> list = new ArrayList<GroupRequestGroupParticipantArrayItem>();
        GroupRequestGroupParticipantArrayItem groupRequestGroupParticipantArrayItem = new GroupRequestGroupParticipantArrayItem();
        groupRequestGroupParticipantArrayItem.setParticipantUserid(adminUserid);
        list.add(groupRequestGroupParticipantArrayItem);

        groupRequest.setRequestType(CHANGE_GROUP_ADMIN);
        groupRequest.setUserid(userId);
        groupRequest.setGroupParticipantArray(list);
        groupRequest.setGroupid(groupId);
        startAsyncTask(groupRequest, token, completeCallback);
    }

    public static void startAsyncTask(GroupRequest groupRequest, String token, final CompleteCallback completeCallback) {
        GroupResultProcess groupResultProcess = new GroupResultProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                if (object != null)
                    completeCallback.onComplete((GroupRequestResult) object);
                else
                    completeCallback.onFailed(new Exception(""));
            }

            @Override
            public void onFailure(Exception e) {
                completeCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {

            }
        }, groupRequest, token);

        groupResultProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
