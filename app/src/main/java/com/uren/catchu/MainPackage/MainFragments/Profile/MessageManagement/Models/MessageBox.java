package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models;

import catchu.model.User;

public class MessageBox {

    String messageId;
    long date;
    String messageText;
    User senderUser;
    User receiptUser;
    boolean selectedForDelete;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public User getSenderUser() {
        return senderUser;
    }

    public void setSenderUser(User senderUser) {
        this.senderUser = senderUser;
    }

    public User getReceiptUser() {
        return receiptUser;
    }

    public void setReceiptUser(User receiptUser) {
        this.receiptUser = receiptUser;
    }

    public boolean isSelectedForDelete() {
        return selectedForDelete;
    }

    public void setSelectedForDelete(boolean selectedForDelete) {
        this.selectedForDelete = selectedForDelete;
    }
}
