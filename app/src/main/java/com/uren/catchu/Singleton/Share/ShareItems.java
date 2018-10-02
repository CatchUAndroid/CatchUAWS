package com.uren.catchu.Singleton.Share;

import android.graphics.Bitmap;

import com.uren.catchu.SharePackage.Models.ImageShareItemBox;
import com.uren.catchu.SharePackage.Models.VideoShareItemBox;

import java.util.ArrayList;
import java.util.List;

import catchu.model.Comment;
import catchu.model.Media;
import catchu.model.Post;
import catchu.model.User;

public class ShareItems{

    private static ShareItems shareItemsInstance = null;
    private static Post post;
    private static List<ImageShareItemBox> imageShareItemBoxes;
    private static List<VideoShareItemBox> videoShareItemBoxes;
    private static Bitmap textBitmap;

    public static ShareItems getInstance(){
        if(shareItemsInstance == null) {
            shareItemsInstance = new ShareItems();
        }
        return shareItemsInstance;
    }

    public ShareItems(){
        imageShareItemBoxes = new ArrayList<ImageShareItemBox>();
        videoShareItemBoxes = new ArrayList<VideoShareItemBox>();
        post = new Post();
        post.setAttachments(new ArrayList<Media>());
        post.setAllowList(new ArrayList<User>());
        post.setComments(new ArrayList<Comment>());
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

    public List<ImageShareItemBox> getImageShareItemBoxes() {
        return imageShareItemBoxes;
    }

    public void setImageShareItemBoxes(List<ImageShareItemBox> imageShareItemBoxes) {
        ShareItems.imageShareItemBoxes = imageShareItemBoxes;
    }

    public List<VideoShareItemBox> getVideoShareItemBoxes() {
        return videoShareItemBoxes;
    }

    public void setVideoShareItemBoxes(List<VideoShareItemBox> videoShareItemBoxes) {
        ShareItems.videoShareItemBoxes = videoShareItemBoxes;
    }

    public void addImageShareItemBox(ImageShareItemBox shareItemBox){
        imageShareItemBoxes.add(shareItemBox);
    }

    public void clearImageShareItemBox(){
        imageShareItemBoxes.clear();
    }

    public void addVideoShareItemBox(VideoShareItemBox videoShareItemBox){
        videoShareItemBoxes.add(videoShareItemBox);
    }

    public void clearVideoShareItemBox(){
        videoShareItemBoxes.clear();
    }

    public int getTotalMediaCount(){
        return videoShareItemBoxes.size() + imageShareItemBoxes.size();
    }

}
