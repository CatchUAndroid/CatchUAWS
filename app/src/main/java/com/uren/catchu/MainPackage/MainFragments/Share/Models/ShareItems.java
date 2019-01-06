package com.uren.catchu.MainPackage.MainFragments.Share.Models;

import java.util.ArrayList;
import java.util.List;

import catchu.model.BucketUploadResponse;
import catchu.model.Comment;
import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.Media;
import catchu.model.Post;
import catchu.model.User;

public class ShareItems {

    Post post;
    List<ImageShareItemBox> imageShareItemBoxes;
    List<VideoShareItemBox> videoShareItemBoxes;
    String selectedShareType;
    int shareTryCount = 0;
    BucketUploadResponse bucketUploadResponse;
    GroupRequestResultResultArrayItem selectedGroup;

    public ShareItems() {
        imageShareItemBoxes = new ArrayList<>();
        videoShareItemBoxes = new ArrayList<>();
        selectedGroup = new GroupRequestResultResultArrayItem();
        post = new Post();
        post.setAttachments(new ArrayList<Media>());
        post.setAllowList(new ArrayList<User>());
        post.setComments(new ArrayList<Comment>());
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public List<ImageShareItemBox> getImageShareItemBoxes() {
        return imageShareItemBoxes;
    }

    public List<VideoShareItemBox> getVideoShareItemBoxes() {
        return videoShareItemBoxes;
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
}
