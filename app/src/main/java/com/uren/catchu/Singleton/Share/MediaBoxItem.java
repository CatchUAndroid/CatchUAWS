package com.uren.catchu.Singleton.Share;

import android.net.Uri;

import java.net.URL;

import catchu.model.Media;

public class MediaBoxItem {

    private static MediaBoxItem mediaBoxItem = null;
    private static Media media;
    private static String imageFileUrl;
    private static Uri videoUri;

    public static MediaBoxItem getInstance(){
        if(mediaBoxItem == null) {
            mediaBoxItem = new MediaBoxItem();
        }
        return mediaBoxItem;
    }

    public MediaBoxItem(){ }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        MediaBoxItem.media = media;
    }

    public String getImageFileUrl() {
        return imageFileUrl;
    }

    public void setImageFileUrl(String imageFileUrl) {
        MediaBoxItem.imageFileUrl = imageFileUrl;
    }

    public Uri getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(Uri videoUri) {
        MediaBoxItem.videoUri = videoUri;
    }
}
