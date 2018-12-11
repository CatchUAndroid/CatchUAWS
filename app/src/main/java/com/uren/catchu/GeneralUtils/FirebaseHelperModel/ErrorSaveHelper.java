package com.uren.catchu.GeneralUtils.FirebaseHelperModel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uren.catchu.GeneralUtils.CommonUtils;

import java.util.HashMap;
import java.util.Map;

import static com.uren.catchu.Constants.StringConstants.FB_CHILD_ANDROID;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_ERRORS;

public class ErrorSaveHelper {

    public static void writeErrorToDB(String className, String methodName, String errMessage) {
        if (className == null) return;
        if (className.isEmpty()) return;
        if (methodName == null) return;
        if (methodName.isEmpty()) return;
        if (errMessage == null) return;
        if (errMessage.isEmpty()) return;

        CommonUtils.LOG_EXCEPTION_ERR(className + " - " + methodName, errMessage);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_ERRORS).child(FB_CHILD_ANDROID)
                .child(className).child(methodName);

        Map<String, String> errorVal = new HashMap<>();
        errorVal.put(errMessage, " ");

        databaseReference.setValue(errorVal, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

            }
        });
    }
}
