package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.PostListResponse;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;

public class UserCaughtPostListProcess extends AsyncTask<Void, Void, PostListResponse> {

    private OnEventListener<PostListResponse> mCallBack;
    private Exception mException;
    private String userId;
    private String uid;
    private String token;
    private String longitude;
    private String perpage;
    private String latitude;
    private String radius;
    private String page;
    private String privacyType;

    public UserCaughtPostListProcess(Context context, OnEventListener callback, String userId,
                                     String uid,
                                     String longitude,
                                     String perpage,
                                     String latitude,
                                     String radius,
                                     String page,
                                     String privacyType,
                                     String token) {
        this.mCallBack = callback;
        Context mContext = context;
        this.userId = userId;
        this.uid = uid;
        this.longitude = longitude;
        this.perpage = perpage;
        this.latitude = latitude;
        this.radius = radius;
        this.page = page;
        this.privacyType = privacyType;
        this.token = token;
    }

    @Override
    protected PostListResponse doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            PostListResponse postListResponse = instance.client.usersUidCaughtGet(userId, uid, token, longitude, perpage, latitude, radius, page, privacyType);

            if (postListResponse.getError().getCode() == RESPONSE_OK) {
                return postListResponse;
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
    protected void onPostExecute(PostListResponse postListResponse) {
        super.onPostExecute(postListResponse);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(postListResponse);
            } else {
                mCallBack.onFailure(mException);
            }
        }

    }
}
