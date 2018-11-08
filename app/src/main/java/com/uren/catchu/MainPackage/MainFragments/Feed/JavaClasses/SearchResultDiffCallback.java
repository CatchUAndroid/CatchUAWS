package com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;

import catchu.model.Post;
import catchu.model.User;

public class SearchResultDiffCallback extends DiffUtil.Callback {

    private List<User> mOldUserList;
    private List<User> mNewUserList;

    public SearchResultDiffCallback(List<User> oldUserList, List<User> newUserList) {
        this.mOldUserList = oldUserList;
        this.mNewUserList = newUserList;
    }

    @Override
    public int getOldListSize() {
        return mOldUserList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewUserList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldUserList.get(oldItemPosition).getUserid() == mNewUserList.get(
                newItemPosition).getUserid();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

        final User oldUser = mOldUserList.get(oldItemPosition);
        final User newUser = mNewUserList.get(newItemPosition);
        boolean x = oldUser.getFollowStatus().equals(newUser.getFollowStatus());
        return x;

    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator


        User oldUser = mOldUserList.get(newItemPosition);
        User newUser = mNewUserList.get(oldItemPosition);
        Bundle diffBundle = new Bundle();
        if (oldUser.getFollowStatus() != newUser.getFollowStatus()) {
            diffBundle.putString("isLiked", newUser.getFollowStatus());
        }

        return diffBundle;


        //return super.getChangePayload(oldItemPosition, newItemPosition);
    }

}
