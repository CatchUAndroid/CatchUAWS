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
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderFollowings;

import catchu.model.FollowInfoResultArrayItem;

import static com.uren.catchu.Constants.StringConstants.FRIEND_CREATE_FOLLOW_DIRECTLY;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_FOLLOW;

public class UserDataUtil {

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

    public static void setProfilePicture(Context context, String url, String name, TextView shortNameTv, ImageView profilePicImgView) {
        if (url != null && !url.trim().isEmpty()) {
            shortNameTv.setVisibility(View.GONE);
            Glide.with(context)
                    .load(url)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePicImgView);
        } else {
            if (name != null && !name.trim().isEmpty()) {
                shortNameTv.setVisibility(View.VISIBLE);
                shortNameTv.setText(UserDataUtil.getShortenUserName(name));
                profilePicImgView.setImageDrawable(null);
            } else {
                shortNameTv.setVisibility(View.GONE);
                Glide.with(context)
                        .load(context.getResources().getIdentifier("user_icon", "drawable", context.getPackageName()))
                        .apply(RequestOptions.circleCropTransform())
                        .into(profilePicImgView);
            }
        }
    }

    public static void updateFollowButton(Context context, Boolean friendRelation, Boolean pendingFriendRequest, Button displayButton) {
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
                buttonShape = ShapeUtil.getShape(context.getResources().getColor(R.color.black_25_transparent, null),
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

    public static void updatePendingButton(Context context, Button displayButton) {
        CommonUtils.hideKeyBoard(context);
        GradientDrawable buttonShape;
        displayButton.setText(context.getResources().getString(R.string.ACCEPT_REQUEST));
        displayButton.setTextColor(context.getResources().getColor(R.color.White, null));
        buttonShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                0, GradientDrawable.RECTANGLE, 15, 0);
        displayButton.setBackground(buttonShape);
    }
}
