package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.GetNotificationCountCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.MessageUpdateCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.NotificationStatusCallback;

import java.util.Map;

import static com.uren.catchu.Constants.StringConstants.FB_CHILD_CLUSTER_MESSAGE_NOTIFICATION;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_CLUSTER_STATUS;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_CONTENT_ID;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_NOTIFICATIONS;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_NOTIFICATION_STATUS;
import static com.uren.catchu.Constants.StringConstants.FB_VALUE_NOTIFICATION_DELETE;
import static com.uren.catchu.Constants.StringConstants.FB_VALUE_NOTIFICATION_READ;
import static com.uren.catchu.Constants.StringConstants.FB_VALUE_NOTIFICATION_SEND;

public class MessagingPersonProcess {

    static DatabaseReference notificationReference;
    static ValueEventListener notificationListener;

    static DatabaseReference notificationStatusReference;
    static ValueEventListener notificationStatusListener;

    public static void getOtherUserNotificationCount(final Context context, final String senderUserid, String chattedUserId,
                                                     final GetNotificationCountCallback getNotificationCountCallback) {

        notificationReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_NOTIFICATIONS).child(chattedUserId);

        notificationListener = notificationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int readCount = 0, sendCount = 0, deleteCount = 0;

                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Map<String, Object> map = (Map) snapshot.getValue();

                        if (map != null) {
                            String notificationStatus = (String) map.get(FB_CHILD_NOTIFICATION_STATUS);
                            String clusterStatus = (String) map.get(FB_CHILD_CLUSTER_STATUS);

                            if (senderUserid.equals(snapshot.getKey()) && notificationStatus != null)
                                getNotificationCountCallback.onNotifStatus(notificationStatus);

                            if (notificationStatus != null) {
                                switch (notificationStatus) {
                                    case FB_VALUE_NOTIFICATION_READ:
                                        readCount++;
                                        break;

                                    case FB_VALUE_NOTIFICATION_SEND:
                                        sendCount++;
                                        break;

                                    case FB_VALUE_NOTIFICATION_DELETE:
                                        deleteCount++;
                                        break;

                                    default:
                                        break;
                                }
                            } else if (clusterStatus != null) {
                                getNotificationCountCallback.onClusterNotifStatus(clusterStatus);
                            }
                        }
                    }
                    getNotificationCountCallback.onDeleteCount(deleteCount);
                    getNotificationCountCallback.onReadCount(readCount);
                    getNotificationCountCallback.onSendCount(sendCount);
                } catch (Exception e) {
                    ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                getNotificationCountCallback.onFailed(databaseError.toString());
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

    public static void getMyNotificationStatus(final Context context, String myUserId, String chattedUserid,
                                               final NotificationStatusCallback notificationStatusCallback) {
        notificationStatusReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_NOTIFICATIONS)
                .child(myUserId)
                .child(chattedUserid)
                .child(FB_CHILD_NOTIFICATION_STATUS);

        notificationStatusListener = notificationStatusReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {
                    if (dataSnapshot != null && dataSnapshot.getValue() != null)
                        notificationStatusCallback.onReturn((String) dataSnapshot.getValue());
                } catch (Exception e) {
                    ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    public static void removeAllListeners() {
        if (notificationStatusReference != null && notificationStatusListener != null)
            notificationStatusReference.removeEventListener(notificationStatusListener);

        if (notificationReference != null && notificationListener != null)
            notificationReference.removeEventListener(notificationListener);
    }
}
