package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models;

import catchu.model.UserProfileProperties;

public class MessageListBox {

    UserProfileProperties userProfileProperties;
    String messageText;
    long date;
    boolean iamReceipt;
    boolean isSeen;

    public UserProfileProperties getUserProfileProperties() {
        return userProfileProperties;
    }

    public void setUserProfileProperties(UserProfileProperties userProfileProperties) {
        this.userProfileProperties = userProfileProperties;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isIamReceipt() {
        return iamReceipt;
    }

    public void setIamReceipt(boolean iamReceipt) {
        this.iamReceipt = iamReceipt;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
