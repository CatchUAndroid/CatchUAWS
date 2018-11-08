package com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Interfaces;

import catchu.model.GroupRequestResultResultArrayItem;

public interface UpdateGroupCallback {
    void onSuccess(GroupRequestResultResultArrayItem groupItem);
    void onFailed(Exception e);
}
