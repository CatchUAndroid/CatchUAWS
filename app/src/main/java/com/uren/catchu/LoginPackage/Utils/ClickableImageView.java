package com.uren.catchu.LoginPackage.Utils;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;


/**
 * Created by ASUS on 29.5.2018.
 */

public class ClickableImageView extends AppCompatImageView {

    public ClickableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(new EffectTouchListener());
    }
}
