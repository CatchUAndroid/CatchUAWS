package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/*import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;*/
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.MessageSentFCMCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.FCMItems;
import com.uren.catchu.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.uren.catchu.Constants.StringConstants.FCM_CODE_BODY;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_DATA;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_MESSAGE_ID;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_NOTIFICATION;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_PHOTO_URL;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_RECEIPT_USERID;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_SENDER_USERID;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_TITLE;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_TO;
import static com.uren.catchu.Constants.StringConstants.FCM_MESSAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.FCM_MESSAGE_TYPE_NORMAL_TO_PERSON;
import static com.uren.catchu.Constants.StringConstants.FCM_MESSAGE_URL;

public class SendMessageToFCM {

    static OkHttpClient mClient = new OkHttpClient();

    public static void sendMessage(final Context context,
                                   final FCMItems fcmItems,
                                   final MessageSentFCMCallback messageSentFCMCallback) {

        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    JSONObject root = new JSONObject();
                    JSONObject notification = new JSONObject();
                    notification.put(FCM_CODE_BODY, fcmItems.getBody());
                    notification.put(FCM_CODE_TITLE, fcmItems.getTitle());

                    JSONObject data = new JSONObject();
                    data.put(FCM_CODE_PHOTO_URL, fcmItems.getPhotoUrl());
                    data.put(FCM_CODE_SENDER_USERID, fcmItems.getSenderUserid());
                    data.put(FCM_CODE_RECEIPT_USERID, fcmItems.getReceiptUserid());
                    data.put(FCM_CODE_MESSAGE_ID, fcmItems.getMessageid());
                    data.put(FCM_MESSAGE_TYPE, FCM_MESSAGE_TYPE_NORMAL_TO_PERSON);
                    root.put(FCM_CODE_NOTIFICATION, notification);
                    root.put(FCM_CODE_DATA, data);
                    root.put(FCM_CODE_TO, fcmItems.getOtherUserDeviceToken());

                    String result = postToFCM(root.toString(), context, messageSentFCMCallback);
                    return result;
                } catch (Exception e) {
                    messageSentFCMCallback.onFailed(e);
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    JSONObject resultJson = new JSONObject(result);
                    int success, failure;
                    success = resultJson.getInt("success");
                    failure = resultJson.getInt("failure");
                    Toast.makeText(context, "Message Success: " + success + "Message Failed: " + failure, Toast.LENGTH_LONG).show();

                    if(failure > 0)
                        messageSentFCMCallback.onFailed(new Exception("Message Send Failed!"));
                    else
                        messageSentFCMCallback.onSuccess();

                } catch (Exception e) {
                    messageSentFCMCallback.onFailed(e);
                    e.printStackTrace();
                    Toast.makeText(context, "Message Failed, Unknown error occurred.", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    public static String postToFCM(String bodyString, Context context,
                                   MessageSentFCMCallback messageSentFCMCallback) throws IOException {
        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, bodyString);
            Request request = new Request.Builder()
                    .url(FCM_MESSAGE_URL)
                    .post(body)
                    .addHeader("Authorization", "key=" + context.getResources().getString(R.string.FCM_SERVER_KEY))
                    .build();
            Response response = mClient.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            messageSentFCMCallback.onFailed(e);
            e.printStackTrace();
        }
        return "";
    }
}
