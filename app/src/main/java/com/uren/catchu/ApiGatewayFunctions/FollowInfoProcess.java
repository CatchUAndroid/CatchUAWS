package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.FollowInfo;
import catchu.model.FollowInfoResultArrayItem;
import catchu.model.UserProfile;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;


public class FollowInfoProcess extends AsyncTask<Void, Void, FollowInfo> {

    private OnEventListener<FollowInfo> mCallBack;
    private Context mContext;
    public Exception mException;
    public FollowInfo followInfo;
    private String token;

    public FollowInfoProcess(Context context, OnEventListener callback, FollowInfo followInfo, String token) {
        mCallBack = callback;
        mContext = context;
        this.followInfo = followInfo;
        this.token = token;
    }


    @Override
    protected FollowInfo doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {

            FollowInfo rsp = instance.client.usersFollowPost(token, followInfo);

            if(rsp.getError().getCode().intValue() == RESPONSE_OK){
                return rsp;
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
    protected void onPostExecute(FollowInfo followInfo) {
        super.onPostExecute(followInfo);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(followInfo);
            } else {
                mCallBack.onFailure(mException);
            }
        }

    }
}