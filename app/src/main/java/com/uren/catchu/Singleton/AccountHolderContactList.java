package com.uren.catchu.Singleton;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.FriendListRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Models.Contact;
import com.uren.catchu.Permissions.PermissionModule;

import java.util.ArrayList;
import java.util.List;

import catchu.model.FriendList;
import catchu.model.User;
import catchu.model.UserListResponse;
import catchu.model.UserProfileProperties;

public class AccountHolderContactList {

    private static AccountHolderContactList accountHolderContactList = null;
    private static UserListResponse userListResponse;
    private static CompleteCallback mCompleteCallback;
    private static Context mContext;
    private static PermissionModule permissionModule;
    private static Fragment mFragment;

    public static void getInstance(Context context, Fragment fragment, CompleteCallback completeCallback) {

        mContext = context;
        mFragment = fragment;
        mCompleteCallback = completeCallback;

        if (accountHolderContactList == null) {
            permissionModule = new PermissionModule(mContext);
            userListResponse = new UserListResponse();
            userListResponse.setItems(new ArrayList<User>());
            accountHolderContactList = new AccountHolderContactList();
        } else
            mCompleteCallback.onComplete(userListResponse);
    }

    public AccountHolderContactList() {
        getContactFriends();
    }

    public int getSize() {
        return userListResponse.getItems().size();
    }

    public static void setInstance(AccountHolderContactList instance) {
        accountHolderContactList = instance;
    }

    private void getContactFriends() {
        Activity activity = (Activity) mContext;

        if (!permissionModule.checkReadContactsPermission()) {
            if (mFragment != null)
                mFragment.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, permissionModule.PERMISSION_READ_CONTACTS);
            else
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_CONTACTS}, permissionModule.PERMISSION_READ_CONTACTS);
        } else {
            getPhoneList();
        }
    }

    public void getPhoneList() {
        List<Contact> contactList = new ArrayList<>();


        Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            Contact contact = new Contact();

            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            String clearPhoneNum = "";

            for (int i = 0; i < phoneNumber.length(); i++) {

                char ch = phoneNumber.charAt(i);
                if (Character.isDigit(ch)) {
                    clearPhoneNum += ch;
                }
            }
            contact.setName(name);
            contact.setPhoneNumber(clearPhoneNum);
            Log.i("Info", "->name:" + name + " ->phone:" + phoneNumber);
            contactList.add(contact);

        }
        phones.close();
    }

    public static synchronized void reset(){
        accountHolderContactList = null;
    }

}
