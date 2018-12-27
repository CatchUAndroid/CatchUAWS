package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces;

public interface GetContentIdCallback {
    void onSuccess(String contentId);
    void onError(String errMessage);
}
