package com.uren.catchu.GeneralUtils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;

public class BitmapConversion extends AppCompatActivity {

    public static Bitmap getRoundedShape(Bitmap scaleBitmapImage, int Width, int Height, String imagePath) {

        Bitmap orientedBitmap;

        if(imagePath != null)
            orientedBitmap = ExifUtil.rotateBitmap(imagePath, scaleBitmapImage);
        else
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
    }

    public static Bitmap getBitmapOriginRotate(Bitmap scaleBitmapImage, String imagePath){

        Bitmap orientedBitmap = ExifUtil.rotateBitmap(imagePath, scaleBitmapImage);
        return orientedBitmap;
    }
}