package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models;

public class FCMItems {

    String otherUserDeviceToken;
    String title;
    String body;
    String photoUrl;
    String senderUserid;
    String receiptUserid;
    String messageid;

    public String getOtherUserDeviceToken() {
        return otherUserDeviceToken;
    }

    public void setOtherUserDeviceToken(String otherUserDeviceToken) {
        this.otherUserDeviceToken = otherUserDeviceToken;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getSenderUserid() {
        return senderUserid;
    }

    public void setSenderUserid(String senderUserid) {
        this.senderUserid = senderUserid;
    }

    public String getReceiptUserid() {
        return receiptUserid;
    }

    public void setReceiptUserid(String receiptUserid) {
        this.receiptUserid = receiptUserid;
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }
}
