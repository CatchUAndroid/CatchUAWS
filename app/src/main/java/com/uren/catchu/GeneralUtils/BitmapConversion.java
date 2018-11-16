package com.uren.catchu.GeneralUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class BitmapConversion extends AppCompatActivity {

    /*public static Bitmap getRoundedShape(Bitmap scaleBitmapImage, int Width, int Height, String imagePath) {

        Bitmap orientedBitmap;

        if (imagePath != null) {
            orientedBitmap = ExifUtil.rotateImageIfRequired(imagePath, scaleBitmapImage);
        } else
            orientedBitmap = scaleBitmapImage;

        int targetWidth = Width;
        int targetHeight = Height;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = orientedBitmap;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);

        return targetBitmap;
    }*/

    /*public static RoundedBitmapDrawable getRoundedDrawable(Bitmap scaleBitmapImage, int Width, int Height, String imagePath, Context context) {

        Bitmap orientedBitmap;

        if (imagePath != null)
            orientedBitmap = ExifUtil.rotateImageIfRequired(imagePath, scaleBitmapImage);
        else
            orientedBitmap = scaleBitmapImage;

        RoundedBitmapDrawable targetBitmap = RoundedBitmapDrawableFactory.create(context.getResources(), orientedBitmap);
        targetBitmap.setCircular(true);
        targetBitmap.setAntiAlias(true);
        return targetBitmap;
    }*/

    public static Bitmap getScreenShot(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        //bm.recycle();
        return resizedBitmap;
    }
}