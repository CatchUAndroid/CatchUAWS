package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.ProviderListRequestProcess;
import com.uren.catchu.FragmentControllers.FragNavController;
import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderContactsProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.MessageDataUtil;
import com.uren.catchu.GeneralUtils.DataModelUtil.PhoneNumberFormatUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.Interfaces.OnLoadedListener;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.Adapters.ContactFriendsAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.Adapters.InviteContactsAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Models.Contact;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.OtherProfileFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import catchu.model.Provider;
import catchu.model.ProviderList;
import catchu.model.User;
import catchu.model.UserListResponse;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.PROVIDER_TYPE_PHONE;

@SuppressLint("ValidFragment")
public class ContactFriendsFragment extends BaseFragment {

    View mView;
    RecyclerView appUsersRecyclerView;
    RecyclerView inviteRecyclerView;
    LinearLayout warningMsgLayout;
    TextView warningMsgTv;
    LinearLayout inviteWarningMsgLayout;
    TextView inviteWarningMsgTv;
    PermissionModule permissionModule;
    ContactFriendsAdapter contactFriendsAdapter;
    InviteContactsAdapter inviteContactsAdapter;
    List<Contact> reformedContactList;
    List<Contact> inviteContactsList;
    UserListResponse appUsersList;
    OnLoadedListener onLoadedListener;
    LinearLayout toolbarLayout;
    boolean showTollbar;
    ProgressBar progressBar;
    EditText editTextSearch;
    ImageView imgCancelSearch;
    ImageView backImgv;
    ImageView searchImgv;
    boolean edittextFocused = false;

