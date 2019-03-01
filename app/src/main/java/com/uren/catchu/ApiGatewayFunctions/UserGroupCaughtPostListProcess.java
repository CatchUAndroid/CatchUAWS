package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.PostListResponse;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;

public class UserGroupCaughtPostListProcess extends AsyncTask<Void, Void, PostListResponse> {

    private OnEventListener<PostListResponse> mCallBack;
    private Context mContext;
    private Exception mException;
    private String userId;
    private String groupId;
    private String token;
    private String longitude;
    private String perpage;
    private String latitude;
    private String radius;
    private String page;

    public UserGroupCaughtPostListProcess(Context context, OnEventListener callback, String userId,
                                          String groupId,
                                          String longitude,
                                          String perpage,
                                          String latitude,
                                          String radius,
                                          String page,
                                          String token) {
        this.mCallBack = callback;
        this.mContext = context;
        this.userId = userId;
        this.groupId = groupId;
        this.longitude = longitude;
        this.perpage = perpage;
        this.latitude = latitude;
        this.radius = radius;
        this.page = page;
        this.token = token;
    }

    @Override
    protected PostListResponse doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            PostListResponse postListResponse = instance.client.groupsGroupidCaughtGet(userId, token, groupId, longitude,
                                                                                    perpage, latitude, radius, page);

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
