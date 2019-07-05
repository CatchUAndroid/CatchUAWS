package com.uren.catchu.ApiGatewayFunctions;

import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.FriendList;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;

public class FriendListRequestProcess extends AsyncTask<Void, Void, FriendList> {

    private OnEventListener<FriendList> mCallBack;
    public Exception mException;
    public String userid;
    private String token;
    private String pageCount;
    private String perPageCount;

    public FriendListRequestProcess(OnEventListener callback, String userid, String token, int pageCount, int perPageCount) {
        this.mCallBack = callback;
        this.userid = userid;
        this.token = token;
        this.pageCount = String.valueOf(pageCount);
        this.perPageCount = String.valueOf(perPageCount);
    }

    @Override
    protected FriendList doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            //
            FriendList friendList = instance.client.friendsGet(userid, perPageCount, pageCount, token);

            if (friendList.getError().getCode().intValue() == RESPONSE_OK) {
                return friendList;
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
    protected void onPostExecute(FriendList friendList) {
        super.onPostExecute(friendList);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(friendList);
            } else {
                mCallBack.onFailure(mException);
            }
        }
    }
}
