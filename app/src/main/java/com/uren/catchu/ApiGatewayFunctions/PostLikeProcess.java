package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.BaseResponse;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;

public class PostLikeProcess extends AsyncTask<Void, Void, BaseResponse> {

    private OnEventListener<BaseResponse> mCallBack;
    public Exception mException;
    private String userId;
    private String postId;
    private String commentId;
    private boolean isPostLiked;
    private String token;

    public PostLikeProcess(Context context, OnEventListener callback, String userId, String postId, String commentId, boolean isPostLiked, String token) {
        mCallBack = callback;
        Context mContext = context;
        this.userId = userId;
        this.postId = postId;
        this.commentId = commentId;
        this.isPostLiked = isPostLiked;
        this.token = token;
    }

    @Override
    protected BaseResponse doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            BaseResponse baseResponse;

            if (isPostLiked) {
                //call post like service
                baseResponse = instance.client.postsPostidCommentsCommentidLikePost(userId, postId, token, commentId);
            } else {
                //call post dislike service
                baseResponse = instance.client.postsPostidCommentsCommentidLikeDelete(userId, postId, token, commentId);
            }

            if (baseResponse.getError().getCode() == RESPONSE_OK) {
                return baseResponse;
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
    protected void onPostExecute(BaseResponse baseResponse) {
        super.onPostExecute(baseResponse);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(baseResponse);
            } else {
                mCallBack.onFailure(mException);
            }
        }

    }
}
