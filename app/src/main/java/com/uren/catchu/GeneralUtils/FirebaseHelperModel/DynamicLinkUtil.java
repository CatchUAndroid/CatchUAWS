package com.uren.catchu.GeneralUtils.FirebaseHelperModel;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;

import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Models.Contact;
import com.uren.catchu.R;

import androidx.fragment.app.Fragment;

import static com.uren.catchu.Constants.StringConstants.APP_INVITATION_LINK;
import static com.uren.catchu.Constants.StringConstants.DYNAMIC_LINK_DOMAIN;

public class DynamicLinkUtil {

    public static void setAppInvitationLink(final Context context, Fragment fragment){

            Intent intent = new Intent();
            String msg = context.getResources().getString(R.string.CONTACT_INVITE_MESSAGE) + " " + APP_INVITATION_LINK;
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, msg);
            intent.setType("text/plain");
            if (intent.resolveActivity(context.getPackageManager()) != null)
                fragment.startActivity(Intent.createChooser(intent, "Share"));
    }

    public static void setAppInvitationLinkForSms(Context context, Contact contact, Fragment fragment) {
            if (contact != null && contact.getPhoneNumber() != null) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:" + contact.getPhoneNumber()));
                String msg = context.getResources().getString(R.string.CONTACT_INVITE_MESSAGE) + " " + APP_INVITATION_LINK;
                sendIntent.putExtra("sms_body", msg);
                fragment.startActivity(sendIntent);
            }
    }

    /*public static void shareShortDynamicLink(final Context context, final androidx.fragment.app.Fragment fragment) {
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
    }

    public static String buildDynamicLink(Context context) {
        String dynamicLink = null;

            dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setDynamicLinkDomain(DYNAMIC_LINK_DOMAIN)
                    .setLink(Uri.parse(CommonUtils.getGooglePlayAppLink(context)))
                    .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                    .setIosParameters(new DynamicLink.IosParameters.Builder("").build())
                    //.setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder().setTitle("Share This app"))
                    .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder().build())
                    .buildDynamicLink().getUri().toString();


        return dynamicLink;
    }*/
}
