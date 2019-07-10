package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.CommentRequest;
import catchu.model.CommentResponse;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;

public class PostCommentProcess extends AsyncTask<Void, Void, CommentResponse> {

    private OnEventListener<CommentResponse> mCallBack;
    public Exception mException;
    private String userId;
    private String postId;
    private String commentId;
    private CommentRequest commentRequest;
    private String token;

    public PostCommentProcess(Context context, OnEventListener callback,
                              String userId, String postId, String commentId,
                              CommentRequest commentRequest, String token) {
        mCallBack = callback;
        Context mContext = context;
        this.userId = userId;
        this.postId = postId;
        this.commentId = commentId;
        this.commentRequest = commentRequest;
        this.token = token;
    }

    @Override
    protected CommentResponse doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {

            CommentResponse commentResponse = instance.client.postsPostidCommentsCommentidPost(userId, postId, token, commentId, commentRequest);

            if (commentResponse.getError().getCode() == RESPONSE_OK) {
                return commentResponse;
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
    protected void onPostExecute(CommentResponse commentResponse) {
        super.onPostExecute(commentResponse);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(commentResponse);
            } else {
                mCallBack.onFailure(mException);
            }
        }

    }
}
