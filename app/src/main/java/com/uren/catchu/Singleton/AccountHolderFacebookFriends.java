package com.uren.catchu.Singleton;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.uren.catchu.ApiGatewayFunctions.FollowInfoProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.ProviderListRequestProcess;
import com.uren.catchu.Interfaces.CompleteCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import catchu.model.Provider;
import catchu.model.ProviderList;
import catchu.model.User;
import catchu.model.UserListResponse;
import static com.uren.catchu.Constants.StringConstants.PROVIDER_TYPE_FACEBOOK;

public class AccountHolderFacebookFriends {

    private static AccountHolderFacebookFriends accountHolderFacebookFriends = null;
    private static UserListResponse userListResponse;
    private static CompleteCallback mCompleteCallback;

    public static void getInstance(CompleteCallback completeCallback) {

        mCompleteCallback = completeCallback;

        if (accountHolderFacebookFriends == null) {
            userListResponse = new UserListResponse();
            userListResponse.setItems(new ArrayList<User>());
            accountHolderFacebookFriends = new AccountHolderFacebookFriends();
        } else
            mCompleteCallback.onComplete(userListResponse);
    }

    public AccountHolderFacebookFriends() {
        getFacebookFriends();
    }

    public static UserListResponse getFacebookFriendsList() {
        return userListResponse;
    }

    public int getSize() {
        return userListResponse.getItems().size();
    }

    public static void setInstance(AccountHolderFacebookFriends instance) {
        accountHolderFacebookFriends = instance;
    }

    private void getFacebookFriends() {
        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        if (object != null) {
                            try {
                                JSONArray friendList = object.getJSONObject("friends").getJSONArray("data");

                                Log.i("Info", "");

                                ProviderList providerList = new ProviderList();
                                List<Provider> list = new ArrayList<>();

                                for (int i = 0; i < friendList.length(); i++) {
                                    JSONObject jsonObject = friendList.getJSONObject(i);
                                    String providerId = (String) jsonObject.get("id");

                                    Provider provider = new Provider();
                                    provider.setProviderid(providerId);
                                    provider.setProviderType(PROVIDER_TYPE_FACEBOOK);

                                    list.add(provider);
                                }

                                providerList.setItems(list);
                                providerListProcess(providerList);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                mCompleteCallback.onFailed(e);
                            }
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "friends");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    public void providerListProcess(final ProviderList providerList) {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                ProviderListRequestProcess providerListRequestProcess = new ProviderListRequestProcess(new OnEventListener() {
                    @Override
                    public void onSuccess(Object object) {
                        if (object != null) {
                            userListResponse = (UserListResponse) object;
                            mCompleteCallback.onComplete(userListResponse);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        mCompleteCallback.onFailed(e);
                    }

                    @Override
                    public void onTaskContinue() {

                    }
                }, providerList, token, AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid());

                providerListRequestProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

    public static synchronized void reset(){
        accountHolderFacebookFriends = null;
    }

}
