package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.FriendList;
import catchu.model.FriendRequestList;
import catchu.model.GroupRequest;
import catchu.model.GroupRequestResult;

public class FriendListRequestProcess extends AsyncTask<Void, Void, FriendList> {

    private OnEventListener<FriendList> mCallBack;
    public Exception mException;
    public String userid;

    public FriendListRequestProcess(OnEventListener callback, String userid) {
        this.mCallBack = callback;
        this.userid = userid;
    }

    @Override
    protected FriendList doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            FriendList friendList = instance.client.friendsGet(userid);
            return friendList;

        } catch (Exception e) {
            mException = e;
            e.printStackTrace();
            Log.e("error ", e.toString());
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (mCallBack != null) {
            mCallBack.onTaskContinue();
        }
    }

    @Override
    protected void onPostExecute(FriendList friendList) {
        super.onPostExecute(friendList);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(friendList);
            } else {
                mCallBack.onFailure(mException);
            }
        }
    }
}
