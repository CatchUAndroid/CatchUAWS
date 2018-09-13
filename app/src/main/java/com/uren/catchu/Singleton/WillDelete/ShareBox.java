package com.uren.catchu.Singleton.WillDelete;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;

import catchu.model.Location;

public class ShareBox {
    private static ShareBox shareBoxInstance = null;
    private static ArrayList<Uri> photoUriList;
    private static ArrayList<Uri> videoUriList;
    private static ArrayList<Bitmap> textBitmapList;
    private static ArrayList<String> textList;
    private static Location location;

    public static ShareBox getInstance(){
        if(shareBoxInstance == null) {
            photoUriList = new ArrayList<Uri>();
            videoUriList = new ArrayList<Uri>();
            textBitmapList = new ArrayList<Bitmap>();
            textList = new ArrayList<String>();
            shareBoxInstance = new ShareBox();
        }
        return shareBoxInstance;
    }

    public ShareBox(){ }

    public static void setInstance(ShareBox shareBox){
        shareBoxInstance = shareBox;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        ShareBox.location = location;
    }

    /* +++++++++++++++++++++ Photo Uri list functions ++++++++++++++++++++++++++++++*/
    public ArrayList<Uri> getPhotoUriList() {
        return photoUriList;
    }

    public void setPhotoUriList(ArrayList<Uri> photoUriList) {
        ShareBox.photoUriList = photoUriList;
    }

    public void addUriToPhotoList(Uri uri){
        photoUriList.add(uri);
    }

    public void removeUriFromPhotoList(Uri uri){
        photoUriList.remove((Uri)uri);
    }

    /* +++++++++++++++++++++ Video Uri list functions ++++++++++++++++++++++++++++++*/
    public ArrayList<Uri> getVideoUriList() {
        return videoUriList;
    }

    public void setVideoUriList(ArrayList<Uri> videoUriList) {
        ShareBox.videoUriList = videoUriList;
    }

    public void addUriToVideoList(Uri uri){
        videoUriList.add(uri);
    }

    public void removeUriFromVideoList(Uri uri){
        videoUriList.remove((Uri)uri);
    }

    /* +++++++++++++++++++++ Text Uri list functions ++++++++++++++++++++++++++++++*/
    public ArrayList<String> getTextList() {
        return textList;
    }

    public void setTextList(ArrayList<String> textList) {
        ShareBox.textList = textList;
    }

    public void addTextToTextList(String text){
        textList.add(text);
    }

    public void removeTextFromTextList(String text){
        textList.remove(text);
    }

    /* +++++++++++++++++++++ Text Bitmap list functions ++++++++++++++++++++++++++++++*/
    public ArrayList<Bitmap> getTextBitmapList() {
        return textBitmapList;
    }

    public void setTextBitmapList(ArrayList<Bitmap> textBitmapList) {
        ShareBox.textBitmapList = textBitmapList;
    }

    public void addBitmapToTextBitmapList(Bitmap bitmap){
        textBitmapList.add(bitmap);
    }

    public void removeBitmapFromBitmapList(Bitmap bitmap){
        textBitmapList.remove(bitmap);
    }
}
