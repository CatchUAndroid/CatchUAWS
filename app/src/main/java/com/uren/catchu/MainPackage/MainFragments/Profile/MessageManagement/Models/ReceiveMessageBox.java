package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models;

import com.google.firebase.messaging.RemoteMessage;

import catchu.model.User;

public class ReceiveMessageBox {

    String fromUserId;
    int notificationId;

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }
}
