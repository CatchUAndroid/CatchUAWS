package com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import catchu.model.Post;

public class PostDiffCallback extends DiffUtil.Callback {

    private List<Post> mOldPostList;
    private List<Post> mNewPostList;

    public PostDiffCallback(List<Post> oldPostList, List<Post> newPostList) {
        this.mOldPostList = oldPostList;
        this.mNewPostList = newPostList;
    }

    @Override
    public int getOldListSize() {
        return mOldPostList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewPostList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        try {
            return mOldPostList.get(oldItemPosition).getPostid() == mNewPostList.get(
                    newItemPosition).getPostid();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

        final Post oldPost = mOldPostList.get(oldItemPosition);
        final Post newPost = mNewPostList.get(newItemPosition);
        boolean x = oldPost.getIsLiked().equals(newPost.getIsLiked());
        return x;

    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator


        Post newPost = mNewPostList.get(newItemPosition);
        Post oldPost = mOldPostList.get(oldItemPosition);
        Bundle diffBundle = new Bundle();
        if (newPost.getIsLiked() != oldPost.getIsLiked()) {
            diffBundle.putBoolean("isLiked", newPost.getIsLiked());
        }

        return diffBundle;


        //return super.getChangePayload(oldItemPosition, newItemPosition);
    }

}
