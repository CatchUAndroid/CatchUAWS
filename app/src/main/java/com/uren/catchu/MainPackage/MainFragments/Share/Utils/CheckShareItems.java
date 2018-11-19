package com.uren.catchu.MainPackage.MainFragments.Share.Utils;

import android.content.Context;

import com.uren.catchu.R;
import com.uren.catchu.Singleton.Share.ShareItems;

public class CheckShareItems {

    Context context;
    String errorMessage = "";

    public CheckShareItems(Context context) {
        this.context = context;
    }

    public boolean shareIsPossible() {
        if (ShareItems.getInstance().getImageShareItemBoxes() != null && ShareItems.getInstance().getImageShareItemBoxes().size() > 0)
            return true;

        if (ShareItems.getInstance().getVideoShareItemBoxes() != null && ShareItems.getInstance().getVideoShareItemBoxes().size() > 0)
            return true;

        if (ShareItems.getInstance().getPost() != null && ShareItems.getInstance().getPost().getMessage() != null &&
                !ShareItems.getInstance().getPost().getMessage().trim().isEmpty())
            return true;

        errorMessage = context.getResources().getString(R.string.pleaseAddShareItem);
        return false;
    }

    public boolean isLocationLoaded() {
        if (ShareItems.getInstance().getPost() != null && ShareItems.getInstance().getPost().getLocation() == null) {
            errorMessage = context.getResources().getString(R.string.locationIsEmpty);
            return false;
        }
        return true;
    }

    public String getErrMessage() {
        return errorMessage;
    }

    public void setErrMessage(String errMessage) {
        errorMessage = errMessage;
    }
}
