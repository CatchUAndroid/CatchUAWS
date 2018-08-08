package com.uren.catchu.Singleton;

import catchu.model.FriendList;
import catchu.model.FriendListResultArrayItem;

public class SelectedFriendList {

    private static SelectedFriendList instance = null;
    private static FriendList friendList;

    public static SelectedFriendList getInstance(){

        if(instance == null) {
            friendList = new FriendList();
            instance = new SelectedFriendList();
        }
        return instance;
    }

    public static void setInstance(SelectedFriendList instance) {
        SelectedFriendList.instance = instance;
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

    public FriendListResultArrayItem getFriend(int position){
        return friendList.getResultArray().get(position);
    }

    public void addFriend(FriendListResultArrayItem friendListResultArrayItem){
        friendList.getResultArray().add(friendListResultArrayItem);
    }

    public void removeFriend(String userID){

        for(int index = 0; index < getSize(); index++){
            FriendListResultArrayItem friendListResultArrayItem = friendList.getResultArray().get(index);

            if(friendListResultArrayItem.getUserid().equals(userID)){
                friendList.getResultArray().remove(index);
                break;
            }
        }
    }

    public void clearFriendList(){
        if(friendList.getResultArray().size() > 0) {
            friendList.setResultArray(null);
        }
    }
}
