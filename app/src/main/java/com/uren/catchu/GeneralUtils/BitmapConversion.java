package com.uren.catchu.GeneralUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class BitmapConversion extends AppCompatActivity {

    public static Bitmap getRoundedShape(Bitmap scaleBitmapImage, int Width, int Height, String imagePath) {

        Bitmap orientedBitmap;

        if(imagePath != null) {
            //orientedBitmap = ExifUtil.rotateBitmap(imagePath, scaleBitmapImage);
            orientedBitmap = ExifUtil.rotateImageIfRequired(imagePath, scaleBitmapImage);
        }
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

        //Bitmap orientedBitmap = ExifUtil.rotateBitmap(imagePath, scaleBitmapImage);
        Bitmap orientedBitmap = ExifUtil.rotateImageIfRequired(imagePath, scaleBitmapImage);
        return orientedBitmap;
    }

    public static String encodeBase64FromBitmap(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    public static String encodeBase64FromFilepath(String path)
    {
        File imagefile = new File(path);
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(imagefile);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    public static Bitmap getScreenShot(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }
}