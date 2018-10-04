package com.uren.catchu.GeneralUtils;

import android.graphics.drawable.GradientDrawable;
import android.view.View;

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
}
