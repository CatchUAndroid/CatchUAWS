package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.LoginPackage.Models.LoginUser;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.FeedContextMenuManager;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Adapters.MessageListAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.MessageUpdateCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses.MessageUpdateProcess;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.MessageListBox;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.Interfaces.AccountHolderInfoCallback;

import java.util.ArrayList;
import java.util.Map;

import catchu.model.UserProfile;
import catchu.model.UserProfileProperties;
import io.fabric.sdk.android.Fabric;

import static com.uren.catchu.Constants.NumericConstants.MESSAGE_LIMIT_COUNT;
import static com.uren.catchu.Constants.NumericConstants.REC_MAXITEM_LIMIT_COUNT;
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
import static com.uren.catchu.Constants.StringConstants.FB_VALUE_NOTIFICATION_READ;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_CHATTED_USER;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_RECEIPT_USERID;

public class MessageListActivity extends AppCompatActivity {

    ImageView searchToolbarBackImgv;
    ImageView imgCancelSearch;
    ImageView searchToolbarAddItemImgv;
    EditText editTextSearch;
    ProgressBar progressBar;
    RecyclerView recyclerView;

    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;

    ArrayList<MessageListBox> messageListBoxes;
    MessageListAdapter messageListAdapter;
    LinearLayoutManager linearLayoutManager;
    public static Activity thisActivity;

    boolean setAdapterVal = false;
    boolean adapterLoaded = false;
    boolean dataLoaded = true;

    int limitValue;
    int pastVisibleItems, visibleItemCount, totalItemCount;

    String receiptUserId; // Bu benim

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        thisActivity = this;
        Fabric.with(this, new Crashlytics());

