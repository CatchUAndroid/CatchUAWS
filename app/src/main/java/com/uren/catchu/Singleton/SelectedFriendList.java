package com.uren.catchu.Singleton;

import java.util.ArrayList;
import java.util.List;

import catchu.model.FriendList;
import catchu.model.UserProfileProperties;

public class SelectedFriendList {

    private static SelectedFriendList instance = null;
    private static FriendList friendList;

    public static SelectedFriendList getInstance(){

        if(instance == null) {
            friendList = new FriendList();
            List<UserProfileProperties> userProfileProperties = new ArrayList<UserProfileProperties>();
            friendList.setResultArray(userProfileProperties);
            instance = new SelectedFriendList();
        }
        return instance;
    }

    public static void setInstance(SelectedFriendList instance) {
        SelectedFriendList.instance = instance;
    }

    public boolean isUserInList(String userid){

        for(UserProfileProperties userProfileProperties: friendList.getResultArray()){
            if(userProfileProperties.getUserid().equals(userid)){
                return true;
            }
        }

        return false;
    }

    public FriendList getSelectedFriendList() {
        return friendList;
    }

    public void setSelectedFriendList(FriendList friendList) {
        this.friendList = friendList;
    }

    public int getSize(){
        return friendList.getResultArray().size();
    }

    public void addFriend(UserProfileProperties userProfileProperties){
        friendList.getResultArray().add(userProfileProperties);
    }

    public UserProfileProperties getFriend(int position){
        return friendList.getResultArray().get(position);
    }

    public static void updateFriendList(List<UserProfileProperties> userProfilePropertiesList){
        friendList.setResultArray(userProfilePropertiesList);
    }

    public void removeFriend(UserProfileProperties userProfileProperties){
        friendList.getResultArray().remove(userProfileProperties);
    }

    public void clearFriendList(){
        if(friendList.getResultArray().size() > 0) {
            friendList.setResultArray(new ArrayList<UserProfileProperties>());
        }
    }
}
