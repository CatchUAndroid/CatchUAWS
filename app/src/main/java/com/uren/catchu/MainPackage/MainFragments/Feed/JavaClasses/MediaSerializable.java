package com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses;

import java.io.Serializable;

import catchu.model.Media;

public class MediaSerializable implements Serializable {

    Media media;

    public MediaSerializable(Media media) {
        this.media = media;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }
}
