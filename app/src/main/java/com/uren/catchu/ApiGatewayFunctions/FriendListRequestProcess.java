package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.Singleton.AccountHolderInfo;

import catchu.model.FriendList;
import catchu.model.FriendRequestList;
import catchu.model.GroupRequest;
import catchu.model.GroupRequestResult;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;

public class FriendListRequestProcess extends AsyncTask<Void, Void, FriendList> {

    private OnEventListener<FriendList> mCallBack;
    public Exception mException;
    public String userid;
    private String token;

    public FriendListRequestProcess(OnEventListener callback, String userid, String token) {
        this.mCallBack = callback;
        this.userid = userid;
        this.token = token;
    }

    @Override
    protected FriendList doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            FriendList friendList = instance.client.friendsGet(userid, token);

            if (friendList.getError().getCode().intValue() == RESPONSE_OK) {
                return friendList;
            } else {
                return null;
            }

        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
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
