package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.LoginPackage.Models.LoginUser;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Adapters.MessageWithPersonAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.GetNotificationCountCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.MessageDeleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.MessageSentFCMCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.MessageUpdateCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.NotificationStatusCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.FCMItems;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.MessageBox;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.Interfaces.AccountHolderInfoCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import catchu.model.User;
import catchu.model.UserProfile;
import catchu.model.UserProfileProperties;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import io.fabric.sdk.android.Fabric;

import static com.uren.catchu.Constants.NumericConstants.FCM_MAX_MESSAGE_LEN;
import static com.uren.catchu.Constants.NumericConstants.MAX_ALLOWED_NOTIFICATION_SIZE;
import static com.uren.catchu.Constants.NumericConstants.MESSAGE_LIMIT_COUNT;
import static com.uren.catchu.Constants.StringConstants.APP_NAME;
import static com.uren.catchu.Constants.StringConstants.CHAR_AMPERSAND;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_CLUSTER_STATUS;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_CONTENT_ID;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_DATE;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_DEVICE_TOKEN;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_IS_SEEN;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_LAST_MESSAGE_DATE;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGE;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGES;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGE_CONTENT;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_NAME;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_NOTIFICATIONS;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_NOTIFICATION_STATUS;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_PAGE_IS_SEEN;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_RECEIPT;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_SENDER;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_TOKEN;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_USERID;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_WITH_PERSON;
import static com.uren.catchu.Constants.StringConstants.FB_VALUE_NOTIFICATION_READ;
import static com.uren.catchu.Constants.StringConstants.FB_VALUE_NOTIFICATION_SEND;
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

    User chattedUser = new User();
    DatabaseReference databaseReference;
    DatabaseReference databaseReference1;
    DatabaseReference databaseReference2;
    DatabaseReference databaseReference3;
    ValueEventListener valueEventListener;
    ValueEventListener valueEventListener3;

    DatabaseReference tokenReference;
    ValueEventListener tokenListener;

    DatabaseReference notificationReference;
    ValueEventListener notificationListener;

    ArrayList<MessageBox> messageBoxList;
    ArrayList<MessageBox> messageBoxListTemp;
    MessageWithPersonAdapter messageWithPersonAdapter;
    LinearLayoutManager linearLayoutManager;
    MessageBox lastAddedMessage;

    public static Activity thisActivity;

    String messageContentId = null;
    //long lastChattedTime;
    String chattedUserDeviceToken = null;

    boolean setAdapterVal = false;
    boolean itemAdded = false;
    boolean adapterLoaded = false;
    boolean progressLoaded = true;

    int limitValue;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    int loadCode;
    int invisibleMsgCnt = 0;

    private static final int CODE_BOTTOM_LOADED = 0;
    private static final int CODE_TOP_LOADED = 1;

    LoginUser loginUser;
    String senderUserId;  // Mesaji daha once gondermis kisi(ben degilim)
    String receiptUserId; // Bu benim

    int notificationReadCount = 0, notificationDeleteCount = 0, notificationSendCount = 0;
    String myNotificationStatus = null;
    String clusterNotificationStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message_with_person);
        thisActivity = this;
        Fabric.with(this, new Crashlytics());
        initUIValues();

        try {
            loginUser = (LoginUser) getIntent().getSerializableExtra(FCM_CODE_CHATTED_USER);
            senderUserId = (String) getIntent().getSerializableExtra(FCM_CODE_SENDER_USERID);
            receiptUserId = (String) getIntent().getSerializableExtra(FCM_CODE_RECEIPT_USERID);
            checkMyInformation();

        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void checkMyInformation() {
        try {
            if (AccountHolderInfo.getInstance() != null && AccountHolderInfo.getUserID() != null && !AccountHolderInfo.getUserID().isEmpty())
                checkSenderInformation();
            else if (receiptUserId != null && !receiptUserId.isEmpty()) {
                AccountHolderInfo.getInstance();
                AccountHolderInfo.setAccountHolderInfoCallback(new AccountHolderInfoCallback() {
                    @Override
                    public void onAccountHolderIfoTaken(UserProfile userProfile) {
                        if (receiptUserId.equals(userProfile.getUserInfo().getUserid()))
                            checkSenderInformation();
                    }
                });
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void checkSenderInformation() {
        try {
            if (loginUser != null) {
                setChattedUserInfo(loginUser);
                initVariables();
            } else if (senderUserId != null && !senderUserId.isEmpty()) {
                getChattedUserDetail(senderUserId);
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void setChattedUserInfo(LoginUser loginUser) {
        try {
            chattedUser.setUserid(loginUser.getUserId());
            chattedUser.setUsername(loginUser.getUsername());
            chattedUser.setName(loginUser.getName());
            chattedUser.setProfilePhotoUrl(loginUser.getProfilePhotoUrl());
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void getChattedUserDetail(final String chattedUserId) {
        try {
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
                            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                                    new Object() {
                                    }.getClass().getEnclosingMethod().getName(), e.toString());
                        }

                        @Override
                        public void onTaskContinue() {

                        }
                    }, AccountHolderInfo.getUserID(), chattedUserId, token);

                    loadUserDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public User fillChattedUser(UserProfile up) {
        User chattedUser = null;
        try {
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
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return chattedUser;
    }

    public void initVariables() {
        try {
            messageBoxList = new ArrayList<>();
            sendMessageBtn.setEnabled(false);
            limitValue = MESSAGE_LIMIT_COUNT;
            setChattedPersonInfo();
            setMessageMenu();
            setShapes();
            addListeners();
            getOtherUserDeviceToken();
            getOtherUserNotificationCount();
            getMyNotificationInfo();
            getContentId();
            notificationUpdateProcess();
            //updatePageSeenValue(true);
            EmojIconActions emojIcon = new EmojIconActions(this, mainLinearLayout, messageEdittext, smileyImgv);
            emojIcon.ShowEmojIcon();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void notificationUpdateProcess() {
        try {
            if (myNotificationStatus != null && !myNotificationStatus.equals(FB_VALUE_NOTIFICATION_READ)) {
                MessagingPersonProcess.updateNotificationStatus(AccountHolderInfo.getUserID(), chattedUser.getUserid(), FB_VALUE_NOTIFICATION_READ);
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void getMyNotificationInfo() {

        MessagingPersonProcess.getMyNotificationStatus(MessageWithPersonActivity.this, AccountHolderInfo.getUserID(),
                chattedUser.getUserid(), new NotificationStatusCallback() {
                    @Override
                    public void onReturn(String status) {
                        myNotificationStatus = status;
                    }
                });
    }

    private void getOtherUserNotificationCount() {

        MessagingPersonProcess.getOtherUserNotificationCount(MessageWithPersonActivity.this, AccountHolderInfo.getUserID(),
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

                    }

                    @Override
                    public void onClusterNotifStatus(String status) {
                        clusterNotificationStatus = status;
                    }

                    @Override
                    public void onFailed(String errMessage) {
                        ErrorSaveHelper.writeErrorToDB(null, this.getClass().getSimpleName(),
                                new Object() {
                                }.getClass().getEnclosingMethod().getName(), errMessage);
                    }
                });

        /*notificationReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_NOTIFICATIONS)
                .child(chattedUser.getUserid());

        Query query = notificationReference.child(FB_CHILD_NOTIFICATION_STATUS).equalTo(FB_VALUE_NOTIFICATION_READ);

        notificationListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/


    }

    /*private void updatePageSeenValue(boolean seenValue) {
        databaseReference1 = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES)
                .child(FB_CHILD_WITH_PERSON)
                .child(AccountHolderInfo.getUserID())
                .child(chattedUser.getUserid())
                .child(FB_CHILD_MESSAGE_CONTENT);

        Map<String, Object> values = new HashMap<>();
        values.put(FB_CHILD_PAGE_IS_SEEN, seenValue);

        databaseReference1.updateChildren(values).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                System.out.println();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println();
            }
        });
    }*/

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
    }

    private void setChattedPersonInfo() {
        try {
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
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void getOtherUserDeviceToken() {

        try {
            tokenReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_DEVICE_TOKEN)
                    .child(chattedUser.getUserid()).child(FB_CHILD_TOKEN);

            tokenListener = tokenReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null)
                        chattedUserDeviceToken = (String) dataSnapshot.getValue();

                    tokenReference.removeEventListener(tokenListener);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), databaseError.toString());
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }


    public void setMessageMenu() {
        moreSettingsImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PopupMenu popupMenu = new PopupMenu(MessageWithPersonActivity.this, moreSettingsImgv);
                    popupMenu.inflate(R.menu.message_with_person_menu);

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.blockPerson:
                                    System.out.println();
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
                } catch (Exception e) {
                    ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), e.toString());
                    e.printStackTrace();
                }
            }
        });
    }

    public void addListeners() {
        try {

            profilePicImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (chattedUser != null && chattedUser.getProfilePhotoUrl() != null &&
                            !chattedUser.getProfilePhotoUrl().isEmpty()) {
                        //mFragmentNavigation.pushFragment(new ShowSelectedPhotoFragment(chattedUser.getProfilePhotoUrl()));
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
                    deleteSelectedMessages();
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
                    if (s != null && !s.toString().isEmpty()) {
                        sendMessageBtn.setEnabled(true);
                    } else
                        sendMessageBtn.setEnabled(false);
                }
            });

            sendMessageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessageBtn.startAnimation(AnimationUtils.loadAnimation(MessageWithPersonActivity.this, R.anim.image_click));
                    sendMessageBtn.setEnabled(false);
                    addMessage();
                }
            });

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    updateReceiptIsSeenValue(linearLayoutManager.findLastCompletelyVisibleItemPosition());

                    switch (newState) {
                        case RecyclerView.SCROLL_STATE_IDLE:
                            //dateLayout.setVisibility(View.GONE);
                            break;
                        case RecyclerView.SCROLL_STATE_DRAGGING:
                            //dateLayout.setVisibility(View.VISIBLE);
                            //setDateValue(linearLayoutManager.findFirstVisibleItemPosition());
                            //updateReceiptIsSeenValue(linearLayoutManager.findFirstVisibleItemPosition());
                            //updateReceiptIsSeenValue(linearLayoutManager.findLastVisibleItemPosition());
                            //updateReceiptIsSeenValue(linearLayoutManager.findFirstCompletelyVisibleItemPosition());
                            //updateReceiptIsSeenValue(linearLayoutManager.findLastCompletelyVisibleItemPosition());
                            break;
                        case RecyclerView.SCROLL_STATE_SETTLING:
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    try {
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
                    } catch (Exception e) {
                        ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                                new Object() {
                                }.getClass().getEnclosingMethod().getName(), e.toString());
                        e.printStackTrace();
                    }
                }

            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void updateReceiptIsSeenValue(int firstVisibleItemPosition) {

        try {
            for (int index = firstVisibleItemPosition; index >= 0; index--) {
                final MessageBox messageBox = messageBoxList.get(index);

                if (messageBox != null && messageBox.isReceiptIsSeen() == false && messageContentId != null) {

                    if (messageBox.getReceiptUser() != null && messageBox.getReceiptUser().getUserid() != null &&
                            !messageBox.getReceiptUser().getUserid().isEmpty()) {

                        if (messageBox.getReceiptUser().getUserid().equals(AccountHolderInfo.getUserID())) {
                            databaseReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGE_CONTENT)
                                    .child(messageContentId)
                                    .child(messageBox.getMessageId())
                                    .child(FB_CHILD_RECEIPT);

                            final Map<String, Object> values = new HashMap<>();
                            values.put(FB_CHILD_IS_SEEN, true);

                            databaseReference.updateChildren(values).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    messageBox.setReceiptIsSeen(true);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                                            new Object() {
                                            }.getClass().getEnclosingMethod().getName(), e.toString());
                                }
                            });
                        }
                    }
                }
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    /*private void setDateValue(int position) {
        try {
            MessageBox messageBox = messageBoxList.get(position);
            dateValueTv.setText(CommonUtils.getMessageTime(getContext(), messageBox.getDate()));
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }*/

    public void deleteCompleted() {
        try {
            relLayout1.setVisibility(View.VISIBLE);
            relLayout2.setVisibility(View.GONE);
            UnmarkAllItemsForNotDelete();
            messageWithPersonAdapter.setDeleteActivated(false);
            deleteMsgCntTv.setText("");
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void UnmarkAllItemsForNotDelete() {
        try {
            for (MessageBox messageBox : messageBoxList) {
                messageBox.setSelectedForDelete(false);
            }
            messageWithPersonAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void getContentId() {
        try {
            loadCode = CODE_BOTTOM_LOADED;
            progressBar.setVisibility(View.VISIBLE);

            databaseReference3 = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES).child(FB_CHILD_WITH_PERSON)
                    .child(AccountHolderInfo.getUserID()).child(chattedUser.getUserid()).child(FB_CHILD_MESSAGE_CONTENT);

            valueEventListener3 = databaseReference3.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.getChildren() != null) {
                        Map<String, Object> map = (Map) dataSnapshot.getValue();

                        if (map != null) {
                            messageContentId = (String) map.get(FB_CHILD_CONTENT_ID);
                            //lastChattedTime = (long) map.get(FB_CHILD_LAST_MESSAGE_DATE);
                            getUsersMessaging();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void getUsersMessaging() {

        try {
            if (messageContentId == null) return;
            if (messageContentId.isEmpty()) return;

            progressBar.setVisibility(View.VISIBLE);

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
                        loadCode = CODE_BOTTOM_LOADED;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), databaseError.toString());
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void messageBoxListCheck(DataSnapshot mDataSnapshot) {
        try {
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
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
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
                if (messageWithPersonAdapter != null)
                    messageWithPersonAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void addTempList(List<MessageBox> addedMessageList) {
        try {
            if (addedMessageList != null) {
                messageBoxList.addAll(0, messageBoxListTemp);
                messageWithPersonAdapter.notifyItemRangeInserted(0, messageBoxListTemp.size());
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void setSmoothScrolling() {
        try {
            if (loadCode == CODE_BOTTOM_LOADED) {
                if (adapterLoaded) {
                    if (messageBoxList != null && messageBoxList.size() > 0)
                        recyclerView.smoothScrollToPosition(messageBoxList.size() - 1);
                    adapterLoaded = false;
                } else if (itemAdded) {
                    if (lastAddedMessage != null && messageBoxList != null && messageBoxList.size() > 0) {

                        if (lastAddedMessage.getSenderUser() != null && lastAddedMessage.getSenderUser().getUserid() != null &&
                                lastAddedMessage.getSenderUser().getUserid().equals(AccountHolderInfo.getUserID())) {

                            recyclerView.smoothScrollToPosition(messageBoxList.size() - 1);

                        } else if (lastAddedMessage.getReceiptUser() != null && lastAddedMessage.getReceiptUser().getUserid() != null &&
                                lastAddedMessage.getReceiptUser().getUserid().equals(AccountHolderInfo.getUserID())) {

                            if (linearLayoutManager.findLastVisibleItemPosition() + 3 >= messageBoxList.size()) {
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
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }


    public MessageBox getMessageBox(DataSnapshot outboundSnapshot) {
        MessageBox messageBox = new MessageBox();
        try {
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

        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public void addMessage() {
        try {
            loadCode = CODE_BOTTOM_LOADED;
            databaseReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES).child(FB_CHILD_WITH_PERSON)
                    .child(AccountHolderInfo.getUserID()).child(chattedUser.getUserid()).child(FB_CHILD_MESSAGE_CONTENT);

            if (messageContentId == null)
                messageContentId = databaseReference.push().getKey();

            if (messageContentId == null) return;

            final Map<String, Object> values = new HashMap<>();
            final long messageTime = System.currentTimeMillis();
            values.put(FB_CHILD_CONTENT_ID, messageContentId);
            values.put(FB_CHILD_LAST_MESSAGE_DATE, messageTime);

            databaseReference.updateChildren(values).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    databaseReference1 = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES).child(FB_CHILD_WITH_PERSON)
                            .child(chattedUser.getUserid()).child(AccountHolderInfo.getUserID()).child(FB_CHILD_MESSAGE_CONTENT);

                    databaseReference1.updateChildren(values).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            saveMessageContent(messageTime);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                                    new Object() {
                                    }.getClass().getEnclosingMethod().getName(), e.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), e.toString());
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void saveMessageContent(long messageTime) {
        try {
            databaseReference2 = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGE_CONTENT)
                    .child(messageContentId);

            final String messageId = databaseReference2.push().getKey();
            databaseReference2 = databaseReference2.child(messageId);

            Map<String, Object> values = new HashMap<>();

            values.put(FB_CHILD_DATE, messageTime);
            values.put(FB_CHILD_MESSAGE, messageEdittext.getText().toString());

            Map<String, String> sender = new HashMap<>();
            sender.put(FB_CHILD_NAME, AccountHolderInfo.getInstance().getUser().getUserInfo().getName());
            sender.put(FB_CHILD_USERID, AccountHolderInfo.getUserID());

            Map<String, Object> receipt = new HashMap<>();
            receipt.put(FB_CHILD_NAME, chattedUser.getName());
            receipt.put(FB_CHILD_USERID, chattedUser.getUserid());
            receipt.put(FB_CHILD_IS_SEEN, false);

            values.put(FB_CHILD_SENDER, sender);
            values.put(FB_CHILD_RECEIPT, receipt);

            databaseReference2.setValue(values, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    sendMessageToCloudFunction(messageId);
                    sendMessageBtn.setEnabled(true);
                    messageEdittext.setText("");
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void sendMessageToCloudFunction(String messageId) {

        String body;
        String title;
        try {

            if (notificationSendCount > MAX_ALLOWED_NOTIFICATION_SIZE) {
                sendClusterMessage();
                return;
            }

            UserProfileProperties userProfileProperties =
                    AccountHolderInfo.getInstance().getUser().getUserInfo();

            if (userProfileProperties != null) {
                if (userProfileProperties.getName() != null && !userProfileProperties.getName().isEmpty())
                    title = userProfileProperties.getName();
                else if (userProfileProperties.getUsername() != null && !userProfileProperties.getUsername().isEmpty())
                    title = CHAR_AMPERSAND + userProfileProperties.getUsername();
                else return;

                if (userProfileProperties.getUserid() == null || userProfileProperties.getUserid().isEmpty())
                    return;
            } else return;

            if (messageEdittext != null && messageEdittext.getText() != null &&
                    !messageEdittext.getText().toString().isEmpty()) {

                if (messageEdittext.getText().toString().length() < FCM_MAX_MESSAGE_LEN)
                    body = messageEdittext.getText().toString();
                else
                    body = messageEdittext.getText().toString().substring(0, FCM_MAX_MESSAGE_LEN) + "...";
            } else
                return;

            if (messageId == null || messageId.isEmpty())
                return;

            if (chattedUserDeviceToken == null || chattedUserDeviceToken.isEmpty())
                return;

            System.out.println("userProfileProperties.getProfilePhotoUrl():" +
                    userProfileProperties.getProfilePhotoUrl());

            FCMItems fcmItems = new FCMItems();
            fcmItems.setBody(body);
            fcmItems.setOtherUserDeviceToken(chattedUserDeviceToken);
            fcmItems.setTitle(title);
            fcmItems.setPhotoUrl(userProfileProperties.getProfilePhotoUrl());
            fcmItems.setMessageid(messageId);
            fcmItems.setSenderUserid(userProfileProperties.getUserid());
            fcmItems.setReceiptUserid(chattedUser.getUserid());

            SendMessageToFCM.sendMessage(MessageWithPersonActivity.this,
                    fcmItems,
                    new MessageSentFCMCallback() {
                        @Override
                        public void onSuccess() {
                            MessagingPersonProcess.updateNotificationStatus(chattedUser.getUserid(), AccountHolderInfo.getUserID()
                                    , FB_VALUE_NOTIFICATION_SEND);
                        }

                        @Override
                        public void onFailed(Exception e) {

                        }
                    });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void sendClusterMessage() {
        if (!clusterNotificationStatus.equals(FB_VALUE_NOTIFICATION_SEND)) {
            String body;
            String title;

            UserProfileProperties userProfileProperties =
                    AccountHolderInfo.getInstance().getUser().getUserInfo();

            title = APP_NAME;
            body = this.getResources().getString(R.string.YOU_HAVE_NEW_MESSAGES);

            if (chattedUserDeviceToken == null || chattedUserDeviceToken.isEmpty())
                return;

            FCMItems fcmItems = new FCMItems();
            fcmItems.setBody(body);
            fcmItems.setOtherUserDeviceToken(chattedUserDeviceToken);
            fcmItems.setTitle(title);
            fcmItems.setSenderUserid(userProfileProperties.getUserid());
            fcmItems.setReceiptUserid(chattedUser.getUserid());

            SendClusterMessageToFCM.sendMessage(MessageWithPersonActivity.this,
                    fcmItems,
                    new MessageSentFCMCallback() {
                        @Override
                        public void onSuccess() {
                            MessagingPersonProcess.updateClusterStatus(chattedUser.getUserid(),
                                    FB_VALUE_NOTIFICATION_SEND, new MessageUpdateCallback() {
                                        @Override
                                        public void onComplete() {

                                        }

                                        @Override
                                        public void onFailed(String errMessage) {

                                        }
                                    });
                        }

                        @Override
                        public void onFailed(Exception e) {

                        }
                    });
        }
    }

    public void setAdapter() {
        try {
            messageWithPersonAdapter = new MessageWithPersonAdapter(MessageWithPersonActivity.this, messageBoxList, new MessageDeleteCallback() {
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
            linearLayoutManager = new LinearLayoutManager(MessageWithPersonActivity.this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            setAdapterVal = true;
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void deleteSelectedMessages() {
        try {
            for (final MessageBox messageBox : messageBoxList) {
                if (messageBox.isSelectedForDelete()) {

                    loadCode = CODE_TOP_LOADED;
                    databaseReference2 = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGE_CONTENT)
                            .child(messageContentId).child(messageBox.getMessageId());

                    databaseReference2.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            completeMessageDeletion(messageBox);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                                    new Object() {
                                    }.getClass().getEnclosingMethod().getName(), e.toString());
                        }
                    });
                }
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void completeMessageDeletion(MessageBox messageBox) {
        try {
            int deletedIndex = messageBoxList.indexOf(messageBox);
            messageBoxList.remove(messageBox);
            messageWithPersonAdapter.notifyItemRemoved(deletedIndex);
            messageWithPersonAdapter.notifyItemRangeChanged(deletedIndex, messageBoxList.size());

            boolean checkVal = false;

            if (messageBoxList != null) {
                for (MessageBox messageBox1 : messageBoxList) {
                    if (messageBox1.isSelectedForDelete()) {
                        checkVal = true;
                        break;
                    }
                }

                if (messageBoxList.size() == 0)
                    deleteMessageContent();
            }

            if (!checkVal) {
                relLayout1.setVisibility(View.VISIBLE);
                relLayout2.setVisibility(View.GONE);
                messageWithPersonAdapter.setDeleteActivated(false);
                deleteMsgCntTv.setText("");
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void deleteMessageContent() {

        try {
            databaseReference2 = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES).child(FB_CHILD_WITH_PERSON)
                    .child(AccountHolderInfo.getUserID()).child(chattedUser.getUserid()).child(FB_CHILD_MESSAGE_CONTENT);

            databaseReference1 = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES).child(FB_CHILD_WITH_PERSON)
                    .child(chattedUser.getUserid()).child(AccountHolderInfo.getUserID()).child(FB_CHILD_MESSAGE_CONTENT);

            databaseReference2.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), e.toString());
                }
            });

            databaseReference1.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), e.toString());
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            MessagingPersonProcess.removeAllListeners();

            if (valueEventListener != null && databaseReference != null)
                databaseReference.removeEventListener(valueEventListener);

            if (valueEventListener3 != null && databaseReference3 != null)
                databaseReference3.removeEventListener(valueEventListener3);

        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(MessageWithPersonActivity.this, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (NextActivity.thisActivity == null)
            this.startActivity(new Intent(MessageWithPersonActivity.this, NextActivity.class));

        this.finish();
    }
}
