package com.uren.catchu.GeneralUtils.ClickableImage;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class ClickableImageView extends AppCompatImageView {

    public ClickableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(new EffectTouchListener());
    }
}