        receiptUserId = (String) getIntent().getSerializableExtra(FCM_CODE_RECEIPT_USERID);
        initVariables();
        addListeners();
        checkMyInformation();
    }

    public void initVariables() {
        try {
            initUIValues();
            messageListBoxes = new ArrayList<>();
            searchToolbarAddItemImgv.setVisibility(View.GONE);
            setRecyclerViewScroll();
            limitValue = REC_MAXITEM_LIMIT_COUNT;
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageListActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void initUIValues() {
        searchToolbarBackImgv = findViewById(R.id.searchToolbarBackImgv);
        imgCancelSearch = findViewById(R.id.imgCancelSearch);
        searchToolbarAddItemImgv = findViewById(R.id.searchToolbarAddItemImgv);
        editTextSearch = findViewById(R.id.editTextSearch);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void checkMyInformation() {
        try {
            if (AccountHolderInfo.getInstance() != null && AccountHolderInfo.getUserID() != null && !AccountHolderInfo.getUserID().isEmpty())
                updateClusterStatus();
            else if (receiptUserId != null && !receiptUserId.isEmpty()) {
                AccountHolderInfo.getInstance();
                AccountHolderInfo.setAccountHolderInfoCallback(new AccountHolderInfoCallback() {
                    @Override
                    public void onAccountHolderIfoTaken(UserProfile userProfile) {
                        if (receiptUserId.equals(userProfile.getUserInfo().getUserid()))
                            updateClusterStatus();
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

    private void updateClusterStatus() {

        MessageUpdateProcess.updateClusterStatus(AccountHolderInfo.getUserID(),
                FB_VALUE_NOTIFICATION_READ, new MessageUpdateCallback() {
                    @Override
                    public void onComplete() {
                        getMessages();
                    }

                    @Override
                    public void onFailed(String errMessage) {

                    }
                });
    }

    public void addListeners() {
        searchToolbarBackImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.toString() != null && !s.toString().trim().isEmpty()) {
                    if (messageListAdapter != null)
                        messageListAdapter.updateAdapter(s.toString());
                    imgCancelSearch.setVisibility(View.VISIBLE);
                    searchToolbarBackImgv.setVisibility(View.GONE);
                } else {
                    if (messageListAdapter != null)
                        messageListAdapter.updateAdapter("");
                    imgCancelSearch.setVisibility(View.GONE);
                    searchToolbarBackImgv.setVisibility(View.VISIBLE);
                }
            }
        });

        imgCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextSearch != null)
                    editTextSearch.setText("");
                imgCancelSearch.setVisibility(View.GONE);
                CommonUtils.hideKeyBoard(MessageListActivity.this);
                searchToolbarBackImgv.setVisibility(View.VISIBLE);

            }
        });
    }

    public void getMessages() {
        try {
            databaseReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES).child(FB_CHILD_WITH_PERSON)
                    .child(AccountHolderInfo.getUserID());

            Query query = databaseReference
                    .orderByChild(FB_CHILD_MESSAGE_CONTENT + "/" + FB_CHILD_LAST_MESSAGE_DATE)
                    .limitToLast(limitValue);

            valueEventListener = query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.getChildren() != null) {

                        System.out.println("dataSnapshot.getKey():" + dataSnapshot.getKey());
                        System.out.println("dataSnapshot.getValue():" + dataSnapshot.getValue());

                        for (DataSnapshot outboundSnapshot : dataSnapshot.getChildren()) {
                            System.out.println("getMessages.outboundSnapshot.getKey():" + outboundSnapshot.getKey());
                            System.out.println("getMessages.outboundSnapshot.getValue():" + outboundSnapshot.getValue());
                            getUserDetail(outboundSnapshot);
                        }

                        if (!dataLoaded) {
                            dataLoaded = true;
                            messageListAdapter.removeProgressLoading();
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

    private void setRecyclerViewScroll() {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (dataLoaded) {

                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            dataLoaded = false;
                            limitValue = limitValue + REC_MAXITEM_LIMIT_COUNT;
                            messageListAdapter.addProgressLoading();
                            getMessages();
                        }
                    }
                }
            }
        });
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

                                String contentId = (String) contentMap.get(FB_CHILD_CONTENT_ID);
                                if (contentId != null && !contentId.isEmpty())
                                    getLastMessage(userProfile.getUserInfo(), contentId);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {

                        }

                        @Override
                        public void onTaskContinue() {
                        }
                    }, AccountHolderInfo.getUserID(), outboundSnapshot.getKey(), token);

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
                    startMessageWithPersonActivity(messageListBox);
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

    private void startMessageWithPersonActivity(MessageListBox messageListBox) {

        try {
            if (MessageWithPersonActivity.thisActivity != null) {
                MessageWithPersonActivity.thisActivity.finish();
            }

            Intent intent = new Intent(this, MessageWithPersonActivity.class);
            intent.putExtra(FCM_CODE_CHATTED_USER, getChattedUserInfo(messageListBox));
            intent.putExtra(FCM_CODE_RECEIPT_USERID, AccountHolderInfo.getUserID());
            startActivity(intent);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageListActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public LoginUser getChattedUserInfo(MessageListBox messageListBox) {
        LoginUser user = null;
        try {
            user = new LoginUser();
            if (messageListBox != null && messageListBox.getUserProfileProperties() != null) {
                UserProfileProperties userProfileProperties = messageListBox.getUserProfileProperties();

                if (userProfileProperties.getEmail() != null && !userProfileProperties.getEmail().isEmpty())
                    user.setEmail(userProfileProperties.getEmail());

                if (userProfileProperties.getName() != null && !userProfileProperties.getName().isEmpty())
                    user.setName(userProfileProperties.getName());

                if (userProfileProperties.getProfilePhotoUrl() != null && !userProfileProperties.getProfilePhotoUrl().isEmpty())
                    user.setProfilePhotoUrl(userProfileProperties.getProfilePhotoUrl());

                if (userProfileProperties.getUserid() != null && !userProfileProperties.getUserid().isEmpty())
                    user.setUserId(userProfileProperties.getUserid());

                if (userProfileProperties.getUsername() != null && !userProfileProperties.getUsername().isEmpty())
                    user.setUsername(userProfileProperties.getUsername());
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageListActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return user;
    }

    public void fillMessageBoxList(DataSnapshot outboundSnapshot, UserProfileProperties userProfileProperties) {
        try {

            System.out.println("fillMessageBoxList.outboundSnapshot.getKey():" + outboundSnapshot.getKey());
            System.out.println("fillMessageBoxList.outboundSnapshot.getValue():" + outboundSnapshot.getValue());

            MessageListBox messageListBox = new MessageListBox();

            messageListBox.setUserProfileProperties(userProfileProperties);

            Map<String, Object> map = (Map) outboundSnapshot.getValue();
            messageListBox.setMessageText((String) map.get(FB_CHILD_MESSAGE));
            messageListBox.setDate((long) map.get(FB_CHILD_DATE));

            Map<String, Object> senderMap = (Map) map.get(FB_CHILD_SENDER);
            String senderUserid = (String) senderMap.get(FB_CHILD_USERID);

            Map<String, Object> receiptMap = (Map) map.get(FB_CHILD_RECEIPT);
            String receiptUserid = (String) receiptMap.get(FB_CHILD_USERID);

            if (receiptUserid.equals(AccountHolderInfo.getUserID()))
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (NextActivity.thisActivity == null)
            this.startActivity(new Intent(MessageListActivity.this, NextActivity.class));

        this.finish();
    }
}
