package com.uren.catchu.GeneralUtils.PhotoUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.uren.catchu.GeneralUtils.ExifUtil;
import com.uren.catchu.GeneralUtils.UriAdapter;

import java.io.IOException;
import java.util.Objects;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.FROM_FILE_TEXT;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;

public class PhotoSelectUtil {

    Bitmap bitmap = null;
    Bitmap screeanShotBitmap = null;
    Uri mediaUri = null;
    String imageRealPath = null;
    Context context;
    Intent data;
    String type;
    boolean portraitMode;

    public PhotoSelectUtil() {
    }

    public PhotoSelectUtil(Context context, Intent data, String type) {
        this.context = context;
        this.data = data;
        this.type = type;
        routeSelection();
        setPortraitMode();
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

    private void onSelectFromFileResult() {
        if (mediaUri == null) return;
        imageRealPath = UriAdapter.getRealPathFromURI(mediaUri, context);

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeFile(imageRealPath, options);
        } catch (Exception e) {
            return;
        }

        if (bitmap == null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), mediaUri);
            } catch (IOException e) {
                return;
            }
        }

        if (imageRealPath != null && !imageRealPath.isEmpty())
            bitmap = ExifUtil.rotateImageIfRequired(imageRealPath, bitmap);
        else {
            imageRealPath = UriAdapter.getFilePathFromURI(context, mediaUri, MEDIA_TYPE_IMAGE);
            bitmap = ExifUtil.rotateImageIfRequired(imageRealPath, bitmap);
        }
    }

    public void onSelectFromGalleryResult() {
        if (data == null) return;
        mediaUri = data.getData();

        if (mediaUri == null) return;
        imageRealPath = UriAdapter.getPathFromGalleryUri(context, mediaUri);

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeFile(imageRealPath, options);
        } catch (Exception e) {
            return;
        }

        if (bitmap == null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), mediaUri);
            } catch (IOException e) {
                return;
            }
        }

        bitmap = ExifUtil.rotateImageIfRequired(imageRealPath, bitmap);
    }

    public void onSelectFromCameraResult() {
        if (data == null) return;
        mediaUri = data.getData();

        if (mediaUri == null) return;
        imageRealPath = UriAdapter.getPathFromGalleryUri(context, mediaUri);

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeFile(imageRealPath, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bitmap == null)
            bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");

        bitmap = ExifUtil.rotateImageIfRequired(imageRealPath, bitmap);
    }

    public void setPortraitMode() {
        if (bitmap == null)
            return;

        int width = bitmap.getWidth();
        int heigth = bitmap.getHeight();

        portraitMode = heigth > width;
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

    public String getImageRealPath() {
        return imageRealPath;
    }

    public boolean isPortraitMode() {
        return portraitMode;
    }

    public Bitmap getScreeanShotBitmap() {
        return screeanShotBitmap;
    }

    public void setScreeanShotBitmap(Bitmap screeanShotBitmap) {
        this.screeanShotBitmap = screeanShotBitmap;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
