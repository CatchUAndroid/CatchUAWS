package com.uren.catchu.GeneralUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;

public class BlurBuilder {

    public static Bitmap blur(View v, float bitmapScale, float blurRadius) {
        return blur(v.getContext(), getScreenshot(v, v.getContext()), bitmapScale, blurRadius);
    }

    public static Bitmap blur(Context ctx, Bitmap image, float bitmapScale, float blurRadius) {
        Bitmap outputBitmap = null;

        try {
            int width = Math.round(image.getWidth() * bitmapScale);
            int height = Math.round(image.getHeight() * bitmapScale);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(ctx);
            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
            theIntrinsic.setRadius(blurRadius);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(ctx, BlurBuilder.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }

        return outputBitmap;
    }

    private static Bitmap getScreenshot(View v, Context context) {
        Bitmap b = null;
        try {
            b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.draw(c);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, BlurBuilder.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return b;
    }
}