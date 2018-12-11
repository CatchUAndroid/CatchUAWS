package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.Interfaces.RecyclerViewAdapterCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Adapters.UserGroupsListAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.ViewGroupDetailFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Adapters.MessageWithPersonAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.MessageDeleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.MessageBox;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.ModelViews.PaintView;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.User;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.CHAR_AMPERSAND;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_DATE;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGE;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGES;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_NAME;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_RECEIPT;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_SENDER;
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
    EditText messageEdittext;
    @BindView(R.id.sendMessageBtn)
    Button sendMessageBtn;
    @BindView(R.id.dateLayout)
    RelativeLayout dateLayout;
    @BindView(R.id.dateValueTv)
    TextView dateValueTv;
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

    User chattedUser;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    ArrayList<MessageBox> messageBoxList;
    MessageWithPersonAdapter messageWithPersonAdapter;
    LinearLayoutManager linearLayoutManager;
    boolean setAdapterVal = false;

    boolean itemAdded = false;
    boolean adapterLoaded = false;
    MessageBox lastAddedMessage;

    public MessageWithPersonFragment(User chattedUser) {
        this.chattedUser = chattedUser;
    }

    @Override
    public void onStart() {
        NextActivity.bottomTabLayout.setVisibility(View.GONE);
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
        mView = inflater.inflate(R.layout.fragment_message_with_person, container, false);
        ButterKnife.bind(this, mView);
        initVariables();
        addListeners();
        getUsersMessaging();
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
        dateLayout.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.DodgerBlue, null),
                0, GradientDrawable.RECTANGLE, 15, 0));
        setChattedPersonInfo();
    }

    private void setChattedPersonInfo() {
        UserDataUtil.setProfilePicture(getContext(), chattedUser.getProfilePhotoUrl(),
                chattedUser.getName(), chattedUser.getUsername(), shortUserNameTv, profilePicImgView);

        if (chattedUser != null && chattedUser.getName() != null && !chattedUser.getName().isEmpty())
            toolbarTitle.setText(chattedUser.getName());

        if (chattedUser != null && chattedUser.getUsername() != null && !chattedUser.getUsername().isEmpty())
            toolbarSubTitle.setText(CHAR_AMPERSAND + chattedUser.getUsername());
    }

    public void addListeners() {
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
                sendMessageBtn.setEnabled(false);
                addMessage();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        System.out.println("The RecyclerView is not scrolling");
                        dateLayout.setVisibility(View.GONE);
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        System.out.println("Scrolling now");
                        dateLayout.setVisibility(View.VISIBLE);
                        int position = linearLayoutManager.findFirstVisibleItemPosition();
                        MessageBox messageBox = messageBoxList.get(position);

                        Date date = new Date(messageBox.getDate());
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
                        String formatted = format.format(date);

                        String[] monthArray = getContext().getResources().getStringArray(R.array.months);

                        String dateValue = formatted.substring(8, 10) + " " +
                                monthArray[Integer.parseInt(formatted.substring(5, 7)) - 1] +
                                " " + formatted.substring(0, 4);

                        dateValueTv.setText(dateValue);

                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        System.out.println("Scroll Settling");
                        break;

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
        for (MessageBox messageBox : messageBoxList) {
            messageBox.setSelectedForDelete(false);
        }
        messageWithPersonAdapter.notifyDataSetChanged();
    }

    private void getUsersMessaging() {

        progressBar.setVisibility(View.VISIBLE);

        databaseReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES).child(FB_CHILD_WITH_PERSON)
                .child(getMessageKey());

        Query query = databaseReference.orderByChild(FB_CHILD_DATE);

        valueEventListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot outboundSnapshot : dataSnapshot.getChildren()) {
                    if (outboundSnapshot.getKey() != null && outboundSnapshot.getValue() != null) {
                        System.out.println("outboundSnapshot.getKey():" + outboundSnapshot.getKey());
                        System.out.println("outboundSnapshot.getValue():" + outboundSnapshot.getValue());

                        boolean notInList = false;
                        for (MessageBox messageBox : messageBoxList) {
                            if (messageBox != null && messageBox.getMessageId() != null) {
                                if (messageBox.getMessageId().equals(outboundSnapshot.getKey())) {
                                    notInList = true;
                                    break;
                                }
                            }
                        }

                        if (!notInList) {
                            itemAdded = true;
                            fillMessageBoxList(outboundSnapshot);
                        }
                    }
                }

                if (!setAdapterVal) {
                    adapterLoaded = true;
                    setAdapter();
                } else {
                    if (messageWithPersonAdapter != null)
                        messageWithPersonAdapter.notifyDataSetChanged();
                }

                progressBar.setVisibility(View.GONE);

                if (adapterLoaded) {
                    if (messageBoxList != null && messageBoxList.size() > 0)
                        recyclerView.smoothScrollToPosition(messageBoxList.size() - 1);
                    adapterLoaded = false;
                } else if (itemAdded) {
                    if (lastAddedMessage != null && lastAddedMessage.getSenderUser() != null &&
                            lastAddedMessage.getSenderUser().getUserid() != null) {
                        if (lastAddedMessage.getSenderUser().getUserid().equals(AccountHolderInfo.getUserID())) {
                            if (messageBoxList != null && messageBoxList.size() > 0)
                                recyclerView.smoothScrollToPosition(messageBoxList.size() - 1);
                            itemAdded = false;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public String getMessageKey() {
        String messageKey = null;
        int compareResult = chattedUser.getUserid().compareTo(AccountHolderInfo.getUserID());

        if (compareResult < 0)
            messageKey = chattedUser.getUserid().trim() + "-" + AccountHolderInfo.getUserID().trim();
        else if (compareResult > 0)
            messageKey = AccountHolderInfo.getUserID().trim() + "-" + chattedUser.getUserid().trim();

        System.out.println("messageKey:" + messageKey);
        return messageKey;
    }

    public void fillMessageBoxList(DataSnapshot outboundSnapshot) {
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
        messageBox.setReceiptUser(receiptUser);

        lastAddedMessage = messageBox;

        messageBoxList.add(messageBox);
    }

    public void addMessage() {
        databaseReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES).child(FB_CHILD_WITH_PERSON)
                .child(getMessageKey());

        String messageId = databaseReference.push().getKey();

        Map<String, Object> values = new HashMap<>();

        values.put(FB_CHILD_DATE, System.currentTimeMillis());
        values.put(FB_CHILD_MESSAGE, messageEdittext.getText().toString());

        Map<String, String> sender = new HashMap<>();
        sender.put(FB_CHILD_NAME, AccountHolderInfo.getInstance().getUser().getUserInfo().getName());
        sender.put(FB_CHILD_USERID, AccountHolderInfo.getUserID());

        Map<String, String> receipt = new HashMap<>();
        receipt.put(FB_CHILD_NAME, chattedUser.getName());
        receipt.put(FB_CHILD_USERID, chattedUser.getUserid());

        values.put(FB_CHILD_SENDER, sender);
        values.put(FB_CHILD_RECEIPT, receipt);

        databaseReference.child(messageId).setValue(values, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                sendMessageBtn.setEnabled(true);
                messageEdittext.setText("");
            }
        });
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
                databaseReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES).child(FB_CHILD_WITH_PERSON)
                        .child(getMessageKey()).child(messageBox.getMessageId());
                databaseReference.removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        messageBoxList.remove(messageBox);
                        messageWithPersonAdapter.notifyDataSetChanged();

                        boolean checkVal = false;
                        for (MessageBox messageBox1 : messageBoxList) {
                            if (messageBox1.isSelectedForDelete()) {
                                checkVal = true;
                                break;
                            }
                        }

                        if (!checkVal) {
                            relLayout1.setVisibility(View.VISIBLE);
                            relLayout2.setVisibility(View.GONE);
                            messageWithPersonAdapter.setDeleteActivated(false);
                            deleteMsgCntTv.setText("");
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(valueEventListener);
    }

}
