package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.BaseResponse;
import catchu.model.UserProfile;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;


public class UpdateUserProfile extends AsyncTask<Void, Void, UserProfile> {

    private OnEventListener<UserProfile> mCallBack;
    public Exception mException;
    public UserProfile userProfile;
    private String token;

    public UpdateUserProfile(Context context, OnEventListener callback, UserProfile userProfile, String token) {
        mCallBack = callback;
        Context mContext = context;
        this.userProfile = userProfile;
        this.token = token;
    }


    @Override
    protected UserProfile doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {

            BaseResponse rsp = instance.client.usersPost(token, userProfile);

            if(rsp.getError().getCode().intValue() == RESPONSE_OK){
                return userProfile;
            }else{
                return null;
            }

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
    protected void onPostExecute(UserProfile userProfile) {
        super.onPostExecute(userProfile);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(userProfile);
            } else {
                mCallBack.onFailure(mException);
            }
        }

    }
}