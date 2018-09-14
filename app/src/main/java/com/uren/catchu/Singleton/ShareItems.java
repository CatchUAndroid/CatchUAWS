package com.uren.catchu.Singleton;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.FriendListRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.GeneralUtils.PhotoSelectAdapter;
import com.uren.catchu.Singleton.Share.MediaBoxItem;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import catchu.model.FriendList;
import catchu.model.Location;
import catchu.model.Post;
import catchu.model.UserProfileProperties;

public class ShareItems{

    private static ShareItems shareItemsInstance = null;
    private static Post post;
    private static List<MediaBoxItem> mediaBoxItems;
    private static Bitmap textBitmap;

    public static ShareItems getInstance(){
        if(shareItemsInstance == null) {
            post = new Post();
            mediaBoxItems = new ArrayList<>();
            shareItemsInstance = new ShareItems();
        }
        return shareItemsInstance;
    }

    public ShareItems(){
    }

    public static void setInstance(ShareItems shareItems){
        shareItemsInstance = shareItems;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        ShareItems.post = post;
    }

    public Bitmap getTextBitmap() {
        return textBitmap;
    }

    public void setTextBitmap(Bitmap textBitmap) {
        ShareItems.textBitmap = textBitmap;
    }

    public List<MediaBoxItem> getMediaBoxItems() {
        return mediaBoxItems;
    }

    public void setMediaBoxItems(List<MediaBoxItem> mediaBoxItems) {
        ShareItems.mediaBoxItems = mediaBoxItems;
    }

    public void addMediaBox(MediaBoxItem mediaBoxItem){
        mediaBoxItems.add(mediaBoxItem);
    }

    public void removeMediaBox(MediaBoxItem mediaBoxItem){
        mediaBoxItems.remove((MediaBoxItem)mediaBoxItem);
    }
}
