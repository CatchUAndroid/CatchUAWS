package com.uren.catchu.MainPackage.MainFragments.Share.Models;

import android.net.Uri;

import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;

import catchu.model.Media;

public class ImageShareItemBox {

    PhotoSelectUtil photoSelectUtil;
    boolean uploaded;

    public ImageShareItemBox(PhotoSelectUtil photoSelectUtil){
        this.photoSelectUtil = photoSelectUtil;
    }

    public PhotoSelectUtil getPhotoSelectUtil() {
        return photoSelectUtil;
    }

    public void setPhotoSelectUtil(PhotoSelectUtil photoSelectUtil) {
        this.photoSelectUtil = photoSelectUtil;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }
}
