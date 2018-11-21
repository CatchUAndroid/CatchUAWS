package com.uren.catchu.MainPackage.MainFragments.Share.Utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ResizeAnimation extends Animation {
    final int targetValue;
    View view;
    int startValue;
    int paramType;

    public static int widthType = 0;
    public static int heigthType = 1;

    public ResizeAnimation(View view, int targetValue, int startValue, int paramType) {
        this.view = view;
        this.targetValue = targetValue;
        this.startValue = startValue;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newValue = (int) (startValue + (targetValue - startValue) * interpolatedTime);

        if (paramType == widthType)
            view.getLayoutParams().width = newValue;
        else if(paramType == heigthType)
            view.getLayoutParams().height = newValue;

        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}