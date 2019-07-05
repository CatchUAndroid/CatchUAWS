package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.BaseRequest;
import catchu.model.BaseResponse;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;

/*import com.fasterxml.jackson.databind.ser.Serializers;*/

public class LoginProcess extends AsyncTask<Void, Void, BaseResponse> {

    private OnEventListener<BaseResponse> mCallBack;
    private Context mContext;
    public Exception mException;
    public BaseRequest baseRequest;
    private String token;
    private String userId;

    public LoginProcess(Context context, OnEventListener callback, String userId, BaseRequest baseRequest, String token) {
        mCallBack = callback;
        mContext = context;
        this.baseRequest = baseRequest;
        this.token = token;
        this.userId = userId;
    }

    @Override
    protected BaseResponse doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            BaseResponse rsp = instance.client.loginPost(userId, token, baseRequest);

            if (rsp.getError().getCode().intValue() == RESPONSE_OK)
                return rsp;
            else
                return null;

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
