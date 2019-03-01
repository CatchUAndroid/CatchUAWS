package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Adapters.MessageWithPersonAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.MessageDeleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.MessageBox;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.User;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;

import static com.uren.catchu.Constants.NumericConstants.MESSAGE_LIMIT_COUNT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.CHAR_AMPERSAND;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_CONTENT_ID;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_DATE;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_DEVICE_TOKEN;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_IS_SEEN;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_LAST_MESSAGE_DATE;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGE;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGES;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGE_CONTENT;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_NAME;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_RECEIPT;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_SENDER;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_TOKEN;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_USERID;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_WITH_PERSON;

@SuppressLint("ValidFragment")
public class MessageWithPersonFragment extends BaseFragment {

    View mView;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.messageEdittext)
    hani.momanii.supernova_emoji_library.Helper.EmojiconEditText messageEdittext;
    @BindView(R.id.sendMessageBtn)
    Button sendMessageBtn;
    /* @BindView(R.id.dateLayout)
     RelativeLayout dateLayout;*/
    /*@BindView(R.id.dateValueTv)
    TextView dateValueTv;*/
    @BindView(R.id.commonToolbarbackImgv)
    ImageView commonToolbarbackImgv;
    @BindView(R.id.profilePicImgView)
    ImageView profilePicImgView;
    @BindView(R.id.shortUserNameTv)
    TextView shortUserNameTv;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.toolbarSubTitle)
    TextView toolbarSubTitle;
    @BindView(R.id.moreSettingsImgv)
    ImageView moreSettingsImgv;

    @BindView(R.id.relLayout1)
    RelativeLayout relLayout1;
    @BindView(R.id.relLayout2)
    RelativeLayout relLayout2;
    @BindView(R.id.commonToolbarbackImgv2)
    ImageView commonToolbarbackImgv2;
    @BindView(R.id.deleteMsgImgv)
    ImageView deleteMsgImgv;
    @BindView(R.id.deleteMsgCntTv)
    TextView deleteMsgCntTv;
    @BindView(R.id.edittextRelLayout)
    RelativeLayout edittextRelLayout;
    @BindView(R.id.smileyImgv)
    ImageView smileyImgv;
    @BindView(R.id.mainLinearLayout)
    View mainLinearLayout;
    @BindView(R.id.messageReachLay)
    RelativeLayout messageReachLay;
    @BindView(R.id.waitingMsgCntTv)
    TextView waitingMsgCntTv;
    @BindView(R.id.waitingMsgImgv)
    ImageView waitingMsgImgv;

    User chattedUser;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference1;
    DatabaseReference databaseReference2;
    DatabaseReference databaseReference3;
    ValueEventListener valueEventListener;
    ValueEventListener valueEventListener3;

    DatabaseReference tokenReference;
    ValueEventListener tokenListener;

    ArrayList<MessageBox> messageBoxList;
    ArrayList<MessageBox> messageBoxListTemp;
    MessageWithPersonAdapter messageWithPersonAdapter;
    LinearLayoutManager linearLayoutManager;
    MessageBox lastAddedMessage;

    String messageContentId = null;
    long lastChattedTime;
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

    public MessageWithPersonFragment(User chattedUser) {
        this.chattedUser = chattedUser;
    }

    @Override
    public void onStart() {
        getActivity().findViewById(R.id.tabMainLayout).setVisibility(View.GONE);
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_message_with_person, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            addListeners();
            getOtherUserDeviceToken();
            getContentId();
            EmojIconActions emojIcon = new EmojIconActions(getContext(), mainLinearLayout, messageEdittext, smileyImgv);
            emojIcon.ShowEmojIcon();
        }
        //emojIcon.setUseSystemEmoji(true);
        //messageEdittext.setUseSystemDefault(true);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_RIGHT_TO_LEFT;
    }

    public void initVariables() {
        messageBoxList = new ArrayList<>();
        sendMessageBtn.setEnabled(false);
        limitValue = MESSAGE_LIMIT_COUNT;
            /*dateLayout.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.DodgerBlue, null),
                    0, GradientDrawable.RECTANGLE, 15, 0));*/
        setChattedPersonInfo();
        setMessageMenu();
        smileyImgv.setColorFilter(getContext().getResources().getColor(R.color.Gray, null), PorterDuff.Mode.SRC_IN);
        edittextRelLayout.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.Gray, null), GradientDrawable.RECTANGLE, 50, 2));
        sendMessageBtn.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.DodgerBlue, null),
                0, GradientDrawable.RECTANGLE, 25, 0));

        waitingMsgImgv.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.DeepSkyBlue, null),
                0, GradientDrawable.OVAL, 50, 0));

        moreSettingsImgv.setColorFilter(getContext().getResources().getColor(R.color.White, null), PorterDuff.Mode.SRC_IN);
    }

    private void setChattedPersonInfo() {
        UserDataUtil.setProfilePicture(getContext(), chattedUser.getProfilePhotoUrl(),
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

            }
        });
    }


    public void setMessageMenu() {
        moreSettingsImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(), moreSettingsImgv);
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
            }
        });
    }

    public void addListeners() {

        profilePicImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (chattedUser != null && chattedUser.getProfilePhotoUrl() != null &&
                        !chattedUser.getProfilePhotoUrl().isEmpty()) {
                    mFragmentNavigation.pushFragment(new ShowSelectedPhotoFragment(chattedUser.getProfilePhotoUrl()));
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
                getActivity().onBackPressed();
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
                sendMessageBtn.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
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

                System.out.println("dy:" + dy);

                pastVisibleItems = linearLayoutManager.findLastVisibleItemPosition();

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

    private void updateReceiptIsSeenValue(int firstVisibleItemPosition) {

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
                            }
                        });
                    }
                }
            }
        }
    }

    /*private void setDateValue(int position) {
            MessageBox messageBox = messageBoxList.get(position);
            dateValueTv.setText(CommonUtils.getMessageTime(getContext(), messageBox.getDate()));
    }*/

    public void deleteCompleted() {
        relLayout1.setVisibility(View.VISIBLE);
        relLayout2.setVisibility(View.GONE);
        UnmarkAllItemsForNotDelete();
        messageWithPersonAdapter.setDeleteActivated(false);
        deleteMsgCntTv.setText("");
    }

    public void UnmarkAllItemsForNotDelete() {
        for (MessageBox messageBox : messageBoxList) {
            messageBox.setSelectedForDelete(false);
        }
        messageWithPersonAdapter.notifyDataSetChanged();
    }

    public void getContentId() {
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
                        lastChattedTime = (long) map.get(FB_CHILD_LAST_MESSAGE_DATE);
                        getUsersMessaging();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUsersMessaging() {

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

    public void addMessage() {
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

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void saveMessageContent(long messageTime) {
        databaseReference2 = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGE_CONTENT)
                .child(messageContentId);

        String messageId = databaseReference2.push().getKey();
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
                sendMessageToCloudFunction();
                sendMessageBtn.setEnabled(true);
                messageEdittext.setText("");
            }
        });
    }

    private void sendMessageToCloudFunction() {

        String body;
        String title;

        if (chattedUser != null) {
            if (chattedUser.getName() != null && !chattedUser.getName().isEmpty())
                title = chattedUser.getName();
            else if (chattedUser.getUsername() != null && !chattedUser.getUsername().isEmpty())
                title = CHAR_AMPERSAND + chattedUser.getUsername();
            else return;

            if (chattedUser.getUserid() == null || chattedUser.getUserid().isEmpty())
                return;
        } else return;

        if (messageEdittext != null && messageEdittext.getText() != null &&
                !messageEdittext.getText().toString().isEmpty())
            body = messageEdittext.getText().toString();
        else
            return;

        /*SendMessageToFCM.sendMessage(getContext(),
                chattedUserDeviceToken,
                title,
                body,
                chattedUser.getProfilePhotoUrl(),
                chattedUser.getUserid());*/
    }

    public void setAdapter() {
        messageWithPersonAdapter = new MessageWithPersonAdapter(getContext(), messageBoxList, new MessageDeleteCallback() {
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
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        setAdapterVal = true;
    }

    public void deleteSelectedMessages() {
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

                    }
                });
            }
        }
    }

    public void completeMessageDeletion(MessageBox messageBox) {
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
    }

    private void deleteMessageContent() {

        databaseReference2 = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES).child(FB_CHILD_WITH_PERSON)
                .child(AccountHolderInfo.getUserID()).child(chattedUser.getUserid()).child(FB_CHILD_MESSAGE_CONTENT);

        databaseReference3 = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES).child(FB_CHILD_WITH_PERSON)
                .child(chattedUser.getUserid()).child(AccountHolderInfo.getUserID()).child(FB_CHILD_MESSAGE_CONTENT);

        databaseReference2.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        databaseReference3.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (valueEventListener != null && databaseReference != null)
            databaseReference.removeEventListener(valueEventListener);

        if (valueEventListener3 != null && databaseReference3 != null)
            databaseReference3.removeEventListener(valueEventListener3);
    }
}

