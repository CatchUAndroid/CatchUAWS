package com.uren.catchu.GeneralUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import com.uren.catchu.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;

public class PhotoSelectAdapter {

    Context context;
    Intent data;
    String selectedItemText;

    Uri pictureUri = null;
    String imageRealPath;
    Bitmap photoBitmap;
    Bitmap photoBitmapOrjinal = null;
    InputStream inputStream = null;

    public PhotoSelectAdapter(Context context, Intent data, String selectedItemText) {
        this.context = context;
        this.data = data;
        this.selectedItemText = selectedItemText;
        managePictureChoosen();
    }

    public PhotoSelectAdapter(){}

    public PhotoSelectAdapter(Context context, Uri uri) {
        this.context = context;
        this.pictureUri = uri;
        managePicFromUri();
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
    }

    public void managePictureChoosen() {

        if (selectedItemText == CAMERA_TEXT) {
            photoBitmap = (Bitmap) data.getExtras().get("data");
            pictureUri = data.getData();
            imageRealPath = UriAdapter.getPathFromGalleryUri(context, pictureUri);
            photoBitmapOrjinal = ExifUtil.rotateImageIfRequired(imageRealPath, photoBitmap);
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
}
