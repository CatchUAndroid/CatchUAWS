package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.SingletonApiClient;

import java.math.BigDecimal;

import catchu.model.Response;
import catchu.model.UserProfile;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;


public class UpdateUserProfile extends AsyncTask<Void, Void, UserProfile> {

    private OnEventListener<UserProfile> mCallBack;
    private Context mContext;
    public Exception mException;
    public UserProfile userProfile;

    public UpdateUserProfile(Context context, OnEventListener callback, UserProfile userProfile) {
        mCallBack = callback;
        mContext = context;
        this.userProfile = userProfile;
    }


    @Override
    protected UserProfile doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {

            Response rsp = instance.client.usersPost(userProfile);


            if(rsp.getError().getCode().intValue() == RESPONSE_OK){
                Log.i("-> update response ", "successful");
                return userProfile;
            }else{
                Log.i("-> update response ", "fail");
                return null;
            }

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