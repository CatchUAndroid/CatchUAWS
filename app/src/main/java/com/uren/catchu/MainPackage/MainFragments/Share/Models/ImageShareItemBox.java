package com.uren.catchu.MainPackage.MainFragments.Share.Models;

import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;

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
