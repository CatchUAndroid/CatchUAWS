package com.uren.catchu.MainPackage.MainFragments.Share.Models;

import com.uren.catchu.GeneralUtils.VideoUtil.VideoSelectUtil;

public class VideoShareItemBox {

    VideoSelectUtil videoSelectUtil;
    boolean thumbnailImgUploaded;
    boolean videoUploaded;

    public VideoShareItemBox(VideoSelectUtil videoSelectUtil){
        this.videoSelectUtil = videoSelectUtil;
    }

    public VideoSelectUtil getVideoSelectUtil() {
        return videoSelectUtil;
    }

    public boolean isThumbnailImgUploaded() {
        return thumbnailImgUploaded;
    }

    public void setThumbnailImgUploaded(boolean thumbnailImgUploaded) {
        this.thumbnailImgUploaded = thumbnailImgUploaded;
    }

    public boolean isVideoUploaded() {
        return videoUploaded;
    }

    public void setVideoUploaded(boolean videoUploaded) {
        this.videoUploaded = videoUploaded;
    }
}
