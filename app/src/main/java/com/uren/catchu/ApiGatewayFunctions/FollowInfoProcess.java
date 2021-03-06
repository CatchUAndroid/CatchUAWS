package com.uren.catchu.ApiGatewayFunctions;

import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.FollowInfoListResponse;
import catchu.model.User;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;

public class FollowInfoProcess extends AsyncTask<Void, Void, FollowInfoListResponse> {

    private OnEventListener<FollowInfoListResponse> mCallBack;
    public Exception mException;
    private String userId;
    private String requestedUserId;
    private String perPage;
    private String page;
    private String requestType;
    private String token;

    public FollowInfoProcess(OnEventListener callback, String userId, String requestedUserId, String requestType, int perPage, int page, String token) {
        mCallBack = callback;
        this.userId = userId;
        this.requestType = requestType;
        this.token = token;
        this.requestedUserId = requestedUserId;
        this.page = String.valueOf(page);
        this.perPage = String.valueOf(perPage);
    }


    @Override
    protected FollowInfoListResponse doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            FollowInfoListResponse followInfoListResponse = instance.client.usersUidFollowGet(userId, requestedUserId, token, perPage, requestType, page);

            if (followInfoListResponse.getError().getCode() == RESPONSE_OK) {
                return followInfoListResponse;
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
    protected void onPostExecute(FollowInfoListResponse followInfoListResponse) {
        super.onPostExecute(followInfoListResponse);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(followInfoListResponse);
            } else {
                mCallBack.onFailure(mException);
            }
        }

    }
}