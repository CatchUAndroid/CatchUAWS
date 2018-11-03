package com.uren.catchu.GeneralUtils.DynamicLinkUtil;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.R;

import static com.uren.catchu.Constants.StringConstants.DYNAMIC_LINK_DOMAIN;

public class DynamicLinkUtil {

    public static void shareShortDynamicLink(final Context context, final android.support.v4.app.Fragment fragment) {
        Task<ShortDynamicLink> createLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(buildDynamicLink(context)))
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
        String dynamicLink;

        dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setDynamicLinkDomain(DYNAMIC_LINK_DOMAIN)
                .setLink(Uri.parse(CommonUtils.getGooglePlayAppLink(context)))
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                //.setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder().setTitle("Share This app"))
                .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder().build())
                .buildDynamicLink().getUri().toString();

        return dynamicLink;
    }
}
