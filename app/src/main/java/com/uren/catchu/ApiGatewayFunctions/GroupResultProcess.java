package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.GroupRequest;
import catchu.model.GroupRequestResult;

public class GroupResultProcess extends AsyncTask<Void, Void, GroupRequestResult> {

    private OnEventListener<GroupRequestResult> mCallBack;
    private Context mContext;
    public Exception mException;
    public String userid;
    public String requestType;

    public GroupResultProcess(Context context, OnEventListener callback, String userid, String requestType) {
        this.mCallBack = callback;
        this.mContext = context;
        this.userid = userid;
        this.requestType = requestType;
    }

    @Override
    protected GroupRequestResult doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            GroupRequest groupRequest = new GroupRequest();
            groupRequest.setUserid(userid);
            groupRequest.setRequestType(requestType);
            GroupRequestResult groupRequestResult = instance.client.groupsPost(groupRequest);
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
