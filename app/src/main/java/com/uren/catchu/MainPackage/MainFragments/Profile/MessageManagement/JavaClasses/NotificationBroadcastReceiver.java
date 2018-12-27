package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationBroadcastReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("intent.getData:" + intent.getData());
        System.out.println("context       :" + context);
    }
}
