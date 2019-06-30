package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.uren.catchu.FragmentControllers.FragNavController;
import com.uren.catchu.GeneralUtils.AdMobUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.LoginPackage.Models.LoginUser;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Adapters.MessageWithPersonAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.BlockCompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.GetContentIdCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.GetDeviceTokenCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.GetNotificationCountCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.MessageBlockCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.MessageDeleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.NotificationStatusCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses.MessageAddProcess;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses.MessageBlockProcess;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses.MessageDeleteProcess;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses.MessageGetProcess;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses.MessageUpdateProcess;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.MessageBox;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.TokenInfo;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.ShowSelectedPhotoFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.Interfaces.AccountHolderInfoCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import catchu.model.User;
import catchu.model.UserProfile;
import catchu.model.UserProfileProperties;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import io.fabric.sdk.android.Fabric;

import static com.uren.catchu.Constants.NumericConstants.MESSAGE_LIMIT_COUNT;
import static com.uren.catchu.Constants.StringConstants.CHAR_AMPERSAND;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_DATE;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_DEVICE_TOKEN;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_IS_SEEN;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGE;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGE_CONTENT;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_NAME;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_RECEIPT;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_SENDER;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_TOKEN;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_USERID;
import static com.uren.catchu.Constants.StringConstants.FB_VALUE_NOTIFICATION_READ;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_CHATTED_USER;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_RECEIPT_USERID;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_SENDER_USERID;