    public ContactFriendsFragment(OnLoadedListener onLoadedListener, boolean showTollbar) {
        this.onLoadedListener = onLoadedListener;
        this.showTollbar = showTollbar;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.contact_list_layout, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            checkToolbarVisibility();
            addListeners();
            getContactList();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    public void initVariables() {
        appUsersRecyclerView = mView.findViewById(R.id.appUsersRecyclerView);
        inviteRecyclerView = mView.findViewById(R.id.inviteRecyclerView);
        warningMsgLayout = mView.findViewById(R.id.warningMsgLayout);
        warningMsgTv = mView.findViewById(R.id.warningMsgTv);
        inviteWarningMsgLayout = mView.findViewById(R.id.inviteWarningMsgLayout);
        inviteWarningMsgTv = mView.findViewById(R.id.inviteWarningMsgTv);
        toolbarLayout = mView.findViewById(R.id.toolbarLayout);
        backImgv = mView.findViewById(R.id.backImgv);
        imgCancelSearch = mView.findViewById(R.id.imgCancelSearch);
        editTextSearch = mView.findViewById(R.id.editTextSearch);
        searchImgv = mView.findViewById(R.id.searchImgv);
        progressBar = mView.findViewById(R.id.progressBar);
        permissionModule = new PermissionModule(getActivity());
        reformedContactList = new ArrayList<>();
        inviteContactsList = new ArrayList<>();
    }

    private void checkToolbarVisibility() {
        if (showTollbar)
            toolbarLayout.setVisibility(View.VISIBLE);
    }

    private void addListeners() {
        backImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edittextFocused) {
                    CommonUtils.hideKeyBoard(getContext());
                    backImgv.setVisibility(View.GONE);
                    searchImgv.setVisibility(View.VISIBLE);
                    if (editTextSearch != null)
                        editTextSearch.setText("");
                } else {
                    ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_RIGHT_TO_LEFT;
                    getActivity().onBackPressed();
                }
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
                    updateAdapter(s.toString());
                    imgCancelSearch.setVisibility(View.VISIBLE);
                } else {
                    updateAdapter("");
                    imgCancelSearch.setVisibility(View.GONE);
                }
            }
        });

        editTextSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    backImgv.setVisibility(View.VISIBLE);
                    searchImgv.setVisibility(View.GONE);
                    edittextFocused = true;
                } else {
                    backImgv.setVisibility(View.GONE);
                    searchImgv.setVisibility(View.VISIBLE);
                    edittextFocused = false;
                }
            }
        });

        editTextSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backImgv.setVisibility(View.VISIBLE);
                searchImgv.setVisibility(View.GONE);
            }
        });

        imgCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextSearch != null)
                    editTextSearch.setText("");
                imgCancelSearch.setVisibility(View.GONE);
                CommonUtils.hideKeyBoard(getContext());
                backImgv.setVisibility(View.GONE);
                searchImgv.setVisibility(View.VISIBLE);

            }
        });
    }

    public void getContactList() {
        if (!permissionModule.checkReadContactsPermission()) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, permissionModule.PERMISSION_READ_CONTACTS);
        } else {
            startGetContactList();
        }
    }

    public void startGetContactList() {
        progressBar.setVisibility(View.VISIBLE);
        AccountHolderContactsProcess.getContactList(getActivity(), new CompleteCallback() {
            @Override
            public void onComplete(Object object) {
                List<Contact> contactList = (List<Contact>) object;

                PhoneNumberFormatUtil.reformPhoneList(contactList, getActivity(), new CompleteCallback() {
                    @Override
                    public void onComplete(Object object) {
                        clearDuplicateNumbers((List<Contact>) object);

                        ProviderList providerList = new ProviderList();
                        List<Provider> list = new ArrayList<>();

                        for (Contact contact : reformedContactList) {
                            Provider provider = new Provider();
                            provider.setProviderid(contact.getPhoneNumber());
                            provider.setProviderType(PROVIDER_TYPE_PHONE);
                            list.add(provider);
                        }

                        providerList.setItems(list);
                        providerListProcess(providerList);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        progressBar.setVisibility(View.GONE);
                        onLoadedListener.onError(e.getMessage());
                    }
                });
            }

            @Override
            public void onFailed(Exception e) {
                progressBar.setVisibility(View.GONE);
                onLoadedListener.onError(e.getMessage());
            }
        });
    }

    public void clearDuplicateNumbers(List<Contact> tempContactList) {
        for (Contact contact : tempContactList) {

            boolean isExist = false;

            for (Contact tempContact : reformedContactList) {
                if (tempContact != null && tempContact.getPhoneNumber() != null) {
                    if (contact.getPhoneNumber().trim().equals(tempContact.getPhoneNumber().trim())) {
                        isExist = true;
                        break;
                    }
                }
            }

            if (!isExist)
                reformedContactList.add(contact);
        }
    }

    public void providerListProcess(final ProviderList providerList) {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                ProviderListRequestProcess providerListRequestProcess = new ProviderListRequestProcess(new OnEventListener() {
                    @Override
                    public void onSuccess(Object object) {
                        if (object != null) {
                            appUsersList = (UserListResponse) object;

                            if (getContext() != null) {
                                if (appUsersList != null && appUsersList.getItems() != null && appUsersList.getItems().size() > 0) {
                                    contactFriendsAdapter = new ContactFriendsAdapter(getContext(), appUsersList, new ItemClickListener() {
                                        @Override
                                        public void onClick(Object object, int clickedItem) {
                                            displayUserProfile((String) object, clickedItem);
                                        }
                                    });
                                    appUsersRecyclerView.setAdapter(contactFriendsAdapter);
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                    appUsersRecyclerView.setLayoutManager(linearLayoutManager);
                                } else {
                                    warningMsgTv.setText(getActivity().getResources().getString(R.string.THERE_IS_NO_CONTACTFRIEND_WHO_USING_CATCHU));
                                    warningMsgLayout.setVisibility(View.VISIBLE);
                                }

                                setInviteFriendsAdapter();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        progressBar.setVisibility(View.GONE);
                        onLoadedListener.onError(e.getMessage());
                    }

                    @Override
                    public void onTaskContinue() {

                    }
                }, providerList, token, AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid());

                providerListRequestProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

    public void setInviteFriendsAdapter() {

        prepareInviteContactList();

        if (inviteContactsList != null && inviteContactsList.size() > 0 && getContext() != null) {
            inviteContactsAdapter = new InviteContactsAdapter(getContext(), inviteContactsList, new ItemClickListener() {
                @Override
                public void onClick(Object object, int clickedItem) {
                    Contact contact = (Contact) object;
                    sendSmsToInvite(contact);
                }
            });
            inviteRecyclerView.setAdapter(inviteContactsAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            inviteRecyclerView.setLayoutManager(linearLayoutManager);
        }

        progressBar.setVisibility(View.GONE);
        onLoadedListener.onLoaded();
    }

    public void sendSmsToInvite(Contact contact) {
        if (contact != null && contact.getPhoneNumber() != null) {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("sms:" + contact.getPhoneNumber()));
            sendIntent.putExtra("sms_body", getActivity().getResources().getString(R.string.CONTACT_INVITE_MESSAGE) + CommonUtils.getGooglePlayAppLink(getActivity()));
            startActivity(sendIntent);
        }
    }

    public void prepareInviteContactList() {
        if (reformedContactList != null && reformedContactList.size() > 0) {
            if (appUsersList != null && appUsersList.getItems() != null && appUsersList.getItems().size() > 0) {
                for (Contact contact : reformedContactList) {

                    boolean isExist = false;

                    for (User user : appUsersList.getItems()) {
                        if (user != null && user.getProvider() != null && user.getProvider().getProviderType() != null && user.getProvider().getProviderType().equals(PROVIDER_TYPE_PHONE)) {
                            if (user.getProvider().getProviderid().trim().equals(contact.getPhoneNumber().trim())) {
                                isExist = true;
                                break;
                            }
                        }
                    }

                    if (!isExist)
                        inviteContactsList.add(contact);
                }
            }
        }
    }

    private void displayUserProfile(String userid, int clickedItem) {

        if (!userid.equals(AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid())) {
            if (mFragmentNavigation != null) {
                User user = new User();
                user.setUserid(userid);
                UserInfoListItem userInfoListItem = new UserInfoListItem(user);
                mFragmentNavigation.pushFragment(OtherProfileFragment.newInstance(userInfoListItem), ANIMATE_RIGHT_TO_LEFT);
                FollowInfoResultArrayItem followInfoResultArrayItem = new FollowInfoResultArrayItem();
                followInfoResultArrayItem.setUserid(userid);
                FollowInfoListItem followInfoListItem = new FollowInfoListItem(followInfoResultArrayItem);
                followInfoListItem.setAdapter(contactFriendsAdapter);
                followInfoListItem.setClickedPosition(clickedItem);
                mFragmentNavigation.pushFragment(OtherProfileFragment.newInstance(followInfoListItem), ANIMATE_RIGHT_TO_LEFT);
            }
        } else {
            NextActivity.switchAndUpdateTabSelection(FragNavController.TAB3);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == permissionModule.PERMISSION_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGetContactList();
            }
        }
    }

    public void updateAdapter(String searchText) {
        if (searchText != null && contactFriendsAdapter != null) {
            contactFriendsAdapter.updateAdapter(searchText, new ReturnCallback() {
                @Override
                public void onReturn(Object object) {
                    int itemSize = (int) object;

                    if (itemSize == 0) {
                        warningMsgTv.setText(getActivity().getResources().getString(R.string.THERE_IS_NO_SEARCH_RESULT));
                        warningMsgLayout.setVisibility(View.VISIBLE);
                    } else
                        warningMsgLayout.setVisibility(View.GONE);
                }
            });
        }

        if (searchText != null && inviteContactsAdapter != null) {
            inviteContactsAdapter.updateAdapter(searchText, new ReturnCallback() {
                @Override
                public void onReturn(Object object) {
                    int itemSize = (int) object;

                    if (itemSize == 0) {
                        inviteWarningMsgTv.setText(getActivity().getResources().getString(R.string.THERE_IS_NO_SEARCH_RESULT));
                        inviteWarningMsgLayout.setVisibility(View.VISIBLE);
                    } else
                        inviteWarningMsgLayout.setVisibility(View.GONE);
                }
            });
        }
    }

}
