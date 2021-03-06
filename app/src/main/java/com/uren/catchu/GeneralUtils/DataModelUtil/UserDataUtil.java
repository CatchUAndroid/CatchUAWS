package com.uren.catchu.GeneralUtils.DataModelUtil;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.R;

import static com.uren.catchu.Constants.StringConstants.CHAR_AMPERSAND;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_FOLLOWING;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_NONE;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_OWN;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_PENDING;

public class UserDataUtil {

    public static void setNameOrUserName(String name, String username, TextView textView) {
        int nameMaxLen = 25;

        if (name != null && !name.isEmpty()) {
            if (name.length() > nameMaxLen)
                textView.setText(name.trim().substring(0, nameMaxLen) + "...");
            else
                textView.setText(name);
        } else if (username != null && !username.isEmpty()) {
            if (username.length() > nameMaxLen)
                textView.setText(CHAR_AMPERSAND + username.trim().substring(0, nameMaxLen) + "...");
            else
                textView.setText(CHAR_AMPERSAND + username);
        } else
            textView.setVisibility(View.GONE);
    }

    public static void setName(String name, TextView nameTextView) {
        int nameMaxLen = 25;
        if (name != null && nameTextView != null && !name.isEmpty()) {
            nameTextView.setVisibility(View.VISIBLE);
            if (name.length() > nameMaxLen)
                nameTextView.setText(name.trim().substring(0, nameMaxLen) + "...");
            else
                nameTextView.setText(name);
        } else if (nameTextView != null)
            nameTextView.setVisibility(View.GONE);
    }

    public static void setUsername(String username, TextView usernameTextView) {
        int nameMaxLen = 25;
        if (username != null && usernameTextView != null && !username.isEmpty()) {
            usernameTextView.setVisibility(View.VISIBLE);
            if (username.length() > nameMaxLen)
                usernameTextView.setText(CHAR_AMPERSAND + username.trim().substring(0, nameMaxLen) + "...");
            else
                usernameTextView.setText(CHAR_AMPERSAND + username);
        } else if (usernameTextView != null)
            usernameTextView.setVisibility(View.GONE);
    }

    public static String getShortenUserName(String name) {
        String returnValue = "";
        if (name != null && !name.trim().isEmpty()) {
            String[] seperatedName = name.trim().split(" ");
            for (String word : seperatedName) {
                if (returnValue.length() < 3)
                    returnValue = returnValue + word.substring(0, 1).toUpperCase();
            }
        }

        return returnValue;
    }

