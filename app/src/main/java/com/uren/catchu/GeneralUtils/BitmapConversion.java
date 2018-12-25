package com.uren.catchu.GeneralUtils;

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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.uren.catchu.GeneralUtils.FirebaseHelperModel.CrashlyticsHelper;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.LoginPackage.RegisterActivity;
import com.uren.catchu.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BitmapConversion extends AppCompatActivity {

    public static Bitmap getRoundedShape(Bitmap scaleBitmapImage, int Width, int Height) {

        Bitmap targetBitmap = null;

        try {
            int targetWidth = Width;
            int targetHeight = Height;
            targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,Bitmap.Config.ARGB_8888);

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
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, BitmapConversion.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
            e.printStackTrace();
        }

        return targetBitmap;
    }

    public static Bitmap getScreenShot(View view) {
        Bitmap bitmap = null;
        try {
            view.setDrawingCacheEnabled(true);
            bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, BitmapConversion.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return bitmap;
    }

    public static void setBlurBitmap(Context context, View view, int drawableItem, float bitmapScale,
                                     float blurRadius, Bitmap mBitmap) {
        Bitmap bitmap;
        try {
            if (mBitmap != null)
                bitmap = mBitmap;
            else if (drawableItem != 0)
                bitmap = BitmapFactory.decodeResource(context.getResources(), drawableItem);
            else return;

            Bitmap blurBitmap = BlurBuilder.blur(context, bitmap, bitmapScale, blurRadius);
            Drawable dr = new BitmapDrawable(context.getResources(), blurBitmap);
            view.setBackground(dr);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, BitmapConversion.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        Bitmap resizedBitmap = null;

        try {
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
            //bm.recycle();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, BitmapConversion.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return resizedBitmap;
    }

    public static Bitmap createUserMapBitmap(Context context, ImageView imageView) {
        Bitmap result = null;
        try {
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
            try {
                canvas.setBitmap(null);
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, BitmapConversion.class.getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, BitmapConversion.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return result;
    }

    public static int dp(float value, Context context) {
        int dpValue = 0;
        try {
            if (value == 0) {
                return 0;
            }
            dpValue = (int) Math.ceil(context.getResources().getDisplayMetrics().density * value);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, BitmapConversion.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }

        return dpValue;
    }

    public static Bitmap getBitmapFromInputStream(InputStream input, Context context,
                                                  int width, int height) {
        Bitmap myBitmap = null;
        try {
            myBitmap = BitmapFactory.decodeStream(input);
            Bitmap roundedBitmap = BitmapConversion.getRoundedShape(myBitmap, width, height);
            return roundedBitmap;
        } catch (Exception e) {

            ErrorSaveHelper.writeErrorToDB(context, BitmapConversion.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return myBitmap;
    }
}