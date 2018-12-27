package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces;

public interface MessageSentFCMCallback {
    void onSuccess();
    void onFailed(Exception e);
}
