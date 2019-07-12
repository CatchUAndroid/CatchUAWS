package com.uren.catchu.GeneralUtils;

import android.graphics.drawable.GradientDrawable;
import android.view.View;

import static com.uren.catchu.Constants.NumericConstants.ORIENTATION_BOTTOMRIGHT_TOPLEFT;
import static com.uren.catchu.Constants.NumericConstants.ORIENTATION_BOTTOM_TOP;
import static com.uren.catchu.Constants.NumericConstants.ORIENTATION_LEFT_RIGHT;
import static com.uren.catchu.Constants.NumericConstants.ORIENTATION_RIGHT_LEFT;
import static com.uren.catchu.Constants.NumericConstants.ORIENTATION_TOPLEFT_BOTTOMRIGHT;
import static com.uren.catchu.Constants.NumericConstants.ORIENTATION_TOP_BOTTOM;

public class ShapeUtil {

    public static void setShapeToView(View v, int backgroundColor, int borderColor, int shapeType, float cornerRadius, int strokeWidth) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(shapeType);
        shape.setColor(backgroundColor);
        shape.setCornerRadius(cornerRadius);
        shape.setStroke(strokeWidth, borderColor);
        v.setBackground(shape);
    }

    public static GradientDrawable getShape(int backgroundColor, int borderColor, int shapeType, float cornerRadius, int strokeWidth) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(shapeType);
        shape.setColor(backgroundColor);
        if (cornerRadius != 0)
            shape.setCornerRadius(cornerRadius);
        if (strokeWidth != 0 && borderColor != 0)
            shape.setStroke(strokeWidth, borderColor);
        return shape;
    }

    public static GradientDrawable getGradientBackground(int startColor, int endColor) {
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{startColor, endColor});
        gd.setCornerRadius(0f);
        return gd;
    }

    public static GradientDrawable getGradientBackgroundWithMiddleColor(int startColor, int middleColor, int endColor) {
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{startColor, middleColor, endColor});
        gd.setCornerRadius(0f);
        return gd;
    }

    public static GradientDrawable getGradientBackgroundFromLeft(int startColor, int endColor, int orientationType, float radius) {
        GradientDrawable gradientDrawable;

        if (orientationType == ORIENTATION_LEFT_RIGHT) {
            gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                    new int[]{startColor, endColor});
        } else if (orientationType == ORIENTATION_RIGHT_LEFT) {
            gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT,
                    new int[]{startColor, endColor});
        } else if (orientationType == ORIENTATION_TOPLEFT_BOTTOMRIGHT) {
            gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                    new int[]{startColor, endColor});
        } else if (orientationType == ORIENTATION_BOTTOMRIGHT_TOPLEFT) {
            gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BR_TL,
                    new int[]{startColor, endColor});
        } else if (orientationType == ORIENTATION_TOP_BOTTOM) {
            gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{startColor, endColor});
        } else if (orientationType == ORIENTATION_BOTTOM_TOP) {
            gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                    new int[]{startColor, endColor});
        } else {
            gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                    new int[]{startColor, endColor});
        }

        gradientDrawable.setCornerRadius(radius);
        return gradientDrawable;
    }
}
