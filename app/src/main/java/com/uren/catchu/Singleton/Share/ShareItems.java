package com.uren.catchu.Singleton.Share;

import android.graphics.Bitmap;

import com.uren.catchu.MainPackage.MainFragments.Share.Models.ImageShareItemBox;
import com.uren.catchu.MainPackage.MainFragments.Share.Models.VideoShareItemBox;

import java.util.ArrayList;
import java.util.List;

import catchu.model.BucketUploadResponse;
import catchu.model.Comment;
import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.Media;
import catchu.model.Post;
import catchu.model.User;

public class ShareItems {

    static ShareItems shareItemsInstance = null;
    Post post;
    List<ImageShareItemBox> imageShareItemBoxes;
    List<VideoShareItemBox> videoShareItemBoxes;
    Bitmap textBitmap;
    String selectedShareType;
    int shareTryCount = 0;
    BucketUploadResponse bucketUploadResponse;
    GroupRequestResultResultArrayItem selectedGroup;
    boolean shareStartedValue;

    public static ShareItems getInstance() {
        if (shareItemsInstance == null) {
            shareItemsInstance = new ShareItems();
        }
        return shareItemsInstance;
    }

    public ShareItems() {
        imageShareItemBoxes = new ArrayList<ImageShareItemBox>();
        videoShareItemBoxes = new ArrayList<VideoShareItemBox>();
        selectedGroup = new GroupRequestResultResultArrayItem();
        post = new Post();
        post.setAttachments(new ArrayList<Media>());
        post.setAllowList(new ArrayList<User>());
        post.setComments(new ArrayList<Comment>());
    }

    public static void setInstance(ShareItems shareItems) {
        shareItemsInstance = shareItems;
    }

    public static ShareItems getShareItemsInstance() {
        return shareItemsInstance;
    }

    public void setShareItemsInstance(ShareItems shareItemsInstance) {
        this.shareItemsInstance = shareItemsInstance;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Bitmap getTextBitmap() {
        return textBitmap;
    }

    public void setTextBitmap(Bitmap textBitmap) {
        this.textBitmap = textBitmap;
    }

    public List<ImageShareItemBox> getImageShareItemBoxes() {
        return imageShareItemBoxes;
    }

    public void setImageShareItemBoxes(List<ImageShareItemBox> imageShareItemBoxes) {
        this.imageShareItemBoxes = imageShareItemBoxes;
    }

    public List<VideoShareItemBox> getVideoShareItemBoxes() {
        return videoShareItemBoxes;
    }

    public void setVideoShareItemBoxes(List<VideoShareItemBox> videoShareItemBoxes) {
        this.videoShareItemBoxes = videoShareItemBoxes;
    }

    public void addImageShareItemBox(ImageShareItemBox shareItemBox) {
        imageShareItemBoxes.add(shareItemBox);
    }

    public void clearImageShareItemBox() {
        imageShareItemBoxes.clear();
    }

    public void addVideoShareItemBox(VideoShareItemBox videoShareItemBox) {
        videoShareItemBoxes.add(videoShareItemBox);
    }

    public void clearVideoShareItemBox() {
        videoShareItemBoxes.clear();
    }

    public int getTotalMediaCount() {
        return videoShareItemBoxes.size() + imageShareItemBoxes.size();
    }

    public String getSelectedShareType() {
        return selectedShareType;
    }

    public void setSelectedShareType(String selectedShareType) {
        this.selectedShareType = selectedShareType;
    }

    public GroupRequestResultResultArrayItem getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(GroupRequestResultResultArrayItem selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

    public int getShareTryCount() {
        return shareTryCount;
    }

    public void setShareTryCount(int shareTryCount) {
        this.shareTryCount = shareTryCount;
    }

    public BucketUploadResponse getBucketUploadResponse() {
        return bucketUploadResponse;
    }

    public void setBucketUploadResponse(BucketUploadResponse bucketUploadResponse) {
        this.bucketUploadResponse = bucketUploadResponse;
    }

    public boolean isShareStartedValue() {
        return shareStartedValue;
    }

    public void setShareStartedValue(boolean shareStartedValue) {
        this.shareStartedValue = shareStartedValue;
    }

    public static synchronized void reset(){
        shareItemsInstance = null;
    }

}
