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
import com.uren.catchu.Interfaces.CompleteCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import catchu.model.FollowInfo;
import catchu.model.FollowInfoResultArrayItem;

import static com.uren.catchu.Constants.StringConstants.FRIEND_CREATE_FOLLOW_DIRECTLY;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_FOLLOW;
import static com.uren.catchu.Constants.StringConstants.GET_USER_FOLLOWINGS;

public class AccountHolderFacebookFriends {


    private static AccountHolderFacebookFriends accountHolderFacebookFriends = null;
    private static FollowInfo followInfo;
    private static CompleteCallback mCompleteCallback;

    public static void getInstance(CompleteCallback completeCallback) {

        mCompleteCallback = completeCallback;

        if (accountHolderFacebookFriends == null) {
            followInfo = new FollowInfo();
            List<FollowInfoResultArrayItem> followInfoResultArrayItems = new ArrayList<>();
            followInfo.setResultArray(followInfoResultArrayItems);
            accountHolderFacebookFriends = new AccountHolderFacebookFriends();
        }else
            mCompleteCallback.onComplete(followInfo);
    }

    public AccountHolderFacebookFriends() {
        getFriends();
    }

    public static FollowInfo getFacebookFriendsList() {
        return followInfo;
    }

    public int getSize() {
        return followInfo.getResultArray().size();
    }

    public static void setInstance(AccountHolderFacebookFriends instance) {
        accountHolderFacebookFriends = instance;
    }

    private void getFriends() {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetFacebookFriends(token);
            }
        });
    }

    public static boolean isFacebookFriend(String userid){
        boolean isFoolowing = false;
        if (userid != null && !userid.trim().isEmpty()) {
            int index = 0;
            for (FollowInfoResultArrayItem followInfoResultArrayItem : followInfo.getResultArray()) {
                if (followInfoResultArrayItem.getUserid().equals(userid)) {
                    isFoolowing = true;
                    break;
                }
                index = index + 1;
            }
        }
        return isFoolowing;
    }

    private void startGetFacebookFriends(String token) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        if (object != null) {
                            try {
                                JSONArray friendList = object.getJSONObject("friends").getJSONArray("data");

                                Log.i("Info", "");






                                /*FacebookFriendList.setInstance(null);

                                friendListSize = friendList.length();

                                mProgressDialog.setMessage("Yükleniyor...");
                                mProgressDialog.show();

                                for (int i = 0; i < friendList.length(); i++) {
                                    JSONObject jsonObject = friendList.getJSONObject(i);
                                    String friendName = (String) jsonObject.get(name);
                                    String friendProviderID = (String) jsonObject.get("id");

                                    if (checkFriendByProviderId(friendProviderID))
                                        setFaceFriendToInviteList(friendProviderID, friendName, Yes);
                                    else
                                        setFaceFriendToInviteList(friendProviderID, friendName, null);

                                }*/

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "friends");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

}
