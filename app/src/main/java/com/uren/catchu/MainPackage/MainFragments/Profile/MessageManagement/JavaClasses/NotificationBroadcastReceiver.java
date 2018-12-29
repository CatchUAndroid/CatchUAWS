package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationBroadcastReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

  /*      NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);*/

        System.out.println("intent.getData:" + intent.getData());
        System.out.println("intent.getData:" + intent.getData());
        System.out.println("intent.getData:" + intent.getData());
        System.out.println("intent.getData:" + intent.getData());
        System.out.println("intent.getData:" + intent.getData());
        System.out.println("intent.getData:" + intent.getData());
        System.out.println("intent.getData:" + intent.getData());
        System.out.println("intent.getData:" + intent.getData());
        System.out.println("intent.getData:" + intent.getData());
        System.out.println("intent.getData:" + intent.getData());
        System.out.println("intent.getData:" + intent.getData());
        System.out.println("context       :" + context);
    }
}
