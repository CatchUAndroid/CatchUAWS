package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.MessageBox;

import java.util.List;

import catchu.model.Post;

public class MessageDiffCallback extends DiffUtil.Callback {

    private List<MessageBox> mOldMessageList;
    private List<MessageBox> mNewMessageList;

    public MessageDiffCallback(List<MessageBox> oldMessageList, List<MessageBox> newMessageList) {
        this.mOldMessageList = oldMessageList;
        this.mNewMessageList = newMessageList;
    }

    @Override
    public int getOldListSize() {
        return mOldMessageList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewMessageList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldMessageList.get(oldItemPosition).getMessageId() == mNewMessageList.get(newItemPosition).getMessageId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

        final MessageBox oldMessage = mOldMessageList.get(oldItemPosition);
        final MessageBox newMessage = mNewMessageList.get(newItemPosition);
        boolean x = oldMessage.getMessageText().equals(newMessage.getMessageText());
        return x;

    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator

        MessageBox newMessage = mNewMessageList.get(newItemPosition);
        MessageBox oldMessage = mOldMessageList.get(oldItemPosition);
        Bundle diffBundle = new Bundle();
        if (newMessage.getMessageId() != oldMessage.getMessageId()) {
            diffBundle.putString("messageId", newMessage.getMessageId());
        }

        return diffBundle;


        //return super.getChangePayload(oldItemPosition, newItemPosition);
    }

}
