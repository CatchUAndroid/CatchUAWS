package com.uren.catchu.MainPackage.MainFragments.Share.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.GeneralUtils.VideoUtil.VideoSelectUtil;
import com.uren.catchu.MainPackage.MainFragments.Share.Models.ImageShareItemBox;
import com.uren.catchu.MainPackage.MainFragments.Share.Models.ShareItems;
import com.uren.catchu.MainPackage.MainFragments.Share.Models.VideoShareItemBox;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Permissions.PermissionModule;

import java.io.File;

import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.FROM_FILE_TEXT;

public class ShareDeleteProcess {

    public static void deleteSharedVideo(Context context, PermissionModule permissionModule, ShareItems shareItems) {
        try {
            if (permissionModule.checkWriteExternalStoragePermission()) {

                for (VideoShareItemBox videoShareItemBox : shareItems.getVideoShareItemBoxes()) {
                    VideoSelectUtil videoSelectUtil = videoShareItemBox.getVideoSelectUtil();

                    if (videoSelectUtil != null && videoSelectUtil.getVideoRealPath() != null && !videoSelectUtil.getVideoRealPath().isEmpty()) {
                        if (videoSelectUtil.getSelectType() != null && videoSelectUtil.getSelectType().equals(CAMERA_TEXT)) {
                            File file = new File(videoSelectUtil.getVideoRealPath());
                            file.delete();
                            updateGalleryAfterFileDelete(context, file);
                        }
                    }
                }
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, ShareDeleteProcess.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public static void deleteSharedPhoto(Context context, PermissionModule permissionModule, ShareItems shareItems) {
        try {
            if (permissionModule.checkWriteExternalStoragePermission()) {

                for (ImageShareItemBox imageShareItemBox : shareItems.getImageShareItemBoxes()) {
                    PhotoSelectUtil photoSelectUtil = imageShareItemBox.getPhotoSelectUtil();

                    if (photoSelectUtil != null && photoSelectUtil.getImageRealPath() != null && !photoSelectUtil.getImageRealPath().isEmpty()) {
                        if (photoSelectUtil.getType() != null && photoSelectUtil.getType().equals(FROM_FILE_TEXT)) {
                            File file = new File(photoSelectUtil.getImageRealPath());
                            file.delete();
                            updateGalleryAfterFileDelete(context, file);
                        }
                    }
                }
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, ShareDeleteProcess.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public static void updateGalleryAfterFileDelete(Context context, File file) {
        try {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));

            if (context != null)
                context.sendBroadcast(intent);
            else if (NextActivity.thisActivity != null)
                NextActivity.thisActivity.sendBroadcast(intent);

        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, ShareDeleteProcess.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }
}
