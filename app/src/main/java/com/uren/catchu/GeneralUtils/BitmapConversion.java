package com.uren.catchu.GeneralUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.BitmapCompat;

import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.R;

import java.io.IOException;
import java.io.InputStream;

import static com.uren.catchu.Constants.NumericConstants.MAX_IMAGE_SIZE_1ANDHALFMB;
import static com.uren.catchu.Constants.NumericConstants.MAX_IMAGE_SIZE_2ANDHALFMB;
import static com.uren.catchu.Constants.NumericConstants.MAX_IMAGE_SIZE_5MB;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;

@SuppressLint("Registered")
public class BitmapConversion extends AppCompatActivity {

    public static Bitmap getRoundedShape(Bitmap scaleBitmapImage, int Width, int Height) {

        Bitmap targetBitmap = null;
        int targetWidth = Width;
        int targetHeight = Height;
        targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        canvas.drawBitmap(scaleBitmapImage,
                new Rect(0, 0, scaleBitmapImage.getWidth(),
                        scaleBitmapImage.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);

        return targetBitmap;
    }

    public static Bitmap getScreenShot(View view) {
        Bitmap bitmap = null;
        view.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static void setBlurBitmap(Context context, View view, int drawableItem, float bitmapScale,
                                     float blurRadius, Bitmap mBitmap) {
        Bitmap bitmap;
        if (mBitmap != null)
            bitmap = mBitmap;
        else if (drawableItem != 0) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            bitmap = BitmapFactory.decodeResource(context.getResources(), drawableItem, options);
        } else return;

        Bitmap blurBitmap = BlurBuilder.blur(context, bitmap, bitmapScale, blurRadius);
        Drawable dr = new BitmapDrawable(context.getResources(), blurBitmap);
        view.setBackground(dr);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        Bitmap resizedBitmap = null;

        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);

        if (bm != null && !bm.isRecycled())
            bm.recycle();

        return resizedBitmap;
    }


    public static Bitmap getResizedBitmap2(Context context, PhotoSelectUtil photoSelectUtil) {
        Bitmap mBitmap = null;
        Bitmap resizedBitmap = null;
        int maxByteValue;

        if (photoSelectUtil != null) {
            if (photoSelectUtil.getScreeanShotBitmap() != null)
                mBitmap = photoSelectUtil.getScreeanShotBitmap();
            else if (photoSelectUtil.getBitmap() != null)
                mBitmap = photoSelectUtil.getBitmap();
        } else
            return null;

        if (mBitmap == null) return null;

        if (BitmapCompat.getAllocationByteCount(mBitmap) > MAX_IMAGE_SIZE_5MB)
            maxByteValue = MAX_IMAGE_SIZE_2ANDHALFMB;
        else
            maxByteValue = MAX_IMAGE_SIZE_1ANDHALFMB;

        if (BitmapCompat.getAllocationByteCount(mBitmap) > maxByteValue) {

            for (float i = 0.9f; i > 0; i = i - 0.05f) {
                resizedBitmap = Bitmap.createScaledBitmap(mBitmap,
                        (int) (mBitmap.getWidth() * i),
                        (int) (mBitmap.getHeight() * i), true);

                if (BitmapCompat.getAllocationByteCount(resizedBitmap) < maxByteValue)
                    break;
            }
        } else {
            if (!photoSelectUtil.getType().equals(GALLERY_TEXT)) {
                for (float i = 1.2f; i < 20f; i = i + 1.2f) {
                    resizedBitmap = Bitmap.createScaledBitmap(mBitmap,
                            (int) (mBitmap.getWidth() * i),
                            (int) (mBitmap.getHeight() * i), true);

                    if (BitmapCompat.getAllocationByteCount(resizedBitmap) > maxByteValue)
                        break;
                }
            } else
                resizedBitmap = mBitmap;
        }

        return resizedBitmap;
    }

    public static Bitmap createUserMapBitmap(Context context, ImageView imageView) {
        Bitmap result = null;

        result = Bitmap.createBitmap(dp(62, context), dp(76, context), Bitmap.Config.ARGB_8888);
        result.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(result);
        Drawable drawable = context.getResources().getDrawable(R.mipmap.livepin);
        drawable.setBounds(0, 0, dp(62, context), dp(76, context));
        drawable.draw(canvas);

        Paint roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        RectF bitmapRect = new RectF();
        canvas.save();

        Bitmap bitmap;
        //Bitmap bitmap = BitmapFactory.decodeFile(path.toString()); /*generate bitmap here if your image comes from any url*/
        if (imageView.getDrawable() != null) {
            bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_user_profile);
        }

        if (bitmap != null) {
            BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Matrix matrix = new Matrix();
            float scale = dp(52, context) / (float) bitmap.getWidth();
            matrix.postTranslate(dp(5, context), dp(5, context));
            matrix.postScale(scale, scale);
            roundPaint.setShader(shader);
            shader.setLocalMatrix(matrix);
            bitmapRect.set(dp(5, context), dp(5, context), dp(52 + 5, context), dp(52 + 5, context));
            canvas.drawRoundRect(bitmapRect, dp(26, context), dp(26, context), roundPaint);
        }
        canvas.restore();
        canvas.setBitmap(null);

        return result;
    }

    public static int dp(float value, Context context) {
        int dpValue = 0;

        if (value == 0) {
            return 0;
        }
        dpValue = (int) Math.ceil(context.getResources().getDisplayMetrics().density * value);

        return dpValue;
    }

    public static Bitmap getBitmapFromInputStream(InputStream input, Context context,
                                                  int width, int height) {
        Bitmap myBitmap = null;
        myBitmap = BitmapFactory.decodeStream(input);
        Bitmap roundedBitmap = BitmapConversion.getRoundedShape(myBitmap, width, height);
        return roundedBitmap;
    }

    public static Bitmap compressImage(Context context, PhotoSelectUtil photoSelectUtil) {

        if (photoSelectUtil != null) {
            if (photoSelectUtil.getScreeanShotBitmap() != null)
                return null;

            if (photoSelectUtil.getMediaUri() == null)
                return null;
        }

        Bitmap scaledBitmap = null;

        String filePath = UriAdapter.getRealPathFromURI(photoSelectUtil.getMediaUri(), context);

        if(filePath == null || filePath.trim().isEmpty())
            return null;

        BitmapFactory.Options options = new BitmapFactory.Options();

        //by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        //you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        if(actualHeight == 0 || actualWidth == 0)
            return null;

        //max Height and width values of the compressed image is taken as 816x612
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        //width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        //setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(context, options, actualWidth, actualHeight);

        //inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

        //this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            //load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        //check the rotation of the image and display it properly
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0);

        Matrix matrix = new Matrix();

        if (orientation == 6)
            matrix.postRotate(90);
        else if (orientation == 3)
            matrix.postRotate(180);
        else if (orientation == 8)
            matrix.postRotate(270);

        scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

        return scaledBitmap;
    }

    public static int calculateInSampleSize(Context context, BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int inSampleSize = 0;
        final int height = options.outHeight;
        final int width = options.outWidth;
        inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
}