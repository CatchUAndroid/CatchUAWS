package com.uren.catchu.SharePackage.Utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import com.uren.catchu.Interfaces.AnimationCompleteListener;

public class ShareItemsAnimation {
    int viewHeigth;
    View publc;
    View allFollowers;
    View special;
    View group;
    View justMe;
    long duration = 500;
    int completeCount;
    int completeValue = 4;
    ;

    public ShareItemsAnimation(int viewHeigth, View publc, View allFollowers, View special, View group, View justMe) {
        this.viewHeigth = viewHeigth;
        this.publc = publc;
        this.allFollowers = allFollowers;
        this.special = special;
        this.group = group;
        this.justMe = justMe;
    }

    public void startShareItemsAnimation(boolean show, final AnimationCompleteListener animationCompleteListener) {
        completeCount = 0;
        AnimationSet allFollowersSet = new AnimationSet(true);
        AnimationSet specialSet = new AnimationSet(true);
        AnimationSet groupSet = new AnimationSet(true);
        AnimationSet justMeSet = new AnimationSet(true);

        Animation animAllFollowers;
        Animation animSpecial;
        Animation animGroup;
        Animation animJustme;

        if (show) {
            animAllFollowers = new TranslateAnimation(0f, 0f, publc.getY() - viewHeigth, allFollowers.getY() - viewHeigth);
            animSpecial = new TranslateAnimation(0f, 0f, publc.getY() - viewHeigth * 2, special.getY() - viewHeigth * 2);
            animGroup = new TranslateAnimation(0f, 0f, publc.getY() - viewHeigth * 3, group.getY() - viewHeigth * 3);
            animJustme = new TranslateAnimation(0f, 0f, publc.getY() - viewHeigth * 4, justMe.getY() - viewHeigth * 4);
        } else {
            animAllFollowers = new TranslateAnimation(0f, 0f, allFollowers.getY() - viewHeigth, publc.getY() - viewHeigth);
            animSpecial = new TranslateAnimation(0f, 0f, special.getY() - viewHeigth * 2, publc.getY() - viewHeigth * 2);
            animGroup = new TranslateAnimation(0f, 0f, group.getY() - viewHeigth * 3, publc.getY() - viewHeigth * 3);
            animJustme = new TranslateAnimation(0f, 0f, justMe.getY() - viewHeigth * 4, publc.getY() - viewHeigth * 4);
        }

        animAllFollowers.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                completeCount++;
                if (completeCount == completeValue)
                    animationCompleteListener.onCompleted();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animSpecial.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                completeCount++;
                if (completeCount == completeValue)
                    animationCompleteListener.onCompleted();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animGroup.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                completeCount++;
                if (completeCount == completeValue)
                    animationCompleteListener.onCompleted();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animJustme.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                completeCount++;
                if (completeCount == completeValue)
                    animationCompleteListener.onCompleted();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        allFollowersSet.addAnimation(animAllFollowers);
        allFollowersSet.setDuration(duration);

        specialSet.addAnimation(animSpecial);
        specialSet.setDuration(duration);

        groupSet.addAnimation(animGroup);
        groupSet.setDuration(duration);

        justMeSet.addAnimation(animJustme);
        justMeSet.setDuration(duration);

        allFollowers.startAnimation(allFollowersSet);
        special.startAnimation(specialSet);
        group.startAnimation(groupSet);
        justMe.startAnimation(justMeSet);
    }
}
