package com.uren.catchu.GeneralUtils.FirebaseHelperModel;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Singleton.AccountHolderInfo;

import catchu.model.UserProfileProperties;
import io.fabric.sdk.android.Fabric;

public class CrashlyticsHelper {

    public static void reportCrash(Context context, String className, String methodName,
                                   String errMessage, Exception e) {
        /*Context mContext = null;
        String errorMessage = null;

        if (context != null)
            mContext = context;
        else if (NextActivity.thisActivity != null) {
            mContext = NextActivity.thisActivity;
        }

        if (mContext == null)
            return;

        Fabric.with(mContext, new Crashlytics());

        if (AccountHolderInfo.getInstance() != null && AccountHolderInfo.getInstance().getUser() != null &&
                AccountHolderInfo.getInstance().getUser().getUserInfo() != null) {
            UserProfileProperties userProfileProperties = AccountHolderInfo.getInstance().getUser().getUserInfo();

            if (userProfileProperties.getUserid() != null && !userProfileProperties.getUserid().isEmpty())
                Crashlytics.setUserIdentifier(userProfileProperties.getUserid());
        }

        if (e != null && className != null && methodName != null) {
            Crashlytics.logException(e);
            errorMessage = e.getMessage();
            Crashlytics.log(className + " - " + methodName + " - Error:" + errorMessage);
        } else if (errMessage != null && !errMessage.isEmpty()) {
            errorMessage = errMessage;
            Crashlytics.log(className + " - " + methodName + " - Error:" + errorMessage);
        }

        if (errorMessage != null && !errorMessage.isEmpty())
            CommonUtils.LOG_EXCEPTION_ERR(className + " - " + methodName, errorMessage);*/
    }
}
