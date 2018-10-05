package com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses;

import catchu.model.Media;

public class PostItem {

    private static PostItem ourInstance ;
    private static Media media;

    public static PostItem getInstance() {
        if (ourInstance == null) {
            ourInstance = new PostItem();
        }
        return ourInstance;
    }

    public PostItem() {
        media = new Media();
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        PostItem.media = media;
    }


}
