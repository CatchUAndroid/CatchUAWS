package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.Singleton.AccountHolderInfo;

import catchu.model.GroupRequest;
import catchu.model.GroupRequestResult;

public class GroupResultProcess extends AsyncTask<Void, Void, GroupRequestResult> {

    private OnEventListener<GroupRequestResult> mCallBack;
    public Exception mException;
    public GroupRequest groupRequest;
    private String token;

    public GroupResultProcess(OnEventListener callback, GroupRequest groupRequest, String token) {
        this.mCallBack = callback;
        this.groupRequest = groupRequest;
        this.token = token;
    }

    @Override
    protected GroupRequestResult doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {

            GroupRequestResult groupRequestResult = instance.client.groupsPost(token, groupRequest);
            return groupRequestResult;

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
    protected void onPostExecute(GroupRequestResult groupRequestResult) {
        super.onPostExecute(groupRequestResult);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(groupRequestResult);
            } else {
                mCallBack.onFailure(mException);
            }
        }
    }
}
