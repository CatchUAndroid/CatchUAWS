package com.uren.catchu.GeneralUtils.FirebaseHelperModel;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.MainPackage.NextActivity;

import java.util.HashMap;
import java.util.Map;

import static com.uren.catchu.Constants.StringConstants.FB_CHILD_ANDROID;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_ERRORS;

public class ErrorSaveHelper {

    //Call like this
    /*ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                    new Object() {
    }.getClass().getEnclosingMethod().getName(), e.getMessage());*/

    public static void writeErrorToDB(Context context, String className, String methodName, String errMessage) {

        Context mContext = null;

        if (className == null) return;
        if (className.isEmpty()) return;
        if (methodName == null) return;
        if (methodName.isEmpty()) return;
        if (errMessage == null) return;
        if (errMessage.isEmpty()) return;

        if (context != null)
            mContext = context;
        else if (NextActivity.thisActivity != null)
            mContext = NextActivity.thisActivity;

        if (mContext == null) return;

        //CommonUtils.LOG_EXCEPTION_ERR(className + " - " + methodName, errMessage);

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        Bundle bundle = new Bundle();
        String eventName = FB_CHILD_ANDROID + "_" + className + "_" + methodName;
        /*bundle.putString("Platform", FB_CHILD_ANDROID);
        bundle.putString("Class", className);
        bundle.putString("Method", methodName);*/
        bundle.putString("ErrorMessage", errMessage);

        mFirebaseAnalytics.logEvent(eventName, bundle);

        // TODO: 14.12.2018 - DB ye yazma adimlari silinecek


        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_ERRORS).child(FB_CHILD_ANDROID)
                    .child(className).child(methodName);

            Map<String, String> errorVal = new HashMap<>();
            errorVal.put(databaseReference.push().getKey(), errMessage);

            databaseReference.setValue(errorVal, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    System.out.println();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
