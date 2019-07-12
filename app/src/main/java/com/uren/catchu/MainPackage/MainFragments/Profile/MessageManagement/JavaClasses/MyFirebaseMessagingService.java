package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.uren.catchu.GeneralUtils.BitmapConversion;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.MainActivity;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Activities.MessageWithPersonActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import catchu.model.User;

import static com.uren.catchu.Constants.StringConstants.AddedGroupPushNotificationStruct;
import static com.uren.catchu.Constants.StringConstants.CHAR_AMPERSAND;
import static com.uren.catchu.Constants.StringConstants.DirectFollowsPushNotificationStruct;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_DEVICE_TOKEN;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGE_BLOCK;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_TOKEN;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_PHOTO_URL;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_RECEIPT_USERID;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_SENDER_USERID;
import static com.uren.catchu.Constants.StringConstants.FCM_MESSAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.FollowRequestPushNotificationStruct;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @SuppressLint("WrongThread")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        if (remoteMessage.getData().size() > 0) {
            //Send notif for messaging
            if (remoteMessage.getNotification() != null) {
                String photoUrl = remoteMessage.getData().get(FCM_CODE_PHOTO_URL);

                if (photoUrl == null) photoUrl = "";

                if (!checkMessagingPageIsOpen(remoteMessage))
                    new GetNotification(remoteMessage).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, photoUrl);
            } else {
                //Send notif for other operations
                parseSnsRemoteMessage(remoteMessage);
            }
        }
    }

    private void parseSnsRemoteMessage(RemoteMessage remoteMessage) {
        String keyValue = "", username = "", groupName = "";
        Map<String, String> params = remoteMessage.getData();

        for (String s : params.values()) {
            try {
                JSONObject dataJSONObject = new JSONObject(s);
                keyValue = dataJSONObject.getString("key");

                JSONArray jsonArray = dataJSONObject.getJSONArray("value");
                username = jsonArray.getString(0);

                try {
                    groupName = jsonArray.getString(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /*if (keyValue.equals(AddedGroupPushNotificationStruct)) {
                    JSONArray jsonArray = dataJSONObject.getJSONArray("value");
                    username = jsonArray.getString(0);
                    groupName = jsonArray.getString(1);
                } else if (keyValue.equals(FollowRequestPushNotificationStruct) ||
                        keyValue.equals(DirectFollowsPushNotificationStruct)) {
                    username = dataJSONObject.getString("value");
                }*/

                sendNotificationFromSNS(keyValue, username, groupName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkMessagingPageIsOpen(RemoteMessage remoteMessage) {
        if (MessageWithPersonActivity.thisActivity != null) {

            User chattedUser = MessageWithPersonActivity.chattedUser;
            String senderId = remoteMessage.getData().get(FCM_CODE_SENDER_USERID);
            String receiptId = remoteMessage.getData().get(FCM_CODE_RECEIPT_USERID);

            return senderId.equals(chattedUser.getUserid()) && receiptId.equals(AccountHolderInfo.getUserID());
        }
        return false;
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth != null && firebaseAuth.getCurrentUser() != null &&
                firebaseAuth.getCurrentUser().getUid() != null) {
            String userid = firebaseAuth.getCurrentUser().getUid();
            if (!userid.isEmpty())
                sendRegistrationToServer(token, userid);
        }
    }

    public static void sendRegistrationToServer(String token, String userid) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(FB_CHILD_DEVICE_TOKEN)
                .child(userid);

        final Map<String, Object> values = new HashMap<>();
        values.put(FB_CHILD_TOKEN, token);

        database.updateChildren(values).addOnCompleteListener(task -> {

        }).addOnFailureListener(e -> {

        });
    }

    private void sendNotificationFromSNS(String keyVal, String username, String groupName) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        notificationBuilder.setSmallIcon(R.mipmap.app_notif_icon);
        notificationBuilder.setColor(getResources().getColor(R.color.DodgerBlue, null));

        notificationBuilder.setContentTitle(getResources().getString(R.string.app_name));

        StringBuilder messageBody = new StringBuilder();

        switch (keyVal) {
            case AddedGroupPushNotificationStruct:
                if (CommonUtils.getLanguage().equals("tr"))
                    messageBody.append(CHAR_AMPERSAND).append(username).append(" sizi \'").append(groupName)
                            .append("\' grubuna ekledi");
                else
                    messageBody.append(CHAR_AMPERSAND).append(username).append(" has added you to the \'")
                            .append(groupName).append("\' group");
                break;
            case DirectFollowsPushNotificationStruct:
                messageBody.append(CHAR_AMPERSAND).append(username).append(" ")
                        .append(getResources().getString(R.string.direct_follow_notif_text));
                break;
            case FollowRequestPushNotificationStruct:
                messageBody.append(CHAR_AMPERSAND).append(username).append(" ")
                        .append(getResources().getString(R.string.follow_request_notif_text));
                break;
            default:
                break;
        }

        notificationBuilder.setContentText(messageBody);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(NotificationID.getID(), notificationBuilder.build());
    }

    private void sendNotificationForMessaging(RemoteMessage remoteMessage, Bitmap bitmap) {
        String messageBody = remoteMessage.getNotification().getBody();
        String messageTitle = remoteMessage.getNotification().getTitle();
        String senderId = remoteMessage.getData().get(FCM_CODE_SENDER_USERID);
        String receiptId = remoteMessage.getData().get(FCM_CODE_RECEIPT_USERID);
        String messageType = remoteMessage.getData().get(FCM_MESSAGE_TYPE);

        Intent intent = new Intent(this, MainActivity.class);

        if (senderId != null && !senderId.isEmpty())
            intent.putExtra(FCM_CODE_SENDER_USERID, senderId);

        if (receiptId != null && !receiptId.isEmpty())
            intent.putExtra(FCM_CODE_RECEIPT_USERID, receiptId);

        if (messageType != null && !messageType.isEmpty())
            intent.putExtra(FCM_MESSAGE_TYPE, messageType);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        notificationBuilder.setSmallIcon(R.mipmap.app_notif_icon);
        notificationBuilder.setColor(getResources().getColor(R.color.DodgerBlue, null));

        if (messageTitle != null && !messageTitle.isEmpty())
            notificationBuilder.setContentTitle(messageTitle);

        if (messageBody != null && !messageBody.isEmpty())
            notificationBuilder.setContentText(messageBody);

        if (bitmap != null)
            notificationBuilder.setLargeIcon(bitmap);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(NotificationID.getID(), notificationBuilder.build());
    }

    public class GetNotification extends AsyncTask<String, Void, Void> {

        RemoteMessage remoteMessage;

        public GetNotification(RemoteMessage remoteMessage) {
            this.remoteMessage = remoteMessage;
        }

        @Override
        protected Void doInBackground(String... urls) {

            try {
                String photoUrl = urls[0];
                Bitmap myBitmap = null;

                if (photoUrl != null && !photoUrl.isEmpty()) {
                    URL url = new URL(urls[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    myBitmap = BitmapConversion.getBitmapFromInputStream(input, getApplicationContext(), 350, 350);
                }

                sendNotificationForMessaging(remoteMessage, myBitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}