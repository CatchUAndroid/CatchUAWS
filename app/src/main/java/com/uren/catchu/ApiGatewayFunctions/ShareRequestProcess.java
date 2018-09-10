package com.uren.catchu.ApiGatewayFunctions;

import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.FriendRequest;
import catchu.model.FriendRequestList;
import catchu.model.ShareRequest;
import catchu.model.ShareResponse;

public class ShareRequestProcess extends AsyncTask<Void, Void, ShareResponse> {

    private OnEventListener<ShareResponse> mCallBack;
    public Exception mException;
    public ShareRequest shareRequest;

    public ShareRequestProcess(OnEventListener callback, ShareRequest shareRequest) {
        this.shareRequest = shareRequest;
        this.mCallBack = callback;
    }

    @Override
    protected ShareResponse doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            ShareResponse shareResponse = instance.client.sharesShareidPost("", shareRequest);
            return shareResponse;

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
    protected void onPostExecute(ShareResponse shareResponse) {
        super.onPostExecute(shareResponse);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(shareResponse);
            } else {
                mCallBack.onFailure(mException);
            }
        }
    }
}
