package com.uren.catchu.GeneralUtils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class AnimationUtil {

    public static void setShareItemAnimation(View view){
        AnimationSet set = new AnimationSet(true);

        //Animation anim = new ScaleAnimation(1f, 0f, 1f, 0f, 100f, 100f);
        Animation animT = new TranslateAnimation(0f, 100f, 0f, 100f);

        //set.addAnimation(anim);
        set.addAnimation(animT);
        set.setDuration(500);

        view.startAnimation(set);
    }
}
