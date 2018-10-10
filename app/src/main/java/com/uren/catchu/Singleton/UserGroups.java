package com.uren.catchu.Singleton;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.uren.catchu.ApiGatewayFunctions.GroupResultProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.Interfaces.CompleteCallback;

import java.util.ArrayList;
import java.util.List;

import catchu.model.GroupRequest;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.GET_AUTHENTICATED_USER_GROUP_LIST;

public class UserGroups {
    private static UserGroups userGroupsInstance = null;
    private static GroupRequestResult groupRequestResult;
    private static CompleteCallback mCompleteCallback;

    public static void getInstance(CompleteCallback completeCallback) {

        mCompleteCallback = completeCallback;

        if (userGroupsInstance == null) {
            groupRequestResult = new GroupRequestResult();
            List<UserProfileProperties> userProfilePropertiesList = new ArrayList<>();
            List<GroupRequestResultResultArrayItem> groupRequestResultResultArrayItems = new ArrayList<>();
            groupRequestResult.setResultArrayParticipantList(userProfilePropertiesList);
            groupRequestResult.setResultArray(groupRequestResultResultArrayItems);
            userGroupsInstance = new UserGroups();
        }else
            mCompleteCallback.onComplete(groupRequestResult);
    }

    public GroupRequestResult getGroupRequestResult() {
        return groupRequestResult;
    }

    public UserGroups() {
        getGroupResult();
    }

    public int getGroupCount() {
        return groupRequestResult.getResultArray().size();
    }

    public static void setInstance(UserGroups instance) {
        userGroupsInstance = instance;
    }

    public static void addGroupToRequestResult(GroupRequestResultResultArrayItem item) {
        groupRequestResult.getResultArray().add(item);
    }

    public static GroupRequestResultResultArrayItem getGroupWithId(String groupid) {
        for (GroupRequestResultResultArrayItem groupRequestResultResultArrayItem : groupRequestResult.getResultArray()) {
            if (groupid.equals(groupRequestResultResultArrayItem.getGroupid()))
                return groupRequestResultResultArrayItem;
        }

        return null;
    }

    public static void changeGroupName(String groupid, String groupName) {

        int index = 0;
        for (GroupRequestResultResultArrayItem groupRequestResultResultArrayItem : groupRequestResult.getResultArray()) {
            if (groupid.equals(groupRequestResultResultArrayItem.getGroupid())) {
                groupRequestResultResultArrayItem.setName(groupName);
                groupRequestResult.getResultArray().set(index, groupRequestResultResultArrayItem);
                break;
            }
            index = index + 1;
        }
    }

    public static void changeGroupItem(String groupid, GroupRequestResultResultArrayItem item){
        int index = 0;
        for (GroupRequestResultResultArrayItem groupRequestResultResultArrayItem : groupRequestResult.getResultArray()) {
            if (groupid.equals(groupRequestResultResultArrayItem.getGroupid())) {
                groupRequestResult.getResultArray().remove(index);
                groupRequestResult.getResultArray().add(index, item);
                break;
            }
            index = index + 1;
        }
    }

    public static void changeGroupPicture(String groupid, String groupPicUrl) {

        int index = 0;
        for (GroupRequestResultResultArrayItem groupRequestResultResultArrayItem : groupRequestResult.getResultArray()) {
            if (groupid.equals(groupRequestResultResultArrayItem.getGroupid())) {
                groupRequestResultResultArrayItem.setGroupPhotoUrl(groupPicUrl);
                groupRequestResult.getResultArray().set(index, groupRequestResultResultArrayItem);
                break;
            }
            index = index + 1;
        }
    }

    public static void removeGroupFromList(GroupRequestResultResultArrayItem groupRequestResultResultArrayItem) {
        groupRequestResult.getResultArray().remove(groupRequestResultResultArrayItem);
    }

    public static void changeGroupAdmin(String groupid, String adminId) {

        int index = 0;
        for (GroupRequestResultResultArrayItem groupRequestResultResultArrayItem : groupRequestResult.getResultArray()) {
            if (groupid.equals(groupRequestResultResultArrayItem.getGroupid())) {
                groupRequestResultResultArrayItem.setGroupAdmin(adminId);
                groupRequestResult.getResultArray().set(index, groupRequestResultResultArrayItem);
                break;
            }
            index = index + 1;
        }
    }

    private void getGroupResult() {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetGroupResult(token);
            }
        });
    }

    private void startGetGroupResult(String token) {

        final GroupRequest groupRequest = new GroupRequest();
        groupRequest.setUserid(AccountHolderInfo.getUserID());
        groupRequest.setRequestType(GET_AUTHENTICATED_USER_GROUP_LIST);

        GroupResultProcess groupResultProcess = new GroupResultProcess(new OnEventListener<GroupRequestResult>() {
            @Override
            public void onSuccess(GroupRequestResult object) {
                Log.i("**GroupResultProcess ", "OK");
                if (object != null) {
                    groupRequestResult = (GroupRequestResult) object;
                    mCompleteCallback.onComplete(groupRequestResult);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.i("**GroupResultProcess ", "FAIL - " + e.toString());
                mCompleteCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {

            }
        }, groupRequest, token);

        groupResultProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
