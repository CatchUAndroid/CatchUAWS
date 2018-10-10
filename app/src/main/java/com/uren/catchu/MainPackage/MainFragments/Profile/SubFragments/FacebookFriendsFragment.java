package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.uren.catchu.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;

public class FacebookFriendsFragment extends Fragment{

    View mView;

    public FacebookFriendsFragment() { }

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
        getFacebookFriends();
    }

    public void initVariables(){

    }

    private void addListeners() {

    }

    public void getFacebookFriends() {

        /*GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        if (object != null) {
                            try {
                                JSONArray friendList = object.getJSONObject("friends").getJSONArray("data");
                                FacebookFriendList.setInstance(null);

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

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "friends");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();*/
    }




}
