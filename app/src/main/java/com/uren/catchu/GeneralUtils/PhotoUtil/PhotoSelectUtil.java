package com.uren.catchu.GeneralUtils.PhotoUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ExifUtil;
import com.uren.catchu.GeneralUtils.UriAdapter;
import com.uren.catchu.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.FROM_FILE_TEXT;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;

public class PhotoSelectUtil {

    static Bitmap bitmap = null;
    static Uri mediaUri = null;
    static String imageRealPath = null;
    static Context context;
    static Intent data;
    static String type;

    public PhotoSelectUtil(Context context, Intent data, String type) {
        this.context = context;
        this.data = data;
        this.type = type;
        routeSelection();
    }

    public PhotoSelectUtil(Context context, Uri uri, String type) {
        this.context = context;
        this.type = type;
        this.mediaUri = uri;
        routeSelection();
    }

    private void routeSelection() {
        switch (type) {
            case CAMERA_TEXT:
                onSelectFromCameraResult();
                break;
            case GALLERY_TEXT:
                onSelectFromGalleryResult();
                break;
            case FROM_FILE_TEXT:
                onSelectFromFileResult();
                break;
            default:
                DialogBoxUtil.showErrorDialog(context, context.getResources().getString(R.string.photoSelectTypeUnknown), new InfoDialogBoxCallback() {
                    @Override
                    public void okClick() { }
                });
                break;
        }
    }

    private void onSelectFromFileResult() {
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), mediaUri);
        } catch (IOException e) {
            CommonUtils.showToastLong(context, context.getResources().getString(R.string.error) +
                    e.getMessage());
            e.printStackTrace();
        }
        imageRealPath = UriAdapter.getPathFromGalleryUri(context, mediaUri);
        bitmap = ExifUtil.rotateImageIfRequired(imageRealPath, bitmap);
    }

    public static void onSelectFromGalleryResult() {
        mediaUri = data.getData();
        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), mediaUri);
                imageRealPath = UriAdapter.getPathFromGalleryUri(context, mediaUri);
                bitmap = ExifUtil.rotateImageIfRequired(imageRealPath, bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void onSelectFromCameraResult() {
        bitmap = (Bitmap) data.getExtras().get("data");
        mediaUri = data.getData();
        imageRealPath = UriAdapter.getPathFromGalleryUri(context, mediaUri);
        bitmap = ExifUtil.rotateImageIfRequired(imageRealPath, bitmap);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        PhotoSelectUtil.bitmap = bitmap;
    }

    public Uri getMediaUri() {
        return mediaUri;
    }

    public void setMediaUri(Uri mediaUri) {
        PhotoSelectUtil.mediaUri = mediaUri;
    }

    public String getImageRealPath() {
        return imageRealPath;
    }

    public void setImageRealPath(String imageRealPath) {
        PhotoSelectUtil.imageRealPath = imageRealPath;
    }
}
