package com.uren.catchu.GeneralUtils.DataModelUtil;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.GeneralUtils.ClickableImage.EffectTouchListener;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.R;

public class GroupDataUtil {

    public static void setGroupPicture(Context context, String url, String name, TextView shortNameTv, ImageView groupPicImgView) {
        try {
            if (url != null && !url.trim().isEmpty()) {
                shortNameTv.setVisibility(View.GONE);
                Glide.with(context)
                        .load(url)
                        .apply(RequestOptions.circleCropTransform())
                        .into(groupPicImgView);
                groupPicImgView.setPadding(1,1,1,1); // degerler asagidaki imageShape strokeWidth ile aynı tutulmalı
            } else {
                if (name != null && !name.trim().isEmpty()) {
                    shortNameTv.setVisibility(View.VISIBLE);
                    shortNameTv.setText(UserDataUtil.getShortenUserName(name));
                    groupPicImgView.setImageDrawable(null);
                }else {
                    shortNameTv.setVisibility(View.GONE);
                    Glide.with(context)
                            .load(context.getResources().getIdentifier("groups_icon_500", "drawable", context.getPackageName()))
                            .apply(RequestOptions.centerInsideTransform())
                            .into(groupPicImgView);
                }
            }

            GradientDrawable imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                    context.getResources().getColor(R.color.White, null),
                    GradientDrawable.OVAL, 50, 3);
            groupPicImgView.setBackground(imageShape);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, GroupDataUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }
}
