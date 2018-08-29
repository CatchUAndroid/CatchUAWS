package com.uren.catchu.Singleton;

import android.util.Log;
import android.view.View;

import com.uren.catchu.ApiGatewayFunctions.FriendListRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.GroupResultProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import java.util.List;

import catchu.model.FriendList;
import catchu.model.GroupRequest;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.UserProfile;

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

    public void addGroupToRequestResult(GroupRequestResultResultArrayItem item){
        groupRequestResult.getResultArray().add(item);
    }

    public static GroupRequestResultResultArrayItem getGroupWithId(String groupid){

        for(GroupRequestResultResultArrayItem groupRequestResultResultArrayItem:groupRequestResult.getResultArray()){
            if(groupid.equals(groupRequestResultResultArrayItem.getGroupid()))
                return groupRequestResultResultArrayItem;
        }

        return null;
    }

    public static void removeGroupFromList(GroupRequestResultResultArrayItem groupRequestResultResultArrayItem){
        groupRequestResult.getResultArray().remove(groupRequestResultResultArrayItem);
    }

    private void getGroupResult() {

        final GroupRequest groupRequest = new GroupRequest();
        groupRequest.setUserid(userid);
        groupRequest.setRequestType(GET_AUTHENTICATED_USER_GROUP_LIST);

        GroupResultProcess groupResultProcess = new GroupResultProcess(new OnEventListener<GroupRequestResult>() {

            @Override
            public void onSuccess(GroupRequestResult object) {
                Log.i("Info", "GroupResultProcess on success");
                groupRequestResult = (GroupRequestResult)object;
            }

            @Override
            public void onFailure(Exception e) {

            }

            @Override
            public void onTaskContinue() {

            }
        }, groupRequest);

        groupResultProcess.execute();
    }
}
