package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.MessageSentFCMCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.MessageUpdateCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.FCMItems;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import catchu.model.User;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.NumericConstants.FCM_MAX_MESSAGE_LEN;
import static com.uren.catchu.Constants.NumericConstants.MAX_ALLOWED_NOTIFICATION_SIZE;
import static com.uren.catchu.Constants.StringConstants.APP_NAME;
import static com.uren.catchu.Constants.StringConstants.CHAR_AMPERSAND;
import static com.uren.catchu.Constants.StringConstants.CHAR_E;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_CONTENT_ID;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_DATE;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_IS_SEEN;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_LAST_MESSAGE_DATE;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGE;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGES;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGE_CONTENT;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_NAME;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_RECEIPT;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_SENDER;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_USERID;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_WITH_PERSON;
import static com.uren.catchu.Constants.StringConstants.FB_VALUE_NOTIFICATION_SEND;

public class MessageAddProcess {

    Context context;
    User chattedUser;
    String messageContentId;
    EditText messageEdittext;
    Button sendMessageBtn;
    int notificationSendCount;
    String chattedUserDeviceToken;
    String clusterNotificationStatus;
    String otherUserNotificationStatus;
    private String otherUserSigninValue;

    public MessageAddProcess(Context context, User chattedUser, String messageContentId, EditText messageEdittext, Button sendMessageBtn,
                             int notificationSendCount, String chattedUserDeviceToken, String clusterNotificationStatus,
                             String otherUserNotificationStatus,
                             String otherUserSigninValue) {
        this.context = context;
        this.chattedUser = chattedUser;
        this.messageContentId = messageContentId;
        this.messageEdittext = messageEdittext;
        this.sendMessageBtn = sendMessageBtn;
        this.notificationSendCount = notificationSendCount;
        this.chattedUserDeviceToken = chattedUserDeviceToken;
        this.clusterNotificationStatus = clusterNotificationStatus;
        this.otherUserNotificationStatus = otherUserNotificationStatus;
        this.otherUserSigninValue = otherUserSigninValue;
    }

