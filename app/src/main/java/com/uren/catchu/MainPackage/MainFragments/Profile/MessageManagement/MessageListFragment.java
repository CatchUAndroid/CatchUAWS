package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
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
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Adapters.MessageListAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Adapters.MessageWithPersonAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.MessageDeleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.MessageBox;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.MessageListBox;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.User;
import catchu.model.UserProfile;
import catchu.model.UserProfileProperties;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.CHAR_AMPERSAND;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_DATE;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_IS_SEEN;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGE;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGES;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_MESSAGE_CONTENT;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_NAME;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_RECEIPT;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_SENDER;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_USERID;
import static com.uren.catchu.Constants.StringConstants.FB_CHILD_WITH_PERSON;

public class MessageListFragment extends BaseFragment {

    View mView;

    @BindView(R.id.searchToolbarBackImgv)
    ImageView searchToolbarBackImgv;
    @BindView(R.id.imgCancelSearch)
    ImageView imgCancelSearch;
    @BindView(R.id.editTextSearch)
    EditText editTextSearch;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    DatabaseReference databaseReference;
    DatabaseReference databaseReference1;
    ValueEventListener valueEventListener;
    ValueEventListener valueEventListener1;

    ArrayList<MessageListBox> messageListBoxes;
    MessageListAdapter messageListAdapter;
    LinearLayoutManager linearLayoutManager;

    boolean setAdapterVal = false;
    boolean adapterLoaded = false;

    public MessageListFragment() {

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
            if(mView == null) {
                mView = inflater.inflate(R.layout.fragment_message_list, container, false);
                ButterKnife.bind(this, mView);
                initVariables();
                addListeners();
                getMessages();
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageListFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
    }

    public void initVariables() {
        try {
            messageListBoxes = new ArrayList<>();
        } catch (Resources.NotFoundException e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageListFragment.class.getSimpleName(),
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
                    getActivity().onBackPressed();
                }
            });

        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageListFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void getMessages() {
        try {
            databaseReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES).child(FB_CHILD_WITH_PERSON)
                    .child(AccountHolderInfo.getUserID());

            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.getChildren() != null) {

                        for (DataSnapshot outboundSnapshot : dataSnapshot.getChildren()) {
                            System.out.println("-----outboundSnapshot.getKey():" + outboundSnapshot.getKey());
                            System.out.println("-----outboundSnapshot.getValue():" + outboundSnapshot.getValue());

                            getUserDetail( outboundSnapshot);

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageListFragment.class.getSimpleName(),
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
                                getLastMessage(userProfile.getUserInfo());

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
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageListFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void getLastMessage(final UserProfileProperties userProfileProperties) {
        try {
            if(userProfileProperties.getUserid() != null) {
                databaseReference = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGES).child(FB_CHILD_WITH_PERSON)
                        .child(AccountHolderInfo.getUserID()).child(userProfileProperties.getUserid());

                Query query = databaseReference.orderByValue().limitToLast(1);

                valueEventListener = query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        if(dataSnapshot != null && dataSnapshot.getChildren() != null) {
                            System.out.println("xxxxxxxx>dataSnapshot.getKey():" + dataSnapshot.getKey());
                            System.out.println("xxxxxxxx>dataSnapshot.getValue():" + dataSnapshot.getValue());

                            for (final DataSnapshot outboundSnapshot : dataSnapshot.getChildren()) {
                                System.out.println("........>outboundSnapshot.getKey():" + outboundSnapshot.getKey());

                                databaseReference1 = FirebaseDatabase.getInstance().getReference(FB_CHILD_MESSAGE_CONTENT).child(outboundSnapshot.getKey());

                                valueEventListener1 = databaseReference1.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot mDataSnapshot) {
                                        System.out.println("_____>dataSnapshot.getKey():" + mDataSnapshot.getKey());
                                        System.out.println("_____>dataSnapshot.getValue():" + mDataSnapshot.getValue());

                                        messageBoxListCheck(mDataSnapshot, userProfileProperties);
                                        adapterLoadCheck();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if(databaseError != null){
                            ErrorSaveHelper.writeErrorToDB(getContext(), MessageListFragment.class.getSimpleName(),
                                    new Object() {
                                    }.getClass().getEnclosingMethod().getName(), databaseError.toString());
                        }
                    }
                });
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageListFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void messageBoxListCheck(DataSnapshot mDataSnapshot, UserProfileProperties userProfileProperties) {
        boolean notInList = false;
        for (MessageListBox messageListBox : messageListBoxes) {
            if (messageListBox != null && messageListBox.getUserProfileProperties() != null &&
                    messageListBox.getUserProfileProperties().getUserid() != null) {

                if(messageListBox.getUserProfileProperties().getUserid().equals(userProfileProperties.getUserid())){
                    notInList = true;
                    break;
                }
            }
        }

        if (!notInList) {
            fillMessageBoxList(mDataSnapshot,  userProfileProperties);
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
        try {
            messageListAdapter = new MessageListAdapter(getContext(), messageListBoxes, new ItemClickListener() {
                @Override
                public void onClick(Object object, int clickedItem) {
                    MessageListBox messageListBox = (MessageListBox) object;
                    startMessageWithPersonFragment(messageListBox);

                }
            });

            recyclerView.setAdapter(messageListAdapter);
            linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            setAdapterVal = true;
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageListFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void startMessageWithPersonFragment(MessageListBox messageListBox) {

        if(mFragmentNavigation != null && messageListBox != null && messageListBox.getUserProfileProperties() != null) {
            User user = new User();
            user.setUsername(messageListBox.getUserProfileProperties().getUsername());
            user.setName(messageListBox.getUserProfileProperties().getName());
            user.setUserid(messageListBox.getUserProfileProperties().getUserid());
            user.setProfilePhotoUrl(messageListBox.getUserProfileProperties().getProfilePhotoUrl());
            user.setEmail(messageListBox.getUserProfileProperties().getEmail());
            user.setProvider(messageListBox.getUserProfileProperties().getProvider());
            mFragmentNavigation.pushFragment(new MessageWithPersonFragment(user), ANIMATE_LEFT_TO_RIGHT);
        }
    }

    public void fillMessageBoxList(DataSnapshot outboundSnapshot, UserProfileProperties userProfileProperties) {
        try {
            MessageListBox messageListBox = new MessageListBox();

            messageListBox.setUserProfileProperties(userProfileProperties);

            Map<String, Object> map = (Map) outboundSnapshot.getValue();
            messageListBox.setMessageText((String) map.get(FB_CHILD_MESSAGE));
            messageListBox.setDate((long) map.get(FB_CHILD_DATE));

            Map<String, Object> senderMap = (Map) map.get(FB_CHILD_SENDER);
            String senderUserid = (String) senderMap.get(FB_CHILD_USERID);

            Map<String, Object> receiptMap = (Map) map.get(FB_CHILD_RECEIPT);
            String receiptUserid = (String) receiptMap.get(FB_CHILD_USERID);

            if(receiptUserid.equals(AccountHolderInfo.getUserID()))
                messageListBox.setIamReceipt(true);
            else
                messageListBox.setIamReceipt(false);

            messageListBox.setSeen((boolean) receiptMap.get(FB_CHILD_IS_SEEN));

            messageListBoxes.add(messageListBox);

        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageListFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            databaseReference.removeEventListener(valueEventListener);
            databaseReference1.removeEventListener(valueEventListener1);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), MessageListFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }
}
