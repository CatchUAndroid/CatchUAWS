package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Adapters.MessageWithPersonAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.MessageBox;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;

import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGES;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGE_CONTENT;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_WITH_PERSON;

public class MessageDeleteProcess {

    public static void deleteSelectedMessages(final Context context, final ArrayList<MessageBox> messageBoxList,
                                              String messageContentId, final MessageWithPersonAdapter adapter,
                                              final String chattedUserId, final RelativeLayout r1, final RelativeLayout r2,
                                              final TextView deleteMsgCntTv) {
        for (final MessageBox messageBox : messageBoxList) {
            if (messageBox.isSelectedForDelete()) {

                FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGE_CONTENT)
                        .child(messageContentId).child(messageBox.getMessageId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        completeMessageDeletion(messageBoxList, messageBox,
                                adapter, context, chattedUserId, r1, r2, deleteMsgCntTv);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }
    }

    public static void completeMessageDeletion(ArrayList<MessageBox> messageBoxList, MessageBox messageBox,
                                               MessageWithPersonAdapter adapter, Context context,
                                               String chattedUserid, RelativeLayout r1, RelativeLayout r2,
                                               TextView deleteMsgCntTv) {
        int deletedIndex = messageBoxList.indexOf(messageBox);
        messageBoxList.remove(messageBox);
        adapter.notifyItemRemoved(deletedIndex);
        adapter.notifyItemRangeChanged(deletedIndex, messageBoxList.size());

        boolean checkVal = false;

        if (messageBoxList != null) {
            for (MessageBox messageBox1 : messageBoxList) {
                if (messageBox1.isSelectedForDelete()) {
                    checkVal = true;
                    break;
                }
            }
        }

        if (!checkVal) {
            r1.setVisibility(View.VISIBLE);
            r2.setVisibility(View.GONE);
            adapter.setDeleteActivated(false);
            deleteMsgCntTv.setText("");
        }

        if (messageBoxList != null && messageBoxList.size() == 0)
            deleteMessageContent(context, chattedUserid);
    }

    private static void deleteMessageContent(final Context context, String chattedUserid) {
        FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES).child(FB_CHILD_WITH_PERSON)
                .child(AccountHolderInfo.getUserID()).child(chattedUserid).child(FB_CHILD_MESSAGE_CONTENT)
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES).child(FB_CHILD_WITH_PERSON)
                .child(chattedUserid).child(AccountHolderInfo.getUserID()).child(FB_CHILD_MESSAGE_CONTENT)
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
