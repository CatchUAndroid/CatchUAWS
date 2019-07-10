package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.UserListResponse;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;

public class PostLikeListProcess extends AsyncTask<Void, Void, UserListResponse> {

    private OnEventListener<UserListResponse> mCallBack;
    private Exception mException;
    private String userId;
    private String postId;
    private String perPage;
    private String page;
    private String token;
    private String commentId;

    public PostLikeListProcess(Context context, OnEventListener callback, String userId,
                               String postId,
                               String perPage,
                               String page,
                               String commentId,
                               String token) {
        mCallBack = callback;
        Context mContext = context;
        this.userId = userId;
        this.postId = postId;
        this.perPage = perPage;
        this.page = page;
        this.commentId = commentId;
        this.token = token;
    }

    @Override
    protected UserListResponse doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            UserListResponse userListResponse = instance.client.postsPostidCommentsCommentidLikeGet(userId, postId, perPage, page, token, commentId);

            if (userListResponse.getError().getCode() == RESPONSE_OK) {
                return userListResponse;
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
    protected void onPostExecute(UserListResponse userListResponse) {
        super.onPostExecute(userListResponse);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(userListResponse);
            } else {
                mCallBack.onFailure(mException);
            }
        }

    }
}
