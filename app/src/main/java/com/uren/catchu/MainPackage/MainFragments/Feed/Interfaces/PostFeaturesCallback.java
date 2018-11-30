package com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces;

public interface PostFeaturesCallback {

    void onPostLikeClicked(boolean isPostLiked, int newLikeCount, int position);
    void onCommentAdd(int position, int newCommentCount);
    void onCommentAllowedStatusChanged(int position, boolean commentAllowed);
    void onPostDeleted(int position);

}


