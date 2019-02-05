package com.uren.catchu.GeneralUtils;

import android.graphics.ColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.R;

import static com.uren.catchu.Constants.NumericConstants.ORIENTATION_BOTTOMRIGHT_TOPLEFT;
import static com.uren.catchu.Constants.NumericConstants.ORIENTATION_BOTTOM_TOP;
import static com.uren.catchu.Constants.NumericConstants.ORIENTATION_LEFT_RIGHT;
import static com.uren.catchu.Constants.NumericConstants.ORIENTATION_RIGHT_LEFT;
import static com.uren.catchu.Constants.NumericConstants.ORIENTATION_TOPLEFT_BOTTOMRIGHT;
import static com.uren.catchu.Constants.NumericConstants.ORIENTATION_TOP_BOTTOM;

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

    public static GradientDrawable getGradientBackgroundFromLeft(int startColor, int endColor, int orientationType, float radius) {
        try {

            GradientDrawable gradientDrawable = null;

            if(orientationType == ORIENTATION_LEFT_RIGHT){
                gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[]{startColor, endColor});
            }else if(orientationType == ORIENTATION_RIGHT_LEFT){
                gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT,
                        new int[]{startColor, endColor});
            }else if(orientationType == ORIENTATION_TOPLEFT_BOTTOMRIGHT){
                gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                        new int[]{startColor, endColor});
            }else if(orientationType == ORIENTATION_BOTTOMRIGHT_TOPLEFT){
                gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BR_TL,
                        new int[]{startColor, endColor});
            }else if(orientationType == ORIENTATION_TOP_BOTTOM){
                gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[]{startColor, endColor});
            }else if(orientationType == ORIENTATION_BOTTOM_TOP){
                gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                        new int[]{startColor, endColor});
            }else{
                gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[]{startColor, endColor});
            }

            gradientDrawable.setCornerRadius(radius);
            return gradientDrawable;
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, ShapeUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return null;
    }
}
