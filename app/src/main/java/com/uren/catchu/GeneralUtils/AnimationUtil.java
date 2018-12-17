package com.uren.catchu.GeneralUtils;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.R;

public class AnimationUtil {

    public static void setShareItemAnimation(View view) {
        AnimationSet set = new AnimationSet(true);

        //Animation anim = new ScaleAnimation(1f, 0f, 1f, 0f, 100f, 100f);
        Animation animT = new TranslateAnimation(0f, 100f, 0f, 100f);

        //set.addAnimation(anim);
        set.addAnimation(animT);
        set.setDuration(500);

        view.startAnimation(set);
    }

    public static void blink(Context context, View view) {
        try {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.blink);
            view.startAnimation(animation);
        } catch (Resources.NotFoundException e) {
            ErrorSaveHelper.writeErrorToDB(context, AnimationUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }
}
