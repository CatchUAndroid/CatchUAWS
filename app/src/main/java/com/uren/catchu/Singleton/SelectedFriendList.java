package com.uren.catchu.Singleton;

import java.util.ArrayList;
import java.util.List;

import catchu.model.FriendList;
import catchu.model.UserProfileProperties;

public class SelectedFriendList {

    private static SelectedFriendList instance = null;
    private static FriendList selectedFriendList;

    public static SelectedFriendList getInstance(){

        if(instance == null) {
            selectedFriendList = new FriendList();
            selectedFriendList.setResultArray(new ArrayList<>());
            instance = new SelectedFriendList();
        }
        return instance;
    }

    public static void setInstance(SelectedFriendList instance) {
        SelectedFriendList.instance = instance;
    }

    public boolean isUserInList(String userid){

        for(UserProfileProperties userProfileProperties: selectedFriendList.getResultArray()){
            if(userProfileProperties.getUserid().equals(userid)){
                return true;
            }
        }

        return false;
    }

    public FriendList getSelectedFriendList() {
        return selectedFriendList;
    }

    public void setSelectedFriendList(FriendList friendList) {
        //selectedFriendList.setResultArray(friendList.getResultArray());
        //selectedFriendList = friendList;

        for(UserProfileProperties userProfileProperties: friendList.getResultArray()){
            SelectedFriendList.getInstance().addFriend(userProfileProperties);
        }
    }

    public int getSize(){
        return selectedFriendList.getResultArray().size();
    }

    public void addFriend(UserProfileProperties userProfileProperties){
        selectedFriendList.getResultArray().add(userProfileProperties);
    }

    public UserProfileProperties getFriend(int position){
        return selectedFriendList.getResultArray().get(position);
    }

    public static void updateFriendList(List<UserProfileProperties> userProfilePropertiesList){
        selectedFriendList.setResultArray(userProfilePropertiesList);
    }

    public void removeFriend(UserProfileProperties userProfileProperties){
        selectedFriendList.getResultArray().remove(userProfileProperties);
    }

    public void clearFriendList(){

        if(selectedFriendList.getResultArray().size() > 0) {
            selectedFriendList.setResultArray(new ArrayList<>());
        }
    }

    public static synchronized void reset(){
        instance = null;
    }

}
