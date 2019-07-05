package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.AdMobUtils;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.LoginPackage.Models.LoginUser;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Adapters.MessageListAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.MessageUpdateCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses.MessageUpdateProcess;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.MessageListBox;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.Map;

import catchu.model.UserProfile;
import catchu.model.UserProfileProperties;
import io.fabric.sdk.android.Fabric;

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

public class MessageListActivity extends AppCompatActivity {

    ImageView searchToolbarBackImgv;
    ImageView imgCancelSearch;
    ImageView searchToolbarAddItemImgv;
    EditText editTextSearch;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    AdView adView;

    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;

    ArrayList<MessageListBox> messageListBoxes;
    MessageListAdapter messageListAdapter;
    LinearLayoutManager linearLayoutManager;
    ///public static Activity thisActivity;

    boolean setAdapterVal = false;
    boolean adapterLoaded = false;
    boolean dataLoaded = true;

    int limitValue;
    int pastVisibleItems, visibleItemCount, totalItemCount;

    //String receiptUserId; // Bu benim

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        //thisActivity = this;
        Fabric.with(this, new Crashlytics());

        //receiptUserId = (String) getIntent().getSerializableExtra(FCM_CODE_RECEIPT_USERID);
        initVariables();
        addListeners();
        checkMyInformation();
    }

    public void initVariables() {
        initUIValues();
        messageListBoxes = new ArrayList<>();
        searchToolbarAddItemImgv.setVisibility(View.GONE);
        setRecyclerViewScroll();
        limitValue = REC_MAXITEM_LIMIT_COUNT;
        MobileAds.initialize(MessageListActivity.this, getResources().getString(R.string.ADMOB_APP_ID));
        AdMobUtils.loadBannerAd(adView);
    }

    private void initUIValues() {
        searchToolbarBackImgv = findViewById(R.id.searchToolbarBackImgv);
        imgCancelSearch = findViewById(R.id.imgCancelSearch);
        searchToolbarAddItemImgv = findViewById(R.id.searchToolbarAddItemImgv);
        editTextSearch = findViewById(R.id.editTextSearch);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        adView = findViewById(R.id.adView);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void checkMyInformation() {
        if (AccountHolderInfo.getInstance() != null && AccountHolderInfo.getUserID() != null && !AccountHolderInfo.getUserID().isEmpty())
            updateClusterStatus();
        /*else if (receiptUserId != null && !receiptUserId.isEmpty()) {
            AccountHolderInfo.getInstance();
            AccountHolderInfo.setAccountHolderInfoCallback(new AccountHolderInfoCallback() {
                @Override
                public void onAccountHolderIfoTaken(UserProfile userProfile) {
                    if (receiptUserId.equals(userProfile.getUserInfo().getUserid()))
                        updateClusterStatus();
                }
            });
        }*/
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
                        progressBar.setVisibility(View.GONE);
                        CommonUtils.showToastShort(MessageListActivity.this,
                                getResources().getString(R.string.SOMETHING_WENT_WRONG) + " : " +
                                errMessage);
                    }
                });
    }

    public void addListeners() {
        searchToolbarBackImgv.setOnClickListener(v -> onBackPressed());

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

        imgCancelSearch.setOnClickListener(v -> {
            if (editTextSearch != null)
                editTextSearch.setText("");
            imgCancelSearch.setVisibility(View.GONE);
            CommonUtils.hideKeyBoard(MessageListActivity.this);
            searchToolbarBackImgv.setVisibility(View.VISIBLE);

        });
    }

    public void getMessages() {
        databaseReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES).child(FB_CHILD_WITH_PERSON)
                .child(AccountHolderInfo.getUserID());

        Query query = databaseReference
                .orderByChild(FB_CHILD_MESSAGE_CONTENT + "/" + FB_CHILD_LAST_MESSAGE_DATE)
                .limitToLast(limitValue);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getChildren() != null) {

                    if (!dataLoaded) {
                        dataLoaded = true;
                        if (messageListAdapter.isShowingProgressLoading())
                            messageListAdapter.removeProgressLoading();
                    }

                    for (DataSnapshot outboundSnapshot : dataSnapshot.getChildren()) {
                        if (outboundSnapshot != null)
                            getUserDetail(outboundSnapshot);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                CommonUtils.showToastShort(MessageListActivity.this,
                        getResources().getString(R.string.SOMETHING_WENT_WRONG) + " : " +
                                databaseError.getMessage());
            }
        });
    }

    private void getUserDetail(final DataSnapshot outboundSnapshot) {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                UserDetail loadUserDetail = new UserDetail(new OnEventListener<UserProfile>() {
                    @Override
                    public void onSuccess(UserProfile userProfile) {

                        try {
                            if (userProfile != null && userProfile.getUserInfo() != null) {
                                Map<String, Object> map = (Map) outboundSnapshot.getValue();
                                Map<String, Object> contentMap = (Map) map.get(FB_CHILD_MESSAGE_CONTENT);

                                String contentId = (String) contentMap.get(FB_CHILD_CONTENT_ID);
                                if (contentId != null && !contentId.isEmpty())
                                    getLastMessage(userProfile.getUserInfo(), contentId);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            CommonUtils.showToastShort(MessageListActivity.this,
                                    getResources().getString(R.string.SOMETHING_WENT_WRONG) + " : " +
                                            e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        progressBar.setVisibility(View.GONE);
                        /*CommonUtils.showToastShort(MessageListActivity.this,
                                getResources().getString(R.string.SOMETHING_WENT_WRONG) + " : " +
                                        e.getMessage());*/
                    }

                    @Override
                    public void onTaskContinue() {
                    }
                }, AccountHolderInfo.getUserID(), outboundSnapshot.getKey(), "true", token);

                loadUserDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onTokenFail(String message) {
            }
        });
    }

    private void getLastMessage(final UserProfileProperties userProfileProperties, final String messageContentId) {
        if (userProfileProperties.getUserid() != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGE_CONTENT)
                    .child(messageContentId);

            Query query = databaseReference.orderByChild(FB_CHILD_DATE).limitToLast(1);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null && dataSnapshot.getChildren() != null) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            messageBoxListCheck(child, userProfileProperties);
                            adapterLoadCheck();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressBar.setVisibility(View.GONE);
                    CommonUtils.showToastShort(MessageListActivity.this,
                            getResources().getString(R.string.SOMETHING_WENT_WRONG) + " : " +
                                    databaseError.getMessage());
                }
            });
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

    private void messageBoxListCheck(DataSnapshot mDataSnapshot, UserProfileProperties userProfileProperties) {
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
    }

    private void adapterLoadCheck() {
        if (!setAdapterVal) {
            adapterLoaded = true;
            setAdapter();
        } else {
            if (messageListAdapter != null)
                messageListAdapter.notifyDataSetChanged();
        }
    }

    public void setAdapter() {
        messageListAdapter = new MessageListAdapter(MessageListActivity.this, messageListBoxes, new ItemClickListener() {
            @Override
            public void onClick(Object object, int clickedItem) {
                MessageListBox messageListBox = (MessageListBox) object;
                startMessageWithPersonActivity(messageListBox);
            }
        });

        recyclerView.setAdapter(messageListAdapter);
        linearLayoutManager = new LinearLayoutManager(MessageListActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        setAdapterVal = true;
        progressBar.setVisibility(View.GONE);
    }

    private void startMessageWithPersonActivity(MessageListBox messageListBox) {
        Intent intent = new Intent(this, MessageWithPersonActivity.class);
        intent.putExtra(FCM_CODE_CHATTED_USER, getChattedUserInfo(messageListBox));
        startActivity(intent);
    }

    public LoginUser getChattedUserInfo(MessageListBox messageListBox) {
        LoginUser user = new LoginUser();

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
        return user;
    }

    public void fillMessageBoxList(DataSnapshot outboundSnapshot, UserProfileProperties userProfileProperties) {
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseReference != null && valueEventListener != null)
            databaseReference.removeEventListener(valueEventListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
