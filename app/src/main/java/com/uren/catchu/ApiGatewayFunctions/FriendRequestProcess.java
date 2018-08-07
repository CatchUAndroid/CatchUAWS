package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.FriendRequest;
import catchu.model.GroupRequest;
import catchu.model.GroupRequestResult;
import catchu.model.UserProfile;

public class FriendRequestProcess extends AsyncTask<Void, Void, Void> {

    private OnEventListener<FriendRequest> mCallBack;
    private Context mContext;
    public Exception mException;
    public String requestType;
    public String requesterUserid;
    public String requestedUserid;

    public FriendRequestProcess(Context context, OnEventListener callback,String requestType, String requesterUserid, String requestedUserid) {
        this.requestType = requestType;
        this.requesterUserid = requesterUserid;
        this.requestedUserid = requestedUserid;
        this.mContext = context;
        this.mCallBack = callback;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            FriendRequest friendRequest = new FriendRequest();
            friendRequest.setRequestType(requestType);
            friendRequest.setRequesterUserid(requesterUserid);
            friendRequest.setRequestedUserid(requestedUserid);
            instance.client.requestProcessPost(friendRequest);

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
}
