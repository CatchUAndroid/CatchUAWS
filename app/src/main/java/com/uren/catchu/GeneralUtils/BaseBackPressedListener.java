package com.uren.catchu.GeneralUtils;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.uren.catchu.Interfaces.OnBackPressedListener;

public class BaseBackPressedListener implements OnBackPressedListener {
    private final FragmentActivity activity;

    public BaseBackPressedListener(FragmentActivity activity) {
        this.activity = activity;
    }

    @Override
    public void doBack() {
        activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}