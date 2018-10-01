package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import catchu.model.UserProfile;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;

public class UserDetail extends AsyncTask<Void, Void, UserProfile> {

    private OnEventListener<UserProfile> mCallBack;
    private Context mContext;
    public Exception mException;
    public String userid;
    private String token;

    public UserDetail(Context context, OnEventListener callback, String userid, String token) {
        mCallBack = callback;
        mContext = context;
        this.userid = userid;
        this.token = token;
    }


    @Override
    protected UserProfile doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            UserProfile userProfile = instance.client.usersGet(token, userid);

            if(userProfile.getError().getCode().intValue() == RESPONSE_OK){
                return userProfile;
            }else{
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
