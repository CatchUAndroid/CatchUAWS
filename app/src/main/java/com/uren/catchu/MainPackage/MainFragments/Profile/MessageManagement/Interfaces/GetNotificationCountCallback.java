package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces;

import com.google.firebase.database.DatabaseError;

public interface GetNotificationCountCallback {
    void onReadCount(int count);
    void onSendCount(int count);
    void onDeleteCount(int count);
    void onNotifStatus(String status);
    void onClusterNotifStatus(String status);
    void onFailed(String errMessage);
}
