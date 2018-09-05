package com.uren.catchu.GeneralUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;

public class PhotoSelectAdapter {

    Context context;
    Intent data;
    String selectedItemText;

    Uri pictureUri;
    String imageRealPath;
    Bitmap photoBitmap;
    Bitmap photoBitmapOrjinal = null;
    Bitmap photoRoundedBitmap = null;
    InputStream inputStream = null;

    public PhotoSelectAdapter(Context context, Intent data, String selectedItemText){
        this.context = context;
        this.data = data;
        this.selectedItemText = selectedItemText;
        managePictureChoosen();
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
                e.printStackTrace();
            }
            photoBitmap = BitmapFactory.decodeStream(inputStream);
            photoBitmapOrjinal = ExifUtil.rotateImageIfRequired(imageRealPath, photoBitmap);
            photoRoundedBitmap = BitmapConversion.getRoundedShape(photoBitmap, 600, 600, imageRealPath);
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
