package com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces;

import catchu.model.Post;

public interface PostLikeClickCallback {

    void onPostLikeClicked(boolean isPostLiked, int newLikeCount, int position);

}


