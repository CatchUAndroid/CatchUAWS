package com.uren.catchu.GeneralUtils.FirebaseHelperModel;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Models.Contact;
import com.uren.catchu.R;

import static com.uren.catchu.Constants.StringConstants.APP_INVITATION_LINK;
import static com.uren.catchu.Constants.StringConstants.DYNAMIC_LINK_DOMAIN;

public class DynamicLinkUtil {

    public static void setAppInvitationLink(final Context context, android.support.v4.app.Fragment fragment){

        try {
            Intent intent = new Intent();
            String msg = context.getResources().getString(R.string.CONTACT_INVITE_MESSAGE) + " " + APP_INVITATION_LINK;
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, msg);
            intent.setType("text/plain");
            if (intent.resolveActivity(context.getPackageManager()) != null)
                fragment.startActivity(Intent.createChooser(intent, "Share"));
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, DynamicLinkUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public static void setAppInvitationLinkForSms(Context context, Contact contact, android.support.v4.app.Fragment fragment) {
        try {
            if (contact != null && contact.getPhoneNumber() != null) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:" + contact.getPhoneNumber()));
                String msg = context.getResources().getString(R.string.CONTACT_INVITE_MESSAGE) + " " + APP_INVITATION_LINK;
                sendIntent.putExtra("sms_body", msg);
                fragment.startActivity(sendIntent);
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, DynamicLinkUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    /*public static void shareShortDynamicLink(final Context context, final android.support.v4.app.Fragment fragment) {
        try {
            Task<ShortDynamicLink> createLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    //.setLongLink(Uri.parse(buildDynamicLink(context)))
                    //.setLongLink(Uri.parse(APP_INVITATION_LINK))
                    .buildShortDynamicLink()
                    .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                        @Override
                        public void onComplete(@NonNull Task<ShortDynamicLink> task) {

                            if (task.isSuccessful()) {
                                Uri shortLink = task.getResult().getShortLink();
                                //Uri flowChartLink = task.getResult().getPreviewLink();

                                Intent intent = new Intent();
                                String msg = context.getResources().getString(R.string.CONTACT_INVITE_MESSAGE) + " " + shortLink;
                                intent.setAction(Intent.ACTION_SEND);
                                intent.putExtra(Intent.EXTRA_TEXT, msg);
                                intent.setType("text/plain");
                                if (intent.resolveActivity(context.getPackageManager()) != null)
                                    fragment.startActivity(Intent.createChooser(intent, "Share"));
                            }
                        }
                    });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, DynamicLinkUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public static String buildDynamicLink(Context context) {
        String dynamicLink = null;

        try {
            dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setDynamicLinkDomain(DYNAMIC_LINK_DOMAIN)
                    .setLink(Uri.parse(CommonUtils.getGooglePlayAppLink(context)))
                    .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                    .setIosParameters(new DynamicLink.IosParameters.Builder("").build())
                    //.setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder().setTitle("Share This app"))
                    .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder().build())
                    .buildDynamicLink().getUri().toString();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, DynamicLinkUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }

        return dynamicLink;
    }*/
}
