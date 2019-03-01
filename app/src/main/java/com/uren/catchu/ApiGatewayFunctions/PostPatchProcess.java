package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.BaseResponse;
import catchu.model.PostRequest;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;

public class PostPatchProcess extends AsyncTask<Void, Void, BaseResponse> {

    private OnEventListener<BaseResponse> mCallBack;
    private Context mContext;
    public Exception mException;
    private String userId;
    private String postId;
    private PostRequest postRequest;
    private String token;

    public PostPatchProcess(Context context, OnEventListener callback, String userId, String postId, PostRequest postRequest, String token) {
        mCallBack = callback;
        mContext = context;
        this.userId = userId;
        this.postId = postId;
        this.postRequest = postRequest;
        this.token = token;
    }

    @Override
    protected BaseResponse doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {

            BaseResponse baseResponse = instance.client.postsPostidPatch(userId, postId, token, postRequest);

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
