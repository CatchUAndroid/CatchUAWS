package com.uren.catchu.GeneralUtils.ApiModelsProcess;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Models.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class AccountHolderContactsProcess {

    public static void getContactList(Context context, CompleteCallback completeCallback) {
            List<Contact> contactList = new ArrayList<>();
            String previousPhoneNum = "";

            Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (Objects.requireNonNull(phones).moveToNext()) {

                Contact contact = new Contact();

                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                if(!phoneNumber.equals(previousPhoneNum)) {
                    StringBuilder clearPhoneNum = new StringBuilder();

                    for (int i = 0; i < phoneNumber.length(); i++) {
                        char ch = phoneNumber.charAt(i);
                        if (Character.isDigit(ch)) {
                            clearPhoneNum.append(ch);
                        }
                    }
                    contact.setName(name);
                    contact.setPhoneNumber(clearPhoneNum.toString());
                    contactList.add(contact);
                }
                previousPhoneNum = phoneNumber;
            }
            phones.close();
            Collections.sort(contactList, new CustomComparator());
            completeCallback.onComplete(contactList);
    }

    public static class CustomComparator implements Comparator<Contact> {
        @Override
        public int compare(Contact o1, Contact o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }
}
