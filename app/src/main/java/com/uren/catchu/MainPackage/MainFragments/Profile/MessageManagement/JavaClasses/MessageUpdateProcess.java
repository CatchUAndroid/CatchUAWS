package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses;

import android.content.Context;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.MessageUpdateCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.MessageBox;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.uren.catchu.Constants.StringConstants.FB_CHILD_CLUSTER_MESSAGE_NOTIFICATION;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_CLUSTER_STATUS;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_DEVICE_TOKEN;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_IS_SEEN;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGE_CONTENT;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_NOTIFICATIONS;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_NOTIFICATION_STATUS;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_RECEIPT;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_SIGNIN;

public class MessageUpdateProcess {

    public static void updateReceiptIsSeenValue(final Context context, int lastVisibleItemPosition, final ArrayList<MessageBox> messageBoxList,
                                                String messageContentId) {
        for (int index = lastVisibleItemPosition; index >= 0; index--) {
            final MessageBox messageBox = messageBoxList.get(index);

            if (messageBox != null && messageBox.isReceiptIsSeen() == false && messageContentId != null) {

                if (messageBox.getReceiptUser() != null && messageBox.getReceiptUser().getUserid() != null &&
                        !messageBox.getReceiptUser().getUserid().isEmpty()) {

                    if (messageBox.getReceiptUser().getUserid().equals(AccountHolderInfo.getUserID())) {

                        final Map<String, Object> values = new HashMap<>();
                        values.put(FB_CHILD_IS_SEEN, true);

                        FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGE_CONTENT)
                                .child(messageContentId)
                                .child(messageBox.getMessageId())
                                .child(FB_CHILD_RECEIPT)
                                .updateChildren(values).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                messageBox.setReceiptIsSeen(true);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                }
            }
        }
    }

    public static void updateClusterStatus(String chattedUserId, String value,
                                           final MessageUpdateCallback messageUpdateCallback) {
        FirebaseDatabase.getInstance().getReference(FB_CHILD_NOTIFICATIONS)
                .child(chattedUserId)
                .child(FB_CHILD_CLUSTER_MESSAGE_NOTIFICATION)
                .child(FB_CHILD_CLUSTER_STATUS).setValue((Object) value)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        messageUpdateCallback.onComplete();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                messageUpdateCallback.onFailed(e.toString());
            }
        });
    }

    public static void updateNotificationStatus(String senderUserid, String chattedUserId, String value) {
        FirebaseDatabase.getInstance().getReference(FB_CHILD_NOTIFICATIONS)
                .child(senderUserid).child(chattedUserId).child(FB_CHILD_NOTIFICATION_STATUS).setValue((Object) value)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public static void updateTokenSigninValue(String userid, String value) {
        FirebaseDatabase.getInstance().getReference(FB_CHILD_DEVICE_TOKEN)
                .child(userid).child(FB_CHILD_SIGNIN).setValue((Object) value)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
