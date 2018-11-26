package com.uren.catchu.ApiGatewayFunctions;

import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import catchu.model.PostRequest;
import catchu.model.PostResponse;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;

public class ShareRequestProcess extends AsyncTask<Void, Void, PostResponse> {

    private OnEventListener<PostResponse> mCallBack;
    public Exception mException;
    public PostRequest postRequest;
    private String token;

    public ShareRequestProcess(OnEventListener callback, PostRequest postRequest, String token) {
        this.postRequest = postRequest;
        this.mCallBack = callback;
        this.token = token;
    }

    @Override
    protected PostResponse doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            PostResponse postResponse = (PostResponse) instance.client.postsPostidPost(" ", token, postRequest);

            if (postResponse.getError().getCode().intValue() == RESPONSE_OK)
                return postResponse;
            else
                return null;

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
    protected void onPostExecute(PostResponse postResponse) {
        super.onPostExecute(postResponse);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(postResponse);
            } else {
                mCallBack.onFailure(mException);
            }
        }
    }
}
