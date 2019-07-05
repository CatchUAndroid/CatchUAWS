package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.CommentListResponse;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;

public class PostCommentListProcess extends AsyncTask<Void, Void, CommentListResponse> {

    private OnEventListener<CommentListResponse> mCallBack;
    private Context mContext;
    private Exception mException;
    private String userId;
    private String postId;
    private String token;
    private String commentId;

    public PostCommentListProcess(Context context, OnEventListener callback, String userId,
                                  String postId,
                                  String commentId,
                                  String token) {
        mCallBack = callback;
        mContext = context;
        this.userId = userId;
        this.postId = postId;
        this.commentId = commentId;
        this.token = token;
    }

    @Override
    protected CommentListResponse doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {

            CommentListResponse commentListResponse = instance.client.postsPostidCommentsCommentidGet(userId, postId, token, commentId);

            if (commentListResponse.getError().getCode() == RESPONSE_OK) {
                return commentListResponse;
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
    protected void onPostExecute(CommentListResponse commentListResponse) {
        super.onPostExecute(commentListResponse);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(commentListResponse);
            } else {
                mCallBack.onFailure(mException);
            }
        }

    }
}
