package com.uren.catchu.SharePackage.Models;

import android.net.Uri;

import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;

import catchu.model.Media;

public class ImageShareItemBox {

    PhotoSelectUtil photoSelectUtil;

    public ImageShareItemBox(PhotoSelectUtil photoSelectUtil){
        this.photoSelectUtil = photoSelectUtil;
    }

    public PhotoSelectUtil getPhotoSelectUtil() {
        return photoSelectUtil;
    }

    public void setPhotoSelectUtil(PhotoSelectUtil photoSelectUtil) {
        this.photoSelectUtil = photoSelectUtil;
    }
}
