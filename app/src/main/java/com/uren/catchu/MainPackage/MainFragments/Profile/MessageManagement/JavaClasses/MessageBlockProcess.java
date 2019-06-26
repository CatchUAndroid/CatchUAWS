package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.BlockCompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.MessageBlockCallback;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.HashMap;
import java.util.Map;

import static com.uren.catchu.Constants.StringConstants.FB_CHILD_CONTENT_ID;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_LAST_MESSAGE_DATE;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGES;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGE_BLOCK;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGE_CONTENT;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_WITH_PERSON;

public class MessageBlockProcess {

    public static void getUserBlocked(final String userid1, final String userid2, final MessageBlockCallback messageBlockCallback) {

        DatabaseReference blocked = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGE_BLOCK).child(userid1).child(userid2);

        blocked.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Boolean value = (Boolean) dataSnapshot.getValue();

                if (value != null && value == true)
                    messageBlockCallback.OnReturn(userid1);
                else {
                    DatabaseReference blocked2 = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGE_BLOCK).child(userid2).child(userid1);

                    blocked2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Boolean value2 = (Boolean) dataSnapshot.getValue();

                            if (value2 != null && value2 == true)
                                messageBlockCallback.OnReturn(userid2);
                            else
                                messageBlockCallback.OnReturn(null);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void blockPerson(String userid1, String userid2, final BlockCompleteCallback blockCompleteCallback) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGE_BLOCK).child(userid1);

        final Map<String, Object> values = new HashMap<>();
        values.put(userid2, true);

        databaseReference.updateChildren(values).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                blockCompleteCallback.OnComplete(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                blockCompleteCallback.OnComplete(false);
            }
        });
    }

    public static void unBlockPerson(String userid1, String userid2, final BlockCompleteCallback blockCompleteCallback) {

        FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGE_BLOCK).child(userid1).child(userid2)
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                blockCompleteCallback.OnComplete(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                blockCompleteCallback.OnComplete(false);
            }
        });
    }
}
