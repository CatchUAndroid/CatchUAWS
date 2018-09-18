package com.uren.catchu.SharePackage.Models;

import android.graphics.Bitmap;
import android.net.Uri;

import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.GeneralUtils.VideoUtil.VideoSelectUtil;

import catchu.model.Media;

public class VideoShareItemBox {

    VideoSelectUtil videoSelectUtil;

    public VideoShareItemBox(VideoSelectUtil videoSelectUtil){
        this.videoSelectUtil = videoSelectUtil;
    }

    public VideoSelectUtil getVideoSelectUtil() {
        return videoSelectUtil;
    }

    public void setVideoSelectUtil(VideoSelectUtil videoSelectUtil) {
        this.videoSelectUtil = videoSelectUtil;
    }
}
