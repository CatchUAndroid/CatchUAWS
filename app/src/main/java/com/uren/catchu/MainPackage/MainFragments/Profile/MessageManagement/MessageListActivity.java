package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Adapters.MessageListAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.MessageListBox;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import catchu.model.User;
import catchu.model.UserProfile;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_CONTENT_ID;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_DATE;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_IS_SEEN;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_LAST_MESSAGE_DATE;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGE;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGES;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGE_CONTENT;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_RECEIPT;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_SENDER;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_USERID;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_WITH_PERSON;

public class MessageListActivity extends AppCompatActivity {

    ImageView searchToolbarBackImgv;
    ImageView imgCancelSearch;
    EditText editTextSearch;
    ProgressBar progressBar;
    RecyclerView recyclerView;

    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;

    ArrayList<MessageListBox> messageListBoxes;
    MessageListAdapter messageListAdapter;
    LinearLayoutManager linearLayoutManager;

    boolean setAdapterVal = false;
    boolean adapterLoaded = false;

    FirebaseAuth mAuth;
    String userid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message_list);
        initVariables();
        addListeners();

        if (userid != null && !userid.isEmpty())
            getMessages();
    }

    public void initVariables() {
        try {
            searchToolbarBackImgv = findViewById(R.id.searchToolbarBackImgv);
            imgCancelSearch = findViewById(R.id.imgCancelSearch);
            editTextSearch = findViewById(R.id.editTextSearch);
            progressBar = findViewById(R.id.progressBar);
            recyclerView = findViewById(R.id.recyclerView);
            messageListBoxes = new ArrayList<>();
            mAuth = FirebaseAuth.getInstance();
            userid = mAuth.getCurrentUser().getUid();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageListActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void addListeners() {
        try {
            searchToolbarBackImgv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageListActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void getMessages() {
        try {
            databaseReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES).child(FB_CHILD_WITH_PERSON)
                    .child(userid);

            Query query = databaseReference
                    .orderByChild(FB_CHILD_MESSAGE_CONTENT + "/" + FB_CHILD_LAST_MESSAGE_DATE)
                    .limitToLast(20);
            // TODO: 19.12.2018 - buraya bakacagiz

            valueEventListener = query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.getChildren() != null) {

                        System.out.println("dataSnapshot.getKey():" + dataSnapshot.getKey());
                        System.out.println("dataSnapshot.getValue():" + dataSnapshot.getValue());

                        for (DataSnapshot outboundSnapshot : dataSnapshot.getChildren()) {
                            System.out.println("outboundSnapshot.getKey():" + outboundSnapshot.getKey());
                            System.out.println("outboundSnapshot.getValue():" + outboundSnapshot.getValue());
                            getUserDetail(outboundSnapshot);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageListActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void getUserDetail(final DataSnapshot outboundSnapshot) {
        try {
            AccountHolderInfo.getToken(new TokenCallback() {
                @Override
                public void onTokenTaken(String token) {
                    UserDetail loadUserDetail = new UserDetail(new OnEventListener<UserProfile>() {
                        @Override
                        public void onSuccess(UserProfile userProfile) {

                            if (userProfile != null && userProfile.getUserInfo() != null) {
                                Map<String, Object> map = (Map) outboundSnapshot.getValue();
                                Map<String, Object> contentMap = (Map) map.get(FB_CHILD_MESSAGE_CONTENT);
                                getLastMessage(userProfile.getUserInfo(), (String) contentMap.get(FB_CHILD_CONTENT_ID));
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {

                        }

                        @Override
                        public void onTaskContinue() {
                        }
                    }, userid, outboundSnapshot.getKey(), token);

                    loadUserDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageListActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void getLastMessage(final UserProfileProperties userProfileProperties, final String messageContentId) {
        try {
            if (userProfileProperties.getUserid() != null) {
                databaseReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGE_CONTENT)
                        .child(messageContentId);

                Query query = databaseReference.orderByChild(FB_CHILD_DATE).limitToLast(1);

                valueEventListener = query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        if (dataSnapshot != null && dataSnapshot.getChildren() != null) {
                            System.out.println("xxxxxxxx>dataSnapshot.getKey():" + dataSnapshot.getKey());
                            System.out.println("xxxxxxxx>dataSnapshot.getValue():" + dataSnapshot.getValue());

                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                System.out.println("---->child.getKey():" + child.getKey());
                                System.out.println("---->child.getValue():" + child.getValue());

                                messageBoxListCheck(child, userProfileProperties);
                                adapterLoadCheck();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if (databaseError != null) {
                            ErrorSaveHelper.writeErrorToDB(MessageListActivity.this, this.getClass().getSimpleName(),
                                    new Object() {
                                    }.getClass().getEnclosingMethod().getName(), databaseError.toString());
                        }
                    }
                });
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageListActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void messageBoxListCheck(DataSnapshot mDataSnapshot, UserProfileProperties userProfileProperties) {
        try {
            boolean notInList = false;
            for (MessageListBox messageListBox : messageListBoxes) {
                if (messageListBox != null && messageListBox.getUserProfileProperties() != null &&
                        messageListBox.getUserProfileProperties().getUserid() != null) {

                    if (messageListBox.getUserProfileProperties().getUserid().equals(userProfileProperties.getUserid())) {
                        notInList = true;
                        break;
                    }
                }
            }

            if (!notInList) {
                fillMessageBoxList(mDataSnapshot, userProfileProperties);
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageListActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void adapterLoadCheck() {
        try {
            if (!setAdapterVal) {
                adapterLoaded = true;
                setAdapter();
            } else {
                if (messageListAdapter != null)
                    messageListAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageListActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void setAdapter() {
        try {
            messageListAdapter = new MessageListAdapter(MessageListActivity.this, messageListBoxes, new ItemClickListener() {
                @Override
                public void onClick(Object object, int clickedItem) {
                    MessageListBox messageListBox = (MessageListBox) object;
                    startMessageWithPersonFragment(messageListBox);

                }
            });

            recyclerView.setAdapter(messageListAdapter);
            linearLayoutManager = new LinearLayoutManager(MessageListActivity.this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            setAdapterVal = true;
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageListActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void startMessageWithPersonFragment(MessageListBox messageListBox) {

        try {
            /*if (mFragmentNavigation != null && messageListBox != null && messageListBox.getUserProfileProperties() != null) {
                User user = new User();
                user.setUsername(messageListBox.getUserProfileProperties().getUsername());
                user.setName(messageListBox.getUserProfileProperties().getName());
                user.setUserid(messageListBox.getUserProfileProperties().getUserid());
                user.setProfilePhotoUrl(messageListBox.getUserProfileProperties().getProfilePhotoUrl());
                user.setEmail(messageListBox.getUserProfileProperties().getEmail());
                user.setProvider(messageListBox.getUserProfileProperties().getProvider());
                mFragmentNavigation.pushFragment(new MessageWithPersonFragment(user), ANIMATE_LEFT_TO_RIGHT);
            }*/
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageListActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void fillMessageBoxList(DataSnapshot outboundSnapshot, UserProfileProperties userProfileProperties) {
        try {

            System.out.println("outboundSnapshot.getKey():" + outboundSnapshot.getKey());
            System.out.println("outboundSnapshot.getValue():" + outboundSnapshot.getValue());

            MessageListBox messageListBox = new MessageListBox();

            messageListBox.setUserProfileProperties(userProfileProperties);

            Map<String, Object> map = (Map) outboundSnapshot.getValue();
            messageListBox.setMessageText((String) map.get(FB_CHILD_MESSAGE));
            messageListBox.setDate((long) map.get(FB_CHILD_DATE));

            Map<String, Object> senderMap = (Map) map.get(FB_CHILD_SENDER);
            String senderUserid = (String) senderMap.get(FB_CHILD_USERID);

            Map<String, Object> receiptMap = (Map) map.get(FB_CHILD_RECEIPT);
            String receiptUserid = (String) receiptMap.get(FB_CHILD_USERID);

            if (receiptUserid.equals(userid))
                messageListBox.setIamReceipt(true);
            else
                messageListBox.setIamReceipt(false);

            messageListBox.setSeen((boolean) receiptMap.get(FB_CHILD_IS_SEEN));

            messageListBoxes.add(messageListBox);

        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageListActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (databaseReference != null && valueEventListener != null)
                databaseReference.removeEventListener(valueEventListener);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageListActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }
}
