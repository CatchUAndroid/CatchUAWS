package com.uren.catchu.GeneralUtils.IntentUtil;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.uren.catchu.GeneralUtils.FileAdapter;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.uren.catchu.Constants.NumericConstants.MAX_IMAGE_SIZE;

public class IntentSelectUtil {

    public static Intent getCameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        return intent;
    }

    public static Intent getGalleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }

    public static Intent getGalleryIntentForVideo(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        //intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setTypeAndNormalize("video/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        return intent;
    }
}
