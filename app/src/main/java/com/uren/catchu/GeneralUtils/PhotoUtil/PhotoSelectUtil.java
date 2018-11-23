package com.uren.catchu.GeneralUtils.PhotoUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.graphics.BitmapCompat;

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

    Bitmap bitmap = null;
    Bitmap screeanShotBitmap = null;
    Bitmap resizedBitmap = null;
    Uri mediaUri = null;
    String imageRealPath = null;
    Context context;
    Intent data;
    String type;
    boolean portraitMode;
    float bitmapResizeValue = 0.8f;

    public PhotoSelectUtil(){

    }

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
        setPortraitMode();
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
                break;
        }
    }

    public Bitmap getResizedBitmap() {
        if (getScreeanShotBitmap() != null)
            resizedBitmap = Bitmap.createScaledBitmap(getScreeanShotBitmap(),
                    (int) (getScreeanShotBitmap().getWidth() * bitmapResizeValue),
                    (int) (getScreeanShotBitmap().getHeight() * bitmapResizeValue), true);
        else if(getBitmap() != null)
            resizedBitmap = Bitmap.createScaledBitmap(getBitmap(),
                    (int) (getBitmap().getWidth() * bitmapResizeValue),
                    (int) (getBitmap().getHeight() * bitmapResizeValue), true);

        System.out.println("BitmapCompat.getAllocationByteCount(bitmap):" + BitmapCompat.getAllocationByteCount(bitmap));
        System.out.println("BitmapCompat.getAllocationByteCount(resizedBitmap):" + BitmapCompat.getAllocationByteCount(resizedBitmap));

        return resizedBitmap;
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

    public void onSelectFromGalleryResult() {
        try {
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
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void onSelectFromCameraResult() {
        bitmap = (Bitmap) data.getExtras().get("data");
        mediaUri = data.getData();
        imageRealPath = UriAdapter.getPathFromGalleryUri(context, mediaUri);
        bitmap = ExifUtil.rotateImageIfRequired(imageRealPath, bitmap);
    }

    public void setPortraitMode() {
        if (bitmap == null)
            return;

        int width = bitmap.getWidth();
        int heigth = bitmap.getHeight();

        if (heigth > width)
            portraitMode = true;
        else
            portraitMode = false;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Uri getMediaUri() {
        return mediaUri;
    }

    public void setMediaUri(Uri mediaUri) {
        this.mediaUri = mediaUri;
    }

    public String getImageRealPath() {
        return imageRealPath;
    }

    public void setImageRealPath(String imageRealPath) {
        this.imageRealPath = imageRealPath;
    }

    public boolean isPortraitMode() {
        return portraitMode;
    }

    public void setPortraitMode(boolean portraitMode) {
        this.portraitMode = portraitMode;
    }

    public Bitmap getScreeanShotBitmap() {
        return screeanShotBitmap;
    }

    public void setScreeanShotBitmap(Bitmap screeanShotBitmap) {
        this.screeanShotBitmap = screeanShotBitmap;
    }
}
