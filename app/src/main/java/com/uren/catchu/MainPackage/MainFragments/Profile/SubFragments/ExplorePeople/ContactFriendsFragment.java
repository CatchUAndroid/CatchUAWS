package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.ProviderListRequestProcess;
import com.uren.catchu.FragmentControllers.FragNavController;
import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderContactsProcess;
import com.uren.catchu.GeneralUtils.DataModelUtil.MessageDataUtil;
import com.uren.catchu.GeneralUtils.DataModelUtil.PhoneNumberFormatUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.FollowInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.Adapters.ContactFriendsAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.Adapters.FacebookFriendsAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Models.Contact;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.OtherProfileFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderContactList;
import com.uren.catchu.Singleton.AccountHolderInfo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import catchu.model.FollowInfoResultArrayItem;
import catchu.model.Provider;
import catchu.model.ProviderList;
import catchu.model.UserListResponse;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.PROVIDER_TYPE_FACEBOOK;
import static com.uren.catchu.Constants.StringConstants.PROVIDER_TYPE_PHONE;

public class ContactFriendsFragment extends BaseFragment {

    View mView;
    RecyclerView specialRecyclerView;
    TextView warningMsgTv;
    PermissionModule permissionModule;
    ContactFriendsAdapter contactFriendsAdapter;

    public ContactFriendsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_special_select, container, false);
            ButterKnife.bind(this, mView);
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initVariables();
        addListeners();
        getContactList();
    }

    public void initVariables() {
        specialRecyclerView = mView.findViewById(R.id.specialRecyclerView);
        warningMsgTv = mView.findViewById(R.id.warningMsgTv);
        permissionModule = new PermissionModule(getActivity());
    }

    private void addListeners() {

    }

    public void getContactList() {
        if (!permissionModule.checkReadContactsPermission()) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, permissionModule.PERMISSION_READ_CONTACTS);
        } else {
            startGetContactList();
        }
    }

    public void startGetContactList() {
        AccountHolderContactsProcess.getContactList(getActivity(), new CompleteCallback() {
            @Override
            public void onComplete(Object object) {
                List<Contact> contactList = (List<Contact>) object;

                PhoneNumberFormatUtil.reformPhoneList(contactList, getActivity(), new CompleteCallback() {
                    @Override
                    public void onComplete(Object object) {
                        List<String> fixedPhoneList = (List<String>) object;

                        ProviderList providerList = new ProviderList();
                        List<Provider> list = new ArrayList<>();

                        for (String phoneNumber: fixedPhoneList) {
                            Provider provider = new Provider();
                            provider.setProviderid(phoneNumber);
                            provider.setProviderType(PROVIDER_TYPE_PHONE);
                            list.add(provider);
                        }

                        providerList.setItems(list);
                        providerListProcess(providerList);
                    }

                    @Override
                    public void onFailed(Exception e) {

                    }
                });
            }

            @Override
            public void onFailed(Exception e) {
            }
        });
    }

    public void providerListProcess(final ProviderList providerList) {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                ProviderListRequestProcess providerListRequestProcess = new ProviderListRequestProcess(new OnEventListener() {
                    @Override
                    public void onSuccess(Object object) {
                        if (object != null) {
                            UserListResponse userListResponse = (UserListResponse) object;

                            MessageDataUtil.setWarningMessageVisibility(userListResponse, warningMsgTv,
                                    getActivity().getResources().getString(R.string.THERE_IS_NO_CONTACTFRIEND_WHO_USING_CATCHU));

                            if (userListResponse != null && userListResponse.getItems() != null && userListResponse.getItems().size() > 0) {
                                contactFriendsAdapter = new ContactFriendsAdapter(getActivity(), userListResponse, new ItemClickListener() {
                                    @Override
                                    public void onClick(Object object, int clickedItem) {
                                        displayUserProfile((String) object, clickedItem);
                                    }
                                });
                                specialRecyclerView.setAdapter(contactFriendsAdapter);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                specialRecyclerView.setLayoutManager(linearLayoutManager);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }

                    @Override
                    public void onTaskContinue() {

                    }
                }, providerList, token, AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid());

                providerListRequestProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

    private void displayUserProfile(String userid, int clickedItem) {

        if (!userid.equals(AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid())) {
            if (mFragmentNavigation != null) {
                FollowInfoResultArrayItem followInfoResultArrayItem = new FollowInfoResultArrayItem();
                followInfoResultArrayItem.setUserid(userid);
                FollowInfoListItem followInfoListItem = new FollowInfoListItem(followInfoResultArrayItem);
                mFragmentNavigation.pushFragment(OtherProfileFragment.newInstance(followInfoListItem), ANIMATE_RIGHT_TO_LEFT);
            }
        } else {
            NextActivity.switchAndUpdateTabSelection(FragNavController.TAB5);
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
        if (searchText != null && contactFriendsAdapter != null)
            contactFriendsAdapter.updateAdapter(searchText);
    }
}
