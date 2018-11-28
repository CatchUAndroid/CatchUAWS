package com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces;

import android.view.View;

import catchu.model.User;

public interface CommentAllowedCallback {

    void onCommentAllowedStatusChanged(boolean isCommentAllowed);
}