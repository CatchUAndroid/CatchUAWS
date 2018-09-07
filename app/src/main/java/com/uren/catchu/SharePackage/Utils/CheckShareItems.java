package com.uren.catchu.SharePackage.Utils;

import android.util.Log;

import com.uren.catchu.Singleton.ShareItems;

import static com.uren.catchu.Constants.StringConstants.SPACE_VALUE;

public class CheckShareItems {

    public static boolean shareIsPossible(){

        if(ShareItems.getInstance().getShare().getImageUrl() != null) {
            if (!ShareItems.getInstance().getShare().getImageUrl().trim().equals(""))
                return true;
        }

        if(ShareItems.getInstance().getShare().getText() != null) {
            if (!ShareItems.getInstance().getShare().getText().trim().equals(""))
                return true;
        }

        return false;
    }
}
