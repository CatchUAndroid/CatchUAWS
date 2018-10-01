package com.uren.catchu.LoginPackage.Utils;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;


/**
 * Created by ASUS on 29.5.2018.
 */

public class ClickableImageView extends AppCompatImageView {

    public ClickableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(new EffectTouchListener());
    }
}