public class MessageWithPersonActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    hani.momanii.supernova_emoji_library.Helper.EmojiconEditText messageEdittext;
    Button sendMessageBtn;
    ImageView commonToolbarbackImgv;
    ImageView profilePicImgView;
    TextView shortUserNameTv;
    TextView toolbarTitle;
    TextView toolbarSubTitle;
    ImageView moreSettingsImgv;

    RelativeLayout relLayout1;
    RelativeLayout relLayout2;
    ImageView commonToolbarbackImgv2;
    ImageView deleteMsgImgv;
    TextView deleteMsgCntTv;
    RelativeLayout edittextRelLayout;
    ImageView smileyImgv;
    RelativeLayout messageReachLay;
    TextView waitingMsgCntTv;
    ImageView waitingMsgImgv;
    View mainLinearLayout;
    LinearLayout llBlock;
    TextView blockTv;
    AdView adView;

    public static User chattedUser = new User();
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;

    ArrayList<MessageBox> messageBoxList;
    ArrayList<MessageBox> messageBoxListTemp;
    MessageWithPersonAdapter messageWithPersonAdapter;
    LinearLayoutManager linearLayoutManager;
    MessageBox lastAddedMessage;

    public static Activity thisActivity;
    Context context;

    String messageContentId = null;
    boolean iblocked = false;
    //long lastChattedTime;
    String chattedUserDeviceToken = null;
    private String chattedUserSignInValue = null;

    boolean setAdapterVal = false;
    boolean itemAdded = false;
    boolean adapterLoaded = false;
    boolean progressLoaded = true;

    int limitValue;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    int loadCode;
    int invisibleMsgCnt = 0;
    Menu menuOpts = null;
    PopupMenu popupMenu = null;

    private static final int CODE_BOTTOM_LOADED = 0;
    private static final int CODE_TOP_LOADED = 1;

    LoginUser loginUser;
    String senderUserId;  // Mesaji daha once gondermis kisi(ben degilim)
    String receiptUserId; // Bu benim

    int notificationReadCount = 0, notificationDeleteCount = 0, notificationSendCount = 0;
    String myNotificationStatus = null;
    String otherUserNotificationStatus = null;
    String clusterNotificationStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_with_person);
        thisActivity = this;

        context = MessageWithPersonActivity.this;
        Fabric.with(this, new Crashlytics());
        initUIValues();

        loginUser = (LoginUser) getIntent().getSerializableExtra(FCM_CODE_CHATTED_USER);
        //senderUserId = (String) getIntent().getSerializableExtra(FCM_CODE_SENDER_USERID);
        //receiptUserId = (String) getIntent().getSerializableExtra(FCM_CODE_RECEIPT_USERID);
        checkMyInformation();
    }

    private void checkMyInformation() {
        if (AccountHolderInfo.getInstance() != null && AccountHolderInfo.getUserID() != null && !AccountHolderInfo.getUserID().isEmpty()) {
            checkSenderInformation();
        }/*else if (receiptUserId != null && !receiptUserId.isEmpty()) {
            AccountHolderInfo.getInstance();
            AccountHolderInfo.setAccountHolderInfoCallback(new AccountHolderInfoCallback() {
                @Override
                public void onAccountHolderIfoTaken(UserProfile userProfile) {
                    if (receiptUserId.equals(userProfile.getUserInfo().getUserid()))
                        checkSenderInformation();
                }
            });
        }*/
    }

    private void checkUserBlocked() {
        if (chattedUser != null && chattedUser.getUserid() != null && !chattedUser.getUserid().trim().isEmpty()) {
            MessageBlockProcess.getUserBlocked(AccountHolderInfo.getUserID(), chattedUser.getUserid(), new MessageBlockCallback() {
                @Override
                public void OnReturn(String userid) {
                    if (userid != null) {
                        if (userid.equals(AccountHolderInfo.getUserID())) {
                            iblocked = true;
                            setBlockedValues(0, true);
                        } else if (userid.equals(chattedUser.getUserid())) {
                            setBlockedValues(1, true);
                        } else {
                            setBlockedValues(0, false);
                        }
                    } else {
                        setBlockedValues(0, false);
                    }
                }
            });
        }
    }

    private void setBlockedValues(int person, boolean blockedVal) {
        if (blockedVal) {
            llBlock.setVisibility(View.VISIBLE);
            messageEdittext.setEnabled(false);

            if (person == 0) {
                blockTv.setText(getResources().getString(R.string.REMOVE_BLOCK_PERSON));

                if (menuOpts != null && menuOpts.getItem(0) != null)
                    menuOpts.getItem(0).setTitle(getResources().getString(R.string.UNBLOCK));
            } else if (person == 1) {
                blockTv.setText(getResources().getString(R.string.TO_BE_REMOVED_BLOCK_PERSON));

                if (menuOpts != null && menuOpts.getItem(0) != null)
                    menuOpts.getItem(0).setTitle(getResources().getString(R.string.BLOCK));
            } else {
                llBlock.setVisibility(View.GONE);
                messageEdittext.setEnabled(true);
            }
        } else {
            llBlock.setVisibility(View.GONE);
            messageEdittext.setEnabled(true);

            if (person == 0) {
                if (menuOpts != null && menuOpts.getItem(0) != null)
                    menuOpts.getItem(0).setTitle(getResources().getString(R.string.BLOCK));
            }
        }
    }

    public void checkSenderInformation() {
        if (loginUser != null) {
            setChattedUserInfo(loginUser);
            initVariables();
        } /*else if (senderUserId != null && !senderUserId.isEmpty()) {
            getChattedUserDetail(senderUserId);
        }*/
    }

    private void setChattedUserInfo(LoginUser loginUser) {
        chattedUser.setUserid(loginUser.getUserId());
        chattedUser.setUsername(loginUser.getUsername());
        chattedUser.setName(loginUser.getName());
        chattedUser.setProfilePhotoUrl(loginUser.getProfilePhotoUrl());
    }

    /*private void getChattedUserDetail(final String chattedUserId) {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                UserDetail loadUserDetail = new UserDetail(new OnEventListener<UserProfile>() {

                    @Override
                    public void onSuccess(UserProfile up) {
                        if (up != null) {
                            chattedUser = fillChattedUser(up);
                            initVariables();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }

                    @Override
                    public void onTaskContinue() {

                    }
                }, AccountHolderInfo.getUserID(), chattedUserId, "true", token);

                loadUserDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onTokenFail(String message) {
            }
        });
    }*/

    /*public User fillChattedUser(UserProfile up) {
        User chattedUser = null;

        UserProfile userProfile = (UserProfile) up;
        UserProfileProperties userProfileProperties = userProfile.getUserInfo();

        chattedUser = new User();
        chattedUser.setEmail(userProfileProperties.getEmail());
        chattedUser.setProfilePhotoUrl(userProfileProperties.getProfilePhotoUrl());
        chattedUser.setUserid(userProfileProperties.getUserid());
        chattedUser.setName(userProfileProperties.getName());
        chattedUser.setUsername(userProfileProperties.getUsername());
        chattedUser.setProvider(userProfileProperties.getProvider());
        chattedUser.setIsPrivateAccount(userProfileProperties.getIsPrivateAccount());
        return chattedUser;
    }*/

    public void initVariables() {
        messageBoxList = new ArrayList<>();
        sendMessageBtn.setEnabled(false);
        limitValue = MESSAGE_LIMIT_COUNT;
        setChattedPersonInfo();
        setMessageMenu();
        checkUserBlocked();
        setShapes();
        addListeners();
        getOtherUserDeviceToken();
        getOtherUserNotificationCount();
        getMyNotificationInfo();
        getContentId();
        EmojIconActions emojIcon = new EmojIconActions(this, mainLinearLayout, messageEdittext, smileyImgv);
        emojIcon.ShowEmojIcon();
    }

    public void getMyNotificationInfo() {

        MessageGetProcess.getMyNotificationStatus(context, AccountHolderInfo.getUserID(),
                chattedUser.getUserid(), new NotificationStatusCallback() {
                    @Override
                    public void onReturn(String status) {
                        myNotificationStatus = status;
                        notificationUpdateProcess();
                    }
                });
    }

    private void notificationUpdateProcess() {
        if (myNotificationStatus != null && !myNotificationStatus.equals(FB_VALUE_NOTIFICATION_READ)) {
            MessageUpdateProcess.updateNotificationStatus(AccountHolderInfo.getUserID(), chattedUser.getUserid(), FB_VALUE_NOTIFICATION_READ);
        }
    }

    private void getOtherUserNotificationCount() {

        MessageGetProcess.getOtherUserNotificationCount(AccountHolderInfo.getUserID(),
                chattedUser.getUserid(),
                new GetNotificationCountCallback() {
                    @Override
                    public void onReadCount(int count) {
                        notificationReadCount = count;
                    }

                    @Override
                    public void onSendCount(int count) {
                        notificationSendCount = count;
                    }

                    @Override
                    public void onDeleteCount(int count) {
                        notificationDeleteCount = count;
                    }

                    @Override
                    public void onNotifStatus(String status) {
                        otherUserNotificationStatus = status;
                    }

                    @Override
                    public void onClusterNotifStatus(String status) {
                        clusterNotificationStatus = status;
                    }

                    @Override
                    public void onFailed(String errMessage) {

                    }
                });
    }

    public void setShapes() {
        smileyImgv.setColorFilter(this.getResources().getColor(R.color.Gray, null), PorterDuff.Mode.SRC_IN);
        edittextRelLayout.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.Gray, null), GradientDrawable.RECTANGLE, 50, 2));
        sendMessageBtn.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.DodgerBlue, null),
                0, GradientDrawable.RECTANGLE, 25, 0));
        waitingMsgImgv.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.DeepSkyBlue, null),
                0, GradientDrawable.OVAL, 50, 0));
        moreSettingsImgv.setColorFilter(this.getResources().getColor(R.color.White, null), PorterDuff.Mode.SRC_IN);
    }

    private void initUIValues() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        messageEdittext = findViewById(R.id.messageEdittext);
        sendMessageBtn = findViewById(R.id.sendMessageBtn);
        commonToolbarbackImgv = findViewById(R.id.commonToolbarbackImgv);
        profilePicImgView = findViewById(R.id.profilePicImgView);
        shortUserNameTv = findViewById(R.id.shortUserNameTv);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarSubTitle = findViewById(R.id.toolbarSubTitle);
        moreSettingsImgv = findViewById(R.id.moreSettingsImgv);
        llBlock = findViewById(R.id.llBlock);
        blockTv = findViewById(R.id.blockTv);
        adView = findViewById(R.id.adView);

        relLayout1 = findViewById(R.id.relLayout1);
        relLayout2 = findViewById(R.id.relLayout2);
        commonToolbarbackImgv2 = findViewById(R.id.commonToolbarbackImgv2);
        deleteMsgImgv = findViewById(R.id.deleteMsgImgv);
        deleteMsgCntTv = findViewById(R.id.deleteMsgCntTv);
        edittextRelLayout = findViewById(R.id.edittextRelLayout);
        smileyImgv = findViewById(R.id.smileyImgv);
        messageReachLay = findViewById(R.id.messageReachLay);
        waitingMsgCntTv = findViewById(R.id.waitingMsgCntTv);
        waitingMsgImgv = findViewById(R.id.waitingMsgImgv);
        mainLinearLayout = findViewById(R.id.mainLinearLayout);

        setPopupMenu();
        MobileAds.initialize(MessageWithPersonActivity.this, getResources().getString(R.string.ADMOB_APP_ID));
        AdMobUtils.loadBannerAd(adView);
    }

    private void setChattedPersonInfo() {
        UserDataUtil.setProfilePicture(this, chattedUser.getProfilePhotoUrl(),
                chattedUser.getName(), chattedUser.getUsername(), shortUserNameTv, profilePicImgView);

        if (chattedUser != null && chattedUser.getName() != null && !chattedUser.getName().isEmpty())
            toolbarTitle.setText(chattedUser.getName());
        else
            toolbarTitle.setVisibility(View.GONE);

        if (chattedUser != null && chattedUser.getUsername() != null && !chattedUser.getUsername().isEmpty())
            toolbarSubTitle.setText(CHAR_AMPERSAND + chattedUser.getUsername());
        else
            toolbarSubTitle.setVisibility(View.GONE);
    }

    private void getOtherUserDeviceToken() {
        MessageGetProcess.getOtherUserDeviceToken(context,
                chattedUser, new GetDeviceTokenCallback() {
                    @Override
                    public void onSuccess(TokenInfo tokenInfo) {
                        if (tokenInfo != null) {
                            chattedUserDeviceToken = tokenInfo.getToken();
                            chattedUserSignInValue = tokenInfo.getSigninValue();
                        }
                    }
                });
    }

    private void setPopupMenu() {
        popupMenu = new PopupMenu(context, moreSettingsImgv);
        popupMenu.inflate(R.menu.message_with_person_menu);
        menuOpts = popupMenu.getMenu();
    }

    public void setMessageMenu() {
        moreSettingsImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.blockPerson:

                                if (iblocked) {
                                    MessageBlockProcess.unBlockPerson(AccountHolderInfo.getUserID(), chattedUser.getUserid(), new BlockCompleteCallback() {
                                        @Override
                                        public void OnComplete(boolean value) {
                                            if (value == true) {
                                                iblocked = false;
                                                checkUserBlocked();
                                            }
                                        }
                                    });
                                } else {
                                    MessageBlockProcess.blockPerson(AccountHolderInfo.getUserID(), chattedUser.getUserid(), new BlockCompleteCallback() {
                                        @Override
                                        public void OnComplete(boolean value) {
                                            if (value == true) {
                                                iblocked = true;
                                                checkUserBlocked();
                                            }
                                        }
                                    });
                                }
                                break;
                            case R.id.complainPerson:
                                System.out.println();
                                break;
                            case R.id.clearConversion:
                                System.out.println();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    public void addListeners() {
        profilePicImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (chattedUser != null && chattedUser.getProfilePhotoUrl() != null &&
                        !chattedUser.getProfilePhotoUrl().isEmpty()) {
                    //mNavController.pushFragment(new ShowSelectedPhotoFragment(chattedUser.getProfilePhotoUrl()));
                }
            }
        });

        waitingMsgImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerView != null && messageBoxList != null)
                    recyclerView.smoothScrollToPosition(messageBoxList.size() - 1);
            }
        });

        deleteMsgImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCode = CODE_TOP_LOADED;
                MessageDeleteProcess.deleteSelectedMessages(context, messageBoxList,
                        messageContentId, messageWithPersonAdapter, chattedUser.getUserid(),
                        relLayout1, relLayout2, deleteMsgCntTv);
            }
        });

        commonToolbarbackImgv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCompleted();
            }
        });

        commonToolbarbackImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageWithPersonActivity.this.onBackPressed();
            }
        });

        messageEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.toString().trim().isEmpty()) {
                    sendMessageBtn.setEnabled(true);
                } else
                    sendMessageBtn.setEnabled(false);
            }
        });

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageBtn.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_click));
                sendMessageBtn.setEnabled(false);
                loadCode = CODE_BOTTOM_LOADED;
                MessageAddProcess messageAddProcess = new MessageAddProcess(context,
                        chattedUser, messageContentId, messageEdittext, sendMessageBtn,
                        notificationSendCount, chattedUserDeviceToken, clusterNotificationStatus,
                        otherUserNotificationStatus, chattedUserSignInValue);
                messageAddProcess.addMessage();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                MessageUpdateProcess.updateReceiptIsSeenValue(context,
                        linearLayoutManager.findLastCompletelyVisibleItemPosition(),
                        messageBoxList, messageContentId);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                System.out.println("dy:" + dy);

                pastVisibleItems = linearLayoutManager.findLastVisibleItemPosition();

                notificationUpdateProcess();

                if (dy < 0) {

                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();

                    if (progressLoaded && (visibleItemCount + (totalItemCount - (pastVisibleItems + 1))) >= totalItemCount) {

                        if (messageContentId != null && messageBoxList != null && messageBoxList.size() > 0) {
                            progressLoaded = false;
                            limitValue = limitValue + MESSAGE_LIMIT_COUNT;
                            messageWithPersonAdapter.addProgressLoading();
                            loadCode = CODE_TOP_LOADED;
                            getUsersMessaging();
                        }
                    }
                }

                if (pastVisibleItems == (messageBoxList.size() - 1)) {
                    if (messageReachLay.getVisibility() == View.VISIBLE)
                        messageReachLay.setVisibility(View.GONE);
                    invisibleMsgCnt = 0;
                }
            }

        });
    }

    public void deleteCompleted() {
        relLayout1.setVisibility(View.VISIBLE);
        relLayout2.setVisibility(View.GONE);
        UnmarkAllItemsForNotDelete();
        messageWithPersonAdapter.setDeleteActivated(false);
        deleteMsgCntTv.setText("");
    }

    public void UnmarkAllItemsForNotDelete() {
        if (messageBoxList != null) {
            for (MessageBox messageBox : messageBoxList) {
                messageBox.setSelectedForDelete(false);
            }
        }

        if (messageWithPersonAdapter != null)
            messageWithPersonAdapter.notifyDataSetChanged();
    }

    public void getContentId() {
        loadCode = CODE_BOTTOM_LOADED;

        MessageGetProcess.getContentId(chattedUser, new GetContentIdCallback() {
            @Override
            public void onSuccess(String contentId) {
                messageContentId = contentId;
                getUsersMessaging();
            }

            @Override
            public void onError(String errMessage) {

            }
        });
    }

    private void getUsersMessaging() {

        if (messageContentId == null) return;
        if (messageContentId.isEmpty()) return;

        databaseReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGE_CONTENT)
                .child(messageContentId);

        Query query = databaseReference.orderByChild(FB_CHILD_DATE).limitToLast(limitValue);

        valueEventListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getChildren() != null) {

                    messageBoxListTemp = new ArrayList<>();

                    for (final DataSnapshot outboundSnapshot : dataSnapshot.getChildren()) {

                        if (outboundSnapshot != null &&
                                outboundSnapshot.getKey() != null && outboundSnapshot.getValue() != null) {

                            messageBoxListCheck(outboundSnapshot);
                        }
                    }

                    if (!progressLoaded) {
                        progressLoaded = true;
                        messageWithPersonAdapter.removeProgressLoading();
                    }

                    if (loadCode == CODE_TOP_LOADED && messageBoxListTemp != null &&
                            messageBoxListTemp.size() > 0) {
                        addTempList(messageBoxListTemp);
                    } else
                        adapterLoadCheck();

                    setSmoothScrolling();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void messageBoxListCheck(DataSnapshot mDataSnapshot) {
        boolean notInList = false;
        for (MessageBox messageBox : messageBoxList) {
            if (messageBox != null && messageBox.getMessageId() != null) {
                if (messageBox.getMessageId().equals(mDataSnapshot.getKey())) {
                    notInList = true;
                    break;
                }
            }
        }

        if (!notInList) {
            MessageBox messageBox = getMessageBox(mDataSnapshot);

            if (loadCode == CODE_BOTTOM_LOADED) {
                itemAdded = true;
                lastAddedMessage = messageBox;
                messageBoxList.add(messageBox);
            } else if (loadCode == CODE_TOP_LOADED) {
                messageBoxListTemp.add(messageBox);
            }
        }
    }

    private void adapterLoadCheck() {
        if (!setAdapterVal) {
            adapterLoaded = true;
            setAdapter();
        } else {
            if (messageWithPersonAdapter != null)
                messageWithPersonAdapter.notifyDataSetChanged();
        }
    }

    public void addTempList(List<MessageBox> addedMessageList) {
        if (addedMessageList != null) {
            messageBoxList.addAll(0, messageBoxListTemp);
            messageWithPersonAdapter.notifyItemRangeInserted(0, messageBoxListTemp.size());
        }
    }

    private void setSmoothScrolling() {
        if (loadCode == CODE_BOTTOM_LOADED) {
            if (adapterLoaded) {
                if (messageBoxList != null && messageBoxList.size() > 0) {
                    recyclerView.smoothScrollToPosition(messageBoxList.size() - 1);
                    MessageUpdateProcess.updateReceiptIsSeenValue(context, (messageBoxList.size() - 1), messageBoxList, messageContentId);
                }
                adapterLoaded = false;

            } else if (itemAdded) {
                if (lastAddedMessage != null && messageBoxList != null && messageBoxList.size() > 0) {

                    if (lastAddedMessage.getSenderUser() != null && lastAddedMessage.getSenderUser().getUserid() != null &&
                            lastAddedMessage.getSenderUser().getUserid().equals(AccountHolderInfo.getUserID())) {

                        recyclerView.smoothScrollToPosition(messageBoxList.size() - 1);

                    } else if (lastAddedMessage.getReceiptUser() != null && lastAddedMessage.getReceiptUser().getUserid() != null &&
                            lastAddedMessage.getReceiptUser().getUserid().equals(AccountHolderInfo.getUserID())) {

                        if (linearLayoutManager.findLastVisibleItemPosition() + 5 >= messageBoxList.size()) {
                            loadCode = CODE_BOTTOM_LOADED;
                            recyclerView.smoothScrollToPosition(messageBoxList.size() - 1);
                        } else {
                            invisibleMsgCnt = invisibleMsgCnt + 1;
                            waitingMsgCntTv.setText(Integer.toString(invisibleMsgCnt));
                            if (messageReachLay.getVisibility() == View.GONE)
                                messageReachLay.setVisibility(View.VISIBLE);
                        }
                    }
                    itemAdded = false;
                }
            }
        }
    }


    public MessageBox getMessageBox(DataSnapshot outboundSnapshot) {
        MessageBox messageBox = new MessageBox();

        messageBox.setMessageId(outboundSnapshot.getKey());
        Map<String, Object> map = (Map) outboundSnapshot.getValue();

        messageBox.setDate((long) map.get(FB_CHILD_DATE));
        messageBox.setMessageText((String) map.get(FB_CHILD_MESSAGE));

        Map<String, Object> senderMap = (Map) map.get(FB_CHILD_SENDER);

        User senderUser = new User();
        senderUser.setUserid((String) senderMap.get(FB_CHILD_USERID));
        senderUser.setName((String) senderMap.get(FB_CHILD_NAME));
        messageBox.setSenderUser(senderUser);

        Map<String, Object> receiptMap = (Map) map.get(FB_CHILD_RECEIPT);

        User receiptUser = new User();
        receiptUser.setUserid((String) receiptMap.get(FB_CHILD_USERID));
        receiptUser.setName((String) receiptMap.get(FB_CHILD_NAME));
        messageBox.setReceiptIsSeen((boolean) receiptMap.get(FB_CHILD_IS_SEEN));
        messageBox.setReceiptUser(receiptUser);

        return messageBox;
    }

    public void setAdapter() {
        messageWithPersonAdapter = new MessageWithPersonAdapter(context, messageBoxList, new MessageDeleteCallback() {
            @Override
            public void OnDeleteActivated(boolean activated) {
                if (activated) {
                    relLayout1.setVisibility(View.GONE);
                    relLayout2.setVisibility(View.VISIBLE);
                } else {
                    relLayout1.setVisibility(View.VISIBLE);
                    relLayout2.setVisibility(View.GONE);
                }
            }
        }, deleteMsgCntTv);

        recyclerView.setAdapter(messageWithPersonAdapter);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        setAdapterVal = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MessageGetProcess.removeAllListeners();
        thisActivity = null;

        if (valueEventListener != null && databaseReference != null)
            databaseReference.removeEventListener(valueEventListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (NextActivity.thisActivity == null)
            this.startActivity(new Intent(context, NextActivity.class));

        this.finish();
    }
}
