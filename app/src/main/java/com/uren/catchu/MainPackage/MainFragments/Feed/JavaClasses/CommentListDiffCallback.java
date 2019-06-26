package com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses;

import android.os.Bundle;
import androidx.annotation.Nullable;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;
import catchu.model.Comment;
import catchu.model.Post;

public class CommentListDiffCallback extends DiffUtil.Callback {

    private List<Comment> mOldCommentList;
    private List<Comment> mNewCommentList;

    public CommentListDiffCallback(List<Comment> oldCommenttList, List<Comment> newCommentList) {
        this.mOldCommentList = oldCommenttList;
        this.mNewCommentList = newCommentList;
    }

    @Override
    public int getOldListSize() {
        return mOldCommentList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewCommentList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldCommentList.get(oldItemPosition).getCommentid() == mNewCommentList.get(
                newItemPosition).getCommentid();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

        final Comment oldComment = mOldCommentList.get(oldItemPosition);
        final Comment newComment = mNewCommentList.get(newItemPosition);
        boolean x = oldComment.getIsLiked().equals(newComment.getIsLiked());
        return x;

    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator


        Comment newComment = mNewCommentList.get(newItemPosition);
        Comment oldComment = mOldCommentList.get(oldItemPosition);
        Bundle diffBundle = new Bundle();
        if (newComment.getIsLiked() != oldComment.getIsLiked()) {
            diffBundle.putBoolean("isLiked", newComment.getIsLiked());
        }

        return diffBundle;

        //return super.getChangePayload(oldItemPosition, newItemPosition);
    }

}
