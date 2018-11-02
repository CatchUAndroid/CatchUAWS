package com.uren.catchu.GeneralUtils.DataModelUtil;

import android.content.Context;
import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.CountryListProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Models.Contact;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.List;

import catchu.model.Country;
import catchu.model.CountryListResponse;

public class PhoneNumberFormatUtil {

    public static void reformPhoneList(final List<Contact> contactList, final Context context, final CompleteCallback completeCallback) {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {

                CountryListProcess countryListProcess = new CountryListProcess(new OnEventListener() {
                    @Override
                    public void onSuccess(Object object) {
                        if (object != null) {
                            CountryListResponse countryListResponse = (CountryListResponse) object;

                            String locale = context.getResources().getConfiguration().locale.getCountry();

                            for (Country country : countryListResponse.getItems()) {
                                if (country != null && country.getCode() != null && !country.getCode().trim().isEmpty() &&
                                        country.getDialCode() != null && !country.getDialCode().isEmpty()) {
                                    if (country.getCode().trim().equals(locale)) {
                                        formatNumbersWithDialCode(country.getDialCode(), locale, contactList, completeCallback);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        completeCallback.onFailed(e);
                    }

                    @Override
                    public void onTaskContinue() {

                    }
                }, AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid(),
                        token);
                countryListProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

    public static void formatNumbersWithDialCode(String dialCode, String locale, final List<Contact> contactList, CompleteCallback completeCallback){
        List<Contact> reformedContactList = new ArrayList<>();

        if(locale.equals("TR")){
            for(Contact contact: contactList){
                if(contact != null && contact.getPhoneNumber() != null && !contact.getPhoneNumber().isEmpty()){

                    String completeNumber = "";
                    String reverseText = new StringBuilder(contact.getPhoneNumber().trim()).reverse().toString();
                    completeNumber = dialCode.trim() + new StringBuilder(reverseText.substring(0,10)).reverse().toString();

                    Contact contactTemp = new Contact();
                    contactTemp.setName(contact.getName());
                    contactTemp.setPhoneNumber(completeNumber);

                    reformedContactList.add(contactTemp);
                }
            }
        }
        completeCallback.onComplete(reformedContactList);
    }
}
