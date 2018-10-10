package com.uren.catchu.GroupPackage.Interfaces;

import catchu.model.GroupRequestResultResultArrayItem;

public interface UpdateGroupCallback {
    void onSuccess(GroupRequestResultResultArrayItem groupItem);
    void onFailed(Exception e);
}
