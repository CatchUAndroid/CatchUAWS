package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
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
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Adapters.MessageWithPersonAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.MessageDeleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.MessageBox;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.User;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.CHAR_AMPERSAND;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_CONTENT_ID;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_DATE;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_IS_SEEN;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_LAST_MESSAGE_DATE;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGE;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGES;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGE_CONTENT;
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

    User chattedUser;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference1;
    DatabaseReference databaseReference2;
    DatabaseReference databaseReference3;
    ValueEventListener valueEventListener;
    ArrayList<MessageBox> messageBoxList;
    MessageWithPersonAdapter messageWithPersonAdapter;
    LinearLayoutManager linearLayoutManager;
    boolean setAdapterVal = false;

    boolean itemAdded = false;
    boolean adapterLoaded = false;
    MessageBox lastAddedMessage;

    String messageContentId = null;
    long lastChattedTime;


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
        try {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            mView = inflater.inflate(R.layout.fragment_message_with_person, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            addListeners();
            getContentId();
            EmojIconActions emojIcon = new EmojIconActions(getContext(), mainLinearLayout, messageEdittext, smileyImgv);
            emojIcon.ShowEmojIcon();
            //emojIcon.setUseSystemEmoji(true);
            //messageEdittext.setUseSystemDefault(true);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_RIGHT_TO_LEFT;
    }

    public void initVariables() {
        try {
            messageBoxList = new ArrayList<>();
            sendMessageBtn.setEnabled(false);
            /*dateLayout.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.DodgerBlue, null),
                    0, GradientDrawable.RECTANGLE, 15, 0));*/
            setChattedPersonInfo();
            smileyImgv.setColorFilter(getContext().getResources().getColor(R.color.Gray, null), PorterDuff.Mode.SRC_IN);
            edittextRelLayout.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                    getResources().getColor(R.color.Gray, null), GradientDrawable.RECTANGLE, 50, 2));
            sendMessageBtn.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.DodgerBlue, null),
                    0, GradientDrawable.RECTANGLE, 25, 0));
        } catch (Resources.NotFoundException e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void setChattedPersonInfo() {
        try {
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
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void addListeners() {
        try {

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

            });
        } catch (Resources.NotFoundException e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
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
                                    ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                                            new Object() {
                                            }.getClass().getEnclosingMethod().getName(), e.toString());
                                }
                            });
                        }
                    }
                }
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
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
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
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
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void getContentId() {
        try {
            progressBar.setVisibility(View.VISIBLE);

            databaseReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES).child(FB_CHILD_WITH_PERSON)
                    .child(AccountHolderInfo.getUserID()).child(chattedUser.getUserid()).child(FB_CHILD_MESSAGE_CONTENT);

            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
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

        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void getUsersMessaging() {

        try {
            progressBar.setVisibility(View.VISIBLE);

            databaseReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGE_CONTENT)
                    .child(messageContentId);

            Query query = databaseReference.orderByChild(FB_CHILD_DATE);

            valueEventListener = query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.getChildren() != null) {

                        for (final DataSnapshot outboundSnapshot : dataSnapshot.getChildren()) {

                            if (outboundSnapshot != null &&
                                    outboundSnapshot.getKey() != null && outboundSnapshot.getValue() != null) {

                                messageBoxListCheck(outboundSnapshot);

                            }
                        }
                        adapterLoadCheck();
                        setSmoothScrolling();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), databaseError.toString());
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
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
                itemAdded = true;
                fillMessageBoxList(mDataSnapshot);
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
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
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void setSmoothScrolling() {
        try {
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
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void fillMessageBoxList(DataSnapshot outboundSnapshot) {
        try {
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

            lastAddedMessage = messageBox;

            messageBoxList.add(messageBox);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void addMessage() {
        try {
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
                            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                                    new Object() {
                                    }.getClass().getEnclosingMethod().getName(), e.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), e.toString());
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void saveMessageContent(long messageTime) {
        try {
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
                    sendMessageBtn.setEnabled(true);
                    messageEdittext.setText("");
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void setAdapter() {
        try {
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
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void deleteSelectedMessages() {
        try {
            for (final MessageBox messageBox : messageBoxList) {
                if (messageBox.isSelectedForDelete()) {

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
                            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                                    new Object() {
                                    }.getClass().getEnclosingMethod().getName(), e.toString());
                        }
                    });
                }
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void completeMessageDeletion(MessageBox messageBox) {
        try {
            messageBoxList.remove(messageBox);
            messageWithPersonAdapter.notifyDataSetChanged();

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
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void deleteMessageContent() {

        try {
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
                    ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), e.toString());
                }
            });

            databaseReference3.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), e.toString());
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (valueEventListener != null && databaseReference != null)
                databaseReference.removeEventListener(valueEventListener);

        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageWithPersonFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }
}

