package com.uren.catchu.ApiGatewayFunctions;

import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.BaseResponse;
import catchu.model.Endpoint;
import catchu.model.FollowInfoListResponse;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;

public class EndPointProcess extends AsyncTask<Void, Void, BaseResponse> {

    private OnEventListener<BaseResponse> mCallBack;
    public Exception mException;
    private String token;
    private Endpoint endpoint;

    public EndPointProcess(OnEventListener callback, String token, Endpoint endpoint) {
        mCallBack = callback;
        this.token = token;
        this.endpoint = endpoint;
    }

    @Override
    protected BaseResponse doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            BaseResponse baseResponse = instance.client.endpointPost(token, endpoint);

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