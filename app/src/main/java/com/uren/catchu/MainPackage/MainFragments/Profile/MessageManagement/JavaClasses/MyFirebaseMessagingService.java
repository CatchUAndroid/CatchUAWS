package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.uren.catchu.GeneralUtils.BitmapConversion;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.MessageWithPersonFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.uren.catchu.Constants.NumericConstants.MESSAGE_SEEN_TIMER_VAL_IN_SEC;
import static com.uren.catchu.Constants.NumericConstants.VERIFY_PHONE_NUM_DURATION;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGES;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGE_CONTENT;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_PAGE_IS_SEEN;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_WITH_PERSON;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_PHOTO_URL;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_RECEIPT_USERID;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_SENDER_USERID;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_WILL_START_FRAGMENT;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
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

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        /*if (remoteMessage.getData().size() > 0) {

            if (true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }*/

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().size() > 0 && remoteMessage.getNotification() != null)
            checkMessageIsSeen(remoteMessage);
    }

    public void checkMessageIsSeen(final RemoteMessage remoteMessage) {

        try {
            databaseReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES)
                    .child(FB_CHILD_WITH_PERSON)
                    .child((String) remoteMessage.getData().get(FCM_CODE_RECEIPT_USERID))
                    .child((String) remoteMessage.getData().get(FCM_CODE_SENDER_USERID))
                    .child(FB_CHILD_MESSAGE_CONTENT)
                    .child(FB_CHILD_PAGE_IS_SEEN);

            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot != null) {
                            boolean pageSeen = (boolean) dataSnapshot.getValue();

                            if (!pageSeen){
                                new GetNotification(remoteMessage).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                        (String) remoteMessage.getData().get(FCM_CODE_PHOTO_URL));
                            }

                            if (databaseReference != null && valueEventListener != null)
                                databaseReference.removeEventListener(valueEventListener);
                        }
                    } catch (Exception e) {
                        ErrorSaveHelper.writeErrorToDB(null, MyFirebaseMessagingService.class.getSimpleName(),
                                new Object() {
                                }.getClass().getEnclosingMethod().getName(), e.toString());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    ErrorSaveHelper.writeErrorToDB(null, MyFirebaseMessagingService.class.getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), databaseError.toString());

                    if (databaseReference != null && valueEventListener != null)
                        databaseReference.removeEventListener(valueEventListener);
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, MyFirebaseMessagingService.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

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

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    public static void sendRegistrationToServer(String token, String userid) {

        try {
            DatabaseReference database = FirebaseDatabase.getInstance().getReference("DeviceToken").child(userid);

            final Map<String, Object> values = new HashMap<>();
            values.put("Token", token);

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

    private void sendNotification(String messageBody, String messageTitle, String senderId, String receiptId, Bitmap bitmap) {
        try {
            Intent intent = new Intent(this, MessageWithPersonActivity.class);
            intent.putExtra(FCM_CODE_SENDER_USERID, senderId);
            intent.putExtra(FCM_CODE_RECEIPT_USERID, receiptId);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            String channelId = getString(R.string.default_notification_channel_id);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

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

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public class GetNotification extends AsyncTask<String, Void, Void> {

        RemoteMessage remoteMessage;

        public GetNotification(RemoteMessage remoteMessage) {
            this.remoteMessage = remoteMessage;
        }

        @Override
        protected Void doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                Bitmap roundedBitmap = BitmapConversion.getRoundedShape(myBitmap, 350, 350);

                if (roundedBitmap != null)
                    myBitmap = roundedBitmap;

                sendNotification(remoteMessage.getNotification().getBody(),
                        remoteMessage.getNotification().getTitle(),
                        (String) remoteMessage.getData().get(FCM_CODE_SENDER_USERID),
                        (String)remoteMessage.getData().get(FCM_CODE_RECEIPT_USERID),
                        myBitmap);


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