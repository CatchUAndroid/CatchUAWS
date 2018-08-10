package com.uren.catchu.ApiGatewayFunctions;

import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.CommonS3BucketResult;

public class SignedUrlGetProcess extends AsyncTask<Void, Void, CommonS3BucketResult> {

    private OnEventListener<CommonS3BucketResult> mCallBack;
    public Exception mException;
    public String extensionType;

    public SignedUrlGetProcess(OnEventListener callback,String extensionType) {
        this.extensionType = extensionType;
        this.mCallBack = callback;
    }

    @Override
    protected CommonS3BucketResult doInBackground(Void... voids) {
        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            CommonS3BucketResult commonS3BucketResult = instance.client.commonSignedurlGet(extensionType);
            return commonS3BucketResult;

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
    protected void onPostExecute(CommonS3BucketResult commonS3BucketResult) {
        super.onPostExecute(commonS3BucketResult);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(commonS3BucketResult);
            } else {
                mCallBack.onFailure(mException);
            }
        }
    }
}