    public static void setProfilePicture(Context context, String url, String name, String username, TextView shortNameTv, ImageView profilePicImgView) {
        if (context == null) return;

        if (url != null && !url.trim().isEmpty()) {
            shortNameTv.setVisibility(View.GONE);
            Glide.with(context)
                    .load(url)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePicImgView);
            profilePicImgView.setPadding(1, 1, 1, 1); // degerler asagidaki imageShape strokeWidth ile aynı tutulmalı
        } else {
            if (name != null && !name.trim().isEmpty()) {
                shortNameTv.setVisibility(View.VISIBLE);
                shortNameTv.setText(UserDataUtil.getShortenUserName(name));
                profilePicImgView.setImageDrawable(null);
            } else if (username != null && !username.trim().isEmpty()) {
                shortNameTv.setVisibility(View.VISIBLE);
                shortNameTv.setText(UserDataUtil.getShortenUserName(username));
                profilePicImgView.setImageDrawable(null);
            } else {
                shortNameTv.setVisibility(View.GONE);
                Glide.with(context)
                        .load(R.mipmap.icon_user_profile)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profilePicImgView);
            }
        }

        GradientDrawable imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                context.getResources().getColor(R.color.White, null),
                GradientDrawable.OVAL, 50, 3);
        profilePicImgView.setBackground(imageShape);
    }

    public static void updateFollowButton(Context context, Boolean friendRelation, Boolean pendingFriendRequest, Button displayButton,
                                          Boolean isHideKeybard) {

        if (isHideKeybard != null && isHideKeybard)
            CommonUtils.hideKeyBoard(context);

        GradientDrawable buttonShape;
        if (friendRelation != null && friendRelation) {
            displayButton.setText(context.getResources().getString(R.string.following));
            displayButton.setTextColor(context.getResources().getColor(R.color.Black, null));
            buttonShape = ShapeUtil.getShape(context.getResources().getColor(R.color.White, null),
                    context.getResources().getColor(R.color.Gray, null), GradientDrawable.RECTANGLE, 15, 2);
        } else {
            if (pendingFriendRequest != null && pendingFriendRequest) {
                displayButton.setText(context.getResources().getString(R.string.request_sended));
                displayButton.setTextColor(context.getResources().getColor(R.color.White, null));
                buttonShape = ShapeUtil.getShape(context.getResources().getColor(R.color.Silver, null),
                        0, GradientDrawable.RECTANGLE, 15, 0);
            } else {
                displayButton.setText(context.getResources().getString(R.string.follow));
                displayButton.setTextColor(context.getResources().getColor(R.color.White, null));
                buttonShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                        0, GradientDrawable.RECTANGLE, 15, 0);
            }
        }
        displayButton.setBackground(buttonShape);
    }

    public static void updatePendingApproveButton(Context context, Button displayButton) {
        CommonUtils.hideKeyBoard(context);
        GradientDrawable buttonShape;
        displayButton.setTextColor(context.getResources().getColor(R.color.White, null));
        buttonShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                0, GradientDrawable.RECTANGLE, 15, 0);
        displayButton.setBackground(buttonShape);
    }

    public static void updatePendingRejectButton(Context context, Button displayButton) {
        CommonUtils.hideKeyBoard(context);
        GradientDrawable buttonShape;
        displayButton.setTextColor(context.getResources().getColor(R.color.Black, null));
        buttonShape = ShapeUtil.getShape(context.getResources().getColor(R.color.White, null),
                context.getResources().getColor(R.color.Gray, null), GradientDrawable.RECTANGLE, 15, 2);
        displayButton.setBackground(buttonShape);
    }

    /**
     * Like list vs için yeni yazılan serviste takip durumu için tek bir değer dönüyor. 2.versiyon bu yüzden yazıldı.
     */
    public static void updateFollowButton2(Context context, String followStatus, Button displayButton,
                                           Boolean isHideKeybard) {

        if (isHideKeybard != null && isHideKeybard)
            CommonUtils.hideKeyBoard(context);

        GradientDrawable buttonShape = null;
        displayButton.setVisibility(View.VISIBLE);

        if (followStatus.equals(FOLLOW_STATUS_FOLLOWING)) {
            //takip ediliyor
            displayButton.setText(context.getResources().getString(R.string.following));
            displayButton.setTextColor(context.getResources().getColor(R.color.Black, null));
            buttonShape = ShapeUtil.getShape(context.getResources().getColor(R.color.White, null),
                    context.getResources().getColor(R.color.Gray, null), GradientDrawable.RECTANGLE, 15, 2);

        } else if (followStatus.equals(FOLLOW_STATUS_PENDING)) {
            //istek gonderildi
            displayButton.setText(context.getResources().getString(R.string.request_sended));
            displayButton.setTextColor(context.getResources().getColor(R.color.White, null));
            buttonShape = ShapeUtil.getShape(context.getResources().getColor(R.color.Silver, null),
                    0, GradientDrawable.RECTANGLE, 15, 0);

        } else if (followStatus.equals(FOLLOW_STATUS_OWN)) {
            //kendisi
            displayButton.setVisibility(View.GONE);
        } else if (followStatus.equals(FOLLOW_STATUS_NONE)) {
            //takip/istek yok
            displayButton.setText(context.getResources().getString(R.string.follow));
            displayButton.setTextColor(context.getResources().getColor(R.color.White, null));
            buttonShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                    0, GradientDrawable.RECTANGLE, 15, 0);

        } else {
            //ne olur ne olmaz durumu :)
            displayButton.setText(context.getResources().getString(R.string.follow));
            displayButton.setTextColor(context.getResources().getColor(R.color.White, null));
            buttonShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                    0, GradientDrawable.RECTANGLE, 15, 0);
        }

        displayButton.setBackground(buttonShape);
    }

    public static void updateMessagingButton(Context context, String followStatus, Button sendMessageBtn) {
        if (sendMessageBtn != null) {
            sendMessageBtn.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.White, null),
                    context.getResources().getColor(R.color.Gray, null), GradientDrawable.RECTANGLE, 15, 2));

            if (followStatus != null) {
                if (followStatus.equals(FOLLOW_STATUS_OWN))
                    sendMessageBtn.setVisibility(View.GONE);
                else
                    sendMessageBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    public static void updateInviteButton(Context context, Button displayButton, Boolean isHideKeyboard) {
        if (isHideKeyboard != null && isHideKeyboard)
            CommonUtils.hideKeyBoard(context);

        GradientDrawable buttonShape;
        displayButton.setText(context.getResources().getString(R.string.invite));
        displayButton.setTextColor(context.getResources().getColor(R.color.Coral, null));
        buttonShape = ShapeUtil.getShape(context.getResources().getColor(R.color.White, null),
                context.getResources().getColor(R.color.Coral, null), GradientDrawable.RECTANGLE, 15, 3);

        displayButton.setBackground(buttonShape);
    }
}
