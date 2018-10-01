package com.uren.catchu.Singleton;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.uren.catchu.ApiGatewayFunctions.GroupResultProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;

import catchu.model.GroupRequest;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;

import static com.uren.catchu.Constants.StringConstants.GET_AUTHENTICATED_USER_GROUP_LIST;

public class UserGroups {

    // TODO: 15.08.2018 - Simdilik kullanmadik, silebiliriz dusunelim...

    private static UserGroups userGroupsInstance = null;
    private static String userid;
    private static GroupRequestResult groupRequestResult;

    public static UserGroups getInstance(String userid){

        if(userGroupsInstance == null)
            userGroupsInstance = new UserGroups(userid);

        return userGroupsInstance;
    }

    public GroupRequestResult getGroupRequestResult(){
        return groupRequestResult;
    }

    public UserGroups(String userid){
        this.userid = userid;
        getGroupResult();
    }

    public int getGroupCount(){
        return groupRequestResult.getResultArray().size();
    }

    public static void setInstance(UserGroups instance) {
        userGroupsInstance = instance;
    }

    public static void addGroupToRequestResult(GroupRequestResultResultArrayItem item){
        groupRequestResult.getResultArray().add(item);
    }

    public static GroupRequestResultResultArrayItem getGroupWithId(String groupid){

        for(GroupRequestResultResultArrayItem groupRequestResultResultArrayItem:groupRequestResult.getResultArray()){
            if(groupid.equals(groupRequestResultResultArrayItem.getGroupid()))
                return groupRequestResultResultArrayItem;
        }

        return null;
    }

    public static void changeGroupName(String groupid, String groupName){

        int index = 0;

        for(GroupRequestResultResultArrayItem groupRequestResultResultArrayItem:groupRequestResult.getResultArray()){
            if(groupid.equals(groupRequestResultResultArrayItem.getGroupid())){
                groupRequestResultResultArrayItem.setName(groupName);
                groupRequestResult.getResultArray().set(index, groupRequestResultResultArrayItem);
                break;
            }
            index = index + 1;
        }
    }

    public static void changeGroupPicture(String groupid, String groupPicUrl){

        int index = 0;

        for(GroupRequestResultResultArrayItem groupRequestResultResultArrayItem:groupRequestResult.getResultArray()){
            if(groupid.equals(groupRequestResultResultArrayItem.getGroupid())){
                groupRequestResultResultArrayItem.setGroupPhotoUrl(groupPicUrl);
                groupRequestResult.getResultArray().set(index, groupRequestResultResultArrayItem);
                break;
            }
            index = index + 1;
        }
    }



    public static void removeGroupFromList(GroupRequestResultResultArrayItem groupRequestResultResultArrayItem){
        groupRequestResult.getResultArray().remove(groupRequestResultResultArrayItem);
    }

    public static void changeGroupAdmin(String groupid, String adminId){

        int index = 0;

        for(GroupRequestResultResultArrayItem groupRequestResultResultArrayItem:groupRequestResult.getResultArray()){
            if(groupid.equals(groupRequestResultResultArrayItem.getGroupid())){
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
        groupRequest.setUserid(userid);
        groupRequest.setRequestType(GET_AUTHENTICATED_USER_GROUP_LIST);

        GroupResultProcess groupResultProcess = new GroupResultProcess(new OnEventListener<GroupRequestResult>() {

            @Override
            public void onSuccess(GroupRequestResult object) {
                Log.i("**GroupResultProcess ", "OK");
                groupRequestResult = (GroupRequestResult)object;
            }

            @Override
            public void onFailure(Exception e) {
                Log.i("**GroupResultProcess ", "FAIL - "+ e.toString());
            }

            @Override
            public void onTaskContinue() {

            }
        }, groupRequest, token);

        groupResultProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
}
