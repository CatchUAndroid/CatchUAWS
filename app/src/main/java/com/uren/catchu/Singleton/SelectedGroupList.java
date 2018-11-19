package com.uren.catchu.Singleton;

import java.util.ArrayList;
import java.util.List;

import catchu.model.FriendList;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.UserProfileProperties;

public class SelectedGroupList {

    /*private static SelectedGroupList instance = null;
    private static GroupRequestResult groupRequestResult;

    public static SelectedGroupList getInstance(){

        if(instance == null) {
            groupRequestResult = new GroupRequestResult();
            List<GroupRequestResultResultArrayItem> itemList = new ArrayList<GroupRequestResultResultArrayItem>();
            groupRequestResult.setResultArray(itemList);
            instance = new SelectedGroupList();
        }
        return instance;
    }

    public static void setInstance(SelectedGroupList instance) {
        SelectedGroupList.instance = instance;
    }

    public int getSize(){
        return groupRequestResult.getResultArray().size();
    }

    public void addGroupToList(GroupRequestResultResultArrayItem groupRequestResultResultArrayItem){
        groupRequestResult.getResultArray().add(groupRequestResultResultArrayItem);
    }

    public GroupRequestResult getGroupRequestResult(){
        return groupRequestResult;
    }

    public void setGroupRequestResultList(List<GroupRequestResultResultArrayItem> itemList){
        groupRequestResult.setResultArray(itemList);
    }*/
}