    public void addMessage() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES).child(FB_CHILD_WITH_PERSON)
                .child(AccountHolderInfo.getUserID()).child(chattedUser.getUserid()).child(FB_CHILD_MESSAGE_CONTENT);

        if (messageContentId == null)
            messageContentId = databaseReference.push().getKey();

        if (messageContentId == null) return;

        final Map<String, Object> values = new HashMap<>();
        //final long messageTime = System.currentTimeMillis();
        values.put(FB_CHILD_CONTENT_ID, messageContentId);
        values.put(FB_CHILD_LAST_MESSAGE_DATE, ServerValue.TIMESTAMP);

        databaseReference.updateChildren(values).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES).child(FB_CHILD_WITH_PERSON)
                        .child(chattedUser.getUserid()).child(AccountHolderInfo.getUserID()).child(FB_CHILD_MESSAGE_CONTENT)
                        .updateChildren(values).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        saveMessageContent();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        enableUIItems();

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                enableUIItems();

            }
        });
    }

    public void enableUIItems() {
        sendMessageBtn.setEnabled(true);
        messageEdittext.setText("");
    }

    public void saveMessageContent() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGE_CONTENT)
                .child(messageContentId);

        final String messageId = databaseReference.push().getKey();
        databaseReference = databaseReference.child(Objects.requireNonNull(messageId));

        Map<String, Object> values = new HashMap<>();

        values.put(FB_CHILD_DATE, ServerValue.TIMESTAMP);
        values.put(FB_CHILD_MESSAGE, messageEdittext.getText().toString().trim());

        Map<String, String> sender = new HashMap<>();
        sender.put(FB_CHILD_NAME, AccountHolderInfo.getInstance().getUser().getUserInfo().getName());
        sender.put(FB_CHILD_USERID, AccountHolderInfo.getUserID());

        Map<String, Object> receipt = new HashMap<>();
        receipt.put(FB_CHILD_NAME, chattedUser.getName());
        receipt.put(FB_CHILD_USERID, chattedUser.getUserid());
        receipt.put(FB_CHILD_IS_SEEN, false);

        values.put(FB_CHILD_SENDER, sender);
        values.put(FB_CHILD_RECEIPT, receipt);

        databaseReference.setValue(values, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (otherUserSigninValue != null && otherUserSigninValue.equals(CHAR_E))
                    sendMessageToCloudFunction(messageId);
                enableUIItems();
            }
        });
    }

    public void sendMessageToCloudFunction(String messageId) {

        String body;
        String title;

        if (notificationSendCount > MAX_ALLOWED_NOTIFICATION_SIZE) {
            sendClusterMessage();
            return;
        }

        if (otherUserNotificationStatus != null &&
                otherUserNotificationStatus.equals(FB_VALUE_NOTIFICATION_SEND)) return;

        UserProfileProperties userProfileProperties =
                AccountHolderInfo.getInstance().getUser().getUserInfo();

        if (userProfileProperties != null) {
            if (userProfileProperties.getName() != null && !userProfileProperties.getName().isEmpty())
                title = userProfileProperties.getName();
            else if (userProfileProperties.getUsername() != null && !userProfileProperties.getUsername().isEmpty())
                title = CHAR_AMPERSAND + userProfileProperties.getUsername();
            else return;

            if (userProfileProperties.getUserid() == null || userProfileProperties.getUserid().isEmpty())
                return;
        } else return;

        if (messageEdittext != null && messageEdittext.getText() != null &&
                !messageEdittext.getText().toString().isEmpty()) {

            if (messageEdittext.getText().toString().trim().length() < FCM_MAX_MESSAGE_LEN)
                body = messageEdittext.getText().toString().trim();
            else
                body = messageEdittext.getText().toString().trim().substring(0, FCM_MAX_MESSAGE_LEN) + "...";
        } else
            return;

        if (messageId == null || messageId.isEmpty())
            return;

        if (chattedUserDeviceToken == null || chattedUserDeviceToken.isEmpty())
            return;

        FCMItems fcmItems = new FCMItems();
        fcmItems.setBody(body);
        fcmItems.setOtherUserDeviceToken(chattedUserDeviceToken);
        fcmItems.setTitle(title);
        fcmItems.setPhotoUrl(userProfileProperties.getProfilePhotoUrl());
        fcmItems.setMessageid(messageId);
        fcmItems.setSenderUserid(userProfileProperties.getUserid());
        fcmItems.setReceiptUserid(chattedUser.getUserid());

        SendMessageToFCM.sendMessage(context,
                fcmItems,
                new MessageSentFCMCallback() {
                    @Override
                    public void onSuccess() {
                        MessageUpdateProcess.updateNotificationStatus(chattedUser.getUserid(), AccountHolderInfo.getUserID()
                                , FB_VALUE_NOTIFICATION_SEND);
                    }

                    @Override
                    public void onFailed(Exception e) {

                    }
                });
    }

    public void sendClusterMessage() {
        if (clusterNotificationStatus != null &&
                clusterNotificationStatus.equals(FB_VALUE_NOTIFICATION_SEND)) return;

        String body;
        String title;

        UserProfileProperties userProfileProperties =
                AccountHolderInfo.getInstance().getUser().getUserInfo();

        title = APP_NAME;
        body = context.getResources().getString(R.string.YOU_HAVE_NEW_MESSAGES);

        if (chattedUserDeviceToken == null || chattedUserDeviceToken.isEmpty())
            return;

        FCMItems fcmItems = new FCMItems();
        fcmItems.setBody(body);
        fcmItems.setOtherUserDeviceToken(chattedUserDeviceToken);
        fcmItems.setTitle(title);
        fcmItems.setSenderUserid(userProfileProperties.getUserid());
        fcmItems.setReceiptUserid(chattedUser.getUserid());

        SendClusterMessageToFCM.sendMessage(context,
                fcmItems,
                new MessageSentFCMCallback() {
                    @Override
                    public void onSuccess() {
                        MessageUpdateProcess.updateClusterStatus(chattedUser.getUserid(),
                                FB_VALUE_NOTIFICATION_SEND, new MessageUpdateCallback() {
                                    @Override
                                    public void onComplete() {

                                    }

                                    @Override
                                    public void onFailed(String errMessage) {

                                    }
                                });
                    }

                    @Override
                    public void onFailed(Exception e) {

                    }
                });
    }
}
