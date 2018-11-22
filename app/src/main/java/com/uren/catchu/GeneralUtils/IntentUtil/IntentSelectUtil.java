package com.uren.catchu.GeneralUtils.IntentUtil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

public class IntentSelectUtil {

    public static Intent getCameraIntent(){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        return intent;
    }

    public static Intent getGalleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }
}
