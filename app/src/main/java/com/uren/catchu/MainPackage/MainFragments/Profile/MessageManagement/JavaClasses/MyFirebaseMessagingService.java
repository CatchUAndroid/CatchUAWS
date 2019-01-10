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
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.uren.catchu.GeneralUtils.BitmapConversion;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.MainActivity;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Activities.MessageWithPersonActivity;
import com.uren.catchu.R;

import com.uren.catchu.Singleton.AccountHolderInfo;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import catchu.model.User;

import static com.uren.catchu.Constants.StringConstants.FB_CHILD_DEVICE_TOKEN;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_TOKEN;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_PHOTO_URL;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_RECEIPT_USERID;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_SENDER_USERID;
import static com.uren.catchu.Constants.StringConstants.FCM_MESSAGE_TYPE;

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

        if (remoteMessage.getData().size() > 0 && remoteMessage.getNotification() != null) {
            String photoUrl = (String) remoteMessage.getData().get(FCM_CODE_PHOTO_URL);

            if (photoUrl == null) photoUrl = "";

            if (!checkMessagingPageIsOpen(remoteMessage))
                new GetNotification(remoteMessage).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, photoUrl);
        }
    }

    private boolean checkMessagingPageIsOpen(RemoteMessage remoteMessage) {
        try {
            if(MessageWithPersonActivity.thisActivity != null){

                User chattedUser = MessageWithPersonActivity.chattedUser;
                String senderId = (String) remoteMessage.getData().get(FCM_CODE_SENDER_USERID);
                String receiptId = (String) remoteMessage.getData().get(FCM_CODE_RECEIPT_USERID);

                if(senderId.equals(chattedUser.getUserid()) && receiptId.equals(AccountHolderInfo.getUserID()))
                    return true;
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, MyFirebaseMessagingService.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
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

        try {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            String userid = firebaseAuth.getCurrentUser().getUid();

            if (!userid.isEmpty())
                sendRegistrationToServer(token, userid);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, MyFirebaseMessagingService.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public static void sendRegistrationToServer(String token, String userid) {

        try {
            DatabaseReference database = FirebaseDatabase.getInstance().getReference(FB_CHILD_DEVICE_TOKEN)
                    .child(userid);

            final Map<String, Object> values = new HashMap<>();
            values.put(FB_CHILD_TOKEN, token);

            database.updateChildren(values).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    System.out.println("Token saved to DB");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("Token save to DB Error !!!");
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, MyFirebaseMessagingService.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void sendNotification(RemoteMessage remoteMessage, Bitmap bitmap) {
        try {

            String messageBody = remoteMessage.getNotification().getBody();
            String messageTitle = remoteMessage.getNotification().getTitle();
            String senderId = (String) remoteMessage.getData().get(FCM_CODE_SENDER_USERID);
            String receiptId = (String) remoteMessage.getData().get(FCM_CODE_RECEIPT_USERID);
            String messageType = (String) remoteMessage.getData().get(FCM_MESSAGE_TYPE);

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
                    .setContentIntent(pendingIntent)
                    .setDeleteIntent(getDeleteIntent());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notificationBuilder.setSmallIcon(R.drawable.app_notif_icon);
                notificationBuilder.setColor(getResources().getColor(R.color.DodgerBlue, null));
            } else {
                notificationBuilder.setSmallIcon(R.drawable.app_notif_icon);
            }

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

        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    protected PendingIntent getDeleteIntent() {
        Intent intent = new Intent(this, NotificationBroadcastReceiver.class);
        intent.setAction("notification_cancelled");
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
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

                sendNotification(remoteMessage, myBitmap);

            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(null, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
            return null;
        }
    }
}