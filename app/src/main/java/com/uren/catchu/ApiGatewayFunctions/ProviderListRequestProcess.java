package com.uren.catchu.ApiGatewayFunctions;

import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import catchu.model.ProviderList;
import catchu.model.UserListResponse;

public class ProviderListRequestProcess extends AsyncTask<Void, Void, UserListResponse> {

    private OnEventListener<UserListResponse> mCallBack;
    public Exception mException;
    public ProviderList providerList;
    private String token;
    private String userid;

    public ProviderListRequestProcess(OnEventListener callback, ProviderList providerList, String token, String userid) {
        this.providerList = providerList;
        this.mCallBack = callback;
        this.token = token;
        this.userid = userid;
    }

    @Override
    protected UserListResponse doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            UserListResponse userListResponse = (UserListResponse) instance.client.usersProvidersPost(userid, token, providerList);
            return userListResponse;

        } catch (Exception e) {
            mException = e;
            e.printStackTrace();
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
    protected void onPostExecute(UserListResponse userListResponse) {
        super.onPostExecute(userListResponse);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(userListResponse);
            } else {
                mCallBack.onFailure(mException);
            }
        }
    }
}
