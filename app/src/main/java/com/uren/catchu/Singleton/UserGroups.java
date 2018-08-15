package com.uren.catchu.Singleton;

import android.util.Log;
import android.view.View;

import com.uren.catchu.ApiGatewayFunctions.FriendListRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.GroupResultProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.FriendList;
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

    private void getGroupResult() {

        GroupRequest groupRequest = new GroupRequest();
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
