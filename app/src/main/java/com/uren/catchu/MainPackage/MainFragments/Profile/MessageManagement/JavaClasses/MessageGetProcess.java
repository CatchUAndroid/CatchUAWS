package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.GetContentIdCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.GetDeviceTokenCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.GetNotificationCountCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.NotificationStatusCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.UnreadMessageCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.TokenInfo;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import catchu.model.User;

import static com.uren.catchu.Constants.StringConstants.FB_CHILD_CLUSTER_STATUS;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_CONTENT_ID;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_DEVICE_TOKEN;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_IS_SEEN;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGES;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGE_CONTENT;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_NOTIFICATIONS;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_NOTIFICATION_STATUS;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_RECEIPT;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_SIGNIN;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_TOKEN;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_USERID;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_WITH_PERSON;
import static com.uren.catchu.Constants.StringConstants.FB_VALUE_NOTIFICATION_DELETE;
import static com.uren.catchu.Constants.StringConstants.FB_VALUE_NOTIFICATION_READ;
import static com.uren.catchu.Constants.StringConstants.FB_VALUE_NOTIFICATION_SEND;

public class MessageGetProcess {

    static DatabaseReference contentIdReference;
    static ValueEventListener contentIdListener;

    static DatabaseReference notificationStatusReference;
    static ValueEventListener notificationStatusListener;

    static DatabaseReference notificationReference;
    static ValueEventListener notificationListener;

    static DatabaseReference tokenReference;
    static ValueEventListener tokenListener;

    public static void getContentId(User chattedUser,
                                    final GetContentIdCallback getContentIdCallback) {
        try {
            contentIdReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES).child(FB_CHILD_WITH_PERSON)
                    .child(AccountHolderInfo.getUserID()).child(chattedUser.getUserid()).child(FB_CHILD_MESSAGE_CONTENT);

            contentIdListener = contentIdReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildren() != null) {
                        Map<String, Object> map = (Map) dataSnapshot.getValue();

                        if (map != null)
                            getContentIdCallback.onSuccess((String) map.get(FB_CHILD_CONTENT_ID));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    getContentIdCallback.onError(databaseError.toString());
                }
            });

        } catch (Exception e) {
            getContentIdCallback.onError(e.toString());
            e.printStackTrace();
        }
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
                if (dataSnapshot.getValue() != null)
                    notificationStatusCallback.onReturn((String) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void getOtherUserNotificationCount(final String senderUserid, String chattedUserId,
                                                     final GetNotificationCountCallback getNotificationCountCallback) {

        try {
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
                        getNotificationCountCallback.onFailed(e.toString());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    getNotificationCountCallback.onFailed(databaseError.toString());
                }
            });
        } catch (Exception e) {
            getNotificationCountCallback.onFailed(e.toString());
            e.printStackTrace();
        }
    }

    public static void getOtherUserDeviceToken(final Context context, User chattedUser,
                                               final GetDeviceTokenCallback getDeviceTokenCallback) {

        tokenReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_DEVICE_TOKEN)
                .child(chattedUser.getUserid());

        tokenListener = tokenReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {
                    Map<String, Object> map = (Map) dataSnapshot.getValue();
                    TokenInfo tokenInfo = new TokenInfo();
                    tokenInfo.setToken((String) map.get(FB_CHILD_TOKEN));
                    tokenInfo.setSigninValue((String) map.get(FB_CHILD_SIGNIN));
                    getDeviceTokenCallback.onSuccess(tokenInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                    getDeviceTokenCallback.onSuccess(null);
                }


               /* for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Map<String, Object> map = (Map) dataSnapshot1.getValue();

                    TokenInfo tokenInfo = new TokenInfo();
                    tokenInfo.setToken((String) map.get(FB_CHILD_TOKEN));
                    tokenInfo.setSigninValue((String) map.get(FB_CHILD_SIGNIN));
                    getDeviceTokenCallback.onSuccess(tokenInfo);
                }*/
                tokenReference.removeEventListener(tokenListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void getUnreadMessageCount(final UnreadMessageCallback unreadMessageCallback) {
        DatabaseReference unreadMessageReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES).child(FB_CHILD_WITH_PERSON)
                .child(AccountHolderInfo.getUserID());

        unreadMessageReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> contentIdList = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Map<String, Object> map = (Map) dataSnapshot1.getValue();
                    Map<String, Object> messageContentMap = (Map) Objects.requireNonNull(map).get(FB_CHILD_MESSAGE_CONTENT);
                    String contentId = (String) messageContentMap.get(FB_CHILD_CONTENT_ID);
                    contentIdList.add(contentId);
                }
                getUnreadMessageDetails(contentIdList, unreadMessageCallback);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void getUnreadMessageDetails(ArrayList<String> contentIdList, final UnreadMessageCallback unreadMessageCallback) {

        final Map<String, String> uniqueContentIdMap = new HashMap<>();
        for (final String contentId : contentIdList) {
            DatabaseReference unreadMessageReference1 = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGE_CONTENT).child(contentId);
            Query query = unreadMessageReference1.orderByChild(FB_CHILD_RECEIPT + "/" + FB_CHILD_IS_SEEN).equalTo(false);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                        Map<String, Object> map = (Map) dataSnapshot2.getValue();
                        Map<String, Object> receiptMap = (Map) Objects.requireNonNull(map).get(FB_CHILD_RECEIPT);
                        String receiptUserId = (String) receiptMap.get(FB_CHILD_USERID);

                        if (receiptUserId != null && receiptUserId.equals(AccountHolderInfo.getUserID())) {
                            uniqueContentIdMap.put(contentId, contentId);
                            unreadMessageCallback.onReturn(uniqueContentIdMap.size());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public static void removeAllListeners() {
        if (contentIdReference != null && contentIdListener != null)
            contentIdReference.removeEventListener(contentIdListener);

        if (notificationStatusReference != null && notificationStatusListener != null)
            notificationStatusReference.removeEventListener(notificationStatusListener);

        if (notificationReference != null && notificationListener != null)
            notificationReference.removeEventListener(notificationListener);

        if (tokenReference != null && tokenListener != null)
            tokenReference.removeEventListener(tokenListener);
    }
}
