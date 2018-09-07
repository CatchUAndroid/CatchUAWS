package com.uren.catchu.Singleton;

import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.FriendListRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.GeneralUtils.PhotoSelectAdapter;

import catchu.model.FriendList;
import catchu.model.Share;
import catchu.model.ShareRequest;
import catchu.model.UserProfileProperties;

public class ShareItems {

    private static ShareItems shareItemsInstance = null;
    private static Share share;
    private static PhotoSelectAdapter photoSelectAdapter;

    public static ShareItems getInstance(){

        if(shareItemsInstance == null) {
            share = new Share();
            shareItemsInstance = new ShareItems();
        }

        return shareItemsInstance;
    }

    public ShareItems(){
    }

    public static void setInstance(ShareItems shareItems){
        shareItemsInstance = shareItems;
    }

    public static Share getShare() {
        return share;
    }

    public void setShare(Share share) {
        ShareItems.share = share;
    }

    public PhotoSelectAdapter getPhotoSelectAdapter() {
        return photoSelectAdapter;
    }

    public void setPhotoSelectAdapter(PhotoSelectAdapter photoSelectAdapter) {
        ShareItems.photoSelectAdapter = photoSelectAdapter;
    }
}
