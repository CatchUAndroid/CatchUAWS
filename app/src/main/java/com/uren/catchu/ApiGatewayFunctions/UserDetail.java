package com.uren.catchu.ApiGatewayFunctions;

import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.UserProfile;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;

public class UserDetail extends AsyncTask<Void, Void, UserProfile> {

    private OnEventListener<UserProfile> mCallBack;
    private Exception mException;
    private String userid;
    private String requestedUserId;
    private String shortInfo;
    private String token;

    public UserDetail(OnEventListener callback, String userid, String requestedUserId, String shortInfo, String token) {
        mCallBack = callback;
        this.userid = userid;
        this.requestedUserId = requestedUserId;
        this.shortInfo = shortInfo;
        this.token = token;
        System.out.println("userid:" + userid + "  ---  requestedUserId:" + requestedUserId);
    }


    @Override
    protected UserProfile doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            UserProfile userProfile = instance.client.usersGet(userid, requestedUserId, token, shortInfo);

            if(userProfile.getError().getCode() == RESPONSE_OK){
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
