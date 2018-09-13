package com.uren.catchu.SharePackage.Utils;

import android.content.Context;
import android.util.Log;

import com.uren.catchu.R;
import com.uren.catchu.Singleton.ShareItems;

import static com.uren.catchu.Constants.StringConstants.SPACE_VALUE;

public class CheckShareItems {

    Context context;
    String errorMessage = "";

    public CheckShareItems(Context context){
        this.context = context;
    }

    public boolean shareIsPossible(){

        if(ShareItems.getInstance().getPhotoSelectAdapter().getPictureUri() != null)
            return true;

        if(ShareItems.getInstance().getShare().getText() != null) {
            if (!ShareItems.getInstance().getShare().getText().trim().equals(""))
                return true;
        }

        errorMessage = context.getResources().getString(R.string.pleaseAddShareItem);
        return false;
    }

    public boolean isLocationLoaded(){
        if(ShareItems.getInstance().getShare().getLocation() == null) {
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
