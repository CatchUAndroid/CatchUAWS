package com.uren.catchu.ApiGatewayFunctions;

import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.FriendRequest;
import catchu.model.FriendRequestList;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;

public class FriendRequestProcess extends AsyncTask<Void, Void, FriendRequestList> {

    private OnEventListener<FriendRequestList> mCallBack;
    public Exception mException;
    public String requestType;
    public String requesterUserid;
    public String requestedUserid;
    private String token;

    public FriendRequestProcess(OnEventListener callback,String requestType, String requesterUserid, String requestedUserid, String token) {
        this.requestType = requestType;
        this.requesterUserid = requesterUserid;
        this.requestedUserid = requestedUserid;
        this.mCallBack = callback;
        this.token = token;
    }

    @Override
    protected FriendRequestList doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            FriendRequest friendRequest = new FriendRequest();
            friendRequest.setRequestType(requestType);
            friendRequest.setRequesterUserid(requesterUserid);
            friendRequest.setRequestedUserid(requestedUserid);
            FriendRequestList friendRequestList = instance.client.followRequestPost(token, friendRequest);

            if (friendRequestList.getError().getCode().intValue() == RESPONSE_OK) {
                return friendRequestList;
            } else {
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
    protected void onPostExecute(FriendRequestList friendRequestList) {
        super.onPostExecute(friendRequestList);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(friendRequestList);
            } else {
                mCallBack.onFailure(mException);
            }
        }
    }
}
