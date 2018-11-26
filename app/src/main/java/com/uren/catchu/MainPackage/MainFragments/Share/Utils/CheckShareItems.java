package com.uren.catchu.MainPackage.MainFragments.Share.Utils;

import android.content.Context;

import com.uren.catchu.R;
import com.uren.catchu.MainPackage.MainFragments.Share.Models.ShareItems;

public class CheckShareItems {

    Context context;
    String errorMessage = "";
    ShareItems shareItems;

    public CheckShareItems(Context context, ShareItems shareItems) {
        this.context = context;
        this.shareItems = shareItems;
    }

    public boolean shareIsPossible() {
        if (shareItems.getPost() != null && shareItems.getPost().getLocation() == null) {
            errorMessage = context.getResources().getString(R.string.locationIsEmpty);
            return false;
        }

        if (shareItems.getImageShareItemBoxes() != null && shareItems.getImageShareItemBoxes().size() > 0)
            return true;

        if (shareItems.getVideoShareItemBoxes() != null && shareItems.getVideoShareItemBoxes().size() > 0)
            return true;

        if (shareItems.getPost() != null && shareItems.getPost().getMessage() != null &&
                !shareItems.getPost().getMessage().trim().isEmpty())
            return true;

        errorMessage = context.getResources().getString(R.string.pleaseAddShareItem);
        return false;
    }

    public String getErrMessage() {
        return errorMessage;
    }

    public void setErrMessage(String errMessage) {
        errorMessage = errMessage;
    }
}
