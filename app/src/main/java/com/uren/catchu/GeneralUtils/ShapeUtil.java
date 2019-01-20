package com.uren.catchu.GeneralUtils;

import android.graphics.ColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;

public class ShapeUtil {

    public static void setShapeToView(View v, int backgroundColor, int borderColor, int shapeType, float cornerRadius, int strokeWidth) {
        try {
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(shapeType);
            shape.setColor(backgroundColor);
            shape.setCornerRadius(cornerRadius);
            shape.setStroke(strokeWidth, borderColor);
            v.setBackground(shape);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, ShapeUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public static GradientDrawable getShape(int backgroundColor, int borderColor, int shapeType, float cornerRadius, int strokeWidth) {
        GradientDrawable shape = new GradientDrawable();
        try {
            shape.setShape(shapeType);
            shape.setColor(backgroundColor);
            if (cornerRadius != 0)
                shape.setCornerRadius(cornerRadius);
            if (strokeWidth != 0 && borderColor != 0)
                shape.setStroke(strokeWidth, borderColor);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, ShapeUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return shape;
    }

    public static GradientDrawable getGradientBackground(int startColor, int endColor) {
        try {
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{startColor, endColor});
            gd.setCornerRadius(0f);
            return gd;
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, ShapeUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public static GradientDrawable getGradientBackgroundWithMiddleColor(int startColor, int middleColor, int endColor) {
        try {
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{startColor, middleColor, endColor});
            gd.setCornerRadius(0f);
            return gd;
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, ShapeUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return null;
    }
}
