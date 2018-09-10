package com.uren.catchu.GeneralUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.uren.catchu.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.FROM_FILE_TEXT;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;

public class PhotoSelectAdapter {

    Context context;
    Intent data;
    String selectedItemText;

    Uri pictureUri = null;
    String imageRealPath;
    Bitmap photoBitmap;
    Bitmap photoBitmapOrjinal = null;
    Bitmap photoRoundedBitmap = null;
    InputStream inputStream = null;

    public PhotoSelectAdapter(Context context, Intent data, String selectedItemText) {
        this.context = context;
        this.data = data;
        this.selectedItemText = selectedItemText;
        managePictureChoosen();
    }

    public PhotoSelectAdapter(Context context, Uri uri) {
        this.context = context;
        this.pictureUri = uri;
        managePicFromUri();
    }

    public PhotoSelectAdapter() {
    }
    
    private void managePicFromUri() {
        try {
            photoBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), pictureUri);
        } catch (IOException e) {
            CommonUtils.showToastLong(context, context.getResources().getString(R.string.error) +
                    e.getMessage());
            e.printStackTrace();
        }
        imageRealPath = UriAdapter.getPathFromGalleryUri(context, pictureUri);
        photoBitmapOrjinal = ExifUtil.rotateImageIfRequired(imageRealPath, photoBitmap);
        photoRoundedBitmap = BitmapConversion.getRoundedShape(photoBitmap, 600, 600, imageRealPath);
    }

    public void managePictureChoosen() {

        if (selectedItemText == CAMERA_TEXT) {
            photoBitmap = (Bitmap) data.getExtras().get("data");
            pictureUri = data.getData();
            imageRealPath = UriAdapter.getPathFromGalleryUri(context, pictureUri);
            photoBitmapOrjinal = ExifUtil.rotateImageIfRequired(imageRealPath, photoBitmap);
            photoRoundedBitmap = BitmapConversion.getRoundedShape(photoBitmap, 600, 600, imageRealPath);
        } else if (selectedItemText == GALLERY_TEXT) {
            pictureUri = data.getData();
            imageRealPath = UriAdapter.getPathFromGalleryUri(context, pictureUri);
            try {
                inputStream = context.getContentResolver().openInputStream(pictureUri);
            } catch (FileNotFoundException e) {
                CommonUtils.showToastLong(context, context.getResources().getString(R.string.error) +
                        e.getMessage());
                e.printStackTrace();
            }
            photoBitmap = BitmapFactory.decodeStream(inputStream);
            photoBitmapOrjinal = ExifUtil.rotateImageIfRequired(imageRealPath, photoBitmap);
            photoRoundedBitmap = BitmapConversion.getRoundedShape(photoBitmap, 600, 600, imageRealPath);
        } else if (selectedItemText == FROM_FILE_TEXT) {

        }
    }

    public Uri getPictureUri() {
        return pictureUri;
    }

    public String getImageRealPath() {
        return imageRealPath;
    }

    public Bitmap getPhotoBitmap() {
        return photoBitmap;
    }

    public Bitmap getPhotoBitmapOrjinal() {
        return photoBitmapOrjinal;
    }

    public Bitmap getPhotoRoundedBitmap() {
        return photoRoundedBitmap;
    }
}
