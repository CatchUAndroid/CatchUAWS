package com.uren.catchu.MainPackage.MainFragments.Profile.SettingsManagement.Models;

import android.widget.ImageView;

import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.SharePackage.Models.ImageShareItemBox;

public class ProblemNotifyModel {

    PhotoSelectUtil photoSelectUtil;
    ImageView imageView;
    ImageView deleteImgv;

    public PhotoSelectUtil getPhotoSelectUtil() {
        return photoSelectUtil;
    }

    public void setPhotoSelectUtil(PhotoSelectUtil photoSelectUtil) {
        this.photoSelectUtil = photoSelectUtil;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public ImageView getDeleteImgv() {
        return deleteImgv;
    }

    public void setDeleteImgv(ImageView deleteImgv) {
        this.deleteImgv = deleteImgv;
    }
}
